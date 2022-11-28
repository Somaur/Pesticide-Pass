package com.example.pesticide_pass;

import static com.example.pesticide_pass.tools.FileTools.WriteBitmapToUri;
import static com.example.pesticide_pass.tools.FileTools.getNewTempUri;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.pesticide_pass.data.FittedModel;
import com.example.pesticide_pass.data.ModelsRepository;
import com.example.pesticide_pass.data.TaggedImage;
import com.example.pesticide_pass.tools.GetPicLifecycleObserver;
import com.example.pesticide_pass.tools.GetSampleLifecycleObserver;
import com.huangxy.actionsheet.ActionSheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PredictResultActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    XYPlot plot;
    TextView tv1;
    TextView tv2;
    Button btn1;
    Button btn2;
    Button btn3;
    private ActionSheet actionSheet;

    int idx;
    FittedModel model;
    double x, y;
    double limit;

    boolean hasLimit;
    boolean dangerous;

    private GetPicLifecycleObserver    getPicLifecycleObserver;
    private GetSampleLifecycleObserver getSampleLifecycleObserver;

    // alert: background="#a0ff0000"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict_result);

        getPicLifecycleObserver = new GetPicLifecycleObserver(this, getActivityResultRegistry());
        getLifecycle().addObserver(getPicLifecycleObserver);
        getSampleLifecycleObserver = new GetSampleLifecycleObserver(this, getActivityResultRegistry());
        getSampleLifecycleObserver.setReceive((tag, uri1) -> {
            double gray = new TaggedImage(uri1, tag, PredictResultActivity.this).getGrayscale();
            Intent intent = new Intent(this, PredictResultActivity.class);
            intent.putExtra("gray", gray);
            intent.putExtra("i", idx);
            if (hasLimit) intent.putExtra("limit", limit);
            startActivity(intent);
            finish();
        });
        getLifecycle().addObserver(getSampleLifecycleObserver);

        getExtra();

        linearLayout = findViewById(R.id.ll);
        plot = findViewById(R.id.plot);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);

        plot.setTitle(model.getName());
        draw();
        tv1.setText(String.format(Locale.CHINA, "预测浓度：%.3f", x));
        if (!hasLimit) {
            tv2.setText("未设置阈值");
        }
        else {
            if (dangerous) {
                tv2.setText("预测值超过阈值！");
                linearLayout.setBackgroundColor(0xa0ff0000);
            }
            else {
                tv2.setText("预测值低于阈值");
            }
        }

        actionSheet = new ActionSheet.DialogBuilder(this)
                .addSheet("拍照", view -> {
                    getPicLifecycleObserver.setTake_photo(uri -> {
                        getSampleLifecycleObserver.setImgUri(uri);
                        getSampleLifecycleObserver.launch();
                    });
                    actionSheet.hide();
                })
                .addSheet("从相册获取图片", view -> {
                    getPicLifecycleObserver.setChose_photo((GetPicLifecycleObserver.ReceivePicUri) uri -> {
                        getSampleLifecycleObserver.setImgUri(uri);
                        getSampleLifecycleObserver.launch();
                    });
                    actionSheet.hide();
                })
                .addCancelListener(v -> actionSheet.hide())
                .create();

        btn1.setOnClickListener(view -> actionSheet.show());
        btn2.setOnClickListener(view -> {
            // TODO: 生成分享图片
            linearLayout.setDrawingCacheEnabled(true);
            linearLayout.buildDrawingCache();
            Bitmap bmp = Bitmap.createBitmap(linearLayout.getDrawingCache());
            linearLayout.destroyDrawingCache();
            Uri uri = getNewTempUri(this, "share.jpg");
            WriteBitmapToUri(this, bmp, uri);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            shareIntent = Intent.createChooser(shareIntent, "Share");
            startActivity(shareIntent);
        });
        btn3.setOnClickListener(view -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        actionSheet.dismiss();
    }

    private void getExtra() {
        Intent intent = getIntent();
        idx = intent.getIntExtra("i", 0);
        model = ModelsRepository.getInstance(this).fittedModels.get(idx);
        y = intent.getDoubleExtra("gray", 0);
        hasLimit = intent.hasExtra("limit");
        if (hasLimit) limit = intent.getDoubleExtra("limit", 0);
    }

    // 把阈值放在图的右 1/3 处，然后点和阈值的差距为 dx， 总x宽为 dx * 6 或 dx * 2.5
    // Y = max(abs(f(minx) - f(maxx)), 0.5)
    // if left ^ k > 0, (y + .25*Y, y - .75*Y)
    // else (y + .75*Y, y - .25*Y)
    private void draw() {

        x = g(y);
        double X, Y;
        if (!hasLimit) limit = x + 5;
        double dx = x - limit;
        dangerous = dx > 0;
        if (dangerous) X = dx * 6;
        else X = dx * -2.5;

        double x2 = limit + X / 3;
        double x1 = x2 - X;
        Y = Math.max(Math.abs(f(x1) - f(x2)), 0.5);
        double y1, y2;
        if (dangerous == (model.getK() < 0)) {
            y1 = y - .25 * Y;
            y2 = y + .75 * Y;
        }
        else {
            y1 = y - .75 * Y;
            y2 = y + .25 * Y;
        }
        double xStep = (x2 - x1) / 10;
        double yStep = (y2 - y1) / 10;
        plot.setDomainBoundaries(x1 - xStep, x2 + xStep, BoundaryMode.FIXED);
        plot.setRangeBoundaries(y1 - yStep, y2 + yStep, BoundaryMode.FIXED);

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter seriesPointFormat =
                new LineAndPointFormatter(this, R.xml.point_formatter);
        XYSeries seriesPoint = new SimpleXYSeries(Collections.singletonList(x), Collections.singletonList(y), "预测点");
        plot.addSeries(seriesPoint, seriesPointFormat);

        plot.setLinesPerRangeLabel(3);

        LineAndPointFormatter series1Format =
                new LineAndPointFormatter(this, R.xml.line_point_formatter);
        LineAndPointFormatter series2Format =
                new LineAndPointFormatter(this, R.xml.line_predict_limit_fomatter);

        plot.addSeries(generateSeries1(x1 + xStep / 2, x2 - xStep / 2, 1), series1Format);
        plot.addSeries(generateSeries2(y1 - yStep / 2, y2 + yStep / 2, limit), series2Format);
    }

    protected XYSeries generateSeries1(double minX, double maxX, double resolution) {
        final double range = maxX - minX;
        final double step = range / resolution;
        List<Number> xVals = new ArrayList<>();
        List<Number> yVals = new ArrayList<>();

        double x = minX;
        while (x < maxX) {
            xVals.add(x);
            yVals.add(f(x));
            x += step;
        }
        xVals.add(maxX);
        yVals.add(f(maxX));

        return new SimpleXYSeries(xVals, yVals, "函数");
    }

    protected XYSeries generateSeries2(double minY, double maxY, double X) {
        List<Number> xVals = new ArrayList<>();
        List<Number> yVals = new ArrayList<>();
        xVals.add(X);
        yVals.add(minY);
        xVals.add(X);
        yVals.add(maxY);

        return new SimpleXYSeries(xVals, yVals, "阈值");
    }

    private double f(double x) {
        return x * model.getK() + model.getB();
    }
    private double g(double y) {
        if (model.getK() == 0) return limit - 1;
        return (y - model.getB()) / model.getK();
    }
}
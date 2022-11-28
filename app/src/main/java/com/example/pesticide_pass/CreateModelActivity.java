package com.example.pesticide_pass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.pesticide_pass.data.FittedModel;
import com.example.pesticide_pass.data.ModelsRepository;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateModelActivity extends AppCompatActivity {

    String name;
    ArrayList<Double> xPos;
    ArrayList<Double> yPos;

    XYPlot plot;
    Button btn1;

    final WeightedObservedPoints obs    = new WeightedObservedPoints();
    final PolynomialCurveFitter  fitter = PolynomialCurveFitter.create(1);
    double[] coeff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_model);

        // initialize our XYPlot reference:
        plot = findViewById(R.id.plot);
        btn1 = findViewById(R.id.btn1);

        getExtra();
        plot.setTitle(name);
        fitterModel();
        draw();

        ModelsRepository.getInstance(this).fittedModels.add(new FittedModel(name, coeff[1], coeff[0]));
        ModelsRepository.saveToLocale(this);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySetResult();
                finish();
            }
        });
    }

    private void getExtra() {
        Intent intent = getIntent();
        xPos = (ArrayList<Double>) intent.getSerializableExtra("xPos");
        yPos = (ArrayList<Double>) intent.getSerializableExtra("yPos");
        name = intent.getStringExtra("name");
    }

    private void draw() {
        XYSeries seriesPoint = new SimpleXYSeries(xPos , yPos, "Data");

        // 设置绘图范围，不设置的话自动生成大小会把点弄到边界上
        // Domain: x
        // Range: y
        double x1 = Collections.min(xPos);
        double x2 = Collections.max(xPos);
        double y1 = Collections.min(yPos);
        double y2 = Collections.max(yPos);
        double xStep = (x2 - x1) / 10;
        double yStep = (y2 - y1) / 10;
        plot.setDomainBoundaries(x1 - xStep, x2 + xStep, BoundaryMode.FIXED);
        plot.setRangeBoundaries(y1 - yStep, y2 + yStep, BoundaryMode.FIXED);

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter seriesPointFormat =
                new LineAndPointFormatter(this, R.xml.point_formatter);

        // add each series to the xy-plot:
        plot.addSeries(seriesPoint, seriesPointFormat);

        // reduce the number of range labels
        plot.setLinesPerRangeLabel(3);

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series3Format =
                new LineAndPointFormatter(this, R.xml.line_point_formatter);

        // add a new series' to the xy-plot:
        plot.addSeries(generateSeries(x1 - xStep / 2, x2 + xStep / 2, 1), series3Format);
    }

    private void fitterModel() {
        for (int i = 0; i < xPos.size(); ++i) {
            obs.add(xPos.get(i), yPos.get(i));
        }
        coeff = fitter.fit(obs.toList());
    }

    protected XYSeries generateSeries(double minX, double maxX, double resolution) {
        final double range = maxX - minX;
        final double step = range / resolution;
        List<Number> xVals = new ArrayList<>();
        List<Number> yVals = new ArrayList<>();

        double x = minX;
        while (x < maxX) {
            xVals.add(x);
            yVals.add(fx(x));
            x += step;
        }
        xVals.add(maxX);
        yVals.add(fx(maxX));

        return new SimpleXYSeries(xVals, yVals, "Model");
    }

    protected double fx(double x) {
        return x * coeff[1] + coeff[0];
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            mySetResult();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void mySetResult() {
        // nothing
    }
}
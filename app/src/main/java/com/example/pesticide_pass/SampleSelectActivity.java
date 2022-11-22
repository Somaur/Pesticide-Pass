package com.example.pesticide_pass;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pesticide_pass.data.ImageTag;
import com.example.pesticide_pass.data.TaggedImage;

import java.util.Locale;


public class SampleSelectActivity extends AppCompatActivity {

    ImageView iv;
    TextView tv;
    Button btn;

    TaggedImage taggedImage;

    double height, width;

    @SuppressLint("ClickableViewAccessibility")  // 用来消除setOnTouchListener警告的
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_select);

        Intent intent = getIntent();
        Uri image_uri = (Uri) intent.getParcelableExtra("image_uri");
        taggedImage = new TaggedImage(image_uri, null, SampleSelectActivity.this);

        tv = findViewById(R.id.tv);
        btn = findViewById(R.id.btn1);
        iv = findViewById(R.id.iv1);

        iv.setImageURI(image_uri);
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                height = iv.getHeight();
                width = iv.getWidth();
                int x = (int)(motionEvent.getX() * 600. / width);
                int y = (int)(motionEvent.getY() * 600. / height);
                Log.e("OnTouch", "" + x + " " + y);
                taggedImage.setTag(new ImageTag.Dot(x, y), SampleSelectActivity.this);
                tv.setText(String.format(Locale.CHINA,
                        "采样坐标: [%d, %d]\n 灰度: [%.2f]", x, y, taggedImage.getGrayscale()
                ));
                return false;
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("tag", taggedImage.getTag());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
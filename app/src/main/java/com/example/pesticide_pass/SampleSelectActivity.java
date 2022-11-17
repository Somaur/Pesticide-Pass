package com.example.pesticide_pass;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.badge.BadgeUtils;

import java.util.Locale;


public class SampleSelectActivity extends AppCompatActivity {

    ImageView iv;
    TextView tv;
    Button btn;

    float retX, retY;

    @SuppressLint("ClickableViewAccessibility")  // 用来消除setOnTouchListener警告的
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_select);

        Intent intent = getIntent();
        Uri image_uri = (Uri) intent.getParcelableExtra("image_uri");

        iv = findViewById(R.id.iv);
        tv = findViewById(R.id.tv);
        btn = findViewById(R.id.btn);

        iv.setImageURI(image_uri);
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                retX = motionEvent.getX();
                retY = motionEvent.getY();
                tv.setText(String.format(Locale.CHINA, "采样坐标: [%.2f, %.2f]", retX, retY));
                return false;
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("x", (int)retX);
                intent.putExtra("y", (int)retY);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
package com.example.pesticide_pass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.pesticide_pass.running_state.RunningState;
import com.example.pesticide_pass.ui.login.LoginActivity;

public class TempStartActivity extends AppCompatActivity {
    
    // ......

    Button button1; // 添加
    Button button2; // 预测
    Button button3; // 管理
    Button button4; // 登录

    Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_start);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        switch1 = findViewById(R.id.switch1);
        setButtonsListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch1.setChecked(RunningState.logged_in);
    }

    private void setButtonsListener(){
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TempStartActivity.this, AddModelActivity.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TempStartActivity.this, ModelPredictionActivity.class);
                startActivity(intent);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TempStartActivity.this, ModelManageActivity.class);
                startActivity(intent);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TempStartActivity.this, AccountSettingActivity.class);
                startActivity(intent);
            }
        });
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                RunningState.logged_in = b;
            }
        });
    }
}
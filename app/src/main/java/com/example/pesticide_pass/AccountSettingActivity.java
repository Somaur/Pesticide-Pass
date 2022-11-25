package com.example.pesticide_pass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.pesticide_pass.running_state.RunningState;
import com.example.pesticide_pass.tools.debug.ToBeContinued;
import com.example.pesticide_pass.ui.login.LoginActivity;

public class AccountSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        if (!RunningState.logged_in) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            if (!RunningState.logged_in) finish();
        }

        findViewById(R.id.button5).setOnClickListener(ToBeContinued.clickListener);
        findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RunningState.logged_in = false;
                Toast.makeText(AccountSettingActivity.this, "账号已登出", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
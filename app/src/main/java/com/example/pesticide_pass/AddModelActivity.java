package com.example.pesticide_pass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.pesticide_pass.tools.debug.ToBeContinued;

public class AddModelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_model);

        findViewById(R.id.button).setOnClickListener(ToBeContinued.clickListener);
        findViewById(R.id.button6).setOnClickListener(ToBeContinued.clickListener);

        Intent intent = new Intent();
        intent.putExtra("model_id", "AAA");
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
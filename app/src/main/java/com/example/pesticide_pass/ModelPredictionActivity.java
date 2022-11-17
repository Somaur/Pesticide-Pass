package com.example.pesticide_pass;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pesticide_pass.tools.debug.ToBeContinued;

import java.util.ArrayList;
import java.util.List;

public class ModelPredictionActivity extends AppCompatActivity {

    private List<String> model_names = new ArrayList<>();
    private Spinner sp;
    private Button  btn;

    private String                         new_model_id;
    private ActivityResultLauncher<Intent> resultModelLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.e("RESULT", "Into 'onActivityResult'");
                if (result.getResultCode() == RESULT_OK) {
                    new_model_id = result.getData().getStringExtra("model_id");
                    model_names.add("新增模型");
                    sp.setSelection(model_names.size() - 1);
                    Toast.makeText(ModelPredictionActivity.this, "自动选择新增的模型", Toast.LENGTH_SHORT).show();
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_prediction);

        findViewById(R.id.button8).setOnClickListener(ToBeContinued.clickListener);

        sp = findViewById(R.id.sp_model);
        btn = findViewById(R.id.btn);

        for (int i = 1; i <= 10; ++i) {
            model_names.add("示例模型 " + i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, model_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        btn.setOnClickListener(new View.OnClickListener() {
            // 添加新模型将跳转到“添加页面”，并返回新增的模型
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ModelPredictionActivity.this, AddModelActivity.class);
                resultModelLauncher.launch(intent);
            }
        });
    }

    // TODO: 编写“进行预测”之后的逻辑
}
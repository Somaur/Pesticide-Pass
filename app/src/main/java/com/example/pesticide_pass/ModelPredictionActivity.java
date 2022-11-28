package com.example.pesticide_pass;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pesticide_pass.data.FittedModel;
import com.example.pesticide_pass.data.ModelsRepository;

import java.util.ArrayList;
import java.util.List;

public class ModelPredictionActivity extends AppCompatActivity {

    private List<String> model_names;
    private List<FittedModel> models;

    private Spinner sp;
    private Button  btn1;
    private Button  btn2;
    private EditText et1;

    private       String                         new_model_name;
    private final ActivityResultLauncher<Intent> resultModelLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                models = ModelsRepository.getInstance(this).fittedModels;
                model_names.clear();

                for (int i = 0; i < models.size(); ++i) {
                    model_names.add(models.get(i).getName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, model_names);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp.setAdapter(adapter);

                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    new_model_name = result.getData().getStringExtra("model_name");

                    sp.setSelection(model_names.size() - 1);
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_prediction);

        sp = findViewById(R.id.sp_models);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        et1 = findViewById(R.id.et1);
        model_names = new ArrayList<>();
        models = ModelsRepository.getInstance(this).fittedModels;
        Log.e("JSON", "models.size(): " + models.size());

        for (int i = 0; i < models.size(); ++i) {
            model_names.add(models.get(i).getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, model_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        btn1.setOnClickListener(new View.OnClickListener() {
            // 添加新模型将跳转到“添加页面”，并返回新增的模型
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ModelPredictionActivity.this, AddModelActivity.class);
                resultModelLauncher.launch(intent);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et1.getText().length() == 0) return;
                int i = sp.getSelectedItemPosition();
                FittedModel model = models.get(i);
                double x = Double.parseDouble(et1.getText().toString());
                double y = x * model.getK() + model.getB();
                et1.setText(Double.toString(y));
            }
        });
    }

    // TODO: 编写“进行预测”之后的逻辑
}
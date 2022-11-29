package com.example.pesticide_pass;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pesticide_pass.data.FittedModel;
import com.example.pesticide_pass.data.ImageTag;
import com.example.pesticide_pass.data.ModelsRepository;
import com.example.pesticide_pass.data.TaggedImage;
import com.example.pesticide_pass.tools.GetPicLifecycleObserver;
import com.example.pesticide_pass.tools.GetSampleLifecycleObserver;
import com.huangxy.actionsheet.ActionSheet;

import java.util.ArrayList;
import java.util.List;

public class ModelPredictionActivity extends AppCompatActivity {

    private List<String> model_names;
    private List<FittedModel> models;

    private Spinner sp;
    private Button  btn1;
    private Button  btn2;
    private EditText    et1;
    private ActionSheet actionSheet;

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

    private GetPicLifecycleObserver    getPicLifecycleObserver;
    private GetSampleLifecycleObserver getSampleLifecycleObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_prediction);

        getPicLifecycleObserver = new GetPicLifecycleObserver(this, getActivityResultRegistry());
        getLifecycle().addObserver(getPicLifecycleObserver);
        getSampleLifecycleObserver = new GetSampleLifecycleObserver(this, getActivityResultRegistry());
        getSampleLifecycleObserver.setReceive((tag, uri1) -> {
            double gray = new TaggedImage(uri1, tag, ModelPredictionActivity.this).getGrayscale();
            int i = sp.getSelectedItemPosition();
            Intent intent = new Intent(this, PredictResultActivity.class);
            intent.putExtra("gray", gray);
            intent.putExtra("i", i);
            try {
                double limit = Double.parseDouble(et1.getText().toString());
                intent.putExtra("limit", limit);
            } catch (Exception ignored) {}
            startActivity(intent);
            finish();
        });
        getLifecycle().addObserver(getSampleLifecycleObserver);

        sp = findViewById(R.id.sp_models);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        et1 = findViewById(R.id.et1);
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

        model_names = new ArrayList<>();
        models = ModelsRepository.getInstance(this).fittedModels;
        for (int i = 0; i < models.size(); ++i) {
            model_names.add(models.get(i).getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, model_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);


        btn1.setOnClickListener(view -> {
            // 添加新模型将跳转到“添加页面”，并返回新增的模型
            Intent intent = new Intent(ModelPredictionActivity.this, AddModelActivity.class);
            resultModelLauncher.launch(intent);
        });

        btn2.setOnClickListener(view -> {
            if (models.size() == 0) {
                Toast.makeText(this, "没有模型，请新建模型", Toast.LENGTH_SHORT).show();
            }
            actionSheet.show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        actionSheet.dismiss();
    }
}
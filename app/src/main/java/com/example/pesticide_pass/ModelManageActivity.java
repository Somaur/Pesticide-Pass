package com.example.pesticide_pass;

import static com.example.pesticide_pass.data.FittedModel.parseJSONArray;
import static com.example.pesticide_pass.data.FittedModel.parseJSONArrayString;

import android.content.DialogInterface;
import android.os.Bundle;

import com.example.pesticide_pass.data.FittedModel;
import com.example.pesticide_pass.data.ModelsRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pesticide_pass.ui.manage.SectionsPagerAdapter;
import com.example.pesticide_pass.databinding.ActivityModelManageBinding;
import com.huangxy.actionsheet.ActionSheet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class ModelManageActivity extends AppCompatActivity {

    private ActivityModelManageBinding binding;

    private ActionSheet actionSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityModelManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        actionSheet = new ActionSheet.DialogBuilder(this)
                .addSheet("输入参数添加模型", v -> {
                    showInputArgDialog();
                    actionSheet.hide();
                })
                .addSheet("从分享文本添加模型", v -> {
                    showInputJSONDialog();
                    actionSheet.hide();
                })
                .addCancelListener(v -> actionSheet.hide())
                .create();

        fab.setOnClickListener(view -> actionSheet.show());
    }

    private void showInputJSONDialog() {
        final EditText editText = new EditText(this);
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);
        inputDialog.setTitle("粘贴生成的分享文本").setView(editText);
        inputDialog.setPositiveButton("确定", (dialog, which) -> {
            String msg = editText.getText().toString();
            List<FittedModel> models = parseJSONArrayString(msg);
            ModelsRepository.getInstance(this).fittedModels.addAll(models);
            ModelsRepository.saveToLocale(this);
            //..
        }).show();
    }

    private void showInputArgDialog() {

        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(ModelManageActivity.this);
        final View dialogView = LayoutInflater.from(ModelManageActivity.this)
                .inflate(R.layout.input_model_dialog,null);
        customizeDialog.setTitle("请输入模型信息");
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定", (dialog, which) -> {
            String name = ((EditText)dialogView.findViewById(R.id.et1)).getText().toString();
            String ks = ((EditText)dialogView.findViewById(R.id.et2)).getText().toString();
            String bs = ((EditText)dialogView.findViewById(R.id.et3)).getText().toString();
            if (name.length() == 0 || ks.length() == 0 || bs.length() == 0) {
                Toast.makeText(this, "请输入全部信息！", Toast.LENGTH_SHORT).show();
                return;
            }
            FittedModel model = new FittedModel(name, Double.parseDouble(ks), Double.parseDouble(bs));
            ModelsRepository.getInstance(this).fittedModels.add(model);
            ModelsRepository.saveToLocale(this);
        });
        customizeDialog.setNegativeButton("取消", (dialogInterface, i) -> {
            // ...
        });
        customizeDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        actionSheet.dismiss();
    }
}
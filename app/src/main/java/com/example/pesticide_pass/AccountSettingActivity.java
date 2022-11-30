package com.example.pesticide_pass;

import static com.example.pesticide_pass.data.FittedModel.listToJSON;
import static com.example.pesticide_pass.data.FittedModel.parseJSONArrayString;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pesticide_pass.data.FittedModel;
import com.example.pesticide_pass.data.ModelsRepository;
import com.example.pesticide_pass.running_state.RunningState;
import com.example.pesticide_pass.tools.Remote;
import com.example.pesticide_pass.tools.debug.ToBeContinued;
import com.example.pesticide_pass.ui.login.DBLogin;
import com.example.pesticide_pass.ui.login.LoginActivity;

import java.util.List;

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

        // 修改密码
        findViewById(R.id.button5).setOnClickListener(view -> {
            final EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);
            inputDialog.setTitle("输入新密码").setView(editText);
            inputDialog.setPositiveButton("确定", (dialog, which) -> {
                String password = editText.getText().toString();
                if (password.length() < 6)
                    Toast.makeText(this, "密码需要至少6位", Toast.LENGTH_SHORT).show();
                else{
                    if(Remote.changePassword(password)){
                        Toast.makeText(this, "密码修改成功！", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(this, "密码修改失败！", Toast.LENGTH_SHORT).show();
                    }
                }
                //..
            }).show();
        });
        findViewById(R.id.button7).setOnClickListener(view -> {
            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(this);
            normalDialog.setTitle("登出账号");
            normalDialog.setMessage("确定要登出账号吗？");
            normalDialog.setPositiveButton("确定", (dialog, which) -> {
                RunningState.logged_in = false;
                Toast.makeText(AccountSettingActivity.this, "账号已登出", Toast.LENGTH_SHORT).show();
                finish();
            });
            normalDialog.setNegativeButton("取消", (dialog, which) -> {
                //... Nothing
            });
            // 显示
            normalDialog.show();
        });
    }
}
package com.example.pesticide_pass.data;

import android.util.Log;

import com.example.pesticide_pass.data.model.LoggedInUser;
import com.example.pesticide_pass.tools.Remote;
import com.example.pesticide_pass.ui.login.DBLogin;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    int code;
    public Result<LoggedInUser> login(String username, String password) {

        try {
            // 这里是处理登录和判断成功的位置
            Log.e("LOGIN", String.format("name: %s, pw: %s", username, password));
            Log.e("LOGIN", "CheckResult: " + Remote.checkLogin(username, password));
            if (!Remote.checkLogin(username, password)) throw new Exception();
            return new Result.Success<>(new LoggedInUser(
                    java.util.UUID.randomUUID().toString(),
                    "NickName"));

//            //创建一个线程
//            //username=123456 password=123456 nickname=LingTong
//            //真机测试需要关闭自己电脑的防火墙，Android Studio测试不需要关闭防火墙
//            Thread t1 = new Thread(new Runnable() {
//                public void run() {
//
//                    //DBLogin.context=LoginActivity.this;
//                    if(DBLogin.linkMysql()){
//                        code=DBLogin.linkLoginsql(username, password);
//                    }
//                    else{
//                        code=-1;
//                    }
//                }
//            });
//            t1.start();
//            t1.join();
//            if (code != 1) throw new Exception();
//            LoggedInUser user = new LoggedInUser(
//                                        java.util.UUID.randomUUID().toString(),
//                                        DBLogin.getnickname());
//            return new Result.Success<>(user);
        } catch (Exception e) {
            // 登录失败走这里
            e.printStackTrace();
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // 这里是处理登出的位置（通常情况下）
    }
}
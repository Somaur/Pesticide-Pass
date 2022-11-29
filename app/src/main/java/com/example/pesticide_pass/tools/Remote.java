package com.example.pesticide_pass.tools;

import com.example.pesticide_pass.data.FittedModel;
import com.example.pesticide_pass.tools.debug.FakeRemote;

import java.util.ArrayList;
import java.util.List;

public class Remote {
    // 这里本该接入凌童的Web模块，但服务器什么的没搞定
    // 现在先用假的远端替代
    public static ArrayList<FittedModel> getModels() {
        return FakeRemote.getModels();
    }

    public static void upLoad(List<FittedModel> models) {
        FakeRemote.upLoad(models);
    }

    public static boolean checkLogin(String username, String password) {
        return FakeRemote.checkLogin(username, password);
    }

    public static void changePassword(String password) {
        FakeRemote.changePassword(password);
    }
}

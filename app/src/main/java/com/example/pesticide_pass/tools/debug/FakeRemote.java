package com.example.pesticide_pass.tools.debug;

import android.content.Context;

import com.example.pesticide_pass.data.FittedModel;
import com.example.pesticide_pass.data.ModelsRepository;

import java.util.ArrayList;
import java.util.List;

public class FakeRemote {

    private static ArrayList<FittedModel> fittedModels;
    private static String _username = "user";
    private static String _password = "123456";

    public static ArrayList<FittedModel> getModels() {
        if(fittedModels == null){
            fittedModels = new ArrayList<>();
            fittedModels.add(new FittedModel("远程模型1", -1, 90));
            fittedModels.add(new FittedModel("远程模型2", -2, 80));
            fittedModels.add(new FittedModel("远程模型3", -3, 70));
            fittedModels.add(new FittedModel("远程模型4", -4, 60));
            fittedModels.add(new FittedModel("远程模型5", -5, 50));
        }
        return fittedModels;
    }

    public static void upLoad(List<FittedModel> models) {
        fittedModels.addAll(models);
    }

    public static boolean checkLogin(String username, String password) {
        return username.equals(_username) && password.equals(_password);
    }

    public static void changePassword(String password) {
        _password = password;
    }
}

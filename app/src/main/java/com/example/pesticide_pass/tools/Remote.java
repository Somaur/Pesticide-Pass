package com.example.pesticide_pass.tools;

import com.example.pesticide_pass.data.FittedModel;
import com.example.pesticide_pass.tools.debug.FakeRemote;
import com.example.pesticide_pass.ui.login.DBLogin;
import com.example.pesticide_pass.ui.login.ModuleBean;

import java.util.ArrayList;
import java.util.List;

public class Remote {
    // 这里本该接入凌童的Web模块，但服务器什么的没搞定
    // 现在先用假的远端替代
    private static int code=-1;
    private static boolean res=false;
    public static ArrayList<FittedModel> getModels() {
        ArrayList<FittedModel> mlist=new ArrayList<FittedModel>();
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                ArrayList<ModuleBean> list=new ArrayList<ModuleBean>();
                if(DBLogin.linkMysql()) {
                    list=DBLogin.getAllModules();
                    for (ModuleBean x:list) {
                        mlist.add(new FittedModel(x.getName(),x.getK(),x.getK(),x.getCreate_time()));
                    }
                }
            }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mlist;
    }

    public static void upLoad(List<FittedModel> models) {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                if(DBLogin.linkMysql()) {
                    for (FittedModel x:models) {
                        DBLogin.saveModule(x.getName(),x.getK(),x.getB(),x.getCreate_time());
                    }
                }
            }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkLogin(String username, String password) {
            //创建一个线程
            //username=123456 password=123456 nickname=LingTong
            //真机测试需要关闭自己电脑的防火墙，Android Studio测试不需要关闭防火墙
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                if(DBLogin.linkMysql()){
                    code=DBLogin.linkLoginsql(username, password);
                }
                else{
                    code=-1;
                }
            }
        });
        t1.start();
        try {
            t1.join();
            if(code==0) return true;
            else return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return FakeRemote.checkLogin(username, password);
    }

    public static boolean changePassword(String password) {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                if(DBLogin.linkMysql())
                res=DBLogin.modifyPassword(password);
            }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
        //FakeRemote.changePassword(password);
    }
}


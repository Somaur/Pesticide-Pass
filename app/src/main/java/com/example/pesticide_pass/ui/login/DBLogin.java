package com.example.pesticide_pass.ui.login;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBLogin {
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String url = "jdbc:mysql://49.140.58.112:3306/pesticide";//lt的主机ip
    private static final String user = "root";
    private static final String pwd = "root";
    private static String nickname;
    private static Connection conn=null;
    public static int status=-1;
    //public static Context context;
    static boolean linkMysql(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.e("DB","驱动加载成功！");
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        try {
            conn=DriverManager.getConnection(url, user, pwd);
            //Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            Log.e("DB","数据库连接成功！");
            return true;
        }
        catch (Exception e){
            Log.e("DB","数据库连接失败！");
            //Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }


    static int linkLoginsql(String username, String password) {
        int code=-1;
        try {

            String logSql = "Select * from users where username='"+ username+ "'and password='"+ password+ "'";
            PreparedStatement stmt = conn.prepareStatement(logSql);
            ResultSet rs = stmt.executeQuery(logSql);
            if(rs!=null){
                if(rs.next()){
                    code=0;
                    status=0;
                    nickname=rs.getString("nickname");
                }else{
                    code=1;
                    status=1;
                }
            }
            else{
                code=1;
                status=1;
            }
            // 获取跳转判断
            rs.close();
            stmt.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }

        //关闭数据库
        if(conn!=null){
            try {

                conn.close();
                Log.e("DB","数据库退出！");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return code;
    }

    public static String getnickname(){
        return nickname;
    }
}

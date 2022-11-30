package com.example.pesticide_pass.ui.login;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBLogin {
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String url = "jdbc:mysql://49.140.58.112:3306/pesticide";//lt的主机ip
    private static final String user = "root";
    private static final String pwd = "root";
    private static String nickname=null;
    private static String username=null;
    private static int id;
    private static Connection conn=null;
    public static int status=-1;
    //public static Context context;
    public static boolean linkMysql(){
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

    public static int linkLoginsql(String username_, String password) {
        Log.e("DB","logining");
        int code=-1;
        try {
            String logSql = "Select * from users where username='"+ username_+ "'and password='"+ password+ "'";
            PreparedStatement stmt = conn.prepareStatement(logSql);
            ResultSet rs = stmt.executeQuery(logSql);
            if(rs!=null){
                if(rs.next()){
                    code=0;
                    status=0;
                    nickname=rs.getString("nickname");
                    username=rs.getString("username");
                    id=rs.getInt("id");
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
    public static boolean register(String username,String password,String nickname){
        Log.e("DB","Registering");
        String sql = "INSERT INTO users(username,password) "
                + " VALUES (?,?,?)";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);			// 设置第一个“?”的内容
            stmt.setString(2,password);		// 设置第二个“?”的内容
            stmt.setString(3,nickname);			// 设置第三个“?”的内容
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if(conn!=null){
            try {
                conn.close();
                Log.e("DB","数据库退出！");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }
    public static boolean modifyPassword(String password_new){
        Log.e("DB","Modifying password");
        Log.e("DB",username);
        String sql = "UPDATE users SET password=? WHERE id=?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, password_new);			// 设置第一个“?”的内容
            stmt.setInt(2,id);		// 设置第二个“?”的内容
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if(conn!=null){
            try {
                conn.close();
                Log.e("DB","数据库退出！");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }
    public static ArrayList<ModuleBean> getAllModules(){
        Log.e("DB","getting");
        String logSql = "Select * from modules";
        PreparedStatement stmt = null;
        ArrayList<ModuleBean> modules=new ArrayList<ModuleBean>();
        try {
            stmt = conn.prepareStatement(logSql);
            ResultSet rs = stmt.executeQuery(logSql);
            if(rs!=null){
                while(rs.next()){
                    ModuleBean temp=new ModuleBean();
                    temp.setName(rs.getString("name"));
                    temp.setB(Double.parseDouble(rs.getString("b")));
                    temp.setK(Double.parseDouble(rs.getString("k")));
                    temp.setCreate_time(rs.getString("create_time"));
                    modules.add(temp);
                }
                rs.close();
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(conn!=null){
            try {
                conn.close();
                Log.e("DB","数据库退出！");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return modules;
    }
    public static boolean saveModule(String name,double k,double b,String create_time){
        Log.e("DB","Saving");
        String sql = "INSERT INTO modules(name,k,b,create_time) "
                + " VALUES (?,?,?,?)";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name) ;			// 设置第一个“?”的内容
            stmt.setDouble(2,k);		// 设置第二个“?”的内容
            stmt.setDouble(3,b); ;			// 设置第三个“?”的内容
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            //java.sql.Date sqldate=new java.sql.Date(date.getTime());
            //stmt.setDate(4,sqldate);      // 设置第四个“?”的内容
            stmt.setString(4,formatter.format(date));
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if(conn!=null){
            try {
                conn.close();
                Log.e("DB","数据库退出！");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }
    public static String getnickname(){
        return nickname;
    }
    public static String getusername(){
        return username;
    }
}


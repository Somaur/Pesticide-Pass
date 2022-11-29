package com.example.pesticide_pass.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FittedModel {
    private final String name;
    private final double k;
    private final double b;
    private final String create_time;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

    public FittedModel(String name, double k, double b, String create_time) {
        this.name = name;
        this.k = k;
        this.b = b;
        this.create_time = create_time;
    }

    public FittedModel(String name, double k, double b) {
        this.name = name;
        this.k = k;
        this.b = b;
        this.create_time = formatter.format(Calendar.getInstance().getTime());
    }

    public String getName() {
        return name;
    }

    public double getB() {
        return b;
    }

    public double getK() {
        return k;
    }

    public String getCreate_time() {
        return create_time;
    }

    public JSONObject toJSON() {
        JSONObject ret = null;
        try {
            ret = new JSONObject();
            ret.put("name", name);
            ret.put("k", k);
            ret.put("b", b);
            ret.put("create_time", create_time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static FittedModel parseJSONString(String json) {
        FittedModel ret = null;
        try {
            ret = parseJSON(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static FittedModel parseJSON(JSONObject jsonObject) {
        FittedModel ret = null;
        try {
            String name = jsonObject.getString("name");
            double k = jsonObject.getDouble("k");
            double b = jsonObject.getDouble("b");
            String create_time;
            if (jsonObject.has("create_time"))
                create_time = jsonObject.getString("create_time");
            else
                create_time = Calendar.getInstance().toString();
            ret = new FittedModel(name, k, b, create_time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}

package com.example.pesticide_pass.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    public static JSONArray listToJSON(List<FittedModel> models) {
        JSONArray jsonArray = new JSONArray();
        for (FittedModel model : models) {
            JSONObject jsonObject = model.toJSON();
            if (jsonObject != null) jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public static ArrayList<FittedModel> parseJSONArray(JSONArray jsonArray) {
        ArrayList<FittedModel> ret = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            FittedModel m = null;
            try {
                m = FittedModel.parseJSON((JSONObject)jsonArray.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (m != null) ret.add(m);
        }
        return ret;
    }

    public static ArrayList<FittedModel> parseJSONArrayString(String str) {
        ArrayList<FittedModel> ret = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(str);
            ret = parseJSONArray(jsonArray);
        } catch (JSONException e) {
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

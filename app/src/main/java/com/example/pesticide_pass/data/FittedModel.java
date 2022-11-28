package com.example.pesticide_pass.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FittedModel {
    private final String name;
    private final double k;
    private final double b;

    public FittedModel(String name, double k, double b) {
        this.name = name;
        this.k = k;
        this.b = b;
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

    public JSONObject toJSON() {
        JSONObject ret = null;
        try {
            ret = new JSONObject();
            ret.put("name", name);
            ret.put("k", k);
            ret.put("b", b);
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
            ret = new FittedModel(name, k, b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}

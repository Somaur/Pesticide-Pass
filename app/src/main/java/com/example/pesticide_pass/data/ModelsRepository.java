package com.example.pesticide_pass.data;

import android.content.Context;
import android.util.Log;

import com.example.pesticide_pass.data.FittedModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class ModelsRepository {
    static private final String fileName = "models.json";

    private static volatile ModelsRepository instance;

    // private constructor : singleton access
    private ModelsRepository(Context context) {
        fittedModels = readLocalModels(context);
    }

    public static ModelsRepository getInstance(Context context) {
        if(instance == null){
            instance = new ModelsRepository(context);
        }
        return instance;
    }

    public ArrayList<FittedModel> fittedModels;

    public static void refresh(Context context) {
        getInstance(context).fittedModels = readLocalModels(context);
    }

    public static void saveToLocale(Context context) {
        writeLocalModels(context, getInstance(context).fittedModels);
    }

    private static ArrayList<FittedModel> readLocalModels(Context context) {
        ArrayList<FittedModel> ret = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(fileName);
            byte[] buff = new byte[1024];
            StringBuilder sb = new StringBuilder();
            int hasRead;
            while ((hasRead = fis.read(buff)) > 0) {
                sb.append(new String(buff, 0, hasRead));
            }

            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); ++i) {
                FittedModel m = FittedModel.parseJSON((JSONObject)jsonArray.get(i));
                if (m != null) ret.add(m);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }
    private static void writeLocalModels(Context context, ArrayList<FittedModel> models) {
        ArrayList<FittedModel> ret = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < models.size(); ++i) {
                jsonArray.put(models.get(i).toJSON());
            }

            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            PrintStream ps = new PrintStream(fos);
            ps.print(jsonArray);
            ps.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

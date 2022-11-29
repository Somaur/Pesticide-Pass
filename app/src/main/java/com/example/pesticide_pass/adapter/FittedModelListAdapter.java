package com.example.pesticide_pass.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.pesticide_pass.R;
import com.example.pesticide_pass.data.FittedModel;
import com.example.pesticide_pass.data.ModelsRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FittedModelListAdapter extends BaseAdapter {

    private List<FittedModel> models;
    private HashMap<Integer, Boolean> selected;
    private Context context;

    public FittedModelListAdapter(Context context, List<FittedModel> models) {
        this.models = models;
        this.context = context;
        this.selected = new HashMap<>();
    }

    public ArrayList<FittedModel> getSelectedModels () {
        ArrayList<FittedModel> ret = new ArrayList<>();
        for (int i = 0; i < models.size(); ++i) {
            if (selected.containsKey(i) && Boolean.TRUE.equals(selected.get(i))) {
                ret.add(models.get(i));
            }
        }
        return ret;
    }

    public ArrayList<Integer> getSelectedIndex () {
        ArrayList<Integer> ret = new ArrayList<>();
        for (int i = 0; i < models.size(); ++i) {
            if (selected.containsKey(i) && Boolean.TRUE.equals(selected.get(i))) {
                ret.add(i);
            }
        }
        return ret;
    }

    public void selectAll() {
        for (int i = 0; i < models.size(); ++i) {
            selected.put(i, true);
        }
        notifyDataSetChanged();
    }

    public void unSelectAll() {
        for (int i = 0; i < models.size(); ++i) {
            selected.put(i, false);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(context).inflate(R.layout.item_fitted_model, null);
            holder.tv1 = view.findViewById(R.id.tv1);
            holder.tv2 = view.findViewById(R.id.tv2);
            holder.iv1 = view.findViewById(R.id.iv1);
            holder.btn1 = view.findViewById(R.id.btn1);
            holder.cb1 = view.findViewById(R.id.cb1);
            holder.iv1.setOnClickListener(new iv1onClickListener(holder));
            holder.btn1.setOnClickListener(new btn1onClickListener(holder));
            holder.cb1.setOnCheckedChangeListener(new cb1OnCheckedChangeListener(holder));
            view.setTag(holder);
        }
        else {
            holder = (Holder) view.getTag();
        }
        holder.i = i;
        holder.tv1.setText(models.get(i).getName());
        holder.tv2.setText(models.get(i).getCreate_time());
        holder.cb1.setChecked(Boolean.TRUE.equals(selected.get(i)));
        return view;
    }

    private static class Holder {
        public TextView  tv1;   // name
        public TextView  tv2;   // create time
        public ImageView iv1;   // delete
        public Button    btn1;  // more information
        public CheckBox  cb1;   // select
        public int       i;     // index
    }

    private class iv1onClickListener implements View.OnClickListener {
        public Holder holder;
        public iv1onClickListener (Holder holder) {
            this.holder = holder;
        }
        @Override
        public void onClick(View view) {
            // delete
            models.remove(holder.i);
            if (models == ModelsRepository.getInstance(context).fittedModels)
                ModelsRepository.saveToLocale(context);
            notifyDataSetChanged();
        }
    }

    private class btn1onClickListener implements View.OnClickListener {
        public Holder holder;
        public btn1onClickListener (Holder holder) {
            this.holder = holder;
        }
        @Override
        public void onClick(View view) {
            final AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
            FittedModel model = models.get(holder.i);
            normalDialog.setTitle(model.getName());
            normalDialog.setMessage("参数k: " + model.getK() +
                    "\n参数b: " + model.getB() +
                    "\n创建时间: " + model.getCreate_time());
            normalDialog.setPositiveButton("确定", (dialog, which) -> {
                //...To-do
            });
            // 显示
            normalDialog.show();
        }
    }

    private class cb1OnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        public Holder holder;
        public cb1OnCheckedChangeListener (Holder holder) {
            this.holder = holder;
        }
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            selected.put(holder.i, b);
        }
    }
}

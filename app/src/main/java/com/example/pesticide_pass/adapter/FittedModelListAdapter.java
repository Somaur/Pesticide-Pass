package com.example.pesticide_pass.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pesticide_pass.R;
import com.example.pesticide_pass.data.FittedModel;
import com.example.pesticide_pass.data.ModelsRepository;

import java.util.List;

public class FittedModelListAdapter extends BaseAdapter {

    private List<FittedModel> models;
    private Context context;

    public FittedModelListAdapter(Context context, List<FittedModel> models) {
        this.models = models;
        this.context = context;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_tagged_image, null);
            holder.tv1 = view.findViewById(R.id.tv1);
            holder.tv2 = view.findViewById(R.id.tv2);
            holder.iv1 = view.findViewById(R.id.iv1);
            holder.btn1 = view.findViewById(R.id.btn1);
            holder.iv1.setOnClickListener(new iv1onClickListener(holder));
            holder.btn1.setOnClickListener(new btn1onClickListener(holder));
            view.setTag(holder);
        }
        else {
            holder = (Holder) view.getTag();
        }
        holder.i = i;
        holder.tv1.setText(models.get(i).getName());
        holder.tv1.setText(models.get(i).getCreate_time());
        return view;
    }

    private static class Holder {
        public TextView  tv1;   // name
        public TextView  tv2;   // create time
        public ImageView iv1;   // delete
        public Button    btn1;  // more information
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
            ModelsRepository.getInstance(context).fittedModels.remove(holder.i);
            ModelsRepository.saveToLocale(context);
            models.remove(holder.i);
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
            // TODO: 点击按钮后弹出一个AlertDialog，展示模型的详细信息
        }
    }
}

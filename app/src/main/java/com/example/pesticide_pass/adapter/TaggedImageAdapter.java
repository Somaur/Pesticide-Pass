package com.example.pesticide_pass.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pesticide_pass.R;
import com.example.pesticide_pass.data.TaggedImage;

import java.util.ArrayList;
import java.util.List;

public class TaggedImageAdapter extends BaseAdapter {

    private Context           context;
    private List<TaggedImage> taggedImages;
    private List<Double> values;

    public TaggedImageAdapter(Context context, List<TaggedImage> data) {
        this.context = context;
        this.taggedImages = data;
        this.values = new ArrayList<Double>();
        for (int i = 0; i < data.size(); ++i) this.values.add(0d);
    }

    public void setTaggedImages(List<TaggedImage> taggedImages) {
        this.taggedImages = taggedImages;
        this.values = new ArrayList<Double>();
        for (int i = 0; i < taggedImages.size(); ++i) this.values.add(0d);
    }

    public void addTaggedImage(TaggedImage ti) {
        this.taggedImages.add(ti);
        this.values.add(0.0);
    }

    public TaggedImage getTaggedImage(int i) {
        return taggedImages.get(i);
    }

    public Double getValue(int i) {
        return values.get(i);
    }

    @Override
    public int getCount() {
        return taggedImages.size();
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
            holder.iv = view.findViewById(R.id.iv);
            holder.tv3 = view.findViewById(R.id.tv3);
            holder.et1 = view.findViewById(R.id.et1);
            holder.et1.addTextChangedListener(new Et1TextWatcher(holder));
            view.setTag(holder);
        }
        else {
            holder = (Holder) view.getTag();
        }
        holder.index = i;
        holder.iv.setImageBitmap(taggedImages.get(i).getBmp());
        double gs = Math.round(taggedImages.get(i).getGrayscale() * 1000) / 1000d;
        holder.tv3.setText(String.valueOf(gs));
        holder.et1.setText(String.valueOf(values.get(i)));
        return view;
    }

    static class Holder {
        public TextView tv3;
        public ImageView iv;
        public EditText et1;
        public int index;
    }

    class Et1TextWatcher implements TextWatcher {

        public Holder holder;

        Et1TextWatcher(Holder holder) {
            this.holder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 0) {
                values.set(holder.index, 0d);
            }
            else {
                values.set(holder.index, Double.parseDouble(editable.toString()));
            }
        }
    }
}

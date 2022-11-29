package com.example.pesticide_pass.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.documentfile.provider.DocumentFile;

import com.example.pesticide_pass.R;
import com.example.pesticide_pass.data.ImageTag;
import com.example.pesticide_pass.data.TaggedImage;
import com.example.pesticide_pass.tools.GetSampleLifecycleObserver;

import java.util.ArrayList;
import java.util.List;

public class TaggedImageListAdapter extends BaseAdapter {

    private final Context           context;
    private final List<TaggedImage> taggedImages;
    private final List<Double>      values;

    private final GetSampleLifecycleObserver getSampleLifecycleObserver;

    private class mGetSample implements GetSampleLifecycleObserver.ReceiveSample {
        int i;
        public mGetSample (int i) {
            this.i = i;
        }
        @Override
        public void receive(ImageTag tag, Uri uri) {
            getTaggedImage(i).setTag(tag, context);
            notifyDataSetChanged();
        }
    }

    public TaggedImageListAdapter(Context context, List<TaggedImage> data, GetSampleLifecycleObserver getSampleLifecycleObserver) {
        this.context = context;
        this.getSampleLifecycleObserver = getSampleLifecycleObserver;
        if (data != null) this.taggedImages = data;
        else this.taggedImages = new ArrayList<>();
        this.values = new ArrayList<>();
        if (data != null) for (int i = 0; i < data.size(); ++i) this.values.add(0d);
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
            holder.iv1 = view.findViewById(R.id.iv1);
            holder.iv2 = view.findViewById(R.id.iv2);
            holder.btn1 = view.findViewById(R.id.btn1);
            holder.tv3 = view.findViewById(R.id.tv3);
            holder.et1 = view.findViewById(R.id.et1);
            holder.et1.addTextChangedListener(new Et1TextWatcher(holder));
            holder.btn1.setOnClickListener(new btn1ClickListener(holder));
            holder.iv2.setOnClickListener(new iv2ClickListener(holder));
            view.setTag(holder);
        }
        else {
            holder = (Holder) view.getTag();
        }
        holder.i = i;
        holder.iv1.setImageURI(taggedImages.get(i).getUri());
        if (taggedImages.get(i).isTagged()) {
            double gs = Math.round(taggedImages.get(i).getGrayscale() * 100) / 100d;
            holder.tv3.setText(String.valueOf(gs));
        }
        else {
            holder.tv3.setText("未取样");
        }
        holder.et1.setText(String.valueOf(values.get(i)));
        return view;
    }

    private static class Holder {
        public TextView  tv3;
        public ImageView iv1;
        public ImageView iv2;
        public EditText  et1;
        public Button    btn1;
        public int       i;
    }

    private class btn1ClickListener implements View.OnClickListener {
        public Holder holder;
        btn1ClickListener(Holder holder) {
            this.holder = holder;
        }

        @Override
        public void onClick(View view) {
            // change Tag
            getSampleLifecycleObserver.setReceive(new mGetSample(holder.i));
            getSampleLifecycleObserver.setImgUri(taggedImages.get(holder.i).getUri());
            getSampleLifecycleObserver.launch();
        }
    }

    private class iv2ClickListener implements View.OnClickListener {
        public Holder holder;
        iv2ClickListener(Holder holder) {
            this.holder = holder;
        }

        @Override
        public void onClick(View view) {
            // remove Item
            Uri uri = taggedImages.get(holder.i).getUri();
            taggedImages.remove(holder.i);
            values.remove(holder.i);
            DocumentFile.fromSingleUri(context.getApplicationContext(), uri).delete();
            notifyDataSetChanged();
        }
    }

    private class Et1TextWatcher implements TextWatcher {

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
                values.set(holder.i, 0d);
            }
            else {
                values.set(holder.i, Double.parseDouble(editable.toString()));
            }
        }
    }
}

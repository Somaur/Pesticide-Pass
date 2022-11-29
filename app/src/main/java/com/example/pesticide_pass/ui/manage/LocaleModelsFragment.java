package com.example.pesticide_pass.ui.manage;

import static com.example.pesticide_pass.data.FittedModel.listToJSON;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pesticide_pass.R;
import com.example.pesticide_pass.adapter.FittedModelListAdapter;
import com.example.pesticide_pass.data.FittedModel;
import com.example.pesticide_pass.data.ModelsRepository;
import com.example.pesticide_pass.tools.debug.FakeRemote;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocaleModelsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocaleModelsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public LocaleModelsFragment() {
        // Required empty public constructor
    }

    public static LocaleModelsFragment newInstance(String param1, String param2) {
        LocaleModelsFragment fragment = new LocaleModelsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ListView lv;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private TextView tv1;
    FittedModelListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_locale_models, container, false);
        lv = view.findViewById(R.id.lv);
        btn1 = view.findViewById(R.id.btn1);
        btn2 = view.findViewById(R.id.btn2);
        btn3 = view.findViewById(R.id.btn3);
        btn4 = view.findViewById(R.id.btn4);
        btn5 = view.findViewById(R.id.btn5);
        btn6 = view.findViewById(R.id.btn6);
        tv1 = view.findViewById(R.id.tv1);
        List<FittedModel> models = ModelsRepository.getInstance(getContext()).fittedModels;
        adapter = new FittedModelListAdapter(getContext(), models);
        lv.setAdapter(adapter);
        lv.setEmptyView(tv1);

        btn1.setOnClickListener(view1 -> adapter.notifyDataSetChanged());
        btn2.setOnClickListener(new View.OnClickListener() {
            Context context;
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
                String msg = listToJSON(adapter.getSelectedModels()).toString();
                normalDialog.setTitle("分享文本");
                normalDialog.setMessage(msg);
                normalDialog.setPositiveButton("复制", (dialog, which) -> {
                    //...To-do
                    ClipboardManager clipboard = (ClipboardManager)context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", msg);
                    clipboard.setPrimaryClip(clip);
                });
                // 显示
                normalDialog.show();
            }
            public View.OnClickListener setContext(Context context) {
                this.context = context;
                return this;
            }
        }.setContext(getContext()));
        // 上传
        btn3.setOnClickListener(view15 -> {
            //...
            FakeRemote.upLoad(adapter.getSelectedModels());
        });
        // 全选
        btn4.setOnClickListener(view14 -> adapter.selectAll());
        // 全不选
        btn5.setOnClickListener(view13 -> adapter.unSelectAll());
        // 批量删除
        btn6.setOnClickListener(view12 -> {
            List<Integer> idx = adapter.getSelectedIndex();
            for (int i = idx.size() - 1; i >= 0; --i) {
                models.remove((int)idx.get(i));
            }
            ModelsRepository.saveToLocale(getContext());
            adapter.notifyDataSetChanged();
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
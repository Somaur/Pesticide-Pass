package com.example.pesticide_pass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pesticide_pass.adapter.TaggedImageAdapter;
import com.example.pesticide_pass.data.ImageTag;
import com.example.pesticide_pass.data.TaggedImage;
import com.example.pesticide_pass.tools.GetPicLifecycleObserver;
import com.example.pesticide_pass.tools.GetSampleLifecycleObserver;
import com.huangxy.actionsheet.ActionSheet;

import java.util.ArrayList;

public class AddModelActivity extends AppCompatActivity{

    private Button btn_add_img;
    private Button btn_create_model;
    private ListView lv;
    private EditText et1;
    private ActionSheet actionSheet;

    private TaggedImageAdapter lvAdapter;

    private GetPicLifecycleObserver    getPicLifecycleObserver;
    private GetSampleLifecycleObserver getSampleLifecycleObserver;

    private class mGetSample implements GetSampleLifecycleObserver.ReceiveSample {
        @Override
        public void receive(ImageTag tag, Uri uri) {
            lvAdapter.addTaggedImage(new TaggedImage(uri, tag, AddModelActivity.this));
            lvAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_model);

        getPicLifecycleObserver = new GetPicLifecycleObserver(this, getActivityResultRegistry());
        getLifecycle().addObserver(getPicLifecycleObserver);
        getSampleLifecycleObserver = new GetSampleLifecycleObserver(this, getActivityResultRegistry());
        getLifecycle().addObserver(getSampleLifecycleObserver);

        btn_add_img = findViewById(R.id.btn1);
        btn_create_model = findViewById(R.id.btn2);
        lv = findViewById(R.id.lv);
        et1 = findViewById(R.id.et1);

        lvAdapter = new TaggedImageAdapter(this, new ArrayList<>(), getSampleLifecycleObserver);
        lv.setAdapter(lvAdapter);

        actionSheet = new ActionSheet.DialogBuilder(this)
                .addSheet("拍照", view -> {
                    getPicLifecycleObserver.setTake_photo(uri -> {
                        getSampleLifecycleObserver.setReceive(new mGetSample());
                        getSampleLifecycleObserver.setImgUri(uri);
                        getSampleLifecycleObserver.launch();
                    });
                    actionSheet.hide();
                })
                .addSheet("从相册获取图片", view -> {
                    getPicLifecycleObserver.setChose_photo((GetPicLifecycleObserver.ReceivePicUriList) uriList -> {
                        for (Uri uri : uriList)
                            lvAdapter.addTaggedImage(new TaggedImage(uri, null, AddModelActivity.this));
                        lvAdapter.notifyDataSetChanged();
                    });
                    actionSheet.hide();
                })
                .addCancelListener(v -> actionSheet.hide())
                .create();

        btn_add_img.setOnClickListener(view -> actionSheet.show());

        btn_create_model.setOnClickListener(view -> {
            if (lvAdapter.getCount() < 2) {
                Toast.makeText(AddModelActivity.this, "请设置两张及以上图片！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (et1.getText().length() == 0) {
                Toast.makeText(AddModelActivity.this, "请输入模型名称！", Toast.LENGTH_SHORT).show();
                return;
            }
            String name = et1.getText().toString();
            ArrayList<Double> xPos = new ArrayList<>();
            ArrayList<Double> yPos = new ArrayList<>();
            for (int i = 0; i < lvAdapter.getCount(); ++i) {
                if (!lvAdapter.getTaggedImage(i).isTagged()) {
                    Toast.makeText(AddModelActivity.this, "有图片未取样！", Toast.LENGTH_SHORT).show();
                    return;
                }
                xPos.add(lvAdapter.getValue(i));
                yPos.add(lvAdapter.getTaggedImage(i).getGrayscale());
            }
            Intent intent = new Intent(AddModelActivity.this, CreateModelActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("xPos", xPos);
            intent.putExtra("yPos", yPos);
            startActivity(intent);

            Intent retIntent = new Intent();
            retIntent.putExtra("model_name", name);
            setResult(RESULT_OK, retIntent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        actionSheet.dismiss();
    }
}
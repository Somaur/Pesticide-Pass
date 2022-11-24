package com.example.pesticide_pass;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pesticide_pass.adapter.TaggedImageAdapter;
import com.example.pesticide_pass.data.ImageTag;
import com.example.pesticide_pass.data.TaggedImage;

import java.util.ArrayList;

public class AddModelActivity extends AppCompatActivity implements TaggedImageAdapter.ICallback {

    Button btn_add_img;
    Button btn_create_model;
    ListView lv;

    TaggedImageAdapter lvAdapter;

    private final ActivityResultLauncher<Intent> resultModelLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<Uri> image_uris = result.getData().getParcelableArrayListExtra("image_uris");
                    for (Uri uri : image_uris) {
                        lvAdapter.addTaggedImage(new TaggedImage(uri, null, AddModelActivity.this));
                    }
                    lvAdapter.notifyDataSetChanged();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_model);

        Intent intent = new Intent();
        intent.putExtra("model_id", "AAA");
        setResult(RESULT_OK, intent);

        btn_add_img = findViewById(R.id.btn1);
        btn_create_model = findViewById(R.id.btn2);
        lv = findViewById(R.id.lv);

        lvAdapter = new TaggedImageAdapter(this, new ArrayList<>(), this);
        lv.setAdapter(lvAdapter);
        btn_add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddModelActivity.this, AddModelByPictureActivity.class);
                resultModelLauncher.launch(intent);
            }
        });

        btn_create_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lvAdapter.getCount() < 2) {
                    Toast.makeText(AddModelActivity.this, "请设置两张及以上图片！", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<Double> xPos = new ArrayList<>();
                ArrayList<Double> yPos = new ArrayList<>();
                for (int i = 0; i < lvAdapter.getCount(); ++i) {
                    if (!lvAdapter.getTaggedImage(i).isTagged()) {
                        Toast.makeText(AddModelActivity.this, "有图片未取样！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    xPos.add(lvAdapter.getTaggedImage(i).getGrayscale());
                    yPos.add(lvAdapter.getValue(i));
                }
                Intent intent = new Intent(AddModelActivity.this, CreateModelActivity.class);
                intent.putExtra("xPos", xPos);
                intent.putExtra("yPos", yPos);
                startActivity(intent);
                finish();
            }
        });
    }



    private final ChangeTagOnAdapter             changeTagOnAdapter = new ChangeTagOnAdapter();
    private final ActivityResultLauncher<Intent> changeTagLauncher  = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            changeTagOnAdapter
    );
    class ChangeTagOnAdapter implements ActivityResultCallback<ActivityResult> {
        private int i;

        public void setI(int i) {
            this.i = i;
        }

        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                ImageTag tag = (ImageTag) result.getData().getSerializableExtra("tag");
                lvAdapter.getTaggedImage(i).setTag(tag, AddModelActivity.this);
                lvAdapter.notifyDataSetChanged();
            }
        }
    }
    @Override
    public void launch(Intent intent, int i) {
        changeTagOnAdapter.setI(i);
        changeTagLauncher.launch(intent);
    }
}
package com.example.pesticide_pass;

import static com.example.pesticide_pass.tools.Image.to600_600;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pesticide_pass.adapter.TaggedImageAdapter;
import com.example.pesticide_pass.data.ImageTag;
import com.example.pesticide_pass.data.TaggedImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class AddModelActivity extends AppCompatActivity implements TaggedImageAdapter.ICallback {

    Button btn_add_img;
    Button btn_create_model;
    ListView lv;

    TaggedImageAdapter lvAdapter;

    Bitmap bmp;
    Uri image_uri;
    private final ActivityResultLauncher<Intent> resultModelLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ImageTag tag = (ImageTag) result.getData().getSerializableExtra("tag");
                    lvAdapter.addTaggedImage(new TaggedImage(image_uri, tag, AddModelActivity.this));
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
                switch (lvAdapter.getCount() % 5) {
                    case 0: bmp = to600_600(BitmapFactory.decodeResource(getResources(), R.drawable.img0_1));
                        break;
                    case 1: bmp = to600_600(BitmapFactory.decodeResource(getResources(), R.drawable.img1_0));
                        break;
                    case 2: bmp = to600_600(BitmapFactory.decodeResource(getResources(), R.drawable.img5_0));
                        break;
                    case 3: bmp = to600_600(BitmapFactory.decodeResource(getResources(), R.drawable.img10_0));
                        break;
                    default: bmp = to600_600(BitmapFactory.decodeResource(getResources(), R.drawable.img15_0));
                        break;
                }
                File temp_image = new File(getCacheDir(), String.format(Locale.CHINA, "temp_%d.jpg", lvAdapter.getCount()));
                if (temp_image.exists()) temp_image.delete();
                try {
                    temp_image.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image_uri = FileProvider.getUriForFile(getApplicationContext(),
                        "com.example.pesticide_pass.FileProvider",
                        temp_image
                );
                // TODO: 此处应该从“照相机”或“相册”获取图片并置入image_uri，现暂用资源文件代替
                //       考虑到相册批量获取图片的可能性，后续还需要视情况进行修改
                try {
                    FileOutputStream fos = new FileOutputStream(temp_image);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // ***********************

                Intent intent = new Intent(AddModelActivity.this, SampleSelectActivity.class);
                intent.putExtra("image_uri", image_uri);
                resultModelLauncher.launch(intent);
            }
        });

        btn_create_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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



    private ChangeTagOnAdapter changeTagOnAdapter = new ChangeTagOnAdapter();
    private final ActivityResultLauncher<Intent> changeTagLauncher = registerForActivityResult(
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
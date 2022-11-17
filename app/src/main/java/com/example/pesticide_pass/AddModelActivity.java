package com.example.pesticide_pass;

import static com.example.pesticide_pass.tools.Image.to600_600;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pesticide_pass.adapter.TaggedImageAdapter;
import com.example.pesticide_pass.data.ImageTag;
import com.example.pesticide_pass.data.TaggedImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddModelActivity extends AppCompatActivity {

    Button btn_add_img;
    Button btn_create_model;
    ListView lv;

    List<TaggedImage>  images;
    TaggedImageAdapter lvAdapter;

    Bitmap bmp;
    private ActivityResultLauncher<Intent> resultModelLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    int x = result.getData().getIntExtra("x", 0);
                    int y = result.getData().getIntExtra("y", 0);
                    images.add(new TaggedImage(bmp, new ImageTag.Dot(x, y)));
                    lvAdapter.setTaggedImages(images);
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
        images = new ArrayList<>();

        lvAdapter = new TaggedImageAdapter(this, images);
        lv.setAdapter(lvAdapter);
        btn_add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (images.size() % 5) {
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
                File temp_image = new File(getCacheDir(), String.format(Locale.CHINA, "temp_%d.jpg", images.size()));
                if (temp_image.exists()) temp_image.delete();
                try {
                    temp_image.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri image_uri = FileProvider.getUriForFile(getApplicationContext(),
                        "com.example.pesticide_pass.FileProvider",
                        temp_image
                );
                // TODO: 此处应该从“照相机”或“相册”获取图片并置入image_uri，现暂用资源文件代替
                // 考虑到相册批量获取图片的可能性，后续还需要视情况进行修改
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

    // TODO: 重写与 AccountSettingActivity 的通信
    //       考虑一个 TaggedImage 对应一个缓存文件，从而避开通信图片
//    // broadcast receiver
//    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals("action.refreshFriend")) {
//            }
//        }
//    };
}
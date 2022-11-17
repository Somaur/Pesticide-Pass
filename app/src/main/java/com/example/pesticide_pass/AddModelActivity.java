package com.example.pesticide_pass;

import static com.example.pesticide_pass.tools.Image.to600_600;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pesticide_pass.data.ImageTag;
import com.example.pesticide_pass.data.TaggedImage;
import com.example.pesticide_pass.tools.debug.ToBeContinued;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddModelActivity extends AppCompatActivity {

    Button btn_add_img;
    ListView lv;

    List<TaggedImage> images;
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

        findViewById(R.id.button).setOnClickListener(ToBeContinued.clickListener);
        btn_add_img = findViewById(R.id.btn);
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
    }

    static class TaggedImageAdapter extends BaseAdapter {

        private Context context;
        private List<TaggedImage> taggedImages;

        public TaggedImageAdapter(Context context, List<TaggedImage> data) {
            this.context = context;
            this.taggedImages = data;
        }

        public void setTaggedImages(List<TaggedImage> taggedImages) {
            this.taggedImages = taggedImages;
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
            if (view == null)
            {
                view = LayoutInflater.from(context).inflate(R.layout.item_tagged_image, null);
            }
            ImageView iv = view.findViewById(R.id.iv);
            TextView tv3 = view.findViewById(R.id.tv3);
            iv.setImageBitmap(taggedImages.get(i).getBmp());
            tv3.setText(String.valueOf(taggedImages.get(i).getGrayscale()));
            return view;
        }
    }
}
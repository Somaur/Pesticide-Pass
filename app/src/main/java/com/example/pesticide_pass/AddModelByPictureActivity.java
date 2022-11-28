package com.example.pesticide_pass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pesticide_pass.tools.BitmapCompressUtils;
import com.example.pesticide_pass.tools.GetPicLifecycleObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AddModelByPictureActivity extends AppCompatActivity {

    Button take_photo;
    Button chose_photo;
    Button check;
    GridLayout mGridLayout;

    double screen_width;
    double screen_height;
    ArrayList<Uri> uriList;

    private GetPicLifecycleObserver getPicLifecycleObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_model_by_picture);

        getPicLifecycleObserver = new GetPicLifecycleObserver(this, getActivityResultRegistry());
        getLifecycle().addObserver(getPicLifecycleObserver);

        take_photo = findViewById(R.id.take_photo);
        chose_photo = findViewById(R.id.chose_photo);
        check = findViewById(R.id.check);
        mGridLayout = findViewById(R.id.gridlayout);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screen_height= dm.heightPixels;

        uriList = new ArrayList<>();

        take_photo.setOnClickListener(view -> {
            getPicLifecycleObserver.setTake_photo(new GetPicLifecycleObserver.ReceivePicUri() {
                @Override
                public void receive(Uri uri) {
                    if (uri != null) {
                        uriList.add(uri);
                        displayImage(uri);
                    }
                }
            });
        });

        chose_photo.setOnClickListener(view -> {
            getPicLifecycleObserver.setChose_photo(new GetPicLifecycleObserver.ReceivePicUriList() {
                @Override
                public void receive(List<Uri> uris) {
                    uriList.addAll(uris);
                    for (Uri uri : uris) displayImage(uri);
                }
            });
        });

        check.setOnClickListener(view -> {
            mySetResult();
            finish();
        });
    }

    private void displayImage(Uri uri) {
        if(uri == null) {
            Toast.makeText(this,"failed to get image", Toast.LENGTH_LONG).show();
            return;
        }
        screen_width = mGridLayout.getWidth();
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Bitmap bitmap2 = BitmapCompressUtils.zoomImage(bitmap,(screen_width-30)/3,(screen_width-10)/3);

        ImageView iv = new ImageView(getApplicationContext());
        iv.setImageBitmap(bitmap2);
        iv.setPadding(5,5,5,5);
        mGridLayout.addView(iv);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mySetResult();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void mySetResult() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("image_uris", uriList);
        setResult(RESULT_OK, intent);
    }
}

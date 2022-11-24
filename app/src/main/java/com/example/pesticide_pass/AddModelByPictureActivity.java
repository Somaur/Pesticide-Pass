package com.example.pesticide_pass;

import static com.example.pesticide_pass.tools.FileTools.WriteBitmapToUri;
import static com.example.pesticide_pass.tools.FileTools.getNewTempUri;
import static com.example.pesticide_pass.tools.FileTools.getPathFromUri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pesticide_pass.tools.BitmapCompressUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;


public class AddModelByPictureActivity extends AppCompatActivity {

    Button take_photo;
    Button chose_photo;
    Uri imgUri;
    final int TAKE_PHOTO=1;
    final int CHOSE_PHOTO=2;
    double screen_width;
    double screen_height;
    ArrayList<Uri> uriList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_model_by_picture);

        take_photo=findViewById(R.id.take_photo);
        chose_photo=findViewById(R.id.chose_photo);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screen_width = dm.widthPixels;
        screen_height= dm.heightPixels;

        uriList = new ArrayList<>();

        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTake_photo();
            }
        });

        chose_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChose_photo();
            }

        });
    }

    /**
     * 创建file对象储存拍摄到的照片,将图片命名为output_image.jpg，将他存储在sd卡的关联目录下，调用getExternalCacheDir()
     * 方法可以获得这个目录
     */
    private void setTake_photo(){
        imgUri = getNewTempUri(AddModelByPictureActivity.this, "temp_image.jpg");
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    /*  其实可以理解为系统帮你预设好了的标识符，有
        RESULT_OK
        RESULT_CANCELED
        RESULT_FIRST_USER
        在调用系统activity时返回时RESULT_CANCELED如字面意思代表取消，RESULT_OK代表成功。*/
   /* 意思是当Activity的启动模式是singleTask时,这个Activity不会运行在该task任务栈中.
        并且会马上收到一个cancel result的信号.这就是原因了.比如Activity A 使用startActivityForResult()跳转到Activity B中,
        同时A的启动模式是SingleTask, 这时一调用startActivityForResult()去跳转B,
        A中的onActivityResult()方法会马上收到一个RESULT_CANCEL(值为0)的resultCode.这样RESULT_OK是无法被响应的.*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case TAKE_PHOTO:
                Log.d("MainActivity","输出为："+requestCode);

                if (resultCode == Activity.RESULT_OK){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri));
                        Bitmap bitmap2 = BitmapCompressUtils.zoomImage(
                                bitmap,
                                (screen_width - 30) / 3,
                                (screen_width - 10) / 3);
                        uriList.add(imgUri);

                        GridLayout mGridLayout = (GridLayout) findViewById(R.id.gridlayout);
                        ImageView iv = new ImageView(getApplicationContext());
                        iv.setImageBitmap(bitmap2);
                        iv.setPadding(5, 5, 5, 5);
                        mGridLayout.addView(iv);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    DocumentFile.fromSingleUri(getApplicationContext(), imgUri).delete();
                }
                break;

            case CHOSE_PHOTO:
                Log.d("MainActivity","输出为："+requestCode);
                if (resultCode == Activity.RESULT_OK){
                    //判断系统版本，4.4以上系统用这个方法处理图片
                    imgUri = getNewTempUri(AddModelByPictureActivity.this, "temp_image.jpg");
                    handleImageBeforeKiKat(data);
                    uriList.add(imgUri);
                }
                break;

            default:break;

        }
    }

    private void setChose_photo(){
        if (ContextCompat.checkSelfPermission(AddModelByPictureActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddModelByPictureActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            } else {
                Toast.makeText(this, "你还没有统一访问相册的权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //选择相册 intent.setType(“audio/*”); //选择音频 intent.setType(“video/*”); //选择视频
    //这是正常的访问系统自带的文件管理器。但是setType只支持单个setType一般是以下这种(以只查看图片文件为例):
    private void openAlbum() {
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOSE_PHOTO);
    }

    private void handleImageBeforeKiKat(Intent data) {
        String imgPath;
        Uri uri = data.getData();
        imgPath = getPathFromUri(AddModelByPictureActivity.this, uri);

//        Log.e("TAG", "imgPath: " + imgPath);
        displayImage(imgPath);//显示选中的图片

    }

    private void displayImage(String imagePath) {
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Bitmap bitmap2= BitmapCompressUtils.zoomImage(bitmap,(screen_width-30)/3,(screen_width-10)/3);

            WriteBitmapToUri(AddModelByPictureActivity.this, bitmap, imgUri);

            GridLayout mGridLayout =(GridLayout) findViewById(R.id.gridlayout);
            ImageView iv=new ImageView(getApplicationContext());
            iv.setImageBitmap(bitmap2);
            iv.setPadding(5,5,5,5);
            mGridLayout.addView(iv);
        }else{
            Toast.makeText(this,"failed to get image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("image_uris", uriList);
            setResult(RESULT_OK, intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}

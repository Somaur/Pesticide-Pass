package com.example.pesticide_pass;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AddModelByPictureActivity extends AppCompatActivity {

    Button take_photo;
    Button chose_photo;
    Button create_model;
    ImageView pictureList;
    Uri imgUri;
    final int TAKE_PHOTO=1;
    final int CHOSE_PHOTO=2;
    double screen_width;
    double screen_height;
    BitmapList bitmapList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_model_by_picture);

        take_photo=findViewById(R.id.take_photo);
        chose_photo=findViewById(R.id.chose_photo);
        create_model=findViewById(R.id.create_model);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screen_width = dm.widthPixels;
        screen_height= dm.heightPixels;

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

        create_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用模型创建模块
                //传递参数为bitmapList,存储了所有选中的bitmap.
            }
        });
    }

    private void setTake_photo(){
        //创建file对象储存拍摄到的照片,将图片命名为output_image.jpg，将他存储在sd卡的关联目录下，调用getExternalCacheDir()
        //方法可以获得这个目录
        File outputImg=new File(getExternalCacheDir(),"output_image.jpg");
        try {
            if (outputImg.exists()){
                outputImg.delete();
            }
            outputImg.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }

        //判断系统版本，低于7.0会将file对象转换为uir对象否则调用getUriForFile将file对象转化为一个封装过的uir对象
        //因为7.0开始直接使用本地真实路径会被认为是不安全的会抛出FileUirExposeption异常，FileProvider是一个
        //内容提供器会将封装的uir提供给外部
        if (Build.VERSION.SDK_INT>=24){
            imgUri= FileProvider.getUriForFile(getApplicationContext(),"com.example.pesticide_pass.image_provider",outputImg);
            String adb=imgUri.toString();

        }else {
            imgUri= Uri.fromFile(outputImg);
        }
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");

           /* 先来说下intent的作用，intent是Android程序中各组件之间进行交互的一种重要方式，一般被用来启动活动、启动服务以及发送广播等；
           intent在启动Activity的时候可以传递数据，比如说给另一个Activity传递数据，那么活动与活动之间是怎样进行数据传递的呢？
            这时候就需要用到putExtra()方法。intent中提供一系列的putExtra()方法的重载，可以把想要传递的数据暂存在intent中，
            当另一个活动启动后，再把这些数据从intent缓存中取出即可。
            putExtra("A", B)方法中，AB为键值对，第一个参数为键名，第二个参数为键对应的值，这个值才是真正要传递的数据。*/

          /*  当向intent传入 MediaStore.EXTRA_OUTPUT参数后，表明这是一个存储动作。
            相机拍摄到的图片会直接存储到相应路径，不会缓存在内存中。*/
        //intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);指定图片输出地址
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
        startActivityForResult(intent,TAKE_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case TAKE_PHOTO:
                Log.d("MainActivity","输出为："+requestCode);
          /*  其实可以理解为系统帮你预设好了的标识符，
            有
            RESULT_OK
            RESULT_CANCELED
                    RESULT_FIRST_USER
            在调用系统activity时返回时RESULT_CANCELED如字面意思代表取消，RESULT_OK代表成功。*/
           /* 意思是当Activity的启动模式是singleTask时,这个Activity不会运行在该task任务栈中.
                    并且会马上收到一个cancel result的信号.这就是原因了.比如Activity A 使用startActivityForResult()跳转到Activity B中,
            同时A的启动模式是SingleTask, 这时一调用startActivityForResult()去跳转B,
                A中的onActivityResult()方法会马上收到一个RESULT_CANCEL(值为0)的resultCode.这样RESULT_OK是无法被响应的.*/

                if (resultCode == Activity.RESULT_OK){
                    try {
                        //将拍摄的照片显示出来
                    /*   BitmapFactory.decodeByteArray(byte[] data, int offset, int length)
                                         从指定字节数组的offset位置开始，将长度为length的字节数据解析成Bitmap对象

                                            BitmapFactory.decodeFile(String path)
                    　 　该方法将指定路径的图片转成Bitmap，
                    　

                                            BitmapFactory.decodeFile(String path, Options options)
                    　　 该方法使用options的变量信息，将指定路径的图片转成Bitmap

                                            decodeResource()
                    　 可以将/res/drawable/内预先存入的图片转换成Bitmap对象

                                            decodeStream()
                    　 方法可以将InputStream对象转换成Bitmap对象。*/
                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri));
                        BitmapCompressUtils bitmapCompressUtils=new BitmapCompressUtils();
                        Bitmap bitmap2=bitmapCompressUtils.zoomImage(bitmap,(screen_width-30)/3,(screen_width-10)/3);
                        bitmapList.list.add(bitmap2);
                        GridLayout mGridLayout =(GridLayout) findViewById(R.id.gridlayout);
                        ImageView iv=new ImageView(getApplicationContext());
                        iv.setImageBitmap(bitmap2);
                        iv.setPadding(5,5,5,5);
                        mGridLayout.addView(iv);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CHOSE_PHOTO:
         /*   String adb=imgUri.toString();
            Log.d("MainActivity","s输出为："+adb);*/
                Log.d("MainActivity","输出为："+requestCode);
                //判断系统版本，4.4以上系统用这个方法处理图片
                if (Build.VERSION.SDK_INT>=31){
                    handleImageOnKiKat(data);
                }else {
                    handleImageBeforeKiKat1(data);
                }
                break;

            default:break;

        }
    }

    private void setChose_photo(){
        if (ContextCompat.checkSelfPermission(AddModelByPictureActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddModelByPictureActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else {
            openAlbum1();
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this,"你还没有统一访问相册的权限",Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void openAlbum() {
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        //选择相册 intent.setType(“audio/*”); //选择音频 intent.setType(“video/*”); //选择视频
        //这是正常的访问系统自带的文件管理器。但是setType只支持单个setType一般是以下这种(以只查看图片文件为例):
        intent.setType("image/*");
        startActivityForResult(intent,CHOSE_PHOTO);
    }

    private void openAlbum1() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), CHOSE_PHOTO);
    }

    private void handleImageBeforeKiKat(Intent data) {
        String imgPath=null;
        Uri uri=data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            //如果是Document类型的uri，则使用Document id处理
            String docid=DocumentsContract.getDocumentId(uri);
  /*1、“==”比较两个变量本身的值，即两个对象在内存中的首地址。
            (java中，对象的首地址是它在内存中存放的起始地址，它后面的地址是用来存放它所包含的各个属性的地址，所以内存中会用多个内存块来存放对象的各个参数，
            而通过这个首地址就可以找到该对象，进而可以找到该对象的各个属性)
            2、“equals()”比较字符串中所包含的内容是否相同。*/
            /*uri.getAuthority()返回此URL的权限部分，如果此URL没有权限，则返回null。*/

            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docid.split(":")[1];//解析出数字格式的 id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imgPath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docid));
                imgPath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是 content 类型的 uri ， 则使用普通方式处理
            imgPath = getImagePath(uri, null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是 file 类型的 Uri，直接获取图片路径即可
            imgPath = uri.getPath();
        }
        displayImage(imgPath);//显示选中的图片

    }

    private void decodeUri(Uri uri){
        String imgPath=null;
        if (DocumentsContract.isDocumentUri(this,uri)){
            //如果是Document类型的uri，则使用Document id处理
            String docid=DocumentsContract.getDocumentId(uri);
  /*1、“==”比较两个变量本身的值，即两个对象在内存中的首地址。
            (java中，对象的首地址是它在内存中存放的起始地址，它后面的地址是用来存放它所包含的各个属性的地址，所以内存中会用多个内存块来存放对象的各个参数，
            而通过这个首地址就可以找到该对象，进而可以找到该对象的各个属性)
            2、“equals()”比较字符串中所包含的内容是否相同。*/
            /*uri.getAuthority()返回此URL的权限部分，如果此URL没有权限，则返回null。*/

            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docid.split(":")[1];//解析出数字格式的 id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imgPath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docid));
                imgPath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是 content 类型的 uri ， 则使用普通方式处理
            imgPath = getImagePath(uri, null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是 file 类型的 Uri，直接获取图片路径即可
            imgPath = uri.getPath();
        }
        displayImage(imgPath);//显示选中的图片
    }


    private void handleImageBeforeKiKat1(Intent data) {
        ClipData imageNames = data.getClipData();
        if (imageNames != null){
            for (int i=0; i<imageNames.getItemCount(); i++){
                Uri uri = imageNames.getItemAt(i).getUri();
                decodeUri(uri);
            }
            //uri = imageNames.getItemAt(0).getUri();
        }else {
            Uri uri = data.getData();
            decodeUri(uri);
            //fileList.add(uri.toString());
        }
    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过 Uri 和 selection 来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){

            Log.d("MainActivity","co count："+cursor.getColumnCount()
                    +"row"+cursor.getCount());
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }



    private void handleImageOnKiKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }
    private void displayImage(String imagePath) {
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            BitmapCompressUtils bitmapCompressUtils=new BitmapCompressUtils();
            Bitmap bitmap2=bitmapCompressUtils.zoomImage(bitmap,(screen_width-30)/3,(screen_width-10)/3);
            bitmapList.list.add(bitmap2);
            GridLayout mGridLayout =(GridLayout) findViewById(R.id.gridlayout);
            ImageView iv=new ImageView(getApplicationContext());
            iv.setImageBitmap(bitmap2);
            iv.setPadding(5,5,5,5);
            mGridLayout.addView(iv);
        }else{
            Toast.makeText(this,"failed to get image", Toast.LENGTH_LONG).show();
        }
    }


}

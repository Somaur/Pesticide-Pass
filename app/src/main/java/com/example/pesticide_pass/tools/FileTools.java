package com.example.pesticide_pass.tools;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.Calendar;

public class FileTools {
    /**
     * 创建一个新的临时文件，并得到它的Uri。
     * 传入参数的 name 将会被加上时间戳以避免重复，所以不要用这个名字来查找文件。
     * @param context 传入Activity的context
     * @param name 临时文件的名字
     * @return new create file's Uri
     */
    public static Uri getNewTempUri(Context context, String name) {
        long time = Calendar.getInstance().getTimeInMillis() % 1000000000;
        File tempFile = new File(context.getCacheDir(),time + name);
        Uri uri;
        if (tempFile.exists()) tempFile.delete();
        try {
            tempFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT>=24){
            uri= FileProvider.getUriForFile(context.getApplicationContext(),
                    "com.example.pesticide_pass.FileProvider",
                    tempFile);
        }else {
            uri= Uri.fromFile(tempFile);
        }
        return uri;
    }

    public static String getPathFromUri(Context context, Uri uri) {
        String imgPath = null;

        if (DocumentsContract.isDocumentUri(context, uri)) {
            //如果是Document类型的uri，则使用Document id处理

            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的 id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imgPath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);

            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                imgPath = getImagePath(context, contentUri, null);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是 content 类型的 uri ， 则使用普通方式处理
            imgPath = getImagePath(context, uri, null);

        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是 file 类型的 Uri，直接获取图片路径即可
            imgPath = uri.getPath();
        }

        return imgPath;
    }


    /**
     * 此方法应该放弃使用
     * 仅在 4.4 及更低版本有效
     */
    @SuppressLint("Range")
    private static String getImagePath(Context context, Uri uri, String selection) {
        String path = null;
        //通过 Uri 和 selection 来获取真实的图片路径
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
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

    public static void WriteBitmapToUri(Context context, Bitmap bmp, Uri uri) {
        String path = uri.getPath();
        try {
            FileOutputStream fos = new FileOutputStream(path);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

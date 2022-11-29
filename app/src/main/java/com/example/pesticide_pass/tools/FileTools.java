package com.example.pesticide_pass.tools;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
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
import java.io.OutputStream;
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

    public static void WriteBitmapToUri(Context context, Bitmap bmp, Uri uri) {
        try {
            OutputStream fos = context.getContentResolver().openOutputStream(uri);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

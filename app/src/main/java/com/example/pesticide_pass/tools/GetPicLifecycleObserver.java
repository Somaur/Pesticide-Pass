package com.example.pesticide_pass.tools;

import static com.example.pesticide_pass.tools.FileTools.WriteBitmapToUri;
import static com.example.pesticide_pass.tools.FileTools.getNewTempUri;
import static com.example.pesticide_pass.tools.Image.to600_600;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetPicLifecycleObserver implements DefaultLifecycleObserver {

    private final ActivityResultRegistry mRegistry;
    private       Context                context;
    private       Uri                    imgUri;

    private ReceivePicUri     receivePicUri;
    private ReceivePicUriList receivePicUriList;
    private boolean           takeSinglePic;

    private final String temp_file_name = "temp_image.jpg";

    private ActivityResultLauncher<String> requestPermissionsLauncher;
    private ActivityResultLauncher<Intent> takePhotoLauncher;
    private ActivityResultLauncher<Intent> choosePhotoLauncher;

    public GetPicLifecycleObserver(@NonNull Context context, ActivityResultRegistry registry) {
        this.context = context;
        this.mRegistry = registry;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        // ...

        takePhotoLauncher = mRegistry.register("takePhoto", owner, new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        receivePicUri.receive(imgUri);
                    }
                });
        choosePhotoLauncher = mRegistry.register("choosePhoto", owner, new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                        handleImageBeforeKiKat(result.getData());
                    }
                });
        requestPermissionsLauncher = mRegistry.register("rp_key1", owner, new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        openAlbum();
                    } else {
                        Toast.makeText(context, "你还没有统一访问相册的权限", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setTake_photo(ReceivePicUri receivePicUri){
        this.receivePicUri = receivePicUri;
        imgUri = getNewTempUri(context, temp_file_name);

        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        takePhotoLauncher.launch(intent);
    }

    public void setChose_photo(ReceivePicUri receivePicUri){
        this.receivePicUri= receivePicUri;
        this.takeSinglePic = true;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissionsLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }else {
            openAlbum();
        }
    }

    public void setChose_photo(ReceivePicUriList receivePicUriList){
        this.receivePicUriList = receivePicUriList;
        this.takeSinglePic = false;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissionsLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (!takeSinglePic) intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        choosePhotoLauncher.launch(intent);
    }

    private void handleImageBeforeKiKat(Intent data) {
        ClipData imageNames = data.getClipData();
        if (imageNames != null) {
            ArrayList<Uri> uriList = new ArrayList<>();
            for (int i=0; i<imageNames.getItemCount(); i++){
                Uri uri = imageNames.getItemAt(i).getUri();
                uri = moveToCache(uri);
                if (uri != null) uriList.add(uri);
            }
            receivePicUriList.receive(uriList);
        } else {
            Uri uri = data.getData();
            uri = moveToCache(uri);
            if (takeSinglePic) receivePicUri.receive(uri);
            else {
                ArrayList<Uri> uriList = new ArrayList<>();
                if (uri != null) uriList.add(uri);
                receivePicUriList.receive(uriList);
            }
        }
    }

    private Uri moveToCache(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            Uri new_uri = getNewTempUri(context, temp_file_name);
            WriteBitmapToUri(context, to600_600(bitmap), new_uri);
            return new_uri;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface ReceivePicUri {
        void receive(Uri uri);
    }

    public interface ReceivePicUriList {
        void receive(List<Uri> uriList);
    }
}

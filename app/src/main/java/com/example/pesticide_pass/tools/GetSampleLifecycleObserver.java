package com.example.pesticide_pass.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.pesticide_pass.AddModelActivity;
import com.example.pesticide_pass.SampleSelectActivity;
import com.example.pesticide_pass.data.ImageTag;

public class GetSampleLifecycleObserver implements DefaultLifecycleObserver {

    private final ActivityResultRegistry mRegistry;
    private       Context                context;

    private ReceiveSample receiveSample;
    private Uri imgUri;

    private ActivityResultLauncher<Intent> getSampleLauncher;

    public GetSampleLifecycleObserver(@NonNull Context context, ActivityResultRegistry registry) {
        this.context = context;
        this.mRegistry = registry;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        // ...

        getSampleLauncher = mRegistry.register("getSample", owner, new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        ImageTag tag = (ImageTag) result.getData().getSerializableExtra("tag");
                        receiveSample.receive(tag, imgUri);
                    }
                });
    }

    public void launch() {
        Intent intent = new Intent(context, SampleSelectActivity.class);
        intent.putExtra("image_uri", imgUri);
        getSampleLauncher.launch(intent);
    }

    public void setImgUri(Uri uri) {
        this.imgUri = uri;
    }

    public void setReceive(ReceiveSample receiveSample) {
        this.receiveSample = receiveSample;
    }

    public interface ReceiveSample {
        void receive(ImageTag tag, Uri uri);
    }
}

package com.example.pesticide_pass.data;

import static com.example.pesticide_pass.tools.Image.to600_600;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.Serializable;

public class TaggedImage {

    private Uri    uri;
    private ImageTag tag;
    private double   grayscale = 0.0;

    public TaggedImage(Uri uri, ImageTag tag, Context context) {
        this.uri = uri;
        this.tag = tag;
        updateGrayscale(context);
    }

    private void updateGrayscale(Context context) {
        grayscale = getGrayscale(uri, tag, context);
    }

    public double getGrayscale() {
        return grayscale;
    }

    public Uri getUri() {
        return uri;
    }

    public ImageTag getTag() {
        return tag;
    }

    public void setTag(ImageTag tag, Context context) {
        this.tag = tag;
        updateGrayscale(context);
    }

    public boolean isTagged() {
        return tag != null;
    }


    public static double getGrayscale(Uri uri, ImageTag tag, Context context) {
        try {
            return getGrayscale(
                    to600_600(BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri))),
                    tag
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1d;
        }
    }

    public static double getGrayscale(Bitmap bmp, ImageTag tag) {
        if (tag instanceof ImageTag.Dot) {
            int pixel = bmp.getPixel(((ImageTag.Dot) tag).x, ((ImageTag.Dot) tag).y);
            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);
            return red * .229 + green * .587 + blue * .114;
        }
        return -1d;
    }
}
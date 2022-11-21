package com.example.pesticide_pass.data;

import android.graphics.Bitmap;
import android.graphics.Color;

public class TaggedImage {
    private Bitmap   bmp;
    private ImageTag tag;
    private double   grayscale = 0.0;

    public TaggedImage(Bitmap bmp, ImageTag tag) {
        this.bmp = bmp;
        this.tag = tag;
        updateGrayscale();
    }

    private void updateGrayscale() {
        if (tag instanceof ImageTag.Dot) {
            int pixel = bmp.getPixel(((ImageTag.Dot) tag).x, ((ImageTag.Dot) tag).y);
            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);
            grayscale = red * .229 + green * .587 + blue * .114;
        }
    }

    public double getGrayscale() {
        return grayscale;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setTag(ImageTag tag) {
        this.tag = tag;
        updateGrayscale();
    }
}
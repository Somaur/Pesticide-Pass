package com.example.pesticide_pass.tools;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class Image {
    public static Bitmap to600_600(Bitmap bitmap) {
        int bmpW = bitmap.getWidth();
        int bmpH = bitmap.getHeight();
        float scaleW = 600f / bmpW;
        float scaleH = 600f / bmpH;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);
        return Bitmap.createBitmap(bitmap, 0, 0, bmpW, bmpH, matrix, true);
    }

}

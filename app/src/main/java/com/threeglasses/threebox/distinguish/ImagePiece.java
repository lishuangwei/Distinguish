package com.threeglasses.threebox.distinguish;

import android.graphics.Bitmap;

/**
 * Created by shuangwei on 18-11-8.
 */

public class ImagePiece {
    private int index;
    private Bitmap bitmap;
    private String finger;

    public String getFinger() {
        return finger;
    }

    public void setFinger(String finger) {
        this.finger = finger;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

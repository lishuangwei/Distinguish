package com.threeglasses.threebox.distinguish;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import java.util.List;

/**
 * Created by shuangwei on 18-11-8.
 */

public class SimilarPhoto {
    public static final String TYPE_NO = "no";
    public static final String TYPE_LR = "left_right";
    public static final String TYPE_UD = "up_down";

    public static String find(List<ImagePiece> photos, int small) {
        if (photos.size() <= 0) return null;
        calculateFingerPrint(photos);
        int hor = hamDist(Long.parseLong(photos.get(0).getFinger()), Long.parseLong(photos.get(1).getFinger()));
        int hor1 = hamDist(Long.parseLong(photos.get(2).getFinger()), Long.parseLong(photos.get(3).getFinger()));
        Log.d("shuang", "hamDist: hor 汉明距离=" + hor);
        Log.d("shuang", "hamDist: hor1 汉明距离=" + hor1);
        int ver = hamDist(Long.parseLong(photos.get(0).getFinger()), Long.parseLong(photos.get(2).getFinger()));
        int ver1 = hamDist(Long.parseLong(photos.get(1).getFinger()), Long.parseLong(photos.get(3).getFinger()));
        Log.d("shuang", "hamDist: ver 汉明距离=" + ver);
        Log.d("shuang", "hamDist: ver1 汉明距离=" + ver1);
        int avgh = (hor + hor1) / 2;
        int avgv = (ver + ver1) / 2;
        if (avgh <= avgv && avgh <= small && hor > 0 && hor1 > 0) {
            return TYPE_LR;
        } else if (avgv <= avgh && avgv <= small && avgv > 0 && ver > 0 && ver1 > 0) {
            return TYPE_UD;
        } else {
            return TYPE_NO;
        }
    }

    private static void calculateFingerPrint(List<ImagePiece> photos) {
        for (ImagePiece p : photos) {
            Bitmap scaledBitmap = scale(toGrayscale(p.getBitmap()));
            p.setFinger(getFingerPrint(scaledBitmap) + "");
        }
    }

    private static long getFingerPrint(Bitmap bitmap) {
        double[][] grayPixels = getGrayPixels(bitmap);
        double grayAvg = getGrayAvg(grayPixels);
        return getFingerPrint(grayPixels, grayAvg);
    }

    private static long getFingerPrint(double[][] pixels, double avg) {
        int width = pixels[0].length;
        int height = pixels.length;
        Log.d("shuang", "getFingerPrint: " + pixels.length);
        byte[] bytes = new byte[width * height];

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (pixels[i][j] >= avg) {
                    bytes[i * height + j] = 1;
                    stringBuilder.append("1");
                } else {
                    bytes[i * height + j] = 0;
                    stringBuilder.append("0");
                }
            }
        }

        Log.d("shuang", "getFingerPrint: " + stringBuilder.toString());

        long fingerprint1 = 0;
        long fingerprint2 = 0;
        for (int i = 0; i < 100; i++) {
            if (i < 50) {
                fingerprint1 += (bytes[99 - i] << i);
            } else {
                fingerprint2 += (bytes[99 - i] << (i - 49));
            }
        }

        return (fingerprint2 << 200) + fingerprint1;
    }

    private static double getGrayAvg(double[][] pixels) {
        int width = pixels[0].length;
        int height = pixels.length;
        long count = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double gay = pixels[i][j];
                count += gay;
            }
        }
        Log.d("shuang", "getGrayAvg: count=" + count);
        return count / (width * height);
    }


    private static double[][] getGrayPixels(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        double[][] pixels = new double[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i][j] = computeGrayValue(bitmap.getPixel(i, j));
            }
        }
        return pixels;
    }

    private static int computeGrayValue(int pixel) {
        int alpha = pixel & 0xff000000;
        int red = (pixel >> 16) & 0xFF;
        int green = (pixel >> 8) & 0xFF;
        int blue = (pixel) & 0xff;
        pixel = (red * 77 + green * 151 + blue * 28) >> 8;
        return alpha | (pixel << 16) | (pixel << 8) | pixel;
    }

    private static int hamDist(long finger1, long finger2) {
        int dist = 0;
        long result = finger1 ^ finger2;
        while (result != 0) {
            ++dist;
            result &= result - 1;
        }
        return dist;
    }

    //转为灰度图
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    //缩放图片
    public static Bitmap scale(Bitmap bitmap) {
        float scale_width = 10f / bitmap.getWidth();
        float scale_height = 10f / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale_width, scale_height);
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return scaledBitmap;
    }

}

package ru.oako.collager.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * Draw set of images based on number of pictures
 * Created by Alexei on 27.07.2014.
 */
public class Collage {
    public static final int PIC_LARGE = 660;
    public static final int PIC_THIRD = 440;
    public static final int PIC_MID = 330;
    public static final int PIC_SMALL = 220;


    /**
     * Draw one large image on canvas
     */
    public static Canvas drawOneBitmap(Canvas canvas, Bitmap bitmap, int startHeight) {
        canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, PIC_LARGE, PIC_LARGE, false), 0, startHeight, null);
        return canvas;
    }

    /**
     * Draw two medium sized images on canvas
     */
    public static Canvas drawTwoBitmaps(Canvas canvas, ArrayList<Bitmap> mBitmaps, int startIndex, int startHeight) {
        canvas.drawBitmap(Bitmap.createScaledBitmap(mBitmaps.get(startIndex), PIC_MID, PIC_MID, false), 0, startHeight, null);
        canvas.drawBitmap(Bitmap.createScaledBitmap(mBitmaps.get(startIndex + 1), PIC_MID, PIC_MID, false), PIC_MID, startHeight, null);
        return canvas;
    }

    /**
     * Draw a set of three images on canvas
     */
    public static Canvas drawThreeBitmaps(Canvas canvas, ArrayList<Bitmap> mBitmaps, int startIndex, int startHeight) {
        canvas.drawBitmap(Bitmap.createScaledBitmap(mBitmaps.get(startIndex), PIC_THIRD, PIC_THIRD, false), 0, startHeight, null);
        canvas.drawBitmap(Bitmap.createScaledBitmap(mBitmaps.get(startIndex + 1), PIC_SMALL, PIC_SMALL, false), PIC_THIRD, startHeight, null);
        canvas.drawBitmap(Bitmap.createScaledBitmap(mBitmaps.get(startIndex + 2), PIC_SMALL, PIC_SMALL, false), PIC_THIRD, startHeight + PIC_SMALL, null);
        return canvas;
    }

    /**
     * Calculate height of canvas image based on number of images
     */
    public static int calculateCanvasHeight(int numOfBitmaps) {
        if (numOfBitmaps == 1)
            return PIC_LARGE;
        if (numOfBitmaps == 2)
            return PIC_MID;
        if (numOfBitmaps % 3 == 0)
            return PIC_THIRD * (numOfBitmaps / 3);
        return calculateCanvasHeight(numOfBitmaps - (numOfBitmaps % 3)) + calculateCanvasHeight(numOfBitmaps % 3);
    }
}

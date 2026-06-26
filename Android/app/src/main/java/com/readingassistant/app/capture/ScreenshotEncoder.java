package com.readingassistant.app.capture;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public final class ScreenshotEncoder {
    private static final int MAX_LONG_EDGE = 1280;
    private static final int JPEG_QUALITY = 76;

    private ScreenshotEncoder() {
    }

    public static String toJpegBase64(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return "";
        }
        Bitmap output = scaleIfNeeded(bitmap);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        output.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream);
        if (output != bitmap) {
            output.recycle();
        }
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
    }

    private static Bitmap scaleIfNeeded(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int longEdge = Math.max(width, height);
        if (longEdge <= MAX_LONG_EDGE) {
            return bitmap;
        }
        float ratio = MAX_LONG_EDGE / (float) longEdge;
        int targetWidth = Math.max(1, Math.round(width * ratio));
        int targetHeight = Math.max(1, Math.round(height * ratio));
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
    }
}

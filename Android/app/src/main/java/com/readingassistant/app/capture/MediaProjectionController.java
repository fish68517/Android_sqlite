package com.readingassistant.app.capture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.readingassistant.app.util.DebugLog;

import java.nio.ByteBuffer;

public class MediaProjectionController {
    public interface ScreenshotCallback {
        void onSuccess(String base64Jpeg);

        void onFailure(String message);
    }

    private final Context context;
    private HandlerThread captureThread;
    private Handler captureHandler;
    private MediaProjection mediaProjection;
    private ImageReader imageReader;
    private VirtualDisplay virtualDisplay;
    private int width;
    private int height;
    private int densityDpi;

    public MediaProjectionController(Context context) {
        this.context = context.getApplicationContext();
    }

    public synchronized void start(int resultCode, Intent data) {
        DebugLog.d("MediaProjection start requested resultCode=" + resultCode + " dataNull=" + (data == null));
        release();
        ensureThread();
        MediaProjectionManager manager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (manager == null || data == null) {
            DebugLog.w("MediaProjection start skipped managerNull=" + (manager == null) + " dataNull=" + (data == null));
            return;
        }
        mediaProjection = manager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            DebugLog.w("MediaProjection is null after authorization");
            return;
        }
        mediaProjection.registerCallback(new MediaProjection.Callback() {
            @Override
            public void onStop() {
                DebugLog.w("MediaProjection stopped by system");
                releaseInternal(false);
            }
        }, captureHandler);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getRealMetrics(metrics);
        } else {
            metrics = context.getResources().getDisplayMetrics();
        }
        width = Math.max(1, metrics.widthPixels);
        height = Math.max(1, metrics.heightPixels);
        densityDpi = metrics.densityDpi;
        DebugLog.d("MediaProjection metrics width=" + width + " height=" + height + " densityDpi=" + densityDpi);
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);
        virtualDisplay = mediaProjection.createVirtualDisplay(
                "ReadingAssistantCapture",
                width,
                height,
                densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),
                null,
                captureHandler
        );
        DebugLog.d("MediaProjection virtual display created ready=" + isReady());
    }

    public synchronized boolean isReady() {
        return mediaProjection != null && imageReader != null && virtualDisplay != null;
    }

    public void captureLatestBase64(final ScreenshotCallback callback) {
        if (!isReady()) {
            DebugLog.w("Capture latest skipped: MediaProjection is not ready");
            callback.onFailure("屏幕录制未授权或已失效");
            return;
        }
        ensureThread();
        DebugLog.d("Capture latest scheduled");
        captureHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tryAcquire(callback, 0);
            }
        }, 180);
    }

    private void tryAcquire(final ScreenshotCallback callback, final int attempt) {
        Image image = null;
        Bitmap bitmap = null;
        try {
            image = imageReader.acquireLatestImage();
            if (image == null) {
                if (attempt < 3) {
                    DebugLog.d("Capture image is null, retry attempt=" + (attempt + 1));
                    captureHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tryAcquire(callback, attempt + 1);
                        }
                    }, 150);
                } else {
                    DebugLog.w("Capture image failed after retries");
                    callback.onFailure("暂未获取到屏幕画面");
                }
                return;
            }
            bitmap = imageToBitmap(image);
            String base64 = ScreenshotEncoder.toJpegBase64(bitmap);
            if (base64.isEmpty()) {
                DebugLog.w("Capture encode failed bitmap=" + bitmap.getWidth() + "x" + bitmap.getHeight());
                callback.onFailure("截图编码失败");
            } else {
                DebugLog.d("Capture encode success bitmap=" + bitmap.getWidth()
                        + "x" + bitmap.getHeight()
                        + " base64Length=" + base64.length());
                callback.onSuccess(base64);
            }
        } catch (Exception e) {
            DebugLog.e("Capture failed", e);
            callback.onFailure(e.getMessage() == null ? "截图失败" : e.getMessage());
        } finally {
            if (image != null) {
                image.close();
            }
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    private Bitmap imageToBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        int bitmapWidth = width + rowPadding / pixelStride;
        Bitmap paddedBitmap = Bitmap.createBitmap(bitmapWidth, height, Bitmap.Config.ARGB_8888);
        paddedBitmap.copyPixelsFromBuffer(buffer);
        Bitmap croppedBitmap = Bitmap.createBitmap(paddedBitmap, 0, 0, width, height);
        paddedBitmap.recycle();
        return croppedBitmap;
    }

    private void ensureThread() {
        if (captureThread == null) {
            captureThread = new HandlerThread("reading-capture");
            captureThread.start();
            captureHandler = new Handler(captureThread.getLooper());
        }
    }

    public synchronized void release() {
        DebugLog.d("MediaProjection release requested");
        releaseInternal(true);
    }

    private synchronized void releaseInternal(boolean stopProjection) {
        DebugLog.d("MediaProjection releaseInternal stopProjection=" + stopProjection
                + " hasVirtualDisplay=" + (virtualDisplay != null)
                + " hasImageReader=" + (imageReader != null)
                + " hasProjection=" + (mediaProjection != null));
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
        if (mediaProjection != null) {
            MediaProjection projection = mediaProjection;
            mediaProjection = null;
            if (stopProjection) {
                projection.stop();
            }
        }
        if (captureThread != null) {
            captureThread.quitSafely();
            captureThread = null;
            captureHandler = null;
        }
    }
}

package com.example.xiaoshuo.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 图片复制助手，帮助将外部图片导入到应用中
 */
public class ImageCopyHelper {
    private static final String TAG = "ImageCopyHelper";
    
    /**
     * 准备一个外部图片并将其加载到ImageView中
     * @param context 上下文
     * @param sourcePath 源图片路径
     * @param targetFileName 目标文件名
     * @param imageView 要显示图片的ImageView
     * @return 是否成功
     */
    public static boolean prepareAndLoadExternalImage(Context context, String sourcePath, 
                                                      String targetFileName, ImageView imageView) {
        File targetFile = new File(context.getFilesDir(), targetFileName);
        
        try {
            // 复制文件
            copyFile(new File(sourcePath), targetFile);
            
            // 加载图片
            Bitmap bitmap = BitmapFactory.decodeFile(targetFile.getAbsolutePath());
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                return true;
            } else {
                Log.e(TAG, "Failed to decode bitmap from " + targetFile.getAbsolutePath());
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error preparing image: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 复制文件
     * @param sourceFile 源文件
     * @param destFile 目标文件
     * @throws IOException 如果发生IO异常
     */
    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            throw new IOException("Source file doesn't exist: " + sourceFile.getAbsolutePath());
        }
        
        try (InputStream in = new java.io.FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destFile)) {
            
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }
    
    /**
     * 将assets中的图片加载到ImageView
     * @param context 上下文
     * @param assetFileName assets文件名
     * @param imageView ImageView
     * @return 是否成功
     */
    public static boolean loadImageFromAssets(Context context, String assetFileName, ImageView imageView) {
        AssetManager assetManager = context.getAssets();
        
        try {
            InputStream inputStream = assetManager.open(assetFileName);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                return true;
            } else {
                Log.e(TAG, "Failed to decode bitmap from assets: " + assetFileName);
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading image from assets: " + e.getMessage());
            return false;
        }
    }
} 
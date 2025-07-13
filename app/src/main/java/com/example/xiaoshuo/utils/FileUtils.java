package com.example.xiaoshuo.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件工具类，用于处理图片文件
 */
public class FileUtils {
    private static final String TAG = "FileUtils";
    
    /**
     * 获取drawable目录中的图片资源ID
     * @param context 上下文
     * @param resourceName 资源名称
     * @return 资源ID，如果未找到则返回0
     */
    public static int getDrawableResourceId(Context context, String resourceName) {
        return context.getResources().getIdentifier(
                resourceName, "drawable", context.getPackageName());
    }
    
    /**
     * 从assets目录复制图片到应用私有文件夹
     * @param context 上下文
     * @param assetName assets中的文件名
     * @param targetName 目标文件名
     * @return 目标文件的File对象
     */
    public static File copyImageFromAssets(Context context, String assetName, String targetName) {
        AssetManager assetManager = context.getAssets();
        File targetFile = new File(context.getFilesDir(), targetName);
        
        try {
            InputStream in = assetManager.open(assetName);
            OutputStream out = new FileOutputStream(targetFile);
            
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            
            in.close();
            out.flush();
            out.close();
            return targetFile;
        } catch (IOException e) {
            Log.e(TAG, "Error copying image from assets: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 检查drawable资源是否存在
     * @param context 上下文
     * @param resourceName 资源名称
     * @return 是否存在
     */
    public static boolean isDrawableExists(Context context, String resourceName) {
        int resourceId = getDrawableResourceId(context, resourceName);
        return resourceId != 0;
    }
    
    /**
     * 获取drawable资源
     * @param context 上下文
     * @param resourceName 资源名称
     * @return Drawable对象，如果未找到返回null
     */
    public static Drawable getDrawable(Context context, String resourceName) {
        int resourceId = getDrawableResourceId(context, resourceName);
        if (resourceId != 0) {
            return context.getResources().getDrawable(resourceId, context.getTheme());
        }
        return null;
    }
} 
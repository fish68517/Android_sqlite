package com.example.healthdietapp.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.example.healthdietapp.R;

public final class ImageUtils {

    private static final String TAG = "ImageUtils";

    private ImageUtils() {
        // no instance
    }

    public static void loadFirstImage(ImageView imageView, String imagesStr) {
        if (imageView == null) return;

        // 设置默认兜底图片，防止加载失败时空白
        @DrawableRes int defaultResId = R.drawable.ic_default_image;
        // 兜底图先设置，避免闪烁/复用错图（RecyclerView 场景很重要）


        if (imagesStr == null || imagesStr.trim().isEmpty()) {
            imageView.setImageResource(defaultResId);
            return;
        }

        // 提取逗号分隔的第一张图片
        String firstImage = imagesStr.split(",")[0].trim();
        Log.d(TAG, "Loading first image: " + firstImage);

        // 如果是 content://, file:// 或是 我们生成的绝对路径 /data/...
        if (firstImage.startsWith("content://") || firstImage.startsWith("file://") || firstImage.startsWith("/")) {
            try {
                Uri imageUri;
                if (firstImage.startsWith("/")) {
                    // 对绝对路径进行文件包装，防止 Android 解析器不认
                    imageUri = Uri.fromFile(new java.io.File(firstImage));
                } else {
                    imageUri = Uri.parse(firstImage);
                }
                imageView.setImageURI(imageUri);
            } catch (Exception e) {
                Log.e(TAG, "加载相册/本地路径图片失败: " + firstImage, e);
                imageView.setImageResource(defaultResId);
            }
        } else {
            // 处理默认的 drawable 资源数据 (例如 "img_post1_a.jpg")
            Context context = imageView.getContext();
            String drawableName = firstImage;
            if (drawableName.contains(".")) {
                drawableName = drawableName.substring(0, drawableName.lastIndexOf("."));
            }

            int resId = context.getResources().getIdentifier(
                    drawableName, "drawable", context.getPackageName());

            if (resId != 0) {
                imageView.setImageResource(resId);
            } else {
                Log.w(TAG, "在 drawable 目录中找不到图片: " + drawableName);
                imageView.setImageResource(defaultResId);
            }
        }
    }
}
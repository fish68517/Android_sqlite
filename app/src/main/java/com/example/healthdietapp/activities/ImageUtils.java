package com.example.healthdietapp.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.example.healthdietapp.R;

public final class ImageUtils {

    private static final String TAG = "ImageUtils";

    private ImageUtils() {
        // no instance
    }

    /**
     * 从数据库 images 字段加载图片到 ImageView：
     * - imagesStr 可能是 "a.jpg,b.jpg" 或 "content://..." 或 "/storage/..." 等
     * - 自动取逗号分隔第一张
     * - 支持 content://、file://、绝对路径(/xxx)
     * - 支持 drawable 名（如 img_post1_a 或 img_post1_a.jpg）
     * - 失败时显示 defaultResId
     */
    public static void loadFirstImage(ImageView imageView,
                                      String imagesStr) {
        if (imageView == null) return;

        Context context = imageView.getContext();
        if (context == null) return;

        @DrawableRes int defaultResId = R.drawable.ic_default_image;
        // 兜底图先设置，避免闪烁/复用错图（RecyclerView 场景很重要）
        imageView.setImageResource(defaultResId);

        if (TextUtils.isEmpty(imagesStr) || TextUtils.isEmpty(imagesStr.trim())) {
            Log.d(TAG, "imagesStr empty, use default.");
            return;
        }

        String first = extractFirst(imagesStr);
        Log.d(TAG, "Raw images string: " + imagesStr);
        Log.d(TAG, "Loading first image: " + first);

        if (TextUtils.isEmpty(first)) {
            Log.d(TAG, "first image empty after extract, use default.");
            return;
        }

        // 1) Uri / 文件路径
        if (isUriOrAbsolutePath(first)) {
            try {
                Uri uri = Uri.parse(first);
                imageView.setImageURI(uri);
                return;
            } catch (Exception e) {
                Log.e(TAG, "加载相册/本地路径图片失败: " + first, e);
                imageView.setImageResource(defaultResId);
                return;
            }
        }

        // 2) drawable 名称（可能带后缀）
        String drawableName = stripExtension(first);

        int resId = context.getResources().getIdentifier(
                drawableName, "drawable", context.getPackageName()
        );

        if (resId != 0) {
            try {
                Drawable d = ContextCompat.getDrawable(context, resId);
                if (d != null) {
                    imageView.setImageDrawable(d);
                } else {
                    imageView.setImageResource(defaultResId);
                }
            } catch (Exception e) {
                Log.e(TAG, "加载 drawable 资源失败: " + drawableName + " (resId=" + resId + ")", e);
                imageView.setImageResource(defaultResId);
            }
        } else {
            Log.w(TAG, "在 drawable 目录中找不到图片: " + drawableName + "，使用默认图");
            imageView.setImageResource(defaultResId);
        }
    }

    /** 取逗号分隔的第一张 */
    private static String extractFirst(String imagesStr) {
        String s = imagesStr.trim();
        int idx = s.indexOf(',');
        if (idx >= 0) {
            return s.substring(0, idx).trim();
        }
        return s;
    }

    /** 判断是否为 content:// file:// 或 /xxx 这种绝对路径 */
    private static boolean isUriOrAbsolutePath(String s) {
        String v = s.trim();
        return v.startsWith("content://")
                || v.startsWith("file://")
                || v.startsWith("/");
    }

    /** 去掉 .jpg/.png 等后缀，得到 drawable 名 */
    private static String stripExtension(String name) {
        String n = name.trim();
        int dot = n.lastIndexOf('.');
        if (dot > 0) {
            return n.substring(0, dot);
        }
        return n;
    }
}
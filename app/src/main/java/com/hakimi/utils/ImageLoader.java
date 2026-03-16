package com.hakimi.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hakimi.HakimiApplication;
import com.hakimi.R;

/**
 * Enhanced image loading utility that supports multiple image sources
 *
 * @author hakimi
 */
public class ImageLoader {

    private static String BASE_IMAGE_URL = "http://192.168.2.185:8081/image/";


    public static void setBaseImageUrl(String baseUrl) {
        BASE_IMAGE_URL = baseUrl;
    }

    /**
     * Load image from various sources (remote URL, local server, or drawable resource)
     */
    public static void loadImage(String url, ImageView imageView) {
        if (TextUtils.isEmpty(url)) {
            // 加载默认的图片
            url = "http://192.168.2.185:8081/image/food.jpg";
            Glide.with(HakimiApplication.getInstance())
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            return;

        }

        Log.e("ImageLoader", "Image URL: " + url);

        // Image URL: content://media/external/images/media/22


        if (url.startsWith("content://")) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
                    return;
        }
        // Handle Android drawable resources (format: res://drawable/resource_name)
        if (url.startsWith("res://drawable/")) {
            String resourceName = url.substring("res://drawable/".length());
            int resourceId = imageView.getResources().getIdentifier(resourceName, "drawable", imageView.getContext().getPackageName());
            if (resourceId != 0) {
                Glide.with(imageView.getContext())
                        .load(resourceId)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.food)
                        .placeholder(R.drawable.food)
                        .into(imageView);
            }
            return;
        }



        // Handle relative paths and example.com URLs
        if (!url.startsWith("http")) {
            // url 是否包含 http
            // For direct image names like "catfood1.jpg"
            url = BASE_IMAGE_URL + url;
        } else if (url.contains("example.com")) {
            // Transform example.com URLs to local server
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            url = BASE_IMAGE_URL + fileName;
        }

        Log.e("ImageLoader", "Loading image: " + url);
        Glide.with(HakimiApplication.getInstance())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.food)
                .placeholder(R.drawable.food)
                .into(imageView);
    }

    /**
     * Load circular image with support for multiple image sources
     */
    public static void loadCircleImage(String url, ImageView imageView) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        // Handle Android drawable resources
        if (url.startsWith("res://drawable/")) {
            String resourceName = url.substring("res://drawable/".length());
            int resourceId = imageView.getResources().getIdentifier(resourceName, "drawable", imageView.getContext().getPackageName());
            if (resourceId != 0) {
                Glide.with(imageView.getContext())
                        .load(resourceId)
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }
            return;
        }

        // Handle URL transformation
        if (!url.startsWith("http")) {
            url = BASE_IMAGE_URL + url;
        } else if (url.contains("example.com")) {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            url = BASE_IMAGE_URL + fileName;
        }

        Glide.with(HakimiApplication.getInstance())
                .load(url)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    /**
     * Load image with placeholder, supporting multiple image sources
     */
    public static void loadImageWithPlaceholder(String url, ImageView imageView, int placeholderResId) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        // Handle Android drawable resources
        if (url.startsWith("res://drawable/")) {
            String resourceName = url.substring("res://drawable/".length());
            int resourceId = imageView.getResources().getIdentifier(resourceName, "drawable", imageView.getContext().getPackageName());
            if (resourceId != 0) {
                Glide.with(imageView.getContext())
                        .load(resourceId)
                        .placeholder(placeholderResId)
                        .error(placeholderResId)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }
            return;
        }

        // Handle URL transformation
        if (!url.startsWith("http") || url.startsWith("https")) {
            url = BASE_IMAGE_URL + url;
        } else if (url.contains("example.com")) {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            url = BASE_IMAGE_URL + fileName;
        }

        Glide.with(HakimiApplication.getInstance())
                .load(url)
                .placeholder(placeholderResId)
                .error(placeholderResId)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}

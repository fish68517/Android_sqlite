package com.example.healthdietapp.utils;

import android.content.Context;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

/**
 * ErrorHandler - Centralized error handling and user feedback utility
 * Provides methods for displaying user-friendly error messages
 */
public class ErrorHandler {

    /**
     * Show a short toast message
     */
    public static void showShortToast(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show a long toast message
     */
    public static void showLongToast(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Show an error dialog with a single button
     */
    public static void showErrorDialog(Context context, String title, String message) {
        if (context != null) {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("确定", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    /**
     * Show an error dialog with custom button text
     */
    public static void showErrorDialog(Context context, String title, String message, String buttonText) {
        if (context != null) {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(buttonText, (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    /**
     * Show a confirmation dialog with two buttons
     */
    public static void showConfirmDialog(Context context, String title, String message,
                                        String positiveText, String negativeText,
                                        Runnable onPositive, Runnable onNegative) {
        if (context != null) {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positiveText, (dialog, which) -> {
                        dialog.dismiss();
                        if (onPositive != null) {
                            onPositive.run();
                        }
                    })
                    .setNegativeButton(negativeText, (dialog, which) -> {
                        dialog.dismiss();
                        if (onNegative != null) {
                            onNegative.run();
                        }
                    })
                    .show();
        }
    }

    /**
     * Handle database exceptions
     */
    public static void handleDatabaseException(Context context, Exception e) {
        e.printStackTrace();
        String message = "数据库操作失败，请重试";
        if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
            message = "该数据已存在，请检查输入";
        } else if (e.getMessage() != null && e.getMessage().contains("FOREIGN KEY")) {
            message = "数据关联错误，请重试";
        }
        showShortToast(context, message);
    }

    /**
     * Handle authentication exceptions
     */
    public static void handleAuthenticationException(Context context, String message) {
        if (message == null) {
            message = "认证失败，请重试";
        }
        showShortToast(context, message);
    }

    /**
     * Handle validation exceptions
     */
    public static void handleValidationException(Context context, String message) {
        if (message == null) {
            message = "输入验证失败，请检查输入";
        }
        showShortToast(context, message);
    }

    /**
     * Handle network exceptions (reserved for future use)
     */
    public static void handleNetworkException(Context context, Exception e) {
        e.printStackTrace();
        String message = "网络连接失败，请检查网络设置";
        if (e.getMessage() != null && e.getMessage().contains("timeout")) {
            message = "网络请求超时，请重试";
        }
        showShortToast(context, message);
    }

    /**
     * Handle conflict exceptions
     */
    public static void handleConflictException(Context context, String message) {
        if (message == null) {
            message = "操作冲突，请重试";
        }
        showShortToast(context, message);
    }

    /**
     * Handle generic exceptions
     */
    public static void handleGenericException(Context context, Exception e) {
        e.printStackTrace();
        String message = "操作失败，请重试";
        if (e.getMessage() != null && !e.getMessage().isEmpty()) {
            message = e.getMessage();
        }
        showShortToast(context, message);
    }

    /**
     * Log exception for debugging
     */
    public static void logException(String tag, Exception e) {
        if (e != null) {
            android.util.Log.e(tag, "Exception: " + e.getMessage(), e);
        }
    }
}

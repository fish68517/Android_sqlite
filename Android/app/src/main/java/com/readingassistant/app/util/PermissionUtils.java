package com.readingassistant.app.util;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import com.readingassistant.app.access.ReaderAccessibilityService;

public final class PermissionUtils {
    private PermissionUtils() {
    }

    public static boolean canDrawOverlays(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }

    public static boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        return context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isAccessibilityEnabled(Context context) {
        String enabledServices = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );
        if (enabledServices == null || enabledServices.trim().isEmpty()) {
            return false;
        }
        ComponentName expected = new ComponentName(context, ReaderAccessibilityService.class);
        String expectedName = expected.flattenToString();
        String expectedShortName = expected.flattenToShortString();
        String[] services = enabledServices.split(":");
        for (String service : services) {
            if (expectedName.equalsIgnoreCase(service) || expectedShortName.equalsIgnoreCase(service)) {
                return true;
            }
        }
        return false;
    }

    public static boolean areNotificationsEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            return manager == null || manager.areNotificationsEnabled();
        }
        return true;
    }
}

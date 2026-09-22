package com.example.sensecheck.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public final class PermissionUtils {
    public static final int REQUEST_CODE = 9001;

    private PermissionUtils() {
    }

    public static String[] missingPermissions(Context context) {
        List<String> permissions = new ArrayList<>();
        addIfMissing(context, permissions, Manifest.permission.ACCESS_FINE_LOCATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            addIfMissing(context, permissions, Manifest.permission.BLUETOOTH_SCAN);
            addIfMissing(context, permissions, Manifest.permission.BLUETOOTH_CONNECT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            addIfMissing(context, permissions, Manifest.permission.NEARBY_WIFI_DEVICES);
            addIfMissing(context, permissions, Manifest.permission.POST_NOTIFICATIONS);
        }
        return permissions.toArray(new String[0]);
    }

    public static void requestMissing(Activity activity) {
        String[] permissions = missingPermissions(activity);
        if (permissions.length > 0) {
            activity.requestPermissions(permissions, REQUEST_CODE);
        }
    }

    public static boolean hasLocation(Context context) {
        return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasBluetoothScan(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                ? hasLocation(context)
                : context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasBluetoothConnect(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                || context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasNearbyWifi(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || context.checkSelfPermission(Manifest.permission.NEARBY_WIFI_DEVICES)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static String statusText(Context context) {
        String[] missing = missingPermissions(context);
        if (missing.length == 0) {
            return "权限状态：签到所需权限已授予";
        }
        return "权限状态：仍缺少 " + missing.length + " 项权限，部分证据可能无法采集";
    }

    private static void addIfMissing(Context context, List<String> result, String permission) {
        if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            result.add(permission);
        }
    }
}


package com.readingassistant.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.readingassistant.app.BuildConfig;
import com.readingassistant.app.R;
import com.readingassistant.app.overlay.FloatingSummaryWindow;
import com.readingassistant.app.service.CaptureForegroundService;
import com.readingassistant.app.util.DebugLog;
import com.readingassistant.app.util.PermissionUtils;

public class MainActivity extends Activity {
    private static final int REQUEST_MEDIA_PROJECTION = 2001;
    private static final int REQUEST_NOTIFICATION = 2002;

    private TextView overlayStatus;
    private TextView accessibilityStatus;
    private TextView notificationStatus;
    private TextView batteryStatus;
    private TextView apiStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugLog.d("MainActivity onCreate");
        setContentView(buildContentView());
    }

    @Override
    protected void onResume() {
        super.onResume();
        DebugLog.d("MainActivity onResume");
        updateStatus();
    }

    private View buildContentView() {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(20), dp(20), dp(28));
        scrollView.addView(root);

        TextView title = new TextView(this);
        title.setText("阅读总结辅助");
        title.setTextSize(24);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.rgb(24, 34, 48));
        root.addView(title);

        TextView desc = new TextView(this);
        desc.setText("开启权限后，应用会通过无障碍读取页面文字，必要时使用屏幕录制截图兜底，并把大模型总结显示在顶部悬浮窗。");
        desc.setTextSize(15);
        desc.setTextColor(Color.rgb(90, 100, 116));
        desc.setPadding(0, dp(8), 0, dp(18));
        root.addView(desc);

        overlayStatus = addStatus(root, "悬浮窗权限");
        accessibilityStatus = addStatus(root, "无障碍服务");
        notificationStatus = addStatus(root, "通知权限");
        batteryStatus = addStatus(root, "电池优化");
        apiStatus = addStatus(root, "模型配置");

        root.addView(button("开启悬浮窗权限", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOverlaySettings();
            }
        }));
        root.addView(button("开启无障碍服务", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        }));
        root.addView(button("请求通知权限", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNotificationPermissionIfNeeded();
            }
        }));
        root.addView(button("关闭电池优化", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBatteryOptimizationSettings();
            }
        }));
        root.addView(button("打开 MIUI 权限设置", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMiuiPermissionPage();
            }
        }));
        root.addView(button("测试顶部悬浮窗", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingSummaryWindow.get(MainActivity.this).showSummary(getString(R.string.summary_placeholder));
            }
        }));
        root.addView(primaryButton("开始辅助", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAssistant();
            }
        }));
        root.addView(button("停止辅助", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(CaptureForegroundService.createStopIntent(MainActivity.this));
                Toast.makeText(MainActivity.this, "已请求停止辅助服务", Toast.LENGTH_SHORT).show();
            }
        }));

        TextView note = new TextView(this);
        note.setText("提示：MediaProjection 截屏必须由系统授权，无法完全静默绕过。API Key 请只放在 Android/local.properties。");
        note.setTextSize(13);
        note.setTextColor(Color.rgb(102, 112, 133));
        note.setPadding(0, dp(18), 0, 0);
        root.addView(note);
        return scrollView;
    }

    private TextView addStatus(LinearLayout root, String label) {
        TextView textView = new TextView(this);
        textView.setTextSize(15);
        textView.setTextColor(Color.rgb(24, 34, 48));
        textView.setPadding(0, dp(6), 0, dp(6));
        textView.setText(label + "：检查中");
        root.addView(textView);
        return textView;
    }

    private Button primaryButton(String text, View.OnClickListener listener) {
        Button button = button(text, listener);
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(Color.rgb(36, 107, 253));
        return button;
    }

    private Button button(String text, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setOnClickListener(listener);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dp(8);
        button.setLayoutParams(params);
        return button;
    }

    private void updateStatus() {
        overlayStatus.setText("悬浮窗权限：" + yesNo(PermissionUtils.canDrawOverlays(this)));
        accessibilityStatus.setText("无障碍服务：" + yesNo(PermissionUtils.isAccessibilityEnabled(this)));
        notificationStatus.setText("通知权限：" + yesNo(PermissionUtils.hasNotificationPermission(this) && PermissionUtils.areNotificationsEnabled(this)));
        batteryStatus.setText("电池优化：" + (isIgnoringBatteryOptimizations() ? "已忽略" : "可能受限"));
        apiStatus.setText("模型配置：" + (BuildConfig.GLM_API_KEY.trim().isEmpty() ? "未配置 GLM_API_KEY" : "已配置 GLM"));
        DebugLog.d("Permission status overlay=" + PermissionUtils.canDrawOverlays(this)
                + " accessibility=" + PermissionUtils.isAccessibilityEnabled(this)
                + " notification=" + (PermissionUtils.hasNotificationPermission(this) && PermissionUtils.areNotificationsEnabled(this))
                + " ignoreBattery=" + isIgnoringBatteryOptimizations()
                + " glmKeyConfigured=" + !BuildConfig.GLM_API_KEY.trim().isEmpty());
    }

    private String yesNo(boolean value) {
        return value ? "已开启" : "未开启";
    }

    private void startAssistant() {
        DebugLog.d("Start assistant clicked");
        if (!PermissionUtils.canDrawOverlays(this)) {
            DebugLog.w("Start assistant blocked: overlay permission missing");
            Toast.makeText(this, "请先开启悬浮窗权限", Toast.LENGTH_SHORT).show();
            openOverlaySettings();
            return;
        }
        if (!PermissionUtils.hasNotificationPermission(this)) {
            DebugLog.w("Start assistant blocked: notification permission missing");
            requestNotificationPermissionIfNeeded();
            return;
        }
        MediaProjectionManager manager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (manager == null) {
            DebugLog.w("Start assistant blocked: MediaProjectionManager is null");
            Toast.makeText(this, "当前系统不支持屏幕录制授权", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivityForResult(manager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            DebugLog.d("Request notification permission");
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION);
        } else {
            Toast.makeText(this, "通知权限已开启或当前系统无需申请", Toast.LENGTH_SHORT).show();
        }
    }

    private void openOverlaySettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
            );
            startActivity(intent);
        }
    }

    private void openBatteryOptimizationSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private void openMiuiPermissionPage() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
        ));
        intent.putExtra("extra_pkgname", getPackageName());
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "未找到 MIUI 权限页，请在系统设置中手动开启自启动、后台运行和悬浮窗", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private boolean isIgnoringBatteryOptimizations() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        return manager != null && manager.isIgnoringBatteryOptimizations(getPackageName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DebugLog.d("MainActivity onActivityResult requestCode=" + requestCode
                + " resultCode=" + resultCode
                + " dataNull=" + (data == null));
        if (requestCode != REQUEST_MEDIA_PROJECTION) {
            return;
        }
        if (resultCode != RESULT_OK || data == null) {
            DebugLog.w("MediaProjection permission denied or data null");
            Toast.makeText(this, "未获得屏幕录制授权，只能依赖无障碍文字通道", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent serviceIntent = CaptureForegroundService.createStartIntent(this, resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DebugLog.d("Start foreground service via startForegroundService");
            startForegroundService(serviceIntent);
        } else {
            DebugLog.d("Start service via startService");
            startService(serviceIntent);
        }
        Toast.makeText(this, "辅助服务已启动，可切换到阅读 App", Toast.LENGTH_SHORT).show();
        updateStatus();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}

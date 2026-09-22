package com.example.knowledgelabs.ch06;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowledgelabs.util.LabUi;

public class BroadcastSessionActivity extends Activity {
    private static final String ACTION_FORCE_OFFLINE = "com.example.knowledgelabs.FORCE_OFFLINE";
    private TextView statusView;
    private TextView systemBroadcastView;
    private boolean registered;

    private final BroadcastReceiver sessionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                int percent = scale <= 0 ? 0 : Math.round(level * 100f / scale);
                systemBroadcastView.setText("系统广播：当前电量 " + percent + "%");
                return;
            }
            if (!ACTION_FORCE_OFFLINE.equals(intent.getAction())) {
                return;
            }
            getSharedPreferences("session", MODE_PRIVATE).edit().putBoolean("logged_in", false).apply();
            statusView.setText("当前状态：已被广播强制下线");
            new AlertDialog.Builder(BroadcastSessionActivity.this)
                    .setTitle("登录失效")
                    .setMessage("收到应用内自定义广播，当前账号已强制下线。")
                    .setPositiveButton("确定", null)
                    .show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout container = LabUi.setupSimple(this, "第6章 登录状态广播", "动态注册 BroadcastReceiver 与自定义广播");
        statusView = LabUi.addHeading(this, container, "当前状态：未登录");
        systemBroadcastView = LabUi.addText(this, container, "系统广播：等待电量状态");
        Button login = LabUi.addButton(this, container, "模拟登录");
        Button forceOffline = LabUi.addButton(this, container, "发送强制下线广播");

        login.setOnClickListener(v -> {
            getSharedPreferences("session", MODE_PRIVATE).edit().putBoolean("logged_in", true).apply();
            statusView.setText("当前状态：已登录");
            Toast.makeText(this, "登录状态已保存", Toast.LENGTH_SHORT).show();
        });
        forceOffline.setOnClickListener(v -> {
            Intent intent = new Intent(ACTION_FORCE_OFFLINE).setPackage(getPackageName());
            sendBroadcast(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ACTION_FORCE_OFFLINE);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(sessionReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(sessionReceiver, filter);
        }
        registered = true;
        boolean loggedIn = getSharedPreferences("session", MODE_PRIVATE).getBoolean("logged_in", false);
        statusView.setText(loggedIn ? "当前状态：已登录" : "当前状态：未登录");
    }

    @Override
    protected void onStop() {
        if (registered) {
            unregisterReceiver(sessionReceiver);
            registered = false;
        }
        super.onStop();
    }
}

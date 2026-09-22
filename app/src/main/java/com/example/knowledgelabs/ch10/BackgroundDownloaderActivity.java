package com.example.knowledgelabs.ch10;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.knowledgelabs.R;

public class BackgroundDownloaderActivity extends Activity {
    private static final int REQUEST_NOTIFICATION = 1001;
    private ProgressBar progressBar;
    private TextView statusView;
    private boolean registered;

    private final BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra(DownloadService.EXTRA_PROGRESS, 0);
            progressBar.setProgress(progress);
            statusView.setText(progress >= 100 ? "下载完成" : "后台下载进度：" + progress + "%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_downloader);
        progressBar = findViewById(R.id.progressDownload);
        statusView = findViewById(R.id.tvDownloadStatus);
        findViewById(R.id.btnStartDownload).setOnClickListener(v -> startWithPermission());
        findViewById(R.id.btnStopDownload).setOnClickListener(v -> {
            Intent intent = new Intent(this, DownloadService.class).setAction(DownloadService.ACTION_STOP);
            startService(intent);
            statusView.setText("已请求停止服务");
        });
    }

    private void startWithPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION);
            return;
        }
        progressBar.setProgress(0);
        statusView.setText("正在启动前台 Service…");
        startForegroundService(new Intent(this, DownloadService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(DownloadService.ACTION_PROGRESS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(progressReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(progressReceiver, filter);
        }
        registered = true;
    }

    @Override
    protected void onStop() {
        if (registered) {
            unregisterReceiver(progressReceiver);
            registered = false;
        }
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startWithPermission();
        }
    }
}

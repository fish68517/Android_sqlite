package com.example.knowledgelabs.ch01;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knowledgelabs.BuildConfig;
import com.example.knowledgelabs.util.LabUi;

public final class HelloStartActivity extends Activity {
    private static final String TAG = "Ch01HelloStart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout container = LabUi.setupSimple(
                this,
                "Hello Android",
                "Android Studio、SDK、Activity、TextView、Log 与项目结构");
        LabUi.addHeading(this, container, "运行环境");
        TextView details = LabUi.addText(this, container,
                "渠道：" + BuildConfig.LAB_ID
                        + "\n版本：" + BuildConfig.VERSION_NAME
                        + "\n设备：" + Build.MANUFACTURER + " " + Build.MODEL
                        + "\nAndroid：" + Build.VERSION.RELEASE
                        + "（API " + Build.VERSION.SDK_INT + "）"
                        + "\n应用包名：" + getPackageName());
        details.setTextIsSelectable(true);
        LabUi.addHeading(this, container, "第一个 TextView");
        LabUi.addText(this, container, "HelloWorld 已成功运行。点击下方按钮后，可在 Logcat 中搜索 " + TAG + "。");
        LabUi.addButton(this, container, "写入 Logcat 日志").setOnClickListener(view -> {
            Log.d(TAG, "按钮已点击，onCreate 页面运行正常");
            Toast.makeText(this, "日志已写入 Logcat", Toast.LENGTH_SHORT).show();
        });
        Log.i(TAG, "HelloStartActivity onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }
}


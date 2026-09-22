package com.example.sensecheck.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sensecheck.R;
import com.example.sensecheck.data.AttendanceDbHelper;
import com.example.sensecheck.data.CheckinRecord;
import com.example.sensecheck.util.ExportManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class HistoryActivity extends Activity {
    private static final int REQUEST_EXPORT = 5201;

    private AttendanceDbHelper database;
    private List<CheckinRecord> records;
    private ListView listView;
    private TextView statsView;
    private String pendingContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        database = new AttendanceDbHelper(this);
        listView = findViewById(R.id.listHistory);
        statsView = findViewById(R.id.tvHistoryStats);
        listView.setEmptyView(findViewById(R.id.tvEmpty));

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, CheckinDetailActivity.class);
            intent.putExtra(CheckinDetailActivity.EXTRA_RECORD_ID, id);
            startActivity(intent);
        });
        findViewById(R.id.btnExportCsv).setOnClickListener(view -> export("csv"));
        findViewById(R.id.btnExportJson).setOnClickListener(view -> export("json"));
        findViewById(R.id.btnShareCsv).setOnClickListener(view -> shareCsv());
        findViewById(R.id.btnClearHistory).setOnClickListener(view -> confirmClear());
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        records = database.getAll();
        listView.setAdapter(new CheckinRecordAdapter(this, records));
        int total = records.size();
        int success = database.getSuccessCount();
        float rate = total == 0 ? 0f : success * 100f / total;
        statsView.setText(String.format(
                Locale.CHINA,
                "共 %d 条 · 成功 %d 条 · 成功率 %.1f%%",
                total,
                success,
                rate));
    }

    private void export(String format) {
        if (records.isEmpty()) {
            Toast.makeText(this, "没有可导出的记录", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean csv = "csv".equals(format);
        pendingContent = csv ? ExportManager.toCsv(records) : ExportManager.toJson(records);
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(csv ? "text/csv" : "application/json");
        String date = new SimpleDateFormat("yyyyMMdd-HHmm", Locale.CHINA).format(new Date());
        intent.putExtra(Intent.EXTRA_TITLE, "签到记录-" + date + "." + format);
        startActivityForResult(intent, REQUEST_EXPORT);
    }

    private void shareCsv() {
        if (records.isEmpty()) {
            Toast.makeText(this, "没有可分享的记录", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_SUBJECT, "无感签到记录");
        intent.putExtra(Intent.EXTRA_TEXT, ExportManager.toCsv(records));
        startActivity(Intent.createChooser(intent, "分享签到记录"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_EXPORT || resultCode != RESULT_OK || data == null) {
            return;
        }
        Uri uri = data.getData();
        if (uri == null || pendingContent == null) {
            return;
        }
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream == null) {
                throw new IOException("无法打开目标文件");
            }
            outputStream.write(pendingContent.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            Toast.makeText(this, "签到记录已导出", Toast.LENGTH_SHORT).show();
        } catch (IOException exception) {
            Toast.makeText(this, "导出失败：" + exception.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            pendingContent = null;
        }
    }

    private void confirmClear() {
        if (records.isEmpty()) {
            Toast.makeText(this, "当前没有记录", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("清空签到记录")
                .setMessage("此操作无法撤销，确定继续吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("清空", (dialog, which) -> {
                    database.clearAll();
                    reload();
                    Toast.makeText(this, "签到记录已清空", Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}

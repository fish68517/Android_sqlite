package com.example.sensecheck.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.sensecheck.R;
import com.example.sensecheck.data.AttendanceDbHelper;
import com.example.sensecheck.data.CheckinRecord;
import com.example.sensecheck.util.TimeUtils;

import java.util.Locale;

public final class CheckinDetailActivity extends Activity {
    public static final String EXTRA_RECORD_ID = "record_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_detail);
        TextView resultView = findViewById(R.id.tvDetailResult);
        TextView contentView = findViewById(R.id.tvDetailContent);

        long recordId = getIntent().getLongExtra(EXTRA_RECORD_ID, -1L);
        CheckinRecord record = new AttendanceDbHelper(this).getById(recordId);
        if (record == null) {
            resultView.setText("记录不存在");
            contentView.setText("该签到记录可能已被清除。");
        } else {
            resultView.setText(record.getResult());
            resultView.setTextColor(resultColor(record.getResult()));
            contentView.setText(buildDetail(record));
        }
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());
    }

    private String buildDetail(CheckinRecord record) {
        String latitude = Double.isNaN(record.getLatitude())
                ? "未知"
                : String.format(Locale.CHINA, "%.6f", record.getLatitude());
        String longitude = Double.isNaN(record.getLongitude())
                ? "未知"
                : String.format(Locale.CHINA, "%.6f", record.getLongitude());
        String accuracy = Float.isNaN(record.getAccuracy())
                ? "未知"
                : String.format(Locale.CHINA, "%.1f 米", record.getAccuracy());
        String distance = Float.isNaN(record.getDistanceMeters())
                ? "未知"
                : String.format(Locale.CHINA, "%.1f 米", record.getDistanceMeters());

        return "课程：" + record.getCourseName()
                + "\n采集时间：" + TimeUtils.formatDateTime(record.getSampleTime())
                + "\n判断原因：" + record.getReason()
                + "\n\n纬度：" + latitude
                + "\n经度：" + longitude
                + "\n定位精度：" + accuracy
                + "\n距教室：" + distance
                + "\n\nWi-Fi 数量：" + record.getWifiCount()
                + "\n蓝牙数量：" + record.getBluetoothCount()
                + "\nWi-Fi Direct 数量：" + record.getWifiDirectCount();
    }

    private int resultColor(String result) {
        if (CheckinRecord.RESULT_SUCCESS.equals(result)) {
            return getColor(R.color.success);
        }
        if (CheckinRecord.RESULT_FAILED.equals(result)) {
            return getColor(R.color.danger);
        }
        return getColor(R.color.warning);
    }
}


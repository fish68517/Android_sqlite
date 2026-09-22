package com.example.sensecheck.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sensecheck.R;
import com.example.sensecheck.data.CheckinRecord;
import com.example.sensecheck.util.TimeUtils;

import java.util.List;
import java.util.Locale;

public final class CheckinRecordAdapter extends BaseAdapter {
    private final Context context;
    private final List<CheckinRecord> records;

    public CheckinRecordAdapter(Context context, List<CheckinRecord> records) {
        this.context = context;
        this.records = records;
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public CheckinRecord getItem(int position) {
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_checkin_record, parent, false);
            holder = new ViewHolder();
            holder.course = convertView.findViewById(R.id.tvItemCourse);
            holder.result = convertView.findViewById(R.id.tvItemResult);
            holder.time = convertView.findViewById(R.id.tvItemTime);
            holder.summary = convertView.findViewById(R.id.tvItemSummary);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CheckinRecord record = getItem(position);
        holder.course.setText(record.getCourseName());
        holder.result.setText(record.getResult());
        holder.result.setTextColor(resultColor(record.getResult()));
        holder.time.setText(TimeUtils.formatDateTime(record.getSampleTime()));
        String distance = Float.isNaN(record.getDistanceMeters())
                ? "未知"
                : String.format(Locale.CHINA, "%.0f 米", record.getDistanceMeters());
        holder.summary.setText("距教室 " + distance
                + " · Wi-Fi " + record.getWifiCount()
                + " · 蓝牙 " + record.getBluetoothCount()
                + " · P2P " + record.getWifiDirectCount());
        return convertView;
    }

    private int resultColor(String result) {
        if (CheckinRecord.RESULT_SUCCESS.equals(result)) {
            return context.getColor(R.color.success);
        }
        if (CheckinRecord.RESULT_FAILED.equals(result)) {
            return context.getColor(R.color.danger);
        }
        return context.getColor(R.color.warning);
    }

    private static final class ViewHolder {
        TextView course;
        TextView result;
        TextView time;
        TextView summary;
    }
}


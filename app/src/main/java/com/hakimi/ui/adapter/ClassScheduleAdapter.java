package com.hakimi.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hakimi.R;
import com.hakimi.model.ClassSchedule;

import java.util.ArrayList;
import java.util.List;

public class ClassScheduleAdapter extends RecyclerView.Adapter<ClassScheduleAdapter.ViewHolder> {

    private final List<ClassSchedule> items = new ArrayList<>();

    public void setItems(List<ClassSchedule> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassSchedule item = items.get(position);
        holder.tvName.setText(item.getCourseName());
        holder.tvTime.setText(item.getStartTime() + " - " + item.getEndTime());
        holder.tvWeekday.setText(weekdayLabel(item.getWeekday()));
        String location = item.getLocation();
        holder.tvLocation.setText(location == null || location.trim().isEmpty()
                ? "\u4f4d\u7f6e\uff1a\u672a\u586b\u5199"
                : "\u4f4d\u7f6e\uff1a" + location);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String weekdayLabel(int day) {
        switch (day) {
            case 1:
                return "\u5468\u4e00";
            case 2:
                return "\u5468\u4e8c";
            case 3:
                return "\u5468\u4e09";
            case 4:
                return "\u5468\u56db";
            case 5:
                return "\u5468\u4e94";
            case 6:
                return "\u5468\u516d";
            case 7:
                return "\u5468\u65e5";
            default:
                return "";
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvTime;
        private final TextView tvWeekday;
        private final TextView tvLocation;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_schedule_name);
            tvTime = itemView.findViewById(R.id.tv_schedule_time);
            tvWeekday = itemView.findViewById(R.id.tv_schedule_weekday);
            tvLocation = itemView.findViewById(R.id.tv_schedule_location);
        }
    }
}

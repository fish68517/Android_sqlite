package com.myapplication.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.myapplication.R;
import com.myapplication.app.model.MediaModel;
import java.util.ArrayList;

public class MediaAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MediaModel> list;

    // 记录当前播放的位置，-1 表示没有播放
    private int currentPlayingPosition = -1;
    // 记录播放状态：true=正在播放(显示暂停图标), false=暂停/停止(显示播放图标)
    private boolean isPlaying = false;

    // 回调接口，处理按钮点击
    private OnPlayClickListener listener;

    public interface OnPlayClickListener {
        void onPlayClick(int position);
    }

    public MediaAdapter(Context context, ArrayList<MediaModel> list, OnPlayClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    // 更新播放状态的方法
    public void setPlayingState(int position, boolean isPlaying) {
        this.currentPlayingPosition = position;
        this.isPlaying = isPlaying;
        notifyDataSetChanged(); // 刷新列表UI
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int position) { return list.get(position); }

    @Override
    public long getItemId(int position) { return list.get(position).id; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_media_list, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tv_media_name);
            holder.tvAuthor = convertView.findViewById(R.id.tv_media_author);
            holder.ivPlay = convertView.findViewById(R.id.iv_play_pause);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MediaModel item = list.get(position);
        holder.tvName.setText(item.name);
        holder.tvAuthor.setText(item.author);

        // --- 核心逻辑：根据状态设置图标 ---
        if (position == currentPlayingPosition && isPlaying) {
            // 如果是当前选中的行，且状态是播放，显示暂停图标
            holder.ivPlay.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            // 其他情况，显示播放图标
            holder.ivPlay.setImageResource(android.R.drawable.ic_media_play);
        }

        // 按钮点击事件
        holder.ivPlay.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayClick(position);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tvName, tvAuthor;
        ImageView ivPlay;
    }
}
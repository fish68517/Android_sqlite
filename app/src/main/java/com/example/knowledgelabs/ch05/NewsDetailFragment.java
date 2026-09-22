package com.example.knowledgelabs.ch05;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NewsDetailFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private static final String[] CONTENT = {
            "productFlavors 可以在同一套源码中定义不同 applicationId、应用名称和入口功能。",
            "Fragment 会经历 attach、create、createView、start、resume 等状态，并随宿主界面变化。",
            "本实验通过 layout-sw600dp 限定符：手机显示单栏，平板同时显示新闻列表与正文。",
            "列表 Fragment 通过回调接口通知 Activity，再由 Activity 更新详情 Fragment。"
    };

    public static NewsDetailFragment newInstance(int position) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(android.view.LayoutInflater inflater, ViewGroup container, Bundle state) {
        int position = getArguments() == null ? 0 : getArguments().getInt(ARG_POSITION, 0);
        TextView textView = new TextView(inflater.getContext());
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);
        textView.setTextSize(18);
        textView.setText(NewsListFragment.TITLES[position] + "\n\n" + CONTENT[position]);
        return textView;
    }
}

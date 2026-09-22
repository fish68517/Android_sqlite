package com.example.knowledgelabs.ch05;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NewsListFragment extends Fragment {
    public static final String[] TITLES = {
            "Android 单工程多渠道实践",
            "Fragment 的生命周期",
            "手机与平板的自适应布局",
            "Activity 与 Fragment 通信"
    };

    public interface OnNewsSelectedListener {
        void onNewsSelected(int position);
    }

    private OnNewsSelectedListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnNewsSelectedListener) context;
    }

    @Override
    public View onCreateView(android.view.LayoutInflater inflater, ViewGroup container, Bundle state) {
        Context context = inflater.getContext();
        ListView listView = new ListView(context);
        listView.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, TITLES));
        listView.setOnItemClickListener((parent, view, position, id) -> listener.onNewsSelected(position));
        return listView;
    }
}

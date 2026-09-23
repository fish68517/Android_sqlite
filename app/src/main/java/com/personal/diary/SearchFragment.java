package com.personal.diary;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.personal.diary.db.DiaryDbHelper;
import com.personal.diary.model.SearchItem;
import com.personal.diary.ui.RecordAdapter;
import com.personal.diary.ui.RecordItem;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements RecordAdapter.Listener {
    private DiaryDbHelper db;
    private SessionManager session;
    private EditText searchEdit;
    private TextView emptyText;
    private RecordAdapter adapter;

    public SearchFragment() {
        super(R.layout.fragment_search);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DiaryDbHelper(requireContext());
        session = new SessionManager(requireContext());
        searchEdit = view.findViewById(R.id.searchEdit);
        emptyText = view.findViewById(R.id.emptyText);
        adapter = new RecordAdapter(this);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        view.findViewById(R.id.searchButton).setOnClickListener(v -> search());
    }

    private void search() {
        String keyword = searchEdit.getText().toString().trim();
        if (keyword.isEmpty()) {
            emptyText.setText("请输入关键词搜索");
            adapter.submit(new ArrayList<>());
            return;
        }
        List<RecordItem> items = new ArrayList<>();
        for (SearchItem searchItem : db.search(session.getUserId(), keyword)) {
            items.add(new RecordItem(searchItem.id, 0, searchItem.type, searchItem.title, searchItem.body, searchItem.meta));
        }
        adapter.submit(items);
        emptyText.setText(items.isEmpty() ? "没有找到相关内容" : "共找到 " + items.size() + " 条结果");
        emptyText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(RecordItem item) {
        Toast.makeText(requireContext(), item.type + "：" + item.title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrimary(RecordItem item) {
    }

    @Override
    public void onSecondary(RecordItem item) {
    }

    @Override
    public void onEdit(RecordItem item) {
    }

    @Override
    public void onDelete(RecordItem item) {
    }
}

package com.personal.diary;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.personal.diary.db.DiaryDbHelper;
import com.personal.diary.ui.RecordAdapter;
import com.personal.diary.ui.RecordItem;

import java.util.List;

public abstract class BaseRecordsFragment extends Fragment implements RecordAdapter.Listener {
    protected DiaryDbHelper db;
    protected SessionManager session;
    protected RecordAdapter adapter;
    private TextView emptyText;

    public BaseRecordsFragment() {
        super(R.layout.fragment_records);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DiaryDbHelper(requireContext());
        session = new SessionManager(requireContext());
        emptyText = view.findViewById(R.id.emptyText);
        adapter = new RecordAdapter(this);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        FloatingActionButton addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> onAddClick());
        refresh();
    }

    protected void refresh() {
        List<RecordItem> items = loadItems();
        adapter.submit(items);
        emptyText.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        emptyText.setText(emptyMessage());
    }

    protected void toast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected abstract List<RecordItem> loadItems();

    protected abstract String emptyMessage();

    protected abstract void onAddClick();

    @Override
    public void onItemClick(RecordItem item) {
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

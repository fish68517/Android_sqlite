package com.archive.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.MyApplication;
import com.archive.app.adapter.NoteAdapter;
import com.archive.app.model.Note;
import com.archive.app.mvp.SearchContract;
import com.archive.app.mvp.SearchPresenter;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchContract.View, NoteAdapter.OnNoteClickListener {

    private SearchContract.Presenter presenter;
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private TextView emptyView;
    private ProgressBar loadingSpinner;
    private SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presenter = new SearchPresenter(this);
        presenter.attachView(this);

        recyclerView = findViewById(R.id.search_results_recycler_view);
        emptyView = findViewById(R.id.empty_search_view);
        loadingSpinner = findViewById(R.id.search_loading_spinner);
        searchView = findViewById(R.id.search_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter(this, new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        setupSearchView();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.searchNotes(getUserId(), query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    adapter.setNotes(new ArrayList<>());
                    emptyView.setVisibility(View.GONE);
                }
                // Optional: Implement real-time search here
                // presenter.searchNotes(getUserId(), newText);
                return true;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void showSearchResults(List<Note> notes) {
        adapter.setNotes(notes);
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyResults() {
        adapter.setNotes(new ArrayList<>());
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        loadingSpinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loadingSpinner.setVisibility(View.GONE);
    }

    @Override
    public long getUserId() {
        return MyApplication.curUser.getId();
    }

    @Override
    public void onNoteClick(Note note) {
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra("note_id", note.getId());
        startActivity(intent);
    }

    @Override
    public void onNoteLongClick(Note note) {
        // No action on long click in search results
    }
} 
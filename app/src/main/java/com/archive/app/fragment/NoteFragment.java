package com.archive.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.MyApplication;
import com.archive.app.activity.NoteEditorActivity;
import com.archive.app.activity.SearchActivity;
import com.archive.app.adapter.NoteAdapter;
import com.archive.app.model.Note;
import com.archive.app.mvp.NoteContract;
import com.archive.app.mvp.NotePresenter;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NoteFragment extends Fragment implements NoteContract.View, NoteAdapter.OnNoteClickListener {

    private NoteContract.Presenter presenter;
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private TextView emptyView;
    private ProgressBar loadingSpinner;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new NotePresenter(getContext());
        presenter.attachView(this);

        recyclerView = view.findViewById(R.id.notes_recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        loadingSpinner = view.findViewById(R.id.loading_spinner);
        fab = view.findViewById(R.id.fab_add_note);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NoteAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(v -> {
            // Intent to start NoteEditorActivity for a new note
             Intent intent = new Intent(getActivity(), NoteEditorActivity.class);
             startActivity(intent);
        });
        
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.loadNotes(getUserId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    @Override
    public void showNotes(List<Note> notes) {
        adapter.setNotes(notes);
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyView() {
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
        // Intent to start NoteEditorActivity for editing an existing note
         Intent intent = new Intent(getActivity(), NoteEditorActivity.class);
         intent.putExtra("note_id", note.getId());
         startActivity(intent);
    }

    @Override
    public void onNoteLongClick(Note note) {
        new AlertDialog.Builder(requireContext())
                .setTitle("删除笔记")
                .setMessage("确定要删除这篇笔记吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    System.out.println("delete note：" + note.getId());
                    presenter.deleteNote(note.getId());
                    Toast.makeText(getContext(), "笔记已移入回收站", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
} 
package com.archive.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.archive.app.model.Note;
import com.example.myapplication.R;

import java.util.List;

public class RecycleBinAdapter extends RecyclerView.Adapter<RecycleBinAdapter.DeletedNoteViewHolder> {

    private List<Note> deletedNoteList;
    private OnDeletedNoteActionsListener listener;

    public interface OnDeletedNoteActionsListener {
        void onRestore(Note note);
        void onPermanentDelete(Note note);
    }

    public RecycleBinAdapter(List<Note> deletedNoteList, OnDeletedNoteActionsListener listener) {
        this.deletedNoteList = deletedNoteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeletedNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deleted_note, parent, false);
        return new DeletedNoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeletedNoteViewHolder holder, int position) {
        Note note = deletedNoteList.get(position);
        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());
        holder.restoreButton.setOnClickListener(v -> listener.onRestore(note));
        holder.deleteButton.setOnClickListener(v -> listener.onPermanentDelete(note));
    }

    @Override
    public int getItemCount() {
        return deletedNoteList.size();
    }

    public void setDeletedNotes(List<Note> notes) {
        this.deletedNoteList = notes;
        notifyDataSetChanged();
    }

    static class DeletedNoteViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;
        Button restoreButton;
        Button deleteButton;

        public DeletedNoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.deleted_note_title);
            content = itemView.findViewById(R.id.deleted_note_content);
            restoreButton = itemView.findViewById(R.id.btn_restore);
            deleteButton = itemView.findViewById(R.id.btn_permanent_delete);
        }
    }
} 
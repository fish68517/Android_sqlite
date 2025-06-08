package com.archive.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.archive.app.model.Note;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList;
    private Context context;
    private OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
        void onNoteLongClick(Note note);
    }

    public NoteAdapter(Context context, List<Note> noteList, OnNoteClickListener listener) {
        this.context = context;
        this.noteList = noteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());

        if (note.getImage() != null && note.getImage().length > 0) {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(context).load(note.getImage()).into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onNoteClick(note));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onNoteLongClick(note);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void setNotes(List<Note> notes) {
        this.noteList = notes;
        notifyDataSetChanged();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;
        ImageView image;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.note_title);
            content = itemView.findViewById(R.id.note_content);
            image = itemView.findViewById(R.id.note_image);
        }
    }
} 
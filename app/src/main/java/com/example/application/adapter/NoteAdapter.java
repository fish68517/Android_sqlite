package com.example.application.adapter;// =================================================================================
// 文件路径: app/src/main/java/com/example/application/ui/NoteAdapter.java
// =================================================================================


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.databinding.ItemNoteBinding;
import com.example.application.model.Note;


public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteViewHolder> {

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Note note, View cardView);
    }

    public NoteAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            // 通常使用唯一ID来判断是否是同一个项目
            return oldItem.getId() == newItem.getId();
        }
        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            // 判断项目的内容是否相同
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getContent().equals(newItem.getContent());
        }
    };

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteBinding binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note currentNote = getItem(position);
        holder.bind(currentNote, listener);
    }


    // 使用 ListAdapter 内置的 getItem() 方法，而不是自定义的 getNoteAt()
    // 这样可以确保你总是从当前已经过 Diff 计算的列表中获取数据
    public Note getNoteAt(int position){
        return getItem(position);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private final ItemNoteBinding binding;

        public NoteViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Note note, final OnItemClickListener listener) {
            binding.noteTitle.setText(note.getTitle());
            binding.noteContentPreview.setText(note.getContent());
            itemView.setOnClickListener(v -> listener.onItemClick(note, binding.noteCard));
        }
    }
}
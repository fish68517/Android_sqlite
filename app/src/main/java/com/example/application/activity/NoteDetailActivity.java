package com.example.application.activity;// =================================================================================
// 文件路径: app/src/main/java/com/example/geeknotes/ui/NoteDetailActivity.java
// =================================================================================


import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.databinding.ActivityNoteDetailBinding;
import com.example.application.model.Note;
import com.example.application.viewmodel.NoteViewModel;


public class NoteDetailActivity extends AppCompatActivity {
    private ActivityNoteDetailBinding binding;
    private NoteViewModel noteViewModel;
    private Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        currentNote = (Note) getIntent().getSerializableExtra("note");

        if (currentNote != null) {
            binding.etNoteTitle.setText(currentNote.getTitle());
            binding.etNoteContent.setText(currentNote.getContent());
        }

        setupToolbar();
        setupRichTextButtons();
    }

    private void setupToolbar(){
        binding.detailTopAppBar.setNavigationOnClickListener(v -> saveAndExit());
    }

    private void setupRichTextButtons() {
        // 任务: 富文本编辑器 - 级别 2 (简化实现)
        // 描述: 为选中文本添加或移除粗体/斜体样式。
        binding.btnBold.setOnClickListener(v -> toggleStyle(Typeface.BOLD));
        binding.btnItalic.setOnClickListener(v -> toggleStyle(Typeface.ITALIC));
    }

    private void toggleStyle(int style) {
        int start = binding.etNoteContent.getSelectionStart();
        int end = binding.etNoteContent.getSelectionEnd();
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder(binding.etNoteContent.getText());
        StyleSpan[] spans = ssb.getSpans(start, end, StyleSpan.class);
        boolean styleExists = false;
        for (StyleSpan span : spans) {
            if (span.getStyle() == style) {
                ssb.removeSpan(span);
                styleExists = true;
            }
        }

        if (!styleExists) {
            ssb.setSpan(new StyleSpan(style), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        binding.etNoteContent.setText(ssb);
        binding.etNoteContent.setSelection(start, end);
    }

    private void saveAndExit() {
        String title = binding.etNoteTitle.getText().toString();
        String content = binding.etNoteContent.getText().toString();

        if (currentNote == null) { // New note
            if (!title.isEmpty() || !content.isEmpty()) {
                noteViewModel.insert(new Note(title, content));
            }
        } else { // Existing note
            // Here you would implement update logic
        }
        supportFinishAfterTransition(); // Smoothly transition back
    }

    @Override
    public void onBackPressed() {
        saveAndExit();
        super.onBackPressed();
    }
}
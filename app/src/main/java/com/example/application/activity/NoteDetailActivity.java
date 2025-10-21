package com.example.application.activity;// =================================================================================
// 文件路径: app/src/main/java/com/example/application/ui/NoteDetailActivity.java
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
import android.text.Editable;


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

        // 如果没有选择任何文本，则不执行任何操作
        if (start == end) {
            return;
        }

        // 1. 直接获取 EditText 的 Editable 对象，而不是创建一个新的 SpannableStringBuilder
        Editable editable = binding.etNoteContent.getText();

        // 2. 在 editable 对象上直接操作
        StyleSpan[] spans = editable.getSpans(start, end, StyleSpan.class);
        boolean styleExists = false;
        for (StyleSpan span : spans) {
            if (span.getStyle() == style) {
                editable.removeSpan(span);
                styleExists = true;
            }
        }

        if (!styleExists) {
            editable.setSpan(new StyleSpan(style), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // 3. 【关键】删除下面这行代码！
        // binding.etNoteContent.setText(editable); // 不再需要，EditText 会自动刷新

        // 4. 恢复光标选择是好的做法，但通常在直接修改 Editable 时也不是必需的，
        //    因为选择状态通常不会丢失。但保留也无妨。
        binding.etNoteContent.setSelection(start, end);
    }

    private void saveAndExit() {
        String title = binding.etNoteTitle.getText().toString();
        // 注意：这里需要获取Spannable文本，但保存到数据库时通常存为HTML或纯文本
        String content = binding.etNoteContent.getText().toString();

        if (currentNote == null) { // 新笔记
            if (!title.isEmpty() || !content.isEmpty()) {
                noteViewModel.insert(new Note(title, content));
            }
        } else { // 已存在的笔记
            currentNote.setTitle(title);
            currentNote.setContent(content);
            noteViewModel.update(currentNote); // <-- 添加更新逻辑
        }
        supportFinishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        saveAndExit();
        super.onBackPressed();
    }
}
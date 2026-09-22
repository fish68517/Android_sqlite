package com.example.knowledgelabs.ch03;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.knowledgelabs.R;

public final class NoteDetailActivity extends Activity {
    public static final String EXTRA_TITLE = "note_title";
    public static final String EXTRA_CONTENT = "note_content";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String content = getIntent().getStringExtra(EXTRA_CONTENT);
        ((TextView) findViewById(R.id.tvNoteDetailTitle))
                .setText(title == null || title.isEmpty() ? "未命名便签" : title);
        ((TextView) findViewById(R.id.tvNoteDetailContent))
                .setText(content == null || content.isEmpty() ? "暂无正文" : content);
        findViewById(R.id.btnReturnResult).setOnClickListener(view -> {
            setResult(RESULT_OK, new Intent().putExtra("viewed", true));
            finish();
        });
    }
}


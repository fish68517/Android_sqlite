package com.example.knowledgelabs.ch03;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.knowledgelabs.R;

public final class ActivityNotebookActivity extends Activity {
    private static final int REQUEST_PREVIEW = 3001;
    private static final String TAG = "Ch03Notebook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebook);
        Log.d(TAG, "onCreate");

        EditText titleView = findViewById(R.id.etNoteTitle);
        EditText contentView = findViewById(R.id.etNoteContent);
        findViewById(R.id.btnPreviewNote).setOnClickListener(view -> {
            Intent intent = new Intent(this, NoteDetailActivity.class);
            intent.putExtra(NoteDetailActivity.EXTRA_TITLE, titleView.getText().toString().trim());
            intent.putExtra(NoteDetailActivity.EXTRA_CONTENT, contentView.getText().toString().trim());
            startActivityForResult(intent, REQUEST_PREVIEW);
        });
        findViewById(R.id.btnOpenBrowser).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com"));
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PREVIEW && resultCode == RESULT_OK) {
            Toast.makeText(this, "已从第二个 Activity 返回", Toast.LENGTH_SHORT).show();
        }
    }

    @Override protected void onStart() { super.onStart(); Log.d(TAG, "onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d(TAG, "onResume"); }
    @Override protected void onPause() { Log.d(TAG, "onPause"); super.onPause(); }
    @Override protected void onStop() { Log.d(TAG, "onStop"); super.onStop(); }
    @Override protected void onDestroy() { Log.d(TAG, "onDestroy"); super.onDestroy(); }
}


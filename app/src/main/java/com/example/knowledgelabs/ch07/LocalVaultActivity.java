package com.example.knowledgelabs.ch07;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.knowledgelabs.R;

import java.util.ArrayList;
import java.util.List;

public class LocalVaultActivity extends Activity {
    private final List<VaultDbHelper.Memo> memos = new ArrayList<>();
    private EditText memoInput;
    private ArrayAdapter<VaultDbHelper.Memo> adapter;
    private VaultDbHelper dbHelper;
    private long selectedId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_vault);
        EditText userInput = findViewById(R.id.etVaultUser);
        EditText passwordInput = findViewById(R.id.etVaultPassword);
        CheckBox remember = findViewById(R.id.cbRememberPassword);
        memoInput = findViewById(R.id.etMemo);
        ListView listView = findViewById(R.id.listMemos);
        dbHelper = new VaultDbHelper(this);

        SharedPreferences preferences = getSharedPreferences("local_account", MODE_PRIVATE);
        userInput.setText(preferences.getString("username", "student"));
        remember.setChecked(preferences.getBoolean("remember", false));
        if (remember.isChecked()) {
            passwordInput.setText(preferences.getString("password", ""));
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, memos);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            VaultDbHelper.Memo memo = memos.get(position);
            selectedId = memo.id;
            memoInput.setText(memo.content);
            memoInput.setSelection(memo.content.length());
        });
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            VaultDbHelper.Memo memo = memos.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("删除备忘录")
                    .setMessage(memo.content)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("删除", (dialog, which) -> {
                        dbHelper.delete(memo.id);
                        if (selectedId == memo.id) {
                            clearSelection();
                        }
                        refreshMemos();
                    }).show();
            return true;
        });

        findViewById(R.id.btnAddMemo).setOnClickListener(v -> {
            saveAccount(preferences, userInput, passwordInput, remember);
            String content = memoInput.getText().toString().trim();
            if (!requireMemo(content)) return;
            dbHelper.insert(content);
            clearSelection();
            refreshMemos();
            Toast.makeText(this, "备忘录已写入 SQLite", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btnUpdateMemo).setOnClickListener(v -> {
            String content = memoInput.getText().toString().trim();
            if (selectedId < 0) {
                Toast.makeText(this, "请先点击选择一条备忘录", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!requireMemo(content)) return;
            dbHelper.update(selectedId, content);
            clearSelection();
            refreshMemos();
        });
        refreshMemos();
    }

    private void saveAccount(SharedPreferences preferences, EditText user, EditText password, CheckBox remember) {
        SharedPreferences.Editor editor = preferences.edit()
                .putString("username", user.getText().toString().trim())
                .putBoolean("remember", remember.isChecked());
        editor.putString("password", remember.isChecked() ? password.getText().toString() : "").apply();
    }

    private boolean requireMemo(String content) {
        if (TextUtils.isEmpty(content)) {
            memoInput.setError("备忘录不能为空");
            return false;
        }
        return true;
    }

    private void clearSelection() {
        selectedId = -1;
        memoInput.setText("");
    }

    private void refreshMemos() {
        memos.clear();
        memos.addAll(dbHelper.queryAll());
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}

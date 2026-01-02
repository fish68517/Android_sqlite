package com.myapplication.app.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.myapplication.app.db.StudentDbHelper;

public class AddHomeworkActivity extends AppCompatActivity {

    private EditText etContent, etDeadline;
    private Button btnSave;
    private StudentDbHelper dbHelper;

    // 当前正在编辑的 ID，-1 表示新增模式
    private int currentHwId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_homework);

        dbHelper = new StudentDbHelper(this);
        etContent = findViewById(R.id.et_hw_content);
        etDeadline = findViewById(R.id.et_hw_deadline);
        btnSave = findViewById(R.id.btn_save_hw);

        // 获取传递过来的 ID
        currentHwId = getIntent().getIntExtra("hw_id", -1);

        // 如果 ID 不为 -1，说明是编辑模式，需要先加载旧数据
        if (currentHwId != -1) {
            initData(currentHwId);
            btnSave.setText("保存修改");
        }

        btnSave.setOnClickListener(v -> saveHomework());
    }

    // 编辑模式：从数据库查询旧数据并显示在输入框中
    private void initData(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(StudentDbHelper.TABLE_HOMEWORK, null,
                StudentDbHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            String content = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_HW_CONTENT));
            String deadline = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_HW_DEADLINE));

            etContent.setText(content);
            etDeadline.setText(deadline);
        }
        cursor.close();
    }

    private void saveHomework() {
        String content = etContent.getText().toString();
        String deadline = etDeadline.getText().toString();

        if (content.isEmpty()) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StudentDbHelper.COL_HW_CONTENT, content);
        values.put(StudentDbHelper.COL_HW_DEADLINE, deadline);

        if (currentHwId == -1) {
            // --- 新增逻辑 ---
            long newRowId = db.insert(StudentDbHelper.TABLE_HOMEWORK, null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "作业已添加", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            // --- 修改逻辑 ---
            int rows = db.update(StudentDbHelper.TABLE_HOMEWORK, values,
                    StudentDbHelper.COLUMN_ID + "=?",
                    new String[]{String.valueOf(currentHwId)});

            if (rows > 0) {
                Toast.makeText(this, "作业已修改", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
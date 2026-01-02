package com.myapplication.app.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.myapplication.app.db.StudentDbHelper;

public class AddCourseActivity extends AppCompatActivity {

    private EditText etName, etTime, etPlace;
    private Button btnSave;
    private TextView tvTitle; // 页面标题，用于改为“修改课程”
    private StudentDbHelper dbHelper;

    // 当前编辑的课程ID，如果是 -1 表示是添加新课程
    private int currentCourseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        dbHelper = new StudentDbHelper(this);

        // 绑定控件
        etName = findViewById(R.id.et_name);
        etTime = findViewById(R.id.et_time);
        etPlace = findViewById(R.id.et_place);
        btnSave = findViewById(R.id.btn_save);
        // 注意：如果你布局里标题是写死的TextView没有ID，你需要去 layout/activity_add_course.xml 给标题加个id="@+id/tv_title"
        // 如果不想改布局，可以把下面这行和 setTitle 的代码删掉
        // tvTitle = findViewById(R.id.tv_title);

        // 获取传递过来的 ID
        currentCourseId = getIntent().getIntExtra("course_id", -1);

        if (currentCourseId != -1) {
            // ID存在，说明是编辑模式，加载旧数据
            initData(currentCourseId);
            btnSave.setText("保存修改");
            // if (tvTitle != null) tvTitle.setText("修改课程");
        }

        btnSave.setOnClickListener(v -> saveCourse());
    }

    // 编辑模式：回显数据
    private void initData(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(StudentDbHelper.TABLE_COURSE, null,
                StudentDbHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_COURSE_NAME));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_COURSE_TIME));
            String place = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_COURSE_PLACE));

            etName.setText(name);
            etTime.setText(time);
            etPlace.setText(place);
        }
        cursor.close();
    }

    private void saveCourse() {
        String name = etName.getText().toString();
        String time = etTime.getText().toString();
        String place = etPlace.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "请输入课程名称", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StudentDbHelper.COL_COURSE_NAME, name);
        values.put(StudentDbHelper.COL_COURSE_TIME, time);
        values.put(StudentDbHelper.COL_COURSE_PLACE, place);

        if (currentCourseId == -1) {
            // --- 添加模式：插入新数据 ---
            long newRowId = db.insert(StudentDbHelper.TABLE_COURSE, null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            // --- 编辑模式：更新现有数据 ---
            int rows = db.update(StudentDbHelper.TABLE_COURSE, values,
                    StudentDbHelper.COLUMN_ID + "=?",
                    new String[]{String.valueOf(currentCourseId)});

            if (rows > 0) {
                Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
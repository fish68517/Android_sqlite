package com.myapplication.app.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.myapplication.app.AlarmReceiver;
import com.myapplication.app.db.StudentDbHelper;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExamActivity extends AppCompatActivity {

    private EditText etName, etTime;
    private Button btnSave;
    private TextView tvTitle; // 如果布局里有标题TextView
    private StudentDbHelper dbHelper;

    // 当前编辑的 ID，-1 表示添加
    private int currentExamId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);

        dbHelper = new StudentDbHelper(this);
        etName = findViewById(R.id.et_exam_name);
        etTime = findViewById(R.id.et_exam_time);
        btnSave = findViewById(R.id.btn_save_exam);

        // 获取传递的 ID
        currentExamId = getIntent().getIntExtra("exam_id", -1);

        // 如果是编辑模式，初始化数据
        if (currentExamId != -1) {
            initData(currentExamId);
            btnSave.setText("保存修改并设置提醒");
        }

        btnSave.setOnClickListener(v -> saveExam());
    }

    // 回显数据
    private void initData(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(StudentDbHelper.TABLE_EXAM, null,
                StudentDbHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_EXAM_NAME));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(StudentDbHelper.COL_EXAM_TIME));
            etName.setText(name);
            etTime.setText(time);
        }
        cursor.close();
    }

    private void saveExam() {
        String name = etName.getText().toString();
        String timeStr = etTime.getText().toString(); // 格式: yyyy-MM-dd HH:mm

        if (name.isEmpty() || timeStr.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StudentDbHelper.COL_EXAM_NAME, name);
        values.put(StudentDbHelper.COL_EXAM_TIME, timeStr);

        boolean isSuccess;

        if (currentExamId == -1) {
            // --- 添加 ---
            long newId = db.insert(StudentDbHelper.TABLE_EXAM, null, values);
            isSuccess = (newId != -1);
        } else {
            // --- 修改 ---
            int rows = db.update(StudentDbHelper.TABLE_EXAM, values,
                    StudentDbHelper.COLUMN_ID + "=?",
                    new String[]{String.valueOf(currentExamId)});
            isSuccess = (rows > 0);
        }

        if (isSuccess) {
            // 无论是添加还是修改，都重新设置一次提醒
            setReminder(name, timeStr);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setReminder(String examName, String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = sdf.parse(timeStr);
            long triggerTime = date.getTime();

            if (triggerTime < System.currentTimeMillis()) return; // 过去的时间不提醒

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("title", "考试提醒");
            intent.putExtra("msg", "即将进行 " + examName + " 考试，请做好准备！");

            // 使用唯一 ID (如果是修改，简单的做法是覆盖旧提醒或者设置新提醒，这里直接设新提醒)
            int requestCode = (int) System.currentTimeMillis();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );

            if (alarmManager != null) {
                // 允许在低电耗模式下触发
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "时间格式错误 (yyyy-MM-dd HH:mm)", Toast.LENGTH_LONG).show();
        }
    }
}
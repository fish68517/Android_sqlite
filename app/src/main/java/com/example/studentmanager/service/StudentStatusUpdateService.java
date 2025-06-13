package com.example.studentmanager.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.studentmanager.db.StudentDBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StudentStatusUpdateService extends Service {

    public static final String ACTION_STATUS_UPDATED = "com.example.studentmanager.ACTION_STATUS_UPDATED";
    public static final String EXTRA_UPDATED_COUNT = "extra_updated_count";
    private static final String TAG = "StudentStatusUpdateService";

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private StudentDBHelper dbHelper;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // 正常情况下，我们在这里执行耗时任务
            try {
                performStatusUpdate();
            } catch (Exception e) {
                Log.e(TAG, "Error updating student status", e);
            }
            // 任务完成后停止服务
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        dbHelper = new StudentDBHelper(this);
        // 启动一个新线程来处理服务任务，避免阻塞主线程
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // 获取线程的Looper，并将其用于我们的Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service starting...");

        // 对于每个启动请求，都发送一个消息来启动一个任务，并传递startId，
        // 这样当任务完成时我们才知道要停止哪个请求。
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        // 如果服务被系统杀死，我们不希望它自动重启
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // 我们不提供绑定，所以返回null
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service destroyed.");
        // 退出Looper，清理线程
        if (serviceLooper != null) {
            serviceLooper.quit();
        }
        super.onDestroy();
    }

    /**
     * 执行学生状态更新的核心逻辑
     */
    @SuppressLint("Range")
    private void performStatusUpdate() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("students",
                new String[]{"student_id", "admission_date"},
                "status = ?",
                new String[]{"在校"},
                null, null, null);

        int updatedCount = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int studentId = cursor.getInt(cursor.getColumnIndex("student_id"));
                String admissionDateStr = cursor.getString(cursor.getColumnIndex("admission_date"));

                if (shouldGraduate(admissionDateStr)) {
                    ContentValues values = new ContentValues();
                    values.put("status", "毕业");
                    int rowsAffected = db.update("students", values, "student_id = ?", new String[]{String.valueOf(studentId)});
                    if (rowsAffected > 0) {
                        updatedCount++;
                    }
                }
            }
            cursor.close();
        }

        Log.i(TAG, "Status update complete. " + updatedCount + " students graduated.");

        // 发送广播通知UI
        Intent broadcastIntent = new Intent(ACTION_STATUS_UPDATED);
        broadcastIntent.putExtra(EXTRA_UPDATED_COUNT, updatedCount);
        sendBroadcast(broadcastIntent);
    }

    /**
     * 检查学生是否应该毕业（入学满4年）
     */
    private boolean shouldGraduate(String admissionDateStr) {
        if (admissionDateStr == null || admissionDateStr.isEmpty()) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date admissionDate = sdf.parse(admissionDateStr);
            Calendar admissionCal = Calendar.getInstance();
            admissionCal.setTime(admissionDate);

            Calendar currentCal = Calendar.getInstance();
            
            // 计算年份差距
            int yearDiff = currentCal.get(Calendar.YEAR) - admissionCal.get(Calendar.YEAR);
            
            // 如果年份差距大于4，或者等于4但当前月份已超过入学月份，则认为应毕业
            if (yearDiff > 4) {
                return true;
            }
            if (yearDiff == 4 && currentCal.get(Calendar.MONTH) > admissionCal.get(Calendar.MONTH)) {
                return true;
            }
            
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing admission date", e);
            return false;
        }
    }
} 
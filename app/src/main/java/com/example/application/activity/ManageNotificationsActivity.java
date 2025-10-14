package com.example.application.activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.application.DatabaseHelper;
import com.example.application.R;
import com.example.application.model.Notification;
import java.util.ArrayList;
import java.util.List;

public class ManageNotificationsActivity extends AppCompatActivity {

    private ListView lvNotifications;
    private Button btnAddNotification;
    private DatabaseHelper dbHelper;
    private List<Notification> notificationList;
    private ArrayAdapter<Notification> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_notifications);

        dbHelper = new DatabaseHelper(this);
        lvNotifications = findViewById(R.id.lv_manage_notifications);
        btnAddNotification = findViewById(R.id.btn_add_notification);

        btnAddNotification.setOnClickListener(v -> showAddEditDialog(null));

        lvNotifications.setOnItemClickListener((parent, view, position, id) -> {
            Notification selectedNotification = notificationList.get(position);
            showOptionsDialog(selectedNotification);
        });

        loadNotifications();
    }

    private void loadNotifications() {
        notificationList = dbHelper.getAllNotifications();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationList);
        lvNotifications.setAdapter(adapter);
    }

    private void showOptionsDialog(final Notification notification) {
        new AlertDialog.Builder(this)
                .setTitle("选择操作")
                .setItems(new CharSequence[]{"编辑", "删除"}, (dialog, which) -> {
                    if (which == 0) { // 编辑
                        showAddEditDialog(notification);
                    } else { // 删除
                        confirmDelete(notification);
                    }
                })
                .show();
    }

    private void confirmDelete(final Notification notification) {
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除这条通知吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    dbHelper.deleteNotification(notification);
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                    loadNotifications(); // 刷新列表
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showAddEditDialog(final Notification notification) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_notification, null);
        builder.setView(dialogView);

        final EditText etTitle = dialogView.findViewById(R.id.et_notification_title);
        final EditText etContent = dialogView.findViewById(R.id.et_notification_content);
        builder.setTitle(notification == null ? "发布新通知" : "编辑通知");

        if (notification != null) {
            etTitle.setText(notification.getTitle());
            etContent.setText(notification.getContent());
        }

        builder.setPositiveButton(notification == null ? "发布" : "保存", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "标题和内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (notification == null) { // 新增
                Notification newNotification = new Notification();
                newNotification.setTitle(title);
                newNotification.setContent(content);
                dbHelper.addNotification(newNotification);
                Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
            } else { // 编辑
                notification.setTitle(title);
                notification.setContent(content);
                dbHelper.updateNotification(notification);
                Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
            }
            loadNotifications(); // 刷新列表
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
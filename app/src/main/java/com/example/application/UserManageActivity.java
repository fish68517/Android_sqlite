package com.example.application;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.application.DatabaseHelper;
import com.example.application.R;
import com.example.application.model.User;
import java.util.List;
import java.util.stream.Collectors;

public class UserManageActivity extends AppCompatActivity {

    private ListView lvAllUsers;
    private Button btnAddUser;
    private DatabaseHelper dbHelper;
    private List<User> userList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manage);

        dbHelper = new DatabaseHelper(this);
        lvAllUsers = findViewById(R.id.lv_all_users);
        btnAddUser = findViewById(R.id.btn_add_user);

        btnAddUser.setOnClickListener(v -> showAddEditUserDialog(null));

        lvAllUsers.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = userList.get(position);
            showUserOptionsDialog(selectedUser);
        });

        loadUsers();
    }

    private void loadUsers() {
        userList = dbHelper.getAllUsers();
        // 将User对象列表转换为更易读的字符串列表
        List<String> userDisplayList = userList.stream()
                .map(user -> "ID: " + user.getId() + " | " + user.getUsername() + " (" + user.getRole() + ")")
                .collect(Collectors.toList());

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userDisplayList);
        lvAllUsers.setAdapter(adapter);
    }

    private void showUserOptionsDialog(final User user) {
        new AlertDialog.Builder(this)
                .setTitle("操作用户: " + user.getUsername())
                .setItems(new CharSequence[]{"编辑用户信息", "删除用户"}, (dialog, which) -> {
                    if (which == 0) { // 编辑
                        showAddEditUserDialog(user);
                    } else { // 删除
                        confirmDeleteUser(user);
                    }
                })
                .show();
    }

    private void confirmDeleteUser(final User user) {
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("你确定要删除用户 '" + user.getUsername() + "' 吗？此操作不可逆！")
                .setPositiveButton("删除", (dialog, which) -> {
                    dbHelper.deleteUser(user.getId());
                    Toast.makeText(this, "用户已删除", Toast.LENGTH_SHORT).show();
                    loadUsers(); // 刷新列表
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showAddEditUserDialog(final User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_user, null);
        builder.setView(dialogView);

        final EditText etUsername = dialogView.findViewById(R.id.et_dialog_username);
        final EditText etPassword = dialogView.findViewById(R.id.et_dialog_password);
        final Spinner spinnerRole = dialogView.findViewById(R.id.spinner_dialog_role);

        // 设置角色下拉框
        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_roles, android.R.layout.simple_spinner_item);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        builder.setTitle(user == null ? "添加新用户" : "编辑用户");

        if (user != null) { // 编辑模式
            etUsername.setText(user.getUsername());
            etPassword.setText(user.getPassword());
            // 设置 Spinner 的选中项
            for (int i = 0; i < roleAdapter.getCount(); i++) {
                if (roleAdapter.getItem(i).toString().equals(user.getRole())) {
                    spinnerRole.setSelection(i);
                    break;
                }
            }
        }

        builder.setPositiveButton(user == null ? "添加" : "保存", (dialog, which) -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String role = spinnerRole.getSelectedItem().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user == null) { // 新增用户
                // 检查用户名是否已存在
                if (dbHelper.getUserByUsername(username) != null) {
                    Toast.makeText(this, "该用户名已存在", Toast.LENGTH_SHORT).show();
                    return;
                }
                User newUser = new User(0, username, password, role);
                dbHelper.addUser(newUser);
                Toast.makeText(this, "用户添加成功", Toast.LENGTH_SHORT).show();
            } else { // 更新用户
                user.setUsername(username);
                user.setPassword(password);
                user.setRole(role);
                dbHelper.updateUser(user);
                Toast.makeText(this, "用户信息更新成功", Toast.LENGTH_SHORT).show();
            }
            loadUsers(); // 刷新列表
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
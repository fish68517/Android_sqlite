package com.personal.diary;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.personal.diary.db.DiaryDbHelper;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameEdit;
    private EditText emailEdit;
    private EditText passwordEdit;
    private EditText confirmPasswordEdit;
    private RadioGroup roleGroup;
    private DiaryDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DiaryDbHelper(this);
        usernameEdit = findViewById(R.id.usernameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        confirmPasswordEdit = findViewById(R.id.confirmPasswordEdit);
        roleGroup = findViewById(R.id.roleGroup);
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> register());
    }

    private void register() {
        String username = usernameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString();
        String confirm = confirmPasswordEdit.getText().toString();
        String role = roleGroup.getCheckedRadioButtonId() == R.id.adminRoleRadio ? "admin" : "user";
        if (username.length() < 3) {
            toast("用户名至少 3 位");
            return;
        }
        if (!email.contains("@")) {
            toast("请输入正确邮箱");
            return;
        }
        if (password.length() < 6) {
            toast("密码至少 6 位");
            return;
        }
        if (!password.equals(confirm)) {
            toast("两次输入的密码不一致");
            return;
        }
        long id = db.registerUser(username, password, email, role);
        if (id <= 0) {
            toast("注册失败，用户名可能已存在");
            return;
        }
        toast("注册成功，请登录");
        finish();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

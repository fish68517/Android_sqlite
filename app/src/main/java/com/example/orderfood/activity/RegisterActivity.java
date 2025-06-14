package com.example.orderfood.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.R;
import com.example.orderfood.model.Student;
import com.example.orderfood.model.MerchantBean;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button registerButton;

    private EditText emailEditText;

    private RadioGroup registerRadioGroup;
    private RadioButton studentRegisterRadioButton;
    private RadioButton merchantRegisterRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        emailEditText = findViewById(R.id.email);

        registerRadioGroup = findViewById(R.id.register_radio_group);
        studentRegisterRadioButton = findViewById(R.id.radio_student_register);
        merchantRegisterRadioButton = findViewById(R.id.radio_merchant_register);
        registerButton = findViewById(R.id.register_button);

        registerButton = findViewById(R.id.register_button);


        registerButton.setOnClickListener(v -> {
            if (true) {
                handleRegister();
            }
        });
    }

    private void handleRegister() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String email = emailEditText.getText().toString();

        if (studentRegisterRadioButton.isChecked()) {
            registerStudent(username, password, email);
        } else if (merchantRegisterRadioButton.isChecked()) {
            registerMerchant(username, password, email);
        } else {
            Toast.makeText(this, "请选择注册类型", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerStudent(String username, String password, String email) {
        Student student = new Student();
        student.setName(username);
        student.setPassword(password);
        student.setContactInfo(email);
        DBMysqlHelper.getInstance().registerStudent(student, new DBMysqlHelper.DatabaseCallback<Student>() {
            @Override
            public void onSuccess(Student result) {
                Toast.makeText(RegisterActivity.this, "学生注册成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Exception e) {

                Toast.makeText(RegisterActivity.this, "学生注册失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerMerchant(String username, String password, String email) {
        MerchantBean merchant = new MerchantBean();
        merchant.setName(username);
        merchant.setWindowLocation(email);
        merchant.setPassword(password);
        DBMysqlHelper.getInstance().registerMerchant(merchant, new DBMysqlHelper.DatabaseCallback<MerchantBean>() {
            @Override
            public void onSuccess(MerchantBean result) {
                Toast.makeText(RegisterActivity.this, "商家注册成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Exception e) {

                Toast.makeText(RegisterActivity.this, "商家注册失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

}

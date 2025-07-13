package com.example.xiaoshuo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.models.User;
import com.example.xiaoshuo.utils.UserManager;

public class PhoneLoginFragment extends Fragment {

    private EditText etPhone;
    private EditText etVerifyCode;
    private Button btnGetCode;
    private Button btnLogin;
    
    private UserManager userManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_login, container, false);
        
        userManager = UserManager.getInstance(requireContext());
        
        initViews(view);
        setupListeners();
        
        return view;
    }

    private void initViews(View view) {
        etPhone = view.findViewById(R.id.et_phone);
        etVerifyCode = view.findViewById(R.id.et_verify_code);
        btnGetCode = view.findViewById(R.id.btn_get_code);
        btnLogin = view.findViewById(R.id.btn_login);
    }

    private void setupListeners() {
        btnGetCode.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            if (phone.isEmpty() || phone.length() != 11) {
                Toast.makeText(getContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 模拟发送验证码
            Toast.makeText(getContext(), "验证码已发送", Toast.LENGTH_SHORT).show();
            startCountdown();
        });
        
        btnLogin.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String code = etVerifyCode.getText().toString().trim();
            
            if (phone.isEmpty() || phone.length() != 11) {
                Toast.makeText(getContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (code.isEmpty() || code.length() != 6) {
                Toast.makeText(getContext(), "请输入6位验证码", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 模拟登录成功，创建用户对象并保存
            User user = new User();
            user.setId(2);
            user.setUsername("用户" + phone.substring(phone.length() - 4)); // 使用手机号后4位作为用户名
            user.setPhoneNumber(phone);
            user.setAvatarUrl(""); // 默认头像为空，用户可以在个人中心设置
            
            // 保存用户登录状态
            userManager.login(user);
            
            Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });
    }
    
    private void startCountdown() {
        // 模拟倒计时
        btnGetCode.setEnabled(false);
        btnGetCode.setText("60s");
        // 实际应用中应使用CountDownTimer或Handler来实现倒计时
        btnGetCode.postDelayed(() -> {
            btnGetCode.setEnabled(true);
            btnGetCode.setText(R.string.get_verify_code);
        }, 3000); // 演示用，3秒后恢复
    }
} 
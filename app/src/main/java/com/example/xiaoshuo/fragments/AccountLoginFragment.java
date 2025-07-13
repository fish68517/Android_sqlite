package com.example.xiaoshuo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.models.User;
import com.example.xiaoshuo.utils.UserManager;

public class AccountLoginFragment extends Fragment {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvForgetPassword;
    
    private UserManager userManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_login, container, false);
        
        userManager = UserManager.getInstance(requireContext());
        
        initViews(view);
        setupListeners();
        
        return view;
    }

    private void initViews(View view) {
        etUsername = view.findViewById(R.id.et_username);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        tvForgetPassword = view.findViewById(R.id.tv_forget_password);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            
            if (username.isEmpty()) {
                Toast.makeText(getContext(), "请输入用户名/手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (password.isEmpty() || password.length() < 6) {
                Toast.makeText(getContext(), "请输入至少6位密码", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 模拟登录成功，创建用户对象并保存
            User user = new User();
            user.setId(1);
            user.setUsername(username);
            user.setPassword(password);
            user.setAvatarUrl(""); // 默认头像为空，用户可以在个人中心设置
            
            // 保存用户登录状态
            userManager.login(user);
            
            Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });
        
        tvForgetPassword.setOnClickListener(v -> {
            // 跳转到忘记密码页面
            Toast.makeText(getContext(), "忘记密码功能开发中", Toast.LENGTH_SHORT).show();
        });
    }
} 
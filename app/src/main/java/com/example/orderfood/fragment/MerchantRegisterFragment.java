package com.example.orderfood.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.R;
import com.example.orderfood.model.MerchantBean;
import com.google.android.material.textfield.TextInputEditText;

public class MerchantRegisterFragment extends Fragment {
    private TextInputEditText etName;
    private TextInputEditText etPassword;
    private TextInputEditText etLocation;
    private TextInputEditText etBusinessHours;
    private TextInputEditText etCategory;
    private Button btnRegister;
    private DBMysqlHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DBMysqlHelper.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchant_register, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.etName);
        etPassword = view.findViewById(R.id.etPassword);
        etLocation = view.findViewById(R.id.etLocation);
        etBusinessHours = view.findViewById(R.id.etBusinessHours);
        etCategory = view.findViewById(R.id.etCategory);
        btnRegister = view.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerMerchant());
    }

    private void registerMerchant() {
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String businessHours = etBusinessHours.getText().toString().trim();
        String category = etCategory.getText().toString().trim();

        if (name.isEmpty() || password.isEmpty() || location.isEmpty() || 
            businessHours.isEmpty() || category.isEmpty()) {
            Toast.makeText(getContext(), "请填写所有信息", Toast.LENGTH_SHORT).show();
            return;
        }

        MerchantBean merchant = new MerchantBean();
        merchant.setName(name);
        merchant.setPassword(password);
        merchant.setWindowLocation(location);
        merchant.setBusinessHours(businessHours);
        merchant.setCategory(category);

        dbHelper.registerMerchant(merchant, new DBMysqlHelper.DatabaseCallback<MerchantBean>() {
            @Override
            public void onSuccess(MerchantBean result) {
                Toast.makeText(getContext(), "注册成功", Toast.LENGTH_SHORT).show();
                clearInputs();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "注册失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearInputs() {
        etName.setText("");
        etPassword.setText("");
        etLocation.setText("");
        etBusinessHours.setText("");
        etCategory.setText("");
    }
} 
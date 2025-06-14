package com.example.orderfood.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.model.UserAddress;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditAddressActivity extends AppCompatActivity {
    private TextInputLayout nameLayout;
    private TextInputLayout phoneLayout;
    private TextInputLayout addressLayout;
    private TextInputEditText nameEdit;
    private TextInputEditText phoneEdit;
    private TextInputEditText addressEdit;
    private MaterialCheckBox defaultCheck;
    private MaterialButton saveButton;
    
    private DBMysqlHelper dbHelper;
    private int addressId = -1;
    private UserAddress currentAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        
        dbHelper = DBMysqlHelper.getInstance();
        addressId = getIntent().getIntExtra("address_id", -1);
        
        initViews();
        if (addressId != -1) {
            loadAddress();
        }
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(addressId == -1 ? "新增地址" : "编辑地址");

        nameLayout = findViewById(R.id.nameLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        addressLayout = findViewById(R.id.addressLayout);
        nameEdit = findViewById(R.id.nameEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        addressEdit = findViewById(R.id.addressEdit);
        defaultCheck = findViewById(R.id.defaultCheck);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> saveAddress());
    }

    private void loadAddress() {
        dbHelper.getUserAddress(addressId, new DBMysqlHelper.DatabaseCallback<UserAddress>() {
            @Override
            public void onSuccess(UserAddress address) {
                if (address != null) {
                    currentAddress = address;
                    nameEdit.setText(address.getName());
                    phoneEdit.setText(address.getPhone());
                    addressEdit.setText(address.getAddress());
                    defaultCheck.setChecked(address.isDefault());
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(EditAddressActivity.this, "加载地址失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveAddress() {
        String name = nameEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();
        String address = addressEdit.getText().toString().trim();

        // 验证输入
        if (TextUtils.isEmpty(name)) {
            nameLayout.setError("请输入姓名");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            phoneLayout.setError("请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            addressLayout.setError("请输入地址");
            return;
        }

        // 清除错误提示
        nameLayout.setError(null);
        phoneLayout.setError(null);
        addressLayout.setError(null);

        // 创建或更新地址对象
        UserAddress userAddress = currentAddress != null ? currentAddress : new UserAddress();
        userAddress.setUserId(MyApplication.getUserId());
        userAddress.setName(name);
        userAddress.setPhone(phone);
        userAddress.setAddress(address);
        userAddress.setDefault(defaultCheck.isChecked());

        if (addressId == -1) {
            // 新增地址
            dbHelper.addUserAddress(userAddress, new DBMysqlHelper.DatabaseCallback<UserAddress>() {
                @Override
                public void onSuccess(UserAddress result) {
                    Toast.makeText(EditAddressActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(EditAddressActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // 更新地址
            dbHelper.updateUserAddress(userAddress, new DBMysqlHelper.DatabaseCallback<UserAddress>() {
                @Override
                public void onSuccess(UserAddress result) {
                    Toast.makeText(EditAddressActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(EditAddressActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 
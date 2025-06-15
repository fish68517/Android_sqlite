package com.example.orderfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.adapter.AddressAdapter;
import com.example.orderfood.model.UserAddress;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AddressAdapter adapter;
    private FloatingActionButton fabAdd;
    private DBMysqlHelper dbHelper;
    private List<UserAddress> addresses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        dbHelper = DBMysqlHelper.getInstance(this);
        initViews();
        loadAddresses();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("收货地址");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new AddressAdapter(this, addresses);
        adapter.setOnItemClickListener(new AddressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserAddress address) {
                // 返回选中的地址
                Intent intent = new Intent();
                intent.putExtra("address_id", address.getId());
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onEditClick(UserAddress address) {
                // 跳转到编辑页面
                Intent intent = new Intent(AddressActivity.this, EditAddressActivity.class);
                intent.putExtra("address_id", address.getId());
                startActivityForResult(intent, REQUEST_EDIT_ADDRESS);
            }

            @Override
            public void onDeleteClick(UserAddress address) {
                deleteAddress(address);
            }

            @Override
            public void onSetDefaultClick(UserAddress address) {
                setDefaultAddress(address);
            }
        });
        recyclerView.setAdapter(adapter);

        fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditAddressActivity.class);
            startActivityForResult(intent, REQUEST_ADD_ADDRESS);
        });
    }

    private void loadAddresses() {
        dbHelper.getUserAddresses(MyApplication.getUserId(), new DBMysqlHelper.DatabaseCallback<List<UserAddress>>() {
            @Override
            public void onSuccess(List<UserAddress> result) {
                addresses.clear();
                addresses.addAll(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AddressActivity.this, "加载地址失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteAddress(UserAddress address) {
        dbHelper.deleteUserAddress(address.getId(), new DBMysqlHelper.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(AddressActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                loadAddresses();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AddressActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDefaultAddress(UserAddress address) {
        address.setDefault(true);
        dbHelper.updateUserAddress(address, new DBMysqlHelper.DatabaseCallback<UserAddress>() {
            @Override
            public void onSuccess(UserAddress result) {
                Toast.makeText(AddressActivity.this, "设置默认地址成功", Toast.LENGTH_SHORT).show();
                loadAddresses();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AddressActivity.this, "设置默认地址失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == REQUEST_ADD_ADDRESS || requestCode == REQUEST_EDIT_ADDRESS)) {
            loadAddresses();
        }
    }

    private static final int REQUEST_ADD_ADDRESS = 1;
    private static final int REQUEST_EDIT_ADDRESS = 2;
}

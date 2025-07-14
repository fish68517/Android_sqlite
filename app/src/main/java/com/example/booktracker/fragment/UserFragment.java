package com.example.booktracker.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booktracker.R;
import com.example.booktracker.activity.OrderListActivity;
import com.example.booktracker.adapter.BorrowAdapter;
import com.example.booktracker.db.BorrowDB;
import com.example.booktracker.db.BusinessResult;
import com.example.booktracker.entity.Borrow;
import com.example.booktracker.entity.User;
import com.example.booktracker.utils.CurrentUserUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class UserFragment extends Fragment {
    private MaterialButton btnSearch;
    private TextInputEditText etBookName;
    private RecyclerView rvBorrow;
    private TextView tvUsername, tvLogout;
    private Button btnMyOrders;
    private RadioGroup rgType;
    private BorrowAdapter borrowAdapter;
    private User currentUser;
    private MaterialToolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        tvUsername = view.findViewById(R.id.tv_username);
        rvBorrow = view.findViewById(R.id.rv_borrow);
        btnSearch = view.findViewById(R.id.btn_search);
        etBookName = view.findViewById(R.id.et_book_name);
        rgType = view.findViewById(R.id.rg_type);
        tvLogout = view.findViewById(R.id.tv_logout);
        btnMyOrders = view.findViewById(R.id.btn_my_orders);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 获取当前用户
        currentUser = CurrentUserUtils.getCurrentUser();
        tvUsername.setText(currentUser.getUsername());

        // 设置RecyclerView
        setupBorrowAdapter();
        
        // 退出登录
        setupLogoutButton();
        
        // 查询
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        
        // 我的订单按钮
        btnMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderListActivity.class);
                startActivity(intent);
            }
        });
        
        // 初始化查询
        search();
    }

    private void setupBorrowAdapter() {
        borrowAdapter = new BorrowAdapter();
        borrowAdapter.setOnItemClickListener(new BorrowAdapter.OnItemClickListener() {
            @Override
            public void onItemBorrowClick(int position, Borrow borrow) {
                if (TextUtils.isEmpty(borrow.getReturnDate())) {
                    showReturnBookConfirmDialog(position, borrow);
                }
            }
        });
        rvBorrow.setAdapter(borrowAdapter);
        rvBorrow.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    
    private void setupLogoutButton() {
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmDialog();
            }
        });
    }

    private void showReturnBookConfirmDialog(int position, Borrow borrow) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("归还图书")
                .setMessage("确定要归还《" + borrow.getBookName() + "》吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        returnBook(position, borrow);
                    }
                })
                .setNegativeButton("取消", null)
                .setIcon(R.drawable.ic_home)
                .show();
    }
    
    private void showLogoutConfirmDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("退出登录")
                .setMessage("确定要退出当前账号吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requireActivity().finish();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void returnBook(int position, Borrow borrow) {
        BusinessResult<String> result = BorrowDB.returnBook(borrow.getId(), borrow.getBookId());
        if (result.isSuccess()) {
            borrowAdapter.getList().get(position).setReturnDate(result.getData());
            borrowAdapter.notifyItemChanged(position);
            Toast.makeText(getContext(), "归还成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void search() {
        int checkedRadioButtonId = rgType.getCheckedRadioButtonId();
        // 0 全部 1 借阅中 2 已归还
        int type = 0;
        if (checkedRadioButtonId == R.id.rb_borrowing) {
            type = 1;
        } else if (checkedRadioButtonId == R.id.rb_returned) {
            type = 2;
        }
        
        // 查询借阅列表
        String bookName = etBookName.getText() != null ? etBookName.getText().toString() : "";
        BusinessResult<List<Borrow>> result = BorrowDB.queryBorrowList(currentUser.getId(), bookName, type);
        if (result.isSuccess()) {
            List<Borrow> list = result.getData();
            borrowAdapter.setList(list);
        } else {
            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

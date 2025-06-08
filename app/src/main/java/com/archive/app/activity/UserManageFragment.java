package com.archive.app.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.db.OpenHelperDataBase;
import com.archive.app.model.User;
import com.example.myapplication.R;

import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UserManageFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private OpenHelperDataBase dbHelper;
    private List<User> userList = new ArrayList<>();
    private static final String TAG = "UserManageFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_manage, container, false);
        recyclerView = view.findViewById(R.id.rv_user_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dbHelper = new OpenHelperDataBase(getContext());
        loadUserList();
        adapter = new UserAdapter(getContext(), userList, new UserAdapter.OnUserActionListener() {
            @Override
            public void onView(User record) {
                showDetailDialog(record);
            }
            @Override
            public void onDelete(User record) {
                boolean success = dbHelper.deleteUser(record.id);
                if (success) {
                    Toast.makeText(getContext(), "已删除", Toast.LENGTH_SHORT).show();
                    loadUserList();
                }
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void loadUserList() {
        userList.clear();
        userList.addAll(dbHelper.getAllUsers());
        if (adapter != null) adapter.notifyDataSetChanged();
        Log.d(TAG, "用户列表加载完成，数量：" + userList.size());
    }

    private void showDetailDialog(User record) {
        new AlertDialog.Builder(getContext())
                .setTitle("用户详情")
                .setMessage("用户ID: " + record.getId() +
                        "\n用户名: " + record.getUsername() +
                        "\n角色: " + record.getRole())
                .setPositiveButton("确定", null)
                .show();
    }



    // 适配器
    public static class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
        private Context context;
        private List<User> data;
        private OnUserActionListener listener;
        public interface OnUserActionListener {
            void onView(User record);
            void onDelete(User record);
        }
        public UserAdapter(Context context, List<User> data, OnUserActionListener listener) {
            this.context = context;
            this.data = data;
            this.listener = listener;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_user_record, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            User record = data.get(position);
            holder.tvUsername.setText("用户名: " + record.getUsername());
            holder.tvRole.setText("角色: " + record.getRole());
            holder.btnView.setOnClickListener(v -> listener.onView(record));
            holder.btnDelete.setOnClickListener(v -> listener.onDelete(record));
        }
        @Override
        public int getItemCount() {
            return data.size();
        }
        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvUsername, tvRole;
            Button btnView, btnDelete;
            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvUsername = itemView.findViewById(R.id.tv_user_username);
                tvRole = itemView.findViewById(R.id.tv_user_role);
                btnView = itemView.findViewById(R.id.btn_user_view);
                btnDelete = itemView.findViewById(R.id.btn_user_delete);
            }
        }
    }
}

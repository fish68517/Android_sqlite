package com.example.application.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.application.DatabaseHelper;
import com.example.application.R;
import com.example.application.model.User;
import java.util.ArrayList;
import java.util.List;

public class AdminManageAccountsFragment extends Fragment {

    private ListView lvAllUsers;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> userListInfo = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_accounts, container, false);

        dbHelper = new DatabaseHelper(getContext());
        lvAllUsers = view.findViewById(R.id.lv_all_users);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, userListInfo);
        lvAllUsers.setAdapter(adapter);

        loadAllUsers();

        return view;
    }

    private void loadAllUsers() {
        userListInfo.clear();
        List<User> allUsers = dbHelper.getAllUsers();
        if (allUsers.isEmpty()) {
            userListInfo.add("数据库中没有用户");
        } else {
            for (User user : allUsers) {
                userListInfo.add("ID: " + user.getId() + " | 用户名: " + user.getUsername() + " | 角色: " + user.getRole());
            }
        }
        adapter.notifyDataSetChanged();
    }
}
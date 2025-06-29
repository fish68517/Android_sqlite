package com.example.orderfood.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.app.AlertDialog;
import android.widget.EditText;
import android.text.InputType;

import com.example.orderfood.DataBaseOpenHelper;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.activity.AddContactActivity;
import com.example.orderfood.activity.CreateGroupActivity;
import com.example.orderfood.adapter.ContactsAdapter;
import com.example.orderfood.adapter.GroupsAdapter;
import com.example.orderfood.model.Group;
import com.example.orderfood.model.User;

import java.util.List;

import io.reactivex.annotations.Nullable;

public class ContactsFragment extends Fragment implements ContactsAdapter.OnContactLongClickListener {

    private static final int ADD_CONTACT_REQUEST = 1;
    private RecyclerView contactsRecyclerView;
    private RecyclerView groupsRecyclerView;
    private ContactsAdapter contactsAdapter;
    private GroupsAdapter groupsAdapter;
    private DataBaseOpenHelper dbHelper;
    private List<User> contactList;
    private List<Group> groupList;



    private int currentUserId = MyApplication.user.getId();
    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        dbHelper = new DataBaseOpenHelper(getContext());

        contactList = dbHelper.getContacts(currentUserId);
        groupList = dbHelper.getGroupsForUser(currentUserId);

        contactsRecyclerView = view.findViewById(R.id.recycler_view_contacts);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsAdapter = new ContactsAdapter(contactList, this);
        contactsRecyclerView.setAdapter(contactsAdapter);

        groupsRecyclerView = view.findViewById(R.id.recycler_view_groups);
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        groupsAdapter = new GroupsAdapter(groupList);
        groupsRecyclerView.setAdapter(groupsAdapter);

        Button addContactButton = view.findViewById(R.id.btn_add_contact);
        addContactButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddContactActivity.class);
            startActivityForResult(intent, ADD_CONTACT_REQUEST);
        });
        
        Button createGroupButton = view.findViewById(R.id.btn_create_group);
        createGroupButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
            startActivityForResult(intent, ADD_CONTACT_REQUEST); // Can reuse the same request code
        });

        return view;
    }

    private void showAddOptions() {
        // This method can be deprecated or removed if the new buttons are preferred
        final CharSequence[] options = {"增加联系人", "创建群聊", "取消"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("请选择操作");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("增加联系人")) {
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                startActivityForResult(intent, ADD_CONTACT_REQUEST);
            } else if (options[item].equals("创建群聊")) {
                Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
                startActivityForResult(intent, ADD_CONTACT_REQUEST); // Can reuse the same request code
            } else if (options[item].equals("取消")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            refreshLists();
        }
    }

    @Override
    public void onContactLongClick(User contact) {
        final CharSequence[] options = {"修改备注", "删除联系人", "取消"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("操作");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("修改备注")) {
                showEditRemarkDialog(contact);
            } else if (options[item].equals("删除联系人")) {
                showDeleteConfirmationDialog(contact);
            } else if (options[item].equals("取消")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showDeleteConfirmationDialog(User contact) {
        new AlertDialog.Builder(getContext())
                .setTitle("删除联系人")
                .setMessage("确定要删除联系人 " + contact.getNickname() + " 吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    // Assuming current user ID is 1
                    dbHelper.deleteContact(currentUserId, contact.getId());
                    refreshLists();
                    Toast.makeText(getContext(), "联系人已删除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showEditRemarkDialog(User contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("修改备注");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("输入新备注");
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String remark = input.getText().toString().trim();
            if (!remark.isEmpty()) {
                // Assuming current user ID is 1
                dbHelper.updateContactRemark(currentUserId, contact.getId(), remark);
                Toast.makeText(getContext(), "备注已更新", Toast.LENGTH_SHORT).show();
                refreshLists(); // Refresh to reflect changes if any
            }
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void refreshLists() {
        // Assuming current user's ID is 1
        contactList.clear();
        contactList.addAll(dbHelper.getContacts(currentUserId));
        contactsAdapter.notifyDataSetChanged();

        groupList.clear();
        groupList.addAll(dbHelper.getGroupsForUser(currentUserId));
        groupsAdapter.notifyDataSetChanged();

        Toast.makeText(getContext(), "列表已更新", Toast.LENGTH_SHORT).show();
    }
} 
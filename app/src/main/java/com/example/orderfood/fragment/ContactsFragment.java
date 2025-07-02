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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.app.AlertDialog;
import android.widget.EditText;
import android.text.InputType;
import android.widget.TextView;

import com.example.orderfood.DataBaseOpenHelper;
import com.example.orderfood.MyApplication;
import com.example.orderfood.R;
import com.example.orderfood.activity.AddContactActivity;
import com.example.orderfood.activity.CreateGroupActivity;
import com.example.orderfood.adapter.ContactsAdapter;
import com.example.orderfood.adapter.GroupsAdapter;
import com.example.orderfood.model.Group;
import com.example.orderfood.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.annotations.Nullable;

public class ContactsFragment extends Fragment implements ContactsAdapter.OnContactActionsListener {

    private static final int ADD_CONTACT_REQUEST = 1;
    private RecyclerView contactsRecyclerView;
    private RecyclerView groupsRecyclerView;
    private ContactsAdapter contactsAdapter;
    private GroupsAdapter groupsAdapter;
    private DataBaseOpenHelper dbHelper;
    private List<Object> contactListWithHeaders;
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

        List<User> contactList = dbHelper.getContacts(currentUserId);
        Collections.sort(contactList, (o1, o2) -> o1.getNickname().compareTo(o2.getNickname()));
        contactListWithHeaders = createGroupedList(contactList);
        groupList = dbHelper.getGroupsForUser(currentUserId);

        contactsRecyclerView = view.findViewById(R.id.recycler_view_contacts);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsAdapter = new ContactsAdapter(contactListWithHeaders, this);
        contactsRecyclerView.setAdapter(contactsAdapter);

        groupsRecyclerView = view.findViewById(R.id.recycler_view_groups);
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        groupsAdapter = new GroupsAdapter(groupList);
        groupsRecyclerView.setAdapter(groupsAdapter);

        ImageView menuButton = view.findViewById(R.id.iv_menu);
        menuButton.setOnClickListener(v -> showPopupMenu(v));


        return view;
    }

    private List<Object> createGroupedList(List<User> contacts) {
        List<Object> groupedList = new ArrayList<>();
        String lastHeader = "";
        for (User contact : contacts) {
            String nickname = contact.getNickname();
            if (nickname != null && !nickname.isEmpty()) {
                String header = nickname.substring(0, 1).toUpperCase();
                if (!lastHeader.equals(header)) {
                    groupedList.add(header);
                    lastHeader = header;
                }
            }
            groupedList.add(contact);
        }
        return groupedList;
    }

    private void showPopupMenu(View anchor) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_menu, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAsDropDown(anchor);

        TextView addFriend = popupView.findViewById(R.id.tv_add_friend);
        addFriend.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddContactActivity.class);
            startActivityForResult(intent, ADD_CONTACT_REQUEST);
            popupWindow.dismiss();
        });

        TextView startGroupChat = popupView.findViewById(R.id.tv_start_group_chat);
        startGroupChat.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
            startActivityForResult(intent, ADD_CONTACT_REQUEST); // Can reuse the same request code
            popupWindow.dismiss();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            refreshLists();
        }
    }

    @Override
    public void onEditClick(User contact) {
        showEditRemarkDialog(contact);
    }

    @Override
    public void onDeleteClick(User contact) {
        showDeleteConfirmationDialog(contact);
    }

    private void showDeleteConfirmationDialog(User contact) {
        new AlertDialog.Builder(getContext())
                .setTitle("删除联系人")
                .setMessage("注意：这将会永久删除你们之间的所有聊天记录。")
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
        builder.setTitle("修改昵称");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("输入新昵称");
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String remark = input.getText().toString().trim();
            if (!remark.isEmpty()) {
                // Assuming current user ID is 1
                dbHelper.updateUsername(contact.getId(),  remark);
                dbHelper.updateContactRemark(currentUserId, contact.getId(), remark);
                dbHelper.updateUserNickname(contact.getId(),  remark);

                Toast.makeText(getContext(), "昵称已更新", Toast.LENGTH_SHORT).show();
                refreshLists();
            }
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void refreshLists() {
        // Assuming current user's ID is 1
        List<User> contactList = dbHelper.getContacts(currentUserId);
        Collections.sort(contactList, (o1, o2) -> o1.getNickname().compareTo(o2.getNickname()));
        contactListWithHeaders.clear();
        contactListWithHeaders.addAll(createGroupedList(contactList));
        contactsAdapter.notifyDataSetChanged();

        groupList.clear();
        groupList.addAll(dbHelper.getGroupsForUser(currentUserId));
        groupsAdapter.notifyDataSetChanged();

        Toast.makeText(getContext(), "列表已更新", Toast.LENGTH_SHORT).show();
    }
} 
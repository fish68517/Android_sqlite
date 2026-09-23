package com.personal.diary;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.personal.diary.db.DiaryDbHelper;
import com.personal.diary.model.AdminRecord;
import com.personal.diary.model.Comment;
import com.personal.diary.ui.RecordAdapter;
import com.personal.diary.ui.RecordItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminManageFragment extends Fragment implements RecordAdapter.Listener {
    private static final String ARG_TYPE = "type";
    private DiaryDbHelper db;
    private SessionManager session;
    private RecordAdapter adapter;
    private EditText searchEdit;
    private TextView emptyText;
    private Button addButton;
    private String type;
    private final Map<Long, AdminRecord> records = new HashMap<>();

    public static AdminManageFragment newInstance(String type) {
        AdminManageFragment fragment = new AdminManageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    public AdminManageFragment() {
        super(R.layout.fragment_admin_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DiaryDbHelper(requireContext());
        session = new SessionManager(requireContext());
        type = getArguments() == null ? "users" : getArguments().getString(ARG_TYPE, "users");
        searchEdit = view.findViewById(R.id.adminSearchEdit);
        emptyText = view.findViewById(R.id.adminEmptyText);
        addButton = view.findViewById(R.id.adminAddButton);
        adapter = new RecordAdapter(this);
        RecyclerView recyclerView = view.findViewById(R.id.adminRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        addButton.setOnClickListener(v -> showEditor(null));
        if ("feedback".equals(type)) {
            addButton.setVisibility(View.GONE);
        }
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refresh();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        refresh();
    }

    private void refresh() {
        records.clear();
        List<RecordItem> items = new ArrayList<>();
        for (AdminRecord record : db.adminList(type, searchEdit.getText().toString())) {
            records.put(record.id, record);
            RecordItem item = new RecordItem(record.id, record.userId, type,
                    displayTitle(record), displayBody(record), displayMeta(record));
            item.primaryAction = primaryActionText();
            item.secondaryAction = secondaryActionText();
            item.showDelete = true;
            items.add(item);
        }
        adapter.submit(items);
        emptyText.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private String displayTitle(AdminRecord record) {
        if ("moments".equals(type)) {
            return getString(R.string.admin_nav_moments);
        }
        if ("tree_holes".equals(type)) {
            return getString(R.string.admin_nav_tree_holes);
        }
        if ("feedback".equals(type)) {
            return getString(R.string.admin_nav_feedback);
        }
        return record.title;
    }

    private String displayBody(AdminRecord record) {
        return record.body == null ? "" : record.body;
    }

    private String displayMeta(AdminRecord record) {
        if ("users".equals(type)) {
            String[] parts = split(record.extra, 3);
            return getString(R.string.admin_label_email) + "：" + record.meta + "  "
                    + getString(R.string.admin_label_role) + "：" + roleText(parts[0]) + "\n"
                    + getString(R.string.admin_label_register_time) + "：" + parts[2];
        }
        if ("diaries".equals(type)) {
            String[] parts = split(record.extra, 4);
            return getString(R.string.admin_label_author) + "：" + record.meta + "  "
                    + getString(R.string.admin_label_mood) + "：" + parts[0] + "  "
                    + getString(R.string.admin_label_weather) + "：" + parts[1];
        }
        if ("moments".equals(type)) {
            String[] parts = split(record.extra, 2);
            return getString(R.string.admin_label_author) + "：" + record.meta + "  "
                    + getString(R.string.admin_label_likes) + "：" + parts[0] + "  "
                    + getString(R.string.admin_label_favorites) + "：" + parts[1];
        }
        if ("tree_holes".equals(type) || "posts".equals(type)) {
            String[] parts = split(record.extra, 1);
            return getString(R.string.admin_label_author) + "：" + record.meta + "  "
                    + getString(R.string.admin_label_comments) + "：" + parts[0];
        }
        if ("feedback".equals(type)) {
            String[] parts = split(record.extra, 2);
            return getString(R.string.admin_label_author) + "：" + record.meta + "  "
                    + getString(R.string.admin_label_status) + "：" + parts[0] + "\n"
                    + getString(R.string.admin_label_reply) + "：" + parts[1];
        }
        if ("notices".equals(type)) {
            return getString(R.string.admin_label_publish_time) + "：" + record.meta;
        }
        return record.meta;
    }

    private String primaryActionText() {
        return "feedback".equals(type) ? getString(R.string.admin_reply) : getString(R.string.admin_edit);
    }

    private String secondaryActionText() {
        if ("posts".equals(type) || "tree_holes".equals(type)) {
            return getString(R.string.admin_comments);
        }
        return "";
    }

    @Override
    public void onItemClick(RecordItem item) {
        AdminRecord record = records.get(item.id);
        if (record == null) {
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(displayTitle(record))
                .setMessage(displayBody(record) + "\n\n" + displayMeta(record))
                .setPositiveButton(R.string.admin_confirm, null)
                .show();
    }

    @Override
    public void onPrimary(RecordItem item) {
        showEditor(records.get(item.id));
    }

    @Override
    public void onSecondary(RecordItem item) {
        if ("posts".equals(type)) {
            showComments("post", item.id);
        } else if ("tree_holes".equals(type)) {
            showComments("tree_hole", item.id);
        }
    }

    @Override
    public void onEdit(RecordItem item) {
        showEditor(records.get(item.id));
    }

    @Override
    public void onDelete(RecordItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_delete_title)
                .setMessage(R.string.admin_delete_message)
                .setPositiveButton(R.string.admin_confirm, (dialog, which) -> {
                    if ("users".equals(type)) {
                        db.adminDeleteUser(item.id);
                    } else {
                        db.adminDelete(type, item.id);
                    }
                    refresh();
                })
                .setNegativeButton(R.string.admin_cancel, null)
                .show();
    }

    private void showEditor(AdminRecord record) {
        View form = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_admin_form, null);
        EditText one = form.findViewById(R.id.adminFieldOne);
        EditText two = form.findViewById(R.id.adminFieldTwo);
        EditText three = form.findViewById(R.id.adminFieldThree);
        EditText four = form.findViewById(R.id.adminFieldFour);
        EditText five = form.findViewById(R.id.adminFieldFive);
        bindForm(record, one, two, three, four, five);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(editorTitle())
                .setView(form)
                .setPositiveButton(R.string.admin_save, null)
                .setNegativeButton(R.string.admin_cancel, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (!saveRecord(record, one, two, three, four, five)) {
                Toast.makeText(requireContext(), R.string.admin_required_message, Toast.LENGTH_SHORT).show();
                return;
            }
            dialog.dismiss();
            refresh();
        }));
        dialog.show();
    }

    private void bindForm(AdminRecord record, EditText one, EditText two, EditText three, EditText four, EditText five) {
        if ("users".equals(type)) {
            one.setHint(R.string.admin_label_username);
            two.setHint(R.string.admin_label_nickname);
            three.setHint(R.string.admin_label_email);
            four.setHint(R.string.admin_label_role);
            five.setHint(R.string.admin_label_password);
            if (record != null) {
                one.setText(record.title);
                two.setText(record.body);
                three.setText(record.meta);
                four.setText(roleText(split(record.extra, 1)[0]));
                five.setVisibility(View.GONE);
            } else {
                four.setText(R.string.admin_role_user);
            }
        } else if ("diaries".equals(type)) {
            five.setVisibility(View.GONE);
            one.setHint(R.string.admin_label_title);
            two.setHint(R.string.admin_label_content);
            three.setHint(R.string.admin_label_mood);
            four.setHint(R.string.admin_label_weather);
            if (record != null) {
                String[] parts = split(record.extra, 4);
                one.setText(record.title);
                two.setText(record.body);
                three.setText(parts[0]);
                four.setText(parts[1]);
            }
        } else if ("moments".equals(type) || "tree_holes".equals(type)) {
            five.setVisibility(View.GONE);
            one.setHint(R.string.admin_label_content);
            two.setVisibility(View.GONE);
            three.setVisibility(View.GONE);
            four.setVisibility(View.GONE);
            if (record != null) {
                one.setText(record.body);
            }
        } else if ("posts".equals(type) || "notices".equals(type)) {
            five.setVisibility(View.GONE);
            one.setHint(R.string.admin_label_title);
            two.setHint(R.string.admin_label_content);
            three.setVisibility(View.GONE);
            four.setVisibility(View.GONE);
            if (record != null) {
                one.setText(record.title);
                two.setText(record.body);
            }
        } else if ("feedback".equals(type)) {
            five.setVisibility(View.GONE);
            one.setHint(R.string.admin_label_reply);
            two.setHint(R.string.admin_label_status);
            three.setVisibility(View.GONE);
            four.setVisibility(View.GONE);
            if (record != null) {
                String[] parts = split(record.extra, 2);
                one.setText(parts[1]);
                two.setText(parts[0].isEmpty() ? getString(R.string.admin_feedback_status_done) : parts[0]);
            }
        }
    }

    private boolean saveRecord(AdminRecord record, EditText one, EditText two, EditText three, EditText four, EditText five) {
        long id = record == null ? 0 : record.id;
        long ownerId = record == null ? session.getUserId() : record.userId;
        String v1 = one.getText().toString().trim();
        String v2 = two.getText().toString().trim();
        String v3 = three.getText().toString().trim();
        String v4 = four.getText().toString().trim();
        String v5 = five.getText().toString().trim();
        if (v1.isEmpty()) {
            return false;
        }
        if ("users".equals(type)) {
            db.adminSaveUser(id, v1, v5, v3, v2, "", roleValue(v4));
            return true;
        }
        if (("diaries".equals(type) || "posts".equals(type) || "notices".equals(type)) && v2.isEmpty()) {
            return false;
        }
        db.adminSaveContent(type, id, ownerId, v1, v2, v3, v4);
        return true;
    }

    private int editorTitle() {
        if ("users".equals(type)) {
            return R.string.admin_user_form_title;
        }
        if ("feedback".equals(type)) {
            return R.string.admin_feedback_form_title;
        }
        if ("notices".equals(type)) {
            return R.string.admin_notice_form_title;
        }
        return R.string.admin_content_form_title;
    }

    private void showComments(String targetType, long targetId) {
        StringBuilder builder = new StringBuilder();
        for (Comment comment : db.getComments(targetType, targetId)) {
            builder.append(comment.author).append("：").append(comment.content)
                    .append("\n").append(comment.createdAt).append("\n\n");
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_comments)
                .setMessage(builder.length() == 0 ? getString(R.string.admin_empty) : builder.toString())
                .setPositiveButton(R.string.admin_confirm, null)
                .show();
    }

    private String[] split(String value, int size) {
        String[] result = new String[size];
        String[] parts = value == null ? new String[0] : value.split("\\|", -1);
        for (int i = 0; i < size; i++) {
            result[i] = i < parts.length ? parts[i] : "";
        }
        return result;
    }

    private String roleText(String role) {
        return "admin".equals(role) ? getString(R.string.admin_role_admin) : getString(R.string.admin_role_user);
    }

    private String roleValue(String roleText) {
        return roleText != null && roleText.contains(getString(R.string.admin_role_admin)) ? "admin" : "user";
    }
}

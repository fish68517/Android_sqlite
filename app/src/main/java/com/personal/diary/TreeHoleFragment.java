package com.personal.diary;

import android.app.AlertDialog;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.personal.diary.model.Comment;
import com.personal.diary.model.TreeHole;
import com.personal.diary.ui.RecordItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeHoleFragment extends BaseRecordsFragment {
    private final Map<Long, TreeHole> treeHoleMap = new HashMap<>();

    @Override
    protected List<RecordItem> loadItems() {
        treeHoleMap.clear();
        List<RecordItem> items = new ArrayList<>();
        for (TreeHole treeHole : db.getTreeHoles()) {
            treeHoleMap.put(treeHole.id, treeHole);
            String author = treeHole.anonymous ? "匿名用户" : safe(treeHole.author);
            RecordItem item = new RecordItem(
                    treeHole.id,
                    treeHole.userId,
                    "tree_hole",
                    author + " 的树洞",
                    treeHole.content,
                    treeHole.createdAt + "  评论 " + treeHole.commentCount);
            item.primaryAction = "评论";
            item.secondaryAction = "查看评论";
            item.showDelete = treeHole.userId == session.getUserId();
            items.add(item);
        }
        return items;
    }

    @Override
    protected String emptyMessage() {
        return "暂无树洞，点击右下角写下心声";
    }

    @Override
    protected void onAddClick() {
        LinearLayout form = DialogUtil.form(requireContext());
        EditText content = DialogUtil.input(requireContext(), form, "写下想说的话", "", 5);
        CheckBox anonymous = DialogUtil.checkBox(requireContext(), form, "匿名发布", true);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("发布树洞")
                .setView(form)
                .setPositiveButton("发布", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String value = content.getText().toString().trim();
            if (value.isEmpty()) {
                toast("内容不能为空");
                return;
            }
            db.saveTreeHole(session.getUserId(), value, anonymous.isChecked());
            dialog.dismiss();
            refresh();
        }));
        dialog.show();
    }

    @Override
    public void onItemClick(RecordItem item) {
        showComments(item.id);
    }

    @Override
    public void onPrimary(RecordItem item) {
        LinearLayout form = DialogUtil.form(requireContext());
        EditText content = DialogUtil.input(requireContext(), form, "评论内容", "", 3);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("发表评论")
                .setView(form)
                .setPositiveButton("提交", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String value = content.getText().toString().trim();
            if (value.isEmpty()) {
                toast("评论不能为空");
                return;
            }
            db.addComment(session.getUserId(), "tree_hole", item.id, value);
            dialog.dismiss();
            refresh();
        }));
        dialog.show();
    }

    @Override
    public void onSecondary(RecordItem item) {
        showComments(item.id);
    }

    @Override
    public void onDelete(RecordItem item) {
        DialogUtil.confirm(requireContext(), "删除树洞", "确定删除这条树洞吗？", () -> {
            db.deleteTreeHole(item.id, session.getUserId());
            refresh();
        });
    }

    private void showComments(long targetId) {
        TreeHole treeHole = treeHoleMap.get(targetId);
        List<Comment> comments = db.getComments("tree_hole", targetId);
        StringBuilder builder = new StringBuilder();
        if (treeHole != null) {
            builder.append(treeHole.content).append("\n\n");
        }
        if (comments.isEmpty()) {
            builder.append("暂无评论");
        } else {
            for (Comment comment : comments) {
                builder.append(safe(comment.author)).append("：").append(comment.content)
                        .append("\n").append(comment.createdAt).append("\n\n");
            }
        }
        new AlertDialog.Builder(requireContext())
                .setTitle("树洞评论")
                .setMessage(builder.toString())
                .setPositiveButton("知道了", null)
                .show();
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "用户" : value;
    }
}

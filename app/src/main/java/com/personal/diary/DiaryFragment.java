package com.personal.diary;

import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.personal.diary.model.Diary;
import com.personal.diary.ui.RecordItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiaryFragment extends BaseRecordsFragment {
    private final Map<Long, Diary> diaryMap = new HashMap<>();

    @Override
    protected List<RecordItem> loadItems() {
        diaryMap.clear();
        List<RecordItem> items = new ArrayList<>();
        for (Diary diary : db.getDiaries(session.getUserId())) {
            diaryMap.put(diary.id, diary);
            RecordItem item = new RecordItem(
                    diary.id,
                    diary.userId,
                    "diary",
                    diary.title,
                    diary.content,
                    "心情：" + safe(diary.mood) + "  天气：" + safe(diary.weather) + "  标签：" + safe(diary.tags) + "\n" + diary.updatedAt);
            item.showEdit = true;
            item.showDelete = true;
            items.add(item);
        }
        return items;
    }

    @Override
    protected String emptyMessage() {
        return "暂无日记，点击右下角开始记录";
    }

    @Override
    protected void onAddClick() {
        showDiaryDialog(null);
    }

    @Override
    public void onItemClick(RecordItem item) {
        Diary diary = diaryMap.get(item.id);
        if (diary == null) {
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(diary.title)
                .setMessage("心情：" + safe(diary.mood) + "\n天气：" + safe(diary.weather) + "\n标签：" + safe(diary.tags)
                        + "\n状态：" + safe(diary.status) + "\n\n" + diary.content)
                .setPositiveButton("知道了", null)
                .show();
    }

    @Override
    public void onEdit(RecordItem item) {
        showDiaryDialog(diaryMap.get(item.id));
    }

    @Override
    public void onDelete(RecordItem item) {
        DialogUtil.confirm(requireContext(), "删除日记", "确定删除这篇日记吗？", () -> {
            db.deleteDiary(item.id, session.getUserId());
            refresh();
        });
    }

    private void showDiaryDialog(Diary diary) {
        LinearLayout form = DialogUtil.form(requireContext());
        EditText title = DialogUtil.input(requireContext(), form, "标题", diary == null ? "" : diary.title, 1);
        EditText content = DialogUtil.input(requireContext(), form, "正文", diary == null ? "" : diary.content, 4);
        EditText mood = DialogUtil.input(requireContext(), form, "心情，例如 开心/平静/难过", diary == null ? "平静" : diary.mood, 1);
        EditText weather = DialogUtil.input(requireContext(), form, "天气", diary == null ? "晴" : diary.weather, 1);
        EditText tags = DialogUtil.input(requireContext(), form, "标签，多个用逗号分隔", diary == null ? "" : diary.tags, 1);
        EditText status = DialogUtil.input(requireContext(), form, "状态：草稿/已发布/私密", diary == null ? "已发布" : diary.status, 1);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(diary == null ? "新建日记" : "编辑日记")
                .setView(form)
                .setPositiveButton("保存", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String titleValue = title.getText().toString().trim();
            String contentValue = content.getText().toString().trim();
            if (titleValue.isEmpty() || contentValue.isEmpty()) {
                toast("标题和正文不能为空");
                return;
            }
            long id = diary == null ? 0 : diary.id;
            db.saveDiary(id, session.getUserId(), titleValue, contentValue,
                    mood.getText().toString().trim(),
                    weather.getText().toString().trim(),
                    tags.getText().toString().trim(),
                    status.getText().toString().trim());
            dialog.dismiss();
            refresh();
        }));
        dialog.show();
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "未填写" : value;
    }
}

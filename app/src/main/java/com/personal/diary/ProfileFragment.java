package com.personal.diary;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.personal.diary.db.DiaryDbHelper;
import com.personal.diary.model.User;
import com.personal.diary.ui.StatsDashboardView;

public class ProfileFragment extends Fragment {
    private DiaryDbHelper db;
    private SessionManager session;
    private TextView nicknameText;
    private TextView userInfoText;
    private TextView statsText;
    private StatsDashboardView statsChart;
    private User user;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DiaryDbHelper(requireContext());
        session = new SessionManager(requireContext());
        nicknameText = view.findViewById(R.id.nicknameText);
        userInfoText = view.findViewById(R.id.userInfoText);
        statsText = view.findViewById(R.id.statsText);
        statsChart = view.findViewById(R.id.statsChart);
        view.findViewById(R.id.editProfileButton).setOnClickListener(v -> showEditProfileDialog());
        view.findViewById(R.id.changePasswordButton).setOnClickListener(v -> showPasswordDialog());
        view.findViewById(R.id.feedbackButton).setOnClickListener(v -> showFeedbackDialog());
        view.findViewById(R.id.noticeButton).setOnClickListener(v -> showNoticeDialog());
        view.findViewById(R.id.logoutButton).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).logout();
            }
        });
        refresh();
    }

    private void refresh() {
        user = db.getUser(session.getUserId());
        if (user == null) {
            return;
        }
        nicknameText.setText(user.nickname);
        userInfoText.setText("用户名：" + user.username + "\n邮箱：" + user.email + "\n签名：" + user.signature + "\n注册时间：" + user.createdAt);
        int diaryCount = db.count("diaries", user.id);
        int momentCount = db.count("moments", user.id);
        int treeHoleCount = db.count("tree_holes", user.id);
        int favoriteCount = db.countFavorites(user.id);
        statsText.setText("本地统计\n日记：" + diaryCount + " 篇\n幸福瞬间：" + momentCount + " 条\n树洞：" + treeHoleCount + " 条\n收藏：" + favoriteCount + " 条");
        statsChart.setData(
                new String[]{"日记", "瞬间", "树洞", "收藏"},
                new int[]{diaryCount, momentCount, treeHoleCount, favoriteCount},
                db.getRecentSevenDayLabels(),
                db.getRecentSevenDayRecordCounts(user.id),
                db.getRecentSevenDayMoodScores(user.id));
    }

    private void showEditProfileDialog() {
        if (user == null) {
            return;
        }
        LinearLayout form = DialogUtil.form(requireContext());
        EditText nickname = DialogUtil.input(requireContext(), form, "昵称", user.nickname, 1);
        EditText email = DialogUtil.input(requireContext(), form, "邮箱", user.email, 1);
        EditText signature = DialogUtil.input(requireContext(), form, "个性签名", user.signature, 2);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("编辑资料")
                .setView(form)
                .setPositiveButton("保存", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nicknameValue = nickname.getText().toString().trim();
            if (nicknameValue.isEmpty()) {
                toast("昵称不能为空");
                return;
            }
            db.updateUser(user.id, nicknameValue, email.getText().toString().trim(), signature.getText().toString().trim());
            dialog.dismiss();
            refresh();
        }));
        dialog.show();
    }

    private void showFeedbackDialog() {
        LinearLayout form = DialogUtil.form(requireContext());
        EditText content = DialogUtil.input(requireContext(), form, "请输入反馈内容", "", 4);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("留言反馈")
                .setMessage(db.getFeedbackText(session.getUserId()))
                .setView(form)
                .setPositiveButton("提交", null)
                .setNegativeButton("关闭", null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String value = content.getText().toString().trim();
            if (value.isEmpty()) {
                toast("反馈内容不能为空");
                return;
            }
            db.addFeedback(session.getUserId(), value);
            toast("反馈已保存");
            dialog.dismiss();
        }));
        dialog.show();
    }

    private void showNoticeDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("公告信息")
                .setMessage(db.getNoticesText())
                .setPositiveButton("知道了", null)
                .show();
    }

    private void showPasswordDialog() {
        LinearLayout form = DialogUtil.form(requireContext());
        EditText oldPassword = DialogUtil.input(requireContext(), form, "旧密码", "", 1);
        EditText newPassword = DialogUtil.input(requireContext(), form, "新密码，至少 6 位", "", 1);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("修改密码")
                .setView(form)
                .setPositiveButton("保存", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newValue = newPassword.getText().toString();
            if (newValue.length() < 6) {
                toast("新密码至少 6 位");
                return;
            }
            boolean success = db.changePassword(session.getUserId(), oldPassword.getText().toString(), newValue);
            if (!success) {
                toast("旧密码错误");
                return;
            }
            toast("密码已修改");
            dialog.dismiss();
        }));
        dialog.show();
    }

    private void toast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}

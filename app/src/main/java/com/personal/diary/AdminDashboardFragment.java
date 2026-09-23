package com.personal.diary;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.personal.diary.db.DiaryDbHelper;
import com.personal.diary.ui.AdminStatsView;

public class AdminDashboardFragment extends Fragment {
    private DiaryDbHelper db;

    public AdminDashboardFragment() {
        super(R.layout.fragment_admin_dashboard);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DiaryDbHelper(requireContext());
        TextView summary = view.findViewById(R.id.adminStatsSummary);
        AdminStatsView chart = view.findViewById(R.id.adminStatsChart);

        int users = db.countAll("users");
        int diaries = db.countAll("diaries");
        int moments = db.countAll("moments");
        int treeHoles = db.countAll("tree_holes");
        int posts = db.countAll("posts");
        int feedback = db.countAll("feedback");
        int notices = db.countAll("notices");
        int momentLikes = db.countTargetAll("likes", "moment");
        int momentFavorites = db.countTargetAll("favorites", "moment");
        int diaryLikes = db.countTargetAll("likes", "diary");
        int diaryFavorites = db.countTargetAll("favorites", "diary");
        int comments = db.countAll("comments");

        summary.setText(getString(R.string.admin_label_users) + "：" + users + "\n"
                + getString(R.string.admin_label_diaries) + "：" + diaries + "\n"
                + getString(R.string.admin_label_moments) + "：" + moments + "\n"
                + getString(R.string.admin_label_tree_holes) + "：" + treeHoles + "\n"
                + getString(R.string.admin_label_posts) + "：" + posts + "\n"
                + getString(R.string.admin_label_feedback) + "：" + feedback + "\n"
                + getString(R.string.admin_label_notices) + "：" + notices + "\n"
                + getString(R.string.admin_label_likes) + "：" + (momentLikes + diaryLikes) + "\n"
                + getString(R.string.admin_label_favorites) + "：" + (momentFavorites + diaryFavorites) + "\n"
                + getString(R.string.admin_label_comments) + "：" + comments);

        chart.setData(
                getString(R.string.admin_stats_content),
                getString(R.string.admin_stats_trend),
                getString(R.string.admin_stats_interaction),
                getString(R.string.admin_stats_interaction_detail),
                new String[]{
                        getString(R.string.admin_label_users),
                        getString(R.string.admin_label_diaries),
                        getString(R.string.admin_label_moments),
                        getString(R.string.admin_label_tree_holes),
                        getString(R.string.admin_label_posts),
                        getString(R.string.admin_label_feedback),
                        getString(R.string.admin_label_notices)},
                new int[]{users, diaries, moments, treeHoles, posts, feedback, notices},
                db.getRecentSevenDayLabels(),
                db.getRecentSevenDayAllCounts(),
                db.getRecentSevenDayInteractionCounts(),
                new String[]{
                        getString(R.string.admin_label_moment_like),
                        getString(R.string.admin_label_moment_favorite),
                        getString(R.string.admin_label_diary_like),
                        getString(R.string.admin_label_diary_favorite),
                        getString(R.string.admin_label_tree_comment),
                        getString(R.string.admin_label_post_comment)},
                new int[]{
                        momentLikes,
                        momentFavorites,
                        diaryLikes,
                        diaryFavorites,
                        db.countTargetAll("comments", "tree_hole"),
                        db.countTargetAll("comments", "post")});
    }
}

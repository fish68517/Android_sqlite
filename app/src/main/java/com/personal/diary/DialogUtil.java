package com.personal.diary;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public final class DialogUtil {
    private DialogUtil() {
    }

    public static LinearLayout form(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(context, 18);
        layout.setPadding(padding, padding, padding, 0);
        return layout;
    }

    public static EditText input(Context context, LinearLayout parent, String hint, String value, int minLines) {
        EditText editText = new EditText(context);
        editText.setHint(hint);
        editText.setText(value == null ? "" : value);
        editText.setMinLines(minLines);
        editText.setTextColor(context.getColor(R.color.diary_text));
        editText.setHintTextColor(context.getColor(R.color.diary_text_soft));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dp(context, 8), 0, 0);
        parent.addView(editText, params);
        return editText;
    }

    public static CheckBox checkBox(Context context, LinearLayout parent, String text, boolean checked) {
        CheckBox checkBox = new CheckBox(context);
        checkBox.setText(text);
        checkBox.setTextColor(context.getColor(R.color.diary_text));
        checkBox.setChecked(checked);
        parent.addView(checkBox);
        return checkBox;
    }

    public static void confirm(Context context, String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", (dialog, which) -> onConfirm.run())
                .setNegativeButton("取消", null)
                .show();
    }

    public static int dp(Context context, int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }
}

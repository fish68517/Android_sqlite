package com.example.knowledgelabs.util;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.knowledgelabs.R;

public final class LabUi {
    private LabUi() {
    }

    public static LinearLayout setupSimple(Activity activity, String title, String subtitle) {
        activity.setContentView(R.layout.activity_simple_lab);
        ((TextView) activity.findViewById(R.id.tvTitle)).setText(title);
        ((TextView) activity.findViewById(R.id.tvSubtitle)).setText(subtitle);
        return activity.findViewById(R.id.contentContainer);
    }

    public static TextView addText(Activity activity, LinearLayout container, String text) {
        TextView textView = new TextView(activity);
        textView.setText(text);
        textView.setTextColor(activity.getColor(R.color.text_primary));
        textView.setTextSize(16f);
        textView.setLineSpacing(0f, 1.25f);
        textView.setPadding(0, dp(activity, 6), 0, dp(activity, 6));
        container.addView(textView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return textView;
    }

    public static TextView addHeading(Activity activity, LinearLayout container, String text) {
        TextView textView = addText(activity, container, text);
        textView.setTextSize(19f);
        textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textView.setTextColor(activity.getColor(R.color.primary));
        return textView;
    }

    public static Button addButton(Activity activity, LinearLayout container, String text) {
        Button button = new Button(activity);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(15f);
        button.setAllCaps(false);
        button.setBackgroundTintList(activity.getColorStateList(R.color.primary));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(activity, 50));
        params.topMargin = dp(activity, 8);
        container.addView(button, params);
        return button;
    }

    private static int dp(Activity activity, int value) {
        return Math.round(value * activity.getResources().getDisplayMetrics().density);
    }
}


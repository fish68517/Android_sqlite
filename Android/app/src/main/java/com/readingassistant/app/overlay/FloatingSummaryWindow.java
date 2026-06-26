package com.readingassistant.app.overlay;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.readingassistant.app.util.DebugLog;
import com.readingassistant.app.util.PermissionUtils;

public class FloatingSummaryWindow {
    private static FloatingSummaryWindow instance;

    private final Context context;
    private final WindowManager windowManager;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private View currentView;
    private float downX;

    private final Runnable autoDismissRunnable = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };

    private FloatingSummaryWindow(Context context) {
        this.context = context.getApplicationContext();
        windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
    }

    public static synchronized FloatingSummaryWindow get(Context context) {
        if (instance == null) {
            instance = new FloatingSummaryWindow(context);
        }
        return instance;
    }

    public void showSummary(final String summary) {
        DebugLog.d("Overlay show requested summary=" + DebugLog.preview(summary));
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                showOnMain(summary);
            }
        });
    }

    public void hideForCapture() {
        DebugLog.d("Overlay hide for capture requested");
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        });
    }

    public void dismiss() {
        mainHandler.removeCallbacks(autoDismissRunnable);
        if (currentView != null && windowManager != null) {
            try {
                windowManager.removeView(currentView);
                DebugLog.d("Overlay dismissed");
            } catch (IllegalArgumentException ignored) {
                // The view may already be detached by the system.
            }
            currentView = null;
        }
    }

    private void showOnMain(String summary) {
        if (!PermissionUtils.canDrawOverlays(context) || windowManager == null) {
            DebugLog.w("Overlay show skipped canDraw=" + PermissionUtils.canDrawOverlays(context)
                    + " windowManagerNull=" + (windowManager == null));
            return;
        }
        dismiss();

        FrameLayout container = new FrameLayout(context);
        int horizontalMargin = dp(14);
        int topPadding = dp(10);
        container.setPadding(horizontalMargin, topPadding, horizontalMargin, 0);

        TextView textView = new TextView(context);
        textView.setText(summary);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(14);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setMaxLines(2);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(dp(14), dp(10), dp(14), dp(10));

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.argb(218, 28, 34, 44));
        background.setCornerRadius(dp(8));
        textView.setBackground(background);

        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        container.addView(textView, textParams);
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downX = event.getRawX();
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    if (Math.abs(event.getRawX() - downX) > dp(80)) {
                        dismiss();
                    }
                    return true;
                }
                return true;
            }
        });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                overlayType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        try {
            windowManager.addView(container, params);
            currentView = container;
            mainHandler.postDelayed(autoDismissRunnable, 5200);
            DebugLog.d("Overlay added to window");
        } catch (RuntimeException ignored) {
            DebugLog.e("Overlay add failed", ignored);
            currentView = null;
        }
    }

    private int overlayType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        return WindowManager.LayoutParams.TYPE_PHONE;
    }

    private int dp(int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }
}

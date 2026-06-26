package com.readingassistant.app.access;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.readingassistant.app.model.PageSnapshot;
import com.readingassistant.app.scheduler.CaptureCoordinator;
import com.readingassistant.app.util.DebugLog;

public class ReaderAccessibilityService extends AccessibilityService {
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        DebugLog.d("Accessibility service connected");
        CaptureCoordinator.getInstance().init(getApplicationContext());
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) {
            DebugLog.w("Accessibility event is null");
            return;
        }
        int type = event.getEventType();
        if (type != AccessibilityEvent.TYPE_VIEW_SCROLLED
                && type != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
                && type != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return;
        }
        DebugLog.d("Accessibility event type=" + eventTypeName(type)
                + " package=" + event.getPackageName()
                + " class=" + event.getClassName());
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) {
            DebugLog.w("Accessibility root is null package=" + event.getPackageName());
            return;
        }
        try {
            PageSnapshot snapshot = TextNodeExtractor.fromRoot(root, event.getPackageName());
            CaptureCoordinator.getInstance().onPageMaybeChanged(snapshot);
        } finally {
            root.recycle();
        }
    }

    @Override
    public void onInterrupt() {
        DebugLog.w("Accessibility service interrupted");
        CaptureCoordinator.getInstance().pauseFromAccessibilityInterrupt();
    }

    private String eventTypeName(int type) {
        if (type == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            return "TYPE_VIEW_SCROLLED";
        }
        if (type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            return "TYPE_WINDOW_CONTENT_CHANGED";
        }
        if (type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return "TYPE_WINDOW_STATE_CHANGED";
        }
        return String.valueOf(type);
    }
}

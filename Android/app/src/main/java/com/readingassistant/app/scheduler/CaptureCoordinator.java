package com.readingassistant.app.scheduler;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.readingassistant.app.ai.AiCallback;
import com.readingassistant.app.ai.AiClient;
import com.readingassistant.app.ai.SummaryParser;
import com.readingassistant.app.capture.MediaProjectionController;
import com.readingassistant.app.model.PageSnapshot;
import com.readingassistant.app.overlay.FloatingSummaryWindow;
import com.readingassistant.app.util.DebugLog;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CaptureCoordinator {
    private static final long DEBOUNCE_MS = 1000;
    private static final long NORMAL_COOLDOWN_MS = 5000;
    private static final long FAILURE_PAUSE_MS = 60000;
    private static final long DUPLICATE_SKIP_MS = 30000;

    private static final CaptureCoordinator INSTANCE = new CaptureCoordinator();

    private final Set<String> packageBlacklist = new HashSet<>(Arrays.asList(
            "com.android.settings",
            "com.miui.securitycenter",
            "com.tencent.mm",
            "com.eg.android.AlipayGphone"
    ));

    private Context context;
    private HandlerThread workerThread;
    private Handler workerHandler;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private AiClient aiClient;
    private MediaProjectionController mediaProjectionController;
    private FloatingSummaryWindow overlayWindow;
    private PageSnapshot pendingSnapshot;
    private boolean running;
    private boolean inCooldown;
    private long sequenceCounter;
    private long activeSequence;
    private String lastHash = "";
    private long lastHashAt;
    private int consecutiveFailures;

    private final Runnable debounceRunnable = new Runnable() {
        @Override
        public void run() {
            processPendingSnapshot();
        }
    };

    private CaptureCoordinator() {
    }

    public static CaptureCoordinator getInstance() {
        return INSTANCE;
    }

    public synchronized void init(Context appContext) {
        if (context == null) {
            context = appContext.getApplicationContext();
        }
        if (workerThread == null) {
            workerThread = new HandlerThread("reading-coordinator");
            workerThread.start();
            workerHandler = new Handler(workerThread.getLooper());
        }
        if (aiClient == null) {
            aiClient = new AiClient();
        }
        if (overlayWindow == null) {
            overlayWindow = FloatingSummaryWindow.get(context);
        }
        DebugLog.d("Coordinator initialized");
    }

    public void setMediaProjectionController(MediaProjectionController controller) {
        mediaProjectionController = controller;
        DebugLog.d("MediaProjectionController attached ready=" + (controller != null && controller.isReady()));
    }

    public void setRunning(boolean running) {
        this.running = running;
        DebugLog.d("Coordinator running=" + running);
        if (running) {
            inCooldown = false;
            consecutiveFailures = 0;
        }
    }

    public void onPageMaybeChanged(final PageSnapshot snapshot) {
        if (snapshot == null) {
            DebugLog.w("Page change ignored: snapshot is null");
            return;
        }
        if (!running) {
            DebugLog.w("Page change ignored: coordinator not running package=" + snapshot.getPackageName());
            return;
        }
        initIfPossible();
        workerHandler.post(new Runnable() {
            @Override
            public void run() {
                String rejectReason = rejectReason(snapshot);
                if (rejectReason != null) {
                    DebugLog.d("Page rejected reason=" + rejectReason
                            + " package=" + snapshot.getPackageName()
                            + " hash=" + DebugLog.shortHash(snapshot.getContentHash())
                            + " textLength=" + snapshot.getText().length());
                    return;
                }
                pendingSnapshot = snapshot;
                DebugLog.d("Page accepted debounceMs=" + DEBOUNCE_MS
                        + " package=" + snapshot.getPackageName()
                        + " source=" + snapshot.getSourceType()
                        + " hash=" + DebugLog.shortHash(snapshot.getContentHash())
                        + " textLength=" + snapshot.getText().length()
                        + " preview=" + DebugLog.preview(snapshot.getText()));
                workerHandler.removeCallbacks(debounceRunnable);
                workerHandler.postDelayed(debounceRunnable, DEBOUNCE_MS);
            }
        });
    }

    public void pauseFromAccessibilityInterrupt() {
        DebugLog.w("Coordinator pause from accessibility interrupt");
        if (aiClient != null) {
            aiClient.cancelRunning();
        }
    }

    public void shutdown() {
        DebugLog.d("Coordinator shutdown");
        running = false;
        if (aiClient != null) {
            aiClient.cancelRunning();
        }
        if (overlayWindow != null) {
            overlayWindow.dismiss();
        }
        if (workerHandler != null) {
            workerHandler.removeCallbacksAndMessages(null);
        }
    }

    private void initIfPossible() {
        if (context != null && workerHandler == null) {
            init(context);
        }
    }

    private boolean shouldAccept(PageSnapshot snapshot) {
        return rejectReason(snapshot) == null;
    }

    private String rejectReason(PageSnapshot snapshot) {
        if (inCooldown) {
            return "cooldown";
        }
        String packageName = snapshot.getPackageName();
        if (context != null && context.getPackageName().equals(packageName)) {
            return "self_package";
        }
        if (packageBlacklist.contains(packageName)) {
            return "blacklist";
        }
        String hash = snapshot.getContentHash();
        long now = System.currentTimeMillis();
        boolean accepted = hash == null
                || hash.isEmpty()
                || !hash.equals(lastHash)
                || now - lastHashAt > DUPLICATE_SKIP_MS;
        return accepted ? null : "duplicate_hash";
    }

    private void processPendingSnapshot() {
        PageSnapshot snapshot = pendingSnapshot;
        pendingSnapshot = null;
        if (snapshot == null) {
            DebugLog.w("Process pending skipped: snapshot is null");
            return;
        }
        String rejectReason = rejectReason(snapshot);
        if (rejectReason != null) {
            DebugLog.d("Process pending rejected reason=" + rejectReason
                    + " package=" + snapshot.getPackageName()
                    + " hash=" + DebugLog.shortHash(snapshot.getContentHash()));
            return;
        }
        DebugLog.d("Process pending package=" + snapshot.getPackageName()
                + " usefulText=" + snapshot.hasUsefulText()
                + " textLength=" + snapshot.getText().length()
                + " mediaReady=" + (mediaProjectionController != null && mediaProjectionController.isReady()));
        if (snapshot.hasUsefulText()) {
            requestTextSummary(snapshot);
        } else {
            requestScreenshotSummary(snapshot);
        }
    }

    private void requestTextSummary(final PageSnapshot snapshot) {
        if (aiClient == null) {
            DebugLog.w("Text summary skipped: AiClient is null");
            return;
        }
        final long sequence = nextSequence();
        DebugLog.d("AI text request start seq=" + sequence
                + " package=" + snapshot.getPackageName()
                + " textLength=" + snapshot.getText().length()
                + " hash=" + DebugLog.shortHash(snapshot.getContentHash()));
        aiClient.cancelRunning();
        aiClient.requestTextSummary(snapshot.getText(), snapshot.getPreview(), new AiCallback() {
            @Override
            public void onSuccess(final String summary) {
                DebugLog.d("AI text request success seq=" + sequence
                        + " summary=" + DebugLog.preview(summary));
                if (SummaryParser.isInsufficient(summary)
                        && mediaProjectionController != null
                        && mediaProjectionController.isReady()) {
                    DebugLog.d("AI text result is insufficient, fallback to screenshot seq=" + sequence);
                    requestScreenshotSummary(snapshot);
                    return;
                }
                handleAiSuccess(sequence, snapshot, summary);
            }

            @Override
            public void onFailure(String message) {
                DebugLog.w("AI text request failure seq=" + sequence
                        + " message=" + message
                        + " fallbackToScreenshot=" + (mediaProjectionController != null && mediaProjectionController.isReady()));
                if (mediaProjectionController != null && mediaProjectionController.isReady()) {
                    requestScreenshotSummary(snapshot);
                } else {
                    handleAiFailure(sequence);
                }
            }
        });
    }

    private void requestScreenshotSummary(final PageSnapshot snapshot) {
        if (mediaProjectionController == null || !mediaProjectionController.isReady() || aiClient == null) {
            DebugLog.w("Screenshot summary skipped mediaReady="
                    + (mediaProjectionController != null && mediaProjectionController.isReady())
                    + " aiReady=" + (aiClient != null)
                    + " activeSeq=" + activeSequence);
            handleAiFailure(activeSequence);
            return;
        }
        final long sequence = nextSequence();
        DebugLog.d("Screenshot request start seq=" + sequence
                + " package=" + snapshot.getPackageName()
                + " originalTextLength=" + snapshot.getText().length());
        aiClient.cancelRunning();
        if (overlayWindow != null) {
            overlayWindow.hideForCapture();
        }
        mediaProjectionController.captureLatestBase64(new MediaProjectionController.ScreenshotCallback() {
            @Override
            public void onSuccess(String base64Jpeg) {
                if (sequence != activeSequence) {
                    DebugLog.d("Screenshot ignored because sequence expired seq=" + sequence
                            + " activeSeq=" + activeSequence);
                    return;
                }
                DebugLog.d("Screenshot captured seq=" + sequence
                        + " base64Length=" + (base64Jpeg == null ? 0 : base64Jpeg.length()));
                aiClient.requestImageSummary(base64Jpeg, snapshot.getPreview(), new AiCallback() {
                    @Override
                    public void onSuccess(String summary) {
                        DebugLog.d("AI image request success seq=" + sequence
                                + " summary=" + DebugLog.preview(summary));
                        handleAiSuccess(sequence, snapshot, summary);
                    }

                    @Override
                    public void onFailure(String message) {
                        DebugLog.w("AI image request failure seq=" + sequence
                                + " message=" + message);
                        handleAiFailure(sequence);
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                DebugLog.w("Screenshot capture failure seq=" + sequence + " message=" + message);
                handleAiFailure(sequence);
            }
        });
    }

    private long nextSequence() {
        sequenceCounter++;
        activeSequence = sequenceCounter;
        return activeSequence;
    }

    private void handleAiSuccess(final long sequence, final PageSnapshot snapshot, final String summary) {
        workerHandler.post(new Runnable() {
            @Override
            public void run() {
                if (sequence != activeSequence) {
                    DebugLog.d("AI success ignored because sequence expired seq=" + sequence
                            + " activeSeq=" + activeSequence);
                    return;
                }
                consecutiveFailures = 0;
                lastHash = snapshot.getContentHash();
                lastHashAt = System.currentTimeMillis();
                DebugLog.d("AI success accepted seq=" + sequence
                        + " package=" + snapshot.getPackageName()
                        + " hash=" + DebugLog.shortHash(snapshot.getContentHash())
                        + " summary=" + DebugLog.preview(summary));
                if (overlayWindow != null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            overlayWindow.showSummary(summary);
                        }
                    });
                }
                enterCooldown(NORMAL_COOLDOWN_MS);
            }
        });
    }

    private void handleAiFailure(final long sequence) {
        workerHandler.post(new Runnable() {
            @Override
            public void run() {
                if (sequence != activeSequence) {
                    DebugLog.d("AI failure ignored because sequence expired seq=" + sequence
                            + " activeSeq=" + activeSequence);
                    return;
                }
                consecutiveFailures++;
                DebugLog.w("AI failure accepted seq=" + sequence
                        + " consecutiveFailures=" + consecutiveFailures);
                enterCooldown(consecutiveFailures >= 3 ? FAILURE_PAUSE_MS : NORMAL_COOLDOWN_MS);
            }
        });
    }

    private void enterCooldown(long durationMs) {
        inCooldown = true;
        DebugLog.d("Enter cooldown durationMs=" + durationMs);
        workerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                inCooldown = false;
                DebugLog.d("Cooldown finished");
            }
        }, durationMs);
    }
}

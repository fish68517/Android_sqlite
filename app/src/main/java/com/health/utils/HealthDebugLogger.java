package com.Health.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class HealthDebugLogger {

    private static final String DEFAULT_TAG = "HealthDebug";
    private static final SimpleDateFormat FILE_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat LINE_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private static volatile Context appContext;
    private static volatile boolean initialized;

    private HealthDebugLogger() {
    }

    public static void init(Context context) {
        if (context == null) {
            return;
        }
        appContext = context.getApplicationContext();
        if (initialized) {
            return;
        }
        initialized = true;
        cleanupOldLogs();
        i(DEFAULT_TAG, buildStartupMessage());
    }

    public static void d(String tag, String message) {
        write("DEBUG", tag, message, null);
    }

    public static void i(String tag, String message) {
        write("INFO", tag, message, null);
    }

    public static void w(String tag, String message) {
        write("WARN", tag, message, null);
    }

    public static void e(String tag, String message) {
        write("ERROR", tag, message, null);
    }

    public static void e(String tag, String message, Throwable throwable) {
        write("ERROR", tag, message, throwable);
    }

    public static String getLogDirectoryPath(Context context) {
        File dir = resolveLogDir(context == null ? appContext : context.getApplicationContext());
        return dir == null ? "" : dir.getAbsolutePath();
    }

    private static void write(String level, String tag, String message, Throwable throwable) {
        String safeTag = tag == null ? DEFAULT_TAG : tag;
        String safeMessage = message == null ? "" : message;
        String line = LINE_DATE_FORMAT.format(new Date())
                + " " + level
                + "/" + safeTag
                + " [" + Thread.currentThread().getName() + "] "
                + safeMessage;

        if ("ERROR".equals(level)) {
            Log.e(safeTag, safeMessage, throwable);
        } else if ("WARN".equals(level)) {
            Log.w(safeTag, safeMessage, throwable);
        } else if ("DEBUG".equals(level)) {
            Log.d(safeTag, safeMessage, throwable);
        } else {
            Log.i(safeTag, safeMessage, throwable);
        }

        Context context = appContext;
        if (context == null) {
            return;
        }

        EXECUTOR.execute(() -> {
            File dir = resolveLogDir(context);
            if (dir == null) {
                return;
            }
            File logFile = new File(dir, "health_debug_" + FILE_DATE_FORMAT.format(new Date()) + ".log");
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(line);
                writer.write('\n');
                if (throwable != null) {
                    writer.write(stackTraceToString(throwable));
                    writer.write('\n');
                }
            } catch (Exception ignored) {
            }
        });
    }

    private static File resolveLogDir(Context context) {
        if (context == null) {
            return null;
        }
        File baseDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (baseDir == null) {
            baseDir = context.getFilesDir();
        }
        File logDir = new File(baseDir, "health_logs");
        if (!logDir.exists() && !logDir.mkdirs()) {
            return null;
        }
        return logDir;
    }

    private static void cleanupOldLogs() {
        Context context = appContext;
        if (context == null) {
            return;
        }
        EXECUTOR.execute(() -> {
            File dir = resolveLogDir(context);
            if (dir == null) {
                return;
            }
            File[] files = dir.listFiles();
            if (files == null) {
                return;
            }
            long keepAfter = System.currentTimeMillis() - 14L * 24L * 60L * 60L * 1000L;
            for (File file : files) {
                if (file != null && file.isFile() && file.lastModified() < keepAfter) {
                    file.delete();
                }
            }
        });
    }

    private static String buildStartupMessage() {
        Context context = appContext;
        String versionName = "unknown";
        long versionCode = -1L;
        if (context != null) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                versionName = packageInfo.versionName;
                versionCode = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                        ? packageInfo.getLongVersionCode()
                        : packageInfo.versionCode;
            } catch (Exception ignored) {
            }
        }
        return "Logger initialized. versionName=" + versionName
                + ", versionCode=" + versionCode
                + ", brand=" + Build.BRAND
                + ", model=" + Build.MODEL
                + ", sdk=" + Build.VERSION.SDK_INT
                + ", android=" + Build.VERSION.RELEASE
                + ", logDir=" + getLogDirectoryPath(context);
    }

    private static String stackTraceToString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }
}

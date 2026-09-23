package com.personal.diary.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class TimeUtil {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    private TimeUtil() {
    }

    public static String now() {
        return FORMAT.format(new Date());
    }
}

package com.Health;

import android.app.Application;

import com.Health.health.HealthAlertNotifier;
import com.Health.health.HealthMonitorScheduler;
import com.Health.local.LocalHealthRepository;
import com.Health.model.User;
import com.Health.utils.HealthDebugLogger;

/**
 * 应用程序主类
 * 
 * @author Health
 */
public class HealthApplication extends Application {

    private static HealthApplication instance;
    public static User curUser;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        HealthDebugLogger.init(this);
        LocalHealthRepository.getInstance(this).ensureSeedData();
        HealthAlertNotifier.ensureChannel(this);
        HealthMonitorScheduler.schedulePeriodic(this);
    }

    public static HealthApplication getInstance() {
        return instance;
    }

    public static void setUser(User user) {
        curUser = user;
    }
}


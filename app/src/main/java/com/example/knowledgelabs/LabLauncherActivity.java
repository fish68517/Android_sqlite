package com.example.knowledgelabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.knowledgelabs.ch01.HelloStartActivity;
import com.example.knowledgelabs.ch02.KotlinToolboxActivity;
import com.example.knowledgelabs.ch03.ActivityNotebookActivity;
import com.example.knowledgelabs.ch04.ComponentGalleryActivity;
import com.example.knowledgelabs.ch05.FragmentNewsActivity;
import com.example.knowledgelabs.ch06.BroadcastSessionActivity;
import com.example.knowledgelabs.ch07.LocalVaultActivity;
import com.example.knowledgelabs.ch08.ContactViewerActivity;
import com.example.knowledgelabs.ch09.MediaCenterActivity;
import com.example.knowledgelabs.ch10.BackgroundDownloaderActivity;
import com.example.knowledgelabs.ch11.NetworkJsonClientActivity;
import com.example.knowledgelabs.ch15.WeatherLiteActivity;

public final class LabLauncherActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, targetActivity(BuildConfig.LAB_ID)));
        finish();
    }

    private Class<? extends Activity> targetActivity(String labId) {
        switch (labId) {
            case "ch02":
                return KotlinToolboxActivity.class;
            case "ch03":
                return ActivityNotebookActivity.class;
            case "ch04":
                return ComponentGalleryActivity.class;
            case "ch05":
                return FragmentNewsActivity.class;
            case "ch06":
                return BroadcastSessionActivity.class;
            case "ch07":
                return LocalVaultActivity.class;
            case "ch08":
                return ContactViewerActivity.class;
            case "ch09":
                return MediaCenterActivity.class;
            case "ch10":
                return BackgroundDownloaderActivity.class;
            case "ch11":
                return NetworkJsonClientActivity.class;
            case "ch15":
                return WeatherLiteActivity.class;
            default:
                return HelloStartActivity.class;
        }
    }
}


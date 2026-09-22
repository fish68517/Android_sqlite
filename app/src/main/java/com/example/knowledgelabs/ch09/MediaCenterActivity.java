package com.example.knowledgelabs.ch09;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.knowledgelabs.R;

public class MediaCenterActivity extends Activity {
    private static final int REQUEST_CAMERA = 901;
    private static final int REQUEST_GALLERY = 902;
    private static final int REQUEST_NOTIFICATION = 903;
    private static final String CHANNEL_ID = "media_demo";
    private ImageView imageView;
    private VideoView videoView;
    private ToneGenerator toneGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_center);
        imageView = findViewById(R.id.ivMediaImage);
        videoView = findViewById(R.id.videoView);
        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 80);
        createNotificationChannel();

        findViewById(R.id.btnTakePhoto).setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CAMERA);
            } else {
                Toast.makeText(this, "设备未提供相机应用", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btnPickImage).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    .setType("image/*");
            startActivityForResult(intent, REQUEST_GALLERY);
        });
        findViewById(R.id.btnNotify).setOnClickListener(v -> ensureNotificationPermission());
        findViewById(R.id.btnPlayTone).setOnClickListener(v ->
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 500));
        findViewById(R.id.btnPlayVideo).setOnClickListener(v -> playSampleVideo());
    }

    private void ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION);
        } else {
            sendNotification();
        }
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "媒体实验通知", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("第9章 Notification Channel 演示");
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }

    private void sendNotification() {
        android.app.Notification notification = new android.app.Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("MediaCenter 实验")
                .setContentText("相机、相册、音频和视频组件已经准备好")
                .setStyle(new android.app.Notification.BigTextStyle()
                        .bigText("这是一条由 Notification Channel 管理的媒体实验通知。可继续测试拍照、相册、提示音和 VideoView。"))
                .setAutoCancel(true)
                .build();
        getSystemService(NotificationManager.class).notify(9, notification);
    }

    private void playSampleVideo() {
        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);
        videoView.setMediaController(controller);
        videoView.setVideoURI(Uri.parse(
                "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"));
        videoView.setOnPreparedListener(MediaPlayer::start);
        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(this, "示例视频加载失败，请检查网络", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;
        if (requestCode == REQUEST_CAMERA) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = extras == null ? null : (Bitmap) extras.get("data");
            if (bitmap != null) imageView.setImageBitmap(bitmap);
        } else if (requestCode == REQUEST_GALLERY && data.getData() != null) {
            Uri uri = data.getData();
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imageView.setImageURI(uri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendNotification();
        }
    }

    @Override
    protected void onDestroy() {
        videoView.stopPlayback();
        toneGenerator.release();
        super.onDestroy();
    }
}

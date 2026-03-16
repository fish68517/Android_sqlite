package com.hakimi.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hakimi.HakimiApplication;
import com.hakimi.R;
import com.hakimi.local.LocalHealthRepository;
import com.hakimi.local.LocalResult;
import com.hakimi.model.Diary;
import com.hakimi.utils.SharedPrefManager;

public class WriteDiaryActivity extends AppCompatActivity {

    private EditText etDiaryContent;
    private RadioGroup rgMood;
    private Button btnSaveDiary;
    private LocalHealthRepository localRepository;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_diary);

        localRepository = LocalHealthRepository.getInstance(this);
        sharedPrefManager = SharedPrefManager.getInstance();

        etDiaryContent = findViewById(R.id.et_diary_content);
        rgMood = findViewById(R.id.rg_mood);
        btnSaveDiary = findViewById(R.id.btn_save_diary);

        findViewById(R.id.btn_close_diary).setOnClickListener(v -> finish());
        btnSaveDiary.setOnClickListener(v -> saveDiary());
    }

    private void saveDiary() {
        String content = etDiaryContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入日记内容", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId = getCurrentUserId();
        if (userId == null || userId <= 0) {
            Toast.makeText(this, "未获取到当前用户", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSaveDiary.setEnabled(false);
        btnSaveDiary.setText("保存中...");

        LocalResult<Diary> result = localRepository.createDiary(userId, content, getSelectedMood());

        btnSaveDiary.setEnabled(true);
        btnSaveDiary.setText("保存");

        if (result.isSuccess()) {
            Toast.makeText(this, "日记保存成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
            return;
        }

        Toast.makeText(this,
                TextUtils.isEmpty(result.getMessage()) ? "日记保存失败" : result.getMessage(),
                Toast.LENGTH_SHORT).show();
    }

    private int getSelectedMood() {
        int checkedId = rgMood.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_sad) {
            return 1;
        }
        if (checkedId == R.id.rb_normal) {
            return 2;
        }
        return 3;
    }

    private Long getCurrentUserId() {
        long userId = sharedPrefManager.getUserId();
        if (userId > 0) {
            return userId;
        }
        if (HakimiApplication.curUser != null && HakimiApplication.curUser.getId() != null) {
            return HakimiApplication.curUser.getId();
        }
        return null;
    }
}

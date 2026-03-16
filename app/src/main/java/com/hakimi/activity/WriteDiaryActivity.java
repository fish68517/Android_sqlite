package com.hakimi.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hakimi.HakimiApplication;
import com.hakimi.R;
import com.hakimi.model.ApiResponse;
import com.hakimi.model.Diary;
import com.hakimi.network.ApiService;
import com.hakimi.network.RetrofitClient;
import com.hakimi.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteDiaryActivity extends AppCompatActivity {

    private EditText etDiaryContent;
    private RadioGroup rgMood;
    private Button btnSaveDiary;
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_diary);

        apiService = RetrofitClient.getInstance().getApiService();
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
            Toast.makeText(this, "\u8bf7\u8f93\u5165\u65e5\u8bb0\u5185\u5bb9", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId = getCurrentUserId();
        if (userId == null || userId <= 0) {
            Toast.makeText(this, "\u672a\u83b7\u53d6\u5230\u5f53\u524d\u7528\u6237", Toast.LENGTH_SHORT).show();
            return;
        }

        Diary diary = new Diary();
        diary.setUserId(userId);
        diary.setContent(content);
        diary.setMood(getSelectedMood());

        btnSaveDiary.setEnabled(false);
        btnSaveDiary.setText("\u4fdd\u5b58\u4e2d...");

        apiService.createDiary(diary).enqueue(new Callback<ApiResponse<Diary>>() {
            @Override
            public void onResponse(Call<ApiResponse<Diary>> call, Response<ApiResponse<Diary>> response) {
                btnSaveDiary.setEnabled(true);
                btnSaveDiary.setText("\u4fdd\u5b58");
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(WriteDiaryActivity.this, "\u65e5\u8bb0\u4fdd\u5b58\u6210\u529f", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(WriteDiaryActivity.this, "\u65e5\u8bb0\u4fdd\u5b58\u5931\u8d25", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Diary>> call, Throwable t) {
                btnSaveDiary.setEnabled(true);
                btnSaveDiary.setText("\u4fdd\u5b58");
                Toast.makeText(WriteDiaryActivity.this, "\u7f51\u7edc\u9519\u8bef: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
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

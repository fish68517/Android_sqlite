package com.personal.diary;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.personal.diary.model.Moment;
import com.personal.diary.ui.RecordItem;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MomentFragment extends BaseRecordsFragment {
    private final Map<Long, Moment> momentMap = new HashMap<>();
    private byte[] selectedImageData;
    private ImageView selectedImagePreview;
    private TextView selectedImageText;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImagePicked);

    @Override
    protected List<RecordItem> loadItems() {
        momentMap.clear();
        List<RecordItem> items = new ArrayList<>();
        for (Moment moment : db.getMoments(session.getUserId())) {
            momentMap.put(moment.id, moment);
            RecordItem item = new RecordItem(
                    moment.id,
                    moment.userId,
                    "moment",
                    safe(moment.author) + " 的幸福瞬间",
                    moment.content,
                    moment.createdAt + "  点赞 " + moment.likeCount + "  收藏 " + moment.favoriteCount);
            item.imageData = moment.imageData;
            item.primaryAction = moment.liked ? "取消点赞" : "点赞";
            item.secondaryAction = moment.favorited ? "取消收藏" : "收藏";
            item.showDelete = moment.userId == session.getUserId();
            items.add(item);
        }
        return items;
    }

    @Override
    protected String emptyMessage() {
        return "暂无幸福瞬间，点击右下角发布";
    }

    @Override
    protected void onAddClick() {
        selectedImageData = null;
        LinearLayout form = DialogUtil.form(requireContext());
        EditText content = DialogUtil.input(requireContext(), form, "记录一个幸福瞬间", "", 4);

        Button pickButton = new Button(requireContext());
        pickButton.setText("从相册选择图片");
        pickButton.setTextColor(requireContext().getColor(R.color.white));
        pickButton.setBackgroundResource(R.drawable.bg_button);
        form.addView(pickButton);

        selectedImageText = new TextView(requireContext());
        selectedImageText.setText("未选择图片");
        selectedImageText.setTextColor(requireContext().getColor(R.color.diary_text_soft));
        form.addView(selectedImageText);

        selectedImagePreview = new ImageView(requireContext());
        selectedImagePreview.setAdjustViewBounds(true);
        selectedImagePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        selectedImagePreview.setVisibility(View.GONE);
        form.addView(selectedImagePreview, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, DialogUtil.dp(requireContext(), 180)));

        pickButton.setOnClickListener(v -> imagePicker.launch("image/*"));

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("发布幸福瞬间")
                .setView(form)
                .setPositiveButton("发布", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String value = content.getText().toString().trim();
            if (value.isEmpty()) {
                toast("内容不能为空");
                return;
            }
            db.saveMoment(session.getUserId(), value, "", selectedImageData);
            dialog.dismiss();
            refresh();
        }));
        dialog.show();
    }

    private void onImagePicked(Uri uri) {
        if (uri == null) {
            return;
        }
        try {
            selectedImageData = compressImage(uri);
            if (selectedImageData == null || selectedImageData.length == 0) {
                toast("图片读取失败");
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(selectedImageData, 0, selectedImageData.length);
            if (selectedImagePreview != null) {
                selectedImagePreview.setImageBitmap(bitmap);
                selectedImagePreview.setVisibility(View.VISIBLE);
            }
            if (selectedImageText != null) {
                selectedImageText.setText("已选择图片，已压缩后准备保存到 SQLite");
            }
        } catch (Exception e) {
            toast("图片读取失败：" + e.getMessage());
        }
    }

    private byte[] compressImage(Uri uri) throws Exception {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream input = requireContext().getContentResolver().openInputStream(uri)) {
            BitmapFactory.decodeStream(input, null, bounds);
        }
        int sampleSize = 1;
        int maxSide = Math.max(bounds.outWidth, bounds.outHeight);
        while (maxSide / sampleSize > 1200) {
            sampleSize *= 2;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        Bitmap bitmap;
        try (InputStream input = requireContext().getContentResolver().openInputStream(uri)) {
            bitmap = BitmapFactory.decodeStream(input, null, options);
        }
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 78, output);
        return output.toByteArray();
    }

    @Override
    public void onItemClick(RecordItem item) {
        Moment moment = momentMap.get(item.id);
        if (moment == null) {
            return;
        }
        LinearLayout layout = DialogUtil.form(requireContext());
        TextView text = new TextView(requireContext());
        text.setText("发布者：" + safe(moment.author) + "\n\n" + moment.content);
        text.setTextColor(requireContext().getColor(R.color.diary_text));
        layout.addView(text);
        if (moment.imageData != null && moment.imageData.length > 0) {
            ImageView imageView = new ImageView(requireContext());
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(moment.imageData, 0, moment.imageData.length);
            imageView.setImageBitmap(bitmap);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, DialogUtil.dp(requireContext(), 260));
            params.setMargins(0, DialogUtil.dp(requireContext(), 12), 0, 0);
            layout.addView(imageView, params);
        }
        new AlertDialog.Builder(requireContext())
                .setTitle("幸福瞬间")
                .setView(layout)
                .setPositiveButton("知道了", null)
                .show();
    }

    @Override
    public void onPrimary(RecordItem item) {
        db.toggleLike(session.getUserId(), "moment", item.id);
        refresh();
    }

    @Override
    public void onSecondary(RecordItem item) {
        db.toggleFavorite(session.getUserId(), "moment", item.id);
        refresh();
    }

    @Override
    public void onDelete(RecordItem item) {
        DialogUtil.confirm(requireContext(), "删除瞬间", "确定删除这条幸福瞬间吗？", () -> {
            db.deleteMoment(item.id, session.getUserId());
            refresh();
        });
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "用户" : value;
    }
}

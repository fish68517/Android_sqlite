package com.archive.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.archive.app.MyApplication;
import com.archive.app.model.Category;
import com.archive.app.model.Note;
import com.archive.app.mvp.NoteEditorContract;
import com.archive.app.mvp.NoteEditorPresenter;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NoteEditorActivity extends AppCompatActivity implements NoteEditorContract.View {

    private static final int PICK_IMAGE_REQUEST = 1;

    private NoteEditorContract.Presenter presenter;
    private EditText titleEditText;
    private EditText contentEditText;
    private Spinner categorySpinner;
    private ImageView imagePreview;
    private Button selectImageButton;
    private Button saveButton;

    private long noteId = -1;
    private List<Category> categoryList = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        Toolbar toolbar = findViewById(R.id.toolbar_note_editor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        presenter = new NoteEditorPresenter(this);
        presenter.attachView(this);

        titleEditText = findViewById(R.id.edit_note_title);
        contentEditText = findViewById(R.id.edit_note_content);
        categorySpinner = findViewById(R.id.spinner_category);
        imagePreview = findViewById(R.id.image_preview);
        selectImageButton = findViewById(R.id.btn_select_image);
        saveButton = findViewById(R.id.btn_save_note);

        noteId = getIntent().getLongExtra("note_id", -1);

        if (noteId == -1) {
            getSupportActionBar().setTitle("创建新笔记");
        } else {
            getSupportActionBar().setTitle("编辑笔记");
        }

        setupCategorySpinner();
        
        selectImageButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveNote());

        if (noteId != -1) {
            presenter.loadNote(noteId);
        }
        presenter.loadCategories(getUserId());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupCategorySpinner() {
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imagePreview.setImageBitmap(bitmap);
                imagePreview.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveNote() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        long selectedCategoryId = -1;
        int selectedPosition = categorySpinner.getSelectedItemPosition();
        if(selectedPosition >= 0 && selectedPosition < categoryList.size()){
            selectedCategoryId = categoryList.get(selectedPosition).getId();
        }

        byte[] imageBytes = null;
        if(imagePreview.getDrawable() != null){
            Bitmap bitmap = ((BitmapDrawable) imagePreview.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            imageBytes = baos.toByteArray();
        }

        if (noteId == -1) {
            presenter.saveNote(title, content, selectedCategoryId, imageBytes);
        } else {
            presenter.updateNote(noteId, title, content, selectedCategoryId, imageBytes);
        }
    }

    @Override
    public void showNoteDetails(Note note) {
        titleEditText.setText(note.getTitle());
        contentEditText.setText(note.getContent());
        if(note.getImage() != null){
            Glide.with(this).load(note.getImage()).into(imagePreview);
            imagePreview.setVisibility(View.VISIBLE);
        }
        setSpinnerSelection(note.getCategoryId());
    }

    @Override
    public void showCategories(List<Category> categories) {
        this.categoryList = categories;
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }
        categoryAdapter.clear();
        categoryAdapter.addAll(categoryNames);
        categoryAdapter.notifyDataSetChanged();

        if (noteId != -1) {
           presenter.loadNote(noteId); // Reload note to set spinner after categories are loaded
        }
    }
    
    private void setSpinnerSelection(long categoryId) {
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == categoryId) {
                categorySpinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onNoteSaved() {
        Toast.makeText(this, "笔记已保存", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public long getUserId() {
        return MyApplication.curUser.getId();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
} 
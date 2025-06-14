package com.example.orderfood.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DataBaseHelper;
import com.example.orderfood.R;
import com.example.orderfood.adapter.PostAdapter;
import com.example.orderfood.model.Post;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> postList = new ArrayList();
    private AlertDialog alertDialog;
    private DataBaseHelper db;
    private Uri imageUri;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        // 初始化发布按钮
        MaterialButton btnPublish = view.findViewById(R.id.btn_publish);
        TextView textView = view.findViewById(R.id.tv_title);
        TextView textView1 = view.findViewById(R.id.tv_sub_title);

        TextView textView2 = view.findViewById(R.id.tv_sub_title_2);
        textView2.setOnClickListener(v -> {
            // 跳转到��子列表
            // 过滤出收藏的帖子
            List<Post> favoritePosts = new ArrayList<>();
            for (Post post : postList) {
                if (post.isFavorite()) {
                    favoritePosts.add(post);
                }
            }
            // 刷新recyclerView
            adapter.setPostList(favoritePosts);
            adapter.notifyDataSetChanged();

            textView.setTextColor(Color.GRAY);
            textView1.setTextColor(Color.GRAY);
            textView2.setTextColor(Color.BLACK);

        });

        textView.setOnClickListener(v -> {
            // 跳转到��子列表
            // 刷新recyclerView
            adapter.setPostList(postList);
            adapter.notifyDataSetChanged();


            textView.setTextColor(Color.BLACK);
            textView1.setTextColor(Color.GRAY);
            textView2.setTextColor(Color.GRAY);

        });
        btnPublish.setOnClickListener(v -> {
            // 弹出发布对话框
            showPublishPostDialog();
        });

        db = new DataBaseHelper(getActivity());

        // 初始化帖子列表
        recyclerView = view.findViewById(R.id.recyclerView_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadPosts();
        adapter = new PostAdapter(postList,getActivity());
        recyclerView.setAdapter(adapter);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPosts();
        adapter.notifyDataSetChanged();
    }

    /**
     * 加载帖子列表
     */

    private void loadPosts() {
      /*  postList.add(new Post("明天会更好", "今天 11:10 发布 · 白云山景", "今天青龙山风景真美啊", R.drawable.ic_landscape));
        postList.add(new Post("出发旅行", "昨天 9:00 发布 · 日出山顶", "日出非常震撼！", R.drawable.ic_landscape));*/

        postList.clear();
        List<Post> posts = db.getAllPosts();
        for (Post post : posts) {
            postList.add(post);
        }

    }

    private void showPublishPostDialog() {
        // 创建自定义对话框
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(R.layout.dialog_publish_post)
                .setPositiveButton("发布", null) // 先设置为 null，稍后再设置监听
                .setNegativeButton("取消", (dialog1, which) -> {
                    Toast.makeText(getActivity(), "取消发布", Toast.LENGTH_SHORT).show();
                })
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            alertDialog = (AlertDialog) dialogInterface;
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                // 获取输入内容
                EditText editTextTitle = alertDialog.findViewById(R.id.editTextTitle);
                EditText editTextContent = alertDialog.findViewById(R.id.editTextContent);
                EditText editTextLocation = alertDialog.findViewById(R.id.editTextLocation);
                ImageView imageViewPreview = alertDialog.findViewById(R.id.imageViewPreview);

                String title = editTextTitle.getText().toString();
                String content = editTextContent.getText().toString();
                String location = editTextLocation.getText().toString();

                // 检查输入是否有效
                if (title.isEmpty() || content.isEmpty() || location.isEmpty()) {
                    Toast.makeText(getActivity(), "请填写所有字段", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 发布帖子逻辑
                Toast.makeText(getActivity(), "发布成功", Toast.LENGTH_SHORT).show();
                long postId = db.addPost(new Post(title, content, location, R.drawable.travel_qinglongshan));
                // 存储 imageUri 到数据库
                db.addImageUri((int) postId, imageUri.toString()); // 将 imageUri 转换为字符串存入数据库

                // 刷新列表
                loadPosts();
                adapter.notifyDataSetChanged();
                alertDialog.dismiss();
            });
        });

        dialog.show();

        // 处理图片上传
        Button buttonUploadImage = dialog.findViewById(R.id.buttonUploadImage);
        buttonUploadImage.setOnClickListener(v -> {
            // 检查权限并选择图片
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                openGallery();
            }
        });
    }

    // 打开相册选择图片
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    // 处理选择的图片
    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();


            // 通过 ContentProvider 读取图片
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                // 显示图片预览
                ImageView imageViewPreview = alertDialog.findViewById(R.id.imageViewPreview);
                imageViewPreview.setImageBitmap(bitmap);
                imageViewPreview.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Log.e("TAG", "Error loading image: " + e.getMessage());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(getActivity(), "权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

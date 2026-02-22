package com.example.healthdietapp.adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.models.Post;
import com.example.healthdietapp.utils.AnimationUtils;

import java.util.List;

/**
 * PostAdapter - Adapter for displaying posts in RecyclerView
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;
    private OnPostClickListener listener;

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    public PostAdapter(List<Post> posts, OnPostClickListener listener) {
        this.posts = posts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post, listener);

        // Apply list item enter animation with staggered delay
        AnimationUtils.applyListItemEnterAnimationWithDelay(holder.itemView, position * 50);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updatePosts(List<Post> newPosts) {
        this.posts = newPosts;
        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView postTitle;
        private TextView postContent;
        private TextView postLikes;
        private ImageView postImage;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            postContent = itemView.findViewById(R.id.postContent);
            postLikes = itemView.findViewById(R.id.postLikes);
            postImage = itemView.findViewById(R.id.postImage);
        }

        void bind(Post post, OnPostClickListener listener) {
            postTitle.setText(post.getTitle());
            postContent.setText(post.getContent());
            postLikes.setText(String.valueOf(post.getLikes()) + " 点赞");
            itemView.setOnClickListener(v -> listener.onPostClick(post));

            String imagesStr = post.getImages();
            Log.d("PostAdapter", "Raw images string: " + imagesStr);

            // 设置默认兜底图片，如果没有专门的默认图，这里使用系统默认的灰色背景占位

            int defaultImageRes = R.drawable.ic_default_image;

            if (imagesStr != null && !imagesStr.trim().isEmpty()) {
                // 1. 截取逗号分隔的第一张图片
                String firstImage = imagesStr.split(",")[0].trim();
                Log.d("PostAdapter", "Loading first image: " + firstImage);

                // 2. 判断是否是从相册选择的 URI 或绝对路径
                if (firstImage.startsWith("content://") || firstImage.startsWith("file://") || firstImage.startsWith("/")) {
                    try {
                        postImage.setImageURI(Uri.parse(firstImage));
                    } catch (Exception e) {
                        Log.e("PostAdapter", "加载相册图片失败: " + firstImage, e);
                        postImage.setImageResource(defaultImageRes);
                    }
                } else {
                    // 3. 处理本地 drawable 模拟数据 (例如 "img_post1_a.jpg")
                    String drawableName = firstImage;
                    // 移除文件后缀名 (如 .jpg 或 .png)
                    if (drawableName.contains(".")) {
                        drawableName = drawableName.substring(0, drawableName.lastIndexOf("."));
                    }

                    // 动态获取 drawable 的资源 ID
                    int resId = itemView.getContext().getResources().getIdentifier(
                            drawableName, "drawable", itemView.getContext().getPackageName());

                    if (resId != 0) {
                        // 找到了对应的 drawable 图片
                        postImage.setImageResource(resId);
                    } else {
                        // 找不到对应的资源文件，加载默认图片
                        Log.w("PostAdapter", "在 drawable 目录中找不到图片: " + drawableName);
                        postImage.setImageResource(defaultImageRes);
                    }
                }
            } else {
                // 如果数据库中 images 字段为空，直接加载默认图片
                postImage.setImageResource(defaultImageRes);
            }
        }
    }
}
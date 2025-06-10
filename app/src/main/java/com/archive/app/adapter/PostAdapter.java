package com.archive.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.model.Post;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final Context context;
    private final List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.contentTextView.setText(post.getContent());
        holder.timestampTextView.setText(post.getCreatedAt());

        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.imageView.setVisibility(View.VISIBLE);
            // 在实际应用中，您会使用Glide或Picasso等库来加载图片
            // holder.imageView.setImageResource(R.drawable.placeholder_image);
            // 从数据库获取图片名称字符串
            String imageName = post.getImageUrl();
            int imageResId = 0;
            if (imageName != null && !imageName.isEmpty()) {
                // 移除文件扩展名
                String drawableName = imageName.substring(0, imageName.lastIndexOf('.'));
                // 动态获取资源ID
                imageResId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
            }

            Glide.with(context)
                    .load(imageResId) // 加载获取到的资源ID
                    .error(R.drawable.attraction_image4) // 如果ID为0或加载失败，显示默认图片
                    .into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView contentTextView;
        TextView timestampTextView;
        ImageView imageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.post_content);
            timestampTextView = itemView.findViewById(R.id.post_timestamp);
            imageView = itemView.findViewById(R.id.post_image);
        }
    }
} 
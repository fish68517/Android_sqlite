package com.example.orderfood.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.DBMysqlHelper;
import com.example.orderfood.R;
import com.example.orderfood.model.Post;

import java.io.IOException;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private final Context context;
    private List<Post> postList;
    private SharedPreferences sp;

    private DBMysqlHelper db;

    public PostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
        db =  DBMysqlHelper.getInstance(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.username.setText(post.getUsername());
        holder.timestamp.setText(post.getTimestamp());
        holder.content.setText(post.getContent());

        try {
            String imageUri = db.getImageUrisByPostId(post.getPostId()).get(0);
            System.out.println("imageUri: " + imageUri);
            if (imageUri!=null) {
                loadImageFromUri(imageUri,holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.travel_qinglongshan);
            }
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.travel_qinglongshan);
            e.printStackTrace();
        }

        // 将收藏按钮的点击事件绑定到favoriteListener
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 收藏按钮点击事件
                if (post.isFavorite()) {
                    // 取消收藏
                    post.setFavorite(false);
                    db.updatePostFavorite(post.getPostId(),0);
                    holder.favoriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                } else {
                    // 收藏
                    post.setFavorite(true);
                    db.updatePostFavorite(post.getPostId(),1);
                    holder.favoriteButton.setImageResource(R.drawable.ic_star);
                }
            }
        });

        System.out.println("postId: " + post);

        // 根据是否收藏设置按钮的颜色
        if (post.isFavorite()) {
            holder.favoriteButton.setImageResource(R.drawable.ic_star);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        }
    }

    // 加载并显示图片
    private void loadImageFromUri(String uriString, ImageView imageView) {
        Uri uri = Uri.parse(uriString);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            imageView.setImageBitmap(bitmap);
            System.out.println("imageUri ： load image success");
        } catch (IOException e) {
            Log.e("TAG", "imageUri  Error loading image: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() { return postList.size(); }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView favoriteButton;
        TextView username, timestamp, content;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.textView_username);
            timestamp = itemView.findViewById(R.id.textView_timestamp);
            content = itemView.findViewById(R.id.textView_content);
            imageView = itemView.findViewById(R.id.imageView_post);
            favoriteButton = itemView.findViewById(R.id.imageButton_favorite);
        }
    }
}

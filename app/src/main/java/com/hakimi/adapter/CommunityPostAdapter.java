package com.hakimi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.hakimi.R;
import com.hakimi.model.CommunityPost;
import com.hakimi.utils.ImageLoader;

import java.util.List;

/**
 * 社区动态适配器
 * 
 * @author hakimi
 */
public class CommunityPostAdapter extends RecyclerView.Adapter<CommunityPostAdapter.ViewHolder> {

    private List<CommunityPost> postList;
    private android.content.Context context;
    private OnItemClickListener onItemClickListener;
    private OnLikeClickListener onLikeClickListener;
    private OnShareClickListener onShareClickListener;

    public CommunityPostAdapter(android.content.Context context, List<CommunityPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnLikeClickListener(OnLikeClickListener listener) {
        this.onLikeClickListener = listener;
    }

    public void setOnShareClickListener(OnShareClickListener listener) {
        this.onShareClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, CommunityPost post);
    }

    public interface OnLikeClickListener {
        void onLikeClick(int position, CommunityPost post);
    }

    public interface OnShareClickListener {
        void onShareClick(int position, CommunityPost post);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_community_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommunityPost post = postList.get(position);
        holder.tvContent.setText(post.getContent());
        if (post.getCreateTime() != null) {
            holder.tvTime.setText(post.getCreateTime());
        } else {
            holder.tvTime.setText("");
        }
        
        if (post.getUser() != null) {
            holder.tvUsername.setText(post.getUser().getUsername());
            if (post.getUser().getAvatar() != null) {
                //ImageLoader.loadCircleImage(post.getUser().getAvatar(), holder.ivAvatar);
            }
        }
        if (post.getTopic() != null && !post.getTopic().isEmpty()) {
            holder.chipTopic.setVisibility(View.VISIBLE);
            holder.chipTopic.setText(post.getTopic());
        } else {
            holder.chipTopic.setVisibility(View.VISIBLE);
        }

        // 处理图片（可拓展为图片网格）
        if (post.getImageUrls() != null && !post.getImageUrls().isEmpty()) {
            holder.recyclerViewImages.setVisibility(View.VISIBLE);
            // TODO: 设置图片网格适配器展示多图
        } else {
            holder.recyclerViewImages.setVisibility(View.GONE);
        }

        // 设置点赞数
        holder.btnLike.setText(String.valueOf(post.getLikeCount()));

        // 设置评论数
        holder.btnComment.setText("评论 " + post.getCommentCount());

        // 点击事件
        holder.btnLike.setOnClickListener(v -> {
            if (onLikeClickListener != null) {
                onLikeClickListener.onLikeClick(position, post);
            }
        });

        holder.btnComment.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position, post);
            }
        });

        holder.btnShare.setOnClickListener(v -> {
            if (onShareClickListener != null) {
                onShareClickListener.onShareClick(position, post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        com.google.android.material.imageview.ShapeableImageView ivAvatar;
        TextView tvUsername;
        TextView tvTime;
        TextView tvContent;
        RecyclerView recyclerViewImages;
        MaterialButton btnLike;
        MaterialButton btnComment;
        MaterialButton btnShare;
        com.google.android.material.chip.Chip chipTopic;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvContent = itemView.findViewById(R.id.tv_content);
            recyclerViewImages = itemView.findViewById(R.id.recycler_view_images);
            btnLike = itemView.findViewById(R.id.btn_like);
            btnComment = itemView.findViewById(R.id.btn_comment);
            btnShare = itemView.findViewById(R.id.btn_share);
            chipTopic = itemView.findViewById(R.id.chip_topic);
        }
    }
}


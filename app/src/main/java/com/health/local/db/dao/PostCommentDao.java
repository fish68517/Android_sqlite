package com.Health.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.Health.local.db.entity.PostCommentEntity;

import java.util.List;

@Dao
public interface PostCommentDao {

    @Insert
    long insert(PostCommentEntity entity);

    @Query("SELECT * FROM post_comments WHERE postId = :postId ORDER BY id ASC")
    List<PostCommentEntity> findByPostId(long postId);
}

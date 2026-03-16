package com.hakimi.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hakimi.local.db.entity.CommunityPostEntity;

import java.util.List;

@Dao
public interface CommunityPostDao {

    @Insert
    long insert(CommunityPostEntity entity);

    @Query("SELECT * FROM community_posts ORDER BY id DESC LIMIT :limit")
    List<CommunityPostEntity> findLatest(int limit);

    @Query("SELECT * FROM community_posts WHERE id = :id LIMIT 1")
    CommunityPostEntity findById(long id);

    @Query("UPDATE community_posts SET likesCount = :likesCount, updatedAt = :updatedAt WHERE id = :id")
    int updateLikes(long id, int likesCount, String updatedAt);
}

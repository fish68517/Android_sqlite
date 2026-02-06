package com.example.healthdietapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.healthdietapp.models.Post;
import com.example.healthdietapp.models.PostLike;
import com.example.healthdietapp.models.PostCollection;
import com.example.healthdietapp.models.UserFollow;
import com.example.healthdietapp.models.Feedback;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PostDAO - Data Access Object for Post operations
 * Handles post CRUD operations, likes, collections, and follow operations
 */
public class PostDAO {
    private DatabaseHelper dbHelper;

    public PostDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Create a new post
     */
    public boolean createPost(Post post) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (post.getPostId() == null) {
                post.setPostId(UUID.randomUUID().toString());
            }
            
            ContentValues values = new ContentValues();
            values.put("post_id", post.getPostId());
            values.put("user_id", post.getUserId());
            values.put("title", post.getTitle());
            values.put("content", post.getContent());
            values.put("images", post.getImages());
            values.put("tags", post.getTags());
            values.put("likes", post.getLikes());
            values.put("comments", post.getComments());
            values.put("created_at", post.getCreatedAt());
            values.put("updated_at", post.getUpdatedAt());
            
            long result = db.insert("posts", null, values);
            return result != -1;
        } finally {
            db.close();
        }
    }

    /**
     * Get post by ID
     */
    public Post getPostById(String postId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("posts", null, "post_id = ?", 
                    new String[]{postId}, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                Post post = cursorToPost(cursor);
                cursor.close();
                return post;
            }
            return null;
        } finally {
            db.close();
        }
    }

    /**
     * Get all posts
     */
    public List<Post> getAllPosts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Post> posts = new ArrayList<>();
        try {
            Cursor cursor = db.query("posts", null, null, null, null, null, 
                    "created_at DESC");
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    posts.add(cursorToPost(cursor));
                }
                cursor.close();
            }
            return posts;
        } finally {
            db.close();
        }
    }

    /**
     * Get posts by user
     */
    public List<Post> getPostsByUser(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Post> posts = new ArrayList<>();
        try {
            Cursor cursor = db.query("posts", null, "user_id = ?", 
                    new String[]{userId}, null, null, "created_at DESC");
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    posts.add(cursorToPost(cursor));
                }
                cursor.close();
            }
            return posts;
        } finally {
            db.close();
        }
    }

    /**
     * Search posts by title or content
     */
    public List<Post> searchPosts(String keyword) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Post> posts = new ArrayList<>();
        try {
            String searchPattern = "%" + keyword + "%";
            Cursor cursor = db.query("posts", null, 
                    "title LIKE ? OR content LIKE ? OR tags LIKE ?", 
                    new String[]{searchPattern, searchPattern, searchPattern}, 
                    null, null, "created_at DESC");
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    posts.add(cursorToPost(cursor));
                }
                cursor.close();
            }
            return posts;
        } finally {
            db.close();
        }
    }

    /**
     * Get recommended posts (limit to 10)
     */
    public List<Post> getRecommendedPosts(int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Post> posts = new ArrayList<>();
        try {
            Cursor cursor = db.query("posts", null, null, null, null, null, 
                    "likes DESC, created_at DESC LIMIT " + limit);
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    posts.add(cursorToPost(cursor));
                }
                cursor.close();
            }
            return posts;
        } finally {
            db.close();
        }
    }

    /**
     * Update post
     */
    public boolean updatePost(Post post) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            post.setUpdatedAt(System.currentTimeMillis());
            
            ContentValues values = new ContentValues();
            values.put("title", post.getTitle());
            values.put("content", post.getContent());
            values.put("images", post.getImages());
            values.put("tags", post.getTags());
            values.put("likes", post.getLikes());
            values.put("comments", post.getComments());
            values.put("updated_at", post.getUpdatedAt());
            
            int result = db.update("posts", values, "post_id = ?", 
                    new String[]{post.getPostId()});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Delete post
     */
    public boolean deletePost(String postId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("posts", "post_id = ?", 
                    new String[]{postId});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Like a post
     */
    public boolean likePost(String userId, String postId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            PostLike like = new PostLike(UUID.randomUUID().toString(), userId, postId);
            
            ContentValues values = new ContentValues();
            values.put("like_id", like.getLikeId());
            values.put("user_id", like.getUserId());
            values.put("post_id", like.getPostId());
            values.put("liked_at", like.getLikedAt());
            
            long result = db.insertWithOnConflict("post_likes", null, values, 
                    SQLiteDatabase.CONFLICT_IGNORE);
            
            if (result != -1) {
                // Increment likes count
                Post post = getPostById(postId);
                if (post != null) {
                    post.setLikes(post.getLikes() + 1);
                    updatePost(post);
                }
                return true;
            }
            return false;
        } finally {
            db.close();
        }
    }

    /**
     * Unlike a post
     */
    public boolean unlikePost(String userId, String postId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("post_likes", 
                    "user_id = ? AND post_id = ?", 
                    new String[]{userId, postId});
            
            if (result > 0) {
                // Decrement likes count
                Post post = getPostById(postId);
                if (post != null && post.getLikes() > 0) {
                    post.setLikes(post.getLikes() - 1);
                    updatePost(post);
                }
                return true;
            }
            return false;
        } finally {
            db.close();
        }
    }

    /**
     * Check if user has liked a post
     */
    public boolean hasUserLikedPost(String userId, String postId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("post_likes", null, 
                    "user_id = ? AND post_id = ?", 
                    new String[]{userId, postId}, null, null, null);
            
            boolean exists = cursor != null && cursor.getCount() > 0;
            if (cursor != null) {
                cursor.close();
            }
            return exists;
        } finally {
            db.close();
        }
    }

    /**
     * Collect (bookmark) a post
     */
    public boolean collectPost(String userId, String postId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            PostCollection collection = new PostCollection(UUID.randomUUID().toString(), userId, postId);
            
            ContentValues values = new ContentValues();
            values.put("collection_id", collection.getCollectionId());
            values.put("user_id", collection.getUserId());
            values.put("post_id", collection.getPostId());
            values.put("collected_at", collection.getCollectedAt());
            
            long result = db.insertWithOnConflict("post_collections", null, values, 
                    SQLiteDatabase.CONFLICT_IGNORE);
            return result != -1;
        } finally {
            db.close();
        }
    }

    /**
     * Remove collection (bookmark) from a post
     */
    public boolean removeCollection(String userId, String postId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("post_collections", 
                    "user_id = ? AND post_id = ?", 
                    new String[]{userId, postId});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Check if user has collected a post
     */
    public boolean hasUserCollectedPost(String userId, String postId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("post_collections", null, 
                    "user_id = ? AND post_id = ?", 
                    new String[]{userId, postId}, null, null, null);
            
            boolean exists = cursor != null && cursor.getCount() > 0;
            if (cursor != null) {
                cursor.close();
            }
            return exists;
        } finally {
            db.close();
        }
    }

    /**
     * Get user's collected posts
     */
    public List<Post> getUserCollectedPosts(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Post> posts = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT p.* FROM posts p " +
                    "INNER JOIN post_collections pc ON p.post_id = pc.post_id " +
                    "WHERE pc.user_id = ? " +
                    "ORDER BY pc.collected_at DESC", 
                    new String[]{userId});
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    posts.add(cursorToPost(cursor));
                }
                cursor.close();
            }
            return posts;
        } finally {
            db.close();
        }
    }

    /**
     * Follow a user
     */
    public boolean followUser(String followerId, String followeeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            UserFollow follow = new UserFollow(UUID.randomUUID().toString(), followerId, followeeId);
            
            ContentValues values = new ContentValues();
            values.put("follow_id", follow.getFollowId());
            values.put("follower_id", follow.getFollowerId());
            values.put("followee_id", follow.getFolloweeId());
            values.put("followed_at", follow.getFollowedAt());
            
            long result = db.insertWithOnConflict("user_follows", null, values, 
                    SQLiteDatabase.CONFLICT_IGNORE);
            return result != -1;
        } finally {
            db.close();
        }
    }

    /**
     * Unfollow a user
     */
    public boolean unfollowUser(String followerId, String followeeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int result = db.delete("user_follows", 
                    "follower_id = ? AND followee_id = ?", 
                    new String[]{followerId, followeeId});
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Check if user is following another user
     */
    public boolean isUserFollowing(String followerId, String followeeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("user_follows", null, 
                    "follower_id = ? AND followee_id = ?", 
                    new String[]{followerId, followeeId}, null, null, null);
            
            boolean exists = cursor != null && cursor.getCount() > 0;
            if (cursor != null) {
                cursor.close();
            }
            return exists;
        } finally {
            db.close();
        }
    }

    /**
     * Get users that a user is following
     */
    public List<String> getUserFollowing(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> followingIds = new ArrayList<>();
        try {
            Cursor cursor = db.query("user_follows", new String[]{"followee_id"}, 
                    "follower_id = ?", new String[]{userId}, null, null, null);
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    followingIds.add(cursor.getString(cursor.getColumnIndexOrThrow("followee_id")));
                }
                cursor.close();
            }
            return followingIds;
        } finally {
            db.close();
        }
    }

    /**
     * Delete all posts by a user and related data
     */
    public boolean deleteUserPosts(String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            // Get all posts by user
            List<Post> userPosts = getPostsByUser(userId);
            
            // Delete likes and collections for each post
            for (Post post : userPosts) {
                db.delete("post_likes", "post_id = ?", new String[]{post.getPostId()});
                db.delete("post_collections", "post_id = ?", new String[]{post.getPostId()});
            }
            
            // Delete all posts by user
            int result = db.delete("posts", "user_id = ?", new String[]{userId});
            
            // Delete user follows
            db.delete("user_follows", "follower_id = ?", new String[]{userId});
            db.delete("user_follows", "followee_id = ?", new String[]{userId});
            
            return result > 0;
        } finally {
            db.close();
        }
    }

    /**
     * Create feedback
     */
    public boolean createFeedback(Feedback feedback) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (feedback.getFeedbackId() == null) {
                feedback.setFeedbackId(UUID.randomUUID().toString());
            }
            
            ContentValues values = new ContentValues();
            values.put("feedback_id", feedback.getFeedbackId());
            values.put("user_id", feedback.getUserId());
            values.put("content", feedback.getContent());
            values.put("created_at", feedback.getCreatedAt());
            
            long result = db.insert("feedbacks", null, values);
            return result != -1;
        } finally {
            db.close();
        }
    }

    /**
     * Convert cursor to Post object
     */
    private Post cursorToPost(Cursor cursor) {
        Post post = new Post();
        post.setPostId(cursor.getString(cursor.getColumnIndexOrThrow("post_id")));
        post.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
        post.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        post.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
        post.setImages(cursor.getString(cursor.getColumnIndexOrThrow("images")));
        post.setTags(cursor.getString(cursor.getColumnIndexOrThrow("tags")));
        post.setLikes(cursor.getInt(cursor.getColumnIndexOrThrow("likes")));
        post.setComments(cursor.getInt(cursor.getColumnIndexOrThrow("comments")));
        post.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("created_at")));
        post.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("updated_at")));
        return post;
    }
}

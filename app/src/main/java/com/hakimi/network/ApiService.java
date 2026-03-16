package com.hakimi.network;

import com.hakimi.model.ApiResponse;
import com.hakimi.model.Comment;
import com.hakimi.model.Diary;
import com.hakimi.model.ExerciseData;
import com.hakimi.model.FitnessPlan;
import com.hakimi.model.PageResponse;
import com.hakimi.model.Post;
import com.hakimi.model.User;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    String BASE_URL = "http://192.168.2.235:8080/api/";

    @POST("admin/users/register")
    Call<ApiResponse<User>> register(@Body Map<String, String> params);

    @POST("admin/users/login")
    Call<ApiResponse<User>> login(@Body Map<String, String> params);

    @GET("admin/users/info")
    Call<ApiResponse<User>> getUserInfo();

    @GET("admin/users/getUserById")
    Single<ApiResponse<User>> getUserById(@Query("userId") long userId);

    @GET("admin/users/{id}")
    Call<ApiResponse<User>> getUserProfile(@Path("id") long userId);

    @PUT("admin/users/{id}")
    Call<ApiResponse<Object>> updateUser(@Path("id") long userId, @Body User user);

    @PUT("admin/users/updateBodyData")
    Call<ApiResponse<User>> updateBodyData(@Body Map<String, Object> params);

    @Multipart
    @POST("admin/users/uploadAvatar")
    Call<ApiResponse<String>> uploadAvatar(@Part("userId") RequestBody userId,
            @Part MultipartBody.Part image);

    @Multipart
    @POST("upload")
    Call<ApiResponse<String>> uploadFile(@Part MultipartBody.Part file);

    @GET("admin/posts")
    Call<ApiResponse<PageResponse<Post>>> getPosts(@Query("page") int page,
            @Query("pageSize") int pageSize);

    @POST("admin/posts")
    Call<ApiResponse<Post>> createPost(@Body Post post);

    @POST("admin/posts/{id}/like")
    Call<ApiResponse<Post>> likePost(@Path("id") long postId);

    @DELETE("admin/posts/{id}")
    Call<ApiResponse<Void>> deletePost(@Path("id") long postId);

    @POST("admin/comments")
    Call<ApiResponse<Comment>> createComment(@Body Comment comment);

    @POST("admin/diaries")
    Call<ApiResponse<Diary>> createDiary(@Body Diary diary);

    @GET("admin/diaries")
    Call<ApiResponse<List<Diary>>> getDiaries(@Query("userId") long userId);

    @GET("admin/exercise-data")
    Call<ApiResponse<PageResponse<ExerciseData>>> getExerciseData(@Query("page") int page,
            @Query("pageSize") int pageSize);

    @POST("admin/exercise-data/checkin")
    Call<ApiResponse<ExerciseData>> dailyCheckIn(@Body ExerciseData exerciseData);

    @GET("plan/myPlans")
    Call<ApiResponse<List<FitnessPlan>>> getMyPlans(@Query("userId") long userId);

    @POST("plan/generate")
    Call<ApiResponse<FitnessPlan>> generatePlan(@Body Map<String, Object> params);

    @POST("exercise/record")
    Call<ApiResponse<ExerciseData>> recordExercise(@Body ExerciseData exerciseData);

    @POST("plan/generateFitnessPlan")
    Call<ApiResponse<String>> generateFitnessPlan(@Query("goal") String goal);

    @POST("ai/ask")
    Call<ApiResponse<String>> askAI(@Query("question") String question);
}

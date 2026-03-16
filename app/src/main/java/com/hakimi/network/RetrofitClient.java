package com.hakimi.network;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hakimi.utils.SharedPrefManager;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit客户端
 * 
 * @author hakimi
 */
public class RetrofitClient {

    private static final String BASE_URL = ApiService.BASE_URL;
    private static final int CONNECT_TIMEOUT = 30;
    private static final int READ_TIMEOUT = 30;

    private static RetrofitClient instance;
    private Retrofit retrofit;
    private ApiService apiService;

    private RetrofitClient() {
        // 日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

/*        // 添加Token拦截器
        Interceptor tokenInterceptor = chain -> {
            Request original = chain.request();
            String token = SharedPrefManager.getInstance().getToken();
            if (!TextUtils.isEmpty(token)) {
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer " + token);
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
            return chain.proceed(original);
        };*/

        // 创建OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)

                //.addInterceptor(tokenInterceptor)
                .build();

        // 创建自定义Gson实例，配置ISO 8601日期格式解析
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        // 创建Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                           // 2. 添加 RxJava 3 的 Call Adapter Factory
                // 这一行告诉 Retrofit 如何将 Call<T> 转换为 Single<T> 或 Observable<T>
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new RetrofitClient();
                }
            }
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }
}


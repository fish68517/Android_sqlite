package com.example.orderfood;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.orderfood.model.Dish;
import com.example.orderfood.model.DishCategory;
import com.example.orderfood.model.MerchantBean;
import com.example.orderfood.model.Student;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyApplication extends Application {
    private static String username_local;
    private static String password_local;
    private static Integer userid_local;
    public static Context mContext;
    public static List<MerchantBean> merchantList_m;
    public static List<Dish> dishList_m;
    public static List<DishCategory> categoryList_m;
    public static List<Student> studentList = new ArrayList<Student>();
    public static MerchantBean curMerchant;
    public static int lastMerchantId;



    public static void setMerchantList(List<MerchantBean> merchantList) {
        merchantList_m = merchantList;
    }

    public static void setDishList(List<Dish> dishList) {
        dishList_m = dishList;
    }

    public static void setCategoryList(List<DishCategory> body) {
        // 实现本地持久化存储
        categoryList_m = body;
    }

    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static void saveMerchant(MerchantBean body) {

        curMerchant = body;
    }

    public static void saveToken(String token) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("jwtToken", token);
        editor.apply();
    }

    public static String getToken() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("jwtToken", null);
    }

    public static void saveAdmin(String username, String password) {
        username_local = username;
        password_local = password;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        loadMerchants();
        DBMysqlHelper.getInstance();

    }

    private void loadMerchants() {
        DBMysqlHelper.getInstance().getAllMerchants(new DBMysqlHelper.DatabaseCallback<List<MerchantBean>>() {
            @Override
            public void onSuccess(List<MerchantBean> merchants) {
                MerchantBean merchant = merchants.get(merchants.size() - 1);
                lastMerchantId = merchant.getMerchantId();
                System.out.println("lastMerchantId: " + lastMerchantId);

            }

            @Override
            public void onError(Exception e) {


            }
        });
    }

    public static String getStudentName(int userId) {
        for (Student student : studentList) {
            if (student.getStudentId() == userId) {
                return student.getName();
            }
        }
        return null;
    }


    // 保存用户登陆信息
    public static void saveUser(String username, String password, Integer userid) {
        // 实现本地持久化存储
        username_local = username;
        password_local = password;
        userid_local = userid;
    }

    // 获取用户登陆信息
    public static String getUserName() {
        return username_local;
    }

    public static String getPassword() {
        return password_local;
    }

    public static Integer getUserId() {
        return userid_local == null ? 1 : userid_local;
    }
}

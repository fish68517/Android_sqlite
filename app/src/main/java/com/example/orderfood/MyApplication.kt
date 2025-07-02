package com.example.orderfood

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.example.orderfood.model.User
import java.text.SimpleDateFormat
import java.util.*

class MyApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
        lateinit var user: User

        @JvmStatic
        fun formatDate(date: Date): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return dateFormat.format(date)
        }

        // 保存用户登陆信息
        @JvmStatic
        fun saveUser(usertemp: User) {
            // 实现本地持久化存储
            user = usertemp
        }
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
    }
} 
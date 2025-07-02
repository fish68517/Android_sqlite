package com.example.orderfood.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.orderfood.DataBaseOpenHelper
import com.example.orderfood.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginLink: TextView
    private lateinit var dbHelper: DataBaseOpenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DataBaseOpenHelper(this)

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        registerButton = findViewById(R.id.register_button)
        loginLink = findViewById(R.id.login_link)

        registerButton.setOnClickListener { handleRegister() }

        loginLink.setOnClickListener {
            finish() // 结束当前活动，返回登录页
        }
    }

    private fun handleRegister() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        register(username, password)
    }

    private fun register(username: String, password: String) {
        val result = dbHelper.registerUser(username, password)
        when (result) {
            -1L -> Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show()
            in 1..Long.MAX_VALUE -> {
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()
                finish() // 返回登录页
            }
            else -> Toast.makeText(this, "注册失败，请稍后再试", Toast.LENGTH_SHORT).show()
        }
    }
} 
package com.example.orderfood.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.orderfood.DataBaseOpenHelper
import com.example.orderfood.MyApplication
import com.example.orderfood.R
import com.example.orderfood.model.User

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var dbHelper: DataBaseOpenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DataBaseOpenHelper(this)

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login_button)
        registerLink = findViewById(R.id.register_link)

        loadLoginInfo()

        loginButton.setOnClickListener { handleLogin() }

        registerLink.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleLogin() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        login(username, password)
    }

    private fun login(username: String, password: String) {
        val user = dbHelper.loginUser(username, password)
        if (user != null) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()

            saveLoginInfo(username, password)
            MyApplication.saveUser(user)

            val sessionPrefs = getSharedPreferences("AppSession", MODE_PRIVATE)
            val editor = sessionPrefs.edit()
            editor.putInt("CURRENT_USER_ID", user.id)
            editor.apply()

            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)

            finish() 
        } else {
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveLoginInfo(username: String, password: String) {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("rememberMe", true)
        editor.putString("username", username)
        editor.putString("password", password)
        editor.apply()
    }

    private fun loadLoginInfo() {
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val isRemembered = sharedPreferences.getBoolean("rememberMe", false)
        if (isRemembered) {
            val savedUsername = sharedPreferences.getString("username", "")
            val savedPassword = sharedPreferences.getString("password", "")
            usernameEditText.setText(savedUsername)
            passwordEditText.setText(savedPassword)
        }
    }
} 
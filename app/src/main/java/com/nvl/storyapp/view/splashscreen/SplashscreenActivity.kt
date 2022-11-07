package com.nvl.storyapp.view.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nvl.storyapp.R
import com.nvl.storyapp.view.ViewModelFactory
import com.nvl.storyapp.view.login.LoginViewModel
import com.nvl.storyapp.view.main.MainActivity
import com.nvl.storyapp.view.welcome.WelcomeActivity

@SuppressLint("CustomSplashScreen")
class SplashscreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        setupView()
        val time : Long = 3000
        val factory = ViewModelFactory.getInstance(this)
        val viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.getUser().observe(this){user->
                if (user.userId.isEmpty()){
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }, time)

    }
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}
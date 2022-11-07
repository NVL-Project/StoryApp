package com.nvl.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nvl.storyapp.R
import com.nvl.storyapp.databinding.ActivityLoginBinding
import com.nvl.storyapp.view.ViewModelFactory
import com.nvl.storyapp.view.main.MainActivity
import com.nvl.storyapp.view.welcome.WelcomeActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
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

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[LoginViewModel::class.java]

    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val view: View? = this.currentFocus

            if (view != null) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
            when {
                email.isEmpty() -> {
                    binding.emailEditText.error = getString(R.string.add_email)
                }
                password.isEmpty() -> {
                    binding.passwordEditText.error = getString(R.string.add_password)
                }
                else -> {loginViewModel.login(
                email, password
            ).observe(this) { login ->
                if (login.error) {
                    Toast.makeText(
                        this@LoginActivity,
                        login.message,
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.login_success),
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }}}
            }
        }
    }
    private fun playAnimation() {
        val emailV = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailL = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
        val passV = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passL = ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)
        val loginB = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(emailV,emailL,passV,passL,loginB)
            start()
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
        val inten = Intent(this, WelcomeActivity::class.java)
        startActivity(inten)
    }
}
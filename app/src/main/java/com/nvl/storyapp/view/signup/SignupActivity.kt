package com.nvl.storyapp.view.signup

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
import com.nvl.storyapp.databinding.ActivitySignupBinding
import com.nvl.storyapp.view.ViewModelFactory
import com.nvl.storyapp.view.login.LoginActivity
import com.nvl.storyapp.view.welcome.WelcomeActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
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
        signupViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[SignupViewModel::class.java]
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val view: View? = this.currentFocus

            if (view != null) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
            when {
                name.isEmpty() -> {
                    binding.nameEditText.error = getString(R.string.add_name)
                }
                email.isEmpty() -> {
                    binding.emailEditText.error = getString(R.string.add_email)
                }
                password.isEmpty() -> {
                    binding.passwordEditText.error = getString(R.string.add_password)
                }
                else -> {
                    signupViewModel.register(name, email, password
                    ).observe(this) { register ->
                        if (register.error) {
                            Toast.makeText(
                                this@SignupActivity,
                                register.message,
                                Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                this@SignupActivity,
                                getString(R.string.register_success),
                                Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                            }
                        }
                    }
            }
        }
    }

    private fun playAnimation() {
        val nameV = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameL = ObjectAnimator.ofFloat(binding.nameEditText, View.ALPHA, 1f).setDuration(500)
        val emailV = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailL = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
        val passV = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passL = ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)
        val signupB = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(nameV,nameL,emailV,emailL,passV,passL,signupB)
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
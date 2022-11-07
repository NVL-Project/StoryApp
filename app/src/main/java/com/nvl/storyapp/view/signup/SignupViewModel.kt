package com.nvl.storyapp.view.signup

import androidx.lifecycle.ViewModel
import com.nvl.storyapp.data.server.model.Repository

class SignupViewModel(private val pref: Repository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        pref.register(name, email, password)
}
package com.nvl.storyapp.view.login


import androidx.lifecycle.ViewModel
import com.nvl.storyapp.data.server.model.Repository


class LoginViewModel(private val pref: Repository) : ViewModel() {
    fun getUser() = pref.getUser()
    fun login(email: String, password: String) = pref.login(email, password)
}
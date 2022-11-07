package com.nvl.storyapp.view

import com.nvl.storyapp.view.main.MainViewModel
import com.nvl.storyapp.view.signup.SignupViewModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nvl.storyapp.data.server.model.Repository
import com.nvl.storyapp.view.login.LoginViewModel
import com.nvl.storyapp.data.server.model.Injection
import com.nvl.storyapp.view.add.AddViewModel
import com.nvl.storyapp.view.maps.MapsViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory private constructor(
    private val ref: Repository
    ) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(ref) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(ref) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(ref) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                AddViewModel(ref) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(ref) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }
    }
}
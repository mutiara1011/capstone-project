package com.example.myapplication

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.remote.response.acc.UserRepository
import com.example.myapplication.data.remote.response.acc.pref.UserPreference
import com.example.myapplication.data.remote.response.acc.pref.dataStore
import com.example.myapplication.data.remote.retrofit.ApiConfig
import com.example.myapplication.ui.home.HomeViewModel
import com.example.myapplication.ui.login.LoginViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory private constructor(
    private val repository: UserRepository,
    private val userPreference: UserPreference
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository, userPreference) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val apiService = ApiConfig.getApiService()
                val userPreference = UserPreference.getInstance(context.dataStore)
                INSTANCE ?: ViewModelFactory(UserRepository.getInstance(userPreference, apiService), userPreference).also {
                    INSTANCE = it
                }
            }
        }
    }
}

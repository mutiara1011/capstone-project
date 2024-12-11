package com.example.myapplication.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.response.acc.LoginResponse
import com.example.myapplication.data.remote.response.acc.UserRepository
import com.example.myapplication.data.remote.response.acc.pref.UserModel
import com.example.myapplication.data.remote.response.acc.pref.UserPreference
import kotlinx.coroutines.launch

@Suppress("NAME_SHADOWING")
class LoginViewModel(
    private val repository: UserRepository,
    private val userPreference: UserPreference
) : ViewModel() {

    fun login(username: String, password: String, callback: (Boolean, String?, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response: LoginResponse = repository.login(username, password)

                if (response.status.code == 200) {
                    val token = response.data?.token
                    val username = response.data?.username

                    val userModel = UserModel(
                        username = username ?: "",
                        token = token ?: "",
                        isLogin = true
                    )

                    userPreference.saveSession(userModel)

                    callback(true, token, username)
                }
                else {
                    callback(false, null, response.status.message)
                }
            }
            catch (e: Exception) {
                callback(false, null, e.localizedMessage ?: "An error occurred")
            }
        }
    }
}

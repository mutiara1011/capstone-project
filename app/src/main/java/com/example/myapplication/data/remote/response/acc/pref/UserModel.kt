package com.example.myapplication.data.remote.response.acc.pref

data class UserModel(
    val token: String,
    val username: String,
    val isLogin: Boolean = false
)
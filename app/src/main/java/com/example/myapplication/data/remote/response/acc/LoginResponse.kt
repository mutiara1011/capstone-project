package com.example.myapplication.data.remote.response.acc

import com.google.gson.annotations.SerializedName

data class LoginResponse(
	@field:SerializedName("status")
	val status: Statues,

	@field:SerializedName("data")
	val data: LoginData?
)

data class Statues(
	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("message")
	val message: String
)

data class LoginData(
	@field:SerializedName("username")
	val username: String,

	@field:SerializedName("token")
	val token: String
)
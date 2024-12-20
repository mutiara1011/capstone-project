package com.example.myapplication.data.remote.response.acc

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("data")
	val data: Data?,

	@field:SerializedName("status")
	val status: Status
)

data class Status(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("message")
	val message: String
)

data class Data(

	@field:SerializedName("password")
	val password: String,

	@field:SerializedName("username")
	val username: String
)

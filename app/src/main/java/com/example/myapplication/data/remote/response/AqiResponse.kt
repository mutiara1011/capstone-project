package com.example.myapplication.data.remote.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class AqiResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("status")
	val status: Status? = null
) : Parcelable

@Parcelize
data class Status(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("message")
	val message: String? = null
) : Parcelable

@Parcelize
data class Data(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("degree_img")
	val degreeImg: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("degree")
	val degree: String? = null,

	@field:SerializedName("humidity")
	val humidity: Int? = null,

	@field:SerializedName("time")
	val time: String? = null,

	@field:SerializedName("wind")
	val wind: Double? = null
) : Parcelable

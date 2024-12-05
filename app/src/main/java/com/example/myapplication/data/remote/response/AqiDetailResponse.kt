package com.example.myapplication.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AqiDetailResponse(

	@field:SerializedName("status")
	val status: Status? = null,

	@field:SerializedName("data")
	val data: DataDetail? = null
) : Parcelable

@Parcelize
data class DataDetail(

	@field:SerializedName("main")
	val main: Main? = null,

	@field:SerializedName("detail")
	val detail: List<DetailItem>? = null
) : Parcelable

@Parcelize
data class Main(

	@field:SerializedName("aqi_index")
	val aqiIndex: Int? = null,

	@field:SerializedName("polutant_type")
	val polutantType: String? = null,

	@field:SerializedName("concentrate")
	val concentrate: Double? = null
) : Parcelable

@Parcelize
data class DetailItem(

	@field:SerializedName("aqi_index")
	val aqiIndex: Int? = null,

	@field:SerializedName("polutant_type")
	val polutantType: String? = null,

	@field:SerializedName("concentrate")
	val concentrate: Double? = null
) : Parcelable

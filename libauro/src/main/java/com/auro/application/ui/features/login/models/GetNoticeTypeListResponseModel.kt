package com.auro.application.ui.features.login.models


import com.google.gson.annotations.SerializedName

data class GetNoticeTypeListResponseModel(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("eventType")
        val eventType: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("status")
        val status: String
    )
}
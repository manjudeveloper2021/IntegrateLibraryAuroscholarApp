package com.auro.application.ui.features.login.models


import com.google.gson.annotations.SerializedName

data class GetUserTypeListResponseModel(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String
    )
}
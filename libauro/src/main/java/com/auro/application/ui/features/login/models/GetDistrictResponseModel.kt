package com.auro.application.ui.features.login.screens.models


import com.google.gson.annotations.SerializedName

data class GetDistrictResponseModel(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("state")
        val state: String,
        @SerializedName("stateId")
        val stateId: Int
    )
}
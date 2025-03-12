package com.auro.application.ui.features.login.screens.models


import com.google.gson.annotations.SerializedName

data class GetSchoolLIstResponseModel(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("district")
        val district: String,
        @SerializedName("districtId")
        val districtId: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String
    )
}
package com.auro.application.ui.features.login.models


import com.google.gson.annotations.SerializedName

data class GetDisclamerAccpetResponseModel(
    @SerializedName("data")
    val `data`: String,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
)
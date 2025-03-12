package com.auro.application.ui.features.login.models


import com.google.gson.annotations.SerializedName

data class SendOTPResponseModel(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("OTP")
        val OTP: Int
    )
}
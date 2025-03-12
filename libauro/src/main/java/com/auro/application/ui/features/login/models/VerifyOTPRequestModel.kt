package com.auro.application.ui.features.login.models


import com.google.gson.annotations.SerializedName

data class VerifyOTPRequestModel(
    @SerializedName("otp_val")
    val otpVal: Int,
    @SerializedName("sms_number")
    val smsNumber: String
)
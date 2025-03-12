package com.auro.application.ui.features.login.models


import com.google.gson.annotations.SerializedName

data class SendOTPRequestModel(
    @SerializedName("sms_number")
    val smsNumber: String
)
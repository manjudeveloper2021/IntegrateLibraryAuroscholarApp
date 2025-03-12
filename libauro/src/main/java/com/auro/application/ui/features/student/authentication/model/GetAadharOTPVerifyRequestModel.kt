package com.auro.application.ui.features.student.authentication.model


import com.google.gson.annotations.SerializedName

data class GetAadharOTPVerifyRequestModel(
   /* @SerializedName("aadharConsent")
    val aadharConsent: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,*/
    @SerializedName("otp")
    val otp: Double,
    @SerializedName("otpReferenceID")
    val otpReferenceID: String,
    @SerializedName("studentId")
    val studentId: Int
)
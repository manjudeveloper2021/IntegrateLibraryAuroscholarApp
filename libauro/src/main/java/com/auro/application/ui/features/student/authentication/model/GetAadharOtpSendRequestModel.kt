package com.auro.application.ui.features.student.authentication.model


import com.google.gson.annotations.SerializedName

data class GetAadharOtpSendRequestModel(
//    @SerializedName("aadharConsent")
//    val aadharConsent: String,
    @SerializedName("aadharNumber")
    val aadharNumber: Long,
   /* @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,*/
    @SerializedName("studentId")
    val studentId: Int
)
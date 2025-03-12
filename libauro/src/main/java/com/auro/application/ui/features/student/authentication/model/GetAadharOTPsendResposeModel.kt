package com.auro.application.ui.features.student.authentication.model


import com.google.gson.annotations.SerializedName

data class GetAadharOTPsendResposeModel(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("aadhaarNumber")
        val aadhaarNumber: String,
        @SerializedName("otpReferenceID")
        val otpReferenceID: String
    )
}
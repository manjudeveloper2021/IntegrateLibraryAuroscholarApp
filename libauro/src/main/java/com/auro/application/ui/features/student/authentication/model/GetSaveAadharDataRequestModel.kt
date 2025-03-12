package com.auro.application.ui.features.student.authentication.model


import com.google.gson.annotations.SerializedName

data class GetSaveAadharDataRequestModel(
    @SerializedName("aadhaarNo")
    val aadhaarNo: String,
    @SerializedName("dob")
    val dob: String,
    @SerializedName("profile")
    val profile: String,
    @SerializedName("studentId")
    val studentId: Int,
    @SerializedName("studentName")
    val studentName: String
)
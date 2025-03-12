package com.auro.application.ui.features.student.authentication.model


import com.google.gson.annotations.SerializedName

data class SaveAdharResponseModel(
    @SerializedName("data")
    val `data`: String,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
)
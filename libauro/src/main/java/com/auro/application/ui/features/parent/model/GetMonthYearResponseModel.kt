package com.auro.application.ui.features.parent.model


import com.google.gson.annotations.SerializedName

data class GetMonthYearResponseModel(
    @SerializedName("data")
    val `data`: List<String>,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
)
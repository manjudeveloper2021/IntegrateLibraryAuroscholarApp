package com.auro.application.ui.features.login.models


import com.google.gson.annotations.SerializedName

data class GetDisclaimerResponseModel(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("eventId")
        val eventId: String,
        @SerializedName("eventName")
        val eventName: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("languageId")
        val languageId: Int,
        @SerializedName("languageName")
        val languageName: String,
        @SerializedName("text")
        val text: String
    )
}
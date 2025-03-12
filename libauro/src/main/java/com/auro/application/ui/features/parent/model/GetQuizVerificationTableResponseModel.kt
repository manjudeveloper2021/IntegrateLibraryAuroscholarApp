package com.auro.application.ui.features.parent.model


import com.google.gson.annotations.SerializedName

data class GetQuizVerificationTableResponseModel(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("concept")
        val concept: String,
        @SerializedName("quizId")
        val quizId: String,
        @SerializedName("score")
        val score: String,
        @SerializedName("status")
        val status: String
    )
}
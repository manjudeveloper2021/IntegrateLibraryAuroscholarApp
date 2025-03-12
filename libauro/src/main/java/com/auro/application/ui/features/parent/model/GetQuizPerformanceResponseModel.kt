package com.auro.application.ui.features.parent.model


import com.google.gson.annotations.SerializedName

data class GetQuizPerformanceResponseModel(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("avg_score")
        val averageScore: Float,
        @SerializedName("pending_scholarship")
        val pendingMicroscholarshipAmount: String,
        @SerializedName("top_performing_topic_count")
        val topPerformingTopicCount: String,
        @SerializedName("total_quiz_played")
        val totalQuizCount: String,
        @SerializedName("weak_performing_topic_count")
        val weakPerformingTopicCount: String,
        @SerializedName("won_scholarship")
        val wonMicroscholarshipAmount: String
    )
}
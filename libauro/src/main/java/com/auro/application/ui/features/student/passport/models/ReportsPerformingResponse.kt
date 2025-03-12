package com.auro.application.ui.features.student.passport.models

import com.google.gson.annotations.SerializedName

data class ReportsPerformingResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: List<ReportsPerformingData>
) {
    data class ReportsPerformingData(
        @SerializedName("topPerformingTopicCount") var topPerformingTopicCount: String? = null,
        @SerializedName("weakPerformingTopicCount") var weakPerformingTopicCount: String? = null,
        @SerializedName("quizAttemptCount") var quizAttemptCount: String? = null,
        @SerializedName("avgQuizScore") var avgQuizScore: Double? = null,
        @SerializedName("verificationQuizCount") var verificationQuizCount: String? = null

    )
}
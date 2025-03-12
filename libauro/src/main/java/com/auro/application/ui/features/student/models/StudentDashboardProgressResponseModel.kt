package com.auro.application.ui.features.student.models

import com.google.gson.annotations.SerializedName

data class StudentDashboardProgressResponseModel(
    @SerializedName("data")
    val data: StudentDashboardProgress,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class StudentDashboardProgress(

        @SerializedName("kycProgress")
        val kycProgress: Int,

        @SerializedName("profilerProgress")
        val profilerProgress: Int,

        @SerializedName("totalProgress")
        val totalProgress: Int,

        @SerializedName("kycRedirect")
        val kycRedirect: Boolean,

        @SerializedName("profilerRedirect")
        val profilerRedirect: Boolean,

        @SerializedName("quizProgress")
        var quizProgress: Int? = null,

        @SerializedName("quizRedirect")
        var quizRedirect: Boolean? = null,

        @SerializedName("completionFraction")
        var completionFraction: String? = null

    )
}
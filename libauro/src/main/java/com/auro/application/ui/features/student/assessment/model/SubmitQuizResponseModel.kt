package com.auro.application.ui.features.student.assessment.model

import com.google.gson.annotations.SerializedName

data class SubmitQuizResponseModel(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("error")
    val error: String,

    @SerializedName("data")
    val data: SubmitQuizData

){
    data class SubmitQuizData(
        @SerializedName("examAssignmentID")
        val examAssignmentID: String,

        @SerializedName("examId")
        val examId: Int,

        @SerializedName("studentId")
        val studentId: String,

        @SerializedName("maxScore")
        val maxScore: Int,

        @SerializedName("currentExamName")
        val currentExamName: Int,

        @SerializedName("currentQuizAttempt")
        val currentQuizAttempt: Int,

        @SerializedName("nextQuizAttempt")
        val nextQuizAttempt: Int,

        @SerializedName("conceptText")
        val conceptText: String,

        @SerializedName("partnerId")
        val partnerId: Int,

        @SerializedName("partnerName")
        val partnerName: String,

        @SerializedName("partnerLogo")
        val partnerLogo: String,

        @SerializedName("partnerSource")
        val partnerSource: String,

        @SerializedName("submitExamAPIResult")
        val submitExamAPIResult: SubmitExamAPIResult
    )

    data class SubmitExamAPIResult(
        @SerializedName("Score")
        val Score: Int,

        @SerializedName("PassStatus")
        val PassStatus: String,

        )
}

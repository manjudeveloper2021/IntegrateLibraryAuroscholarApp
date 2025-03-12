package com.auro.application.ui.features.student.passport.models

import com.google.gson.annotations.SerializedName

data class QuizVerificationResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
//    @SerializedName("data") var data: List<QuizVerificationData>,
    @SerializedName("data") var data: QuizVerificationData

) {
    data class QuizVerificationData(
        @SerializedName("attempt") var attempt: Int? = null,
        @SerializedName("verificationRemark") var verificationRemark: String? = null,
        @SerializedName("user_id") var userId: Int? = null,
        @SerializedName("concept") var concept: String? = null,
        @SerializedName("score") var score: Int? = null,
        @SerializedName("exam_month") var examMonth: String? = null,
        @SerializedName("quizId") var quizId: String? = null,
        @SerializedName("quizStatus") var quizStatus: String? = null,
        @SerializedName("microScholarshipDisbursal") var microScholarshipDisbursal: String? = null,

        // New api response
        @SerializedName("month") var month: String? = null,
        @SerializedName("concepts") var concepts: List<QuizConcepts>

    ) {
        data class QuizConcepts(
            @SerializedName("name") var name: String? = null,
            @SerializedName("attempts") var attempts: List<QuizAttempts>

        ) {
            data class QuizAttempts(
                @SerializedName("attempt") var attempt: Int? = null,
                @SerializedName("verificationRemark") var verificationRemark: String? = null,
                @SerializedName("user_id") var userId: Int? = null,
                @SerializedName("concept") var concept: String? = null,
                @SerializedName("score") var score: Int? = null,
                @SerializedName("exam_month") var examMonth: String? = null,
                @SerializedName("quizId") var quizId: String? = null,
                @SerializedName("quizStatus") var quizStatus: String? = null,
                @SerializedName("microScholarshipDisbursal") var microScholarshipDisbursal: String? = null

            )
        }
    }

//    data class QuizVerificationData(
//
//        @SerializedName("attempt") var attempt: Int? = null,
//        @SerializedName("verificationRemark") var verificationRemark: String? = null,
//        @SerializedName("user_id") var userId: Int? = null,
//        @SerializedName("concept") var concept: String? = null,
//        @SerializedName("score") var score: Int? = null,
//        @SerializedName("exam_month") var examMonth: String? = null,
//        @SerializedName("quizId") var quizId: String? = null,
//        @SerializedName("quizStatus") var quizStatus: String? = null,
//        @SerializedName("microScholarshipDisbursal") var microScholarshipDisbursal: String? = null
//
//    )
}

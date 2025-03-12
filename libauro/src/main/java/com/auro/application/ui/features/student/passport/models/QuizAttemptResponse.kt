package com.auro.application.ui.features.student.passport.models

import com.google.gson.annotations.SerializedName

data class QuizAttemptResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: List<QuizAttemptListData>
) {
    data class QuizAttemptListData(

        @SerializedName("exam_month") var examMonth: String? = null,
        @SerializedName("concepts") var concepts: List<Concepts>

    ) {
        data class Concepts(

            @SerializedName("concept") var concept: String? = null,
            @SerializedName("subject") var subject: String? = null,
            @SerializedName("quiz_attempts") var quizAttempts: List<QuizAttempts>,
            @SerializedName("practice_quiz_count") var practiceQuizCount: Int? = null

        ) {
            data class QuizAttempts(

                @SerializedName("quiz_attempt") var quizAttempt: Int? = null,
                @SerializedName("score") var score: Int? = null,
                @SerializedName("exam_start") var examStart: String? = null,
                @SerializedName("eligibleScholarshipAmount") var eligibleScholarshipAmount: String? = null

            )
        }
    }
}
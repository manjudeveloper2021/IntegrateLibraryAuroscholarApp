package com.auro.application.ui.features.student.passport.models

import com.google.gson.annotations.SerializedName

data class TopWeakPerformingResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: List<TopWeakPerformingData>
) {
    data class TopWeakPerformingData(

        @SerializedName("exam_month") var examMonth: String? = null,
        @SerializedName("quizzes") var quizzes: List<TopWeakQuizzes>

    ) {
        data class TopWeakQuizzes(

            @SerializedName("quiz") var quiz: TopWeakQuiz? = null

        ) {
            data class TopWeakQuiz(

                @SerializedName("concept") var concept: String? = null,
                @SerializedName("quiz") var quizs: List<TopWeakQuizData>

            ) {
                data class TopWeakQuizData(

                    @SerializedName("quiz_attempt") var quizAttempt: String? = null,
                    @SerializedName("score") var score: Int? = null,
                    @SerializedName("panScore") var panScore: Double? = null,
                    @SerializedName("microScholarshipAmount") var microScholarshipAmount: String? = null

                )
            }
        }
    }
}
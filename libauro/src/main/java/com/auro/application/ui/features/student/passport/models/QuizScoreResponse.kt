package com.auro.application.ui.features.student.passport.models

import com.google.gson.annotations.SerializedName

data class QuizScoreResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: List<QuizScoreData>
) {

    data class QuizScoreData(
        @SerializedName("concept") var concept: String? = null,
        @SerializedName("subject") var subject: Int? = null,
        @SerializedName("conceptId") var conceptId: String? = null,

        @SerializedName("quiz_attempts") var practiceQuizzes: List<QuizAttempt>,
        @SerializedName("core_Quizzes") var coreQuizzes: List<QuizAttempt>,
        @SerializedName("improvement_percentage") var improvementPercentage: Double? = null

    ) {

        data class QuizAttempt(
            @SerializedName("quiz_attempt") var quizAttempt: Int? = null,
            @SerializedName("score") var score: Int? = null,
            @SerializedName("practice") var practice: Int? = null,
            @SerializedName("newKey") var new_key: Int? = null
        )
    }
}

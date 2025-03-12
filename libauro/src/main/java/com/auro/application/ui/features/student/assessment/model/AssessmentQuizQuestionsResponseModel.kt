package com.auro.application.ui.features.student.assessment.model

import com.google.gson.annotations.SerializedName

data class AssessmentQuizQuestionsResponseModel(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("error")
    val error: String,

    @SerializedName("data")
    val data: QuizListData
) {
    data class QuizListData(
        @SerializedName("message")
        val message: String,

        @SerializedName("AssessmentExamID")
        val AssessmentExamID: String? = null,

        @SerializedName("exam_id")
        val exam_id: String,

        @SerializedName("setComplete")
        val setComplete: Int,

        @SerializedName("SendQuestionToCandidateWebApiResult")
        val data: List<SendQuestionToCandidateWebApiResult>
        )

        data class SendQuestionToCandidateWebApiResult(
            @SerializedName("ID")
            val ID: String,

            @SerializedName("QuestionText")
            val QuestionText: String,

            @SerializedName("Answer1")
            val Answer1: String,

            @SerializedName("Answer2")
            val Answer2: String,

            @SerializedName("Answer3")
            val Answer3: String,

            @SerializedName("Answer4")
            val Answer4: String,
        )
}

data class SubmittedQuestionData(
    val questionNo: String,
    val currentQuestionNo: String,
    val questionId: String,
    var selectedAnswer: String = "",
    var isAnswerSelected: Boolean = false,
)

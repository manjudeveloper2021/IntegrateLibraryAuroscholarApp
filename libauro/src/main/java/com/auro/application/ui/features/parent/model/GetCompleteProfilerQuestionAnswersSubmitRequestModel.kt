package com.auro.application.ui.features.parent.model


import com.google.gson.annotations.SerializedName

data class GetCompleteProfilerQuestionAnswersSubmitRequestModel(
    @SerializedName("questions")
    val questions: List<Question>
) {
    data class Question(
        @SerializedName("answer")
        var answer: MutableList<Answer>,
        @SerializedName("id")
        val id: Int,
        @SerializedName("questionType")
        val questionType: String
    ) {
        data class Answer(
            @SerializedName("id")
            val id: Int,
            @SerializedName("text")
            var text: String
        )
    }
}
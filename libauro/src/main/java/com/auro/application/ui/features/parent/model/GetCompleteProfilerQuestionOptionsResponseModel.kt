package com.auro.application.ui.features.parent.model

import com.google.gson.annotations.SerializedName

data class GetProfilerSubmitResponse(
    @SerializedName("data")
    val data: String,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
)
data class GetCompleteProfilerQuestionOptionsResponseModel(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("answers")
        val answers: List<Answers>,  // if answers selected already
        @SerializedName("question")
        val question: Question
    ) {
        data class Question(
            @SerializedName("id")
            val id: String,
            @SerializedName("options")
            val options: List<Option>,
            @SerializedName("priority")
            val priority: Int,
            @SerializedName("questionText")
            val questionText: String,
            @SerializedName("questionType")
            val questionType: String
        ) {
            data class Option(
                @SerializedName("id")
                val id: String,
                @SerializedName("text")
                val text: String ,

                var isSelected: Boolean = false
            )
        }


        data class Answers(
            @SerializedName("id")
            val id: String,
            @SerializedName("text")
            val text: String
        )
    }
}
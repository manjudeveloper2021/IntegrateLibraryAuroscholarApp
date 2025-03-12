package com.auro.application.ui.features.student.assessment.model

import com.google.gson.annotations.SerializedName

data class AssessmentConceptsResponseModel(
    @SerializedName("data")
    val `data`: List<AssessmentConcept>,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class AssessmentConcept(
        @SerializedName("ID")
        val ID: String? = null,
        @SerializedName("Name")
        val Name: String? = null,
        @SerializedName("isSelected")
        var isSelected: String? = null,
        @SerializedName("quizCount")
        val quizCount: String? = null ,

        @SerializedName("nextAttempt")
        val nextAttempt: String? = null,
        @SerializedName("conceptWinningText")
        val conceptWinningText: String? = null
    )
}

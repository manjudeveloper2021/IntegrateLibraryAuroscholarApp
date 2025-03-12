package com.auro.application.ui.features.student.assessment.model

import com.google.gson.annotations.SerializedName

data class AssessmentSaveQuestionResponseModel(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("error")
    val error: String,

)

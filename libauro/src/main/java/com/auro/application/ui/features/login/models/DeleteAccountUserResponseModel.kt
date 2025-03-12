package com.auro.application.ui.features.login.models

import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionOptionsResponseModel.Data
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionOptionsResponseModel.Data.Question
import com.google.gson.annotations.SerializedName

data class DeleteAccountUserResponseModel(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("Message")
        val message: String,
    )
}
package com.auro.application.ui.features.login.models


import com.google.gson.annotations.SerializedName

data class GetSubjectListResponseModel(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("conceptSelected")
        val conceptSelected: Int,
        @SerializedName("icons")
        val icons: String,
        @SerializedName("isSelected")
        var isSelected: Boolean,
        @SerializedName("subjectId")
        val subjectId: Int,
        @SerializedName("subjectName")
        val subjectName: String,
        @SerializedName("totalConcept")
        val totalConcept: Int,
        @SerializedName("isDisabled")
        val isDisabled: Int
    )
}
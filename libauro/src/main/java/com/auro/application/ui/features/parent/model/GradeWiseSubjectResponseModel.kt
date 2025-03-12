package com.auro.application.ui.features.parent.model


import com.google.gson.annotations.SerializedName

data class GradeWiseSubjectResponseModel(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("icons")
        val icons: String,
        @SerializedName("subjectId")
        val subjectId: Int,
        @SerializedName("subjectName")
        val subjectName: String
    )
}
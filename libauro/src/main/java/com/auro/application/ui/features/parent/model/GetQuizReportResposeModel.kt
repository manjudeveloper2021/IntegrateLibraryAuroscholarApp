package com.auro.application.ui.features.parent.model
import com.google.gson.annotations.SerializedName
data class GetQuizReportResposeModel(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("subject")
        val subject: String,
        @SerializedName("concept_id")
        val concept_id: String,
        @SerializedName("concept")
        val concept: String,
        @SerializedName("subjectId")
        val subjectId: Int,
        @SerializedName("maxScore")
        val maxScore: Int,
        @SerializedName("coreScore")
        val coreScore: Int,
        @SerializedName("retakeScore")
        val retakeScore: Int,
        @SerializedName("retake2Score")
        val retake2Score: Int,
        @SerializedName("improvement")
        val improvement: String
    )
}
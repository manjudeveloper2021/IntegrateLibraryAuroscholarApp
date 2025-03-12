package com.auro.application.ui.features.student.assessment.model

import com.google.gson.annotations.SerializedName

data class ConceptWiseQuizResponseModel(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("error")
    val error: String,

    @SerializedName("data")
    val data: List<QuizListData>
) {
    data class QuizListData(
        @SerializedName("id")
        val id: String,

        @SerializedName("student_id")
        val student_id: String,

        @SerializedName("exam_name")
        val exam_name: Int,

        @SerializedName("quiz_attempt")
        val quiz_attempt: Int,

        @SerializedName("exam_month")
        val exam_month: String,

        @SerializedName("assessment_exam_id")
        val assessment_exam_id: String,

        @SerializedName("subject")
        val subject: String,

        @SerializedName("exam_start")
        val exam_start: String,

        @SerializedName("exam_end")
        val exam_end: String,

        @SerializedName("score")
        val score: Int,

        @SerializedName("attempted")
        val attempted: Int,

        @SerializedName("exam_compelete")
        val exam_compelete: String,

        @SerializedName("complete_by")
        val complete_by: String,

        @SerializedName("exam_face_img")
        val exam_face_img: String,

        @SerializedName("face_checked")
        val face_checked: Int,

        @SerializedName("language")
        val language: String,

        @SerializedName("concept_id")
        val concept_id: String,

        @SerializedName("is_verified")
        val is_verified: Int,
    )
}
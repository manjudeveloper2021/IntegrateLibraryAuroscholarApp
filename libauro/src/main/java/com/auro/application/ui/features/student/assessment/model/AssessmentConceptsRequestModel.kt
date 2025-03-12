package com.auro.application.ui.features.student.assessment.model

import com.google.gson.annotations.SerializedName

data class AssessmentConceptsRequestModel(
    @SerializedName("subjectId")
    val subjectId: Int,

    @SerializedName("grade")
    val grade: Int,

    @SerializedName("examMonth")
    val examMonth: String,

    @SerializedName("languageId")
    val languageId: Int
)

data class AssessmentSaveConceptRequestModel(
    @SerializedName("subjectId")
    val subjectId: Int,

    @SerializedName("grade")
    val grade: Int,

    @SerializedName("examMonth")
    val examMonth: String,

    @SerializedName("languageId")
    val languageId: Int,

    @SerializedName("concepts")
    val concepts: List<AssessmentSaveConcept>
) {
    data class AssessmentSaveConcept(
        @SerializedName("id")
        val id: String,

        @SerializedName("conceptName")
        val conceptName: String,
    )
}

data class AssessmentGetQuizRequestModel(
    @SerializedName("studentId")
    val studentId: Int,

    @SerializedName("examName")
    val examName: String,             // concept No

    @SerializedName("attempt")
    val attempt: Int,              // total list size of

    @SerializedName("language")
    val language: String,

    @SerializedName("subject")
    val subject: String,

    @SerializedName("grade")
    val grade: Int,

    @SerializedName("conceptID")
    val conceptID: String,

    @SerializedName("month")
    val month: Int,

    @SerializedName("year")
    val year: Int,

    @SerializedName("stateID")
    val stateID: Int,

    @SerializedName("IsPractice")
    val isPractice: Int,

    )

data class AssessmentSaveQuestionRequestModel(
    @SerializedName("examAssignmentId")
    val examAssignmentId: String,

    @SerializedName("answerId")
    val answerId: String,

    @SerializedName("questionId")
    val questionId: String,

    @SerializedName("questionSerialNo")
    val questionSerialNo: Int,

    @SerializedName("examId")
    val examId: Int,

    )

data class SubmitQuizRequestModel(
    @SerializedName("examAssignmentId")
    val examAssignmentId: String,

    @SerializedName("examId")
    val examId: Int,

    @SerializedName("completedBy")
    val completedBy: String,
)

data class CreateExamImageRequestModel(
    @SerializedName("examId")
    val examId: String)

package com.auro.application.ui.features.student.models

import com.google.gson.annotations.SerializedName


data class SaveStudentProfileResponseModel(
    @SerializedName("data")
    val data: String,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
)

data class StudentProfileResponseModel(
    @SerializedName("data")
    val `data`: ProfileData,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class ProfileData(
        @SerializedName("userId")
        val userId: String,

        @SerializedName("userName")
        val userName: String,

        @SerializedName("phone")
        val phone: String,

        @SerializedName("email")
        val email: String,

        @SerializedName("name")
        val name: String,

        @SerializedName("kycStatus")
        val kycStatus: String,

        @SerializedName("grade")
        val grade: Int,

        @SerializedName("imageUrl")
        val imageUrl: String,

        @SerializedName("state")
        var state: String,

        @SerializedName("district")
        var district: String,

        @SerializedName("medium")
        var medium: String,

        @SerializedName("schoolId")
        var schoolId: String,

        @SerializedName("dateOfBirth")
        val dateOfBirth: String,

        @SerializedName("pincode")
        val pincode: String,

        @SerializedName("gender")
        var gender: String,

        @SerializedName("board")
        var board: String,

        @SerializedName("districtName")
        var districtName: String,

        @SerializedName("stateName")
        var stateName: String,

        @SerializedName("boardName")
        var boardName: String,

        @SerializedName("schoolName")
        var schoolName: String,

        @SerializedName("mediumName")
        var mediumName: String,

        @SerializedName("profiler")
        val profiler: List<ProfilerData>? = null,
    ) {

        data class ProfilerData(
            @SerializedName("question")
            val question: String,

            @SerializedName("answer")
            val answer: String,
        )
    }
}
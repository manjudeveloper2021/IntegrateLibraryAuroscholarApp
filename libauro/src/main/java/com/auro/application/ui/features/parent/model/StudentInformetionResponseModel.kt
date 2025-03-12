package com.auro.application.ui.features.parent.model

import com.google.gson.annotations.SerializedName

data class StudentInformetionResponseModel(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("parent")
        val parent: Parent,

        @SerializedName("student")
        val student: List<Student>,
    ) {

        data class Parent(
            @SerializedName("user_id")
            val user_id: String,
            @SerializedName("user_name")
            val user_name: String,
            @SerializedName("user_mobile")
            val user_mobile: String,
            @SerializedName("name")
            val name: String
        )

        data class Student(
            @SerializedName("avgScore")
            val avgScore: String,
            @SerializedName("grade")
            val grade: Int,
            @SerializedName("imageUrl")
            val imageUrl: String,
            @SerializedName("isUserPin")
            val isUserPin: Boolean,
            @SerializedName("name")
            val name: String,
            @SerializedName("pendingQuizCount")
            val pendingQuizCount: String,
            @SerializedName("totalQuizCount")
            val totalQuizCount: String,
            @SerializedName("userId")
            val userId: String,
            @SerializedName("winningQuizCount")
            val winningQuizCount: String,
            @SerializedName("gender")
            val gender: String
        )
    }
}
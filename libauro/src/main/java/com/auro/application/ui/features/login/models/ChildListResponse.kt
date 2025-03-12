package com.auro.application.ui.features.login.models

import com.google.gson.annotations.SerializedName

data class ChildListResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: Data
) {
    data class Data(
        @SerializedName("parent")
        val parent: Parent ? = null,

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
            val name: String,
            @SerializedName("email_id")
            val email_id: String,
            @SerializedName("isWalkthroughSet")
            val isWalkthroughSet: String,
            @SerializedName("isUserPin")
            val isUserPin: Boolean? = null
        )

        data class Student(
            @SerializedName("userId")
            val userId: String,

            @SerializedName("name")
            val name: String,

            @SerializedName("grade")
            val grade: Int,

            @SerializedName("isUserPin")
            val isUserPin: Boolean,

            @SerializedName("imageUrl")
            val imageUrl: String? = null,

            @SerializedName("kycStatus")
            val kycStatus: String,

            @SerializedName("gender")
            val gender: String? = null,

            @SerializedName("totalQuizCount")
            val totalQuizCount: String,

            @SerializedName("avgScore")
            val avgScore: String,

            @SerializedName("winningQuizCount")
            val winningQuizCount: String,

            @SerializedName("pendingQuizCount")
            val pendingQuizCount: String,

            @SerializedName("deactivateMsg")
            val deactivateMsg: String,

            @SerializedName("isActiveUser")
            val isActiveUser: Int,
        )
    }
}

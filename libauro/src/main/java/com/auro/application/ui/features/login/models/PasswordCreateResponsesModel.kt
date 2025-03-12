package com.auro.application.ui.features.login.models


import com.google.gson.annotations.SerializedName

data class PasswordCreateResponsesModel(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("child")
        val child: List<Any>,
        @SerializedName("Message")
        val message: String,
        @SerializedName("token")
        val token: String,
        @SerializedName("userDetails")
        val userDetails: UserDetails
    ) {
        data class UserDetails(
            @SerializedName("email")
            val email: String,
            @SerializedName("grade")
            val grade: String,
            @SerializedName("isParent")
            val isParent: Boolean,
            @SerializedName("kycStatus")
            val kycStatus: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("pin")
            val pin: String,
            @SerializedName("profilePic")
            val profilePic: String,
            @SerializedName("status")
            val status: String,
            @SerializedName("userId")
            val userId: String,
            @SerializedName("userMobile")
            val userMobile: String,
            @SerializedName("userName")
            val userName: String,
            @SerializedName("userTypeId")
            val userTypeId: Int
        )
    }
}
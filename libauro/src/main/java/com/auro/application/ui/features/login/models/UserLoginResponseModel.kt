package com.auro.application.ui.features.login.models

import com.google.gson.annotations.SerializedName

data class UserLoginResponseModel(
    @SerializedName("data")
    val `data`: LoginData? = null,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class LoginData(
        @SerializedName("child")
        val child: List<UserChildList>,
        @SerializedName("Message")
        val message: String,
        @SerializedName("token")
        val token: String,
        @SerializedName("userDetails")
        val userDetails: UserDetails?
    ) {
        data class UserDetails(
            @SerializedName("email")
            val email: String,
            @SerializedName("grade")
            val grade: String? =null,
            @SerializedName("isParent")
            val isParent: Boolean,
            @SerializedName("kycStatus")
            val kycStatus: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("isPinSet")
            val isPinSet: Boolean?,
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
            val userTypeId: Int,
            @SerializedName("isActiveUser")
            val isActiveUser: Int,
            @SerializedName("isSubjectPreferencesSet")
            val isSubjectPreferencesSet: Boolean,
            @SerializedName("isWalkthroughSet")
            val isWalkthroughSet: Boolean
        )
    }

    data class UserChildList(
        @SerializedName("userId")
        val userId: String,

        @SerializedName("userName")
        val userName: String,

        @SerializedName("userMobile")
        val userMobile: String,

        @SerializedName("userTypeId")
        val userTypeId: Int,

        @SerializedName("grade")
        val grade: Int,

        @SerializedName("profilePic")
        val profilePic: String,

        @SerializedName("kycStatus")
        val kycStatus: String,

        @SerializedName("email")
        val email: String,

        @SerializedName("pin")
        val pin: Int,

        @SerializedName("name")
        val name: String,

        @SerializedName("is_master")
        val is_master: String,

        @SerializedName("status")
        val status: String,

    )
}

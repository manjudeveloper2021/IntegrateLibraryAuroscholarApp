package com.auro.application.ui.features.login.screens.models


import com.google.gson.annotations.SerializedName

data class GetStudentAddResponseModel(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("api_key")
        val apiKey: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("created_by")
        val createdBy: Int,
        @SerializedName("email")
        val email: String,
        @SerializedName("email_encrypt")
        val emailEncrypt: String,
        @SerializedName("email_verified_at")
        val emailVerifiedAt: String,
        @SerializedName("IsActiveUser")
        val isActiveUser: String,
        @SerializedName("IsCheck")
        val isCheck: String,
        @SerializedName("is_master")
        val isMaster: String,
        @SerializedName("jwt_token")
        val jwtToken: String,
        @SerializedName("last_account_inactive_date")
        val lastAccountInactiveDate: String,
        @SerializedName("last_account_restored_date")
        val lastAccountRestoredDate: String,
        @SerializedName("LastActiveDate")
        val lastActiveDate: String,
        @SerializedName("login_flag")
        val loginFlag: String,
        @SerializedName("otp_flag")
        val otpFlag: String,
        @SerializedName("parent_id")
        val parentId: String,
        @SerializedName("partner_id")
        val partnerId: String,
        @SerializedName("password")
        val password: String,
        @SerializedName("pin")
        val pin: String,
        @SerializedName("registration_id")
        val registrationId: Int,
        @SerializedName("remember_token")
        val rememberToken: String,
        @SerializedName("role_id")
        val roleId: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("token_expire")
        val tokenExpire: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("updated_by")
        val updatedBy: String,
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("user_mobile")
        val userMobile: String,
        @SerializedName("user_mobile_encrypt")
        val userMobileEncrypt: String,
        @SerializedName("user_name")
        val userName: String,
        @SerializedName("user_name_encrypt")
        val userNameEncrypt: String,
        @SerializedName("user_prefered_language_id")
        val userPreferedLanguageId: String,
        @SerializedName("user_type_id")
        val userTypeId: Int
    )
}
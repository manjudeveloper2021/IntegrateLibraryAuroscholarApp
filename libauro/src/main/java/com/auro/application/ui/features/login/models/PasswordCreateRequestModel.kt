package com.auro.application.ui.features.login.models


import com.google.gson.annotations.SerializedName

data class PasswordCreateRequestModel(

    @SerializedName("user_mobile")
    val userMobile: String,
    @SerializedName("password")
    val password: String
)
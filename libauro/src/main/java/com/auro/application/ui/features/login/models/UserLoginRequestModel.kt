package com.auro.application.ui.features.login.models

import com.google.gson.annotations.SerializedName

data class UserLoginRequestModel(

    @SerializedName("phone")
    val phone: String,
    @SerializedName("password")
    val password: String
)
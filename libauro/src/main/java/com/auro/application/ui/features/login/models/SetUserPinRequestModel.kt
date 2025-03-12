package com.auro.application.ui.features.login.models

import com.google.gson.annotations.SerializedName

data class SetUserPinRequestModel(
    @SerializedName("id")
    val id: String,

    @SerializedName("pin")
    val pin: String
)

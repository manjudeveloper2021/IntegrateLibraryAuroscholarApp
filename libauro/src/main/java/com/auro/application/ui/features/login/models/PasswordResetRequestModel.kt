package com.auro.application.ui.features.login.models

import com.google.gson.annotations.SerializedName

data class PasswordResetRequestModel(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: String? = null
)

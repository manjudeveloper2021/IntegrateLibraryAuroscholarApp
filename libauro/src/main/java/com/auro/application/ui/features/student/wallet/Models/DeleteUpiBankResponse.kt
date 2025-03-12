package com.auro.application.ui.features.student.wallet.Models

import com.google.gson.annotations.SerializedName

data class DeleteUpiBankResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: String? = null
)

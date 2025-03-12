package com.auro.application.ui.features.parent.model

import com.google.gson.annotations.SerializedName

data class UpdateParentProfileResponseModel(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: String? = null
)

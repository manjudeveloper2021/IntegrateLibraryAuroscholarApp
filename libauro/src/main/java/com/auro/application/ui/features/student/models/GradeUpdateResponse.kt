package com.auro.application.ui.features.student.models

import com.google.gson.annotations.SerializedName

data class GradeUpdateResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: String? = null
)

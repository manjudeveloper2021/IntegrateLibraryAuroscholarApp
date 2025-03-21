package com.auro.application.ui.features.login.models

import com.google.gson.annotations.SerializedName

data class CheckAutoLoginResponseModel(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: DataValidate? = null
) {
    data class DataValidate(
        @SerializedName("user_id") var userid: String? = null,
        @SerializedName("student_name") var studentname: String? = null,
        @SerializedName("profile_pic") var profilepic: String? = null,
    @SerializedName("grade") var grade: Int? = null
    )
}


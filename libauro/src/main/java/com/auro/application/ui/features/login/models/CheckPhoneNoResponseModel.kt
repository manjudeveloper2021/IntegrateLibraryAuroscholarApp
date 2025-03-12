package com.auro.application.ui.features.login.models

import com.google.gson.annotations.SerializedName

data class CheckPhoneNoResponseModel(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: DataValidate? = null
) {
    data class DataValidate(
        @SerializedName("Message") var Message: String? = null,
        @SerializedName("isActiveUser") var isActiveUser: Int? = null
    )
}

data class CheckUserNameResponseModel(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: UserValidate? = null
){
    data class UserValidate(
        @SerializedName("exists") var exists: Int? = null,
        @SerializedName("message") var message: String? = null
    )
}
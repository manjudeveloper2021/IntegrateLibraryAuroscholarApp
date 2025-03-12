package com.auro.application.repository.models


import com.google.gson.annotations.SerializedName

data class GetAppVersionResponseModel(
    @SerializedName("app_version_detials")
    val appVersionDetials: AppVersionDetials,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("response_code")
    val responseCode: Int,
    @SerializedName("status")
    val status: String
) {
    data class AppVersionDetials(
        @SerializedName("feature")
        val feature: String,
        @SerializedName("new_version")
        val newVersion: String,
        @SerializedName("older_version")
        val olderVersion: String,
        @SerializedName("priorty")
        val priorty: String,
        @SerializedName("version_id")
        val versionId: Int
    )
}
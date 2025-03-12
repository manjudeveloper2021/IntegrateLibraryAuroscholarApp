package com.auro.application.ui.features.login.screens.models


import com.google.gson.annotations.SerializedName

data class GetStateListResponseModel(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("limit")
        val limit: String,
        @SerializedName("page")
        val page: Int,
        @SerializedName("results")
        val results: List<Result>,
        @SerializedName("totalCount")
        val totalCount: Int
    ) {
        data class Result(
            @SerializedName("code")
            val code: String,
            @SerializedName("countryId")
            val countryId: Int,
            @SerializedName("countryName")
            val countryName: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String,
            @SerializedName("nativeName")
            val nativeName: String,
            @SerializedName("officialLanguageId")
            val officialLanguageId: Int,
            @SerializedName("officialLanguageName")
            val officialLanguageName: String,
            @SerializedName("priority")
            val priority: Any,
            @SerializedName("status")
            val status: String
        )
    }
}
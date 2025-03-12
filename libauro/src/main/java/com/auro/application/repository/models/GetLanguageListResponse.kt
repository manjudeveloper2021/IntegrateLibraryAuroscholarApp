package com.auro.application.repository.models


import com.google.gson.annotations.SerializedName

data class GetLanguageListResponse(
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
        val page: String,
        @SerializedName("results")
        val results: List<Result>,
        @SerializedName("totalCount")
        val totalCount: Int
    ) {
        data class Result(
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String,
            @SerializedName("icon")
            val icon: Any,
            @SerializedName("nativeName")
            val nativeName: String,
            @SerializedName("priority")
            val priority: Any,
            @SerializedName("status")
            val status: String,
            var isSelected :Boolean
        )
    }
}
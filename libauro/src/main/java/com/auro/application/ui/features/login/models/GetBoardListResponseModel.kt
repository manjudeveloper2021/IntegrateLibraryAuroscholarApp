package com.auro.application.ui.features.login.screens.models


import com.google.gson.annotations.SerializedName

data class GetBoardListResponseModel(
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
            @SerializedName("createdAt")
            val createdAt: String,
            @SerializedName("deletedAt")
            val deletedAt: Any,
            @SerializedName("description")
            val description: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String,
            @SerializedName("priority")
            val priority: Int,
            @SerializedName("sessionEndDate")
            val sessionEndDate: String,
            @SerializedName("sessionStartDate")
            val sessionStartDate: Any,
            @SerializedName("status")
            val status: String,
            @SerializedName("updatedAt")
            val updatedAt: String
        )
    }
}
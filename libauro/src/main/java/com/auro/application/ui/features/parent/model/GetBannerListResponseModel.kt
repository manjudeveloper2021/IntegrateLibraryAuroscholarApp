package com.auro.application.ui.features.parent.model


import com.google.gson.annotations.SerializedName

data class GetBannerListResponseModel(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("description")
        val description: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("image")
        val image: String,
        @SerializedName("link")
        val link: String,
        @SerializedName("tags")
        val tags: String,
        @SerializedName("title")
        val title: String
    )
}
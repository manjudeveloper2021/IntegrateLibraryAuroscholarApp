package com.auro.application.ui.features.parent.model


import com.google.gson.annotations.SerializedName

data class GetPerentProgressResponseModel(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("kycProgress")
        val kycProgress: Int,
        @SerializedName("kycRedirect")
        val kycRedirect: Boolean,
        @SerializedName("profilerProgress")
        val profilerProgress: Int,
        @SerializedName("profilerRedirect")
        val profilerRedirect: Boolean,
        @SerializedName("totalProgress")
        val totalProgress: Int,
        @SerializedName("completionFraction")
        var completionFraction: String? = null
    )
}
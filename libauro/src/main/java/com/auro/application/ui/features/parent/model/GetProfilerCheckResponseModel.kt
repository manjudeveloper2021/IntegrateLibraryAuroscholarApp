package com.auro.application.ui.features.parent.model


import com.google.gson.annotations.SerializedName

data class GetProfilerCheckResponseModel(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("profiler_completed")
        val profiler_completed: String
    )
}
package com.auro.application.ui.features.student.models

import com.google.gson.annotations.SerializedName

data class FaqCategoryResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: List<Data>? = null,
) {
    data class Data(

        @SerializedName("id") var id: Int? = null,
        @SerializedName("name") var name: String? = null,

        )
}

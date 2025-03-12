package com.auro.application.ui.features.student.passport.models

import com.google.gson.annotations.SerializedName

data class AllBadgesResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: List<AllBadgesData>
) {
    data class AllBadgesData(

        @SerializedName("id") var id: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("imageUrl") var imageUrl: String? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("min_score") var minScore: Int? = null,
        @SerializedName("max_score") var maxScore: Int? = null,
        @SerializedName("order") var order: Int? = null

    )
}

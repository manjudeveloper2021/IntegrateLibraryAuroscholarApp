package com.auro.application.ui.features.student.passport.models

import com.google.gson.annotations.SerializedName

data class AllAwardsResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: AllAwardData? = AllAwardData()
)

data class AllAwardData(

    @SerializedName("count") var count: Int? = null,
    @SerializedName("awards") var awards: List<AwardsData>? = null

) {
    data class AwardsData(

        @SerializedName("id") var id: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("imageUrl") var imageUrl: String? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("awardDate") var awardDate: String? = null

    )
}

package com.auro.application.ui.features.student.passport.models

import com.google.gson.annotations.SerializedName

data class AllAwardListResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: List<AwardListData>
) {
    data class AwardListData(

        @SerializedName("id") var id: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("imageUrl") var imageUrl: String? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("demographicType") var demographicType: String? = null,
        @SerializedName("awardType") var awardType: String? = null

    )
}

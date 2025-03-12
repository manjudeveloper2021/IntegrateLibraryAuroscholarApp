package com.auro.application.ui.features.student.passport.models

import com.google.gson.annotations.SerializedName

data class BadgesListResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: BadgesListData? = BadgesListData()
)

data class BadgesListData(

    @SerializedName("badges_count") var badgesCount: Int? = null,
    @SerializedName("badges") var badges: List<BadgesList>? = null

) {
    data class BadgesList(

        @SerializedName("id") var id: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("imageUrl") var imageUrl: String? = null,
        @SerializedName("status") var status: String? = null,
        @SerializedName("date_awarded") var dateAwarded: String? = null

    )
}
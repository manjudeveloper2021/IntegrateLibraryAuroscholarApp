package com.auro.application.ui.features.student.passport.models

import com.google.gson.annotations.SerializedName

data class BadgeAwardDataResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: BadgeAwardData? = BadgeAwardData()
)

data class BadgeAwardData(

    @SerializedName("badge") var badge: BadgeData? = BadgeData(),
    @SerializedName("award") var award: AwardData? = AwardData()

)

data class BadgeData(

    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("imageUrl") var imageUrl: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("date_awarded") var dateAwarded: String? = null

)

data class AwardData(

    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("imageUrl") var imageUrl: String? = null,
    @SerializedName("date_awarded") var dateAwarded: String? = null

)

package com.auro.application.ui.features.student.models

import com.google.gson.annotations.SerializedName

data class SaveReferralModel(
    @SerializedName("referred_by") val referredBy: Int,
    @SerializedName("referred_user_id") val referredUserId: Int,
    @SerializedName("referred_type_id") val referredTypeId: Int,
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String
)

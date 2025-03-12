package com.auro.application.ui.features.student.authentication.model


import com.google.gson.annotations.SerializedName

data class GetKycStatusResponseModel(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("is_aadhaar_verified")
        val isAadhaarVerified: Int,
        @SerializedName("is_kyc_uploaded")
        val isKycUploaded: Int,
        @SerializedName("is_kyc_verified")
        val isKycVerified: Int,
        @SerializedName("is_photo_uploaded")
        val isPhotoUploaded: Int,
        @SerializedName("is_school_card_required")
        val isSchoolCardRequired: Int,
        @SerializedName("is_school_card_uploaded")
        val isSchoolCardUploaded: Int,

        @SerializedName("is_manual_process")
        val isManualProcess: Int,
        @SerializedName("is_aadhaar_front_uploaded")
        val isAadhaarFrontUploaded: Int,
        @SerializedName("is_aadhaar_back_uploaded")
        val isAadhaarBackUploaded: Int,
    )
}
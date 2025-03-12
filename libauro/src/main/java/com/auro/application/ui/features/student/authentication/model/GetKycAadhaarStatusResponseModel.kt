package com.auro.application.ui.features.student.authentication.model

import com.google.gson.annotations.SerializedName

data class GetKycAadhaarStatusResponseModel(
    @SerializedName("data")
    val `data`: AadhaarStatusData,

    @SerializedName("error")
    val error: String,

    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class AadhaarStatusData(
        @SerializedName("studentNameVerified")
        val studentNameVerified: String,

        @SerializedName("studentNameRemarks")
        val studentNameRemarks: String,

        @SerializedName("studentGradeVerified")
        val studentGradeVerified: String,

        @SerializedName("studentGradeRemarks")
        val studentGradeRemarks: String,

        @SerializedName("studentProfileVerified")
        val studentProfileVerified: String,

        @SerializedName("studentProfileRemarks")
        val studentProfileRemarks: String,

        @SerializedName("schoolIdCardRemarks")
        val schoolIdCardRemarks: String,

        @SerializedName("studentKycStatus")
        val studentKycStatus: String,

        @SerializedName("isFinished")
        val isFinished: String,

        @SerializedName("schoolIdCardVerified")
        val schoolIdCardVerified: String,

        @SerializedName("aadhaarFrontVerified")
        val aadhaarFrontVerified: String,

        @SerializedName("aadhaarFrontRemarks")
        val aadhaarFrontRemarks: String,

        @SerializedName("aadhaarBackVerified")
        val aadhaarBackVerified: String,

        @SerializedName("aadhaarBackRemarks")
        val aadhaarBackRemarks: String,

        )
}
package com.auro.application.ui.features.student.authentication.model


import com.google.gson.annotations.SerializedName

data class GetAadharOTPVerifyResponseModel(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
) {
    data class Data(
        @SerializedName("address")
        val address: Address,
        @SerializedName("dateOfBirth")
        val dateOfBirth: String,
        @SerializedName("externalRef")
        val externalRef: String,
        @SerializedName("fullName")
        val fullName: String,
        @SerializedName("gender")
        val gender: String,
        @SerializedName("isEmailVerified")
        val isEmailVerified: Boolean,
        @SerializedName("isMobileVerified")
        val isMobileVerified: Boolean,
        @SerializedName("pool")
        val pool: Pool,
        @SerializedName("profilePic")
        val profilePic: String,
        @SerializedName("shortAadhaarNumber")
        val shortAadhaarNumber: String,
        @SerializedName("xmlContent")
        val xmlContent: String
    ) {
        data class Address(
            @SerializedName("careof")
            val careof: String,
            @SerializedName("country")
            val country: String,
            @SerializedName("dist")
            val dist: String,
            @SerializedName("house")
            val house: String,
            @SerializedName("landmark")
            val landmark: String,
            @SerializedName("loc")
            val loc: String,
            @SerializedName("pc")
            val pc: String,
            @SerializedName("po")
            val po: String,
            @SerializedName("state")
            val state: String,
            @SerializedName("street")
            val street: String,
            @SerializedName("subdist")
            val subdist: String,
            @SerializedName("vtc")
            val vtc: String
        )

        data class Pool(
            @SerializedName("closingBalance")
            val closingBalance: String,
            @SerializedName("mode")
            val mode: String,
            @SerializedName("openingBalance")
            val openingBalance: String,
            @SerializedName("payableValue")
            val payableValue: String,
            @SerializedName("referenceId")
            val referenceId: String,
            @SerializedName("transactionValue")
            val transactionValue: String
        )
    }
}
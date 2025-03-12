package com.auro.application.ui.features.student.partner.models

import com.google.gson.annotations.SerializedName

data class PartnerDetailsResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: PartnerDetails
) {
    data class PartnerDetails(

        @SerializedName("totalItems") var totalItems: Int? = null,
        @SerializedName("currentPage") var currentPage: String? = null,
        @SerializedName("totalPages") var totalPages: Int? = null,
        @SerializedName("data") var data: List<PartnerListData>

    ) {
        data class PartnerListData(

            @SerializedName("id") var id: Int? = null,
            @SerializedName("createdAt") var createdAt: String? = null,
            @SerializedName("updatedAt") var updatedAt: String? = null,
            @SerializedName("deletedAt") var deletedAt: String? = null,
            @SerializedName("name") var name: String? = null,
            @SerializedName("email") var email: String? = null,
            @SerializedName("phone") var phone: String? = null,
            @SerializedName("isMobileVerified") var isMobileVerified: Boolean? = null,
            @SerializedName("isEmailVerified") var isEmailVerified: Boolean? = null,
            @SerializedName("companyName") var companyName: String? = null,
            @SerializedName("companyUrl") var companyUrl: String? = null,
            @SerializedName("totalUserBaseCount") var totalUserBaseCount: Int? = null,
            @SerializedName("partnershipIntent") var partnershipIntent: String? = null,
            @SerializedName("approvalStatus") var approvalStatus: String? = null,
            @SerializedName("totalUserBase") var totalUserBase: Int? = null,
            @SerializedName("activeUserBase") var activeUserBase: Int? = null,
            @SerializedName("paidUserBase") var paidUserBase: Int? = null,
            @SerializedName("gst") var gst: String? = null,
            @SerializedName("pan") var pan: String? = null,
            @SerializedName("apiKey") var apiKey: String? = null,
            @SerializedName("privateApiKey") var privateApiKey: String? = null,
            @SerializedName("commissionPercentage") var commissionPercentage: Int? = null,
            @SerializedName("isPanVerified") var isPanVerified: Boolean? = null,
            @SerializedName("userName") var userName: String? = null,
            @SerializedName("draftReason") var draftReason: String? = null,
            @SerializedName("status") var status: String? = null,
            @SerializedName("logoUrl") var logoUrl: String? = null

        )
    }
}

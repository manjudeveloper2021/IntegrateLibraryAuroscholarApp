package com.auro.application.ui.features.student.wallet.Models

import com.google.gson.annotations.SerializedName

data class StudentUpiListResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: List<UpiListData>
) {
    data class UpiListData(

        @SerializedName("id") var id: Int? = null,
        @SerializedName("ybl_ref_no") var yblRefNo: String? = null,
        @SerializedName("upi_number") var upiNumber: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("status") var status: Int? = null,
        @SerializedName("created_at") var createdAt: String? = null,
        @SerializedName("updated_at") var updatedAt: String? = null

    )
}

package com.auro.application.ui.features.student.wallet.Models

import com.google.gson.annotations.SerializedName

data class StudentAccountsListResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: List<AccountsData>
) {
    data class AccountsData(

//        @SerializedName("id") var id: Int? = null,
//        @SerializedName("ybl_ref_no") var yblRefNo: String? = null,
//        @SerializedName("upi_number") var upiNumber: String? = null,
//        @SerializedName("name") var name: String? = null,
//        @SerializedName("status") var status: Int? = null,
//        @SerializedName("created_at") var createdAt: String? = null,
//        @SerializedName("updated_at") var updatedAt: String? = null,

        @SerializedName("id") var id: Int? = null,
    @SerializedName("bankAccount") var bankAccount: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("ifsc") var ifsc: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null

    )
}

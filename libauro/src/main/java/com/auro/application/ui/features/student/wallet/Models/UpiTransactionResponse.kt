package com.auro.application.ui.features.student.wallet.Models

import com.google.gson.annotations.SerializedName

data class UpiTransactionResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: UpiTransactionData? = UpiTransactionData()
)

data class UpiTransactionData(

    @SerializedName("transaction_id") var transactionId: String? = null,
    @SerializedName("order_id") var orderId: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("timestamp") var timestamp: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("status_description") var statusDescription: String? = null,
    @SerializedName("payee_virtual_address") var payeeVirtualAddress: String? = null,
    @SerializedName("ybl_ref_no") var yblRefNo: String? = null,
    @SerializedName("payee_name") var payeeName: String? = null,
    @SerializedName("transaction_type") var transactionType: String? = null

)
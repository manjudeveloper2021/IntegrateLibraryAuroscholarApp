package com.auro.application.ui.features.student.wallet.Models

import com.google.gson.annotations.SerializedName

data class TransactionHistoryResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: List<TransactionData>
) {
    data class TransactionData(

        @SerializedName("id") var id: String? = null,
        @SerializedName("transaction_id") var transactionId: String? = null,
        @SerializedName("order_id") var orderId: String? = null,
        @SerializedName("amount") var amount: String? = null,
        @SerializedName("timestamp") var timestamp: String? = null,
        @SerializedName("status") var status: String? = null,
        @SerializedName("status_description") var statusDescription: String? = null,
        @SerializedName("transaction_type") var transactionType: String? = null,
        @SerializedName("payee_virtual_address") var payeeVirtualAddress: String? = null,
        @SerializedName("upi_id") var upiId: String? = null,
        @SerializedName("cust_ref_no") var custRefNo: String? = null,
        @SerializedName("upi_response_json") var upiResponseJson: String? = null,
        @SerializedName("created_at") var createdAt: String? = null,
        @SerializedName("updated_at") var updatedAt: String? = null,
        @SerializedName("bankAccount") var bankAccount: String? = null,
        @SerializedName("ifsc") var ifsc: String? = null

    )
}
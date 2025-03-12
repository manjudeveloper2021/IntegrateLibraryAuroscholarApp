package com.auro.application.ui.features.student.wallet.Models

import com.google.gson.annotations.SerializedName

data class QuizStatusDetailsResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: ArrayList<QuizStatusData>? = null
) {
    data class QuizStatusData(

        @SerializedName("id") var id: Int? = null,
        @SerializedName("subject") var subject: String? = null,
        @SerializedName("concept") var concept: String? = null,
        @SerializedName("amount") var amount: Float? = null,
        @SerializedName("remark") var remark: String? = null,
        @SerializedName("transaction_date") var transactionDate: String? = null,
        @SerializedName("amount_status") var amountStatus: String? = null,
        @SerializedName("amount_description") var amountDescription: String? = null,
        @SerializedName("examCompletionDate") var examCompletionDate: String? = null

    )
}

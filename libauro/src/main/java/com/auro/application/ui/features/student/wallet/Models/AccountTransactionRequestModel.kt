package com.auro.application.ui.features.student.wallet.Models

import com.google.gson.annotations.SerializedName

data class AccountTransactionRequestModel(
    @SerializedName("bankAccount") val bankAccount: String,
    @SerializedName("amount") val questionType: Int,
    @SerializedName("wallet") var answer: MutableList<WalletId?>
) {
    data class WalletId(
        @SerializedName("walletId") val walletId: Int
    )
}

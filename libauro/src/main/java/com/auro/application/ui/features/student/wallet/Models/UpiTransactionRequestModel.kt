package com.auro.application.ui.features.student.wallet.Models

import com.google.gson.annotations.SerializedName

data class UpiTransactionRequestModel(
    @SerializedName("upi") val bankAccount: String,
    @SerializedName("amount") val questionType: Int,
    @SerializedName("wallet") var answer: MutableList<WinningWallet?>
) {
    data class WinningWallet(
        @SerializedName("walletId") val walletId: Int
    )
}

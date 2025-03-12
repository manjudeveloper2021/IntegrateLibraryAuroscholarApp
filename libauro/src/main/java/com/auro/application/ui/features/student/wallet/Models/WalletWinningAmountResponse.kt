package com.auro.application.ui.features.student.wallet.Models

import com.google.gson.annotations.SerializedName

data class WalletWinningAmountResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: WinningAmountData? = WinningAmountData()
)

data class WinningAmountData(

    @SerializedName("total_winning_quizes") var totalWinningQuizes: Int? = null,
    @SerializedName("total_amount") var totalAmount: Int? = null,
    @SerializedName("transaction_amount") var transactionAmount: Int? = null,
    @SerializedName("wallet") var wallet: List<WinningWallet>? = null

) {
    data class WinningWallet(

        @SerializedName("id") var id: Int? = null, @SerializedName("amount") var amount: Int? = null

    )
}
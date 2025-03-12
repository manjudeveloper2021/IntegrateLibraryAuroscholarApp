package com.auro.application.ui.features.student.wallet.Models

import com.google.gson.annotations.SerializedName

data class QuizWinningStatusResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: QuizWinningData? = QuizWinningData()
)

data class QuizWinningData(

    @SerializedName("totalWinningQuizCount") var totalWinningQuizCount: Int? = null,
    @SerializedName("winningStatus") var winningStatus: List<WinningStatus>? = null

) {
    data class WinningStatus(
        @SerializedName("quiz_count") var quizCount: String? = null,
        @SerializedName("amount") var amount: Int? = null,
        @SerializedName("status") var status: String? = null,
        @SerializedName("amount_status" ) var amountStatus : String? = null

    )
}
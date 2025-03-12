package com.auro.application.ui.features.student.wallet.Models

import com.google.gson.annotations.SerializedName

data class CreateAccountResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: CreateAccountData? = CreateAccountData()
)

data class CreateAccountData(

    @SerializedName("Message") var Message: String? = null,
    @SerializedName("account") var account: CreateAccount? = CreateAccount()

)

data class CreateAccount(

    @SerializedName("bankAccount") var bankAccount: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("ifsc") var ifsc: String? = null,
    @SerializedName("user") var user: UserAccount? = UserAccount(),
    @SerializedName("status") var status: Int? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("createdAt") var createdAt: String? = null

)

data class UserAccount(

    @SerializedName("user_id") var userId: String? = null

)
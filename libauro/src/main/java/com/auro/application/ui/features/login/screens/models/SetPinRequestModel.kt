package com.auro.application.ui.features.login.screens.models

import com.auro.application.ui.features.login.screens.models.GetStateListResponseModel.Data
import com.google.gson.annotations.SerializedName

data class SetPinRequestModel(val id: String, val pin: String)

//data class ResetPinRequestModel(val userName: String, val pin: String)
data class ResetPinRequestModel(val userName: String, val pin: String, val userId: Int?)

data class LoginWithPinRequestModel(
    val phone: String,
    val pin: String,
    val userId: String,
    val languageId: Int
)
data class ParentWalkthroughRequest(
    val walkthrough : Int = 1
)

data class SetPinResponseModel(
    @SerializedName("data")
    val `data`: String,
    @SerializedName("error")
    val error: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean
)
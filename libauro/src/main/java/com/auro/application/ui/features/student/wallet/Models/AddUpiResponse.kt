package com.auro.application.ui.features.student.wallet.Models

import com.google.gson.annotations.SerializedName

data class AddUpiResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: UpiData? = UpiData()
)

data class UpiData(

    @SerializedName("ybl_id") var yblId: String? = null,
    @SerializedName("virtual_address") var virtualAddress: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("status") var status: Int? = null

)

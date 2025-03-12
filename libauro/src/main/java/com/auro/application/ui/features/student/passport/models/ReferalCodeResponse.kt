package com.auro.application.ui.features.student.passport.models

import com.google.gson.annotations.SerializedName

data class ReferalCodeResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: Data? = Data()

) {
    data class Data(

        @SerializedName("referral_code") var referralCode: String? = null,
        @SerializedName("shortLink") var shortLink: String? = null
    )
}

package com.auro.application.ui.features.login.models

import com.google.gson.annotations.SerializedName

data class TranslationsLanguageResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: List<LanguageData>
) {
    data class LanguageData(

        @SerializedName("LanguageID") var LanguageID: String? = null,
        @SerializedName("key") var key: String? = null,
        @SerializedName("Name") var Name: String? = null,
        @SerializedName("TranslatedName") var TranslatedName: String? = null

    )
}

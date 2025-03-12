package com.auro.application.ui.features.student.models

import com.google.gson.annotations.SerializedName

data class FaqResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: List<FaqData>,
) {
    data class FaqData(

        /*@SerializedName("id") var id: String? = null,
        @SerializedName("question") var question: String? = null,
        @SerializedName("answer") var answer: String? = null,
        @SerializedName("languageId") var languageId: Int? = null,
        @SerializedName("languageName") var languageName: String? = null,
        @SerializedName("faqcatId") var faqcatId: Int? = null,
        @SerializedName("faqcatName") var faqcatName: String? = null,*/


        @SerializedName("categoryId") var categoryId: Int? = null,
        @SerializedName("categoryName") var categoryName: String? = null,
        @SerializedName("question") var question: String? = null,
        @SerializedName("answer") var answer: String? = null,

        )
}

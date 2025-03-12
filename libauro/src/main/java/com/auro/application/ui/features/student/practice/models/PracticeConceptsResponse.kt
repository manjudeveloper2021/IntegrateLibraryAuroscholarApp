package com.auro.application.ui.features.student.practice.models

import com.google.gson.annotations.SerializedName

data class PracticeConceptsResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: List<ConceptsData>

) {
    data class ConceptsData(

        @SerializedName("concept_id") var conceptId: String? = null,
        @SerializedName("concept") var concept: String? = null,
        @SerializedName("nextAttempt") var nextAttempt: Int? = null

    )
}
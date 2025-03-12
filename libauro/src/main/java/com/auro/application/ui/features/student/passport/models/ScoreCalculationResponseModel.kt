package com.auro.application.ui.features.student.passport.models

import com.google.gson.annotations.SerializedName

data class ScoreCalculationResponseModel(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var scoreCalculationData: ScoreCalculationData
) {
    data class ScoreCalculationData(
        @SerializedName("scoring") var scoring: List<Scoring>,
        @SerializedName("marks_distribution") var marks_distribution: List<MarksDistribution>
    )
    data class Scoring(
        @SerializedName("name") var name: String? = null,
        @SerializedName("score") var score: String? = null,
        @SerializedName("description") var description: String? = null,
    )
    data class MarksDistribution(
        @SerializedName("name") var name: String? = null,
        @SerializedName("description") var description: String? = null,
    )
}

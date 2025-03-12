package com.auro.application.ui.features.student.passport.models

import com.google.gson.annotations.SerializedName

data class MonthlyReportResponse(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: MonthlyReport? = MonthlyReport()
)

data class MonthlyReport(
    @SerializedName("coreQuizes") var coreQuizes: String? = null,
    @SerializedName("retakeQuizes") var retakeQuizes: String? = null,
    @SerializedName("practiceQuizes") var practiceQuizes: String? = null,
    @SerializedName("microscholarshipQuizes") var microscholarshipQuizes: String? = null,
    @SerializedName("totalCoreQuizes") var totalCoreQuizes: Int? = null,
    @SerializedName("totalRetakeQuizes") var totalRetakeQuizes: Int? = null,
    @SerializedName("totalPracticeQuizes") var totalPracticeQuizes: Int? = null,
    @SerializedName("totalmicroscholarshipQuizes") var totalmicroscholarshipQuizes: Int? = null,
    @SerializedName("monthScore") var monthScore: Double? = null,
    @SerializedName("rank") var rank: Int? = null

)

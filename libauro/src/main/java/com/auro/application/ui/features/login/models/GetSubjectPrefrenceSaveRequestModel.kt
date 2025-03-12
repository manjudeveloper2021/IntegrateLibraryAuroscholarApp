package com.auro.application.ui.features.login.models


import com.google.gson.annotations.SerializedName

data class GetSubjectPrefrenceSaveRequestModel(
    @SerializedName("studentId")
    val studentId: String,
    @SerializedName("subject")
    val subject: List<Subject>
) {
    data class Subject(
        @SerializedName("id")
        val id: Int
    )
}
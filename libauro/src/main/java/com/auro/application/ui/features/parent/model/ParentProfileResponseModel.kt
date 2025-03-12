package com.auro.application.ui.features.parent.model

import com.google.gson.annotations.SerializedName

data class ParentProfileResponseModel(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("error") var error: String? = null,
    @SerializedName("data") var data: ParentProfileData
)

data class ParentProfileData(

    @SerializedName("username") var username: String,
    @SerializedName("id") var id: Int? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("imgUrl") var imgUrl: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("gender") var gender: String,
    @SerializedName("pincode") var pincode: String,
    @SerializedName("state_id") var stateId: Int? = null,
    @SerializedName("district_id") var districtId: Int? = null,
    @SerializedName("dob") var dob: String,
    @SerializedName("alternativePhone") var alternativePhone: String,
    @SerializedName("student_count") var studentCount: Int? = null,
    @SerializedName("stateName") var stateName: String? = null,
    @SerializedName("districtName") var districtName: String? = null

)

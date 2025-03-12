package com.auro.application.ui.features.student.wallet.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class UpdateParentProfile(
    var id: String? = null,
    var userId: String? = null,
    var name: String? = null,
    var imageUrl: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var gender: String? = null,
    var state: Int? = null,
    var district: Int? = null,
    var pinCode: String? = null,
    var dob: String? = null,
    var username: String? = null,
    var students: Int? = null,
    var alternativePhone: String? = null
) : Parcelable
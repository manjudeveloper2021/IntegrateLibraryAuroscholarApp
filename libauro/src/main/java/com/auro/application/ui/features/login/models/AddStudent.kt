package com.auro.application.ui.features.login.screens.models

/*data class AddStudent(
    val name: String,
    val userName: String,
    val email: String,
    val gender: String,
    val dob: String, // You can also use LocalDate if you want to handle dates
    val state: String,
    val district: String,
    val pinCode: String,
    val alternativePhone: String? = null,
    val grade: String,
    val board: String,
    val language: String,
    val school: String
)*/
import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class AddStudent(
    var name: String? = null,
    var userName: String? = null,
    var email: String? = null,
    var gender: String? = null,
    var dob: String? = null,
    var state: String? = null,
    var district: String? = null,
    var pinCode: String? = null, // Provide a default value
    var alternativePhone: String? = null,
    var grade: String? = null,
    var board: String? = null,
    var language: String? = null,
    var school: String? = null,
    var otherSchool: String? = null, // school name in case of others
    var userTypeId: String? = null,
    var medium: Int? = null
) : Parcelable

@Parcelize
@Serializable
data class ParentModel(
    var name: String? = null,
    var userName: String? = null,
    var email: String? = null,
    var gender: String? = null,
    var dob: String? = null,
    var state: String? = null,
    var district: String? = null,
    var pinCode: String? = null,
    var students : String? = null,
    var alternativePhone: String? = null,
) : Parcelable
package com.auro.application.ui.features.login.models

data class UserRequest(
    val name: String,
    val userName: String,
    val email: String,
    val gender: String,
    val dob: String, // Consider using LocalDate for date representation
    val state: Int,
    val district: Int,
    val pinCode: String,
    val alternativePhone: String,
    val grade: Int,
    val board: Int,
    val language: Int,
    val school: Int
)
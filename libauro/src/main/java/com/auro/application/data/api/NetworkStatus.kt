package com.auro.application.data.api

sealed class NetworkStatus<out T> {
    data class Success<T>(val data: T) : NetworkStatus<T>()
    data class Error(val message: String) : NetworkStatus<Nothing>()
    object Loading : NetworkStatus<Nothing>()
    object Idle : NetworkStatus<Nothing>()
}
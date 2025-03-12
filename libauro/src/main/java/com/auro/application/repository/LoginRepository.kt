package com.auro.application.repository

import com.auro.application.data.api.ApiService
import com.auro.application.repository.models.GetAppVersionResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoginRepository @Inject constructor(private val apiService: ApiService) {

    fun getAppVersion(): Flow<GetAppVersionResponseModel> = flow {
        val response = apiService.getAppVersion()
        emit(response)
    }.flowOn(Dispatchers.IO)





}
package com.auro.application.ui.features.splash.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.data.utlis.CommonFunction
import com.auro.application.repository.models.GetAppVersionResponseModel
import com.auro.application.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val sharedPref: SharedPref, private val repository: LoginRepository, @ApplicationContext private val context: Context) :ViewModel(){
    private val _getAppVersionResponse = MutableStateFlow<NetworkStatus<GetAppVersionResponseModel>>(NetworkStatus.Loading)
    val getAppVersionResponse: StateFlow<NetworkStatus<GetAppVersionResponseModel>> = _getAppVersionResponse

    init {
        getAppVersion()
    }

    private fun getAppVersion() = viewModelScope.launch {
        if (CommonFunction.isNetworkAvailable(context)) {
            try {
                repository.getAppVersion().collect { response ->
                    _getAppVersionResponse.tryEmit(NetworkStatus.Success(response))
                }
            } catch (e: Exception) {
                val errorMessage = "An error occurred. Please try again."
                _getAppVersionResponse.tryEmit(NetworkStatus.Error(errorMessage))
            }
        } else {
            _getAppVersionResponse.tryEmit(NetworkStatus.Error("No internet connection"))
        }
    }

    /*private val _getAppVersionResponse = MutableStateFlow(NetworkStatus.Loading<GetAppVersionResponseModel>())
    val getAppVersionResponse: StateFlow<NetworkStatus<GetAppVersionResponseModel>> = _getAppVersionResponse

    init {
        getAppVersion()
    }

    private fun getAppVersion() = viewModelScope.launch {
        if (CommonFunction.isNetworkAvailable(context)) {
            try {
                repository.getAppVersion().collect { response ->
                    _getAppVersionResponse.tryEmit(NetworkStatus.Success(response))
                }
            } catch (e: Exception) {
                val errorMessage = "An error occurred. Please try again."
                _getAppVersionResponse.tryEmit(NetworkStatus.Error(errorMessage))
            }
        } else {
            _getAppVersionResponse.tryEmit(NetworkStatus.Error("No internet connection"))
        }
    }*/

}

//    //live data work for main thread collect.latest.value  = resposen same time getting error
//    private val _response: MutableLiveData<NetworkStatus<GetAppVersionResponseModel>> = MutableLiveData()
//    val getAppVersionResponse: LiveData<NetworkStatus<GetAppVersionResponseModel>> = _response


package com.auro.application.ui.features.student.partner

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.data.utlis.CommonFunction
import com.auro.application.repository.StudentRepository
import com.auro.application.ui.features.student.partner.models.PartnerDetailsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartnerViewModel @Inject constructor(
    private val repository: StudentRepository,
    private val sharedPref: SharedPref,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val getPartnerDetailsResponse: MutableLiveData<NetworkStatus<PartnerDetailsResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getPartnerDetailsResponseData: LiveData<NetworkStatus<PartnerDetailsResponse?>> =
        getPartnerDetailsResponse

    fun getPartnerDetails(
        strName: String
    ) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getPartnerDetails(strName).first()
                    getPartnerDetailsResponse.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    Log.d("getQuizAttemptResponse:", "" + e.message)
                    getPartnerDetailsResponse.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getPartnerDetailsResponse.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun setPartnerCompanyInfo(partnerDetailsData: PartnerDetailsResponse.PartnerDetails.PartnerListData) {
        sharedPref.setPartnerCompanyName(partnerDetailsData)
    }

    fun getPartnerCompanyInfo(): PartnerDetailsResponse.PartnerDetails.PartnerListData {
        return sharedPref.getPartnerCompanyName()
    }
}
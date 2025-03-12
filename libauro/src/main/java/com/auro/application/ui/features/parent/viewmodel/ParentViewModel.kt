package com.auro.application.ui.features.parent.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.data.utlis.CommonFunction
import com.auro.application.repository.ParentRepository
import com.auro.application.ui.features.login.screens.models.AddStudent
import com.auro.application.ui.features.login.screens.models.ParentModel
import com.auro.application.ui.features.parent.model.GetBannerListResponseModel
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionAnswersSubmitRequestModel
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionOptionsResponseModel
import com.auro.application.ui.features.parent.model.GetMonthYearResponseModel
import com.auro.application.ui.features.parent.model.GetPerentProgressResponseModel
import com.auro.application.ui.features.parent.model.GetProfilerCheckResponseModel
import com.auro.application.ui.features.parent.model.GetProfilerSubmitResponse
import com.auro.application.ui.features.parent.model.GetQuizPerformanceResponseModel
import com.auro.application.ui.features.parent.model.GetQuizReportResposeModel
import com.auro.application.ui.features.parent.model.GetQuizVerificationTableResponseModel
import com.auro.application.ui.features.parent.model.GradeWiseSubjectResponseModel
import com.auro.application.ui.features.parent.model.ParentProfileResponseModel
import com.auro.application.ui.features.parent.model.StudentInformetionResponseModel
import com.auro.application.ui.features.parent.model.UpdateParentProfileResponseModel
import com.auro.application.ui.features.student.authentication.model.GetAadharOTPsendResposeModel
import com.auro.application.ui.features.student.authentication.model.GetAadharOtpSendRequestModel
import com.auro.application.ui.features.student.wallet.Models.UpdateParentProfile
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParentViewModel @Inject constructor(
    private val repository: ParentRepository,
    private val sharedPref: SharedPref,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val getProfilerCheckResponseModel: MutableLiveData<NetworkStatus<GetProfilerCheckResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getProfilerCheckResponseModelLiveData: LiveData<NetworkStatus<GetProfilerCheckResponseModel?>> =
        getProfilerCheckResponseModel


    private val studentInformationResponseModel: MutableLiveData<NetworkStatus<StudentInformetionResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val studentInformationResponseModelLiveData: LiveData<NetworkStatus<StudentInformetionResponseModel?>> =
        studentInformationResponseModel


    private val parentProfilePercentage: MutableLiveData<NetworkStatus<GetPerentProgressResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val parentProfilePercentageLiveModel: LiveData<NetworkStatus<GetPerentProgressResponseModel?>> =
        parentProfilePercentage

    private val percentBannerListResponseModel: MutableLiveData<NetworkStatus<GetBannerListResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val percentBannerListResponseModelLiveData: LiveData<NetworkStatus<GetBannerListResponseModel?>> =
        percentBannerListResponseModel


    private val gradeWiseSubjectModel: MutableLiveData<NetworkStatus<GradeWiseSubjectResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val gradeWiseSubjectResponseModel: LiveData<NetworkStatus<GradeWiseSubjectResponseModel?>> =
        gradeWiseSubjectModel


    private val getMonthYearResponseModel: MutableLiveData<NetworkStatus<GetMonthYearResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getMonthYearResponseModelLiveData: LiveData<NetworkStatus<GetMonthYearResponseModel?>> =
        getMonthYearResponseModel


    private val getQuizPerformanceModel: MutableLiveData<NetworkStatus<GetQuizPerformanceResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getQuizPerformanceModelResponseModelLiveData: LiveData<NetworkStatus<GetQuizPerformanceResponseModel?>> =
        getQuizPerformanceModel

    private val getCompleteProfilerQuestionAndOptionsModel: MutableLiveData<NetworkStatus<GetCompleteProfilerQuestionOptionsResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getCompleteProfilerQuestionAndOptionsModelLiveData: LiveData<NetworkStatus<GetCompleteProfilerQuestionOptionsResponseModel?>> =
        getCompleteProfilerQuestionAndOptionsModel

    private val getStudentProfilerQuestionAndOptionsModel: MutableLiveData<NetworkStatus<GetProfilerSubmitResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getStudentProfilerQuestionAndOptionsModelLiveData: LiveData<NetworkStatus<GetProfilerSubmitResponse?>> =
        getStudentProfilerQuestionAndOptionsModel

    private val getQuizReportResponseModel: MutableLiveData<NetworkStatus<GetQuizReportResposeModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getQuizReportResponseModelLiveData: LiveData<NetworkStatus<GetQuizReportResposeModel?>> =
        getQuizReportResponseModel

    private val getQuizVerificationTableResponseModel: MutableLiveData<NetworkStatus<GetQuizVerificationTableResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getQuizVerificationTableResponseModelLiveData: LiveData<NetworkStatus<GetQuizVerificationTableResponseModel?>> =
        getQuizVerificationTableResponseModel

    private val getParentProfileResponseModel: MutableLiveData<NetworkStatus<ParentProfileResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val parentProfileResponseModel: LiveData<NetworkStatus<ParentProfileResponseModel?>> =
        getParentProfileResponseModel

    private val getUpdateParentProfileResponseModel: MutableLiveData<NetworkStatus<UpdateParentProfileResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val updateParentProfileResponseModel: LiveData<NetworkStatus<UpdateParentProfileResponseModel?>> =
        getUpdateParentProfileResponseModel


    fun getParentStudentList() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val childListResponse = repository.getParentDashboardChildList().first()
                    studentInformationResponseModel.postValue(
                        NetworkStatus.Success(
                            childListResponse
                        )
                    )
                } catch (e: Exception) {
                    studentInformationResponseModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                studentInformationResponseModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val profilePercentageResponse = repository.getParentProfilePercentage().first()
                    parentProfilePercentage.postValue(
                        NetworkStatus.Success(
                            profilePercentageResponse
                        )
                    )
                } catch (e: Exception) {
                    parentProfilePercentage.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                parentProfilePercentage.postValue(NetworkStatus.Error("No internet connection"))
            }
        }

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getParentBanner(1).first()
                    percentBannerListResponseModel.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    Log.d("Exception:", "" + e.message)
                    percentBannerListResponseModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                percentBannerListResponseModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }


    fun getProfilerCheckData() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val childListResponse = repository.getProfilerCheck().first()

                } catch (e: Exception) {
                    getProfilerCheckResponseModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getProfilerCheckResponseModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }

    }

    fun getGradeWiseSubject(grade: Int) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getGradeWiseSubject(grade).first()
                    gradeWiseSubjectModel.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    gradeWiseSubjectModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                gradeWiseSubjectModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }


    fun getMonthYear() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getExamMonthYear().first()
                    getMonthYearResponseModel.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    getMonthYearResponseModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getMonthYearResponseModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }


    fun getStudentPerformance(studentId: Int, subjectId: Int, date: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse =
                        repository.getStudentPerformance(studentId, subjectId, date).first()
                    getQuizPerformanceModel.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    getQuizPerformanceModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getQuizPerformanceModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }
    fun getStudentQuizPerformance(exammonth: String,studentId: Int) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse =
                        repository.getStudentQuizPerformance(exammonth,studentId).first()
                    getQuizPerformanceModel.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    getQuizPerformanceModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getQuizPerformanceModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getCompleteProfilerQuestionAndOption() {
        Log.e("TAG", "getCompleteProfilerQuestionAndOption: new request data is her ----> ")
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getCompleteProfilerQuestionOrOptions().first()
                    getCompleteProfilerQuestionAndOptionsModel.postValue(
                        NetworkStatus.Success(
                            bannerResponse
                        )
                    )
                } catch (e: Exception) {
                    getCompleteProfilerQuestionAndOptionsModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getCompleteProfilerQuestionAndOptionsModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getCompleteProfilerQuestionAndOptionSave(
        getCompleteProfilerQuestionAnswersSubmitRequestModel: GetCompleteProfilerQuestionAnswersSubmitRequestModel
    ) {
        Log.e("TAG", "getCompleteProfilerQuestionAndOption: new request data is her ----> ")
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getCompleteProfilerQuestionOrOptionsSave(
                        getCompleteProfilerQuestionAnswersSubmitRequestModel
                    ).first()
                    getCompleteProfilerQuestionAndOptionsModel.postValue(
                        NetworkStatus.Success(
                            bannerResponse
                        )
                    )
                } catch (e: Exception) {
                    getCompleteProfilerQuestionAndOptionsModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getCompleteProfilerQuestionAndOptionsModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getStudentProfilerQuestionAndOptionSave(
        getCompleteProfilerQuestionAnswersSubmitRequestModel: GetCompleteProfilerQuestionAnswersSubmitRequestModel
    ) {
        Log.e("TAG", "getCompleteProfilerQuestionAndOption: new request data is her ----> ")
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getStudentProfilerQuestionOrOptionsSave(
                        getCompleteProfilerQuestionAnswersSubmitRequestModel
                    ).first()
                    getStudentProfilerQuestionAndOptionsModel.postValue(
                        NetworkStatus.Success(
                            bannerResponse
                        )
                    )
                } catch (e: Exception) {
                    getStudentProfilerQuestionAndOptionsModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getStudentProfilerQuestionAndOptionsModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }


    private val _grade = MutableStateFlow<Int?>(null)
    val grade = _grade.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId = _userId.asStateFlow()

    private val _selectedSubjectId = MutableStateFlow<String?>(null)
    val selectedSubjectId = _selectedSubjectId.asStateFlow()

    private val _date = MutableStateFlow<String?>(null)
    val date = _date.asStateFlow()

    fun setData(userId: String, selectedSubjectId: String, date: String) {
        _userId.value = userId
        _selectedSubjectId.value = selectedSubjectId
        _date.value = date
    }

    fun saveGrade(grade: Int) {
        _grade.value = grade
    }

    fun getQuizPerformanceTable(subjectId: Int, date: String, userId: Int, conceptname: String, languageId: Int) {
//        val requestMap = hashMapOf<String, Int>().apply {
//            put("subjectId", subjectId)
//            put("date", date)
//            put("studentId", userId)
//            put("conceptName", conceptname)
//            put("languageId", languageId)
//        }
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getQuizReport(subjectId,date,userId,conceptname,languageId).first()
                    getQuizReportResponseModel.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    getQuizReportResponseModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getQuizReportResponseModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getQuizVerificationTable(subjectId: String, date: String, userId: String) {
        val requestMap = hashMapOf<String, String>().apply {
            put("subjectId", subjectId)
            put("date", date)
            put("userId", userId)
        }
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse =
                        repository.getQuizVerificationTableReport(requestMap).first()
                    getQuizVerificationTableResponseModel.postValue(
                        NetworkStatus.Success(
                            bannerResponse
                        )
                    )
                } catch (e: Exception) {
                    getQuizVerificationTableResponseModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getQuizVerificationTableResponseModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getParentProfile() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getParentProfile().first()
                    getParentProfileResponseModel.postValue(
                        NetworkStatus.Success(
                            bannerResponse
                        )
                    )
                } catch (e: Exception) {
                    getParentProfileResponseModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getParentProfileResponseModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getUpdateParentProfile(
        strId: String,
        srtUserId: String,
        strName: String,
        strImageUrl: String,
        strPhone: String,
        strEmail: String,
        strGender: String,
        strState: String,
        strDistrict: String,
        strPinCode: String,
        strDob: String,
        strUsername: String,
        strStudents: String,
        strAlternativePhone: String
    ) {

        val requestMap = JsonObject()
        requestMap.addProperty("id", strId)
        requestMap.addProperty("userId", srtUserId)
        requestMap.addProperty("name", strName)
        requestMap.addProperty("imageUrl", strImageUrl)
        requestMap.addProperty("phone", strPhone)
        requestMap.addProperty("email", strEmail)
        requestMap.addProperty("gender", strGender)
        requestMap.addProperty("state", strState.toInt())
        requestMap.addProperty("district", strDistrict.toInt())
        requestMap.addProperty("pinCode", strPinCode)
        requestMap.addProperty("dob", strDob)
        requestMap.addProperty("username", strUsername)
        requestMap.addProperty("students", strStudents.toInt())
        requestMap.addProperty("alternativePhone", strAlternativePhone)
        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getUpdateParentProfile(requestMap).first()
                    getUpdateParentProfileResponseModel.postValue(
                        NetworkStatus.Success(
                            bannerResponse
                        )
                    )
                } catch (e: Exception) {
                    getUpdateParentProfileResponseModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getUpdateParentProfileResponseModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

//    private val _parentProfile = MutableLiveData<UpdateParentProfile>().apply {
//        value = UpdateParentProfile()
//    }
//
//    val parents: LiveData<UpdateParentProfile> = _parentProfile
//
//    fun setParentProfileDetail(updateParentProfile: UpdateParentProfile) {
//        _parentProfile.value = updateParentProfile
//    }

    private val _parent = MutableLiveData<UpdateParentProfile>().apply {
        value = UpdateParentProfile()
    }

    val parent: LiveData<UpdateParentProfile> = _parent

    fun saveParentDetail(student: UpdateParentProfile) {
        _parent.value = student
    }

    fun updateParent(updatedStudent: UpdateParentProfile) {
        _parent.value = updatedStudent
    }

    fun saveNumberOfStudent(number: Int) {
        sharedPref.saveNumberOfStudent(number)
    }

    fun getNumberOfStudent(): Int = sharedPref.getNumberOfStudent()
}
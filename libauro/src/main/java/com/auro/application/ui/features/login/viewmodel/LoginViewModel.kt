package com.auro.application.ui.features.login.viewmodel

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
import com.auro.application.repository.models.GetLanguageListResponse
import com.auro.application.ui.features.login.models.CheckAutoLoginResponseModel
import com.auro.application.ui.features.login.models.CheckPhoneNoResponseModel
import com.auro.application.ui.features.login.models.CheckUserNameResponseModel
import com.auro.application.ui.features.login.models.GetDisclaimerResponseModel
import com.auro.application.ui.features.login.models.GetDisclamerAccpetResponseModel
import com.auro.application.ui.features.login.models.GetNoticeTypeListResponseModel
import com.auro.application.ui.features.login.models.GetSubjectListResponseModel
import com.auro.application.ui.features.login.models.GetSubjectPrefrenceSaveRequestModel
import com.auro.application.ui.features.login.models.GetSubjectPrefrenceSaveResponseModel
import com.auro.application.ui.features.login.models.ChildListResponse
import com.auro.application.ui.features.login.models.DeleteAccountUserResponseModel
import com.auro.application.ui.features.login.models.GetUserTypeListResponseModel
import com.auro.application.ui.features.login.models.ParentRegistationResponseModel
import com.auro.application.ui.features.login.models.PasswordCreateRequestModel
import com.auro.application.ui.features.login.models.PasswordResetRequestModel
import com.auro.application.ui.features.login.models.SendOTPRequestModel
import com.auro.application.ui.features.login.models.SendOTPResponseModel
import com.auro.application.ui.features.login.models.TranslationsLanguageResponse
import com.auro.application.ui.features.login.models.UserLoginRequestModel
import com.auro.application.ui.features.login.models.UserLoginResponseModel
import com.auro.application.ui.features.login.models.VerifyOTPRequestModel
import com.auro.application.ui.features.login.models.VerifyOTPResponseModel
import com.auro.application.ui.features.login.screens.models.AddStudent
import com.auro.application.ui.features.login.screens.models.GetBoardListResponseModel
import com.auro.application.ui.features.login.screens.models.GetDistrictResponseModel
import com.auro.application.ui.features.login.screens.models.GetSchoolLIstResponseModel
import com.auro.application.ui.features.login.screens.models.GetStateListResponseModel
import com.auro.application.ui.features.login.screens.models.GetStudentAddResponseModel
import com.auro.application.ui.features.login.screens.models.ParentModel
import com.auro.application.ui.features.login.screens.models.LoginWithPinRequestModel
import com.auro.application.ui.features.login.screens.models.ParentWalkthroughRequest
import com.auro.application.ui.features.login.screens.models.ResetPinRequestModel
import com.auro.application.ui.features.login.screens.models.SetPinRequestModel
import com.auro.application.ui.features.login.screens.models.SetPinResponseModel
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionAnswersSubmitRequestModel
import com.auro.application.ui.features.parent.model.ParentProfileData
import com.auro.application.ui.features.parent.model.StudentInformetionResponseModel
import com.auro.application.ui.features.student.assessment.model.AssessmentConceptsResponseModel
import com.auro.application.ui.features.student.assessment.model.AssessmentQuizQuestionsResponseModel
import com.auro.application.ui.features.student.assessment.model.AssessmentSaveQuestionResponseModel
import com.auro.application.ui.features.student.assessment.model.SubmitQuizResponseModel
import com.auro.application.ui.features.student.authentication.model.GetKycAadhaarStatusResponseModel
import com.auro.application.ui.features.student.authentication.model.GetKycStatusResponseModel
import com.auro.application.ui.features.student.models.StudentProfileResponseModel
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.HashMap
import javax.inject.Inject

@HiltViewModel
open class LoginViewModel @Inject constructor(
    private val sharedPref: SharedPref,
    private val repository: StudentRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val setPinModel: MutableLiveData<NetworkStatus<SetPinResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val setPinResponseLiveData: LiveData<NetworkStatus<SetPinResponseModel?>> = setPinModel

    private val setChildListModel: MutableLiveData<NetworkStatus<ChildListResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val childListLiveData: LiveData<NetworkStatus<ChildListResponse?>> = setChildListModel

    private val noticeTypeListModel: MutableLiveData<NetworkStatus<GetNoticeTypeListResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getNoticeTypeListResponseModel: LiveData<NetworkStatus<GetNoticeTypeListResponseModel?>> =
        noticeTypeListModel

    private val disclaimerModel: MutableLiveData<NetworkStatus<GetDisclaimerResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getDisclaimerResponseModel: LiveData<NetworkStatus<GetDisclaimerResponseModel?>> =
        disclaimerModel


    private val disclaimerAcceptModel: MutableLiveData<NetworkStatus<GetDisclamerAccpetResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getDisclaimerAcceptResponseModel: LiveData<NetworkStatus<GetDisclamerAccpetResponseModel?>> =
        disclaimerAcceptModel


    private val getSubject: MutableLiveData<NetworkStatus<GetSubjectListResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getSubjectListResponseModel: LiveData<NetworkStatus<GetSubjectListResponseModel?>> =
        getSubject


    private val subjectPrefrenceSaveModel: MutableLiveData<NetworkStatus<GetSubjectPrefrenceSaveResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getSubjectPrefrenceSaveResponseModel: LiveData<NetworkStatus<GetSubjectPrefrenceSaveResponseModel?>> =
        subjectPrefrenceSaveModel


    private val schoolList: MutableLiveData<NetworkStatus<GetSchoolLIstResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getSchoolListResponseModel: LiveData<NetworkStatus<GetSchoolLIstResponseModel?>> =
        schoolList

    private val boardListResponseModel: MutableLiveData<NetworkStatus<GetBoardListResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getBoardListResponseModel: LiveData<NetworkStatus<GetBoardListResponseModel?>> =
        boardListResponseModel

    private val districtList: MutableLiveData<NetworkStatus<GetDistrictResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getDistrictResponseModel: LiveData<NetworkStatus<GetDistrictResponseModel?>> = districtList

    private val stateList: MutableLiveData<NetworkStatus<GetStateListResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getStateListResponseModel: LiveData<NetworkStatus<GetStateListResponseModel?>> = stateList


    private val userType: MutableLiveData<NetworkStatus<GetUserTypeListResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val userTypeListResponseModel: LiveData<NetworkStatus<GetUserTypeListResponseModel?>> = userType

    private val passwordCreate: MutableLiveData<NetworkStatus<UserLoginResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val passwordCreateLiveResponse: LiveData<NetworkStatus<UserLoginResponseModel?>> =
        passwordCreate

    private val resetRequestModel: MutableLiveData<NetworkStatus<PasswordResetRequestModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val passwordResetRequestModel: LiveData<NetworkStatus<PasswordResetRequestModel?>> =
        resetRequestModel

    private val _response: MutableStateFlow<NetworkStatus<GetLanguageListResponse?>> =
        MutableStateFlow(NetworkStatus.Loading)
    val languageResponse: StateFlow<NetworkStatus<GetLanguageListResponse?>> = _response

    private val mediumModel: MutableLiveData<NetworkStatus<GetLanguageListResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val mediumResponseModel: LiveData<NetworkStatus<GetLanguageListResponse?>> = mediumModel

    private val checkPhoneNoResponseModel: MutableLiveData<NetworkStatus<CheckPhoneNoResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val phoneNoResponse: LiveData<NetworkStatus<CheckPhoneNoResponseModel?>> =
        checkPhoneNoResponseModel



    private val checkAutoLoginResponseModel: MutableLiveData<NetworkStatus<CheckAutoLoginResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val autoLoginResponse: LiveData<NetworkStatus<CheckAutoLoginResponseModel?>> =
        checkAutoLoginResponseModel



    private val usernameModel: MutableLiveData<NetworkStatus<CheckUserNameResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val checkUsernameResponse: LiveData<NetworkStatus<CheckUserNameResponseModel?>> = usernameModel

    private val sendOtp: MutableLiveData<NetworkStatus<SendOTPResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val sendOtpResponse: LiveData<NetworkStatus<SendOTPResponseModel?>> = sendOtp

    private val sendOtpOnCall: MutableStateFlow<NetworkStatus<SendOTPResponseModel?>> =
        MutableStateFlow(NetworkStatus.Idle)
    val sendOtpOnCallResponse: StateFlow<NetworkStatus<SendOTPResponseModel?>> =
        sendOtpOnCall.asStateFlow()

    private val verifyOtp: MutableLiveData<NetworkStatus<VerifyOTPResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val verifyResponse: LiveData<NetworkStatus<VerifyOTPResponseModel?>> = verifyOtp

    private val requestLogin: MutableLiveData<NetworkStatus<UserLoginResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val loginResponse: LiveData<NetworkStatus<UserLoginResponseModel?>> = requestLogin

    private val addChild: MutableLiveData<NetworkStatus<GetStudentAddResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val addChildResponse: LiveData<NetworkStatus<GetStudentAddResponseModel?>> = addChild

    private val parentRegistration: MutableLiveData<NetworkStatus<ParentRegistationResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getParentResponseModel: LiveData<NetworkStatus<ParentRegistationResponseModel?>> =
        parentRegistration

    private val userInactive: MutableLiveData<NetworkStatus<DeleteAccountUserResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getUserInactiveResponse: LiveData<NetworkStatus<DeleteAccountUserResponseModel?>> =
        userInactive

    /*private val languageTranslationModel: MutableLiveData<NetworkStatus<LanguageTranslationsResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val languageTranslationResponse: LiveData<NetworkStatus<LanguageTranslationsResponse?>> =
        languageTranslationModel*/

    private val translationsLanguageResponseModel: MutableLiveData<NetworkStatus<TranslationsLanguageResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val translationsLanguageResponse: LiveData<NetworkStatus<TranslationsLanguageResponse?>> =
        translationsLanguageResponseModel

    // Force update
    private val forceUpdateModel: MutableLiveData<NetworkStatus<AssessmentSaveQuestionResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val forceUpdateResponse: LiveData<NetworkStatus<AssessmentSaveQuestionResponseModel?>> =
        forceUpdateModel

    fun getSetPin(userId: String, pin: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getSetPin(SetPinRequestModel(userId, pin)).first()
                    setPinModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    setPinModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                setPinModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getResetPin(userName: String, pin: String, userId: Int?) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response =
                        repository.getResetPin(ResetPinRequestModel(userName, pin, userId)).first()
                    setPinModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    setPinModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                setPinModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getNoticeTypeList(eventType: String = "disclaimer") {
        val requestModel = hashMapOf("eventType" to eventType)
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getNoticeTypeList(eventType).first()
                    noticeTypeListModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    noticeTypeListModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                noticeTypeListModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getDisclaimerById(languageId: Int? = 1, eventId: Int = 6, userTypeId: Int = 1) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getDisclaimer(languageId, eventId, userTypeId).first()
                    disclaimerModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    disclaimerModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                disclaimerModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getDisclaimerAccept(disclaimer_id: Int = 6) {
        val requestModel = hashMapOf("disclaimer_id" to disclaimer_id)
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getDisclaimerSave(requestModel).first()
                    disclaimerAcceptModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    disclaimerAcceptModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                disclaimerAcceptModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getSubjectList(grade: Int) {
        val requestHashMap = hashMapOf("gradeId" to grade)
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getSubjectList().first()
                    getSubject.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    getSubject.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getSubject.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getSubjectPreferenceSave(grade: GetSubjectPrefrenceSaveRequestModel) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getSubjectPreferenceSave(grade).first()
                    subjectPrefrenceSaveModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    subjectPrefrenceSaveModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                subjectPrefrenceSaveModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getChildListApi() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getChildList().first()
                    setChildListModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    setChildListModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                setPinModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }


    fun getSchoolList(districtInt: Int, search: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getSchoolList(districtInt, search).first()
                    schoolList.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    schoolList.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                schoolList.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getBoardList(page: Int = 1, limit: String = "50") {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getBoardList(page, limit).first()
                    boardListResponseModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    boardListResponseModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                boardListResponseModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getDistrict(stateId: Int) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getDistrictList(stateId).first()
                    districtList.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    districtList.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                districtList.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getAddChild(addStudent: AddStudent, image: File?) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getAddStudent(addStudent, image).first()
                    addChild.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    addChild.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                addChild.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getParentProfile(addStudent: ParentModel) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getParentProfile(addStudent).first()
                    parentRegistration.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    parentRegistration.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                parentRegistration.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getParentWalkthrough(addStudent: ParentWalkthroughRequest) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getParentWalkthrough(addStudent).first()
                    parentRegistration.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    parentRegistration.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                parentRegistration.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getStateList() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getStateList().first()
                    stateList.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    stateList.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                stateList.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }


    fun setPassword(smsNumber: String, password: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getPasswordCreate(
                        PasswordCreateRequestModel(
                            userMobile = smsNumber, password = password
                        )
                    ).first()
                    passwordCreate.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    passwordCreate.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                passwordCreate.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getResetPassword(smsNumber: String, userType: String, password: String) {

        val requestMap = JsonObject()
        requestMap.addProperty("phone", smsNumber)
        requestMap.addProperty("userType", userType)
        requestMap.addProperty("password", password)
        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getResetPassword(requestMap).first()
                    resetRequestModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    resetRequestModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                resetRequestModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }


    fun getUserTypeList() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getUerTypeList().first()
                    userType.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    userType.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                userType.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun sendOtp(smsNumber: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response =
                        repository.getSendOtp(SendOTPRequestModel(smsNumber = smsNumber)).first()
                    sendOtp.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    val errorMessage = "An error occurred. Please try again."
                    sendOtp.postValue(NetworkStatus.Error(errorMessage))
                }
            } else {
                sendOtp.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }


    fun sendOtpOnCall(smsNumber: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response =
                        repository.getSendOtpOnCall(SendOTPRequestModel(smsNumber = smsNumber))
                            .first()
                    sendOtp.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    val errorMessage = "An error occurred. Please try again."
                    sendOtp.postValue(NetworkStatus.Error(errorMessage))
                }
            } else {
                sendOtp.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun verifyOtp(otp: String, encryptPhone: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getVerifyOtp(
                        VerifyOTPRequestModel(
                            otpVal = otp.toInt(), smsNumber = encryptPhone
                        )
                    ).first()
                    verifyOtp.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    val errorMessage = e.message ?: "An error occurred. Please try again."
                    verifyOtp.postValue(NetworkStatus.Error(errorMessage))
                }
            } else {
                verifyOtp.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }


    val _otp = MutableLiveData<String>()
    var otp: LiveData<String> = _otp

    private fun setOtp(otp: String) {
        _otp.value = otp
    }

    fun saveUserPhoneNo(phone: String) {
        Log.e("SAVE PHONE", "saveUserPhoneNo: ----> $phone")
        sharedPref.saveUserPhoneNo(phone)
    }

    fun getUserPhoneNo(): String = sharedPref.getPhoneNo()

    fun saveUserType(id: String) {
        sharedPref.saveUserTypeId(id)
    }

    fun saveUserLogin(isLogin: Boolean) {
        sharedPref.saveLogin(isLogin)
    }

    fun getUserType(): String? = sharedPref.getUserTypeId()

    init {
        fetchLanguages("1", "10")
    }

    fun fetchLanguages(page: String = "1", limit: String = "10") {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getLanguageRepo(page, limit).first()
                    _response.emit(NetworkStatus.Success(response))
                    mediumModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
//                    val errorMessage = "An error occurred. Please try again."
                    _response.emit(NetworkStatus.Error(e.message.toString()))
                    mediumModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                _response.emit(NetworkStatus.Error("No internet connection"))
                mediumModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    /*fun checkUsername(username: String) {
        val hashMap = hashMapOf<String, String>().apply {
            put("user_mobile", username)
        }
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    usernameModel.postValue(NetworkStatus.Loading)
                    val response = repository.getCheckUsername(hashMap).first()
                    usernameModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    usernameModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                usernameModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }*/
    fun checkUsernameExiting(username: String, isNew: String) {
        val hashMap = hashMapOf<String, String>().apply {
            put("user_name", username)
            put("action", isNew) // check after login, add for new user
        }
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    usernameModel.postValue(NetworkStatus.Loading)
                    val response = repository.getCheckUsernameExisting(hashMap).first()
                    usernameModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    usernameModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                usernameModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun loginRequestCall(phoneNo: String, password: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getUserLoginRepo(
                        UserLoginRequestModel(
                            phone = phoneNo, password = password
                        )
                    ).first()
                    requestLogin.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    val errorMessage = "An error occurred. Please try again."
                    requestLogin.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                requestLogin.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun loginWithOtpRequestCall(otp: String, encryptPhone: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getUserLoginWithOtpRepo(
                        VerifyOTPRequestModel(
                            otpVal = otp.toInt(), smsNumber = encryptPhone
                        )
                    ).first()
                    requestLogin.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    val errorMessage = "An error occurred. Please try again."
                    requestLogin.postValue(NetworkStatus.Error(errorMessage))
                }
            } else {
                requestLogin.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun loginWithPinRequestCall(
        pin: String,
        encryptPhone: String,
        userId: String,
        languageId: Int,
    ) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getUserLoginWithPinRepo(
                        LoginWithPinRequestModel(
                            phone = encryptPhone,
                            pin = pin,
                            userId = userId,
                            languageId = languageId
                        )
                    ).first()
                    requestLogin.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    requestLogin.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                requestLogin.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun checkPhoneNo(phone: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    checkPhoneNoResponseModel.postValue(NetworkStatus.Loading)
                    val response = repository.getCheckPhoneNoRepo(Request(phone)).first()
                    checkPhoneNoResponseModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    checkPhoneNoResponseModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                checkPhoneNoResponseModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }


    fun getCheckAutoLoginRepo(phone: String,partnerid: String, userid: String, forcepartner: String, addnew: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    checkAutoLoginResponseModel.postValue(NetworkStatus.Loading)
                    val response = repository.getCheckAutoLoginRepo(Request(phone),Request(partnerid),Request(userid),Request(forcepartner),Request(addnew)).first()
                    checkAutoLoginResponseModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    checkAutoLoginResponseModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                checkAutoLoginResponseModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }


    /*fun languageTranslationAPI(langId: Int) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    languageTranslationModel.postValue(NetworkStatus.Loading)
                    val response =
                        repository.getLanguageTranslationRepo(LanguageRequest(langId)).first()
                    languageTranslationModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    languageTranslationModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                languageTranslationModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }*/

    fun getTranslationsLanguageAPI(langId: Int) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    translationsLanguageResponseModel.postValue(NetworkStatus.Loading)
                    val response =
                        repository.getTranslationsLanguage(LanguageRequest(langId)).first()
                    translationsLanguageResponseModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    translationsLanguageResponseModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                translationsLanguageResponseModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getForceUpdateAPI(appVersion: String) {
        val requestMap = JsonObject()
        requestMap.addProperty("apiVersion", appVersion)
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    forceUpdateModel.postValue(NetworkStatus.Loading)
                    val response =
                        repository.getForceUpdateRepo(requestMap).first()
                    forceUpdateModel.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    forceUpdateModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                forceUpdateModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getUserInactive() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    userInactive.postValue(NetworkStatus.Loading)
                    val response = repository.getInactiveUserRepo().first()
                    userInactive.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    userInactive.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                userInactive.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getUserRecover() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    userInactive.postValue(NetworkStatus.Loading)
                    val response = repository.getRecoverUserRepo().first()
                    userInactive.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    userInactive.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                userInactive.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun clearResponse() {
        viewModelScope.launch {
            setPinModel.postValue(NetworkStatus.Idle)
            usernameModel.postValue(NetworkStatus.Idle)
            checkPhoneNoResponseModel.postValue(NetworkStatus.Idle)
            sendOtp.postValue(NetworkStatus.Idle)
            verifyOtp.postValue(NetworkStatus.Idle)
        }

    }

    override fun onCleared() {
        super.onCleared()
        clearResponse()
    }

    fun saveChildCount(id: Int) {
        sharedPref.saveStudentCount(id)
    }

    fun getChildCount(): Int = sharedPref.getStudentCount()

    fun saveLanguageId(id: String) {
        sharedPref.saveLangeId(id)
    }

    fun getLanguageId(): String = sharedPref.getLangeId()

    fun saveLanguageCode(id: String) {
        sharedPref.saveLangCode(id)
    }

    fun getLanguageCode(): String = sharedPref.getLangCode()

    fun saveExamName(id: String) {
        sharedPref.saveExamName(id)
    }

    fun getExamName(): String = sharedPref.getExamName()

    fun saveIsPractice(isPractice: Boolean) {
        sharedPref.saveIsPractice(isPractice)
    }

    fun getIsPractice(): Boolean = sharedPref.getIsPractice()

    fun saveExamId(id: String) {
        sharedPref.saveExamId(id)
    }

    // navigation DAta
    fun getUserName(): String = sharedPref.getUserName()

    fun saveUserName(name: String) {
        sharedPref.saveUserName(name)
    }

    fun getUserEmail(): String = sharedPref.getUserEmail()

    fun saveUserEmail(name: String) {
        sharedPref.saveUserEmail(name)
    }

    fun getUserImage(): String = sharedPref.getUserImage()

    fun saveUserImage(name: String) {
        sharedPref.saveUserImage(name)
    }

    fun getExamId(): String = sharedPref.getExamId()

//    fun saveAssessmentExamId(id: String) {
//        sharedPref.saveExamAssessmentId(id)
//    }

//    fun getExamAssessmentId(): String = sharedPref.getExamAssessmentId()

    fun saveAssessmentQuestion(assessmentQuestion: AssessmentQuizQuestionsResponseModel.QuizListData) {
        sharedPref.saveAssessmentQuestionData(assessmentQuestion)
    }

    fun getAssessmentQuestion(): AssessmentQuizQuestionsResponseModel.QuizListData? =
        sharedPref.getAssessmentQuestionData()!!

    fun saveToken(token: String) {
        sharedPref.saveToken(token)
    }

    fun saveGrade(gradeId: String) {
        sharedPref.saveGrade(gradeId)
    }

    fun getGrade(): String? = sharedPref.getGrade()
    fun getToken(): String? = sharedPref.getToken()


    fun getFlow(): String? = sharedPref.getFlow()

    fun saveFlow(flow: String) {
        sharedPref.saveFlow(flow)
    }

    fun saveScreenName(name: String) {
        sharedPref.saveScreenName(name)
    }

    fun saveUserId(userId: String?) {
        sharedPref.saveUserId(userId)
    }

    fun getUserId(): String? = sharedPref.getUserId()

    fun getScreenName(): String? = sharedPref.getScreenName()


    private val _student = MutableLiveData<AddStudent>().apply {
        value = AddStudent()
    }
    val student: LiveData<AddStudent> = _student

    fun saveStudentDetail(student: AddStudent) {
        _student.value = student
    }

    fun updateStudent(updatedStudent: AddStudent) {
        _student.value = updatedStudent
    }

    private val _parent = MutableLiveData<ParentModel>().apply {
        value = ParentModel()
    }
    val parent: LiveData<ParentModel> = _parent

    fun saveParentDetail(student: ParentModel) {
        _parent.value = student
    }

    fun updateParent(updatedStudent: ParentModel) {
        _parent.value = updatedStudent
    }

    fun saveUserPin(userPin: Boolean) {
        sharedPref.saveUserPin(userPin)
    }

    fun getUserPin(): Boolean = sharedPref.getUserPin()

    fun clearPreferenceData(context: Context) {
        sharedPref.clearData(context)
    }

    // for student login
    fun saveStudentData(childData: ChildListResponse.Data.Student) {
        sharedPref.saveStudentListData(childData)
    }

    fun getStudentData(): ChildListResponse.Data.Student = sharedPref.getStudentListData()

    // for student Subject
    fun saveStudentSubject(childData: List<GetSubjectListResponseModel.Data?>) {
        sharedPref.saveStudentSubjectData(childData)
    }

    fun getStudentSubject(): List<GetSubjectListResponseModel.Data?> =
        sharedPref.getStudentSubjectData()

    // save kyc
    fun saveKycStatusData(kycData: GetKycAadhaarStatusResponseModel.AadhaarStatusData) {
        sharedPref.saveKycData(kycData)
    }

    fun getKycStatusData(): GetKycAadhaarStatusResponseModel.AadhaarStatusData =
        sharedPref.getKycData()

    // kyc uploaded doc status
    fun saveKycDocUploadStatus(kycData: GetKycStatusResponseModel.Data) {
        sharedPref.saveKycDocStatus(kycData)
    }

    fun getKycDocUploadStatus(): GetKycStatusResponseModel.Data = sharedPref.getKycDocStatus()

    // for Parent Login
    fun saveStudentList(childData: StudentInformetionResponseModel.Data.Student) {
        sharedPref.saveStudentListDetails(childData)
    }

    fun getStudentList(): StudentInformetionResponseModel.Data.Student =
        sharedPref.getStudentListDetails()

    fun saveStudentProfileData(profileData: StudentProfileResponseModel.ProfileData?) {
        sharedPref.saveStudentInfo(profileData)
    }

    fun getStudentProfileData(): StudentProfileResponseModel.ProfileData? =
        sharedPref.getStudentInfo()

    fun saveParentProfileData(profileData: ParentProfileData?) {
        sharedPref.saveParentProfileInfo(profileData)
    }

    fun getLanguageTranslationData(listName: String?): HashMap<String, String> =
        sharedPref.getTranslationInfo(listName)

    fun saveLanguageTranslationData(listName: String?, list: HashMap<String, String>) {
        sharedPref.saveTranslationInfo(listName, list)
    }

    fun getParentProfileData(): ParentProfileData? = sharedPref.getParentProfileInfo()

    fun saveStudentProfilerData(profileData: GetCompleteProfilerQuestionAnswersSubmitRequestModel) {
        sharedPref.saveStudentProfilerList(profileData)
    }

    fun getStudentProfilerData(): GetCompleteProfilerQuestionAnswersSubmitRequestModel =
        sharedPref.getStudentProfilerList()!!

    fun removeStudentProfilerData() = sharedPref.removeStudentProfilerList()

    fun saveParentInfo(userDetails: UserLoginResponseModel.LoginData.UserDetails?) {
        sharedPref.saveParentInfo(userDetails)
    }

    fun getStudentSelectedSubjectData(): GetSubjectListResponseModel.Data =
        sharedPref.getSelectedSubjectInfo()

    fun saveStudentSelectedSubjectData(selectedSubject: GetSubjectListResponseModel.Data) {
        sharedPref.saveSelectedSubjectInfo(selectedSubject)
    }

    fun getStudentSelectedConceptData(): AssessmentConceptsResponseModel.AssessmentConcept =
        sharedPref.getSelectedConceptInfo()

    fun saveStudentSelectedConceptData(selectedSubject: AssessmentConceptsResponseModel.AssessmentConcept) {
        sharedPref.saveSelectedConceptInfo(selectedSubject)
    }

    fun getStudentQuizResultData(): SubmitQuizResponseModel.SubmitQuizData =
        sharedPref.getQuizResult()

    fun saveStudentQuizResultData(selectedSubject: SubmitQuizResponseModel.SubmitQuizData) {
        sharedPref.saveQuizResult(selectedSubject)
    }

    fun getParentInfo(): UserLoginResponseModel.LoginData.UserDetails? {
        return sharedPref.getParentInfo()
    }

    fun setLoginInfo(userDetails: UserLoginResponseModel.LoginData.UserDetails?) {
        sharedPref.setLoginInfo(userDetails)
    }

    fun getLoginInfo(): UserLoginResponseModel.LoginData.UserDetails? {
        return sharedPref.getLoginInfo()
    }

    fun setForgotPassword(forgetPassword: String) {
        sharedPref.setForgotPassword(forgetPassword)
    }

    fun getForgotPassword(): String? {
        return sharedPref.getForgotPassword()
    }

    fun setSelectedConcept(number: Int) {
        sharedPref.setSelectedConcept(number)
    }

    fun getSelectedConcept(): Int? {
        return sharedPref.getSelectedConcept()
    }

    fun setSelectedConceptId(concept: String) {
        sharedPref.setSelectedConceptId(concept)
    }

    fun getSelectedConceptId(): String {
        return sharedPref.getSelectedConceptId()
    }

    fun setSelectedNextAttempt(number: Int) {
        sharedPref.setSelectedPracticeConcept(number)
    }

    fun getSelectedNextConcept(): Int? {
        return sharedPref.getSelectedPracticeConcept()
    }

    fun setSelectedConceptName(concept: String) {
        sharedPref.setSelectedConceptName(concept)
    }

    fun getSelectedConceptName(): String {
        return sharedPref.getSelectedConceptName()
    }

    fun setAddStudentFromParentDashboard(parentDashboard: String) {
        sharedPref.setAddStudentFromParentDashboard(parentDashboard)
    }

    fun getAddStudentFromParentDashboard(): String {
        return sharedPref.getAddStudentFromParentDashboard()
    }

}

data class Request(val user_mobile: String)
data class LanguageRequest(val languageId: Int)
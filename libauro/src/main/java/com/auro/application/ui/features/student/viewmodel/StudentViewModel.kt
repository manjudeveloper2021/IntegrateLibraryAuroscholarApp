package com.auro.application.ui.features.student.viewmodel

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
import com.auro.application.ui.features.login.models.GetSubjectListResponseModel
import com.auro.application.ui.features.login.models.VerifyOTPResponseModel
import com.auro.application.ui.features.student.authentication.model.GetAadharOTPVerifyRequestModel
import com.auro.application.ui.features.student.authentication.model.GetAadharOTPVerifyResponseModel
import com.auro.application.ui.features.login.screens.models.AddStudent
import com.auro.application.ui.features.parent.model.GetBannerListResponseModel
import com.auro.application.ui.features.student.assessment.model.AssessmentConceptsRequestModel
import com.auro.application.ui.features.student.assessment.model.AssessmentConceptsResponseModel
import com.auro.application.ui.features.student.assessment.model.AssessmentGetQuizRequestModel
import com.auro.application.ui.features.student.assessment.model.AssessmentQuizQuestionsResponseModel
import com.auro.application.ui.features.student.assessment.model.AssessmentSaveConceptRequestModel
import com.auro.application.ui.features.student.assessment.model.AssessmentSaveQuestionRequestModel
import com.auro.application.ui.features.student.assessment.model.AssessmentSaveQuestionResponseModel
import com.auro.application.ui.features.student.assessment.model.ConceptWiseQuizResponseModel
import com.auro.application.ui.features.student.assessment.model.CreateExamImageRequestModel
import com.auro.application.ui.features.student.assessment.model.SubmitQuizRequestModel
import com.auro.application.ui.features.student.assessment.model.SubmitQuizResponseModel
import com.auro.application.ui.features.student.authentication.model.GetAadharOTPsendResposeModel
import com.auro.application.ui.features.student.authentication.model.GetAadharOtpSendRequestModel
import com.auro.application.ui.features.student.authentication.model.GetKycAadhaarStatusResponseModel
import com.auro.application.ui.features.student.authentication.model.GetKycDocUplaodResposneModel
import com.auro.application.ui.features.student.authentication.model.GetKycStatusResponseModel
import com.auro.application.ui.features.student.authentication.model.GetSaveAadharDataRequestModel
import com.auro.application.ui.features.student.authentication.model.SaveAdharResponseModel
import com.auro.application.ui.features.student.models.FaqCategoryResponse
import com.auro.application.ui.features.student.models.FaqResponse
import com.auro.application.ui.features.student.models.GradeUpdateResponse
import com.auro.application.ui.features.student.models.SaveReferralModel
import com.auro.application.ui.features.student.models.SaveReferralResponse
import com.auro.application.ui.features.student.models.SaveStudentProfileResponseModel
import com.auro.application.ui.features.student.models.StudentDashboardProgressResponseModel
import com.auro.application.ui.features.student.models.StudentProfileResponseModel
import com.auro.application.ui.features.student.passport.models.AllAwardListResponse
import com.auro.application.ui.features.student.passport.models.AllAwardsResponse
import com.auro.application.ui.features.student.passport.models.AllBadgesResponse
import com.auro.application.ui.features.student.passport.models.BadgeAwardDataResponse
import com.auro.application.ui.features.student.passport.models.BadgesListResponse
import com.auro.application.ui.features.student.passport.models.LeaderboardDataResponse
import com.auro.application.ui.features.student.passport.models.MonthlyReportResponse
import com.auro.application.ui.features.student.passport.models.QuizAttemptResponse
import com.auro.application.ui.features.student.passport.models.QuizScoreResponse
import com.auro.application.ui.features.student.passport.models.QuizVerificationResponse
import com.auro.application.ui.features.student.passport.models.ReferalCodeResponse
import com.auro.application.ui.features.student.passport.models.ReportsPerformingResponse
import com.auro.application.ui.features.student.wallet.Models.AccountTransactionRequestModel
import com.auro.application.ui.features.student.passport.models.ScoreCalculationResponseModel
import com.auro.application.ui.features.student.passport.models.TopWeakPerformingResponse
import com.auro.application.ui.features.student.practice.models.PracticeConceptsResponse
import com.auro.application.ui.features.student.wallet.Models.AccountTransactionResponse
import com.auro.application.ui.features.student.wallet.Models.AddUpiResponse
import com.auro.application.ui.features.student.wallet.Models.CreateAccountResponse
import com.auro.application.ui.features.student.wallet.Models.DeleteUpiBankResponse
import com.auro.application.ui.features.student.wallet.Models.PaymentCheckConfigResponse
import com.auro.application.ui.features.student.wallet.Models.QuizStatusDetailsResponse
import com.auro.application.ui.features.student.wallet.Models.QuizWinningStatusResponse
import com.auro.application.ui.features.student.wallet.Models.StudentAccountsListResponse
import com.auro.application.ui.features.student.wallet.Models.StudentUpiListResponse
import com.auro.application.ui.features.student.wallet.Models.StudentUpiListResponse.UpiListData
import com.auro.application.ui.features.student.wallet.Models.TransactionHistoryResponse
import com.auro.application.ui.features.student.wallet.Models.UpiTransactionRequestModel
import com.auro.application.ui.features.student.wallet.Models.UpiTransactionResponse
import com.auro.application.ui.features.student.wallet.Models.WalletWinningAmountResponse
import com.auro.application.ui.features.student.wallet.Models.WinningAmountData
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val repository: StudentRepository,
    private val sharedPref: SharedPref,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _response: MutableStateFlow<NetworkStatus<GetLanguageListResponse?>> =
        MutableStateFlow(NetworkStatus.Loading)
    val languageResponse: StateFlow<NetworkStatus<GetLanguageListResponse?>> = _response

    private val studentDashboardProgress: MutableLiveData<NetworkStatus<StudentDashboardProgressResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getStudentDashboardProgress: LiveData<NetworkStatus<StudentDashboardProgressResponseModel?>> =
        studentDashboardProgress

    private val getSubject: MutableLiveData<NetworkStatus<GetSubjectListResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getSubjectListResponseModel: LiveData<NetworkStatus<GetSubjectListResponseModel?>> =
        getSubject

    private val getStudentProfile: MutableLiveData<NetworkStatus<StudentProfileResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getStudentProfileResponse: LiveData<NetworkStatus<StudentProfileResponseModel?>> =
        getStudentProfile

    private val saveStudentProfile: MutableLiveData<NetworkStatus<SaveStudentProfileResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val saveStudentProfileResponse: LiveData<NetworkStatus<SaveStudentProfileResponseModel?>> =
        saveStudentProfile

    private val getConceptList: MutableLiveData<NetworkStatus<AssessmentConceptsResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getStudentConceptResponse: LiveData<NetworkStatus<AssessmentConceptsResponseModel?>> =
        getConceptList

    private val saveConceptData: MutableLiveData<NetworkStatus<VerifyOTPResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getSaveConceptResponse: LiveData<NetworkStatus<VerifyOTPResponseModel?>> = saveConceptData

    private val getConceptWiseQuiz: MutableLiveData<NetworkStatus<ConceptWiseQuizResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val conceptWiseQuizResponse: LiveData<NetworkStatus<ConceptWiseQuizResponseModel?>> =
        getConceptWiseQuiz

    private val getQuizQuestion: MutableLiveData<NetworkStatus<AssessmentQuizQuestionsResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getQuizQuestionResponse: LiveData<NetworkStatus<AssessmentQuizQuestionsResponseModel?>> =
        getQuizQuestion

    private val saveQuestionModel: MutableLiveData<NetworkStatus<AssessmentSaveQuestionResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val saveQuestionModelResponse: LiveData<NetworkStatus<AssessmentSaveQuestionResponseModel?>> =
        saveQuestionModel

    private val saveExamFolder: MutableLiveData<NetworkStatus<VerifyOTPResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val saveExamFolderResponse: LiveData<NetworkStatus<VerifyOTPResponseModel?>> = saveExamFolder

    private val saveExamImage: MutableLiveData<NetworkStatus<VerifyOTPResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val saveExamImageResponse: LiveData<NetworkStatus<VerifyOTPResponseModel?>> = saveExamImage

    private val submitQuiz: MutableLiveData<NetworkStatus<SubmitQuizResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val submitQuizResponse: LiveData<NetworkStatus<SubmitQuizResponseModel?>> = submitQuiz

    private val getReportPerformingData: MutableLiveData<NetworkStatus<ReportsPerformingResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val reportPerformingResponse: LiveData<NetworkStatus<ReportsPerformingResponse?>> =
        getReportPerformingData

    private val getMonthlyReportData: MutableLiveData<NetworkStatus<MonthlyReportResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val monthlyReportResponse: LiveData<NetworkStatus<MonthlyReportResponse?>> =
        getMonthlyReportData

    private val getReferalCodeData: MutableLiveData<NetworkStatus<ReferalCodeResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getReferalCodeResponse: LiveData<NetworkStatus<ReferalCodeResponse?>> = getReferalCodeData

    private val getBadgeAwardData: MutableLiveData<NetworkStatus<BadgeAwardDataResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getBadgeAwardDataResponse: LiveData<NetworkStatus<BadgeAwardDataResponse?>> =
        getBadgeAwardData

    private val getBadgesListResponse: MutableLiveData<NetworkStatus<BadgesListResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getBadgesListDataResponse: LiveData<NetworkStatus<BadgesListResponse?>> =
        getBadgesListResponse

    private val getLeaderboardData: MutableLiveData<NetworkStatus<LeaderboardDataResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val leaderboardResponse: LiveData<NetworkStatus<LeaderboardDataResponse?>> = getLeaderboardData

    private val getAllBadgesData: MutableLiveData<NetworkStatus<AllBadgesResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val allBadgesResponse: LiveData<NetworkStatus<AllBadgesResponse?>> = getAllBadgesData

    private val getAllAwardsData: MutableLiveData<NetworkStatus<AllAwardsResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val allAwardsResponse: LiveData<NetworkStatus<AllAwardsResponse?>> = getAllAwardsData

    private val getAllAwardsListData: MutableLiveData<NetworkStatus<AllAwardListResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val allAwardsListResponse: LiveData<NetworkStatus<AllAwardListResponse?>> = getAllAwardsListData

    private val getScore: MutableLiveData<NetworkStatus<ScoreCalculationResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getScoreResponse: LiveData<NetworkStatus<ScoreCalculationResponseModel?>> = getScore

    private val getWalletWinningAmountData: MutableLiveData<NetworkStatus<WalletWinningAmountResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val walletWinningAmountResponse: LiveData<NetworkStatus<WalletWinningAmountResponse?>> =
        getWalletWinningAmountData

    private val getQuizWinningStatusData: MutableLiveData<NetworkStatus<QuizWinningStatusResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val quizWinningStatusResponse: LiveData<NetworkStatus<QuizWinningStatusResponse?>> =
        getQuizWinningStatusData

    private val getTransactionHistoryData: MutableLiveData<NetworkStatus<TransactionHistoryResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val quizTransactionHistoryResponse: LiveData<NetworkStatus<TransactionHistoryResponse?>> =
        getTransactionHistoryData

    private val getQuizStatusDetailsData: MutableLiveData<NetworkStatus<QuizStatusDetailsResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val quizQuizStatusDetailsResponse: LiveData<NetworkStatus<QuizStatusDetailsResponse?>> =
        getQuizStatusDetailsData

    private val getPaymentCheckConfig: MutableLiveData<NetworkStatus<PaymentCheckConfigResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val paymentCheckConfigResponse: LiveData<NetworkStatus<PaymentCheckConfigResponse?>> =
        getPaymentCheckConfig

    private val getAddUpiData: MutableLiveData<NetworkStatus<AddUpiResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val addUpiResponse: LiveData<NetworkStatus<AddUpiResponse?>> = getAddUpiData

//    private val getAddUpiData: MutableLiveData<AddUpiResponse?> = MutableLiveData()
//    val addUpiResponse: LiveData<AddUpiResponse?> = getAddUpiData

    private val getCreateAccountData: MutableLiveData<NetworkStatus<CreateAccountResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val createAccountResponse: LiveData<NetworkStatus<CreateAccountResponse?>> =
        getCreateAccountData

    private val getStudentUpiListData: MutableLiveData<NetworkStatus<StudentUpiListResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val studentUpiListResponse: LiveData<NetworkStatus<StudentUpiListResponse?>> =
        getStudentUpiListData

    private val getStudentAccountsListData: MutableLiveData<NetworkStatus<StudentAccountsListResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val studentAccountsListResponse: LiveData<NetworkStatus<StudentAccountsListResponse?>> =
        getStudentAccountsListData

    private val getDeleteUpiBankData: MutableLiveData<NetworkStatus<DeleteUpiBankResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val deleteUpiBankResponse: LiveData<NetworkStatus<DeleteUpiBankResponse?>> =
        getDeleteUpiBankData

    private val getUpiTransactionData: MutableLiveData<NetworkStatus<UpiTransactionResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val upiTransactionResponse: LiveData<NetworkStatus<UpiTransactionResponse?>> =
        getUpiTransactionData

    private val getAccountTransactionData: MutableLiveData<NetworkStatus<AccountTransactionResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val accountTransactionResponse: LiveData<NetworkStatus<AccountTransactionResponse?>> =
        getAccountTransactionData

    private val percentBannerListResponseModel: MutableLiveData<NetworkStatus<GetBannerListResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val percentBannerListResponseModelLiveData: LiveData<NetworkStatus<GetBannerListResponseModel?>> =
        percentBannerListResponseModel

    private val getFaqResponse: MutableLiveData<NetworkStatus<FaqResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val faqResponse: LiveData<NetworkStatus<FaqResponse?>> = getFaqResponse

    private val getFaqCategoryResponse: MutableLiveData<NetworkStatus<FaqCategoryResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val faqCategoryResponse: LiveData<NetworkStatus<FaqCategoryResponse?>> = getFaqCategoryResponse

    init {
        fetchLanguages("1", "10")
    }

    private fun fetchLanguages(page: String, limit: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getLanguageRepo(page, limit).first()
                    _response.emit(NetworkStatus.Success(response))
                } catch (e: Exception) {
//                    val errorMessage = "An error occurred. Please try again."
                    _response.emit(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                _response.emit(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getStudentProgressCall() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getStudentProgressRepo().first()
                    studentDashboardProgress.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    studentDashboardProgress.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                studentDashboardProgress.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getSubjectList() {
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

    fun getStudentProfile() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getStudentProfileRepo().first()
                    getStudentProfile.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    getStudentProfile.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getStudentProfile.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun saveStudentProfile(id: Int, userRequest: AddStudent, image: File?) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.saveStudentProfileRepo(id, userRequest, image).first()
                    saveStudentProfile.postValue(NetworkStatus.Success(response))
                } catch (e: Exception) {
                    Log.d("onBoarding1:", "error " + e.message)
                    saveStudentProfile.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                saveStudentProfile.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }


    private val _latitude = MutableLiveData<Double>()
    private val _longitude = MutableLiveData<Double>()

    val latitude: LiveData<Double> get() = _latitude
    val longitude: LiveData<Double> get() = _longitude

    fun saveLatLung(lat: Double, long: Double) {
        // Update the value of MutableLiveData safely
        _latitude.postValue(lat)
        _longitude.postValue(long)
    }

    private val getAadhaarOTPredisposeModel: MutableLiveData<NetworkStatus<GetAadharOTPsendResposeModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getAadhaarOTPredisposeModellLiveData: LiveData<NetworkStatus<GetAadharOTPsendResposeModel?>> =
        getAadhaarOTPredisposeModel

    private val getAadhaarOTPVerifyResponseModel: MutableLiveData<NetworkStatus<GetAadharOTPVerifyResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getAadhaarOTPVerifyResponseModelLiveData: LiveData<NetworkStatus<GetAadharOTPVerifyResponseModel?>> =
        getAadhaarOTPVerifyResponseModel

    private val getSaveAadharDataRequestModel: MutableLiveData<NetworkStatus<SaveAdharResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getAadharSaveResponseMutableLiveData: LiveData<NetworkStatus<SaveAdharResponseModel?>> =
        getSaveAadharDataRequestModel

    fun getAadhaarOtpSend(requestModel: GetAadharOtpSendRequestModel) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getAadhaarOTPSend(requestModel).first()
                    getAadhaarOTPredisposeModel.postValue(NetworkStatus.Success(bannerResponse))
                    Log.d(
                        "AadhaarException:", "" + bannerResponse
                    )
                } catch (e: Exception) {
                    getAadhaarOTPredisposeModel.postValue(NetworkStatus.Error(e.message.toString()))
                    Log.d(
                        "AadhaarException1:", "" + e.message
                    )
                }
            } else {
                getAadhaarOTPredisposeModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getAadhaarOtpVerify(requestModel: GetAadharOTPVerifyRequestModel) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getAadhaarOtpVerify(requestModel).first()
                    getAadhaarOTPVerifyResponseModel.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    getAadhaarOTPVerifyResponseModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getAadhaarOTPVerifyResponseModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getSaveAadharData(requestModel: GetSaveAadharDataRequestModel) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getSaveAadhaarData(requestModel).first()
                    getSaveAadharDataRequestModel.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    getSaveAadharDataRequestModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getSaveAadharDataRequestModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getAssessmentConceptData(requestModel: AssessmentConceptsRequestModel) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getAssessmentConceptData(requestModel).first()
                    getConceptList.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    getConceptList.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getConceptList.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun saveAssessmentConceptData(requestModel: AssessmentSaveConceptRequestModel) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.saveAssessmentConceptData(requestModel).first()
                    saveConceptData.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    saveConceptData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                saveConceptData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getAssessmentWiseData(id: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val quizResponse = repository.getAssessmentWiseData(id).first()
                    getConceptWiseQuiz.postValue(NetworkStatus.Success(quizResponse))
                } catch (e: Exception) {
                    getConceptWiseQuiz.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getConceptWiseQuiz.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getAssessmentQuestionData(assessmentRequest: AssessmentGetQuizRequestModel) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val quizResponse = repository.getAssessmentQuizData(assessmentRequest).first()
                    getQuizQuestion.postValue(NetworkStatus.Success(quizResponse))
                } catch (e: Exception) {
                    getQuizQuestion.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getQuizQuestion.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun saveAssessmentQuestionData(assessmentRequest: AssessmentSaveQuestionRequestModel) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val quizResponse = repository.saveAssessmentQuizData(assessmentRequest).first()
                    saveQuestionModel.postValue(NetworkStatus.Success(quizResponse))
                } catch (e: Exception) {
                    saveQuestionModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                saveQuestionModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun saveAssessmentImageFolder(assessmentRequest: CreateExamImageRequestModel) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val quizResponse = repository.saveQuizExamImageFolder(assessmentRequest).first()
                    saveExamFolder.postValue(NetworkStatus.Success(quizResponse))
                } catch (e: Exception) {
                    saveExamFolder.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                saveExamFolder.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun saveQuizImage(examId: String, imageType: String, image: File) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val quizResponse = repository.getSaveQuizImage(examId, imageType, image).first()
                    saveExamImage.postValue(NetworkStatus.Success(quizResponse))
                } catch (e: Exception) {
                    saveExamImage.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                saveExamImage.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun submitQuiz(assessmentRequest: SubmitQuizRequestModel) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val quizResponse = repository.submitQuiz(assessmentRequest).first()
                    submitQuiz.postValue(NetworkStatus.Success(quizResponse))
                } catch (e: Exception) {
                    submitQuiz.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                submitQuiz.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    // Passport API
    fun getReportsPerformingData() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse = repository.getReportsPerformingData().first()
                    getReportPerformingData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getReportPerformingData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getReportPerformingData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getMonthlyReportDataData() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse = repository.getMonthlyReportData().first()
                    getMonthlyReportData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getMonthlyReportData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getMonthlyReportData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getReferalCode() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse = repository.getReferalCode().first()
                    getReferalCodeData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getReferalCodeData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getReferalCodeData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getBadgeAwardDataReport() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse = repository.getBadgeAwardDataReport().first()
                    getBadgeAwardData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getBadgeAwardData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getBadgeAwardData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getBadgesListReport() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse = repository.getBadgesListReport().first()
                    getBadgesListResponse.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getBadgesListResponse.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getBadgesListResponse.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getLeaderboardReport(
        passportType: String, scoreType: String, examMonth: String, page: Int, limit: Int,
    ) {
        val requestMap = JsonObject()
        requestMap.addProperty("passportType", passportType)
        requestMap.addProperty("scoreType", scoreType)
        requestMap.addProperty("examMonth", examMonth)
        requestMap.addProperty("page", page)
        requestMap.addProperty("limit", limit)

        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val leaderboardResponse = repository.getLeaderboardReport(requestMap).first()
                    getLeaderboardData.postValue(
                        NetworkStatus.Success(
                            leaderboardResponse
                        )
                    )
                } catch (e: Exception) {
                    getLeaderboardData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getLeaderboardData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getAllBadgesReport() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse = repository.getAllBadgesReport().first()
                    getAllBadgesData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getAllBadgesData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getAllBadgesData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getAllAwardListReport(frequency: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse =
                        repository.getAllAwardListReport(frequency).first()
                    getAllAwardsListData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getAllAwardsListData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getAllAwardsListData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getScoreCalculation() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse = repository.getScoreCalculations().first()
                    getScore.postValue(
                        NetworkStatus.Success(reportsPerformingResponse)
                    )
                } catch (e: Exception) {
                    getScore.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getScore.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getAllAwardsReport(location: String, frequency: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse =
                        repository.getAllAwardsReport(location, frequency).first()
                    getAllAwardsData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getAllAwardsData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getAllAwardsData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    // Wallet Panel Api date 21 Sept 2024
    fun getWalletWinningAmount(userId: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse =
                        repository.getWalletWinningAmount(userId).first()
                    getWalletWinningAmountData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getWalletWinningAmountData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getWalletWinningAmountData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getWalletQuizWinningStatus(userId: String, strExamMonth: String) {

        val requestMap = JsonObject()
        requestMap.addProperty("user_id", userId)
        requestMap.addProperty("examMonth", strExamMonth)
        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse =
                        repository.getWalletQuizWinningStatus(requestMap).first()
                    getQuizWinningStatusData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getQuizWinningStatusData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getQuizWinningStatusData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getTransactionHistory(
        srtTransType: String, strStatus: String, srtStartDate: String, strEndDate: String,
    ) {
        val requestMap = JsonObject()
        requestMap.addProperty("transactionType", srtTransType)
        requestMap.addProperty("status", strStatus)
        requestMap.addProperty("start_date", srtStartDate)
        requestMap.addProperty("end_date", strEndDate)
        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse =
                        repository.getTransactionHistory(requestMap).first()
                    getTransactionHistoryData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getTransactionHistoryData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getTransactionHistoryData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getQuizStatusDetails(
        srtUserId: String, strExamMonth: String,
    ) {
        val requestMap = JsonObject()
        requestMap.addProperty("user_id", srtUserId)
        requestMap.addProperty("examMonth", strExamMonth)
        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse =
                        repository.getQuizStatusDetails(requestMap).first()
                    getQuizStatusDetailsData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getQuizStatusDetailsData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getQuizStatusDetailsData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getPaymentCheckConfig() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse = repository.getPaymentCheckConfig().first()
                    getPaymentCheckConfig.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    println("Payment check config api error :- $e")
                    getPaymentCheckConfig.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getPaymentCheckConfig.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getValidateUpi(
        srtUpiId: String,
    ) {
        val requestMap = JsonObject()
        requestMap.addProperty("upi", srtUpiId)
        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse = repository.getValidateUpi(requestMap).first()
                    getAddUpiData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    println("Validation api error :- $e")
                    getAddUpiData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getAddUpiData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    /*fun getValidateUpi(
        srtUpiId: String
    ) {
        val requestMap = JsonObject()
        requestMap.addProperty("upi", srtUpiId)
        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val apiService = RetrofitInstance.api
                    val response = apiService.getValidateUpi(requestMap)
                    println("Validation api response :- $response")
//                    val reportsPerformingResponse = repository.getValidateUpi(requestMap).first()
//                    println("New Add UPI Response :- $reportsPerformingResponse")
                    getAddUpiData.postValue(
                         NetworkStatus.Success(
                             response.copy()
                         )
                    )
                } catch (e: Exception) {
                    println("New Add UPI Response Exception :- ${e.message.toString()}")
                    getAddUpiData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getAddUpiData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }*/

    fun getStudentUpi() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse = repository.getStudentUpi().first()
                    getStudentUpiListData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getStudentUpiListData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getStudentUpiListData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getStudentAccounts() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse = repository.getStudentAccounts().first()
                    getStudentAccountsListData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getStudentAccountsListData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getStudentAccountsListData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getCreateAccount(
        srtBankAccount: String, srtName: String, srtIfsc: String,
    ) {
        val requestMap = JsonObject()
        requestMap.addProperty("bankAccount", srtBankAccount)
        requestMap.addProperty("name", srtName)
        requestMap.addProperty("ifsc", srtIfsc)
        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse = repository.getCreateAccount(requestMap).first()
                    getCreateAccountData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getCreateAccountData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getCreateAccountData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getDeleteUpi(srtUpiId: String) {

        val requestMap = JsonObject()
        requestMap.addProperty("upiId", srtUpiId)
        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse = repository.getDeleteUpi(requestMap).first()
                    getDeleteUpiBankData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getDeleteUpiBankData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getDeleteUpiBankData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getDeleteAccount(srtBankAccount: String) {
        val requestMap = JsonObject()
        requestMap.addProperty("accountNumber", srtBankAccount)
        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse = repository.getDeleteAccount(requestMap).first()
                    getDeleteUpiBankData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getDeleteUpiBankData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getDeleteUpiBankData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getUpiTransaction(upiTransactionRequestModel: UpiTransactionRequestModel) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse =
                        repository.getUpiTransaction(upiTransactionRequestModel).first()
                    getUpiTransactionData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getUpiTransactionData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getUpiTransactionData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getAccountTransaction(accountTransactionRequestModel: AccountTransactionRequestModel) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse =
                        repository.getAccountTransaction(accountTransactionRequestModel).first()
                    getAccountTransactionData.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getAccountTransactionData.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getAccountTransactionData.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    /* fun getFaq(strId: String, languageId: Int, categoryId: Int) {
         viewModelScope.launch {
             if (CommonFunction.isNetworkAvailable(context)) {
                 try {
                     val reportsPerformingResponse = repository.getFaq(strId, languageId, categoryId).first()
                     getFaqResponse.postValue(
                         NetworkStatus.Success(
                             reportsPerformingResponse
                         )
                     )
                 } catch (e: Exception) {
                     getFaqResponse.postValue(NetworkStatus.Error(e.message.toString()))
                 }
             } else {
                 getFaqResponse.postValue(NetworkStatus.Error("No internet connection"))
             }
         }
     }*/

    fun getFaq(languageId: Int, userTypeId: Int, categoryId: Int) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse =
                        repository.getFaq(languageId, userTypeId, categoryId).first()
                    getFaqResponse.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getFaqResponse.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getFaqResponse.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getFaqCategory(languageId: Int, userTypeId: Int) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val reportsPerformingResponse =
                        repository.getFaqCategory(languageId, userTypeId).first()
                    getFaqCategoryResponse.postValue(
                        NetworkStatus.Success(
                            reportsPerformingResponse
                        )
                    )
                } catch (e: Exception) {
                    getFaqCategoryResponse.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getFaqCategoryResponse.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getStudentBanner() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getStudentBanner(2).first()
                    percentBannerListResponseModel.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    Log.d("ExceptionStudent:", "" + e.message)
                    percentBannerListResponseModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                percentBannerListResponseModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    private val getKycDocUplaodResposneModel: MutableLiveData<NetworkStatus<GetKycDocUplaodResposneModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getKycDocUplaodResposneModelLiveData: LiveData<NetworkStatus<GetKycDocUplaodResposneModel?>> =
        getKycDocUplaodResposneModel

    fun getKycDocUpload(doctype: String, studentId: String, isManual: String, file: File) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getKycAadhaarDocUpload(
                        docType = doctype, studentId = studentId, isManual = isManual, image = file
                    ).first()
                    getKycDocUplaodResposneModel.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    getKycDocUplaodResposneModel.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getKycDocUplaodResposneModel.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    private val getKycStatus: MutableLiveData<NetworkStatus<GetKycStatusResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getKycStateLiveData: LiveData<NetworkStatus<GetKycStatusResponseModel?>> = getKycStatus

    private val getKycFinish: MutableLiveData<NetworkStatus<SaveAdharResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getKycFinishData: LiveData<NetworkStatus<SaveAdharResponseModel?>> = getKycFinish

    private val getKycAadhaarStatus: MutableLiveData<NetworkStatus<GetKycAadhaarStatusResponseModel?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getKycAadhaarStatusLiveData: LiveData<NetworkStatus<GetKycAadhaarStatusResponseModel?>> =
        getKycAadhaarStatus

    private val getQuizAttemptResponse: MutableLiveData<NetworkStatus<QuizAttemptResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getQuizAttemptResponseData: LiveData<NetworkStatus<QuizAttemptResponse?>> =
        getQuizAttemptResponse

    private val getQuizScoreResponse: MutableLiveData<NetworkStatus<QuizScoreResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getQuizScoreResponseData: LiveData<NetworkStatus<QuizScoreResponse?>> = getQuizScoreResponse

    private val getTopWeakPerformingResponse: MutableLiveData<NetworkStatus<TopWeakPerformingResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getTopWeakPerformingResponseData: LiveData<NetworkStatus<TopWeakPerformingResponse?>> =
        getTopWeakPerformingResponse

    private val getQuizVerificationResponse: MutableLiveData<NetworkStatus<QuizVerificationResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getQuizVerificationResponseData: LiveData<NetworkStatus<QuizVerificationResponse?>> =
        getQuizVerificationResponse

    private val getGradeUpdateResponse: MutableLiveData<NetworkStatus<GradeUpdateResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val getGradeUpdateResponseData: LiveData<NetworkStatus<GradeUpdateResponse?>> =
        getGradeUpdateResponse

    private val saveReferralResponse: MutableLiveData<NetworkStatus<SaveReferralResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val saveReferralResponseData: LiveData<NetworkStatus<SaveReferralResponse?>> =
        saveReferralResponse

    private val savePracticeConceptsResponse: MutableLiveData<NetworkStatus<PracticeConceptsResponse?>> =
        MutableLiveData(NetworkStatus.Idle)
    val savePracticeConceptsResponseData: LiveData<NetworkStatus<PracticeConceptsResponse?>> =
        savePracticeConceptsResponse

    fun getKycStatus(studentId: Int) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getKycStatus(studentId).first()
                    getKycStatus.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    Log.d("aadhaarStatus:1", "" + e.message)
                    getKycStatus.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getKycStatus.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getKycFinish(studentId: Int) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val kycFinish = repository.getFinishKyc(studentId).first()
                    getKycFinish.postValue(NetworkStatus.Success(kycFinish))
                } catch (e: Exception) {
                    Log.d("aadhaarStatus:1", "" + e.message)
                    getKycFinish.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getKycFinish.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getKycAadhaarStatus(studentId: Int) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getKycAadhaarStatus(studentId).first()
                    getKycAadhaarStatus.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    Log.d("aadhaarStatus:1", "" + e.message)
                    getKycAadhaarStatus.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getKycAadhaarStatus.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getQuizAttempt(
        subjectId: String, concept: String, attempt: String, examMonth: String, win: String,
    ) {

        val requestMap = JsonObject()
        if (win == "") {
            requestMap.addProperty("subjectId", subjectId)
            requestMap.addProperty("concept", concept)
            requestMap.addProperty("attempt", attempt)
            requestMap.addProperty("examMonth", examMonth)
            println("Json request data :- $requestMap")
        } else {
            requestMap.addProperty("subjectId", subjectId)
            requestMap.addProperty("concept", concept)
            requestMap.addProperty("attempt", attempt)
            requestMap.addProperty("examMonth", examMonth)
            requestMap.addProperty("win", win)
            println("Json request data :- $requestMap")
        }

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getQuizAttempt(requestMap).first()
                    getQuizAttemptResponse.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    Log.d("getQuizAttemptResponse:", "" + e.message)
                    getQuizAttemptResponse.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getQuizAttemptResponse.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getQuizScoreReports(
        subjectId: String, examMonth: String, concept: String,
    ) {
        val requestMap = JsonObject()
        requestMap.addProperty("subjectId", subjectId)
        requestMap.addProperty("examMonth", examMonth)
        requestMap.addProperty("concept", concept)
        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getQuizScoreReports(requestMap).first()
                    getQuizScoreResponse.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    Log.d("getQuizAttemptResponse:", "" + e.message)
                    getQuizScoreResponse.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getQuizScoreResponse.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getWeakPerformingReports(
        userId: String, subjectId: String, concept: String,
    ) {
        val requestMap = JsonObject()
        requestMap.addProperty("userId", userId)
        requestMap.addProperty("subjectId", subjectId)
        requestMap.addProperty("concept", concept)
        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getWeakPerformingReports(requestMap).first()
                    getTopWeakPerformingResponse.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    Log.d("getQuizAttemptResponse:", "" + e.message)
                    getTopWeakPerformingResponse.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getTopWeakPerformingResponse.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getTopPerformingReports(
        userId: String, subjectId: String, concept: String,
    ) {
        val requestMap = JsonObject()
        requestMap.addProperty("userId", userId)
        requestMap.addProperty("subjectId", subjectId)
        requestMap.addProperty("concept", concept)
        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getTopPerformingReports(requestMap).first()
                    getTopWeakPerformingResponse.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    Log.d("getQuizAttemptResponse:", "" + e.message)
                    getTopWeakPerformingResponse.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getTopWeakPerformingResponse.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getQuizVerificationReports(
        userId: String, subjectId: String, date: String, status: String, concept: String,
    ) {
        val requestMap = JsonObject()
        if (status == "Inprogress") {
            requestMap.addProperty("userId", userId)
            requestMap.addProperty("subjectId", subjectId)
            requestMap.addProperty("date", date)
            requestMap.addProperty("status", "")
            requestMap.addProperty("concept", concept)
            println("Json request data :- $requestMap")
        } else {
            requestMap.addProperty("userId", userId)
            requestMap.addProperty("subjectId", subjectId)
            requestMap.addProperty("date", date)
            requestMap.addProperty("status", status)
            requestMap.addProperty("concept", concept)
            println("Json request data :- $requestMap")
        }

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getQuizVerificationReports(requestMap).first()
                    getQuizVerificationResponse.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    Log.d("getQuizAttemptResponse:", "" + e.message)
                    getQuizVerificationResponse.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getQuizVerificationResponse.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getGradeUpdate(strId: String) {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getGradeUpdate(strId).first()
                    getGradeUpdateResponse.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    Log.d("getQuizAttemptResponse:", "" + e.message)
                    getGradeUpdateResponse.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                getGradeUpdateResponse.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }


    fun saveReferralCode(
        referredBy: Int, referredUserId: Int, referredTypeId: Int, success: String, message: String,
    ) {

        val requestMap = JsonObject()
        requestMap.addProperty("referred_by", referredBy)
        requestMap.addProperty("referred_user_id", referredUserId)
        requestMap.addProperty("referred_type_id", referredTypeId)
        requestMap.addProperty("success", success)
        requestMap.addProperty("message", message)
        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.saveReferralCode(requestMap).first()
                    saveReferralResponse.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    Log.d("getQuizAttemptResponse:", "" + e.message)
                    saveReferralResponse.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                saveReferralResponse.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun getPracticeConcepts(
        subjectId: Int,
        studentGrade: Int,
        strConceptName: String,
        strByYearMonth: String,
    ) {
        // update
        val requestMap = JsonObject()
        if (strConceptName == "" && strByYearMonth == "") {
            requestMap.addProperty("subject", subjectId)
            requestMap.addProperty("grade", studentGrade)
        } else if (strConceptName != "") {
            requestMap.addProperty("subject", subjectId)
            requestMap.addProperty("grade", studentGrade)
            requestMap.addProperty("searchByConceptName", strConceptName)
        } else if (strByYearMonth != "") {
            requestMap.addProperty("subject", subjectId)
            requestMap.addProperty("grade", studentGrade)
            requestMap.addProperty("searchByYearMonth", strByYearMonth)
        } else {
            requestMap.addProperty("subject", subjectId)
            requestMap.addProperty("grade", studentGrade)
            requestMap.addProperty("searchByConceptName", strConceptName)
            requestMap.addProperty("searchByYearMonth", strByYearMonth)
        }
        println("Json request data :- $requestMap")

        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val bannerResponse = repository.getPracticeConcepts(requestMap).first()
                    savePracticeConceptsResponse.postValue(NetworkStatus.Success(bannerResponse))
                } catch (e: Exception) {
                    Log.d("getQuizPracticeResponse:", "" + e.message)
                    savePracticeConceptsResponse.postValue(NetworkStatus.Error(e.message.toString()))
                }
            } else {
                savePracticeConceptsResponse.postValue(NetworkStatus.Error("No internet connection"))
            }
        }
    }

    fun clearResponse() {
        viewModelScope.launch {
            getKycStatus.postValue(NetworkStatus.Idle)
            getKycDocUplaodResposneModel.postValue(NetworkStatus.Idle)
            getAadhaarOTPVerifyResponseModel.postValue(NetworkStatus.Idle)
            getAadhaarOTPredisposeModel.postValue(NetworkStatus.Idle)
//            getSaveAadharDataRequestModel.postValue(NetworkStatus.Idle)
        }
    }

    fun setBankUpi(strBankUPI: String) {
        sharedPref.setBankUpiData(strBankUPI)
    }

    fun getBankUpi(): String? {
        return sharedPref.getBankUpiData()
    }

    fun setBankUpiDirectionPage(strBankUPIDirection: String) {
        sharedPref.setBankUpiDirectionPage(strBankUPIDirection)
    }

    fun getBankUpiDirectionPage(): String? {
        return sharedPref.getBankUpiDirectionPage()
    }

    fun setWalletAmount(amount: String) {
        sharedPref.setWalletAmount(amount)
    }

    fun getWalletAmount(): String? {
        return sharedPref.getWalletAmount()
    }

    fun setWalletInfoData(winningWallet: WinningAmountData) {
        sharedPref.setWalletInfo(winningWallet)
    }

    fun getWalletInfoData(): WinningAmountData = sharedPref.getWalletInfo()

    fun setBankAccountUpiID(strBankAccountUpiID: String) {
        sharedPref.setBankAccountUpiID(strBankAccountUpiID)
    }

    fun getBankAccountUpiID(): String? {
        return sharedPref.getBankAccountUpiID()
    }

    fun setTransactionId(trnId: String) {
        sharedPref.setTransactionId(trnId)
    }

    fun getTransactionId(): String? {
        return sharedPref.getTransactionId()
    }

    fun setTransactionDateTime(trnDateTime: String) {
        sharedPref.setTransactionDateTime(trnDateTime)
    }

    fun getTransactionDateTime(): String? {
        return sharedPref.getTransactionDateTime()
    }

    fun setTransactionAmount(trnAmount: String) {
        sharedPref.setTransactionAmount(trnAmount)
    }

    fun getTransactionAmount(): String? {
        return sharedPref.getTransactionAmount()
    }

    fun setTransactionStatus(trnStatus: String) {
        sharedPref.setTransactionStatus(trnStatus)
    }

    fun getTransactionStatus(): String? {
        return sharedPref.getTransactionStatus()
    }

    fun setUPIListData(addedUpiDataList: List<UpiListData>) {
        sharedPref.setUPIListData(addedUpiDataList)
    }

    fun getUPIListData(): List<UpiListData> {
        return sharedPref.getUPIListData()
    }

    fun setStartDate(startDate: String) {
        sharedPref.setStartDate(startDate)
    }

    fun getStartDate(): String? {
        return sharedPref.getStartDate()
    }

    fun setEndDate(endDate: String) {
        sharedPref.setEndDate(endDate)
    }

    fun getEndDate(): String? {
        return sharedPref.getEndDate()
    }

    fun setReferredBy(referredBy: Int) {
        sharedPref.setReferredBy(referredBy)
    }

    fun getReferredBy(): Int? {
        return sharedPref.getReferredBy()
    }

    fun isReferred(isReferred: Boolean) {
        sharedPref.setReferredUserId(isReferred)
    }

    fun getIsReferred(): Boolean? {
        return sharedPref.getReferredUserId()
    }

    fun setReferredTypeId(referredTypeId: Int) {
        sharedPref.setReferredTypeId(referredTypeId)
    }

    fun getReferredTypeId(): Int? {
        return sharedPref.getReferredTypeId()
    }

    fun getStudentPracticeSelectedSubjectData(): GetSubjectListResponseModel.Data =
        sharedPref.getPracticeSelectedSubjectInfo()

    fun setStudentPracticeSelectedSubjectData(selectedSubject: GetSubjectListResponseModel.Data) {
        sharedPref.setPracticeSelectedSubjectInfo(selectedSubject)
    }

    fun setSelectedDate(selectedDate: String) {
        sharedPref.setSelectedDate(selectedDate)
    }

    fun getSelectedDate(): String? {
        return sharedPref.getSelectedDate()
    }

    /* fun setPracticePage(practicePage: String) {
         sharedPref.setPracticePage(practicePage)
     }

     fun getPracticePage(): String? {
         return sharedPref.getPracticePage()
     }*/
}
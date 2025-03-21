package com.auro.application.repository

import com.auro.application.data.api.ApiService
import com.auro.application.repository.models.GetLanguageListResponse
import com.auro.application.ui.features.login.models.CheckAutoLoginResponseModel
import com.auro.application.ui.features.login.screens.models.AddStudent
import com.auro.application.ui.features.login.screens.models.GetBoardListResponseModel
import com.auro.application.ui.features.login.screens.models.GetDistrictResponseModel
import com.auro.application.ui.features.login.screens.models.GetSchoolLIstResponseModel
import com.auro.application.ui.features.login.screens.models.GetStateListResponseModel
import com.auro.application.ui.features.login.screens.models.GetStudentAddResponseModel
import com.auro.application.ui.features.login.screens.models.SetPinRequestModel
import com.auro.application.ui.features.login.screens.models.SetPinResponseModel
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
import com.auro.application.ui.features.login.screens.models.ParentModel
import com.auro.application.ui.features.login.screens.models.LoginWithPinRequestModel
import com.auro.application.ui.features.login.screens.models.ParentWalkthroughRequest
import com.auro.application.ui.features.login.screens.models.ResetPinRequestModel
import com.auro.application.ui.features.login.viewmodel.LanguageRequest
import com.auro.application.ui.features.login.viewmodel.Request
import com.auro.application.ui.features.parent.model.GetBannerListResponseModel
import com.auro.application.ui.features.student.authentication.model.GetAadharOTPVerifyRequestModel
import com.auro.application.ui.features.student.authentication.model.GetAadharOTPVerifyResponseModel
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
import com.auro.application.ui.features.student.models.SaveStudentProfileResponseModel
import com.auro.application.ui.features.student.authentication.model.GetKycDocUplaodResposneModel
import com.auro.application.ui.features.student.authentication.model.GetKycStatusResponseModel
import com.auro.application.ui.features.student.authentication.model.GetSaveAadharDataRequestModel
import com.auro.application.ui.features.student.authentication.model.SaveAdharResponseModel
import com.auro.application.ui.features.student.models.FaqCategoryResponse
import com.auro.application.ui.features.student.models.FaqResponse
import com.auro.application.ui.features.student.models.GradeUpdateResponse
import com.auro.application.ui.features.student.models.SaveReferralModel
import com.auro.application.ui.features.student.models.SaveReferralResponse
import com.auro.application.ui.features.student.models.StudentDashboardProgressResponseModel
import com.auro.application.ui.features.student.models.StudentProfileResponseModel
import com.auro.application.ui.features.student.partner.models.PartnerDetailsResponse
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
import com.auro.application.ui.features.student.wallet.Models.TransactionHistoryResponse
import com.auro.application.ui.features.student.wallet.Models.UpiTransactionRequestModel
import com.auro.application.ui.features.student.wallet.Models.UpiTransactionResponse
import com.auro.application.ui.features.student.wallet.Models.WalletWinningAmountResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

class StudentRepository @Inject constructor(private val apiService: ApiService) {

    fun getLanguageRepo(page: String, limit: String): Flow<GetLanguageListResponse> = flow {
        val response = apiService.getListOfLanguages(page = page, limit = limit)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getUerTypeList(): Flow<GetUserTypeListResponseModel> = flow {
        val response = apiService.getCheckAdminUserType()
        emit(response)
    }.flowOn(Dispatchers.IO)


    fun getCheckPhoneNoRepo(phone: Request): Flow<CheckPhoneNoResponseModel> = flow {
        try {
            val response = apiService.getCheckUserPhoneNo(phone)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }

    }.flowOn(Dispatchers.IO)



    fun getCheckAutoLoginRepo(phone: Request,partnerid: Request, userid: Request, forcepartner: Request, addnew: Request ): Flow<CheckAutoLoginResponseModel> = flow {
        try {
            val response = apiService.getAutoLogin(phone,partnerid,userid,forcepartner,addnew)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }

    }.flowOn(Dispatchers.IO)

    /* fun getLanguageTranslationRepo(langId: LanguageRequest): Flow<LanguageTranslationsResponse> =
         flow {
             try {
                 val response = apiService.getLanguageTranslations(langId)
                 emit(response)
             } catch (http: HttpException) {
                 throw Exception(
                     getErrorMessage(
                         http.response()?.errorBody()?.string()
                     )
                 ) // or emit a custom error model if needed
             }

         }.flowOn(Dispatchers.IO)*/

    fun getTranslationsLanguage(langId: LanguageRequest): Flow<TranslationsLanguageResponse> =
        flow {
            try {
                val response = apiService.getTranslationsLanguage(langId)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }

        }.flowOn(Dispatchers.IO)

    fun getForceUpdateRepo(jsonObject: JsonObject): Flow<AssessmentSaveQuestionResponseModel> =
        flow {
            try {
                val response = apiService.getForceUpdate(jsonObject)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }

        }.flowOn(Dispatchers.IO)

    fun saveReferralCode(jsonObject: JsonObject): Flow<SaveReferralResponse> =
        flow {
            try {
                val response = apiService.saveReferralCode(jsonObject)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }

        }.flowOn(Dispatchers.IO)

    fun getPracticeConcepts(jsonObject: JsonObject): Flow<PracticeConceptsResponse> =
        flow {
            try {
                val response = apiService.getPracticeConcepts(jsonObject)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }

        }.flowOn(Dispatchers.IO)

    fun getCheckUsername(phone: HashMap<String, String>): Flow<CheckPhoneNoResponseModel> = flow {
        try {
            val response = apiService.getCheckUsername(phone)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }

    }.flowOn(Dispatchers.IO)

    fun getCheckUsernameExisting(phone: HashMap<String, String>): Flow<CheckUserNameResponseModel> =
        flow {
            try {
                val response = apiService.getCheckExistingUsername(phone)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }

        }.flowOn(Dispatchers.IO)

    fun getAddStudent(userRequest: AddStudent, image: File?): Flow<GetStudentAddResponseModel> =
        flow {
            // Create RequestBody instances
            val name =
                RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.name.toString())
            val userName = RequestBody.create(
                "text/plain".toMediaTypeOrNull(), userRequest.userName.toString()
            )
            val email =
                RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.email.toString())
            val gender =
                RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.gender.toString())
            val dob =
                RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.dob.toString())
            val state =
                RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.state.toString())
            val district = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                userRequest.district.toString()
            )
            val pinCode =
                RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.pinCode.toString())
            val alternativePhone = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                userRequest.alternativePhone.toString()
            )
            val grade =
                RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.grade.toString())
            val board =
                RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.board.toString())
            val language = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                userRequest.language.toString()
            )
            val school =
                RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.school.toString())
            val schoolOther =
                RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    userRequest.otherSchool.toString()
                )
            val userTypeId = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                userRequest.userTypeId.toString()
            )
            val medium = userRequest.medium?.let {
                RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    it.toString()
                )
            }

            // Prepare image part if available
            val imagePart = image?.let {
                val mediaType = "image/jpeg".toMediaTypeOrNull()
                MultipartBody.Part.createFormData("image", it.name, it.asRequestBody(mediaType))
            }

            try {
                // Call the API with the parts
                val response = apiService.getAddStudent(
                    name,
                    userName,
                    email,
                    gender,
                    dob,
                    state,
                    district,
                    pinCode,
                    alternativePhone,
                    grade,
                    board,
                    language,
                    school,
                    userTypeId,
                    medium,
                    schoolOther,
                    imagePart
                )
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)


    fun getParentProfile(addStudent: ParentModel): Flow<ParentRegistationResponseModel> = flow {
        try {
            val response = apiService.getParentProfile(addStudent)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getParentWalkthrough(addStudent: ParentWalkthroughRequest): Flow<ParentRegistationResponseModel> =
        flow {
            try {
                val response = apiService.getParentWalkthrough(addStudent)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getStudentProfileRepo(): Flow<StudentProfileResponseModel> = flow {
        try {
            val response = apiService.getStudentProfile()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun saveStudentProfileRepo(
        id: Int, userRequest: AddStudent, image: File?,
    ): Flow<SaveStudentProfileResponseModel> = flow {
        val name = RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.name.toString())
        val userName =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.userName.toString())
        val email =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.email.toString())
        val gender =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.gender.toString())
        val dob = RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.dob.toString())
        val state =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.state.toString())
        val district =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.district.toString())
        val pinCode =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.pinCode.toString())
        val otherSchool =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.otherSchool.toString())
        val alternativePhone = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            userRequest.alternativePhone.toString()
        )
        val grade =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.grade.toString())
        val board =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.board.toString())
//        val language = RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.language.toString())
        val school =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.school.toString())
        val userTypeId =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userRequest.userTypeId.toString())
        val medium = userRequest.medium?.let {
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                it.toString()
            )
        }
        // Prepare image part if available
        val imagePart = image?.let {
            val mediaType = "image/jpeg".toMediaTypeOrNull()
            MultipartBody.Part.createFormData("image", it.name, it.asRequestBody(mediaType))
        }
        try {
            val response = apiService.saveStudentProfile(
                id,
                name,
                userName,
                email,
                gender,
                dob,
                state,
                district,
                pinCode,
                alternativePhone,
                grade,
                board,
                school,
                userTypeId,
                medium,
                otherSchool,
                imagePart
            )
            emit(response)
        } catch (http: HttpException) {

            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed

        }
    }.flowOn(Dispatchers.IO)

    fun getSendOtp(phone: SendOTPRequestModel): Flow<SendOTPResponseModel> = flow {
        val response = apiService.getSendOtp(phone)
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getSendOtpOnCall(phone: SendOTPRequestModel): Flow<SendOTPResponseModel> = flow {
        val response = apiService.getSendOtpOnCall(phone)
        emit(response)
    }.flowOn(Dispatchers.IO)


    fun getVerifyOtp(phone: VerifyOTPRequestModel): Flow<VerifyOTPResponseModel> = flow {
        try {
            val response = apiService.getVerifyOtp(phone)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getUserLoginRepo(phone: UserLoginRequestModel): Flow<UserLoginResponseModel> = flow {
        try {
            val response = apiService.getLoginUser(phone)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getPasswordCreate(phone: PasswordCreateRequestModel): Flow<UserLoginResponseModel> =
        flow {
            try {
                val response = apiService.getPasswordCreate(phone)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getResetPassword(jsonObject: JsonObject): Flow<PasswordResetRequestModel> =
        flow {
            try {
                val response = apiService.getResetPassword(jsonObject)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)


    fun getStateList(): Flow<GetStateListResponseModel> = flow {
        try {
            val response = apiService.getStateList()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getDistrictList(state: Int): Flow<GetDistrictResponseModel> = flow {
        try {
            val response = apiService.getDistrictList(state)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getSchoolList(districtId: Int, search: String): Flow<GetSchoolLIstResponseModel> = flow {
        try {
            val response = apiService.getSchoolList(districtId, search)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getBoardList(page: Int, limit: String): Flow<GetBoardListResponseModel> = flow {
        try {
            val response = apiService.getBoardList(page = page.toString(), limit = limit)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getSetPin(setPinRequestModel: SetPinRequestModel): Flow<SetPinResponseModel> = flow {
        try {
            val response = apiService.getSetPin(setPinRequestModel)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getResetPin(setPinRequestModel: ResetPinRequestModel): Flow<SetPinResponseModel> = flow {
        try {
            val response = apiService.getResetPin(setPinRequestModel)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getSubjectList(): Flow<GetSubjectListResponseModel> = flow {
        try {
            val response = apiService.getSubject()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getSubjectPreferenceSave(request: GetSubjectPrefrenceSaveRequestModel): Flow<GetSubjectPrefrenceSaveResponseModel> =
        flow {
            try {
                val response = apiService.getSubjectPreferenceSave(request)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)


    /* fun getNoticeTypeList(request: HashMap<String, String>): Flow<GetNoticeTypeListResponseModel> =
         flow {
             try {
                 val response = apiService.getNoticeTypeList(request)
                 emit(response)
             } catch (http: HttpException) {
                 throw Exception(
                     getErrorMessage(
                         http.response()?.errorBody()?.string()
                     )
                 ) // or emit a custom error model if needed
             }
         }.flowOn(Dispatchers.IO)*/

    fun getNoticeTypeList(strEventType: String): Flow<GetNoticeTypeListResponseModel> =
        flow {
            try {
                val response = apiService.getNoticeTypeList(strEventType)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getDisclaimer(
        languageId: Int?,
        eventId: Int,
        userTypeId: Int,
    ): Flow<GetDisclaimerResponseModel> = flow {
        try {
            val response = apiService.getDisclaimer(languageId, eventId, userTypeId)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)


    fun getDisclaimerSave(request: HashMap<String, Int>): Flow<GetDisclamerAccpetResponseModel> =
        flow {
            try {
                val response = apiService.getDisclaimerAccept(request)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getChildList(): Flow<ChildListResponse> = flow {
        try {
            val response = apiService.getChildList()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getUserLoginWithOtpRepo(phone: VerifyOTPRequestModel): Flow<UserLoginResponseModel> =
        flow {
            try {
                val response = apiService.getLoginWithOtp(phone)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getUserLoginWithPinRepo(phone: LoginWithPinRequestModel): Flow<UserLoginResponseModel> =
        flow {
            try {
                val response = apiService.getLoginWithPin(phone)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(getErrorMessage(http.response()?.errorBody()?.string()))
            }
        }.flowOn(Dispatchers.IO)

    fun getStudentProgressRepo(): Flow<StudentDashboardProgressResponseModel> =
        flow {
            try {
                val response = apiService.getStudentDashboardProgress()
                emit(response)
            } catch (http: HttpException) {
                throw Exception(getErrorMessage(http.response()?.errorBody()?.string()))
            }
        }.flowOn(Dispatchers.IO)

    fun getAadhaarOTPSend(requestMap: GetAadharOtpSendRequestModel): Flow<GetAadharOTPsendResposeModel> =
        flow {
            try {
                val response = apiService.getSendAadharOtp(requestMap)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)


    fun getAadhaarOtpVerify(requestMap: GetAadharOTPVerifyRequestModel): Flow<GetAadharOTPVerifyResponseModel> =
        flow {
            try {
                val response = apiService.getVerifyAadhaarOtp(requestMap)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)


    fun getSaveAadhaarData(requestMap: GetSaveAadharDataRequestModel): Flow<SaveAdharResponseModel> =
        flow {
            try {
                val response = apiService.getSaveAadharData(requestMap)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getKycAadhaarDocUpload(
        docType: String,
        studentId: String,
        isManual: String,
        image: File?,
    ): Flow<GetKycDocUplaodResposneModel> =
        flow {
            // Create RequestBody instances
            val doc = RequestBody.create("text/plain".toMediaTypeOrNull(), docType)
            val studentIdRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), studentId)
            val isManualRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), isManual)
            // Prepare image part if available
            val imagePart = image?.let {
                val mediaType = "image/jpeg".toMediaTypeOrNull()
                MultipartBody.Part.createFormData("image", it.name, it.asRequestBody(mediaType))
            }

            try {
                // Call the API with the parts
                val response = apiService.getKycDocUpload(
                    doc, studentIdRequest, isManualRequest, imagePart
                )
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getKycStatus(studentId: Int): Flow<GetKycStatusResponseModel> = flow {
        try {
            // Call the API with the parts
            val response = apiService.getKycStatus(studentId)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getFinishKyc(studentId: Int): Flow<SaveAdharResponseModel> = flow {
        try {
            // Call the API with the parts
            val response = apiService.getFinishKyc(studentId)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getKycAadhaarStatus(studentId: Int): Flow<GetKycAadhaarStatusResponseModel> = flow {
        try {
            // Call the API with the parts
            val response = apiService.getKycAadhaarStatus(studentId)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getInactiveUserRepo(): Flow<DeleteAccountUserResponseModel> = flow {
        try {
            val response = apiService.getInactiveUser()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getRecoverUserRepo(): Flow<DeleteAccountUserResponseModel> = flow {
        try {
            val response = apiService.getRecoverUser()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getAssessmentConceptData(requestMap: AssessmentConceptsRequestModel): Flow<AssessmentConceptsResponseModel> =
        flow {
            try {
                val response = apiService.getAssessmentConcept(requestMap)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun saveAssessmentConceptData(requestMap: AssessmentSaveConceptRequestModel): Flow<VerifyOTPResponseModel> =
        flow {
            try {
                val response = apiService.saveAssessmentConcept(requestMap)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getAssessmentWiseData(id: String): Flow<ConceptWiseQuizResponseModel> =
        flow {
            try {
                val response = apiService.getConceptWiseQuiz(id)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getAssessmentQuizData(assessmentRequest: AssessmentGetQuizRequestModel): Flow<AssessmentQuizQuestionsResponseModel> =
        flow {
            try {
                val response = apiService.getQuizQuestions(assessmentRequest)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun saveAssessmentQuizData(assessmentRequest: AssessmentSaveQuestionRequestModel): Flow<AssessmentSaveQuestionResponseModel> =
        flow {
            try {
                val response = apiService.saveQuizQuestions(assessmentRequest)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getSaveQuizImage(
        examId: String,
        imageType: String,
        imagePath: File?,
    ): Flow<VerifyOTPResponseModel> =
        flow {
            // Create RequestBody instances
            val examId = RequestBody.create("text/plain".toMediaTypeOrNull(), examId)
            val imageType = RequestBody.create("text/plain".toMediaTypeOrNull(), imageType)
            // Prepare image part if available
            /* val imagePart = image?.let {
                 val mediaType = "image/jpeg".toMediaTypeOrNull()
                 MultipartBody.Part.createFormData("image", it.name, it.asRequestBody(mediaType))
             }*/
//            val file = File(imagePath!!)
//            if (!file.exists()) return
            val imagePart = imagePath?.let {
                val mediaType = "image/jpeg".toMediaTypeOrNull()
                MultipartBody.Part.createFormData("image", it.name, it.asRequestBody(mediaType))
            }

            try {
                // Call the API with the parts
                val response = apiService.saveQuizImage(
                    examId, imageType, imagePart!!
                )
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun saveQuizExamImageFolder(examId: CreateExamImageRequestModel): Flow<VerifyOTPResponseModel> =
        flow {
            // Create RequestBody instances

            try {
                // Call the API with the parts
                val response = apiService.saveExamImageFolder(
                    examId
                )
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun submitQuiz(assessmentRequest: SubmitQuizRequestModel): Flow<SubmitQuizResponseModel> =
        flow {
            try {
                val response = apiService.submitQuiz(assessmentRequest)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)


    // Passport API
    fun getReportsPerformingData(): Flow<ReportsPerformingResponse> = flow {
        try {
            val response = apiService.getReportsPerforming()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getMonthlyReportData(): Flow<MonthlyReportResponse> = flow {
        try {
            val response = apiService.getMonthlyReport()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getReferalCode(): Flow<ReferalCodeResponse> = flow {
        try {
            val response = apiService.getReferalCode()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getBadgeAwardDataReport(): Flow<BadgeAwardDataResponse> = flow {
        try {
            val response = apiService.getBadgeAwardDataReport()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getBadgesListReport(): Flow<BadgesListResponse> = flow {
        try {
            val response = apiService.getBadgesListReport()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getLeaderboardReport(jsonObject: JsonObject): Flow<LeaderboardDataResponse> = flow {
        try {
            val response = apiService.getLeaderboardReport(jsonObject)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getAllBadgesReport(): Flow<AllBadgesResponse> = flow {
        try {
            val response = apiService.getAllBadgesReport()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getScoreCalculations(): Flow<ScoreCalculationResponseModel> = flow {
        try {
            val response = apiService.getScoreCalculationApi()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getAllAwardListReport(frequency: String): Flow<AllAwardListResponse> = flow {
        try {
            val response = apiService.getAllAwardListReport(frequency)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getAllAwardsReport(location: String, frequency: String): Flow<AllAwardsResponse> = flow {
        try {
            val response = apiService.getAllAwardsReport(location, frequency)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    // Wallet Panel Api date 21 Sept 2024
    fun getWalletWinningAmount(userId: String): Flow<WalletWinningAmountResponse> = flow {
        try {
            val response = apiService.getWalletWinningAmount(userId)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getWalletQuizWinningStatus(jsonObject: JsonObject): Flow<QuizWinningStatusResponse> = flow {
        try {
            val response = apiService.getWalletQuizWinningStatus(jsonObject)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getTransactionHistory(jsonObject: JsonObject): Flow<TransactionHistoryResponse> = flow {
        try {
            val response = apiService.getTransactionHistory(jsonObject)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getQuizStatusDetails(jsonObject: JsonObject): Flow<QuizStatusDetailsResponse> = flow {
        try {
            val response = apiService.getQuizStatusDetails(jsonObject)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getPaymentCheckConfig(): Flow<PaymentCheckConfigResponse> = flow {
        try {
            val response = apiService.getPaymentCheckConfig()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getValidateUpi(jsonObject: JsonObject): Flow<AddUpiResponse> = flow {
        try {
            val response = apiService.getValidateUpi(jsonObject)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    /* fun getValidateUpi(jsonObject: JsonObject): Flow<AddUpiResponse> = flow {
         try {
             val apiServices = RetrofitInstance.api
             val response = apiServices.getValidateUpi(jsonObject)
             emit(response)
         } catch (http: HttpException) {
             throw Exception(
                 getErrorMessage(
                     http.response()?.errorBody()?.string()
                 )
             ) // or emit a custom error model if needed
         }
     }.flowOn(Dispatchers.IO)*/

    fun getStudentUpi(): Flow<StudentUpiListResponse> = flow {
        try {
            val response = apiService.getStudentUpi()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getStudentAccounts(): Flow<StudentAccountsListResponse> = flow {
        try {
            val response = apiService.getStudentAccounts()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getStudentBanner(userType: Int): Flow<GetBannerListResponseModel> = flow {
        try {
            val response = apiService.getBannerList(userType)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getCreateAccount(jsonObject: JsonObject): Flow<CreateAccountResponse> = flow {
        try {
            val response = apiService.getCreateAccount(jsonObject)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getDeleteUpi(jsonObject: JsonObject): Flow<DeleteUpiBankResponse> = flow {
        try {
            val response = apiService.getDeleteUpi(jsonObject)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getDeleteAccount(jsonObject: JsonObject): Flow<DeleteUpiBankResponse> = flow {
        try {
            println("Account Number :- $jsonObject")
            val response = apiService.getDeleteAccount(jsonObject)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getUpiTransaction(upiTransactionRequestModel: UpiTransactionRequestModel): Flow<UpiTransactionResponse> =
        flow {
            try {
                val response = apiService.getUpiTransaction(upiTransactionRequestModel)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getAccountTransaction(accountTransactionRequestModel: AccountTransactionRequestModel): Flow<AccountTransactionResponse> =
        flow {
            try {
                val response = apiService.getAccountTransaction(accountTransactionRequestModel)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    /*fun getFaq(strId: String, languageId: Int, categoryId: Int): Flow<FaqResponse> =
        flow {
            try {
                val response = apiService.getFaq(strId, languageId, *//*categoryId*//*)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)*/

    fun getFaq(languageId: Int, userTypeId: Int, categoryId: Int): Flow<FaqResponse> =
        flow {
            try {
                val response = apiService.getFaq(languageId, userTypeId, categoryId)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getFaqCategory(languageId: Int, userTypeId: Int): Flow<FaqCategoryResponse> =
        flow {
            try {
                val response = apiService.getFaqCategory(languageId, userTypeId)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getQuizAttempt(jsonObject: JsonObject): Flow<QuizAttemptResponse> =
        flow {
            try {
                val response = apiService.getQuizAttempt(jsonObject)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getQuizScoreReports(jsonObject: JsonObject): Flow<QuizScoreResponse> =
        flow {
            try {
                val response = apiService.getQuizScoreReports(jsonObject)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getWeakPerformingReports(jsonObject: JsonObject): Flow<TopWeakPerformingResponse> =
        flow {
            try {
                val response = apiService.getWeakPerformingReports(jsonObject)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getTopPerformingReports(jsonObject: JsonObject): Flow<TopWeakPerformingResponse> =
        flow {
            try {
                val response = apiService.getTopPerformingReports(jsonObject)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getQuizVerificationReports(jsonObject: JsonObject): Flow<QuizVerificationResponse> =
        flow {
            try {
                val response = apiService.getQuizVerificationReports(jsonObject)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getPartnerDetails(strName: String): Flow<PartnerDetailsResponse> =
        flow {
            try {
                val response = apiService.getPartnerDetails(strName)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getGradeUpdate(strId: String): Flow<GradeUpdateResponse> =
        flow {
            try {
                val response = apiService.getGradeUpdate(strId)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)
}

private fun getErrorMessage(errorBody: String?): String {
    return try {
        // Parse the error body and extract the key error message
        val jsonObject = JSONObject(errorBody ?: "")
        jsonObject.getString("error")
    } catch (e: JSONException) {
        // If parsing fails, return a default message
        "An error occurred."
    }
}
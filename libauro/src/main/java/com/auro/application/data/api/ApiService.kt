package com.auro.application.data.api

import com.auro.application.data.api.Constants.GET_API_VERSION
import com.auro.application.repository.models.GetAppVersionResponseModel
import com.auro.application.repository.models.GetLanguageListResponse
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
import com.auro.application.ui.features.login.models.VerifyOTPResponseModel
import com.auro.application.ui.features.login.models.SendOTPResponseModel
import com.auro.application.ui.features.login.models.SendOTPRequestModel
import com.auro.application.ui.features.login.models.TranslationsLanguageResponse
import com.auro.application.ui.features.login.models.UserLoginRequestModel
import com.auro.application.ui.features.login.models.UserLoginResponseModel
import com.auro.application.ui.features.login.models.VerifyOTPRequestModel
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
import com.auro.application.ui.features.login.viewmodel.LanguageRequest
import com.auro.application.ui.features.login.viewmodel.Request
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
import com.auro.application.ui.features.student.authentication.model.GetAadharOTPVerifyRequestModel
import com.auro.application.ui.features.student.authentication.model.GetAadharOTPVerifyResponseModel
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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET(GET_API_VERSION)
    suspend fun getAppVersion(): GetAppVersionResponseModel

    /* @POST("v1/student/get-translation-data")  // language translations Api
     suspend fun getLanguageTranslations(@Body languageId: LanguageRequest): LanguageTranslationsResponse*/

    @POST("v1/student/get-translations")  // language translations Api
    suspend fun getTranslationsLanguage(@Body languageId: LanguageRequest): TranslationsLanguageResponse

    @GET("v1/student/admin/languages")
    suspend fun getListOfLanguages(
        @Query("page") page: String, @Query("limit") limit: String,
    ): GetLanguageListResponse

    @GET("v1/student/admin/userType")
    suspend fun getCheckAdminUserType(): GetUserTypeListResponseModel

    @POST("v1/student/user/validate")  // using in login Screen
    suspend fun getCheckUserPhoneNo(@Body user_mobile: Request): CheckPhoneNoResponseModel

    //    @POST("v1/student/user/validate/username")          // as it was required earlier
    @POST("v1/student/user/validate") // using in registration
    suspend fun getCheckUsername(@Body username: HashMap<String, String>): CheckPhoneNoResponseModel

    @POST("v1/student/user/check/username") // using in registration
    suspend fun getCheckExistingUsername(@Body username: HashMap<String, String>): CheckUserNameResponseModel

    @POST("v1/student/send-otp")
    suspend fun getSendOtp(@Body sendOtp: SendOTPRequestModel): SendOTPResponseModel

    @POST("v1/student/ivr-otp")
    suspend fun getSendOtpOnCall(@Body sendOtp: SendOTPRequestModel): SendOTPResponseModel

    @POST("v1/student/verify-otp")
    suspend fun getVerifyOtp(@Body verifyOtp: VerifyOTPRequestModel): VerifyOTPResponseModel

    @POST("v1/student/user/password/create")
    suspend fun getPasswordCreate(@Body verifyOtp: PasswordCreateRequestModel): UserLoginResponseModel

    @POST("v1/student/user/resetPassword")
    suspend fun getResetPassword(@Body jsonObject: JsonObject): PasswordResetRequestModel

    @POST("v1/student/user/loginWithPassword")
    suspend fun getLoginUser(@Body userLogin: UserLoginRequestModel): UserLoginResponseModel

    @Multipart
    @POST("v1/student/student")
    suspend fun getAddStudent(
        @Part("name") name: RequestBody,
        @Part("userName") userName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("state") state: RequestBody,
        @Part("district") district: RequestBody,
        @Part("pinCode") pinCode: RequestBody,
        @Part("alternativePhone") alternativePhone: RequestBody,
        @Part("grade") grade: RequestBody,
        @Part("board") board: RequestBody,
        @Part("language") language: RequestBody,
        @Part("school") school: RequestBody,
        @Part("userTypeId") userTypeId: RequestBody,
        @Part("medium") medium: RequestBody?,
        @Part("otherSchool") otherSchool: RequestBody?,
        @Part image: MultipartBody.Part?, // Optional image part
    ): GetStudentAddResponseModel

    @GET("v1/student/admin/states")
    suspend fun getStateList(
        @Query("page") page: String = "1", @Query("limit") limit: String = "40",
    ): GetStateListResponseModel

    @GET("v1/student/admin/stateWiseDistrict/{stateId}")
    suspend fun getDistrictList(@Path("stateId") stateId: Int): GetDistrictResponseModel

    @GET("v1/student/admin/schools/")
    suspend fun getSchoolList(
        @Query("district") districtId: Int, @Query("search") search: String,
    ): GetSchoolLIstResponseModel

    @GET("v1/student/admin/boards/")
    suspend fun getBoardList(
        @Query("page") page: String = "1", @Query("limit") limit: String = "36",
    ): GetBoardListResponseModel

    @POST("v1/student/user/setPin")
    suspend fun getSetPin(@Body setPinRequestModel: SetPinRequestModel): SetPinResponseModel

    @POST("v1/student/user/resetPin")
    suspend fun getResetPin(@Body setPinRequestModel: ResetPinRequestModel): SetPinResponseModel

    @GET("v1/student/subject/preference")       // update new
    suspend fun getSubject(): GetSubjectListResponseModel

    @POST("v1/student/subject/preference")   //
    suspend fun getSubjectPreferenceSave(@Body username: GetSubjectPrefrenceSaveRequestModel): GetSubjectPrefrenceSaveResponseModel

//    @POST("v1/student/admin/events")
//    suspend fun getNoticeTypeList(@Body username: HashMap<String, String>): GetNoticeTypeListResponseModel

    @GET("v1/student/admin/events")
    suspend fun getNoticeTypeList(@Query("eventType") strEventType: String): GetNoticeTypeListResponseModel

    @GET("v1/student/disclaimers/{languageId}")
    suspend fun getDisclaimer(
        @Path("languageId") languageId: Int? = 1,
        @Query("eventId") eventId: Int? = 6,
        @Query("userTypeId") userTypeId: Int? = 1,
    ): GetDisclaimerResponseModel

    @POST("v1/student/disclaimers/save")
    suspend fun getDisclaimerAccept(
        @Body username: HashMap<String, Int>,
    ): GetDisclamerAccpetResponseModel

    @GET("v1/student/parent/student/information")        // Child student List
    suspend fun getChildList(): ChildListResponse

    @POST("v1/student/user/loginWithOtp")     // login with otp
    suspend fun getLoginWithOtp(@Body verifyOtp: VerifyOTPRequestModel): UserLoginResponseModel

    @POST("v1/student/user/loginWithPin")    // loginWithPin
    suspend fun getLoginWithPin(@Body loginPin: LoginWithPinRequestModel): UserLoginResponseModel

    @POST("v1/student/parent/profile")   // parent profile
    suspend fun getParentProfile(@Body parentModel: ParentModel): ParentRegistationResponseModel

    @POST("v1/student/user/walkThrough")   // parent profile
    suspend fun getParentWalkthrough(@Body walkthroughRequest: ParentWalkthroughRequest): ParentRegistationResponseModel

    //   Student Dashboard Api's
    @GET("v1/student/progress")
    suspend fun getStudentDashboardProgress(): StudentDashboardProgressResponseModel

    @GET("v1/student/profile")   // Student profile
    suspend fun getStudentProfile(): StudentProfileResponseModel

    @Multipart
    @PUT("v1/student/student/profile/{id}")   // Student Update profile
    suspend fun saveStudentProfile(
        @Path("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("userName") userName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("state") state: RequestBody,
        @Part("district") district: RequestBody,
        @Part("pinCode") pinCode: RequestBody,
        @Part("alternativePhone") alternativePhone: RequestBody,
        @Part("grade") grade: RequestBody,
        @Part("board") board: RequestBody,
        @Part("school") school: RequestBody,
        @Part("userTypeId") userTypeId: RequestBody,
        @Part("medium") medium: RequestBody?,
        @Part("otherSchool") otherSchool: RequestBody?,
        @Part image: MultipartBody.Part?, // Optional image part
    ): SaveStudentProfileResponseModel

    //Parent List
    @GET("v1/student/parent/student/information")
    suspend fun getParentDashboardChildList(): StudentInformetionResponseModel

    @GET("v1/student/parent/profiler-completed")
    suspend fun getProfilerCheck(): GetProfilerCheckResponseModel

    @GET("v1/student/parent/progress")
    suspend fun getParentProgress(): GetPerentProgressResponseModel

    @GET("v1/student/admin/banners/{userTypeId}")
    suspend fun getBannerList(@Path("userTypeId") userTypeId: Int): GetBannerListResponseModel // 2 for student, 1 for parent & 3 for teacher

    @POST("v1/student/grade/subjects")
    suspend fun getGradeWiseSubject(@Body hashMap: HashMap<String, Int>): GradeWiseSubjectResponseModel

    @GET("v1/student/get-exam-months")
    suspend fun getExamMonthYear(): GetMonthYearResponseModel

    @POST("v1/student/reports/quiz-performance-individual")
    suspend fun getStudentPerformance(@Body hashMap: HashMap<String, Any>): GetQuizPerformanceResponseModel

    @GET("v1/student/profiler")
    suspend fun getStudentProfiler(): GetCompleteProfilerQuestionOptionsResponseModel

    @POST("v1/student/profiler")     // Quiz performance profiler
    suspend fun getStudentProfilerSave(
        @Body getCompleteProfilerQuestionAnswersSubmitRequestModel: GetCompleteProfilerQuestionAnswersSubmitRequestModel,
    ): GetCompleteProfilerQuestionOptionsResponseModel

    // for student profiler Response class is diff
    @POST("v1/student/profiler")
    suspend fun getStudentSaveProfiler(
        @Body getCompleteProfilerQuestionAnswersSubmitRequestModel: GetCompleteProfilerQuestionAnswersSubmitRequestModel,
    ): GetProfilerSubmitResponse

    @POST("v1/student/parent/student/quiz-performance-report")
    suspend fun getQuizPerformance(@Body hashMap: HashMap<String, Any>): GetQuizReportResposeModel

    @POST("v1/student/parent/student/quiz-verification-report")
    suspend fun getQuizVerificationTable(@Body requestMap: HashMap<String, String>): GetQuizVerificationTableResponseModel

    @POST("v1/student/send-aadhaar-otp")
    suspend fun getSendAadharOtp(@Body requestMap: GetAadharOtpSendRequestModel): GetAadharOTPsendResposeModel

    // Delete Account API
    @POST("v1/student/user/inactiveYourAccount")
    suspend fun getInactiveUser(): DeleteAccountUserResponseModel

    // Recover your account
    @POST("v1/student/user/restoreUserAccount")
    suspend fun getRecoverUser(): DeleteAccountUserResponseModel

    // Authentication API
    @POST("v1/student/verify-aadhaar-otp")
    suspend fun getVerifyAadhaarOtp(@Body requestMap: GetAadharOTPVerifyRequestModel): GetAadharOTPVerifyResponseModel

    @POST("v1/student/save-user-kyc-data")
    suspend fun getSaveAadharData(@Body requestMap: GetSaveAadharDataRequestModel): SaveAdharResponseModel

    @Multipart
    @POST("v1/student/upload-kyc-document")
    suspend fun getKycDocUpload(
        @Part("docType") docType: RequestBody,
        @Part("studentId") studentId: RequestBody,
        @Part("isManual") isManual: RequestBody,
        @Part image: MultipartBody.Part?, // Optional image part
    ): GetKycDocUplaodResposneModel

    @GET("v1/student/check-kyc-upload-status/{studentId}")
    suspend fun getKycStatus(
        @Path("studentId") id: Int,
    ): GetKycStatusResponseModel

    @GET("v1/student/finish-kyc/{studentId}")
    suspend fun getFinishKyc(
        @Path("studentId") id: Int,
    ): SaveAdharResponseModel

    @GET("v1/student/check-kyc-aadhaar-status/{studentId}")
    suspend fun getKycAadhaarStatus(
        @Path("studentId") id: Int,
    ): GetKycAadhaarStatusResponseModel

    // Assessment Api
    @POST("v1/student/assessment/concepts")
    suspend fun getAssessmentConcept(@Body requestMap: AssessmentConceptsRequestModel): AssessmentConceptsResponseModel

    @POST("v1/student/assessment/saveConcepts")
    suspend fun saveAssessmentConcept(
        @Body requestConcept: AssessmentSaveConceptRequestModel,
    ): VerifyOTPResponseModel               // response model is same

    @GET("v1/student/conceptWiseQuizes/{id}")  // for
    suspend fun getConceptWiseQuiz(
        @Path("id") id: String,
    ): ConceptWiseQuizResponseModel

    @POST("v1/student/assessment/quiz/questions")
    suspend fun getQuizQuestions(
        @Body quizRequest: AssessmentGetQuizRequestModel,
    ): AssessmentQuizQuestionsResponseModel

    @POST("v1/student/assessment/save/quiz")
    suspend fun saveQuizQuestions(
        @Body requestConcept: AssessmentSaveQuestionRequestModel,
    ): AssessmentSaveQuestionResponseModel

    @POST("v1/student/assessment/submit/quiz")
    suspend fun submitQuiz(
        @Body requestConcept: SubmitQuizRequestModel,
    ): SubmitQuizResponseModel

    @POST("v1/student/assessment/create-exam-folder")  // to create a folder to upload images
    suspend fun saveExamImageFolder(
        @Body examImageFolder: CreateExamImageRequestModel,
    ): VerifyOTPResponseModel

    @Multipart
    @POST("v1/student/assessment/save-quiz-image")  // to create a folder to upload images
    suspend fun saveQuizImage(
        @Part("examId") examId: RequestBody,
        @Part("imageType") imageType: RequestBody,
        @Part image: MultipartBody.Part, // Optional image part
    ): VerifyOTPResponseModel

    // Passport API 17 Sept 2024
    @GET("v1/student/reports/student/passport")
    suspend fun getReportsPerforming(): ReportsPerformingResponse

    @GET("v1/student/user-score")
    suspend fun getMonthlyReport(): MonthlyReportResponse

    @GET("v1/student/refferal_code")
    suspend fun getReferalCode(): ReferalCodeResponse

    @POST("v1/student/passport/statistics")
    suspend fun getLeaderboardReport(@Body jsonObject: JsonObject): LeaderboardDataResponse

    @GET("v1/student/passport/overview/badge-award")
    suspend fun getBadgeAwardDataReport(): BadgeAwardDataResponse

    @GET("v1/student/passport/badges")
    suspend fun getBadgesListReport(): BadgesListResponse

    @GET("v1/student/passport/badges/list")
    suspend fun getAllBadgesReport(): AllBadgesResponse

    @GET("v1/student/passport/score/score-guide")
    suspend fun getScoreCalculationApi(): ScoreCalculationResponseModel

    @GET("v1/student/passport/awards/list")
    suspend fun getAllAwardListReport(@Query("frequency") frequency: String): AllAwardListResponse

    @GET("v1/student/passport/awards")
    suspend fun getAllAwardsReport(
        @Query("location") location: String, @Query("frequency") frequency: String,
    ): AllAwardsResponse

    // Wallet Panel Api date 21 Sept 2024
    @GET("v1/student/wallet/winning-amount/{id}")
    suspend fun getWalletWinningAmount(
        @Path("id") id: String,
    ): WalletWinningAmountResponse

    @POST("v1/student/wallet/quiz-winning/status")
    suspend fun getWalletQuizWinningStatus(
        @Body jsonObject: JsonObject,
    ): QuizWinningStatusResponse

    @POST("v1/student/payment/transactionHistory")
    suspend fun getTransactionHistory(@Body jsonObject: JsonObject): TransactionHistoryResponse

    @POST("v1/student/wallet/quiz/status/details")
    suspend fun getQuizStatusDetails(@Body jsonObject: JsonObject): QuizStatusDetailsResponse

    @GET("v1/student/payment/studentAccounts")
    suspend fun getStudentAccounts(): StudentAccountsListResponse

    @GET("v1/student/payment/studentUpi")
    suspend fun getStudentUpi(): StudentUpiListResponse

    @GET("v1/student/user/payment-check-config")
    suspend fun getPaymentCheckConfig(): PaymentCheckConfigResponse

    @POST("v1/student/payment/validateUpi")
    suspend fun getValidateUpi(@Body jsonObject: JsonObject): AddUpiResponse

    @POST("v1/student/payment/createAccount")
    suspend fun getCreateAccount(@Body jsonObject: JsonObject): CreateAccountResponse

    @HTTP(method = "DELETE", path = "v1/student/payment/deleteUpi", hasBody = true)
    suspend fun getDeleteUpi(@Body jsonObject: JsonObject): DeleteUpiBankResponse

    @HTTP(method = "DELETE", path = "v1/student/payment/deleteAccount", hasBody = true)
    suspend fun getDeleteAccount(@Body jsonObject: JsonObject): DeleteUpiBankResponse

    @POST("v1/student/payment/upiTransaction")
    suspend fun getUpiTransaction(@Body upiTransactionRequestModel: UpiTransactionRequestModel): UpiTransactionResponse

    @POST("v1/student/payment/accountTransaction")
    suspend fun getAccountTransaction(@Body accountTransactionRequestModel: AccountTransactionRequestModel): AccountTransactionResponse

    // for parent profile on date 24-Sept-2024
    @GET("v1/student/parent/profile")
    suspend fun getParentProfile(): ParentProfileResponseModel

    //@Multipart
    @POST("v1/student/parent/update")
    suspend fun getUpdateParentProfile(@Body jsonObject: JsonObject): UpdateParentProfileResponseModel

    /*@GET("v1/student/admin/faq/user/{id}")
    suspend fun getFaq(
        @Path("id") id: String,
        @Query("languageId") languageId: Int,
       *//* @Query("categoryId") categoryId: Int,*//*
    ): FaqResponse*/

    @GET("v1/student/user/faqs")
    suspend fun getFaq(
        @Query("languageId") languageId: Int,
        @Query("userTypeId") userTypeId: Int,
        @Query("categoryId") categoryId: Int,
    ): FaqResponse

    @GET("v1/student/user/faq-category")
    suspend fun getFaqCategory(
        @Query("languageId") languageId: Int, @Query("userTypeId") userTypeId: Int,
    ): FaqCategoryResponse

    // for student reports on date 09-Oct-2024
    @POST("v1/student/reports/student/quiz-attempt")
    suspend fun getQuizAttempt(@Body jsonObject: JsonObject): QuizAttemptResponse

    //  Passport Reports Api Date - 04-Dec-2024
    @POST("v1/student/reports/quiz-score")
    suspend fun getQuizScoreReports(@Body jsonObject: JsonObject): QuizScoreResponse

    @POST("v1/student/reports/student/weak-performing")
    suspend fun getWeakPerformingReports(@Body jsonObject: JsonObject): TopWeakPerformingResponse

    @POST("v1/student/reports/student/top-performing")
    suspend fun getTopPerformingReports(@Body jsonObject: JsonObject): TopWeakPerformingResponse

    @POST("v1/student/reports/quiz-verification")
    suspend fun getQuizVerificationReports(@Body jsonObject: JsonObject): QuizVerificationResponse

    @GET("v1/student/admin/partner")
    suspend fun getPartnerDetails(@Query("name") strName: String): PartnerDetailsResponse

    @GET("v1/student/grade-update/{id}")
    suspend fun getGradeUpdate(@Path("id") id: String): GradeUpdateResponse

    // APi for ForceUpdate
    @POST("v1/student/user/checkVersion")
    suspend fun getForceUpdate(@Body jsonObject: JsonObject): AssessmentSaveQuestionResponseModel

    // API for Save referral
    @POST("v1/student/save_refferal")
    suspend fun saveReferralCode(@Body jsonObject: JsonObject): SaveReferralResponse

    // API for Practice Quiz   assessment/practice/concepts
    @POST("v1/student/assessment/practice/concepts")
    suspend fun getPracticeConcepts(@Body jsonObject: JsonObject): PracticeConceptsResponse

}
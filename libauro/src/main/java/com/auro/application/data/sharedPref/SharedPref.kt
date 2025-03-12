package com.auro.application.data.sharedPref

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.provider.Settings.Global.putString
import com.auro.application.core.ConstantVariables.CHILD_COUNT
import com.auro.application.core.ConstantVariables.CONCEPT_ID
import com.auro.application.core.ConstantVariables.CONCEPT_Name
import com.auro.application.core.ConstantVariables.CONCEPT_PRACTICE
import com.auro.application.core.ConstantVariables.CONCEPT_SIZE
import com.auro.application.core.ConstantVariables.END_DATE
import com.auro.application.core.ConstantVariables.EXAM_ASSESSMENT_ID
import com.auro.application.core.ConstantVariables.EXAM_ID
import com.auro.application.core.ConstantVariables.EXAM_NAME
import com.auro.application.core.ConstantVariables.FORGOT_PASSWORD
import com.auro.application.core.ConstantVariables.GET_QUIZ_RESULT
import com.auro.application.core.ConstantVariables.IS_PRACTICE
import com.auro.application.core.ConstantVariables.LANGUAGE_CODE
import com.auro.application.core.ConstantVariables.PARENT_DASHBOARD
import com.auro.application.core.ConstantVariables.PARTNER_NAME
import com.auro.application.core.ConstantVariables.PRACTICE_PAGE
import com.auro.application.core.ConstantVariables.REFERRED_BY
import com.auro.application.core.ConstantVariables.REFERRED_TYPE_ID
import com.auro.application.core.ConstantVariables.REFERRED_USER_ID
import com.auro.application.core.ConstantVariables.SELECTED_BANK_UPI
import com.auro.application.core.ConstantVariables.SELECTED_CONCEPT_DATA
import com.auro.application.core.ConstantVariables.SELECTED_BANK_UPI_DATA
import com.auro.application.core.ConstantVariables.SELECTED_BANK_UPI_DIRECTION
import com.auro.application.core.ConstantVariables.SELECTED_DATE
import com.auro.application.core.ConstantVariables.SELECTED_SUBJECT_DATA
import com.auro.application.core.ConstantVariables.SELECTED_WALLET_DATA
import com.auro.application.core.ConstantVariables.START_DATE
import com.auro.application.core.ConstantVariables.STUDENT_KYC_DATA
import com.auro.application.core.ConstantVariables.STUDENT_KYC_UPLOAD_DATA
import com.auro.application.core.ConstantVariables.TRANSACTION_AMOUNT
import com.auro.application.core.ConstantVariables.TRANSACTION_DATE_TIME
import com.auro.application.core.ConstantVariables.TRANSACTION_ID
import com.auro.application.core.ConstantVariables.TRANSACTION_STATUS
import com.auro.application.core.ConstantVariables.UPI_LIST_STUDENT
import com.auro.application.core.ConstantVariables.USER_ASSESSMENT_SAVE_QUESTION
import com.auro.application.core.ConstantVariables.USER_LANGUAGE_TRANSLATION
import com.auro.application.core.ConstantVariables.USER_PARENT_PROFILE
import com.auro.application.core.ConstantVariables.USER_PIN
import com.auro.application.core.ConstantVariables.USER_PROFILER_SAVE_LIST
import com.auro.application.core.ConstantVariables.USER_STUDENT_LIST
import com.auro.application.core.ConstantVariables.USER_STUDENT_LIST_STUDENT
import com.auro.application.core.ConstantVariables.USER_STUDENT_PROFILE
import com.auro.application.core.ConstantVariables.USER_STUDENT_SUBJECT
import com.auro.application.core.ConstantVariables.WALLET_AMOUNT
import com.auro.application.data.api.Constants
import com.auro.application.ui.features.login.models.ChildListResponse
import com.auro.application.ui.features.login.models.GetSubjectListResponseModel
import com.auro.application.ui.features.login.models.UserLoginResponseModel
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionAnswersSubmitRequestModel
import com.auro.application.ui.features.parent.model.ParentProfileData
import com.auro.application.ui.features.parent.model.StudentInformetionResponseModel
import com.auro.application.ui.features.student.assessment.model.AssessmentConceptsResponseModel
import com.auro.application.ui.features.student.assessment.model.AssessmentQuizQuestionsResponseModel
import com.auro.application.ui.features.student.assessment.model.SubmitQuizResponseModel
import com.auro.application.ui.features.student.authentication.model.GetKycAadhaarStatusResponseModel
import com.auro.application.ui.features.student.authentication.model.GetKycStatusResponseModel
import com.auro.application.ui.features.student.models.StudentProfileResponseModel
import com.auro.application.ui.features.student.partner.models.PartnerDetailsResponse
import com.auro.application.ui.features.student.wallet.Models.StudentUpiListResponse
import com.auro.application.ui.features.student.wallet.Models.StudentUpiListResponse.UpiListData
import com.auro.application.ui.features.student.wallet.Models.WinningAmountData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.HashMap

class SharedPref private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        @Volatile
        private var INSTANCE: SharedPref? = null
        fun getInstance(context: Context): SharedPref = INSTANCE ?: synchronized(this) {
            INSTANCE ?: SharedPref(context).also { INSTANCE = it }
        }
    }

    fun clearData(context: Context) {
        sharedPreferences.edit().clear().apply()
    }

    fun saveLoginToken(value: String) {
        with(sharedPreferences.edit()) {
            putString("isLogin", value)
            apply()
        }
    }

    fun saveUserTypeId(value: String) {
        with(sharedPreferences.edit()) {
            putString("user_type_id", value)
            apply()
        }
    }

    fun getUserTypeId(): String? {
        return sharedPreferences.getString("user_type_id", null)
    }

    fun saveNumberOfStudent(value: Int) {
        with(sharedPreferences.edit()) {
            putInt("numberOfStudent", value)
            apply()
        }
    }

    fun getNumberOfStudent(): Int {
        return sharedPreferences.getInt("numberOfStudent", 0)
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun saveRoute() {

    }

    fun getRouteLast() {

    }

    fun saveToken(token: String) {
        Constants.token = token
        with(sharedPreferences.edit()) {
            putString("token", token)
            apply()
        }
    }

    fun saveGrade(it: String) {
        with(sharedPreferences.edit()) {
            putString("grade", it)
            apply()
        }
    }

    fun getGrade(): String? {
        return sharedPreferences.getString("grade", null)
    }

    val currentLogin = "isLogin"

    fun saveLogin(b: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(currentLogin, b)
            apply()
        }
    }

    fun isLogin(): Boolean {
        return sharedPreferences.getBoolean(currentLogin, false)
    }

    // Optional: A method to clear SharedPreferences (use with caution)
    fun clearLogin() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }

    fun saveKycStatus(status: String) {
        with(sharedPreferences.edit()) {
            putString("kycStatus", status)
            apply()
        }
    }

    fun getKycStatus(): String = sharedPreferences.getString("kycStatus", null).toString()

    fun saveUserPhoneNo(phone: String) {
        with(sharedPreferences.edit()) {
            putString("phone", phone)
            apply()
        }
    }

    fun getPhoneNo(): String = sharedPreferences.getString("phone", null).toString()


    fun saveScreenName(screenName: String) {
        with(sharedPreferences.edit()) {
            putString("screenName", screenName)
            apply()
        }
    }

    fun getScreenName(): String? = sharedPreferences.getString("screenName", null).toString()

    fun saveFlow(flow: String) {
        with(sharedPreferences.edit()) {
            putString("flow", flow)
            apply()
        }
    }

    fun getFlow(): String? = sharedPreferences.getString("flow", null).toString()

    fun saveStudentCount(id: Int) {
        with(sharedPreferences.edit()) {
            putInt(CHILD_COUNT, id)
            apply()
        }
    }

    fun getStudentCount(): Int = sharedPreferences.getInt(CHILD_COUNT, 1).toInt()

    fun saveLangeId(id: String) {
        with(sharedPreferences.edit()) {
            putString("languageId", id)
            apply()
        }
    }

    fun getLangeId(): String = sharedPreferences.getString("languageId", "1").toString()

    fun saveUserId(userId: String?) {
        with(sharedPreferences.edit()) {
            putString("userid", userId)
            apply()
        }
    }

    fun getLangCode(): String = sharedPreferences.getString(LANGUAGE_CODE, "e").toString()

    fun saveLangCode(userId: String?) {
        with(sharedPreferences.edit()) {
            putString(LANGUAGE_CODE, userId)
            apply()
        }
    }

    fun getExamName(): String = sharedPreferences.getString(EXAM_NAME, "1").toString()

    fun saveExamName(userId: String?) {
        with(sharedPreferences.edit()) {
            putString(EXAM_NAME, userId)
            apply()
        }
    }

    fun getIsPractice(): Boolean = sharedPreferences.getBoolean(IS_PRACTICE, false)

    fun saveIsPractice(isPractice: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(IS_PRACTICE, isPractice)
            apply()
        }
    }

    fun getExamId(): String = sharedPreferences.getString(EXAM_ID, "1").toString()

    fun saveExamId(userId: String?) {
        with(sharedPreferences.edit()) {
            putString(EXAM_ID, userId)
            apply()
        }
    }

    // NavigationData
    fun saveUserName(name: String) {
        with(sharedPreferences.edit()) {
            putString("userName", name)
            apply()
        }
    }

    fun getUserName(): String = sharedPreferences.getString("userName", "").toString()

    fun saveUserEmail(email: String) {
        with(sharedPreferences.edit()) {
            putString("userEmail", email)
            apply()
        }
    }

    fun getUserEmail(): String = sharedPreferences.getString("userEmail", "").toString()

    fun saveUserImage(email: String) {
        with(sharedPreferences.edit()) {
            putString("userImage", email)
            apply()
        }
    }

    fun getUserImage(): String = sharedPreferences.getString("userImage", "").toString()

    fun getExamAssessmentId(): String =
        sharedPreferences.getString(EXAM_ASSESSMENT_ID, "1").toString()

    fun saveExamAssessmentId(userId: String?) {
        with(sharedPreferences.edit()) {
            putString(EXAM_ASSESSMENT_ID, userId)
            apply()
        }
    }

    fun saveAssessmentQuestionData(question: AssessmentQuizQuestionsResponseModel.QuizListData) {
        val gson = Gson()
        question.let {
            val jsonString = gson.toJson(it)
            sharedPreferences.edit().putString(USER_ASSESSMENT_SAVE_QUESTION, jsonString).apply()
        }
    }

    fun getAssessmentQuestionData(): AssessmentQuizQuestionsResponseModel.QuizListData? {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(USER_ASSESSMENT_SAVE_QUESTION, null)
        return jsonString?.let {
            gson.fromJson(it, AssessmentQuizQuestionsResponseModel.QuizListData::class.java)
        }!!
    }

    fun getUserId(): String? = sharedPreferences.getString("userid", null)

    fun saveParentOnboardingWalkthrough(value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("ParentOnboardingWalkthrough", value)
            apply()
        }
    }

    fun getParentOnboardingWalkthrough(): Boolean {
        return sharedPreferences.getBoolean("ParentOnboardingWalkthrough", true)
    }

    fun saveDashboardWalkthrough(value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("walkthroughViewed", value)
            apply()
        }
    }

    fun saveLoginWalkThrough(value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("studentDashboard", value)
            apply()
        }
    }

    fun getLoginWalkThrough(): Boolean {
        return sharedPreferences.getBoolean("studentDashboard", true)
    }

    fun getDashboardWalkThought(): Boolean {
        return sharedPreferences.getBoolean("walkthroughViewed", true)
    }

    fun saveUserPin(userPin: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(USER_PIN, userPin)
            apply()
        }
    }

    fun getUserPin(): Boolean = sharedPreferences.getBoolean(USER_PIN, false)

    fun isParentNoticeViewed(b: Boolean?): Boolean {
        return b?.let {
            sharedPreferences.edit().putBoolean("parentViewedNotice", it).apply()
            true
        } ?: sharedPreferences.getBoolean("parentViewedNotice", false)
    }

    /*fun isParentNoticeViewed(b: Boolean?) :Boolean{
        b?.let {
            with(sharedPreferences.edit()) {
                putBoolean("parentViewedNotice", b)
                apply()
            }
        }
        return sharedPreferences.getBoolean("parentViewedNotice",true)
    }*/

    fun saveStudentListDetails(studentDetails: StudentInformetionResponseModel.Data.Student) {
        val gson = Gson()
        studentDetails.let {
            val jsonString = gson.toJson(it)
            sharedPreferences.edit().putString(USER_STUDENT_LIST, jsonString).apply()
        }
    }

    fun getStudentListDetails(): StudentInformetionResponseModel.Data.Student {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(USER_STUDENT_LIST, null)
        return jsonString?.let {
            gson.fromJson(it, StudentInformetionResponseModel.Data.Student::class.java)
        }!!
    }

    fun saveStudentListData(studentDetails: ChildListResponse.Data.Student) {
        val gson = Gson()
        studentDetails.let {
            val jsonString = gson.toJson(it)
            sharedPreferences.edit().putString(USER_STUDENT_LIST_STUDENT, jsonString).apply()
        }
    }

    fun getStudentListData(): ChildListResponse.Data.Student {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(USER_STUDENT_LIST_STUDENT, null)
        return jsonString?.let {
            gson.fromJson(it, ChildListResponse.Data.Student::class.java)
        }!!
    }

    fun saveStudentSubjectData(studentDetails: List<GetSubjectListResponseModel.Data?>) {
        val gson = Gson()
        studentDetails.let {
            val jsonString = gson.toJson(it)
            sharedPreferences.edit().putString(USER_STUDENT_SUBJECT, jsonString).apply()
        }
    }

    fun getStudentSubjectData(): List<GetSubjectListResponseModel.Data> {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(USER_STUDENT_SUBJECT, null)
        val type = object : TypeToken<List<GetSubjectListResponseModel.Data>?>() {}.type
        return if (jsonString != null) {
            jsonString.let {
                gson.fromJson(it, type)
            }
        } else{
            emptyList()
        }
    }

    fun saveKycData(studentDetails: GetKycAadhaarStatusResponseModel.AadhaarStatusData) {
        val gson = Gson()
        studentDetails.let {
            val jsonString = gson.toJson(it)
            sharedPreferences.edit().putString(STUDENT_KYC_DATA, jsonString).apply()
        }
    }

    fun getKycData(): GetKycAadhaarStatusResponseModel.AadhaarStatusData {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(STUDENT_KYC_DATA, null)
        return jsonString?.let {
            gson.fromJson(it, GetKycAadhaarStatusResponseModel.AadhaarStatusData::class.java)
        }!!
    }

    fun saveKycDocStatus(studentDetails: GetKycStatusResponseModel.Data) {
        val gson = Gson()
        studentDetails.let {
            val jsonString = gson.toJson(it)
            sharedPreferences.edit().putString(STUDENT_KYC_UPLOAD_DATA, jsonString).apply()
        }
    }

    fun getKycDocStatus(): GetKycStatusResponseModel.Data {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(STUDENT_KYC_UPLOAD_DATA, null)
        return jsonString?.let {
            gson.fromJson(it, GetKycStatusResponseModel.Data::class.java)
        }!!
    }

    fun saveParentInfo(userDetails: UserLoginResponseModel.LoginData.UserDetails?) {
        val gson = Gson()
        userDetails?.let {
            val jsonString = gson.toJson(it)
            sharedPreferences.edit().putString("parent_info", jsonString).apply()
        }
    }

    fun getParentInfo(): UserLoginResponseModel.LoginData.UserDetails? {
        val gson = Gson()
        val jsonString = sharedPreferences.getString("parent_info", null)
        return jsonString?.let {
            gson.fromJson(it, UserLoginResponseModel.LoginData.UserDetails::class.java)
        }
    }

    fun setLoginInfo(userDetails: UserLoginResponseModel.LoginData.UserDetails?) {
        val gson = Gson()
        userDetails?.let {
            val jsonString = gson.toJson(it)
            sharedPreferences.edit().putString("login_info", jsonString).apply()
        }
    }

    fun getLoginInfo(): UserLoginResponseModel.LoginData.UserDetails? {
        val gson = Gson()
        val jsonString = sharedPreferences.getString("login_info", null)
        return jsonString?.let {
            gson.fromJson(it, UserLoginResponseModel.LoginData.UserDetails::class.java)
        }
    }

    fun saveStudentInfo(userDetails: StudentProfileResponseModel.ProfileData?) {
        val gson = Gson()
        userDetails.let {
            val jsonString = gson.toJson(it)
            sharedPreferences.edit().putString(USER_STUDENT_PROFILE, jsonString).apply()
        }
    }

    fun getStudentInfo(): StudentProfileResponseModel.ProfileData? {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(USER_STUDENT_PROFILE, null)
        return jsonString?.let {
            gson.fromJson(it, StudentProfileResponseModel.ProfileData::class.java)
        }
    }

    //    fun saveTranslationInfo(userDetails: LanguageTranslationsResponse.Data) {
    fun saveTranslationInfo(listName: String?, list: HashMap<String, String>) {
        val gson = Gson()
        list.let {
            val jsonString = gson.toJson(it)
//            sharedPreferences.edit().putString(USER_LANGUAGE_TRANSLATION, jsonString).apply()
            sharedPreferences.edit().putString(listName, jsonString).apply()
        }
    }

    //    fun getTranslationInfo(): LanguageTranslationsResponse.Data {
    fun getTranslationInfo(listName: String?): HashMap<String, String> {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(listName, null)
        val type =
            object : TypeToken<HashMap<String, String>>() {}.type//converting the json to list
        return jsonString?.let {
            gson.fromJson(it, type)
        }!!
    }

    fun saveParentProfileInfo(userDetails: ParentProfileData?) {
        val gson = Gson()
        userDetails.let {
            val jsonString = gson.toJson(it)
            sharedPreferences.edit().putString(USER_PARENT_PROFILE, jsonString).apply()
        }
    }

    fun getParentProfileInfo(): ParentProfileData? {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(USER_PARENT_PROFILE, null)
        return jsonString?.let {
            gson.fromJson(it, ParentProfileData::class.java)
        }!!
    }

    fun getSelectedSubjectInfo(): GetSubjectListResponseModel.Data {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(SELECTED_SUBJECT_DATA, null)
        return jsonString?.let {
            gson.fromJson(it, GetSubjectListResponseModel.Data::class.java)
        }!!
    }

    fun saveSelectedSubjectInfo(subject: GetSubjectListResponseModel.Data) {
        with(sharedPreferences.edit()) {
            val gson = Gson()
            val json = gson.toJson(subject)
            putString(SELECTED_SUBJECT_DATA, json)
            apply()
        }
    }

    fun getSelectedConceptInfo(): AssessmentConceptsResponseModel.AssessmentConcept {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(SELECTED_CONCEPT_DATA, null)
        return jsonString?.let {
            gson.fromJson(it, AssessmentConceptsResponseModel.AssessmentConcept::class.java)
        }!!
    }

    fun saveSelectedConceptInfo(subject: AssessmentConceptsResponseModel.AssessmentConcept) {
        with(sharedPreferences.edit()) {
            val gson = Gson()
            val json = gson.toJson(subject)
            putString(SELECTED_CONCEPT_DATA, json)
            apply()
        }
    }

    fun getQuizResult(): SubmitQuizResponseModel.SubmitQuizData {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(GET_QUIZ_RESULT, null)
        return jsonString?.let {
            gson.fromJson(it, SubmitQuizResponseModel.SubmitQuizData::class.java)
        }!!
    }

    fun saveQuizResult(subject: SubmitQuizResponseModel.SubmitQuizData) {
        with(sharedPreferences.edit()) {
            val gson = Gson()
            val json = gson.toJson(subject)
            putString(GET_QUIZ_RESULT, json)
            apply()
        }
    }

    fun getStudentProfilerList(): GetCompleteProfilerQuestionAnswersSubmitRequestModel? {

        val gson = Gson()

        // Retrieve the JSON string
        val json = sharedPreferences.getString(USER_PROFILER_SAVE_LIST, null)

        // Convert the JSON string back to the model
        return if (json != null) {
            gson.fromJson(json, GetCompleteProfilerQuestionAnswersSubmitRequestModel::class.java)
        } else {
            null
        }
    }

    fun saveStudentProfilerList(profilerData: GetCompleteProfilerQuestionAnswersSubmitRequestModel) {
        with(sharedPreferences.edit()) {
            val gson = Gson()
            val json = gson.toJson(profilerData)
            putString(USER_PROFILER_SAVE_LIST, json)
            apply()
        }
    }

    fun setBankUpiData(bankUpi: String) {
        with(sharedPreferences.edit()) {
            putString(SELECTED_BANK_UPI_DATA, bankUpi)
            apply()
        }
    }

    fun getBankUpiData(): String? = sharedPreferences.getString(SELECTED_BANK_UPI_DATA, "")

    fun setBankUpiDirectionPage(bankUpi: String) {
        with(sharedPreferences.edit()) {
            putString(SELECTED_BANK_UPI_DIRECTION, bankUpi)
            apply()
        }
    }

    fun getBankUpiDirectionPage(): String? =
        sharedPreferences.getString(SELECTED_BANK_UPI_DIRECTION, "")

    fun setWalletAmount(amount: String) {
        with(sharedPreferences.edit()) {
            putString(WALLET_AMOUNT, amount)
            apply()
        }
    }

    fun getWalletAmount(): String? =
        sharedPreferences.getString(WALLET_AMOUNT, "")

    fun setWalletInfo(winningWallet: WinningAmountData) {
        val gson = Gson()
        winningWallet.let {
            val jsonString = gson.toJson(it)
            sharedPreferences.edit().putString(SELECTED_WALLET_DATA, jsonString).apply()
        }
    }

    fun getWalletInfo(): WinningAmountData {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(SELECTED_WALLET_DATA, null)
        return jsonString?.let {
            gson.fromJson(it, WinningAmountData::class.java)
        }!!
    }

    fun setBankAccountUpiID(bankUpiId: String) {
        with(sharedPreferences.edit()) {
            putString(SELECTED_BANK_UPI, bankUpiId)
            apply()
        }
    }

    fun getBankAccountUpiID(): String? = sharedPreferences.getString(SELECTED_BANK_UPI, "")

    fun setTransactionId(trnId: String) {
        with(sharedPreferences.edit()) {
            putString(TRANSACTION_ID, trnId)
            apply()
        }
    }

    fun getTransactionId(): String? = sharedPreferences.getString(TRANSACTION_ID, "")

    fun setTransactionDateTime(trnDateTime: String) {
        with(sharedPreferences.edit()) {
            putString(TRANSACTION_DATE_TIME, trnDateTime)
            apply()
        }
    }

    fun getTransactionDateTime(): String? = sharedPreferences.getString(TRANSACTION_DATE_TIME, "")

    fun setTransactionAmount(trnAmount: String) {
        with(sharedPreferences.edit()) {
            putString(TRANSACTION_AMOUNT, trnAmount)
            apply()
        }
    }

    fun getTransactionAmount(): String? = sharedPreferences.getString(TRANSACTION_AMOUNT, "")

    fun setTransactionStatus(trnStatus: String) {
        with(sharedPreferences.edit()) {
            putString(TRANSACTION_STATUS, trnStatus)
            apply()
        }
    }

    fun getTransactionStatus(): String? = sharedPreferences.getString(TRANSACTION_STATUS, "")


    fun setForgotPassword(forgetPassword: String) {
        with(sharedPreferences.edit()) {
            putString(FORGOT_PASSWORD, forgetPassword)
            apply()
        }
    }

    fun getForgotPassword(): String? = sharedPreferences.getString(FORGOT_PASSWORD, "")

    fun setSelectedConcept(number: Int) {
        with(sharedPreferences.edit()) {
            putInt(CONCEPT_SIZE, number)
            apply()
        }
    }

    fun getSelectedConcept(): Int = sharedPreferences.getInt(CONCEPT_SIZE, 0)

    fun setSelectedConceptId(number: String) {
        with(sharedPreferences.edit()) {
            putString(CONCEPT_ID, number)
            apply()
        }
    }

    fun getSelectedConceptId(): String = sharedPreferences.getString(CONCEPT_ID, "0")!!

    fun setSelectedPracticeConcept(number: Int) {
        with(sharedPreferences.edit()) {
            putInt(CONCEPT_PRACTICE, number)
            apply()
        }
    }

    fun getSelectedPracticeConcept(): Int = sharedPreferences.getInt(CONCEPT_PRACTICE, 0)

    fun setSelectedConceptName(number: String) {
        with(sharedPreferences.edit()) {
            putString(CONCEPT_Name, number)
            apply()
        }
    }

    fun getSelectedConceptName(): String = sharedPreferences.getString(CONCEPT_Name, "0")!!

    fun setAddStudentFromParentDashboard(parentDashboard: String) {
        with(sharedPreferences.edit()) {
            putString(PARENT_DASHBOARD, parentDashboard)
            apply()
        }
    }

    fun getAddStudentFromParentDashboard(): String = sharedPreferences.getString(PARENT_DASHBOARD, "")!!

    fun setPartnerCompanyName(partnerDetailsData: PartnerDetailsResponse.PartnerDetails.PartnerListData) {
        with(sharedPreferences.edit()) {
            val gson = Gson()
            val json = gson.toJson(partnerDetailsData)
            putString(PARTNER_NAME, json)
            apply()
        }
    }

    fun getPartnerCompanyName(): PartnerDetailsResponse.PartnerDetails.PartnerListData {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(PARTNER_NAME, null)
        return jsonString?.let {
            gson.fromJson(it, PartnerDetailsResponse.PartnerDetails.PartnerListData::class.java)
        }!!
    }

    fun setUPIListData(addedUpiDataList: List<UpiListData>) {
        val gson = Gson()
        addedUpiDataList.let {
            val jsonString = gson.toJson(it)
            sharedPreferences.edit().putString(UPI_LIST_STUDENT, jsonString).apply()
        }
    }

    fun getUPIListData(): List<UpiListData> {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(UPI_LIST_STUDENT, null)
        return jsonString?.let {
            val type = object : TypeToken<List<UpiListData>>() {}.type
            gson.fromJson(it, type)
        }!!
    }

    fun setStartDate(startDate: String) {
        with(sharedPreferences.edit()) {
            putString(START_DATE, startDate)
            apply()
        }
    }

    fun getStartDate(): String? = sharedPreferences.getString(START_DATE, "")

    fun setEndDate(startDate: String) {
        with(sharedPreferences.edit()) {
            putString(END_DATE, startDate)
            apply()
        }
    }

    fun getEndDate(): String? = sharedPreferences.getString(END_DATE, "")

    fun setReferredBy(referredBy: Int) {
        with(sharedPreferences.edit()) {
            putInt(REFERRED_BY, referredBy)
            apply()
        }
    }

    fun getReferredBy(): Int? = sharedPreferences.getInt(REFERRED_BY, 0)

    fun setReferredUserId(ReferredUserId: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(REFERRED_USER_ID, ReferredUserId)
            apply()
        }
    }

    fun getReferredUserId(): Boolean? = sharedPreferences.getBoolean(REFERRED_USER_ID, false)

    fun setReferredTypeId(ReferredTypeId: Int) {
        with(sharedPreferences.edit()) {
            putInt(REFERRED_TYPE_ID, ReferredTypeId)
            apply()
        }
    }

    fun getPracticeSelectedSubjectInfo(): GetSubjectListResponseModel.Data {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(SELECTED_SUBJECT_DATA, null)
        return jsonString?.let {
            gson.fromJson(it, GetSubjectListResponseModel.Data::class.java)
        }!!
    }

    fun setPracticeSelectedSubjectInfo(subject: GetSubjectListResponseModel.Data) {
        with(sharedPreferences.edit()) {
            val gson = Gson()
            val json = gson.toJson(subject)
            putString(SELECTED_SUBJECT_DATA, json)
            apply()
        }
    }

    fun getReferredTypeId(): Int? = sharedPreferences.getInt(REFERRED_TYPE_ID, 0)

    fun setSelectedDate(selectedDate: String) {
        with(sharedPreferences.edit()) {
            putString(SELECTED_DATE, selectedDate)
            apply()
        }
    }

    fun getSelectedDate(): String? = sharedPreferences.getString(SELECTED_DATE, "")

    fun setPracticePage(practicePage: String) {
        with(sharedPreferences.edit()) {
            putString(PRACTICE_PAGE, practicePage)
            apply()
        }
    }

    fun getPracticePage(): String? = sharedPreferences.getString(PRACTICE_PAGE, "")

    fun removeStudentProfilerList() {

        // Get the editor to modify SharedPreferences
        val editor = sharedPreferences.edit()

// Remove the specific key (model)
        editor.remove(USER_PROFILER_SAVE_LIST)

// Apply the changes
        editor.apply()
    }
}



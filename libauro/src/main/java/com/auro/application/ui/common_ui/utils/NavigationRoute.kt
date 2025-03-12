package com.auro.application.ui.common_ui.utils

import kotlinx.serialization.Serializable


sealed class AppRoute(val route: String) {

    @Serializable
    data object LanguageSelect

    data object SelectRole : AppRoute("select_role")
//    data object Login : AppRoute("login")

    @Serializable
    data class Login(val isForgotPinPassword: String? = null)

//    @Serializable
//    data class PartnerWebViewDetails(val partnerWebViewScreen: String? = null)

    @Serializable
    data class VerifyOtp(val phoneNo: String? = null, val isLoginWithOtp: String? = null)

    @Serializable
    data class EnterPin(
        val phoneNo: String? = null,
        val isLoginWithPin: String? = null,
        val userId: String? = null
    )

    @Serializable
    data class SwitchUserWithPin(
        val phoneNo: String? = null,
        val isLoginWithPin: String? = null,
        val userId: String? = null
    )

    //    data object VerifyOtp : AppRoute("verify_otp")
    @Serializable
    data class EnterPassword(val user: String? = null)

    @Serializable
    data class CreatePassword(val user: String? = null)

    @Serializable
    data class ChildList(val user: String?, val isUserPin: String? = null)

    /*  @Serializable
      data class StudentAssessmentConcept(val user: String?)*/

    @Serializable
    data class CreatePin(val userId: String? = null, val isForgotPinPassword: String? = null)

//    data object EnterPin : AppRoute("enter_pin")

    // Student Dashboard
    data object StudentAuth : AppRoute("student_auth")
    data object StudentProfile : AppRoute("student_profile")
    data object StudentFAQ : AppRoute("student_faq")
    data object StudentSwitchProfile : AppRoute("student_switch_profile")

    // Bottom Menu
    data object StudentDashboard : AppRoute("student_dashboard")
    data object StudentQuizzes : AppRoute("student_quizzes")
    data object StudentPassport : AppRoute("student_passport")
    data object StudentWallet : AppRoute("student_wallet")
    data object StudentWalletKyc : AppRoute("student_wallet_kyc")
    data object StudentEditProfile : AppRoute("StudentEditProfile")
    data object StudentAssessmentConcept : AppRoute("StudentAssessmentConcept")
    data object StudentQuizList : AppRoute("StudentQuizList")
    data object StudentQuizInstructions : AppRoute("StudentQuizInstructions")
    data object QuizDisclaimerScreen : AppRoute("QuizDisclaimerScreen")
    data object QuizQuestionScreen : AppRoute("QuizQuestionScreen")
    data object QuizResultScreen : AppRoute("QuizResultScreen")

    data object StudentWalletDisclaimer : AppRoute("student_wallet_disclaimer")
    data object StudentPractice : AppRoute("student_practice")
    data object StudentPartner : AppRoute("student_partner")
    data object PartnerWebView : AppRoute("PartnerWebView")

    data class DetailsRoute(val index: Int) : AppRoute("details/{index}")

    //Student Onboarding
    @Serializable
    data class RegistrationStep1(val isAccepted: String? = null)

    @Serializable
    data class RegistrationStep2(val userDetails: String? = null)

    data class RegistrationNotice(val isAccepted: String) :
        AppRoute("RegistrationNotice/{isAccepted}")

    @Serializable
    data class SelectSubject(var grade: String? = null)

    @Serializable
    data class SelectSubjectPreference(var grade: String? = null)

    @Serializable
    data class BankAccountVerificationPreview(val upiId: String? = null)

    @Serializable
    data class TransferStatusScreenPreview(val upiId: String? = null)

    @Serializable
    data class UPIVerificationScreen(val upiId: String? = null)

    @Serializable
    data class UPITransferScreen(val userid: String? = null)

//    @Serializable
//    data class PartnerDetailsScreen(val status: String? = null)

    @Serializable
    data class BankAccountDeleteScreen(val userid: String? = null)

    @Serializable
    data class BankAddAccountScreen(val userid: String? = null)

    @Serializable
    data class BankAccountListScreen(val userid: String? = null)

    @Serializable
    data class ScholarshipInProgressScreen(val status: String? = null)

    @Serializable
    data class ScholarshipDisapprovedScreen(val status: String? = null)

    @Serializable
    data class ScholarshipDisbursedScreen(val status: String? = null)

    @Serializable
    data class ScholarshipPendingScreen(val status: String? = null)

    @Serializable
    data class ScholarshipApprovedScreen(val status: String? = null)

    @Serializable
    data class WalletHistoryScreen(val status: String? = null)

    @Serializable
    data class AadharCheck(val kycStatus: String? = null)

    @Serializable
    data class ManualPreviewDocument(
        val userId: String?,
        val kycStatus: String? = null,
        val kycText: String? = null,
        val docType: String? = null,
    )

    @Serializable
    data class ManualAuthenticationReviewScreen(
        val kycStatus: String? = null,
        val kycText: String? = null
    )

    @Serializable
    data class AadhaarInstruction(val isAccept: String? = null)

    @Serializable
    data class AadharOtpVerify(val adharNo: String? = null, val otpReferenceID: String? = null)

    @Serializable
    data class PhotoUpload(val userId: String? = null)

    @Serializable
    data class AuthenticationSchoolIdUpload(val userId: String? = null)

    @Serializable
    data class AuthenticationReview(val userId: String? = null)

    @Serializable
    data class AuthenticationStatus(val userId: String? = null)


    // Student Passport
    @Serializable
    data class StudentQuizReport(val userId: String? = null)

    @Serializable
    data class StudentQuizAttempts(val userId: String? = null)

    @Serializable
    data class StudentQuizScore(val userId: String? = null)

    @Serializable
    data class StudentQuizVerification(val userId: String? = null)

    @Serializable
    data class PracticeConceptList(val userId: String? = null)

    @Serializable
    data class PracticeQuestion(val userId: String? = null)

    @Serializable
    data class PracticeResult(val userId: String? = null)

    //Parent
    @Serializable
    data class ParentRegistrationStep1(val userId: String? = null)

    @Serializable
    data class ParentRegistrationStep2(val userId: String? = null)

    //    data object ParentDashboard : AppRoute("ParentDashboard")
//    data object ParentRegistrationStep2 : AppRoute("ParentRegistrationStep2")
    data object ParentDashboard : AppRoute("ParentDashboard")
    data object ParentAuthentication : AppRoute("ParentAuthentication")
    data object ParentStudent : AppRoute("ParentStudent")
    data object ParentAddStudent : AppRoute("ParentAddStudent")
    data object ParentEditProfile : AppRoute("ParentEditProfile")

    @Serializable
    data class QuizPerformance(
        val name: String? = null,
        val grade: Int = -1,
        val userId: String? = null
    )

    data object ParentProfile : AppRoute("ParentProfile")

    data object QuizReport : AppRoute("QuizReport")

    @Serializable
    data class StudentTopPerformingTopics(
        val userId: String? = null,
        val performingTopics: String? = null
    )


    // code change
    companion object {
        val Language_Activity = "Language_Activity"
        val StudentRegister = "StudentRegister"
        val Login_Activity = "Login_Activity"
        val Student_Dashboard = "Student_Dashboard"

    }
}
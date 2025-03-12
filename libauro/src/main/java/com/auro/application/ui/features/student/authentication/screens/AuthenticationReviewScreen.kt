package com.auro.application.ui.features.student.authentication.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.ConstantVariables.isRegistration
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.ParentDashboardActivity
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.authentication.model.GetKycAadhaarStatusResponseModel
import com.auro.application.ui.features.student.authentication.model.GetKycStatusResponseModel
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.DarkRed1
import com.auro.application.ui.theme.DarkRed2
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.GreenDark01
import com.auro.application.ui.theme.LightRed01
import com.auro.application.ui.theme.LightYellow02
import com.auro.application.ui.theme.Orange

@Preview
@Composable
fun AuthenticationReviewScreen(
    navHostController: NavHostController = rememberNavController(),
    sharedPref: SharedPref? = null,
    data: String? = null,
    viewModel: StudentViewModel = hiltViewModel()
) {
    val context: Context = LocalContext.current
    val loginViewModel: LoginViewModel = hiltViewModel()
//    var userId by rememberSaveable { mutableStateOf("0") }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    var kycStatusResponse by remember { mutableStateOf<GetKycStatusResponseModel.Data?>(null) }
    var studentAadhaarVerify by remember {
        mutableStateOf<GetKycAadhaarStatusResponseModel.AadhaarStatusData?>(
            null
        )
    }

    var studentNameVerified by remember { mutableStateOf("0") }
    var studentNameRemarks by remember { mutableStateOf("0") }
    var studentGradeVerified by remember { mutableStateOf("0") }
    var studentGradeRemarks by remember { mutableStateOf("0") }
    var studentProfileVerified by remember { mutableStateOf("0") }
    var schoolIdCardVerified by remember { mutableStateOf("0") }
    var studentProfileRemarks by remember { mutableStateOf("0") }
    var schoolIdCardRemarks by remember { mutableStateOf("0") }
    var studentKycStatus by remember { mutableStateOf("0") }
    var isFinished by remember { mutableStateOf("0") }
    var isSchoolCardRequired by remember { mutableStateOf("0") }
    Log.e("PhotoUpload", "Review launch: USER_ID  ")
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }
    var userId =
        if (loginViewModel.getParentInfo()!!.isParent) {  // if login as parent & came to authentication
            loginViewModel.getStudentList().userId    // studentId
        } else {
            loginViewModel.getUserId().toString() // parent id
        }

    LaunchedEffect(userId) {
        viewModel.getKycAadhaarStatus(userId.toInt())  // check id status
        viewModel.getKycStatus(userId.toInt())  // check if aadhaar card is uploaded
        Log.e("AuthReview", "Before launch: USER_ID  " + userId)
        viewModel.getKycStateLiveData.observeForever { networkStatus ->
            when (networkStatus) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    kycStatusResponse = null
                    kycStatusResponse = networkStatus.data?.data

                }

                is NetworkStatus.Error -> {}
            }
        }

        viewModel.getKycAadhaarStatusLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
//                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
//                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        loginViewModel.saveKycStatusData(it.data.data)
                        studentAadhaarVerify = it.data.data
                        studentNameVerified = it.data.data.studentNameVerified
                        studentNameRemarks = it.data.data.studentNameRemarks
                        studentGradeVerified = it.data.data.studentGradeVerified
                        studentGradeRemarks = it.data.data.studentGradeRemarks
                        studentProfileVerified = it.data.data.studentProfileVerified
                        schoolIdCardVerified = it.data.data.schoolIdCardVerified
                        studentProfileRemarks = it.data.data.studentProfileRemarks
                        schoolIdCardRemarks = it.data.data.schoolIdCardRemarks
                        studentKycStatus = it.data.data.studentKycStatus
                        isFinished = it.data.data.isFinished
                    }
                }

                is NetworkStatus.Error -> {
//                    isDialogVisible = false
                }
            }
        }
        // finish kyc process
        viewModel.getKycFinishData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
//                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
//                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        navHostController.popBackStack()
                        navHostController.navigate(AppRoute.AuthenticationStatus())
                    }
                }

                is NetworkStatus.Error -> {
//                    isDialogVisible = false
                }
            }
        }
    }
    BackHandler {
//        navHostController.popBackStack()
        if (loginViewModel.getParentInfo()!!.isParent) {
            startActivity(
                context,
                Intent(context, ParentDashboardActivity::class.java).apply {
                    (context as Activity).finish()
                },
                null
            )
        } else {
            startActivity(
                context,
                Intent(context, StudentDashboardActivity::class.java).apply {
                    (context as Activity).finish()
                },
                null
            )
        }
    }

    StudentRegisterBackground(
        isShowBackButton = true,
        onBackButtonClick = {
//            navHostController.popBackStack()
            if (loginViewModel.getParentInfo()!!.isParent) {
                startActivity(
                    context,
                    Intent(context, ParentDashboardActivity::class.java).apply {
                        (context as Activity).finish()
                    },
                    null
                )
            } else {
                startActivity(
                    context,
                    Intent(context, StudentDashboardActivity::class.java).apply {
                        (context as Activity).finish()
                    },
                    null
                )
            }
        },
        content = {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp)
                    ) {
                        Text(
                            text = if (studentAadhaarVerify != null && kycStatusResponse != null) {
                                if (studentKycStatus == "Approve" && kycStatusResponse!!.isAadhaarVerified == 1) {
                                    if (languageData[LanguageTranslationsResponse.REVIEW_PROFILE_AUTHENTICATION_DETAILS].toString() == "") {
                                        stringResource(id = R.string.review_profile_authentication_details)
                                    } else {
                                        languageData[LanguageTranslationsResponse.REVIEW_PROFILE_AUTHENTICATION_DETAILS].toString()
                                    }
                                } else {
                                    if (languageData[LanguageTranslationsResponse.COMPLETE_AUTHENTICATION].toString() == "") {
                                        stringResource(id = R.string.complete_your_authentication)
                                    } else {
                                        languageData[LanguageTranslationsResponse.COMPLETE_AUTHENTICATION].toString()
                                    }
                                }
                            } else {
                                if (languageData[LanguageTranslationsResponse.COMPLETE_AUTHENTICATION].toString() == "") {
                                    stringResource(id = R.string.complete_your_authentication)
                                } else {
                                    languageData[LanguageTranslationsResponse.COMPLETE_AUTHENTICATION].toString()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp),
                            fontFamily = FontFamily(
                                Font(R.font.inter_bold, FontWeight.Bold)
                            ),
                            fontSize = 20.sp,
                            color = Black,
                            textAlign = TextAlign.Left
                        )

                        Text(
                            text = if (languageData[LanguageTranslationsResponse.CHECK_FIELDS_MATCH_AADHAR].toString() == "") {
                                stringResource(id = R.string.please_check_if_all_fields_match_with_aadhar_card)
                            } else {
                                languageData[LanguageTranslationsResponse.CHECK_FIELDS_MATCH_AADHAR].toString()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            color = GrayLight01,
                            fontFamily = FontFamily(
                                Font(R.font.inter_regular, FontWeight.Normal)
                            ),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Start
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(weight = 1f, fill = false)
                            .padding(all = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 70.dp)
                        ) {
                            if (kycStatusResponse != null && studentAadhaarVerify != null) {
                                ProfileAuthenticationRow(
                                    painterResource(id = R.drawable.profile_icon),
                                    painterResource(id = getStateIcon(studentNameVerified)),
                                    if (loginViewModel.getParentInfo() != null) {
                                        if (loginViewModel.getParentInfo()!!.isParent) {
                                            loginViewModel.getStudentList().name
                                        } else {
                                            loginViewModel.getStudentProfileData()!!.name
                                        }
                                    } else {
                                        loginViewModel.getStudentData().name
                                    },
                                    "Pending",
                                    loginViewModel,
                                    studentAadhaarVerify!!.studentNameVerified,
                                    studentAadhaarVerify!!.studentNameVerified,
                                    studentAadhaarVerify!!.studentNameRemarks

                                ) {
                                    if (studentNameVerified != "1") {
                                        if (!loginViewModel.getParentInfo()!!.isParent) {
                                            navHostController.navigate(AppRoute.StudentEditProfile.route)
                                        }
                                    }
                                }

                                ProfileAuthenticationRow(
                                    painterResource(id = R.drawable.star_icon),
                                    painterResource(id = getStateIcon(studentGradeVerified)),
                                    if (languageData[LanguageTranslationsResponse.KEY_GRADE_UPDATED].toString() == "") {
                                        "Grade Updated"
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_GRADE_UPDATED].toString()
                                    },
                                    "Pending",
                                    loginViewModel,
                                    studentAadhaarVerify!!.studentGradeVerified,
                                    studentAadhaarVerify!!.studentGradeVerified,
                                    studentAadhaarVerify!!.studentGradeRemarks
                                ) {
                                    if (studentNameVerified != "1") {
                                        if (!loginViewModel.getParentInfo()!!.isParent) {
                                            navHostController.navigate(AppRoute.StudentEditProfile.route)
                                        }
                                    }
                                }

                                if (kycStatusResponse?.isSchoolCardRequired == 1) {      // if School card required
                                    ProfileAuthenticationRow(
                                        painterResource(id = R.drawable.student_id_upload),
                                        painterResource(
                                            id = getStateIcon(
                                                if ((kycStatusResponse?.isSchoolCardUploaded.toString() == "1") && (schoolIdCardVerified == "0")) {
                                                    "3"
                                                } else if (kycStatusResponse?.isSchoolCardUploaded.toString() == "0") {
                                                    "0"
                                                } else {
                                                    schoolIdCardVerified
                                                }
                                            )
                                        ),
                                        "Student Id upload",
                                        "Pending",
                                        loginViewModel,
                                        kycStatusResponse!!.isSchoolCardUploaded.toString(),
                                        studentAadhaarVerify!!.schoolIdCardVerified,
                                        studentAadhaarVerify!!.schoolIdCardRemarks
                                    ) {
                                        if (studentAadhaarVerify!!.schoolIdCardVerified != "1") {
                                            navHostController.popBackStack()
                                            navHostController.navigate(
                                                AppRoute.AuthenticationSchoolIdUpload(
                                                    userId
                                                )
                                            )
                                        }
                                    }
                                }

                                ProfileAuthenticationRow(
                                    painterResource(id = R.drawable.camera_icon),
                                    painterResource(
                                        id = getStateIcon(
                                            if ((kycStatusResponse?.isPhotoUploaded.toString() == "1") && (studentProfileVerified == "0")) {
                                                "3"
                                            } else if (kycStatusResponse?.isPhotoUploaded.toString() == "0") {
                                                "0"
                                            } else {
                                                studentProfileVerified
                                            }
                                        )
                                    ),
                                    if (languageData[LanguageTranslationsResponse.PHOTO_UPDATED].toString() == "") {
                                        "Photo Updated"
                                    } else {
                                        languageData[LanguageTranslationsResponse.PHOTO_UPDATED].toString()
                                    },
                                    "Pending",
                                    loginViewModel,
                                    kycStatusResponse!!.isPhotoUploaded.toString(),
                                    studentAadhaarVerify!!.studentProfileVerified,
                                    studentAadhaarVerify!!.studentProfileRemarks
                                ) {
                                    if (studentAadhaarVerify!!.studentProfileVerified != "1") {
                                        navHostController.popBackStack()
                                        navHostController.navigate(
                                            AppRoute.PhotoUpload(
                                                userId
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                if (kycStatusResponse != null)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .align(Alignment.BottomCenter)
                            .background(color = Color.White)
                            .shadow(elevation = 0.5.dp, shape = RoundedCornerShape(0.dp)),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        BtnUi(
                            onClick = {
//                            if (isFinished == "1") {
                                if (studentKycStatus == "Approve" && kycStatusResponse!!.isAadhaarVerified == 1) {
                                    viewModel.getKycFinish(userId.toInt())
                                }
                            },
                            title = if (languageData[LanguageTranslationsResponse.FINISH].toString() == "") {
                                stringResource(id = R.string.finish)
                            } else {
                                languageData[LanguageTranslationsResponse.FINISH].toString()
                            },
                            enabled = studentKycStatus == "Approve" && kycStatusResponse!!.isAadhaarVerified == 1
                        )
                    }
            }
        }
    )
}

fun getStateIcon(state: String?): Int {
    /*
                                0 = pending
                                1 = approve
                                2 = disapprove
                                InProcess = '3',
                                 ChangeKYC = '4',
    */
    return when (state) {
        "0" -> R.drawable.ic_right_side
        "1" -> R.drawable.circle_checkbox // Photo uploaded, verification in progress
        "2" -> R.drawable.ic_cancel // Photo uploaded, verified
        else -> R.drawable.ic_pending // No photo uploaded or unknown status
    }
}

@Composable
fun ProfileAuthenticationRow(
    leftIcon: Painter,
    rightIcon: Painter,
    text1: String,
    text2: String,
    loginViewModel: LoginViewModel,
    kycUploadStatus: String,
    studentKycStatus: String,
    remarks: String,
    click: () -> Unit
) {
    Column {

        Column(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = GrayLight02,
                    shape = RoundedCornerShape(10)
                )
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .wrapContentHeight()
                .padding(bottom = 5.dp, start = 2.dp, end = 0.dp)

                .clickable {
                    click.invoke()
                }) {
                Image(
                    painter = leftIcon,
                    contentDescription = "leftIcon",
                    modifier = Modifier
                        .size(65.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(), verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = text1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                        fontFamily = FontFamily(
                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                        ),
                        fontSize = 15.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Left
                    )

                    Text(
                        text = when (kycUploadStatus) {
                            "0" -> {
                                if (remarks != "") {
                                    "In-Process"
                                } else {
                                    text2  // pending
                                }
                            }

                            "1" -> {     //Uploaded but not uploaded
                                when (studentKycStatus) {
                                    "0" -> {
                                        "Uploaded"
                                    }

                                    "1" -> {
                                        "Approved"
                                    }

                                    "2" -> {
                                        "Disapproved"
                                    }

                                    "3" -> {
                                        "Under Verification"
                                    }

                                    else -> text2
                                }
                            }

                            else -> {
                                when (studentKycStatus) {
                                    "0" -> {
                                        "Uploaded"
                                    }

                                    "1" -> {
                                        "Approved"
                                    }

                                    "2" -> {
                                        "Disapproved"
                                    }

                                    "3" -> {
                                        "Under Verification"
                                    }

                                    else -> text2
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        color = when (kycUploadStatus) {
                            "0" -> {
                                GrayLight01
                            }

                            "1" -> {
                                when (studentKycStatus) {
                                    "0" -> {
                                        Orange
                                    }

                                    "1" -> {
                                        GreenDark01
                                    }

                                    "2" -> {
                                        DarkRed1
                                    }

                                    "3" -> {
                                        GrayLight01
                                    }

                                    else -> GrayLight01
                                }
                            }

                            else -> {
//                                GrayLight01
                                when (studentKycStatus) {
                                    "0" -> {
                                        Orange
                                    }

                                    "1" -> {
                                        GreenDark01
                                    }

                                    "2" -> {
                                        DarkRed1
                                    }

                                    "3" -> {
                                        GrayLight01
                                    }

                                    else -> GrayLight01
                                }
                            }
                        },
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
                        ),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                }
                if (!loginViewModel.getParentInfo()!!.isParent) {
                    Image(
                        painter = rightIcon,
                        contentDescription = "rightIcon",
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 5.dp)
                    )
                }
            }

            if (remarks != "" || remarks.isNotEmpty()) {
                if (remarks != "Approved") {
                    Column(
                        modifier = Modifier
                            .background(
                                color = DarkRed2.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(20)
                            )
                            .padding(10.dp)
                    ) {
                        Text(
                            text = remarks,
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp, vertical = 3.dp),
                            fontFamily = FontFamily(
                                Font(R.font.inter_regular, FontWeight.Normal)
                            ),
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
    }
}

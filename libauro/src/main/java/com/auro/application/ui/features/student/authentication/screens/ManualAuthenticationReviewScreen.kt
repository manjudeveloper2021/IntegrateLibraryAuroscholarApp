package com.auro.application.ui.features.student.authentication.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.auro.application.core.ConstantVariables.EDIT_MOBILE
import com.auro.application.core.platform.CustomDialog
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
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.GreenDark01
import com.auro.application.ui.theme.GreenDark02
import com.auro.application.ui.theme.LightYellow02
import com.auro.application.ui.theme.Orange

@Preview
@Composable
fun ManualAuthenticationReviewScreen(
    navHostController: NavHostController = rememberNavController(),
    sharedPref: SharedPref? = null,
    data: String? = null,
) {
    var studentAadhaarVerify by remember {
        mutableStateOf<GetKycAadhaarStatusResponseModel.AadhaarStatusData?>(
            null
        )
    }

    var studentKycVerifyStatus by remember { mutableStateOf<GetKycStatusResponseModel.Data?>(null) }
    val context: Context = LocalContext.current
    val loginViewModel: LoginViewModel = hiltViewModel()
    val viewModel: StudentViewModel = hiltViewModel()
    var isDialogVisible by remember { mutableStateOf(false) }
    var studentKycStatus by remember { mutableStateOf("0") }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)
    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = msgLoader
    )
    val userId =
        if (loginViewModel.getParentInfo() != null) {
            if (loginViewModel.getParentInfo()!!.isParent) {  // if login as parent & came to authentication
                loginViewModel.getStudentList().userId    // studentId
            } else {
                loginViewModel.getUserId().toString()  // parent id / current userID
            }
        } else {
            loginViewModel.getUserId().toString()
        }
    viewModel.getKycAadhaarStatus(userId.toInt())  // check id status
    viewModel.getKycStatus(userId.toInt())  // check if aadhaar card is uploaded
    LaunchedEffect(Unit) {
        isDialogVisible = true

        // API for KYC response
        viewModel.getKycAadhaarStatusLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {

                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        loginViewModel.saveKycStatusData(it.data.data)
                        studentAadhaarVerify = it.data.data
                    }
                }

                is NetworkStatus.Error -> {

                }
            }
        }

        viewModel.getKycStateLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {

                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        loginViewModel.saveKycDocUploadStatus(it.data.data)
                        studentKycVerifyStatus = it.data.data
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
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
        navHostController.popBackStack()
        if (loginViewModel.getParentInfo()!!.isParent) {
            startActivity(
                context,
                Intent(context, ParentDashboardActivity::class.java),
                null
            ).apply { (context as Activity).finish() }
        } else {
            startActivity(
                context,
                Intent(context, StudentDashboardActivity::class.java),
                null
            ).apply { (context as Activity).finish() }
        }
    }

    StudentRegisterBackground(
        isShowBackButton = true,
        onBackButtonClick = {
            navHostController.popBackStack()
            if (loginViewModel.getParentInfo()!!.isParent) {
                startActivity(
                    context,
                    Intent(context, ParentDashboardActivity::class.java),
                    null
                )
            } else {
                startActivity(
                    context,
                    Intent(context, StudentDashboardActivity::class.java),
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
                            text = if (studentAadhaarVerify != null && studentKycVerifyStatus != null) {
                                if (studentAadhaarVerify!!.studentKycStatus == "Approve" && studentKycVerifyStatus!!.isAadhaarVerified == 1) {
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
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Bold,
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
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Normal,
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
                            if (studentKycVerifyStatus != null && studentAadhaarVerify != null) {
                                ProfileManualAuthenticationRow(
                                    languageData = languageData,
                                    painterResource(id = R.drawable.ic_aadhaar_front),
                                    painterResource(id = getIconState(studentAadhaarVerify!!.aadhaarFrontVerified.toString())),
                                    "Aadhaar front side",
                                    "Please upload a clear picture with all the information visible",
                                    studentKycVerifyStatus!!.isAadhaarFrontUploaded.toString(),
                                    studentAadhaarVerify!!.aadhaarFrontVerified,
                                    studentAadhaarVerify!!.aadhaarFrontRemarks
                                ) {
                                    if (studentAadhaarVerify!!.aadhaarFrontVerified == "0"   //Pending
                                        || studentAadhaarVerify!!.aadhaarFrontVerified == "2"   // Disapprove
                                        || studentAadhaarVerify!!.aadhaarFrontVerified == "4"   //ChangeKYC
                                    ) {
                                        navHostController.navigate(
                                            AppRoute.ManualPreviewDocument(
                                                userId, "", "Aadhaar Front Preview ", "5"
                                            )
                                        )
                                    }
                                }

                                ProfileManualAuthenticationRow(
                                    languageData = languageData,
                                    painterResource(id = R.drawable.ic_aadhaar_back),
                                    painterResource(id = getIconState(studentAadhaarVerify!!.aadhaarBackVerified)),
                                    "Aadhaar Back Side",
                                    "Please upload a clear picture with all the information visible",
                                    studentKycVerifyStatus!!.isAadhaarBackUploaded.toString(),
                                    studentAadhaarVerify!!.aadhaarBackVerified,
                                    studentAadhaarVerify!!.aadhaarBackRemarks
                                ) {
                                    if (studentAadhaarVerify!!.aadhaarBackVerified == "0"   //Pending
                                        || studentAadhaarVerify!!.aadhaarBackVerified == "2"   // Disapprove
                                        || studentAadhaarVerify!!.aadhaarBackVerified == "4"   //ChangeKYC
                                    ) {
                                        navHostController.navigate(
                                            AppRoute.ManualPreviewDocument(
                                                userId, "", "Aadhaar Back Preview ", "6"
                                            )
                                        )
                                    }
                                }

                                ProfileManualAuthenticationRow(
                                    languageData = languageData,
                                    painterResource(id = R.drawable.profile_icon),
                                    painterResource(id = getIconState(studentAadhaarVerify!!.studentProfileVerified)),
                                    "Photo",
                                    "Please upload a clear picture with all the information visible",
                                    studentKycVerifyStatus!!.isPhotoUploaded.toString(),
                                    studentAadhaarVerify!!.studentProfileVerified,
                                    studentAadhaarVerify!!.studentProfileRemarks
                                ) {
                                    if (studentAadhaarVerify!!.studentProfileVerified == "0"   //Pending
                                        || studentAadhaarVerify!!.studentProfileVerified == "2"   // Disapprove
                                        || studentAadhaarVerify!!.studentProfileVerified == "4"   //ChangeKYC
                                    ) {
                                        navHostController.navigate(
                                            AppRoute.ManualPreviewDocument(
                                                userId, "", "Photo Preview ", "4"
                                            )
                                        )
                                    }
                                }

                                ProfileManualAuthenticationRow(
                                    languageData = languageData,
                                    painterResource(id = R.drawable.ic_school_id),
                                    painterResource(id = getIconState(studentAadhaarVerify!!.schoolIdCardVerified)),
                                    "School Id",
                                    "Please upload a clear picture with all the information visible",
                                    studentKycVerifyStatus!!.isSchoolCardUploaded.toString(),
                                    studentAadhaarVerify!!.schoolIdCardVerified,
                                    studentAadhaarVerify!!.schoolIdCardRemarks
                                ) {
                                    if (studentAadhaarVerify!!.schoolIdCardVerified == "0"   //Pending
                                        || studentAadhaarVerify!!.schoolIdCardVerified == "2"   // Disapprove
                                        || studentAadhaarVerify!!.schoolIdCardVerified == "4"  //ChangeKYC
                                    ) {
                                        navHostController.navigate(
                                            AppRoute.ManualPreviewDocument(
                                                userId, "", "School Id Card Preview", "3"
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                if (studentAadhaarVerify != null && studentKycVerifyStatus != null) {
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
                                if (studentAadhaarVerify!!.studentKycStatus == "Approve") {
                                    navHostController.popBackStack()
                                    navHostController.navigate(AppRoute.AuthenticationStatus())
//                                    viewModel.getKycFinish(userId.toInt())
                                }
                            },
                            title = stringResource(id = R.string.txt_submit),
                            enabled = studentAadhaarVerify!!.studentKycStatus == "Approve"
                        )
                    }
                }
            }

        }
    )
}

@Composable
fun ProfileManualAuthenticationRow(
    languageData: HashMap<String, String>,
    leftIcon: Painter,
    rightIcon: Painter,
    text1: String,
    text2: String,
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
                        .background(Color.Unspecified) // Use a background to check layout
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
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Left
                    )

                    Text(
                        text = when (kycUploadStatus) {
                            "0" -> {  //pending
                                text2
                            }

                            "1" -> {     //Uploaded  but not uploaded
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
                                text2
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, top = 5.dp),
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
                                GrayLight01
                            }
                        }, // Adjusting for GrayLight01
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                }
//                if (!loginViewModel.getParentInfo()!!.isParent) {
                Image(
                    painter = rightIcon,
                    contentDescription = "rightIcon",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 5.dp)
                )
//                }
            }

            if (remarks != "" || remarks.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .background(
                            color = LightYellow02,
                            shape = RoundedCornerShape(20)
                        )
                        .padding(10.dp)
                ) {
                    Text(
                        text = remarks,
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                    )
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

fun getIconState(state: String?): Int {
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

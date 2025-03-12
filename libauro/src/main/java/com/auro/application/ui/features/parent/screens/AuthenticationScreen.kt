package com.auro.application.ui.features.parent.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.ParentDashboardActivity
import com.auro.application.ui.features.parent.model.StudentInformetionResponseModel
import com.auro.application.ui.features.parent.viewmodel.ParentViewModel
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.authentication.StudentAuthenticationActivity
import com.auro.application.ui.features.student.authentication.model.GetKycAadhaarStatusResponseModel
import com.auro.application.ui.features.student.authentication.screens.ProfileAuthenticationRow
import com.auro.application.ui.features.student.authentication.screens.getStateIcon
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.White

@Preview
@Composable
fun AuthenticationScreen(
    navController: NavHostController = rememberNavController(),
    sharedPref: SharedPref? = null,
    title: (String) -> Unit = {}
) {

    val viewModel: ParentViewModel = hiltViewModel()
    val studentViewModel: StudentViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    val studentData =
        remember { mutableStateListOf<StudentInformetionResponseModel.Data.Student>() }
    var studentDataItem by remember {
        mutableStateOf<StudentInformetionResponseModel.Data.Student?>(
            null
        )
    }
    val context = LocalContext.current
    var isDialogVisible by remember { mutableStateOf(false) }
    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
//        message = "Loading your data..."
        message = msgLoader
    )
    BackHandler {
        navController.popBackStack()
        startActivity(
            context,
            Intent(context, ParentDashboardActivity::class.java).apply {
                (context as Activity).finish()
            },
            null
        )
    }
    LaunchedEffect(Unit) {
//        title.invoke("")
        title.invoke(languageData[LanguageTranslationsResponse.AUTHENTICATION].toString())

        viewModel.studentInformationResponseModelLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        studentData.clear()
//                        Log.e("TAG", "ParentDashboardScreen: child list is her "+it.data?.data?.size )
                        loginViewModel.saveChildCount(it.data.data.student.size)
                        studentData.addAll(it.data.data.student)
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
            }
        }
        // API for KYC response
        studentViewModel.getKycAadhaarStatusLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {

                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        loginViewModel.saveKycStatusData(it.data.data)
                    }
                }

                is NetworkStatus.Error -> {

                }
            }
        }

        studentViewModel.getKycStateLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {

                }

                is NetworkStatus.Success -> {
//                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        loginViewModel.saveKycDocUploadStatus(it.data.data)
                        isDialogVisible = false
                        startActivity(
                            context,
                            Intent(context, StudentAuthenticationActivity::class.java),
                            null
                        )

                    }
                }

                is NetworkStatus.Error -> {
//                    isDialogVisible = false
                }
            }
        }
        viewModel.getParentStudentList()

    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val connection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    return super.onPreScroll(available, source)
                }
            }
        }
        var showDialog by remember { mutableStateOf(false) }

        // not in use
        if (studentDataItem != null)
            FullScreenDialog(
                studentViewModel,
                loginViewModel,
                studentDataItem = studentDataItem!!,
                showDialog = showDialog,
                onDismissRequest = { showDialog = false }
            ) {
            }

        Box(Modifier.nestedScroll(connection)) {
            Column {
                Text(
//                    text = "Select Account to Complete \nAuthentication",
                    text = languageData[LanguageTranslationsResponse.KEY_COMPLETE_AUTHENTICATION].toString(),
                    fontSize = 20.sp,
                    fontFamily = FontFamily(
                        Font(R.font.inter_bold, FontWeight.Bold)
                    ),
                    modifier = Modifier.padding(15.dp)
                )

                LazyColumn {
                    items(studentData) { item ->
                        Column(
                            modifier = Modifier
                                .clickable {
                                    isDialogVisible = true
                                    studentDataItem = item
                                    loginViewModel.saveStudentList(item)
                                    studentViewModel.getKycAadhaarStatus(item.userId.toInt())  // check id status
                                    studentViewModel.getKycStatus(item.userId.toInt())  // check if aadhaar card is uploaded

                                }
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                colors = CardColors(
                                    containerColor = White,
                                    contentColor = White,
                                    disabledContentColor = White,
                                    disabledContainerColor = White
                                ),
                                border = BorderStroke(width = 0.5.dp, GrayLight02),
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (item.gender != null && item.imageUrl != null) {
                                            CustomCircularImageViewWithBorder(
                                                item.gender,
                                                imageRes = item.imageUrl,
                                                borderColor = GrayLight02,
                                                borderWidth = 2f
                                            )
                                        } else {
                                            println("Gender and image not found...")
                                        }
                                        Column(
                                            modifier = Modifier
                                                .wrapContentWidth()
                                                .padding(horizontal = 20.dp, vertical = 10.dp)
                                        ) {
                                            Text(
                                                text = item.name,
                                                fontSize = 14.sp,
                                                fontFamily = FontFamily(
                                                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                                ),
                                                color = Gray,
                                            )

                                            Text(
                                                text = if (languageData[LanguageTranslationsResponse.KEY_CLASS].toString() == "") {
                                                    "Class - " + item.grade.toString()
                                                } else {
                                                    languageData[LanguageTranslationsResponse.KEY_CLASS].toString() + " - " + item?.grade.toString()
                                                },
                                                fontFamily = FontFamily(
                                                    Font(R.font.inter_regular, FontWeight.Normal)
                                                ),
                                                fontSize = 12.sp,
                                                color = GrayLight01
                                            )
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        Image(
                                            painter = painterResource(R.drawable.ic_right_side),
                                            contentDescription = stringResource(id = R.string.icon_description),
                                            modifier = Modifier
                                                .size(25.dp)
                                                .background(Color.Unspecified)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// not in use
@Composable
fun FullScreenDialog(
    studentViewModel: StudentViewModel,
    loginViewModel: LoginViewModel,
    studentDataItem: StudentInformetionResponseModel.Data.Student,
    showDialog: Boolean = true,
    onDismissRequest: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {

    var isDialogVisible by remember { mutableStateOf(false) }
    var studentNameVerified by remember { mutableStateOf("0") }
    var studentNameRemarks by remember { mutableStateOf("0") }
    var studentGradeVerified by remember { mutableStateOf("0") }
    var studentGradeRemarks by remember { mutableStateOf("0") }
    var studentProfileVerified by remember { mutableStateOf("0") }
    var studentProfileRemarks by remember { mutableStateOf("0") }
    var schoolIdCardRemarks by remember { mutableStateOf("0") }
    var studentKycStatus by remember { mutableStateOf("0") }
    var isFinished by remember { mutableStateOf("0") }
    val studentAadhaarVerify =
        remember { mutableStateListOf<GetKycAadhaarStatusResponseModel.AadhaarStatusData>() }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)
    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = msgLoader
    )
    var userId by rememberSaveable { mutableStateOf("0") }
    userId =
        if (loginViewModel.getParentInfo()!!.isParent) {  // if login as parent & came to authentication
            loginViewModel.getStudentList().userId    // studentId
        } else {
            loginViewModel.getUserId().toString() // parent id
        }
    /* LaunchedEffect(userId) {
         studentViewModel.getKycAadhaarStatusLiveData.observeForever {
             when (it) {
                 is NetworkStatus.Idle -> {}
                 is NetworkStatus.Loading -> {
                     isDialogVisible = true
                 }

                 is NetworkStatus.Success -> {
                     isDialogVisible = false
                     if (it.data?.isSuccess == true) {
                         studentNameVerified = it.data.data.studentNameVerified
                         studentNameRemarks = it.data.data.studentNameRemarks
                         studentGradeVerified = it.data.data.studentGradeVerified
                         studentGradeRemarks = it.data.data.studentGradeRemarks
                         studentProfileVerified = it.data.data.studentProfileVerified
                         studentProfileRemarks = it.data.data.studentProfileRemarks
                         schoolIdCardRemarks = it.data.data.schoolIdCardRemarks
                         studentKycStatus = it.data.data.studentKycStatus
                         isFinished = it.data.data.isFinished

                     }
                 }

                 is NetworkStatus.Error -> {
                     isDialogVisible = false
                 }
             }
         }


         studentViewModel.getKycAadhaarStatus(userId.toInt())

     }*/

    if (showDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = White),
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 70.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.back_icon),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(
                                Color.Gray
                            ), modifier = Modifier
                                .padding(10.dp)
                                .background(Color.Unspecified)
                                .clickable {
                                    onDismissRequest.invoke()
                                }
                        )
                    }
                    Text(
//                        text = "Review Profile Authentication \nDetails",
                        text = languageData[LanguageTranslationsResponse.REVIEW_PROFILE_AUTHENTICATION_DETAILS].toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                    Text(
//                        text = "Please check if all fields match with Aadhar card.",
                        text = languageData[LanguageTranslationsResponse.CHECK_FIELDS_MATCH_AADHAR].toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal, modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp), color = GrayLight01
                    )
                    Spacer(modifier = Modifier.padding(5.dp))

                    /*LazyColumn {
                        items(studentAadhaarVerify) { item ->*/
                    Column(
                        modifier = Modifier
                            .clickable {
                                /*studentDataItem = item
                                        loginViewModel.saveStudentList(item!!)
                                        showDialog = true*/
                            }
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        ProfileReview(
                            painterResource(id = R.drawable.profile_icon),
                            name = loginViewModel.getStudentList().name,
                            detail = studentNameRemarks,
                            approvedStatus = studentNameVerified
                        )
                        Spacer(modifier = Modifier.padding(5.dp))

                        ProfileReview(
                            painterResource(id = R.drawable.star_icon),
//                            name = "Grade Updated",
                            name = languageData[LanguageTranslationsResponse.KEY_GRADE_UPDATED].toString(),
                            detail = studentGradeRemarks,
                            approvedStatus = studentGradeVerified
                        )

                        if (studentGradeVerified == "2") {      // if grade is disapproved
                            ProfileReview(
                                painterResource(id = R.drawable.star_icon),
//                                name = "School Id Update",
                                name = languageData[LanguageTranslationsResponse.SCHOOL_ID_UPDATE].toString(),
//                                detail = "Your grade-age does not match. Please upload your School ID",
                                detail = languageData[LanguageTranslationsResponse.YOUR_GRADE_AGE_NOT_MATCHED].toString(),
                                approvedStatus = studentGradeVerified
                            )
                        }
                        Spacer(modifier = Modifier.padding(5.dp))
                        ProfileReview(
                            painterResource(id = R.drawable.camera_icon),
//                            name = "Photo Updated",
                            name = languageData[LanguageTranslationsResponse.PHOTO_UPDATED].toString(),
                            detail = studentProfileRemarks,
                            approvedStatus = studentProfileVerified
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileReview(icon: Painter, name: String, detail: String, approvedStatus: String) {
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

                }) {
                Image(
                    painter = icon,
                    contentDescription = "leftIcon",
                    modifier = Modifier
                        .size(65.dp)
                        .background(Color.Unspecified)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(), verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = name,
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
                        text = detail,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        color = Color.Gray.copy(alpha = 0.6f), // Adjusting for GrayLight01
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                }

                Image(
                    painter = if (approvedStatus == "1") {
                        painterResource(id = R.drawable.circle_checkbox)
                    } else {
                        painterResource(id = R.drawable.ic_right_side)
                    },
                    contentDescription = "rightIcon",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 5.dp)
                        .background(Color.Unspecified)
                )
            }

        }
    }
}

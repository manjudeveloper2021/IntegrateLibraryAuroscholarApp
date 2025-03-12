package com.auro.application.ui.features.student.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.auro.application.R
import com.auro.application.core.ConstantVariables.isKYC
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.Constants
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.data.utlis.CustomProgressBar
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.TextWithIconExample
import com.auro.application.ui.common_ui.TextWithIconQuizExample
import com.auro.application.ui.common_ui.components.DoubleBackPressHandler
import com.auro.application.ui.common_ui.components.WalkThroughModel
import com.auro.application.ui.common_ui.components.WalkthroughDialog
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.GetSubjectListResponseModel
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.model.GetBannerListResponseModel
import com.auro.application.ui.features.student.authentication.StudentAuthenticationActivity
import com.auro.application.ui.features.student.models.StudentProfileResponseModel
import com.auro.application.ui.features.student.passport.BadgesAndAwardsActivity
import com.auro.application.ui.features.student.passport.StudentLeaderboardActivity
import com.auro.application.ui.features.student.passport.StudentUnlockAwardsActivity
import com.auro.application.ui.features.student.passport.models.AwardData
import com.auro.application.ui.features.student.passport.models.BadgeAwardData
import com.auro.application.ui.features.student.passport.models.BadgeData
import com.auro.application.ui.features.student.passport.models.MonthlyReport
import com.auro.application.ui.features.student.passport.models.ReferalCodeResponse
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightPink02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.Transparent
import com.auro.application.ui.theme.White
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomePage(navController: NavHostController, intent: String?, sharedPref: SharedPref) {
    val activity = LocalContext.current as? Activity
    val context: Context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    var totalProgress by remember { mutableIntStateOf(0) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })
    val isShowWalkthrough = remember { mutableStateOf(sharedPref.getDashboardWalkThought()) }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    //    val scrollState = rememberScrollState()
    val textPlayQuiz = stringResource(id = R.string.txt_play_quiz)
    val icon = painterResource(id = R.drawable.dash_help)
    val color = Color.Black
    val size = 18.sp

    // Remembering the Progress State
    val scrollState = rememberNestedScrollInteropConnection()
    val selectedBorder = BorderStroke(width = 0.5.dp, GrayLight02)
//    val profileProgress = stringResource(id = R.string.text_profileProgress)
    val profileProgress =
        if (languageData[LanguageTranslationsResponse.KEY_PROFILE_PROGRESS].toString() == "") {
            stringResource(id = R.string.text_profileProgress)
        } else {
            languageData[LanguageTranslationsResponse.KEY_PROFILE_PROGRESS].toString()
        }
    val textQuiz = stringResource(id = R.string.txt_quizzes)
    val completeYourProfile =
        if (languageData[LanguageTranslationsResponse.KEY_COMPLETE_PROFILER].toString() == "") {
            stringResource(id = R.string.text_complete_your_profile)
        } else {
            languageData[LanguageTranslationsResponse.KEY_COMPLETE_PROFILER].toString()
        }
    val languageState by viewModel.languageResponse.collectAsState()
    val networkStatus = languageState
    var subjectData by remember { mutableStateOf(mutableListOf<GetSubjectListResponseModel.Data>()) }
//    var subjectData by remember { mutableListOf<GetSubjectListResponseModel.Data?>(null) }
    var monthlyReport by remember { mutableStateOf<MonthlyReport?>(null) }
    var referalCode by remember { mutableStateOf<ReferalCodeResponse.Data?>(null) }
    var completeProfiler by remember { mutableStateOf<String>("") }
    var kycProgress by remember { mutableStateOf(0) }
    var kycStatus by remember { mutableStateOf("") }
    var profilerProgress by remember { mutableStateOf(0) }
    var quizProgress by remember { mutableStateOf(0) }
    var profileData by remember { mutableStateOf<StudentProfileResponseModel.ProfileData?>(null) }

    var badgeAwardReportData by remember { mutableStateOf<BadgeAwardData?>(null) }
    var badgeReport by remember { mutableStateOf<BadgeData?>(null) }
    var awardReport by remember { mutableStateOf<AwardData?>(null) }
    var isDialogVisible by remember { mutableStateOf(false) }
    val studentBannerListResponseModel =
        remember { mutableStateListOf<GetBannerListResponseModel.Data?>() }

    val isShow = remember { mutableStateOf(sharedPref.getDashboardWalkThought()) }
    val list = listOf<WalkThroughModel>(
        WalkThroughModel(
            "Subject Selection Simplified",
            if (languageData[LanguageTranslationsResponse.WELCOME_IN_ELV_TH_AND_TW_TH_GRADE].toString() == "") {
                "Welcome! In 11th and 12th Grade? Pick your quiz subjects. For other grades, subjects are auto-selected."
            } else {
                languageData[LanguageTranslationsResponse.WELCOME_IN_ELV_TH_AND_TW_TH_GRADE].toString()
            },
            R.drawable.select_your_subject
        ), WalkThroughModel(
            "Explore Fresh Concepts Monthly",
            if (languageData[LanguageTranslationsResponse.CHOOSE_FOUR_NEW_CONCEPTS_EACH_MONTH].toString() == "") {
                "Choose 4 new concepts each month from 48 options. These concepts cannot be repeated in a year!"
            } else {
                languageData[LanguageTranslationsResponse.CHOOSE_FOUR_NEW_CONCEPTS_EACH_MONTH].toString()
            },
            R.drawable.select_concepts
        ), WalkThroughModel(
            "Pick Smart, Win More!",
            if (languageData[LanguageTranslationsResponse.CHANGE_SUBJECTS_AND_CONCEPTS_MONTHLY].toString() == "") {
                "Change subjects and concepts monthly if you haven't played quizzes. For each quiz, you get 2 extra tries and 3 practices."
            } else {
                languageData[LanguageTranslationsResponse.CHANGE_SUBJECTS_AND_CONCEPTS_MONTHLY].toString()
            },
            R.drawable.other_subject
        ), WalkThroughModel(
            "Play, Learn, and Win!",
            if (languageData[LanguageTranslationsResponse.READY].toString() == "") {
                "Ready? Choose concepts, start quizzes, and win! Keep playing, and have fun learning!"
            } else {
                languageData[LanguageTranslationsResponse.READY].toString()
            },
            R.drawable.add_more_concept
        )
    )

    if (isShowWalkthrough.value) {
        WalkthroughDialog(showDialog = true, onDismissRequest = {
            isShowWalkthrough.value = false
            sharedPref.saveDashboardWalkthrough(isShowWalkthrough.value)
        }, list)
    }

    DoubleBackPressHandler {
        navController.popBackStack()
        activity?.finish()
    }

    sharedPref.getToken().toString().let {
        Constants.token = it
        Log.d("Token Key :- ", it)
    }

    if (subjectData.isEmpty()) {
        viewModel.getSubjectList()
    }

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()
    CustomDialog(
        isVisible = isDialogVisible, onDismiss = { isDialogVisible = false },
//        message = "Loading your data..."
        message = msgLoader
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }
    sharedPref.saveLogin(true)
    val userId = if (loginViewModel.getParentInfo() != null) {
        if (loginViewModel.getParentInfo()!!.isParent) {  // if login as parent & came to authentication
            loginViewModel.getUserId().toString()// studentId
        } else {
            loginViewModel.getUserId().toString() // parent id / current userID
        }
    } else {
        loginViewModel.getUserId().toString()
    }
    // response of dashboard data API

    LaunchedEffect(Unit) {
        // Dashboard API methods  ---
        viewModel.getStudentBanner()
        viewModel.getSubjectList()
        viewModel.getStudentProfile()
        viewModel.getStudentProgressCall()
        viewModel.getMonthlyReportDataData()
        viewModel.getReferalCode()

        viewModel.getKycAadhaarStatus(userId.toInt())  // check id status
        viewModel.getKycStatus(userId.toInt())  // check if aadhaar card is uploaded
        viewModel.getBadgeAwardDataReport() // APi to get Badge and Award Data

        viewModel.getStudentDashboardProgress.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        if (it.data.data != null) {
                            totalProgress = it.data.data.totalProgress
                            completeProfiler = it.data.data.completionFraction.toString()
                            kycProgress = it.data.data.kycProgress
                            profilerProgress = it.data.data.profilerProgress
                            quizProgress = it.data.data.quizProgress!!
                        } else {
                            totalProgress = 0
                            completeProfiler = "0/3"
                            kycProgress = 0
                            profilerProgress = 0
                            quizProgress = 0
                        }
                    }
                }

                is NetworkStatus.Error -> {
//                    context.toast(it.message)
                    println("Student Dashboard Progress Data Error :-${it.message}")
                }
            }
        }

        viewModel.getSubjectListResponseModel.observeForever {
            subjectData.clear()
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
//                        subjectData.addAll(it.data.data)
                        isDialogVisible = false
                        subjectData = it.data.data.toMutableList()
                        loginViewModel.saveStudentSubject(it.data.data)
                    }
                }

                is NetworkStatus.Error -> {
//                    context.toast(it.message)
                    println("Subject List Data Error :-${it.message}")
                }
            }
        }

        viewModel.monthlyReportResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        if (it.data.data != null) {
                            monthlyReport = it.data.data
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    println("Performing Reports Data Error :-${it.message}")
//                    context.toast(it.message)
                }
            }
        }

        viewModel.getReferalCodeResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        if (it.data.data != null) {
                            referalCode = it.data.data
                            println("Referal Data Error :-$referalCode")
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    println("Performing Reports Data Error :-${it.message}")
                }
            }
        }

        // API for KYC response
        viewModel.getKycAadhaarStatusLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {

                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        loginViewModel.saveKycStatusData(it.data.data)
                        kycStatus = it.data.data.studentKycStatus
                    }
                }

                is NetworkStatus.Error -> {
                    println("Kyc Aadhaar Status Live Data Error :-${it.message}")
                }
            }
        }

        viewModel.getKycStateLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {

                }

                is NetworkStatus.Success -> {
//                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        loginViewModel.saveKycDocUploadStatus(it.data.data)

                    }
                }

                is NetworkStatus.Error -> {
//                    isDialogVisible = false
                    println("Kyc State Live Data Error :-${it.message}")
                }
            }
        }

        viewModel.getStudentProfileResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        when (it.data.data.gender) {
                            "M", "Male" -> {
                                it.data.data.gender = "Male"
                            }

                            "F", "Female" -> {
                                it.data.data.gender = "Female"
                            }

                            else -> {
                                it.data.data.gender = "Others"
                            }
                        }
                        profileData = it.data.data
                        loginViewModel.saveStudentProfileData(profileData!!)
                        if ((it.data.data.name == "null" || it.data.data.name.isEmpty())
                            || (it.data.data.grade == 0)
                            || (it.data.data.schoolName == "null" || it.data.data.schoolName.isEmpty())
                            || (it.data.data.stateName == "null" || it.data.data.stateName.isEmpty())
                            || (it.data.data.districtName == "null" || it.data.data.districtName.isEmpty())
                        ) {
                            navController.navigate(AppRoute.StudentEditProfile.route)
                            Toast
                                .makeText(
                                    context,
                                    "Please Complete your profile!",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
                }

                is NetworkStatus.Error -> {
//                    context.toast(it.message)
                    isDialogVisible = false
                    println("Student Profile Data Error :-${it.message}")
                }
            }
        }

        viewModel.getBadgeAwardDataResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        if (it.data.data != null) {
                            badgeAwardReportData = it.data.data
                            println("Badge and Award Data :- $badgeAwardReportData")
                            if (badgeAwardReportData?.badge != null) {
                                badgeReport = badgeAwardReportData!!.badge
                                println("Badge Report Data :- $badgeReport")
                            } else {
                                println("Badge Report :- No badge yet")
                            }

                            if (badgeAwardReportData?.award != null) {
                                awardReport = badgeAwardReportData!!.award
                                println("Award Report Data :- $awardReport")
                            } else {
                                println("Award Report :- No award yet")
                            }
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    println("Awards and badges data error :- ${it.message}")
//                    context.toast(it.message)
                }
            }
        }

        viewModel.percentBannerListResponseModelLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    studentBannerListResponseModel.clear()
                    it.data?.data?.let { it1 -> studentBannerListResponseModel.addAll(it1) }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
            }
        }
    }

    var hasCameraPermission by remember { mutableStateOf(false) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
    }

    if (hasCameraPermission) {

    } else {
        try {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } catch (exc: Exception) {
            // Handle exception
        }
    }

    // Check permission and request if necessary
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            hasCameraPermission = true
        }
    }

    AuroscholarAppTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Unspecified)
                .padding(top = 70.dp, bottom = 80.dp, start = 5.dp, end = 5.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                        .padding(top = 10.dp)

                ) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                            .wrapContentSize(),
                        colors = CardColors(
                            containerColor = White,
                            contentColor = White,
                            disabledContentColor = White,
                            disabledContainerColor = White
                        ),
                        border = selectedBorder,
                    ) {
                        Image(
                            modifier = Modifier
                                .wrapContentSize()
                                .background(Color.Unspecified, shape = CircleShape)
                                .padding(8.dp)
                                .fillMaxWidth()
                                .height(135.dp),
                            contentScale = ContentScale.FillBounds,
                            painter = if (studentBannerListResponseModel.isNotEmpty()) {
                                if (studentBannerListResponseModel[0]!!.image != null) {
                                    rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(studentBannerListResponseModel[0]!!.image)
                                            .decoderFactory(SvgDecoder.Factory()) // Adds SVG support
                                            .size(Size.ORIGINAL) // Use original or specify size
                                            .placeholder(R.drawable.in_progress)
                                            .error(R.drawable.in_progress).build()
                                    )
                                } else {
                                    painterResource(R.drawable.in_progress)
                                }
                            } else {
                                painterResource(R.drawable.in_progress)
                            },
                            contentDescription = "Logo"
                        )
                    }

                    if (totalProgress != 100) {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            colors = CardColors(
                                containerColor = White,
                                contentColor = White,
                                disabledContentColor = White,
                                disabledContainerColor = White
                            ),
                            border = selectedBorder,
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                                    .clickable {
                                        try {
                                            if (totalProgress < 100) {
                                                if (profilerProgress < 40) {
                                                    println("Go to the profiler page and update your profiler.")
                                                    navController.navigate(AppRoute.StudentEditProfile.route)
                                                } else if (kycProgress < 40) {
                                                    context.startActivity(Intent(
                                                        context,
                                                        StudentAuthenticationActivity::class.java
                                                    ).apply {
                                                        putExtra(isKYC, isKYC)
                                                    })
                                                } else if (quizProgress < 20) {
                                                    navController.navigate(AppRoute.StudentQuizzes.route)
//                                                    navController.navigate(AppRoute.StudentAssessmentConcept.route)
                                                }
                                            } else {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        languageData[LanguageTranslationsResponse.KEY_PROFILE_PROGRESS_COMPLETE].toString(),
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            }
                                        } catch (exp: Exception) {
                                            exp.message
                                            println("Progress profile error :- ${exp.message}")
                                        }
                                    }, verticalAlignment = Alignment.CenterVertically
                            ) {
                                CustomProgressBar(
                                    percentage = totalProgress.toFloat(), // Update this to reflect your progress
                                    modifier = Modifier.size(50.dp)
                                )

                                Column(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(horizontal = 15.dp, vertical = 15.dp)
                                ) {
                                    Text(
                                        buildAnnotatedString {
                                            withStyle(style = SpanStyle(color = Color.Black)) {
                                                append(profileProgress)
                                            }
                                            withStyle(style = SpanStyle(color = PrimaryBlue)) {
                                                append(" ($completeProfiler)")
                                            }
                                        }, fontFamily = FontFamily(
                                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                        ), textAlign = TextAlign.Start
                                    )

                                    Text(
                                        text = if (completeProfiler == "0/3") {
                                            completeYourProfile
                                        } else if (completeProfiler == "1/3") {
                                            "Complete your Authentication process"
                                        } else if (completeProfiler == "2/3") {
                                            "Play Quiz for Updating 20% of your profile"
                                        } else {
                                            "Complete Profiler for 100% profile Update"
                                        }, textAlign = TextAlign.Start, fontFamily = FontFamily(
                                            Font(R.font.inter_regular, FontWeight.Normal)
                                        ), fontSize = 12.sp, color = Gray
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Image(painter = painterResource(R.drawable.ic_right_side),
                                    contentDescription = stringResource(id = R.string.icon_description),
                                    modifier = Modifier
                                        .size(25.dp)
                                        .padding(start = 4.dp)
                                        .background(Color.Unspecified)
                                        .clickable {
                                            try {
                                                if (totalProgress < 100) {
                                                    if (kycProgress < 40) {
                                                        context.startActivity(Intent(
                                                            context,
                                                            StudentAuthenticationActivity::class.java
                                                        ).apply {
                                                            putExtra(isKYC, isKYC)
                                                        })
                                                    } else if (quizProgress < 20) {
                                                        navController.navigate(AppRoute.StudentAssessmentConcept.route)
                                                    } else if (profilerProgress < 40) {
                                                        println("Go to the profiler page and update your profiler.")
                                                        navController.navigate(AppRoute.StudentEditProfile.route)
                                                    }
                                                } else {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            languageData[LanguageTranslationsResponse.KEY_PROFILE_PROGRESS_COMPLETE].toString(),
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                }
                                            } catch (exp: Exception) {
                                                exp.message
                                                println("Progress profile error :- ${exp.message}")
                                            }
                                        })
                            }
                        }
                    } else {
                        println("Profile progress already completed...")
                    }

                    TextWithIconQuizExample(textQuiz, icon, color, size, onClick = {
                        isShowWalkthrough.value = true
                    })
                    Text(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .wrapContentSize()
                            .wrapContentHeight(), text = textPlayQuiz, fontFamily = FontFamily(
                            Font(R.font.inter_medium, FontWeight.Medium)
                        ), fontSize = 12.sp, color = GrayLight01
                    )
                }
            }
            var subjectListSize = 0
            for (s in subjectData) {
                if (s.isSelected) {
                    subjectListSize += 1
                }
            }
            if (subjectData.size != 0) {
                items(subjectData.size) { index ->
                    ItemSubjectCard(context,
                        kycStatus,
                        subjectListSize,
                        loginViewModel,
                        languageData,
                        navController,
                        selectedBorder,
                        index,
                        subject = subjectData,
                        onItemClicked = {

                        })
                }
            } else {
                Log.d("subjectData", " else: $subjectData")
                viewModel.getSubjectList()
            }

            item {
                LeaderboardUI(
                    selectedBorder,
                    monthlyReport,
                    referalCode,
                    totalProgress,
                    badgeReport,
                    awardReport,
                    languageData
                )
            }

//            item {
//                BottomSheetKYCDialogNotice(isBottomSheetVisible = isBottomSheetVisible,
//                    sheetState = sheetState,
//                    onDismiss = {
//                        scope.launch { sheetState.hide() }.invokeOnCompletion {
//                            isBottomSheetVisible = false
//                        }
//                    })
//            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardUI(
    selectedBorder: BorderStroke,
    monthlyReport: MonthlyReport?,
    referalCode: ReferalCodeResponse.Data?,
    totalProgress: Int,
    badgeReport: BadgeData?,
    awardReport: AwardData?,
    languageData: HashMap<String, String>,
) {

    val context = LocalContext.current
    var showSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })

    BottomSheetReferAFriendScreen(referalCode,
        isBottomSheetVisible = isBottomSheetVisible,
        sheetState = sheetState,
        onDismiss = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                isBottomSheetVisible = false
            }
        })

//    if (showSheet) {
//        ReferBottomSheet(referalCode) {
//            showSheet = false
//        }
//    }

//    var scoreText = stringResource(id = R.string.txt_score)
    var scoreText = if (languageData[LanguageTranslationsResponse.KEY_SCORE].toString() == "") {
        stringResource(id = R.string.txt_score)
    } else {
        languageData[LanguageTranslationsResponse.KEY_SCORE].toString()
    }
    var twxtMoreDetails = stringResource(id = R.string.txt_more_details)
    var rankText = stringResource(id = R.string.txt_rank)
    var monthlyScore: String = ""
    if (monthlyReport != null) {
        val num = monthlyReport.monthScore
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        monthlyScore = df.format(num)
    } else {
        monthlyScore = "0"
    }
//    println("Number format data :- $floatValue")

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(top = 5.dp)
    ) {
        Text(
            modifier = Modifier.padding(start = 5.dp, top = 10.dp),
            text = if (languageData[LanguageTranslationsResponse.LDRBOARD].toString() == "") {
                stringResource(id = R.string.txt_leaderboard)
            } else {
                languageData[LanguageTranslationsResponse.LDRBOARD].toString()
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Black, fontSize = 16.sp, fontFamily = FontFamily(
                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                ), textAlign = TextAlign.Start
            )
        )

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp, top = 10.dp)
                .wrapContentHeight(),
            colors = CardColors(
                containerColor = White,
                contentColor = White,
                disabledContentColor = White,
                disabledContainerColor = White
            ),
            border = selectedBorder,
        ) {
            Column(
                modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.padding(horizontal = 5.dp)) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 10.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .weight(1f),
                        colors = CardColors(
                            containerColor = White,
                            contentColor = White,
                            disabledContentColor = White,
                            disabledContainerColor = White
                        ),
                        border = selectedBorder,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.score_icon),
                                contentDescription = "logo",
                                modifier = Modifier
                                    .size(36.dp) // Add size modifier to make the image visible
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(color = Transparent)// Add clip modifier to make the image circular
                                    .border( // Add border modifier to make image stand out
                                        width = 1.dp, color = LightPink02, shape = CircleShape
                                    )
                            )
                            Column(
                                modifier = Modifier.padding(start = 5.dp),
                            ) {
                                Text(
                                    text = monthlyScore.toString(),
                                    textAlign = TextAlign.Start,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_bold, FontWeight.Bold)
                                    ),
                                    color = Black,
                                    modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                                )
                                Text(
                                    scoreText,
                                    textAlign = TextAlign.Start,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_medium, FontWeight.Medium)
                                    ),
                                    color = Gray,
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(bottom = 10.dp, start = 10.dp)
                                )
                            }
                        }
                    }
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 10.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .weight(1f),
                        colors = CardColors(
                            containerColor = White,
                            contentColor = White,
                            disabledContentColor = White,
                            disabledContainerColor = White
                        ),
                        border = selectedBorder,
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.rank_icon),
                                contentDescription = "logo",
                                modifier = Modifier
                                    .size(36.dp) // Add size modifier to make the image visible
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(color = Transparent)// Add clip modifier to make the image circular
                                    .border( // Add border modifier to make image stand out
                                        width = 1.dp, color = LightPink02, shape = CircleShape
                                    )
                            )
                            Column(
                                modifier = Modifier.padding(start = 5.dp),
                            ) {
                                Text(
                                    text = if (monthlyReport != null) {
                                        "#${monthlyReport!!.rank.toString()}"
                                    } else {
                                        "#0"
                                    },
                                    textAlign = TextAlign.Start,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_bold, FontWeight.Bold)
                                    ),
                                    color = Black,
                                    modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                                )
                                Text(
                                    rankText,
                                    textAlign = TextAlign.Start,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_medium, FontWeight.Medium)
                                    ),
                                    color = Gray,
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(bottom = 10.dp, start = 10.dp)
                                )
                            }
                        }
                    }
                }
                val icon = painterResource(id = R.drawable.ic_right_side)
                val color = PrimaryBlue
                val size = 14.sp
                val onClick = {
                    val intent = Intent(context, StudentLeaderboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.also {
                        it.putExtra("", true)
                    }
                    context.startActivity(intent)
                }
                TextWithIconExample(twxtMoreDetails, icon, color, size, onClick)
            }
        }

        if (totalProgress == 100) {
            AwardsAndBadgesUI(selectedBorder, badgeReport, awardReport, languageData)
        } else {
            println("Profile progress not completed yet.")
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(horizontal = 5.dp, vertical = 10.dp)
                .fillMaxWidth(),
            colors = CardColors(
                containerColor = PrimaryBlue,
                contentColor = PrimaryBlue,
                disabledContentColor = PrimaryBlue,
                disabledContainerColor = PrimaryBlue
            ),
            border = selectedBorder,
        ) {
            Row(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .weight(1f),
                ) {
                    Text(
                        text = stringResource(id = R.string.txt_refer_friend),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = White, fontSize = 14.sp, fontFamily = FontFamily(
                                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                            ), textAlign = TextAlign.Start
                        )
                    )
                    Text(
                        modifier = Modifier.padding(end = 10.dp),
                        text = stringResource(id = R.string.txt_refer_desc),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = White, fontSize = 10.sp, fontFamily = FontFamily(
                                Font(R.font.inter_regular, FontWeight.Normal)
                            ), textAlign = TextAlign.Start
                        )
                    )
                    Row(modifier = Modifier.padding(top = 10.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.social_icon), // Replace with your drawable resource
                            contentDescription = null, // Provide a description for accessibility purposes
                            modifier = Modifier.size(24.dp), tint = White
                        )
                        Spacer(modifier = Modifier.width(8.dp)) // Add space between icon and text
                        Text(
                            modifier = Modifier.padding(end = 4.dp),
                            text = stringResource(id = R.string.txt_friend_referred),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = White, fontSize = 12.sp, fontFamily = FontFamily(
                                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                ), textAlign = TextAlign.Start
                            )
                        )
                    }
                    Button(
                        onClick = {
//                            showSheet = true
                            scope.launch {
                                isBottomSheetVisible = true
                                sheetState.expand()
                            }
                        }, modifier = Modifier
                            .wrapContentSize()
                            .padding(top = 5.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 5.dp,
                                    topEnd = 5.dp,
                                    bottomStart = 5.dp,
                                    bottomEnd = 5.dp
                                )
                            ), colors = ButtonDefaults.buttonColors(
                            containerColor = White, contentColor = PrimaryBlue
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_refer_now),
                            fontFamily = FontFamily(
                                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                            ),
                            fontSize = 14.sp
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.refer_friend_icon),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .wrapContentHeight()
                        .background(Color.Unspecified),
                    alignment = Alignment.TopEnd
                )
            }
        }
    }
}

@Composable
fun AwardsAndBadgesUI(
    selectedBorder: BorderStroke,
    badgeReport: BadgeData?,
    awardReport: AwardData?,
    languageData: HashMap<String, String>,
) {

    val context = LocalContext.current
    var viewAll = stringResource(id = R.string.txt_view_all)

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(top = 5.dp)
    ) {
        Text(
            modifier = Modifier.padding(start = 5.dp, top = 10.dp),
            text = stringResource(id = R.string.txt_badges_awards),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Black, fontSize = 16.sp, fontFamily = FontFamily(
                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                ), textAlign = TextAlign.Start
            )
        )

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp, top = 10.dp)
                .wrapContentHeight(),
            colors = CardColors(
                containerColor = White,
                contentColor = White,
                disabledContentColor = White,
                disabledContainerColor = White
            ),
            border = selectedBorder,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        colors = CardColors(
                            containerColor = White,
                            contentColor = White,
                            disabledContentColor = White,
                            disabledContainerColor = White
                        ),
                        border = selectedBorder,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = if (badgeReport != null) {
                                    rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(badgeReport!!.imageUrl.toString())
                                            .decoderFactory(SvgDecoder.Factory()) // Adds SVG support
                                            .size(Size.ORIGINAL) // Use original or specify size
                                            .placeholder(R.drawable.no_badges)
                                            .error(R.drawable.no_badges).build()
                                    )
//                                    rememberImagePainter(badgeReport!!.imageUrl.toString())
                                } else {
                                    painterResource(R.drawable.no_badges)
                                },
                                contentDescription = "logo",
                                alignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp) // Add size modifier to make the image visible
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(color = Transparent)// Add clip modifier to make the image circular
                                    .border( // Add border modifier to make image stand out
                                        width = 1.dp, color = LightPink02, shape = CircleShape
                                    )
                            )
                            Text(
                                text = if (badgeReport != null) {
                                    badgeReport!!.name.toString()
                                } else {
                                    if (languageData[LanguageTranslationsResponse.NULL_BADGE].toString() == "") {
                                        "No Badge"
                                    } else {
                                        languageData[LanguageTranslationsResponse.NULL_BADGE].toString()
                                    }
                                },
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontFamily = FontFamily(
                                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                ),
                                color = Black,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        colors = CardColors(
                            containerColor = White,
                            contentColor = White,
                            disabledContentColor = White,
                            disabledContainerColor = White
                        ),
                        border = selectedBorder,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = if (awardReport != null) {
                                    rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(awardReport!!.imageUrl.toString())
                                            .decoderFactory(SvgDecoder.Factory()) // Adds SVG support
                                            .size(Size.ORIGINAL) // Use original or specify size
                                            .placeholder(R.drawable.no_badges)
                                            .error(R.drawable.no_badges).build()
                                    )
//                                    rememberImagePainter(awardReport!!.imageUrl.toString())
                                } else {
                                    painterResource(R.drawable.no_badges)
                                },
                                contentDescription = "logo",
                                alignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp) // Add size modifier to make the image visible
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(color = Transparent)// Add clip modifier to make the image circular
                                    .border( // Add border modifier to make image stand out
                                        width = 1.dp, color = LightPink02, shape = CircleShape
                                    )
                            )
                            Text(
                                text = if (awardReport != null) {
                                    awardReport!!.name.toString()
                                } else {
                                    "No Award"
                                },
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontFamily = FontFamily(
                                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                ),
                                color = Black,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                val icon = painterResource(id = R.drawable.ic_right_side)
                val color = PrimaryBlue
                val size = 14.sp
                val onClick = {
                    if (badgeReport != null) {
                        val intent = Intent(context, BadgesAndAwardsActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.also {
                            it.putExtra("", true)
                        }
                        context.startActivity(intent)
                    } else {
                        val intent = Intent(context, StudentUnlockAwardsActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.also {
                            it.putExtra("", true)
                        }
                        context.startActivity(intent)
                    }
                }
                TextWithIconExample(
                    if (badgeReport != null) {
                        stringResource(id = R.string.txt_view_all)
                    } else {
                        "How to Unlock"
                    }, icon, color, size, onClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSubjectCard(
    context: Context,
    kycStatus: String,
    subjectListSize: Int,
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    navController: NavHostController,
    selectedBorder: BorderStroke,
    index: Int,
    subject: MutableList<GetSubjectListResponseModel.Data>,
    onItemClicked: (Boolean) -> Unit,
) {
    val subjectIndex = subject[index]

    val backGroundColor = White

    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })
    var isSubjectSelected by rememberSaveable { mutableStateOf(false) }

    if (subjectIndex.isSelected) {
        Card(shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp, top = 5.dp)
                .fillMaxWidth(),
            colors = CardColors(
                containerColor = White,
                contentColor = White,
                disabledContentColor = White,
                disabledContainerColor = White
            ),
            border = selectedBorder,
            onClick = {
                if (viewModel.getChildCount() > 1) {
                    if (kycStatus.contains("Approve")) {
                        onItemClicked(true)
                        viewModel.saveStudentSelectedSubjectData(subjectIndex)
                        if (subjectIndex.conceptSelected > 0) {
                            navController.navigate(AppRoute.StudentQuizList.route)
                        } else {
                            navController.navigate(AppRoute.StudentAssessmentConcept.route)
                        }
                    } else {
                        scope.launch {
                            isBottomSheetVisible = true
                            sheetState.expand()
                        }
//                    context.toast(
//                        "KYC is not Approved!"
//                    )
                    }
                } else {
                    onItemClicked(true)
                    viewModel.saveStudentSelectedSubjectData(subjectIndex)
                    if (subjectIndex.conceptSelected > 0) {
                        navController.navigate(AppRoute.StudentQuizList.route)
                    } else {
                        navController.navigate(AppRoute.StudentAssessmentConcept.route)
                    }
                }
            })
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 10.dp)
                    .background(color = backGroundColor),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier
                        .width(45.dp)
                        .background(Color.Unspecified)
                        .height(45.dp),
                    painter = if (subjectIndex.icons.isNotEmpty()) {
                        rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(subjectIndex.icons)
                                .decoderFactory(SvgDecoder.Factory()) // Adds SVG support
                                .size(Size.ORIGINAL) // Use original or specify size
                                .placeholder(R.drawable.english_icon).error(R.drawable.english_icon)
                                .build()
                        )
                    } else {
                        painterResource(R.drawable.english_icon)
                    },
                    contentDescription = "Logo"
                )

                Column(
                    modifier = Modifier.padding(start = 5.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        subjectIndex.subjectName,
                        textAlign = TextAlign.Start,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                        ),
                        color = Black,
                        modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                    )

                    Text(
                        subjectIndex.conceptSelected.toString() + if (languageData[LanguageTranslationsResponse.MONTHLY_CONCEPT_SELECTED].toString() == "") {
                            "/4 Monthly concept selected"
                        } else {
                            "/4 " + languageData[LanguageTranslationsResponse.MONTHLY_CONCEPT_SELECTED].toString()
                        },
                        textAlign = TextAlign.Start,
                        fontSize = 12.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
                        ),
                        color = Gray,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(bottom = 10.dp, start = 10.dp)
//                            .heightIn(min = 40.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(R.drawable.ic_right_side),
                    contentDescription = stringResource(id = R.string.icon_description),
                    modifier = Modifier
                        .wrapContentSize()
                        .background(Color.Unspecified)
                )
            }

            BottomSheetKYCDialogNotice(
                isBottomSheetVisible = isBottomSheetVisible,
                sheetState = sheetState,
                onDismiss = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        isBottomSheetVisible = false
                    }
                })
        }
    } else {
        if (subjectListSize < 5) {
            if (viewModel.getGrade() == "11" || viewModel.getGrade() == "12") {
//              navController.popBackStack()
                if (!isSubjectSelected) {
                    isSubjectSelected = true
                    navController.navigate(AppRoute.SelectSubjectPreference())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetKYCDialogNotice(
    isBottomSheetVisible: Boolean, sheetState: SheetState, onDismiss: () -> Unit,
) {

    val context: Context = LocalContext.current
    val viewModel: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    if (isBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = null,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            windowInsets = WindowInsets.ime
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(vertical = 15.dp, horizontal = 15.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_kyc_img), // Replace with your lock icon drawable
                        contentDescription = "KYC Lock",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Apologies! KYC is mandatory to withdraw Scholarship",
                        fontFamily = FontFamily(
                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                        ),
                        fontSize = 16.sp,
                        color = Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "You must complete KYC to withdraw Scholarship. If your KYC is not verified within 3 months of quiz, the quiz will be disapproved and the scholarship amount will be returned.",
                        fontSize = 14.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
                        ),
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                    )

                    BtnUi(
                        onClick = {
                            onDismiss.invoke()
                            context.startActivity(Intent(
                                context, StudentAuthenticationActivity::class.java
                            ).apply {
                                putExtra(isKYC, isKYC)
                            })
                        },
                        title = "Continue to KYC Update",
                        enabled = true,
                        cornerRadius = 10.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetReferAFriendScreen(
// ReferAFriendScreen
    referalCode: ReferalCodeResponse.Data?,
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit,
) {

    val context: Context = LocalContext.current
    val viewModel: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    val clipboardManager = LocalClipboardManager.current

    if (isBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = null,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            windowInsets = WindowInsets.ime
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(vertical = 15.dp, horizontal = 15.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Black, fontSize = 18.sp, fontFamily = FontFamily(
                                    Font(R.font.inter_bold, FontWeight.Bold)
                                ), textAlign = TextAlign.Start
                            )
                        )

                        Image(
                            painter = painterResource(R.drawable.line),
                            contentDescription = "logo",
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(top = 8.dp, start = 16.dp)
                                .clickable {
                                    onDismiss.invoke()
                                }
                                .clip(RoundedCornerShape(100.dp))
                                .border( // Add border modifier to make image stand out
                                    width = 2.dp, color = Color.Black, shape = CircleShape
                                )
                        )

                        Image(
                            painter = painterResource(R.drawable.close_icon),
                            contentDescription = "logo",
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(top = 8.dp)
                                .clickable {
                                    onDismiss.invoke()
                                }
                                .clip(RoundedCornerShape(100.dp))
                                .border( // Add border modifier to make image stand out
                                    width = 2.dp, color = Color.Black, shape = CircleShape
                                )
                        )
                    }

                    Text(
                        text = stringResource(id = R.string.txt_refer_friend),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Black, fontSize = 18.sp, fontFamily = FontFamily(
                                Font(R.font.inter_bold, FontWeight.Bold)
                            ), textAlign = TextAlign.Start
                        )
                    )

                    Text(
                        text = stringResource(id = R.string.txt_refer_copy_code),
                        color = Gray,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
                        ),
                        textAlign = TextAlign.Start
                    )

                    Image(
                        painter = painterResource(R.drawable.circlular_refer_friend),
                        contentDescription = "logo",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(top = 16.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(color = Transparent)// Add clip modifier to make the image circular
                            .border( // Add border modifier to make image stand out
                                width = 1.dp, color = LightPink02, shape = CircleShape
                            )
                    )

                    Text(
                        text = stringResource(id = R.string.txt_code_to_invite),
                        color = Gray,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(65.dp)
                        ) {
                            val width = size.width
                            val height = size.height

                            drawRect(
                                color = GrayLight02,
                                size = androidx.compose.ui.geometry.Size(width * 0.8f, height)
                            )

                            drawRect(
                                color = PrimaryBlue,
                                topLeft = androidx.compose.ui.geometry.Offset(width * 0.8f, 0f),
                                size = androidx.compose.ui.geometry.Size(width * 0.2f, height)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp, horizontal = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (referalCode != null) {
                                    referalCode.shortLink.toString()
                                } else {
                                    ""
                                }, modifier = Modifier.padding(10.dp), style = TextStyle(
                                    textAlign = TextAlign.Start, // Center align the text
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_medium, FontWeight.Medium)
                                    ),
                                    fontSize = 9.sp,
                                    color = Black,
                                )
                            )
                            Text(
                                text = "Copy", modifier = Modifier
                                    .padding(10.dp)
                                    .clickable {
                                        val strCode: String = referalCode?.shortLink ?: ""
                                        clipboardManager.setText(AnnotatedString(strCode))
                                        Toast
                                            .makeText(context, "Code Copied!!", Toast.LENGTH_SHORT)
                                            .show()
                                    }, style = TextStyle(
                                    textAlign = TextAlign.End, // End align the text
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = White,
                                )
                            )
                        }
                    }

                    Text(
                        text = "or Share link via", fontFamily = FontFamily(
                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                        ), fontSize = 14.sp, modifier = Modifier.padding(top = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                    ) {
                        IconButton(onClick = {
                            val strCode: String = referalCode?.shortLink ?: ""
                            val appPackageName = context.packageName
                            val appLink =
                                "https://play.google.com/store/apps/details?id=$appPackageName"

                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Hey, check out this awesome app:- $appLink \n\n" + "Your referal code:- $strCode"
                                )
                                setPackage("com.whatsapp") // Ensures it opens in WhatsApp
                            }

                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(appLink))
                                context.startActivity(fallbackIntent)
                            }
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.whatsapp_icon),
                                contentDescription = "WhatsApp",
                                modifier = Modifier.size(75.dp)
                            )
                        }

                        IconButton(onClick = {
                            val strCode: String = referalCode?.shortLink ?: ""
                            val appPackageName = context.packageName
                            val appLink =
                                "https://play.google.com/store/apps/details?id=$appPackageName"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Hey, check out this awesome app:- $appLink \n\n" + "Your referal code:- $strCode"
                                )
                                `package` = "com.instagram.android"
                            }
                            val resolveInfo = context.packageManager.resolveActivity(intent, 0)
                            if (resolveInfo != null) {
                                startActivity(context, intent, null)
                            } else {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(appLink))
                                startActivity(context, browserIntent, null)
                            }
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.instagram_icon),
                                contentDescription = "Instagram",
                                modifier = Modifier.size(75.dp)
                            )
                        }

                        IconButton(onClick = {
                            val strCode: String = referalCode?.shortLink ?: ""
                            val appPackageName = context.packageName
                            val appLink =
                                "https://play.google.com/store/apps/details?id=$appPackageName"
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Hey, check out this awesome app:- $appLink \n\n" + "Your referal code:- $strCode"
                                )
                                `package` = "com.facebook.katana"
                            }
                            if (shareIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(shareIntent)
                            } else {
                                val browserIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.facebook.com/sharer/sharer.php?u=$appLink")
                                )
                                context.startActivity(browserIntent)
                            }
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.facebook_icon),
                                contentDescription = "Facebook",
                                modifier = Modifier.size(75.dp)
                            )
                        }

                        IconButton(onClick = {
                            val strCode: String = referalCode?.shortLink ?: ""
                            val appPackageName = context.packageName
                            val appLink =
                                "https://play.google.com/store/apps/details?id=$appPackageName"

                            val linkedinIntent = Intent(Intent.ACTION_SEND)
                            linkedinIntent.setType("text/plain")
                            linkedinIntent.putExtra(
                                Intent.EXTRA_TEXT,
                                "Hey, check out this awesome app:- $appLink \n\n" + "Your referal code:- $strCode"
                            )

                            var linkedinAppFound = false
                            val matches2: List<ResolveInfo> =
                                context.packageManager.queryIntentActivities(linkedinIntent, 0)

                            for (info in matches2) {
                                if (info.activityInfo.packageName.lowercase(Locale.getDefault())
                                        .startsWith(
                                            "com.linkedin"
                                        )
                                ) {
                                    linkedinIntent.setPackage(info.activityInfo.packageName)
                                    linkedinAppFound = true
                                    break
                                }
                            }

                            if (linkedinAppFound) {
                                context.startActivity(linkedinIntent);
                            } else {
                                context.toast("LinkedIn app not Installed in your mobile")
                            }
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.linkedin_icon),
                                contentDescription = "YouTube",
                                modifier = Modifier.size(75.dp)
                            )
                        }

                        IconButton(onClick = {
                            try {
                                val strCode: String = referalCode?.shortLink ?: ""
                                val appPackageName = context.packageName
                                val appLink =
                                    "https://play.google.com/store/apps/details?id=$appPackageName"
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Hey, check out this awesome app:- $appLink \n\n" + "Your referal code:- $strCode"
                                    )
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share via")
                                context.startActivity(shareIntent)
                            } catch (exp: Exception) {
                                exp.message
                                println("Refer now find error is :- ${exp.message}")
                            }
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.more_share_icon),
                                contentDescription = "Twitter",
                                modifier = Modifier.size(75.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
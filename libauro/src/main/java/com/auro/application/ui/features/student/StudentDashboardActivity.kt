package com.auro.application.ui.features.student

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.auro.application.R
import com.auro.application.core.ConstantVariables.isRegistration
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.data.utlis.CommonFunction.getGenderIconState
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.ChildListResponse
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.assessment.screens.AssessmentResultScreen
import com.auro.application.ui.features.student.assessment.screens.QuizDisclaimerScreen
import com.auro.application.ui.features.student.assessment.screens.QuizInstructionsScreen
import com.auro.application.ui.features.student.assessment.screens.QuizListScreen
import com.auro.application.ui.features.student.assessment.screens.QuizQuestionScreen
import com.auro.application.ui.features.student.assessment.screens.SelectAssessmentConceptList
import com.auro.application.ui.features.student.authentication.StudentAuthenticationActivity
import com.auro.application.ui.features.student.menu.AppBar
import com.auro.application.ui.features.student.menu.BottomNavigationBar
import com.auro.application.ui.features.student.menu.DrawerBody
import com.auro.application.ui.features.student.menu.DrawerHeader
import com.auro.application.ui.features.student.menu.MenuItem
import com.auro.application.ui.features.student.partner.screens.PartnerDashboardScreen
import com.auro.application.ui.features.student.partner.screens.PartnerDetailsViewScreen
import com.auro.application.ui.features.student.passport.screens.StudentPassportHomeScreen
import com.auro.application.ui.features.student.practice.screens.PracticeConceptListScreen
import com.auro.application.ui.features.student.practice.screens.PracticeResultScreen
import com.auro.application.ui.features.student.practice.screens.StudentPracticeDashboardScreen
import com.auro.application.ui.features.student.screens.AddStudentPinScreen
import com.auro.application.ui.features.student.screens.StudentFaqActivity
import com.auro.application.ui.features.student.screens.StudentHomePage
import com.auro.application.ui.features.student.screens.StudentProfileScreen
import com.auro.application.ui.features.student.screens.StudentQuizzesScreen
import com.auro.application.ui.features.student.screens.StudentSubjectPreferenceScreen
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.features.student.wallet.screens.StudentWalletDisclaimer
import com.auro.application.ui.features.student.wallet.screens.StudentWalletScreen
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.FullTransparent
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightRed01
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StudentDashboardActivity : ComponentActivity() {
    @Inject
    lateinit var sharedPref: SharedPref

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint(
        "UnusedMaterial3ScaffoldPaddingParameter",
        "RememberReturnType",
        "CoroutineCreationDuringComposition"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           
            AuroscholarAppTheme {
                val navController = rememberNavController()
                val viewModel: LoginViewModel = hiltViewModel()
                val studentViewModel: StudentViewModel = hiltViewModel()
                // observe the backstack
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination?.route
                var kycStatus by remember { mutableStateOf("") }

                val languageListName = stringResource(id = R.string.key_lang_list)
                var languageData = HashMap<String, String>()
                languageData = viewModel.getLanguageTranslationData(languageListName)

                val msgLoader =
                    languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val bottomSheetState = rememberStandardBottomSheetState(
                    skipHiddenState = false  // Allow transitioning to hidden state
                )
                val bottomSheetScaffoldState =
                    rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
                val coroutineScope = rememberCoroutineScope()

                val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                val halfScreenHeight = screenHeight / 2

                var sheetPeekHeight by remember { mutableStateOf(0.dp) }
                var isDialogVisible by remember { mutableStateOf(false) }

                val userId = if (viewModel.getParentInfo() != null) {
                    if (viewModel.getParentInfo()!!.isParent) {  // if login as parent & came to authentication
                        viewModel.getUserId().toString()// studentId
                    } else {
                        viewModel.getUserId().toString() // parent id / current userID
                    }
                } else {
                    viewModel.getUserId().toString()
                }

                CustomDialog(
                    isVisible = isDialogVisible, onDismiss = { isDialogVisible = false },
//                    message = "Loading your data..."
                    message = msgLoader
                )

                studentViewModel.getStudentProfile()
                studentViewModel.getKycAadhaarStatus(userId.toInt())

                LaunchedEffect(Unit) {

                    studentViewModel.getStudentProfileResponse.observeForever {
                        when (it) {
                            is NetworkStatus.Idle -> {}
                            is NetworkStatus.Loading -> {
                                isDialogVisible = true
                            }

                            is NetworkStatus.Success -> {
                                //                                profilerData.clear()
                                isDialogVisible = false
                                if (it.data?.isSuccess == true) {
                                    viewModel.saveStudentProfileData(it.data.data)
                                    Log.d("saveStudentData:", "" + it.data.data)
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
                                    viewModel.saveKycStatusData(it.data.data)
                                    kycStatus = it.data.data.studentKycStatus
                                    println("Kyc Approved or not :- $kycStatus")
                                }
                            }

                            is NetworkStatus.Error -> {
                                println("Kyc Aadhaar Status Live Data Error :-${it.message}")
                            }
                        }
                    }
                }

                BottomSheetScaffold(
                    scaffoldState = bottomSheetScaffoldState, sheetContent = {
                        switchProfile(
                            navController,
                            viewModel,
                            languageData,
                            bottomSheetScaffoldState = bottomSheetScaffoldState,
                            coroutineScope = coroutineScope
                        ) {
                            sheetPeekHeight = it
                            scope.launch {
                                drawerState.close()
                            }
                        }

                    }, sheetPeekHeight = sheetPeekHeight
                ) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(end = 30.dp)
                                    .background(FullTransparent)
                            ) {
                                DrawerHeader(viewModel, drawerState, onItemClick = {
                                    navController.navigate(AppRoute.StudentProfile.route)
                                })
                                DrawerBody(
                                    // List of Navigation Drawer
                                    items = listOf(
                                        MenuItem(
                                            id = AppRoute.StudentAuth.route,
//                                            title = "Authentication",
                                            title = languageData[LanguageTranslationsResponse.AUTHENTICATION].toString(),
                                            contentDescription = "Go to auth",
                                            icon = ImageVector.vectorResource(id = R.drawable.ic_faq)
                                        ),
                                        MenuItem(
                                            id = AppRoute.StudentFAQ.route,
//                                            title = "FAQ",
                                            title = languageData[LanguageTranslationsResponse.FAQ].toString(),
                                            contentDescription = "Add new student",
                                            icon = ImageVector.vectorResource(id = R.drawable.add_person)
                                        ),
                                        MenuItem(
                                            id = AppRoute.StudentSwitchProfile.route,
                                            title = if (languageData[LanguageTranslationsResponse.KEY_SWITCH_PROFILE].toString() == "") {
                                                "Switch Profile"
                                            } else {
                                                languageData[LanguageTranslationsResponse.KEY_SWITCH_PROFILE].toString()
                                            },
                                            contentDescription = "Switch User Profile",
                                            icon = ImageVector.vectorResource(id = R.drawable.switch_user_icon)
                                        ),
                                    ), onItemClick = { itemId ->
                                        onMenuItemClick(
                                            viewModel,
                                            itemId,
                                            navController,
                                            this@StudentDashboardActivity,
                                            bottomSheetScaffoldState,
                                            coroutineScope,
                                            halfScreenHeight,
                                            sheetPeekHeight
                                        ) {
                                            sheetPeekHeight = it
                                            scope.launch {
                                                drawerState.close()
                                            }
                                        }
                                    })
                            }
                        },
                    ) {

                        Scaffold(bottomBar = {
                            if (currentDestination in listOf(
                                    AppRoute.StudentDashboard.route,
                                    AppRoute.StudentPassport.route,
                                    AppRoute.StudentWallet.route,
                                    AppRoute.StudentPractice.route,
                                    AppRoute.StudentPartner.route,
                                )
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .zIndex(1f)
                                        .shadow(elevation = 8.dp)
                                ) {
                                    BottomNavigationBar(
                                        navController = navController,
                                        currentDestination,
                                        kycStatus,
                                        languageData
                                    )
                                }
                            }
                        }, topBar = {
                            if (currentDestination == AppRoute.StudentDashboard.route) {
                                AppBar(
                                    viewModel, onNavigationIconClick = {
                                        scope.launch { drawerState.open() }
                                    }, scope, drawerState, currentDestination, navController
                                )
                            }

                        }, content = { innerPadding ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .zIndex(1f)
                            ) {
//                                    NavHostContainer(navController = navController)
                                // Main screen content with shared elements
                                NavHost(
                                    navController = navController,
                                    startDestination = AppRoute.StudentDashboard.route,
                                ) {
                                    composable(AppRoute.StudentDashboard.route) {
                                        StudentHomePage(
                                            navController,
                                            intent.getStringExtra(isRegistration),
                                            sharedPref
                                        )
                                    }
                                    composable(
                                        AppRoute.DetailsRoute(-1).route,
                                        arguments = listOf(navArgument("index") {
                                            type = NavType.IntType
                                        })
                                    ) { backStackEntry ->
                                        val index = backStackEntry.arguments?.getInt("index")!!/* Log.e("TAG", "rediving id " + index)
                                        UserDetailsScreen(index,navController,viewModel) {
                                            navController.navigateUp()*/
                                    }
                                    composable(AppRoute.StudentPassport.route) {
                                        StudentPassportHomeScreen(innerPadding,
                                            applicationContext,
                                            openDrawMenuItem = {
                                                scope.launch {
                                                    drawerState.open()
                                                }
                                            })
                                    }

                                    composable(AppRoute.StudentAssessmentConcept.route) {
                                        SelectAssessmentConceptList(navController, sharedPref)
                                    }
                                    composable(AppRoute.StudentQuizList.route) {
                                        QuizListScreen(navController)
                                    }

                                    composable(AppRoute.StudentQuizzes.route) {
                                        StudentQuizzesScreen(navController)
                                    }

                                    composable<AppRoute.SelectSubjectPreference>() {
                                        val args =
                                            it.toRoute<AppRoute.SelectSubjectPreference>().grade
                                        StudentSubjectPreferenceScreen(
                                            navController, args, viewModel, sharedPref
                                        )
                                    }
                                    composable(AppRoute.StudentQuizInstructions.route) {
                                        QuizInstructionsScreen(navController)
                                    }
                                    composable(AppRoute.QuizDisclaimerScreen.route) {
                                        QuizDisclaimerScreen(navController)
                                    }
                                    composable(AppRoute.QuizQuestionScreen.route) {
                                        QuizQuestionScreen(navController)
                                    }
                                    composable(AppRoute.QuizResultScreen.route) {
                                        AssessmentResultScreen(navController)
                                    }
                                    composable<AppRoute.PracticeConceptList>() {
                                        PracticeConceptListScreen(navController)
                                    }

                                   /* composable<AppRoute.PracticeQuestion>() {
                                        PracticeQuestionScreen(navController)
                                    }*/

                                    composable<AppRoute.PracticeResult>() {
                                        PracticeResultScreen(navController)
                                    }
                                    composable(AppRoute.StudentWalletDisclaimer.route) {
                                        StudentWalletDisclaimer(navController)
                                    }

                                    composable(AppRoute.StudentWallet.route) {
                                        StudentWalletScreen(navController, openMenu = {
                                            scope.launch { drawerState.open() }
                                        })
                                    }

                                    composable(AppRoute.StudentPractice.route) {
                                        StudentPracticeDashboardScreen(
                                            innerPadding,
                                            navController,
                                            openDrawMenuItem = {
                                                scope.launch {
                                                    drawerState.open()
                                                }
                                            })
                                    }

                                    composable(AppRoute.StudentPartner.route) {
                                        PartnerDashboardScreen(innerPadding, openDrawMenuItem = {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        })
                                    }

                                    composable(AppRoute.PartnerWebView.route) {
                                        PartnerDetailsViewScreen(navController, sharedPref)
                                    }

                                    composable(AppRoute.StudentProfile.route) {
                                        scope.launch {
                                            drawerState.close()
                                        }
                                        StudentProfileScreen(
                                            navController, this@StudentDashboardActivity
                                        ) { data ->
                                            navController.navigate("create_pin/$data")
                                        }
                                    }
                                    composable(AppRoute.StudentEditProfile.route) {
                                        StudentEditProfileActivity(navController)
                                    }

                                    composable<AppRoute.SwitchUserWithPin> {
                                        val args = it.toRoute<AppRoute.SwitchUserWithPin>()
                                        val isLoginWithOtp = args.isLoginWithPin
                                        val phoneNo = args.phoneNo
                                        val userId = args.userId
                                        AddStudentPinScreen(
                                            navController,
                                            this@StudentDashboardActivity,
                                            userId.toString()
                                        )
                                        coroutineScope.launch {
                                            scope.launch {
                                                drawerState.close()
                                                bottomSheetState.hide()
                                            }
                                        }
                                    }
                                }
                            }
                        })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun switchProfile(
    navController: NavHostController = rememberNavController(),
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope,
    updatePeekHeight: (Dp) -> Unit
) {
    var childList by remember { mutableStateOf(mutableListOf<ChildListResponse.Data.Student>()) }
    var listData by remember { mutableStateOf<ChildListResponse.Data.Parent?>(null) }
    LaunchedEffect(Unit) {
        viewModel.childListLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {

                    if (it.data?.isSuccess == true) {
                        listData = it.data.data.parent
                        viewModel.saveChildCount(it.data.data.student.size)
                        childList = it.data.data.student.toMutableList()
                    }
                }

                is NetworkStatus.Error -> {
//                    context.toast(it.message)
                }
            }
        }
        viewModel.getChildListApi()
    }

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp), // Add horizontal padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
//                text = "Select Account to Switch",
                text = languageData[LanguageTranslationsResponse.SELECT_ACCOUNT_TO_SWITCH].toString(),
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1f),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Gray,
                textAlign = TextAlign.Start
            )
            Image(painter = painterResource(R.drawable.ic_close),
                contentDescription = "dskjdhsgd",
                modifier = Modifier
                    .padding(end = 10.dp)
                    .clickable {
                        coroutineScope.launch {
                            updatePeekHeight(0.dp) // Reset peek height
                            bottomSheetScaffoldState.bottomSheetState.hide() // Collapse the bottom sheet

                        }
                    })
        }
        // Parent List
        Card(shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .clickable {

                }
                .padding(top = 10.dp)
                .wrapContentHeight()
                .fillMaxWidth(),
            border = BorderStroke(0.8.dp, GrayLight02),
            onClick = {
                navController.navigate(
                    AppRoute.SwitchUserWithPin(
                        "", "", listData!!.user_id
                    )
                )
            }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White) // Set background color based on selection
                    .padding(4.dp), // Add horizontal padding
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(10.dp)
                        .size(52.dp),
                    painter = painterResource(R.drawable.ic_parent),
                    contentDescription = "Logo"
                )

                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 10.dp), verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (listData != null) {
                            listData!!.name
                        } else {
                            if (languageData[LanguageTranslationsResponse.KEY_PARENT].toString() == "") {
                                "Parent"
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_PARENT].toString()
                            }
                        },
                        modifier = Modifier.wrapContentSize(),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Gray,
                        textAlign = TextAlign.Start
                    )

                    Text(
                        text = if (languageData[LanguageTranslationsResponse.KEY_PARENT].toString() == "") {
                            "Parent"
                        } else {
                            languageData[LanguageTranslationsResponse.KEY_PARENT].toString()
                        },
                        modifier = Modifier.wrapContentSize(),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = GrayLight01,
                        textAlign = TextAlign.Start
                    )
                }
                Image(
                    painter = painterResource(R.drawable.ic_right_side),
                    contentDescription = "sdsds",
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }


//Student List
    LazyColumn(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(childList) { child ->
            SwitchUserItem(viewModel, languageData, child, navController, Modifier.clickable {
                // Handle the click event here
                // For example, navigate to a new screen or perform an action
                navController.navigate(AppRoute.SwitchUserWithPin("", "", child.userId))
            })
        }
    }
}

// Function to handle item clicks
@OptIn(ExperimentalMaterial3Api::class)
fun onMenuItemClick(
    viewModel: LoginViewModel,
    itemId: String,
    navController: NavHostController,
    studentDashboardActivity: StudentDashboardActivity,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope,
    halfScreenHeight: Dp,
    currentPeekHeight: Dp,
    updatePeekHeight: (Dp) -> Unit
) {
    // Handle the click event here
    val context = studentDashboardActivity as Context
    when (itemId) {
        AppRoute.StudentAuth.route -> {
            Log.e("TAG", "Calling   " + "StudentAuthenticationActivity")
            startActivity(
                context,
                Intent(context, StudentAuthenticationActivity::class.java),
                null
            ).apply { (context as? Activity)?.finish() }
        }

        AppRoute.StudentFAQ.route -> {
            startActivity(
                context,
                Intent(context, StudentFaqActivity::class.java),
                null
            ).apply { (context as? Activity)?.finish() }
        }

        AppRoute.StudentSwitchProfile.route -> {
            // Navigate to StudentSwitchProfile screen
            coroutineScope.launch {
                viewModel.getChildListApi()
                Log.d(
                    "bottomSheet Value:",
                    "" + bottomSheetScaffoldState.bottomSheetState.currentValue
                )
                if (!bottomSheetScaffoldState.bottomSheetState.currentValue.equals(
                        bottomSheetScaffoldState.bottomSheetState.partialExpand()
                    )
                ) {
                    updatePeekHeight(halfScreenHeight)
                    bottomSheetScaffoldState.bottomSheetState.expand()
                } else {
                    updatePeekHeight(0.dp)
                    bottomSheetScaffoldState.bottomSheetState.hide()
//                    bottomSheetScaffoldState.bottomSheetState.
                }
            }
        }
    }
}

@Composable
fun SwitchUserItem(
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    child: ChildListResponse.Data.Student,
    navHostController: NavHostController,
    modifier: Modifier
) {
    if (child.userId != viewModel.getUserId()) {
        Card(shape = RoundedCornerShape(12.dp),
            modifier = modifier
                .clickable {

                }
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                .fillMaxWidth(),
            border = BorderStroke(0.8.dp, GrayLight02),
            onClick = {
                navHostController.navigate(
                    AppRoute.SwitchUserWithPin(
                        "", "", child.userId
                    )
                )
            }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White) // Set background color based on selection
                    .padding(4.dp), // Add horizontal padding
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border( // Add border modifier to make image stand out
                            width = 1.dp, color = GrayLight02, shape = CircleShape
                        )
                        .background(shape = CircleShape,color = White),
                    contentScale = ContentScale.Crop,
                    painter = if (child.imageUrl != null) {
                        rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current).data(child.imageUrl)
                                .placeholder(
                                    if (child.gender != null) {
                                        getGenderIconState(child.gender)
                                    } else {
                                        R.drawable.icon_male_student
                                    }
                                ).error(
                                    if (child.gender != null) {
                                        getGenderIconState(child.gender)
                                    } else {
                                        R.drawable.icon_male_student
                                    }
                                )

                                .crossfade(true) // Optional: Add a fade transition
                                .build()
                        )
                    } else {
                        painterResource(
                            if (child.gender != null) {
                                getGenderIconState(child.gender)
                            } else {
                                R.drawable.icon_male_student
                            }
                        )
                    },
                    contentDescription = "Logo"
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = child.name,
                        modifier = Modifier.wrapContentSize(),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Gray,
                        textAlign = TextAlign.Start
                    )

                    Text(
                        text = if (child.isActiveUser == 1) {
                            if (languageData[LanguageTranslationsResponse.STUDENTS].toString() == "") {
                                "Student"
                            } else {
                                languageData[LanguageTranslationsResponse.STUDENTS].toString()
                            }
                        } else if (child.isActiveUser == 3) {
                            "Blocked User"
                        }
                        else{
                            "Deleted User"
                        },
                        modifier = Modifier.wrapContentSize(),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (child.isActiveUser == 1) {
                            GrayLight01
                        } else {
                            LightRed01
                        },
                        textAlign = TextAlign.Start
                    )
                }
                Image(
                    painter = painterResource(R.drawable.ic_right_side),
                    contentDescription = "sdsds",
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    AuroscholarAppTheme {

    }
}

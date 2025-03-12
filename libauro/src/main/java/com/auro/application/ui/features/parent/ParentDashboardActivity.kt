package com.auro.application.ui.features.parent

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.auro.application.R
import com.auro.application.core.ConstantVariables
import com.auro.application.data.api.Constants
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.data.utlis.CommonFunction.getGenderIconState
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.LoginMainActivity
import com.auro.application.ui.features.login.componets.CommandAppKey
import com.auro.application.ui.features.login.models.ChildListResponse
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.screens.CreatePinScreen
import com.auro.application.ui.features.login.screens.EnterPinScreen
import com.auro.application.ui.features.login.screens.SelectSubjectScreen
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.screens.AuthenticationScreen
import com.auro.application.ui.features.parent.screens.ParentDashboardScreen
import com.auro.application.ui.features.parent.screens.ParentEditProfileActivity
import com.auro.application.ui.features.parent.screens.ParentProfileScreen
import com.auro.application.ui.features.parent.screens.ParentStudentScreen
import com.auro.application.ui.features.parent.screens.QuizPerformanceScreen
import com.auro.application.ui.features.parent.screens.QuizReportScreen
import com.auro.application.ui.features.parent.viewmodel.ParentViewModel
import com.auro.application.ui.features.student.menu.BottomNavigationItems
import com.auro.application.ui.features.student.menu.DrawerBody
import com.auro.application.ui.features.student.menu.DrawerHeader
import com.auro.application.ui.features.student.menu.MenuItem
import com.auro.application.ui.features.student.screens.AddStudentPinScreen
import com.auro.application.ui.features.student.screens.StudentFaqActivity
import com.auro.application.ui.features.student.screens.StudentSubjectPreferenceScreen
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.FullTransparent
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightRed01
import com.auro.application.ui.theme.PrimaryBlue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ParentDashboardActivity : ComponentActivity() {
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

                val parentViewModel: ParentViewModel = viewModel()
                val viewModels: LoginViewModel = viewModel()
                val viewModel: LoginViewModel = viewModel()
                val studentViewModel: StudentViewModel = viewModel()

                val languageListName = stringResource(id = R.string.key_lang_list)
                var languageData = HashMap<String, String>()
                languageData = viewModel.getLanguageTranslationData(languageListName)

                val navController = rememberNavController()
                // observe the backstack
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination?.route

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

                var title by remember { mutableStateOf("") }

                var isFilter by remember { mutableStateOf(false) }

                /*  LaunchedEffect(Unit) {

                      parentViewModel.getParentProfile()
                      parentViewModel.parentProfileResponseModel.observeForever {
                          when (it) {
                              is NetworkStatus.Idle -> {}
                              is NetworkStatus.Loading -> {
  //                                isDialogVisible = true
                              }

                              is NetworkStatus.Success -> {
  //                                isDialogVisible = false
                                  if (it.data?.isSuccess == true) {

                                      viewModels.saveParentProfileData(it.data.data)

                                  }
                              }

                              is NetworkStatus.Error -> {
  //                                isDialogVisible = false
                              }
                          }
                      }

                  }*/
                /* var userId by rememberSaveable { mutableStateOf("0") }
                 var kycStatus by rememberSaveable { mutableStateOf("0") }
                 userId = if (viewModel.getParentInfo()!!.isParent){  // if login as parent & came to authentication
                     viewModel.getStudentList().userId    // studentId
                 }else {
                     viewModel.getUserId().toString() // parent id
                 }
                 studentViewModel.getKycAadhaarStatus(userId.toInt())

 //                Log.e("TAG", "onCreate: data on parent  "+viewModel1.getParentInfo() )
                 LaunchedEffect(Unit) {
                     studentViewModel.getKycAadhaarStatus(userId.toInt())
                     studentViewModel.getKycAadhaarStatusLiveData.observeForever {
                         when (it) {
                             is NetworkStatus.Idle -> {}
                             is NetworkStatus.Loading -> {

                             }

                             is NetworkStatus.Success -> {
                                 if (it.data?.isSuccess == true) {
                                     kycStatus = it.data.data.studentKycStatus

                                 }
                             }

                             is NetworkStatus.Error -> {

                             }
                         }
                     }
                 }*/

                BottomSheetScaffold(
                    scaffoldState = bottomSheetScaffoldState,
                    sheetContent = {
                        switchProfile(
                            navController, viewModel,
                            bottomSheetScaffoldState = bottomSheetScaffoldState,
                            coroutineScope = coroutineScope
                        ) {
                            sheetPeekHeight = it
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    },
                    sheetPeekHeight = sheetPeekHeight
                ) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(FullTransparent)
                            ) {
                                DrawerHeader(viewModel, drawerState, onItemClick = {
                                    navController.navigate(AppRoute.ParentProfile.route)
                                })

                                println("Switch User Profile :- " + languageData[LanguageTranslationsResponse.KEY_SWITCH_PROFILE].toString())
                                DrawerBody(
                                    // List of Navigation Drawer
                                    items = listOf(
                                        MenuItem(
                                            id = AppRoute.StudentFAQ.route,
//                                            title = "FAQ",
                                            title = languageData[LanguageTranslationsResponse.FAQ].toString(),
                                            contentDescription = "Go to auth",
                                            icon = ImageVector.vectorResource(id = R.drawable.ic_faq)
                                        ),
                                        MenuItem(
                                            id = AppRoute.ParentAddStudent.route,
//                                            title = "Add student",
                                            title = languageData[LanguageTranslationsResponse.ADD_STUDENT].toString(),
                                            contentDescription = "Add student",
                                            icon = ImageVector.vectorResource(id = R.drawable.add_person)
                                        ),
                                        MenuItem(
                                            id = AppRoute.StudentSwitchProfile.route,
                                            title = if (languageData[LanguageTranslationsResponse.KEY_SWITCH_PROFILE].toString() == "") {
                                                "Switch Profile"
                                            } else {
                                                languageData[LanguageTranslationsResponse.KEY_SWITCH_PROFILE].toString()
                                            },
//                                            title = languageData[LanguageTranslationsResponse.KEY_SWITCH_PROFILE].toString(),
                                            contentDescription = "Switch User Profile",
                                            icon = ImageVector.vectorResource(id = R.drawable.switch_user_icon)
                                        ),
                                    ),
                                    onItemClick = { itemId ->
                                        onMenuItemClick(
                                            itemId,
                                            navController,
                                            parentViewModel,
                                            this@ParentDashboardActivity,
                                            bottomSheetScaffoldState,
                                            coroutineScope, halfScreenHeight, sheetPeekHeight
                                        ) {
                                            sheetPeekHeight = it
                                        }
                                    }
                                )
                            }
                        },
                    ) {

                        Scaffold(
                            bottomBar = {
                                // Define a set of routes that should show the bottom navigation bar
                                val showBottomBarDestinations = setOf(
                                    AppRoute.ParentDashboard.route,
                                    AppRoute.ParentAuthentication.route,
                                    AppRoute.ParentStudent.route
                                )

                                // Check if the current destination is in the set
                                if (currentDestination in showBottomBarDestinations) {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .zIndex(1f)
                                            .shadow(elevation = 8.dp)
                                    ) {
                                        ParentBottomNavigationBar(
                                            navController = navController,
                                            currentDestination
                                        )
                                    }
                                }
                            },
                            topBar = {
                                if (currentDestination != AppRoute.ParentProfile.route) {
                                    ParentAppBar(
                                        navController = navController,
                                        onNavigationIconClick = {
                                            scope.launch { drawerState.open() }
                                        },
                                        title = title,
                                        onFilter = {
                                            Log.e("TAG", "Parent top bar clicked --> " + isFilter)
                                            isFilter = !isFilter
                                        }
                                    )
                                }

                            },
                            content = { innerPadding ->
                                Column(
                                    modifier = Modifier
                                        .padding(innerPadding)
                                        .fillMaxSize()
                                        .zIndex(1f)
                                ) {

                                    NavHost(
                                        navController = navController,
                                        startDestination = AppRoute.ParentDashboard.route,
                                    ) {
                                        composable(AppRoute.ParentDashboard.route) {
                                            ParentDashboardScreen(
                                                navController,
                                                sharedPref, parentViewModel
                                            )
                                        }
                                        composable(AppRoute.ParentStudent.route) {
                                            ParentStudentScreen(
                                                navController,
                                                sharedPref, title = { newtitlw ->
                                                    title = newtitlw
                                                }, parentViewModel
                                            )
                                        }

                                        composable<AppRoute.QuizPerformance>() {
                                            val args = it.toRoute<AppRoute.QuizPerformance>()
                                            QuizPerformanceScreen(
                                                navController,
                                                sharedPref,
                                                title = { newtitlw ->
                                                    title = newtitlw
                                                },
                                                name = args.name,
                                                grade = args.grade ?: -1,
                                                userId = args.userId.toString().toInt(),
                                                parentViewModel
                                            )
                                        }
                                        composable<AppRoute.SelectSubjectPreference>() {
                                            val args =
                                                it.toRoute<AppRoute.SelectSubjectPreference>().grade
                                            StudentSubjectPreferenceScreen(
                                                navController,
                                                args,
                                                viewModel,
                                                sharedPref
                                            )
                                        }
                                        composable(AppRoute.QuizReport.route) {
                                            QuizReportScreen(
                                                navController,
                                                sharedPref, title = { newtitlw ->
                                                    title = newtitlw
                                                }, isFilter, parentViewModel
                                            )
                                        }
                                        composable(AppRoute.ParentProfile.route) {
                                            scope.launch {
                                                drawerState.close()
                                            }
                                            ParentProfileScreen(navController)
                                        }
                                        composable(AppRoute.ParentEditProfile.route) {
//                                            scope.launch {
//                                                drawerState.close()
//                                            }
                                            ParentEditProfileActivity(navController)
                                        }


                                        composable(AppRoute.ParentAuthentication.route) {
                                            AuthenticationScreen(
                                                navController,
                                                sharedPref, title = { newtitlw ->
                                                    title = newtitlw
                                                }
                                            )
                                        }
                                        composable<AppRoute.CreatePin>() {
                                            val args = it.toRoute<AppRoute.CreatePin>().userId
                                            val isForgotPinPassword =
                                                it.toRoute<AppRoute.CreatePin>().isForgotPinPassword
                                            CreatePinScreen(
                                                navController,
                                                sharedPref = sharedPref,
                                                viewModel,
                                                userId = args,
                                                isForgotPinPassword
                                            )
                                        }
                                        composable<AppRoute.EnterPin>() {
                                            val args = it.toRoute<AppRoute.EnterPin>()
                                            val phoneNo = args.phoneNo
                                            val isLoginWithOtp = args.isLoginWithPin
                                            val userId = args.userId
                                            EnterPinScreen(
                                                navController,
                                                this@ParentDashboardActivity,
                                                sharedPref,
                                                phoneNo, isLoginWithOtp, userId,
                                            )
                                        }

                                        composable<AppRoute.SwitchUserWithPin> {
                                            val args = it.toRoute<AppRoute.SwitchUserWithPin>()
                                            val isLoginWithOtp = args.isLoginWithPin
                                            val phoneNo = args.phoneNo
                                            val userId = args.userId
                                            AddStudentPinScreen(
                                                navController,
                                                this@ParentDashboardActivity, userId.toString()
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
                            }
                        )
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
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope,
    updatePeekHeight: (Dp) -> Unit
) {

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    var childList by remember { mutableStateOf(mutableListOf<ChildListResponse.Data.Student>()) }
//    var listData by remember { mutableStateOf<ChildListResponse.Data.Parent?>(null) }
    LaunchedEffect(Unit) {
        viewModel.childListLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {

                    if (it.data?.isSuccess == true) {
//                        listData = it.data.data.parent
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
            .padding(start = 15.dp, bottom = 10.dp),
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
                contentDescription = "abcd",
                modifier = Modifier
                    .padding(end = 10.dp)
                    .background(Color.Unspecified)
                    .clickable {
                        coroutineScope.launch {
                            updatePeekHeight(0.dp) // Reset peek height
                            bottomSheetScaffoldState.bottomSheetState.hide() // Collapse the bottom sheet

                        }
                    }
            )
        }
    }


//Student List
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(childList) { child ->
            SwitchUserItem(child, navController,
                Modifier.clickable {
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
    itemId: String,
    navController: NavHostController,
    parentViewModel: ParentViewModel,
    ParentDashboardActivity: ParentDashboardActivity,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope,
    halfScreenHeight: Dp,
    currentPeekHeight: Dp,
    updatePeekHeight: (Dp) -> Unit
) {
    // Handle the click event here
    val context = ParentDashboardActivity as Context

    when (itemId) {
        AppRoute.ParentAddStudent.route -> {
            val number: Int = parentViewModel.getNumberOfStudent()
            println("Number of student :- $number")
            if (number == 5) {
                Toast.makeText(context, "You have already created 5 student.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val intent = Intent(context, LoginMainActivity::class.java)
                intent.putExtra(CommandAppKey.screenName, ConstantVariables.onboarding1)
                context.startActivity(intent)
            }
        }

        AppRoute.StudentFAQ.route -> {
            startActivity(context, Intent(context, StudentFaqActivity::class.java), null)
        }

        AppRoute.StudentSwitchProfile.route -> {
            // Navigate to StudentSwitchProfile screen
            coroutineScope.launch {
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
//                    bottomSheetScaffoldState.bottomSheetState.
                }
            }
        }
    }
}

@Composable
fun SwitchUserItem(
    child: ChildListResponse.Data.Student,
    navHostController: NavHostController,
    modifier: Modifier
) {

    val viewModel: LoginViewModel = viewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .fillMaxWidth(),
        border = BorderStroke(0.8.dp, GrayLight02),
        onClick = { navHostController.navigate(AppRoute.SwitchUserWithPin("", "", child.userId)) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(White) // Set background color based on selection
                .padding(4.dp), // Add horizontal padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .padding(10.dp)
                    .size(52.dp)
                    .background(Color.Unspecified)
                    .clip(RoundedCornerShape(100.dp)),
                painter = if (child.imageUrl != null) {
                    rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(child.imageUrl)
                            .placeholder(
                                if (child.gender != null) {
                                    getGenderIconState(child.gender)
                                } else {
                                    R.drawable.icon_male_student
                                }
                            )
                            .error(
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
                    modifier = Modifier
                        .wrapContentSize(),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Gray,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = if (languageData[LanguageTranslationsResponse.KEY_CLASS].toString() == "") {
                        "Class - ${child.grade}"
                    } else {
                        languageData[LanguageTranslationsResponse.KEY_CLASS].toString() + " - ${child.grade}"
                    },
//                    text = languageData[LanguageTranslationsResponse.KEY_CLASS].toString() + " - ${child.grade}",
                    modifier = Modifier
                        .wrapContentSize(),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = GrayLight01,
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
                    fontSize = 10.sp,
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
                modifier = Modifier
                    .size(25.dp)
                    .background(Color.Unspecified)
            )
        }
    }
}

/*
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentAppBar(onNavigationIconClick: () -> Unit ={}) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .clipToBounds()
    ) {
        Column() {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_auro_scholar),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(62.dp)
                                .height(28.dp)
                        )
                        Image(
                            painter = painterResource(R.drawable.lang_demo),
                            contentDescription = "logo",
                            modifier = Modifier
                                .size(36.dp) // Add size modifier to make the image visible
                                .clip(RoundedCornerShape(100.dp))
                                .background(color = Transparent)// Add clip modifier to make the image circular
                                .border( // Add border modifier to make image stand out
                                    width = 1.dp,
                                    color = PrimaryBlue,
                                    shape = CircleShape
                                )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
                modifier = Modifier
                    .background(Transparent)
                    .zIndex(1f),
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Toggle drawer",
                            tint = com.auro.application.ui.theme.Black
                        )
                    }
                }
            )
            Divider(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = Black))
        }
    }
}
*/

/*
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentAppBar(
    onNavigationIconClick: () -> Unit = {},
    titleContent: @Composable RowScope.() -> Unit = {
        DefaultTitleContent()
    },
    showNavigationIcon: Boolean = true,
    backgroundColor: Color = Color.White,
    contentColor: Color = Color.Black,
    dividerColor: Color = Color.Gray,
    showDivider: Boolean = true
) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .clipToBounds()
    ) {
        Column {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        content = titleContent
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                modifier = Modifier
                    .background(backgroundColor)
                    .zIndex(1f),
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClick) {
                        Icon(
                            imageVector = if (showNavigationIcon) Icons.Default.Menu else Icons.Default.ArrowBack,
                            contentDescription = if (showNavigationIcon) "Toggle drawer" else "Go back",
                            tint = contentColor
                        )
                    }
                }
            )
            if (showDivider) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = dividerColor)
                )
            }
        }
    }
}
*/
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentAppBar(
    navController: NavController = rememberNavController(),
    onNavigationIconClick: () -> Unit = {}, title: String = "",
    backgroundColor: Color = Color.White,
    contentColor: Color = Color.Black,
    dividerColor: Color = Color.Gray,
    showDivider: Boolean = true,
    onFilter: () -> Unit = {}
) {
    // Create a state to safely check if navigation back is possible
    var canNavigateBack by remember { mutableStateOf(false) }
    var currentRouteUpdate by remember { mutableStateOf("") }

    val currentRoute by navController.currentBackStackEntryFlow.collectAsState(initial = null)
    LaunchedEffect(currentRoute) {
        currentRoute?.destination?.route?.let { route ->
            currentRouteUpdate = route

            canNavigateBack =
                if (currentRouteUpdate == AppRoute.ParentDashboard.route || currentRouteUpdate == AppRoute.ParentAuthentication.route || currentRouteUpdate == AppRoute.ParentStudent.route) {
                    false
                } else {
                    true
                }

        }
    }

    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .clipToBounds()
    ) {
        Column {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        content = {
                            Row(
                                modifier = Modifier.height(56.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (currentRouteUpdate == AppRoute.ParentDashboard.route) {
                                    Image(
                                        painter = painterResource(id = R.drawable.logo_auro_scholar),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .width(75.dp)
                                            .height(35.dp)
                                            .background(Color.Unspecified)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Image(

                                        painter = painterResource(R.drawable.ic_profile_bg),
                                        contentDescription = "logo",


                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(100.dp))
                                            .background(Color.Transparent)
                                            .border(
                                                width = 1.dp,
                                                color = PrimaryBlue,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                //navController.navigate(AppRoute.ParentProfile.route)
                                            }
                                    )
                                } else {
                                    Log.e(
                                        "TAG",
                                        "ParentAppBar: current route --> " + currentRouteUpdate + " checking route --> " + AppRoute.QuizReport::class.java.simpleName
                                    )
                                    Text(text = title, color = Black)
                                    if (currentRouteUpdate == AppRoute.QuizReport.route) {
                                        Image(
                                            painter = painterResource(id = R.drawable.filter_icon),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .clickable { onFilter.invoke() }
                                                .background(Color.Unspecified)
                                        )
                                    }
                                }
                            }
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
                modifier = Modifier
                    .background(backgroundColor)
                    .zIndex(1f),
                navigationIcon = {
                    IconButton(onClick = {
                        if (canNavigateBack) {
                            navController.navigateUp()
                        } else {
                            onNavigationIconClick.invoke()
                        }
                    }) {
                        Icon(
                            imageVector = if (canNavigateBack) Icons.Default.ArrowBack else Icons.Default.Menu,
                            contentDescription = if (canNavigateBack) "Go back" else "Toggle drawer",
                            tint = contentColor,
                            modifier = Modifier.background(Color.Unspecified)
                        )
                    }
                }
            )
            if (showDivider) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = dividerColor)
                )
            }
        }
    }
}


@Preview
@Composable
fun ParentBottomNavigationBar(
    navController: NavHostController = rememberNavController(),
    currentDestination: String? = ""
) {

    val viewModel: LoginViewModel = viewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    val item = listOf(
        BottomNavigationItems(
            appRoute = AppRoute.ParentDashboard.route,
//            title = stringResource(id = R.string.text_dashboard),
            title = languageData[LanguageTranslationsResponse.QUIZZES_AND_SCORES_HERE].toString(),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.dashboard_selected_icon),
            unSelectedIcon = ImageVector.vectorResource(id = R.drawable.dashboard_menu_icon)
        ),
        BottomNavigationItems(
            appRoute = AppRoute.ParentAuthentication.route,
//            title = stringResource(id = R.string.authentication),
            title = languageData[LanguageTranslationsResponse.AUTHENTICATION].toString(),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.passport_menu_icon),
            unSelectedIcon = ImageVector.vectorResource(id = R.drawable.passport_menu_icon)
        ),
        BottomNavigationItems(
            appRoute = AppRoute.ParentStudent.route,
//            title = stringResource(id = R.string.student),
            title = languageData[LanguageTranslationsResponse.STUDENTS].toString(),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.wallet_menu_icon),
            unSelectedIcon = ImageVector.vectorResource(id = R.drawable.wallet_menu_icon)
        ),
    )
    val local = LocalContext.current
    val activeColor = PrimaryBlue
    val inactiveColor = GrayLight01

    NavigationBar(
        modifier = Modifier.padding(horizontal = 1.dp),
        containerColor = com.auro.application.ui.theme.White,
        tonalElevation = 8.dp
    ) {
        item.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Log.d("SelectedScreen: ", "" + screen.appRoute)
                    when (screen.appRoute) {
                        AppRoute.ParentDashboard.route ->
                            Icon(
                                imageVector = screen.selectedIcon,
                                contentDescription = null,
                                tint = if (currentDestination == screen.appRoute) activeColor else inactiveColor,
                                modifier = Modifier.size(30.dp)
                            )

                        AppRoute.ParentAuthentication.route ->
                            Icon(
                                imageVector = screen.selectedIcon,
                                contentDescription = null,
                                tint = if (currentDestination == screen.appRoute) activeColor else inactiveColor,
                                modifier = Modifier.size(30.dp)
                            )

                        AppRoute.ParentStudent.route ->
                            Icon(
                                imageVector = screen.selectedIcon,
                                contentDescription = null,
                                tint = if (currentDestination == screen.appRoute) activeColor else inactiveColor,
                                modifier = Modifier.size(30.dp)
                            )

                        else -> {
                            Icon(
                                imageVector = screen.selectedIcon,
                                contentDescription = null,
                                tint = if (currentDestination == screen.appRoute) activeColor else inactiveColor,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                },
                selected = currentDestination == screen.appRoute,
                onClick = {
                    navController.navigate(screen.appRoute)

                },
                label = {
                    Text(
                        text = screen.title,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold,
                        color = if (currentDestination == screen.appRoute) activeColor else inactiveColor,
                        fontSize = 10.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent // Removes background color when selected
                )

            )
        }
    }
}
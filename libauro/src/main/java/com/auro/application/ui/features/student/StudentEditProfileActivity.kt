package com.auro.application.ui.features.student

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.Constants
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.api.aes.AESEncryption.decrypt
import com.auro.application.data.api.aes.AESEncryption.encryptAES
import com.auro.application.data.utlis.CommonFunction.getFileFromUri
import com.auro.application.repository.models.GetLanguageListResponse
import com.auro.application.ui.common_ui.DropdownMenuUi
import com.auro.application.ui.common_ui.DropdownSchoolMenuUi
import com.auro.application.ui.common_ui.DropdownStateMenuUi
import com.auro.application.ui.common_ui.DropdownStudentGradeUi
import com.auro.application.ui.common_ui.DropdownStudentSubjectUi
import com.auro.application.ui.common_ui.GenderEditSelection
import com.auro.application.ui.common_ui.GenderSelection
import com.auro.application.ui.common_ui.ImagePickerScreen
import com.auro.application.ui.common_ui.InputTextField
import com.auro.application.ui.common_ui.TextFieldWithIcon
import com.auro.application.ui.common_ui.showDatePickerDialog
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.screens.SchoolListBottomSheet
import com.auro.application.ui.features.login.screens.models.GetBoardListResponseModel
import com.auro.application.ui.features.login.screens.models.GetDistrictResponseModel
import com.auro.application.ui.features.login.screens.models.GetSchoolLIstResponseModel
import com.auro.application.ui.features.login.screens.models.GetStateListResponseModel
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionAnswersSubmitRequestModel
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionAnswersSubmitRequestModel.Question
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionAnswersSubmitRequestModel.Question.Answer
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionOptionsResponseModel
import com.auro.application.ui.features.parent.viewmodel.ParentViewModel
import com.auro.application.ui.features.student.menu.TabItem
import com.auro.application.ui.features.student.models.StudentProfileResponseModel
import com.auro.application.ui.features.student.screens.isEncrypted
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun StudentEditProfileActivity(navController: NavHostController) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var updateButtonClicked by remember { mutableStateOf(false) }
    //variables of data coming from API
    val viewModel: LoginViewModel = hiltViewModel()
    val parentViewModel: ParentViewModel = hiltViewModel()
    val studentViewModel: StudentViewModel = hiltViewModel()
//    var showSheetMenu by remember { mutableStateOf(false) }
    var isDialogVisible = remember { mutableStateOf(false) }

    // Use state variables for the lists
    val stateList = remember { mutableStateListOf<GetStateListResponseModel.Data.Result>() }
    val districtList = remember { mutableStateListOf<GetDistrictResponseModel.Data>() }
    val schoolList = remember { mutableStateListOf<GetSchoolLIstResponseModel.Data>() }
    val mediumList = remember { mutableStateListOf<GetLanguageListResponse.Data.Result>() }
    val boardList = remember { mutableStateListOf<GetBoardListResponseModel.Data.Result>() }
    val questionAnswerModelList =
        remember { mutableStateListOf<GetCompleteProfilerQuestionOptionsResponseModel.Data>() }
    var profileData by remember { mutableStateOf<StudentProfileResponseModel.ProfileData?>(null) }
    var isApiCalled by remember { mutableStateOf(false) }
    var isUserNameExist by remember { mutableStateOf(false) }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    CustomDialog(
        isVisible = isDialogVisible.value, onDismiss = { isDialogVisible.value = false },
//        message = "Loading your data..."
        message = msgLoader
    )

    LaunchedEffect(Unit) {

        if (profileData == null) {
            studentViewModel.getStudentProfileResponse.observeForever {
                when (it) {
                    is NetworkStatus.Idle -> {}
                    is NetworkStatus.Loading -> {
                        isDialogVisible.value = true
                    }

                    is NetworkStatus.Success -> {
                        isDialogVisible.value = false
                        if (it.data?.isSuccess == true) {
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
                            viewModel.saveStudentProfileData(profileData!!)
                            println("Profile data saved :- $profileData")
                        }
                    }

                    is NetworkStatus.Error -> {
                        isDialogVisible.value = false
                        context.toast(it.message)
                    }
                }
            }
        } else {
            profileData = viewModel.getStudentProfileData()
        }

        viewModel.mediumResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible.value = false
                }

                is NetworkStatus.Success -> {
                    isDialogVisible.value = false
                    mediumList.clear()
                    it.data?.data?.results?.let { it1 -> mediumList.addAll(it1) }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible.value = false
//                    context.toast(it.message)
                }
            }
        }

        viewModel.getStateListResponseModel.observeForever { it ->
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible.value = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible.value = false
                    stateList.clear()
                    districtList.clear()
                    schoolList.clear()
                    stateList.addAll(it.data!!.data.results)

                    /*it.data?.data.let {
                        it?.let { it1 -> stateList.addAll(it1.results) }
                    }*/
                }

                is NetworkStatus.Error -> {
                    isDialogVisible.value = false
//                    context.toast(it.message)
                }
            }
        }

        viewModel.checkUsernameResponse.observeForever {
            when (it) {
                is NetworkStatus.Error -> {
                    isDialogVisible.value = false
                    isUserNameExist = false
                }

                NetworkStatus.Idle -> {
//                    isDialogVisible.value = false
                }

                NetworkStatus.Loading -> {
                    isDialogVisible.value = true
                }

                is NetworkStatus.Success -> {
//                    isDialogVisible.value = false
                    if (it.data?.isSuccess == true) {
                        if (it.data.data!!.exists == 0) {
                            isUserNameExist = false
                        } else {
                            isUserNameExist = true
                            context.toast(it.data.error.toString())
                        }

                    } else {
                        isUserNameExist = true
                        context.toast(it.data?.error.toString())
                    }
                }
            }
        }
        viewModel.getDistrictResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible.value = true
                }
                is NetworkStatus.Success -> {
                    isDialogVisible.value = false
                    districtList.clear()
                    districtList.addAll(it.data?.data?.toMutableList() ?: mutableListOf())
                }

                is NetworkStatus.Error -> {
                    isDialogVisible.value = false
//                    context.toast(it.message)
                }
            }
        }

        viewModel.getSchoolListResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible.value = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible.value = false
                    schoolList.clear()
                    schoolList.addAll(it.data?.data?.toMutableList() ?: mutableListOf())
                }
                is NetworkStatus.Error -> {
                    isDialogVisible.value = false
//                    context.toast(it.message)
                }
            }
        }
        viewModel.getBoardListResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    boardList.clear()
                    boardList.addAll((it.data?.data?.results ?: mutableListOf()))
                }
                is NetworkStatus.Error -> {}
            }
        }
        studentViewModel.getStudentProfileResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible.value = true
                }

                is NetworkStatus.Success -> {

                    isDialogVisible.value = false
                    if (it.data?.isSuccess == true) {

                        profileData = it.data.data
                        viewModel.saveStudentProfileData(profileData!!)
                        viewModel.saveUserName(it.data.data.name)
                        viewModel.saveUserEmail(it.data.data.email)
                        viewModel.saveUserImage(it.data.data.imageUrl)
                        viewModel.saveGrade(it.data.data.grade.toString())
                        println("Profile data saved :- $profileData")
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible.value = false
                    context.toast(it.message)
                }
            }
        }

        parentViewModel.getCompleteProfilerQuestionAndOptionsModelLiveData.observeForever { networkStatus ->
            when (networkStatus) {
                is NetworkStatus.Idle -> {
                    isDialogVisible.value = false
                    Log.e("TAG", "QuizPerformanceScreen: loading")
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible.value = true
                    Log.e("TAG", "QuizPerformanceScreen: loading")
                }

                is NetworkStatus.Success -> {
                    isDialogVisible.value = false
                    Log.e(
                        "TAG",
                        "QuizPerformanceScreen: done ---> " + networkStatus.data?.data.toString()
                    )
                    // Clear the existing list before adding new questions
                    questionAnswerModelList.clear()
                    networkStatus.data?.data?.let {
                        questionAnswerModelList.addAll(it)
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible.value = false
                    // Handle the error case, e.g., show a message to the user
                }
            }
        }

        parentViewModel.getStudentProfilerQuestionAndOptionsModelLiveData.observeForever { networkStatus ->
            when (networkStatus) {
                is NetworkStatus.Idle -> {
                    isDialogVisible.value = false
                    Log.e("TAG", "QuizPerformanceScreen: loading")
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible.value = true
                    Log.e("TAG", "QuizPerformanceScreen: loading")
                }

                is NetworkStatus.Success -> {
                    isDialogVisible.value = false
                    // Clear the existing list before adding new questions
//                    viewModel.removeStudentProfilerData()
//                    context.toast(networkStatus.data?.data.toString())
//                    navController.navigate(AppRoute.StudentProfile.route)
                    val intent = Intent(context, StudentDashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.also {
                        it.putExtra("", true)
                    }
                    context.startActivity(intent)
                }

                is NetworkStatus.Error -> {
                    isDialogVisible.value = false
                    // Handle the error case, e.g., show a message to the user
                }
            }
        }

        studentViewModel.saveStudentProfileResponse.observeForever { networkStatus ->
            when (networkStatus) {
                is NetworkStatus.Idle -> {
                    isDialogVisible.value = false
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible.value = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible.value = false
                    context.toast(networkStatus.data?.data.toString())
                    studentViewModel.getStudentProfile()
//                    context.toast(networkStatus.data?.data.toString())
                }

                is NetworkStatus.Error -> {
                    isDialogVisible.value = false
                    context.toast(networkStatus.message)
                    // Handle the error case, e.g., show a message to the user
                }
            }
        }

        studentViewModel.getGradeUpdateResponseData.observeForever { networkStatus ->
            when (networkStatus) {
                is NetworkStatus.Idle -> {
                    isDialogVisible.value = false

                }

                is NetworkStatus.Loading -> {
                    isDialogVisible.value = true

                }

                is NetworkStatus.Success -> {
                    isDialogVisible.value = false
                    if (networkStatus.data?.isSuccess == true) {
                    } else {
                        context.toast(networkStatus.data!!.error.toString())
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible.value = false
                    context.toast(networkStatus.message)
                    // Handle the error case, e.g., show a message to the user
                }
            }
        }
        if (!isApiCalled) {
            viewModel.fetchLanguages() // for medium
            viewModel.getStateList()
            if (profileData != null && profileData!!.state != "") viewModel.getDistrict(profileData!!.state.toInt())
            if (profileData != null && profileData!!.district != "") viewModel.getSchoolList(
                profileData!!.district.toInt(), ""
            )
            viewModel.getBoardList()
            isApiCalled = true
        }
    }
    AuroscholarAppTheme {
        val tabItems = listOf(
            TabItem(
                title = if (languageData[LanguageTranslationsResponse.KEY_PERSONAL_DETAILS].toString() == "") {
                    stringResource(id = R.string.txt_personal_details)
                } else {
                    languageData[LanguageTranslationsResponse.KEY_PERSONAL_DETAILS].toString()
                }
            ),
            TabItem(
                title = stringResource(id = R.string.text_school_details)
            ),
            TabItem(
                title = if (languageData[LanguageTranslationsResponse.KEY_PROFILER].toString() == "") {
                    stringResource(id = R.string.txt_profiler)
                } else {
                    languageData[LanguageTranslationsResponse.KEY_PROFILER].toString()
                }
            ),
        )
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val pagerState = rememberPagerState { tabItems.size }
        if (profileData == null) {
            studentViewModel.getStudentProfile()
        } else {
            profileData = viewModel.getStudentProfileData()
        }

        LaunchedEffect(selectedTabIndex) {
            // if tab selected then scroll the content
            pagerState.animateScrollToPage(selectedTabIndex)
        }

        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
            // if scrolling the content change the tab as well
            if (!pagerState.isScrollInProgress) {
                selectedTabIndex = pagerState.currentPage
            }
        }
//                Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = White)
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.back_icon),
                    contentDescription = "logo",
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                        navController.navigateUp()
                    },
                    colorFilter = ColorFilter.tint(Black)
                )
                Text(
                    text = if (languageData[LanguageTranslationsResponse.EDIT_PRO].toString() == "") {
                        stringResource(id = R.string.txt_edit_profile)
                    } else {
                        languageData[LanguageTranslationsResponse.EDIT_PRO].toString()
                    },
                    modifier = Modifier
                        .wrapContentSize()
                        .weight(1f)
                        .padding(start = 20.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )/*Image(
                    painter = painterResource(R.drawable.edit_profile_icon),
                    contentDescription = "logo",
                    modifier = Modifier
                        .clickable {
                        },
                    colorFilter = ColorFilter.tint(Black)
                )
                Image(
                    painter = painterResource(R.drawable.more_icon),
                    contentDescription = "logo",
                    modifier = Modifier
                        .clickable {
                            showSheetMenu = true
                        },
                    colorFilter = ColorFilter.tint(Black)
                )*/
            }
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabItems.forEachIndexed { index, tabItem ->
                    Tab(selected = index == selectedTabIndex, onClick = {
//                        if (index != 1) {
//                            selectedTabIndex = index
//                        }
//                        else {
//                            context.toast("Please update Personal Details!")
//                        }
                    }, text = {
                        Text(
                            text = tabItem.title,
                            modifier = Modifier.padding(
                                vertical = 5.dp
                            ),
                            color = if (index == selectedTabIndex) PrimaryBlue else GrayLight01,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    })
                }
            }
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                var updateButtonClicked by remember { mutableStateOf(false) }
                HorizontalPager(
                    state = pagerState, modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = false // Disables swiping
                ) { index ->
                    when (index) {
                        0 -> {
                            if (profileData != null) {
                                PersonalDetailsScreen(
                                    isDialogVisible = isDialogVisible,
                                    navController,
                                    profileData!!,
                                    viewModel,
                                    studentViewModel,
                                    languageData,
                                    context
                                ) {
                                    if (isUserNameExist) {
                                        isDialogVisible.value = false
                                    } else {
                                        isDialogVisible.value = false
                                        updateButtonClicked = true
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index + 1)
                                        }
                                    }
                                }
                            } else {
                                studentViewModel.getStudentProfile()
                            }
                        }
                        1 -> {
                            if (profileData != null) {
                                SchoolDetailsScreen(
                                    updateButtonClicked = updateButtonClicked,
                                    navController,
                                    profileData!!,
                                    studentViewModel,
                                    viewModel,
                                    languageData,
                                    stateList,
                                    districtList,
                                    schoolList,
                                    mediumList,
                                    boardList,
                                    context
                                ) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index + 1)
                                    }
                                }
                            } else {
                                studentViewModel.getStudentProfile()
                            }
                        }

                        2 -> {
                            ViewProfiler(
                                context,
                                navController,
                                viewModel,
                                parentViewModel,
                                questionAnswerModelList
                            )
                            parentViewModel.getCompleteProfilerQuestionAndOption()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ViewProfiler(
    context: Context,
    navController: NavHostController,
    viewModel: LoginViewModel,
    parentViewModel: ParentViewModel,
    questionAnswerModelList: SnapshotStateList<GetCompleteProfilerQuestionOptionsResponseModel.Data>,
) {
    val selectedQuestionList = remember { mutableListOf<Question>() }
//    val selectedAnswersList = remember { mutableListOf<Answer>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(15.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .weight(1f)
                .border(
                    width = 0.5.dp, color = GrayLight02, shape = RoundedCornerShape(10.dp)
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                items(questionAnswerModelList) { question ->
                    ProfilerScreen(
                        lists = question, selectedQuestionList = selectedQuestionList, viewModel/* currentIndex = currentIndex,
                     onNext = {
                         parentViewModel.getCompleteProfilerQuestionAndOptionSave(it)
                         if (currentIndex < questionAnswerModelList.size - 1) {
                             currentIndex++
                         }
                     },
                     onPrevious = {
                         if (currentIndex > 0) {
                             currentIndex--
                         }
                     }*/
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(color = Color.Transparent)
        ) {

            Button(
                onClick = { navController.navigate(AppRoute.StudentProfile.route) },

                modifier = Modifier
                    .background(color = White)
                    .padding(top = 5.dp)
                    .weight(1f)
                    .fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                    containerColor = White
                )
            ) {
                Text(
                    modifier = Modifier
                        .background(color = White)
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.txt_cancel),
                    color = PrimaryBlue,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold
                )
            }

            TextButton(
                onClick = {
//                    val profilerPostData = viewModel.getStudentProfilerData()

                    // Log the updated profiler request model
                    val profilerRequestModel = GetCompleteProfilerQuestionAnswersSubmitRequestModel(
                        selectedQuestionList
                    )
                    if (selectedQuestionList.isNotEmpty()) {
                        parentViewModel.getStudentProfilerQuestionAndOptionSave(profilerRequestModel)
                    }
                    else if (selectedQuestionList.isEmpty()){
                        context.toast("Please update your profiler information.")
                    }

                    else {
                        val intent = Intent(context, StudentDashboardActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.also {
                            it.putExtra("", true)
                        }
                        context.startActivity(intent)
                    }
                },

                modifier = Modifier
                    .background(color = PrimaryBlue, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 5.dp, horizontal = 5.dp)
                    .weight(1f)
                    .height(40.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Text(
                    modifier = Modifier
                        .background(color = PrimaryBlue)
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.txt_submit),
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun PersonalDetailsScreen(
    isDialogVisible: MutableState<Boolean>,
    navController: NavHostController,
    profileData: StudentProfileResponseModel.ProfileData,
    viewModel: LoginViewModel,
    studentViewModel: StudentViewModel,
    languageData: HashMap<String, String>,
    context: Context,
    onNextClick: () -> Unit,
) {

    var date by remember { mutableStateOf(profileData.dateOfBirth) }
    when (profileData.gender) {
        "Male", "M" -> {
            profileData.gender = "Male"
        }

        "Female", "F" -> {
            profileData.gender = "Female"
        }

        else -> {
            profileData.gender = "Others"
        }
    }
    val selectedGender = remember { mutableStateOf(profileData.gender) }
//    val gradeValue: MutableState<String> = remember { mutableStateOf(profileData.grade.toString()) }
    var grade by remember { mutableStateOf(viewModel.getGrade().toString()) }
//    var grade by remember { mutableStateOf(profileData.grade.toString()) }

    val studentName = remember { mutableStateOf(profileData.name) }
//    val username = remember { mutableStateOf(decrypt(profileData.userName)) }
    val username = remember {
        mutableStateOf(
            if (profileData.userName != null) {
                decrypt(profileData.userName)
            } else {
                ""
            }
        )
    }
    val isEncrypted = remember { isEncrypted(profileData.email) }
    val email = remember {
        mutableStateOf(
            if (isEncrypted) {
                decrypt(profileData.email)
            } else {
                profileData.email
            }
        )
    }
//    val email = remember { mutableStateOf(profileData.email) }
    var showImagePicker by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var disableGrade by remember { mutableStateOf(false) }

    var errorMsg: String = stringResource(id = R.string.error_mandatory_field)
    // Show the ImagePickerScreen when `showImagePicker` is true
    if (showImagePicker) {
        ImagePickerScreen(onImagePicked = { uri ->
            imageUri = uri  // Set the selected image URI
            showImagePicker = false  // Hide the picker once the image is picked
        }, onDismiss = {
            showImagePicker = false  // Hide the picker on dismissal
        })
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 5.dp, end = 5.dp, top = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .border(
                        width = 0.5.dp, color = GrayLight02, shape = RoundedCornerShape(10.dp)
                    )
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 20.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center) // Center the column horizontally and vertically
                ) {
                    Box(
                        modifier = Modifier.wrapContentSize(Alignment.Center) // Center the box within the column
                    ) {
                        Box(
                            modifier = Modifier.padding(10.dp), // Add padding to the outer box
                        ) {
                            Image(
                                painter = if (imageUri != null) {
                                    rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current).data(imageUri)
                                            .placeholder(R.drawable.ic_profile)
                                            .error(R.drawable.ic_profile)
                                            .crossfade(true) // Optional: Add a fade transition
                                            .build()
                                    )
                                } else {
                                    if (profileData.imageUrl == null) {
                                        painterResource(R.drawable.ic_profile)
                                    } else {
                                        rememberAsyncImagePainter(
                                            ImageRequest.Builder(LocalContext.current)
                                                .data(profileData.imageUrl)
                                                .placeholder(R.drawable.ic_profile)
                                                .error(R.drawable.ic_profile)
                                                .crossfade(true) // Optional: Add a fade transition
                                                .build()
                                        )
                                    }
                                },
                                contentDescription = "logo",
                                modifier = Modifier
                                    .size(100.dp) // Add size modifier to make the image visible
                                    .clip(CircleShape) // Add clip modifier to make the image circular
                                    .background(shape = CircleShape, color = White)
                                    .align(Alignment.BottomEnd)
                                    .border( // Add border modifier to make image stand out
                                        width = 1.dp, color = GrayLight02, shape = CircleShape
                                    ),
                                contentScale = ContentScale.Crop
                            )
//                        }

                            Icon(
                                painter = painterResource(R.drawable.ic_add_photo), // Replace with your edit pen icon
                                contentDescription = "Edit",
                                modifier = Modifier
                                    .size(50.dp) // Adjust the size of the icon
                                    .offset(
                                        x = 75.dp, // Offset the icon to overlap the image
                                        y = 25.dp
                                    )
                                    .padding(end = 10.dp)
                                    .clickable { showImagePicker = true },
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
                Text(
                    text = buildAnnotatedString {
                        append(
                            if (languageData[LanguageTranslationsResponse.KEY_STUDENT_NAME].toString() == "") {
                                stringResource(R.string.student_name)
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_STUDENT_NAME].toString()
                            }
                        )
                        pushStyle(SpanStyle(color = Color.Red))
                        append(" *")
                        pop()
                    },
                    color = Gray,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                InputTextField(
                    modifier = Modifier.padding(top = 5.dp),
                    value = studentName,
                    if (languageData[LanguageTranslationsResponse.KEY_NAME_AS_PER_AADHAR].toString() == "") {
                        stringResource(R.string.enter_your_name_as_per_aadhar_name)
                    } else {
                        languageData[LanguageTranslationsResponse.KEY_NAME_AS_PER_AADHAR].toString()
                    },
                    editable = if (profileData.kycStatus == "Approve") {
                        (studentName == null) || (studentName.value.isEmpty())
                    } else {
                        true
                    }/*if ((studentName != null) || (studentName.value.isNotEmpty())) {
                        true
                    }
                        else if (profileData.kycStatus != "Approve"){

                    } else {
                        true
                    }*/,
                    keyboardType = KeyboardType.Text
                )

                Text(
                    text = buildAnnotatedString {
                        append(
                            if (languageData[LanguageTranslationsResponse.CREATE_USERNAME].toString() == "") {
                                stringResource(R.string.create_username)
                            } else {
                                languageData[LanguageTranslationsResponse.CREATE_USERNAME].toString()
                            }
                        )
                        pushStyle(SpanStyle(color = Color.Red))
                        append(" *")
                        pop()
                    },
                    modifier = Modifier.padding(top = 10.dp),
                    color = Gray,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                InputTextField(
                    modifier = Modifier.padding(top = 5.dp),
                    value = username,
                    if (languageData[LanguageTranslationsResponse.ENTER_UNAME].toString() == "") {
                        stringResource(R.string.enter_your_username)
                    } else {
                        languageData[LanguageTranslationsResponse.ENTER_UNAME].toString() + " *"
                    },
                    editable = true/*if (profileData.kycStatus == "Approve") {
                        (username == null) || (username.value.isEmpty())
                    } else {
                        true
                    }*//*if ((username != null) && (username.value.isNotEmpty())) {
                        profileData.kycStatus != "Approve"
                    } else {
                        true
                    }*/,
                    keyboardType = KeyboardType.Text
                )

                val data =
                    languageData[LanguageTranslationsResponse.KEY_INSTRUCTION_FIRST].toString() + "\n" + languageData[LanguageTranslationsResponse.KEY_INSTRUCTION_SECOND].toString() + "\n" + languageData[LanguageTranslationsResponse.KEY_INSTRUCTION_THIRD].toString() + "\n" + languageData[LanguageTranslationsResponse.KEY_INSTRUCTION_FOURTH].toString() + "\n" + languageData[LanguageTranslationsResponse.KEY_INSTRUCTION_FIFTH].toString()

                Text(
                    data,
                    modifier = Modifier.padding(5.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    color = GrayLight01,
                    fontSize = 10.sp
                )

                // grade
                Text(
                    text = buildAnnotatedString {
                        append(
                            if (languageData[LanguageTranslationsResponse.KEY_GRADE].toString() == "") {
                                stringResource(R.string.grade)
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_GRADE].toString()
                            }
                        )
                        pushStyle(SpanStyle(color = Color.Red))
                        append(" *")
                        pop()
                    },
                    modifier = Modifier.padding(top = 10.dp),
                    color = Gray,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                DropdownStudentGradeUi(
                    listOf(
                        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
                    ),
                    onItemSelected = {
                        grade = it
//                    sharedPref?.saveGrade(it)
//                    Log.e("TAG", "OnboardingStep1: shared prefred ---- > "+sharedPref?.getGrade())
                    },
                    placeholder = grade,
                    icon = painterResource(R.drawable.ic_down),
                    onClick = {},
                    disableEnable = profileData.kycStatus != "Approve"
                )

                /*Text(
                    stringResource(R.string.grade),
                    modifier = Modifier.padding(top = 10.dp),
                    color = Gray,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                DisabledInputTextField(
                    isEnable = profileData.kycStatus != "Approve",
                    modifier = Modifier.padding(top = 5.dp), value = grade,
                    stringResource(R.string.txt_grade), keyboardType = KeyboardType.Number,
                    maxLength = 2
                )*/

                Text(
                    text = buildAnnotatedString {
                        append(languageData[LanguageTranslationsResponse.GENDER].toString())
                        pushStyle(SpanStyle(color = Color.Red))
                        append(" *")
                        pop()
                    },
                    modifier = Modifier.padding(top = 10.dp),
                    color = Gray,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                GenderEditSelection(
                    items = listOf(
                        "Male", "Female", "Others"
                    ),
                    selectedItem = selectedGender,
                    disableEnable = profileData.kycStatus != "Approve"
                ) { selected ->
                    // Handle the selected item here
                    println("Selected: $selected")
                }

                // date  of birth
                Text(
//                    stringResource(R.string.date_of_birth),
                    text = buildAnnotatedString {
                        append(languageData[LanguageTranslationsResponse.DATE_OF_BIRTH].toString())
                        pushStyle(SpanStyle(color = Color.Red))
                        append(" *")
                        pop()
                    },
                    modifier = Modifier.padding(top = 10.dp),
                    color = Gray,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                TextFieldWithIcon(
                    modifier = Modifier.clickable {
                        var selectedMonth: String = ""
                        var selectedDay: String = ""
                        showDatePickerDialog(context) { year, month, dayOfMonth ->
                            selectedMonth = if (month < 10) {
                                "0$month"
                            } else {
                                month.toString()
                            }
                            selectedDay = if (dayOfMonth < 10) {
                                "0$dayOfMonth"
                            } else {
                                dayOfMonth.toString()
                            }
                            date = "$year-$selectedMonth-$selectedDay"
                        }
                    },
                    value = remember { mutableStateOf(date) },
                    placeholder = if (date.isEmpty()) languageData[LanguageTranslationsResponse.KEY_SELECT_DOB].toString() else date
                )

                // email
                Text(
//                    stringResource(R.string.email),
                    text = languageData[LanguageTranslationsResponse.EMAIL_ID].toString(),
                    modifier = Modifier.padding(top = 10.dp),
                    color = Gray,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                InputTextField(
                    modifier = Modifier.padding(top = 5.dp),
                    value = email,
                    languageData[LanguageTranslationsResponse.KEY_ENTER_MAIL_ID].toString(),
                    true,
                    keyboardType = KeyboardType.Email
                )
            }
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(color = Color.White),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            navController.popBackStack()
                            navController.navigateUp()
//                            navController.navigate(AppRoute.StudentProfile.route)
                        },
                        modifier = Modifier
                            .background(color = Color.Transparent)
                            .padding(vertical = 5.dp, horizontal = 5.dp)
                            .weight(1f)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        )
                    ) {
                        Text(
                            modifier = Modifier
                                .background(color = Color.White)
                                .fillMaxWidth(),
                            text = languageData[LanguageTranslationsResponse.KEY_CANCEL].toString(),
                            color = PrimaryBlue,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    TextButton(
                        onClick = {
                            var byteArray: ByteArray? = null

                            //    private lateinit var bitmap: Bitmap
                            var fileName: String? = null
                            // Reset errors
                            // Validate fields
                            if (username.value.isNotEmpty() && studentName.value.isNotEmpty() && selectedGender.value.isNotEmpty() && date.isNotEmpty()) {
                                Constants.userName = username.value.encryptAES()?.trim()
                                Constants.name = studentName.value
                                Constants.email =
                                    if (email.value == "null" || email.value.isEmpty()) {
                                        ""
                                    } else {
                                        email.value.encryptAES()?.trim()
                                    }
                                Constants.gender = when (selectedGender.value) {
                                    "Male", "M" -> {
                                        "M"
                                    }

                                    "Female", "F" -> {
                                        "F"
                                    }

                                    else -> {
                                        "O"
                                    }
                                }

                                Constants.dateOfBirth = date
                                Constants.grade = grade

                                if (imageUri != null) {/* byteArray = uriToByteArray(context, imageUri!!)
 //                                fileName = getFileName(context, imageUri)*/
                                    val file = getFileFromUri(context, imageUri)
                                    Constants.imageUrlPart = file
                                } else {
                                    Constants.imageUrlPart = null
                                }

                                // Move to next tab if validation is successful
                                isDialogVisible.value = true
                                viewModel.checkUsernameExiting(
                                    username.value, "check"
                                ) // check after login, add for new user
                                studentViewModel.getGradeUpdate(grade)
                                onNextClick()

                            } else {
                                context.toast(errorMsg)
                            }
                        },
                        modifier = Modifier
                            .background(color = PrimaryBlue, shape = RoundedCornerShape(12.dp))
                            .padding(vertical = 5.dp, horizontal = 5.dp)
                            .weight(1f)
                            .height(40.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        )
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = stringResource(id = R.string.next),
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SchoolDetailsScreen(
    updateButtonClicked: Boolean,
    navController: NavHostController,
    profileData: StudentProfileResponseModel.ProfileData,
    studentViewModel: StudentViewModel,
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    stateList: SnapshotStateList<GetStateListResponseModel.Data.Result>,
    districtList: SnapshotStateList<GetDistrictResponseModel.Data>,
    schoolList: SnapshotStateList<GetSchoolLIstResponseModel.Data>,
    mediumList: SnapshotStateList<GetLanguageListResponse.Data.Result>,
    boardList: SnapshotStateList<GetBoardListResponseModel.Data.Result>,
    context: Context,
    onNextClick: () -> Unit,
) {

    // data from sharedpref
    if (profileData.stateName == "") {
        profileData.stateName =
            if (languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_STATE].toString() == "") {
                stringResource(R.string.txt_please_select_state)
            } else {
                languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_STATE].toString()
            }
    }
    val state by remember { mutableStateOf(profileData.stateName) }
    if (profileData.districtName == "") {
        profileData.districtName =
            if (languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_DISTRICT].toString() == "") {
                stringResource(R.string.txt_please_select_district)
            } else {
                languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_DISTRICT].toString()
            }
    }
    val district = remember { mutableStateOf(profileData.districtName) }
    if (profileData.mediumName == "") {
        profileData.mediumName =
            if (languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_MEDIUM].toString() == "") {
                stringResource(R.string.txt_please_select_medium)
            } else {
                languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_MEDIUM].toString()
            }
    }
    val medium by remember { mutableStateOf(profileData.mediumName) }
    if (profileData.schoolName == "") {
        profileData.schoolName =
            if (languageData[LanguageTranslationsResponse.KEY_ENTER_SCHOOL_NAME].toString() == "") {
                stringResource(R.string.enter_school_name)
            } else {
                languageData[LanguageTranslationsResponse.KEY_ENTER_SCHOOL_NAME].toString()
            }
    }
    val school = remember { mutableStateOf(profileData.schoolName) }
    if (profileData.boardName == "") {
        profileData.boardName = stringResource(R.string.txt_please_select_board)
    }
    val board = remember { mutableStateOf(profileData.boardName) }

    if (profileData.state == "") {
        profileData.state = "0"
    }
    val stateSelectedId = remember { mutableIntStateOf(profileData.state.toInt()) }
    if (profileData.district == "") {
        profileData.district = "0"
    }
    val districtSelectedId = remember { mutableIntStateOf(profileData.district.toInt()) }
    if (profileData.schoolId == "") {
        profileData.schoolId = "0"
    }
    val schoolSelectedId = remember { mutableIntStateOf(profileData.schoolId.toInt()) }
    if (profileData.board == "") {
        profileData.board = "0"
    }
    val boardSelectId = remember { mutableIntStateOf(profileData.board.toInt()) }
    if (profileData.medium == "") {
        profileData.medium = "0"
    }
    val mediumSelectId = remember { mutableIntStateOf(profileData.medium.toInt()) }

    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })
    var isDialogVisible by remember { mutableStateOf(false) }
    var schoolListOpen by remember { mutableStateOf(false) }
    val new_school_name = remember { mutableStateOf("") }
    var selectedSchool by remember { mutableStateOf(profileData.schoolName) }
    val pincode = remember { mutableStateOf(profileData.pincode) }
    var isApiCalled by remember { mutableStateOf(false) }

    if (!isApiCalled) {
        viewModel.fetchLanguages() // for medium
        viewModel.getStateList()
        if (profileData != null && profileData!!.state != "") viewModel.getDistrict(profileData!!.state.toInt())
        if (profileData != null && profileData!!.district != "") viewModel.getSchoolList(
            profileData!!.district.toInt(), ""
        )
        viewModel.getBoardList()
        isApiCalled = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 5.dp, end = 5.dp, top = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(start = 5.dp, end = 5.dp, top = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .border(
                            width = 0.5.dp, color = GrayLight02, shape = RoundedCornerShape(10.dp)
                        )
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 20.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Text(
                        languageData[LanguageTranslationsResponse.KEY_STATE].toString(),
                        modifier = Modifier.padding(top = 10.dp)
                    )

                    Log.e(
                        "TAG", "OnboardingStep2: state list " + profileData.state
                    )
                    DropdownStateMenuUi(
                        stateList.map { it.name }.toList(),
                        onItemSelected = { selected ->
                            districtList.clear()
                            schoolList.clear()
                            district.value = "Please select District"
                            school.value = "Please select school"
                            districtSelectedId.value = -1
                            schoolSelectedId.value = -1
                            stateList.find { it.name == selected }?.id?.let {
                                stateSelectedId.value = it
                                viewModel.getDistrict(
                                    stateSelectedId.value
                                )
                            }
                        },
                        placeholder = state,
                        onClick = {
                            Log.e(
                                "TAG", "OnboardingStep2: state list " + profileData.state
                            )
                        },
                        disableEnable = if (profileData.kycStatus == "Approve") {
                            profileData.state == "0" || profileData.state == languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_STATE].toString()
                        } else {
                            true
                        }


                        /*if (profileData.state != "0") {
                            profileData.kycStatus != "Approve"
                        } else {
                            true
                        }*/
                    )

                    Text(
                        text = buildAnnotatedString {
                            append(languageData[LanguageTranslationsResponse.KEY_CITY_DIST].toString())
                            pushStyle(SpanStyle(color = Color.Red))
                            append(" *")
                            pop()
                        },
                        modifier = Modifier.padding(top = 10.dp)
                    )

                    DropdownStateMenuUi(
                        districtList.map { it.name }.toMutableList(),
                        onItemSelected = { selected ->
                            schoolList.clear()
//                            selectedSchool = "Select"
                            school.value = "Please select school"
                            schoolSelectedId.value = -1
                            selectedSchool =
                                languageData[LanguageTranslationsResponse.KEY_SELECT].toString()
                            districtList.find { it.name == selected }?.id?.let {
                                viewModel.getSchoolList(it, "")
                                districtSelectedId.value = it
                            }
                        },
                        placeholder = district.value,
                        onClick = {
                            viewModel.getDistrict(
                                stateSelectedId.value
                            )
                        },
                        disableEnable = if (profileData.kycStatus == "Approve") {
                            profileData.district == "0" || profileData.district == languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_DISTRICT].toString()
                        } else {
                            true
                        }

                        /* if (profileData.district != "0") {
                             profileData.kycStatus != "Approve"
                         } else {
                             true
                         }*/
                    )

                    Text(
                        buildAnnotatedString {
                            append(
                                if (languageData[LanguageTranslationsResponse.KEY_SCHOOL_NAME].toString() == "") {
                                    stringResource(R.string.school_name)
                                } else {
                                    languageData[LanguageTranslationsResponse.KEY_SCHOOL_NAME].toString()
                                }
                            )
                            pushStyle(SpanStyle(color = Color.Red))
                            append(" *")
                            pop()
                        }, modifier = Modifier.padding(top = 10.dp)
                    )


                    /*if (schoolList.isEmpty() || schoolList.size == 0) {
                        Text(stringResource(R.string.other_school))
                        InputTextField(
                            modifier = Modifier.padding(top = 5.dp),
                            value = new_school_name,
                            stringResource(R.string.enter_school_name),
                            keyboardType = KeyboardType.Text
                        )

                        Text(stringResource(R.string.pin_code))

                        InputTextField(
                            modifier = Modifier.padding(top = 5.dp),
                            value = pincode,
                            stringResource(R.string.enter_pin_code),
                            keyboardType = KeyboardType.Number
                        )
                    } else {*/

//                    }

                    SchoolListBottomSheet(
                        isBottomSheetVisible = isBottomSheetVisible,
                        sheetState = sheetState,
                        onDismiss = {
                            scope.launch { sheetState.hide() }
                                .invokeOnCompletion { isBottomSheetVisible = false }
                        },
                        onDecline = {
//                            navHostController.navigate(AppRoute.RegistrationNotice("").route)
                        },
                        onTextSelected = {
                            selectedSchool =
                                if (it == languageData[LanguageTranslationsResponse.KEY_SELECT].toString()) {
                                    profileData.schoolName
                                } else {
                                    it
                                }
                            schoolList.find { it.name == selectedSchool }?.id?.let {
                                schoolSelectedId.value = it
                            }
                            school.value = selectedSchool

                        },
                        schoolList.map { it.name }.toList()
                    )

                    if (selectedSchool == languageData[LanguageTranslationsResponse.OTHER].toString()) {
//                        Text(stringResource(R.string.other_school))
                        InputTextField(
                            modifier = Modifier.padding(top = 5.dp),
                            value = new_school_name,
                            if (languageData[LanguageTranslationsResponse.KEY_ENTER_SCHOOL_NAME].toString() == "") {
                                stringResource(R.string.enter_school_name)
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_ENTER_SCHOOL_NAME].toString()
                            },
                            keyboardType = KeyboardType.Text
                        )

                        Text(
                            buildAnnotatedString {
                                append(
                                    if (languageData[LanguageTranslationsResponse.KEY_PIN_CODE].toString() == "") {
                                        stringResource(R.string.pin_code)
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_PIN_CODE].toString()
                                    }
                                )
                                pushStyle(SpanStyle(color = Color.Red))
                                append(" *")
                                pop()
                            }
                        )

                        InputTextField(
                            modifier = Modifier.padding(top = 5.dp),
                            value = pincode,
                            if (languageData[LanguageTranslationsResponse.KEY_ENTER_PIN_CODE].toString() == "") {
                                stringResource(R.string.enter_pin_code)
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_ENTER_PIN_CODE].toString()
                            },
                            keyboardType = KeyboardType.Number,
                            maxLength = 6
                        )
                    } else {
                        DropdownSchoolMenuUi(listOf(), onItemSelected = {

                        }, modifier = Modifier.clickable {
                            Log.e("TAG", "SchoolName ")
                        }, placeholder = school.value, onClick = {
                            schoolListOpen = true
                            scope.launch {
                                isBottomSheetVisible = true // true under development code
                                sheetState.expand()
                            }
                        }, disableEnable = if (profileData.kycStatus == "Approve") {
                            profileData.schoolName == "0" || profileData.schoolName == if (languageData[LanguageTranslationsResponse.KEY_ENTER_SCHOOL_NAME].toString() == "") {
                                "Enter school name"
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_ENTER_SCHOOL_NAME].toString()
                            }
                        } else {
                            println("District data :- ${districtSelectedId.value}, ${district.value}")
                            if (districtSelectedId.value == -1) {
                                false
                            } else {
                                true
                            }
                        }

                            /* if (profileData.schoolName != "0") {
                                 profileData.kycStatus != "Approve"
                             } else {
                                 true
                             }*/
                        )
                    }

                    Text(
                        if (languageData[LanguageTranslationsResponse.KEY_SCHOOL_BOARD].toString() == "") {
                            stringResource(R.string.school_board)
                        } else {
                            languageData[LanguageTranslationsResponse.KEY_SCHOOL_BOARD].toString()
                        }, modifier = Modifier.padding(top = 10.dp)
                    )

                    DropdownMenuUi(boardList.map { it.name }.toMutableList(),
                        onItemSelected = { selected ->
                            boardList.find { it.name == selected }?.id?.let {
                                boardSelectId.value = it
                            }
                        },
                        modifier = Modifier.clickable {

                        },
                        placeholder = board.value,
                        onClick = {
                            schoolListOpen = true
                        })

                    Text(
                        buildAnnotatedString {
                            append(
                                if (languageData[LanguageTranslationsResponse.SCHOOL_MED].toString() == "") {
                                    stringResource(R.string.school_medium)
                                } else {
                                    languageData[LanguageTranslationsResponse.SCHOOL_MED].toString()
                                }
                            )
                            pushStyle(SpanStyle(color = Color.Red))
                            append(" *")
                            pop()
                        }, modifier = Modifier.padding(top = 10.dp)
                    )
                    DropdownMenuUi(mediumList.map { it.name }.toMutableList(),
                        onItemSelected = { selected ->
                            mediumList.find { it.name == selected }?.id?.let {
                                mediumSelectId.value = it
                            }
                        },
                        placeholder = medium,
                        onClick = {})

                    if (Constants.grade != null) {
                        if (Constants.grade!!.toInt() > 10) {
                            Text(
                                buildAnnotatedString {
                                    append(stringResource(R.string.txt_quiz_subject))
                                    pushStyle(SpanStyle(color = Color.Red))
                                    append(" *")
                                    pop()
                                },
                                modifier = Modifier.padding(top = 10.dp)
                            )
                            val displayText: String = try {
                                viewModel.getStudentSubject().filter { it!!.isSelected }
                                    .joinToString(", ") { it!!.subjectName }
                            } catch (e:Exception){
                                ""
                            }

                            val truncatedText = if (displayText.length > 10) {
                                displayText.take(20) + "..."
                            } else {
                                displayText
                            }
                            DropdownStudentSubjectUi(
                                viewModel.getStudentSubject(),
                                onItemSelected = {
                                    Constants.grade = it
                                },
                                placeholder = truncatedText,
                                icon = painterResource(R.drawable.ic_down),
                                onClick = {
                                    navController.navigate(AppRoute.SelectSubjectPreference())
                                },
                                disableEnable = true
                            )
                        }
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.navigate(AppRoute.StudentProfile.route) },

                    modifier = Modifier
                        .background(color = White)
                        .padding(top = 5.dp)
                        .weight(1f)

                        .fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                        containerColor = White
                    )
                ) {
                    Text(
                        modifier = Modifier
                            .background(color = White)
                            .fillMaxWidth(),
                        text = languageData[LanguageTranslationsResponse.KEY_CANCEL].toString(),
                        color = PrimaryBlue,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
//                val student by viewModel.student.observeAsState(initial = AddStudent())
                var errorMsg: String = stringResource(id = R.string.error_mandatory_field)
                TextButton(
                    onClick = {
                    if (updateButtonClicked) {
                        val userTypeId = viewModel.getUserType()
                        val userId = viewModel.getUserId()
                        val onBoarding1 = viewModel.student.value
                        onBoarding1?.name = Constants.name
                        onBoarding1?.userName = Constants.userName
                        onBoarding1?.email = Constants.email
                        onBoarding1?.gender =
                            if (Constants.gender.equals("Male") || Constants.gender.equals(
                                    "M"
                                )
                            ) {
                                "M"
                            } else if (Constants.gender.equals("Female") || Constants.gender.equals(
                                    "F"
                                )
                            ) {
                                "F"
                            } else {
                                "O"
                            }
                        onBoarding1?.dob = Constants.dateOfBirth
                        onBoarding1?.grade = Constants.grade
                        onBoarding1?.language = "1"
                        onBoarding1?.alternativePhone = "0"
                        onBoarding1?.board = boardSelectId.value.toString()
                        if (selectedSchool == languageData[LanguageTranslationsResponse.OTHER].toString()) {
                            onBoarding1?.school = ""
                            onBoarding1?.pinCode = pincode.value
                            onBoarding1?.otherSchool = new_school_name.value
                        } else {
                            onBoarding1?.otherSchool = ""
                            onBoarding1?.pinCode = ""
                            onBoarding1?.school = schoolSelectedId.value.toString()
                        }
                        onBoarding1?.state = stateSelectedId.value.toString()
                        onBoarding1?.district = districtSelectedId.value.toString()
                        println("Selected state is :- ${onBoarding1?.state}")
                        println("Selected district is :- ${onBoarding1?.district}")
                        println("Selected school is :- ${onBoarding1?.school}")
                        onBoarding1?.userTypeId = userTypeId
                        onBoarding1?.medium = mediumSelectId.value

                        if (onBoarding1!!.userName != null && onBoarding1.name != null && onBoarding1.email != null && !onBoarding1.gender.equals(
                                "0"
                            ) && onBoarding1.dob != null && onBoarding1.grade != null
                            && !(onBoarding1.school.equals("-1") || onBoarding1.school.equals("0"))
                            && !(onBoarding1.state.equals("-1") || onBoarding1.state.equals("0"))
                            && !(onBoarding1.district.equals("-1") || onBoarding1.district.equals(
                                "0"
                            ))
                            && (!onBoarding1.board.equals("-1") || !onBoarding1.board.equals("0"))
                            && onBoarding1.medium!! != 0
                        ) {
                            println("All update information :- $onBoarding1")
                            if (onBoarding1.grade!!.toInt() < 13) {
                                viewModel.updateStudent(onBoarding1)
                                // call api
                                viewModel.saveGrade(onBoarding1.grade.toString())
                                if (Constants.imageUrlPart != null) {
                                    studentViewModel.saveStudentProfile(
                                        userId!!.toInt(), onBoarding1, Constants.imageUrlPart
                                    )
                                } else {
                                    studentViewModel.saveStudentProfile(
                                        userId!!.toInt(), onBoarding1, image = null
                                    )
                                }
                                onNextClick()
                            } else {
                                context.toast("grade should be more than 12")
                            }
                        } else {
                            context.toast(errorMsg)
                        }
                    }
                    else {
                        context.toast("Please click the Next button for save details at previous page")
                    }
                },

                    modifier = Modifier
                        .background(color = PrimaryBlue,  shape = RoundedCornerShape(12.dp))
                        .padding(vertical = 5.dp, horizontal = 5.dp)
                        .weight(1f)
                        .height(40.dp)
                        .clickable {

                        }
                        .fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    )) {
                    Text(
                        modifier = Modifier
                            .background(color = PrimaryBlue)
                            .fillMaxWidth(),
                        text = if (languageData[LanguageTranslationsResponse.KEY_UPDATE].toString() == "") {
                            stringResource(id = R.string.txt_update)
                        } else {
                            languageData[LanguageTranslationsResponse.KEY_UPDATE].toString()
                        },
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}


@Composable
private fun ProfilerScreen(
    lists: GetCompleteProfilerQuestionOptionsResponseModel.Data,
    selectedQuestionList: MutableList<Question>,
    viewModel: LoginViewModel,
) {
    var iSelectedChanged by remember { mutableStateOf(false) }
    val selectedAnswers =
        remember { mutableStateMapOf<Int, MutableState<Int?>>() }  // Using Int? to store the single selected answer
    val selectedAnswersList = mutableListOf<Answer>()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 10.dp)
    ) {
        Text(
            text = lists.question.questionText,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 15.dp, end = 15.dp, top = 10.dp),
            textAlign = TextAlign.Start
        )/*lists.answers.let { selectedAns ->

        }*/
        lists.question.let { question ->

            // Initialize the selected answer for the current question if not already done
            val questionId = lists.question.id.toInt()
            val selectedAnswerState =
                rememberSaveable { selectedAnswers.getOrPut(questionId) { mutableStateOf(null) } }  // Store only one selected answer per question
//            var isSelected by remember { mutableStateOf(false) }

            lists.question.options.forEach { optionsData ->
                val answerId = optionsData.id.toInt()

                if (iSelectedChanged) {
                    optionsData.isSelected = selectedAnswerState.value == answerId
                } else {
                    optionsData.isSelected =
                        lists.answers.isNotEmpty() && optionsData.text == lists.answers[0].text
                }

                if (optionsData.isSelected) {
                    iSelectedChanged = true
                    val answer = Answer(
                        id = answerId,
                        text = optionsData.text // Assuming text is empty; modify as needed
                    )

                    // Clear previous selection and add the new one
                    selectedAnswerState.value = answerId  // Update selected answer for the question
                    selectedAnswersList.clear()
                    selectedAnswersList.add(answer)

                    // Update the question list with the new answer
                    val question = Question(
                        answer = selectedAnswersList,
                        id = questionId,
                        questionType = question.questionType
                    )
                    selectedQuestionList.removeAll { it.id == questionId }  // Remove old question
                    selectedQuestionList.add(question)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, bottom = 5.dp, top = 5.dp)
                        .clickable {
                            iSelectedChanged = true
                            val answer = Answer(
                                id = answerId,
                                text = optionsData.text // Assuming text is empty; modify as needed
                            )

                            // Clear previous selection and add the new one
                            selectedAnswerState.value =
                                answerId  // Update selected answer for the question
                            selectedAnswersList.clear()
                            selectedAnswersList.add(answer)

                            // Update the question list with the new answer
                            val question = Question(
                                answer = selectedAnswersList,
                                id = questionId,
                                questionType = question.questionType
                            )
                            selectedQuestionList.removeAll { it.id == questionId }  // Remove old question
                            selectedQuestionList.add(question)
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = optionsData.isSelected/*if (isSelected){
                            true
                        }
                        else if (lists.answers.isNotEmpty()) {
                                if (optionsData.text == lists.answers[0].text) {
                                    true
                                } else {
                                    false
                                }
                            }
                        else{
                            false
                        }*/, onClick = null, // Handled by the parent clickable
                        enabled = true
                    )

                    Text(
                        text = optionsData.text,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 10.dp),
                        color = GrayLight01,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(vertical = 10.dp, horizontal = 20.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    color = GrayLight02,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 0.8.dp.toPx()
                )
            }
        }
    }
}

@Composable
private fun OpenBottomButton() {
    Box(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = Color.White),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {},
                modifier = Modifier
                    .background(color = PrimaryBlue, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp, horizontal = 5.dp)
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.txt_update),
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = {},
                modifier = Modifier
                    .background(color = PrimaryBlue)
                    .padding(vertical = 8.dp, horizontal = 5.dp)
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.txt_update),
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun EditProfilePreview() {
    val viewModel: LoginViewModel = hiltViewModel()
    var profileData by remember { mutableStateOf<StudentProfileResponseModel.ProfileData?>(null) }
//    OpenBottomButton()
//    ProfilerScreen()
}
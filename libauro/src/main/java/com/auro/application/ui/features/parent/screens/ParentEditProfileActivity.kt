package com.auro.application.ui.features.parent.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.api.aes.AESEncryption.decrypt
import com.auro.application.data.api.aes.AESEncryption.encryptAES
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.DropdownMenuUi
import com.auro.application.ui.common_ui.GenderSelection
import com.auro.application.ui.common_ui.InputTextField
import com.auro.application.ui.common_ui.TextFieldWithIcon
import com.auro.application.ui.common_ui.showDatePickerDialog
import com.auro.application.ui.common_ui.showParentDatePickerDialog
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.screens.models.GetDistrictResponseModel
import com.auro.application.ui.features.login.screens.models.GetStateListResponseModel
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.ParentDashboardActivity
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionAnswersSubmitRequestModel
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionAnswersSubmitRequestModel.Question
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionAnswersSubmitRequestModel.Question.Answer
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionOptionsResponseModel
import com.auro.application.ui.features.parent.model.ParentProfileData
import com.auro.application.ui.features.parent.model.UpdateParentProfileResponseModel
import com.auro.application.ui.features.parent.viewmodel.ParentViewModel
import com.auro.application.ui.features.student.menu.TabItem
import com.auro.application.ui.features.student.screens.isEncrypted
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@SuppressLint("NewApi")
@Composable
fun ParentEditProfileActivity(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val viewModel: LoginViewModel = hiltViewModel()  // pass these in fun
    val parentViewModel: ParentViewModel = hiltViewModel()
    val studentViewModel: StudentViewModel = hiltViewModel()
    val sharedPref: SharedPref? = null
    var isDialogVisible by remember { mutableStateOf(false) }
    var parentProfileData by remember { mutableStateOf<ParentProfileData?>(null) }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    // Use state variables for the lists
    val stateList = remember { mutableStateListOf<GetStateListResponseModel.Data.Result>() }
    val districtList = remember { mutableStateListOf<GetDistrictResponseModel.Data>() }
    val questionAnswerModelList =
        remember { mutableStateListOf<GetCompleteProfilerQuestionOptionsResponseModel.Data>() }

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()
    CustomDialog(
        isVisible = isDialogVisible, onDismiss = { isDialogVisible = false },
//        message = "Loading your data..."
        message = msgLoader
    )
    parentProfileData = viewModel.getParentProfileData()
    LaunchedEffect(Unit) {
        viewModel.getStateListResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    stateList.clear()
                    districtList.clear()
                    it.data?.data.let {
                        it?.let { it1 -> stateList.addAll(it1.results) }
                        println("State list of country :-  $it")
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
            }
        }

        viewModel.getDistrictResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    districtList.clear()
                    districtList.addAll(it.data?.data?.toMutableList() ?: mutableListOf())
                    println("District list of country :-  $it")
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
            }
        }

        parentViewModel.getCompleteProfilerQuestionAndOptionsModelLiveData.observeForever { networkStatus ->
            when (networkStatus) {
                is NetworkStatus.Idle -> {
                    isDialogVisible = false
                    Log.e("TAG", "QuizPerformanceScreen: loading")
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                    Log.e("TAG", "QuizPerformanceScreen: loading")
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
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
                    isDialogVisible = false
                    // Handle the error case, e.g., show a message to the user
                }
            }
        }

        parentViewModel.getStudentProfilerQuestionAndOptionsModelLiveData.observeForever { networkStatus ->
            when (networkStatus) {
                is NetworkStatus.Idle -> {
                    isDialogVisible = false
                    Log.e("TAG", "QuizPerformanceScreen: loading")
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                    Log.e("TAG", "QuizPerformanceScreen: loading")
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false

                    // Clear the existing list before adding new questions
                    /* viewModel.removeStudentProfilerData()
                     context.toast(networkStatus.data?.data.toString())
                     navController.navigate(AppRoute.ParentProfile.route)*/
                    val intent = Intent(context, ParentDashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.also {
                        it.putExtra("", true)
                    }
                    context.startActivity(intent)
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    // Handle the error case, e.g., show a message to the user
                }
            }
        }

        // update parent Response
        parentViewModel.updateParentProfileResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        Toast.makeText(context, it.data.data.toString(), Toast.LENGTH_SHORT).show()
                        parentViewModel.getParentProfile()

                    } else {
                        Toast.makeText(context, it.data?.error.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                is NetworkStatus.Error -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    isDialogVisible = false
                }
            }
        }
        parentViewModel.parentProfileResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        viewModel.saveUserName(it.data.data.name!!)
                        viewModel.saveUserEmail(it.data.data.email!!)
                        viewModel.saveUserImage("")
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
            }
        }

        viewModel.getStateList()
    }

    AuroscholarAppTheme {

        val tabItems = listOf(
            TabItem(
//                title = stringResource(id = R.string.txt_personal_details)
                title = if (languageData[LanguageTranslationsResponse.KEY_PERSONAL_DETAILS].toString() == "") {
                    stringResource(id = R.string.txt_personal_details)
                } else {
                    languageData[LanguageTranslationsResponse.KEY_PERSONAL_DETAILS].toString()
                }
            ),
            TabItem(
//                title = stringResource(id = R.string.professional_detail)
                title = if (languageData[LanguageTranslationsResponse.KEY_PROFESSIONAL_DETAILS].toString() == "") {
                    stringResource(id = R.string.professional_detail)
                } else {
                    languageData[LanguageTranslationsResponse.KEY_PROFESSIONAL_DETAILS].toString()
                }
            ),
            TabItem(
//                title = stringResource(id = R.string.txt_profiler)
                title = if (languageData[LanguageTranslationsResponse.KEY_PROFILER].toString() == "") {
                    stringResource(id = R.string.txt_profiler)
                } else {
                    languageData[LanguageTranslationsResponse.KEY_PROFILER].toString()
                }
            ),
        )

        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val pagerState = rememberPagerState { tabItems.size }

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

        Column(modifier = Modifier.fillMaxSize()) {
            /* Row(
                 modifier = Modifier
                     .padding(20.dp)
                     .fillMaxWidth()
             ) {
                 Image(
                     painter = painterResource(R.drawable.back_icon),
                     contentDescription = "logo",
                     modifier = Modifier
                         .clickable {
                             navController.popBackStack()
                             navController.navigateUp()
                         },
                     colorFilter = ColorFilter.tint(Black)
                 )
                 Text(
                     text = stringResource(id = R.string.txt_edit_profile),
                     modifier = Modifier
                         .wrapContentSize()
                         .weight(1f)
                         .padding(start = 20.dp),
                     fontStyle = FontStyle.Normal,
                     fontWeight = FontWeight.Bold,
                     fontSize = 18.sp,
                     color = Black,
                     textAlign = TextAlign.Start
                 )
             }*/

            TabRow(selectedTabIndex = selectedTabIndex) {
                tabItems.forEachIndexed { index, tabItem ->
                    Tab(selected = index == selectedTabIndex, onClick = {
                        if (index != 1) {
                            selectedTabIndex = index
                        } else {
                            context.toast("Please update Personal Details!")
                        }
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
                    }, modifier = Modifier.background(color = Color.White)
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                var updateButtonClicked by remember { mutableStateOf(false) }
                HorizontalPager(
                    state = pagerState, modifier = Modifier.fillMaxSize()

                ) { index ->
                    when (index) {
                        0 -> {
                            PersonalDetailsScreen(
                                navController,
                                parentProfileData!!,
                                viewModel,
                                languageData,
                                stateList,
                                districtList,
                                context
                            ) {
                                updateButtonClicked = true
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index + 1)
                                }
                            }
                        }

                        1 -> {
                            ProfessionalDetails(
                                updateButtonClicked = updateButtonClicked,
                                parentProfileData!!,
                                navController,
                                viewModel,
                                languageData
                            ) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index + 1)
                                }
                            }
                        }

                        else -> {
                            ViewProfiler(
                                context,
                                navController,
                                viewModel,
                                parentViewModel,
                                languageData = languageData,
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
fun PersonalDetailsScreen(
    navController: NavHostController = rememberNavController(),
    profileData: ParentProfileData,
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    stateList: SnapshotStateList<GetStateListResponseModel.Data.Result>,
    districtList: SnapshotStateList<GetDistrictResponseModel.Data>,
    context: Context,
    onNextClick: () -> Unit,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var isDialogVisible by remember { mutableStateOf(false) }
    if (profileData.name.toString() == "null") {
        profileData.name = ""
    }
    val parentName = remember { mutableStateOf(profileData.name.toString()) }
    val phoneNumber = remember { mutableStateOf(decrypt(profileData.phone!!)) }

    val isEncrypted = remember { isEncrypted(profileData.email!!) }
    val emailId = remember {
        mutableStateOf(
            if (isEncrypted) {
                decrypt(profileData.email!!.toString())
            } else {
                profileData.email.toString()
            }
        )
    }

    if (profileData.gender != null) {
        when (profileData.gender) {
            "Male", "M" -> {
                profileData.gender = "Male"
            }

            "Female", "F" -> {
                profileData.gender = "Female"
            }

            else -> {
                profileData.gender = "Others"
//            profileData.gender = languageData[LanguageTranslationsResponse.OTHER].toString()
            }
        }
    } else {
        println("data getting null value...")
    }

    val selectedGender = remember { mutableStateOf(profileData.gender) }

    var date by remember { mutableStateOf(profileData.dob) }
//    var date by remember { mutableStateOf("") }
    val pincode = rememberSaveable { mutableStateOf(profileData.pincode) }
    var parentProfileData by remember { mutableStateOf<UpdateParentProfileResponseModel?>(null) }

    if (profileData.stateId == 0 || profileData.stateId == null) {
        0.also { profileData.stateId = it }
    }
    val stateSelectedId = remember { mutableIntStateOf(profileData.stateId!!.toInt()) }

    if (profileData.districtId == 0 || profileData.districtId == null) {
        profileData.districtId = 0
    }
    val districtSelectedId = remember { mutableIntStateOf(profileData.districtId!!.toInt()) }

    val stateName by remember { mutableStateOf(profileData.stateName) }
    var distrinName by remember { mutableStateOf(profileData.districtName) }

    val parentViewModel: ParentViewModel = hiltViewModel()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(horizontal = 15.dp, vertical = 10.dp)
            .border(0.5.dp, color = GrayLight02, shape = RoundedCornerShape(size = 10.dp)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 15.dp)
                    .fillMaxSize()
                    .verticalScrollbar(scrollState)
                    .verticalScroll(state = scrollState)
                    .background(color = Color.White),
            ) {
                Text(
//                    stringResource(R.string.ParentName),
                    text = buildAnnotatedString {
                        append(
                            if (languageData[LanguageTranslationsResponse.PARENTS_NAME].toString() != "" || languageData[LanguageTranslationsResponse.PARENTS_NAME].toString() != null) {
                                languageData[LanguageTranslationsResponse.PARENTS_NAME].toString()
                            } else {
                                stringResource(R.string.ParentName)
                            }
                        )
                        pushStyle(SpanStyle(color = Color.Red))
                        append(" *")
                        pop()
                    },
                    color = Gray,
                    fontStyle = FontStyle.Normal,
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    ),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 15.dp)
                )

                InputTextField(
                    modifier = Modifier.padding(top = 5.dp),
                    value = parentName,
                    if (languageData[LanguageTranslationsResponse.PARENTS_NAME].toString() == "") {
                        stringResource(R.string.ParentName)
                    } else {
                        languageData[LanguageTranslationsResponse.PARENTS_NAME].toString() + " *"
                    },
                    keyboardType = KeyboardType.Text
                )

                Text(
//                    stringResource(R.string.phonenumber),
                    text = buildAnnotatedString {
                        append(
                            if (languageData[LanguageTranslationsResponse.KEY_PHONE_NO].toString() != "") {
                                languageData[LanguageTranslationsResponse.KEY_PHONE_NO].toString()
                            } else {
                                stringResource(R.string.phonenumber)
                            }
                        )
                        pushStyle(SpanStyle(color = Color.Red))
                        append(" *")
                        pop()
                    },
                    color = Gray,
                    fontStyle = FontStyle.Normal,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 15.dp),
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    )
                )

                InputTextField(
                    modifier = Modifier.padding(top = 5.dp),
                    value = phoneNumber,
//                    stringResource(R.string.enter_mob_no),
                    languageData[LanguageTranslationsResponse.KEY_MOBILE_NO].toString(),
                    editable = false,
                    keyboardType = KeyboardType.Phone,
                    maxLength = 10
                )

                Text(
//                    stringResource(R.string.email),
                    text = languageData[LanguageTranslationsResponse.EMAIL_ID].toString(),
                    color = Gray,
                    fontStyle = FontStyle.Normal,
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    ),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )

                InputTextField(
                    modifier = Modifier.padding(top = 5.dp),
                    value = emailId,
//                    stringResource(R.string.enter_email_id),
                    languageData[LanguageTranslationsResponse.KEY_ENTER_MAIL_ID].toString(),
                    keyboardType = KeyboardType.Email
                )

                Text(
//                    stringResource(R.string.gender),
                    text = buildAnnotatedString {
                        append(
                            languageData[LanguageTranslationsResponse.GENDER].toString()
                        )
                        pushStyle(SpanStyle(color = Color.Red))
                        append(" *")
                        pop()
                    },
                    color = Gray,
                    fontStyle = FontStyle.Normal,
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    ),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )

                GenderSelection(
                    items = listOf(
                        "Male",
                        "Female",
                        "Others"
                    ), selectedItem = selectedGender
                ) { selected ->
                    // Handle the selected item here
                    println("Selected: $selected")
                }

                Text(
//                    stringResource(R.string.date_of_birth),
                    text = buildAnnotatedString {
                        append(
                            languageData[LanguageTranslationsResponse.DATE_OF_BIRTH].toString()
                        )
                        pushStyle(SpanStyle(color = Color.Red))
                        append(" *")
                        pop()
                    },
                    modifier = Modifier.padding(top = 10.dp),
                    color = Gray,
                    fontStyle = FontStyle.Normal,
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    ),
                    fontSize = 14.sp
                )
                val context = LocalContext.current
                TextFieldWithIcon(modifier = Modifier
                    .padding(vertical = 5.dp)
                    .clickable {
                        var selectedMonth: String = ""
                        var selectedDay: String = ""
                        showParentDatePickerDialog(context) { year, month, dayOfMonth ->
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

//                    value = remember { mutableStateOf(date) },
                    value = if (parentProfileData != null) {
                        remember { mutableStateOf(getFormattedDate(date)) }
                    } else {
                        remember { mutableStateOf(date) }
                    }, placeholder = if (date != null) {
                        getFormattedDate(date)
//                        date.ifEmpty { stringResource(R.string.select_date_of_birth) }
                    } else {
//                        stringResource(R.string.select_date_of_birth)
                        languageData[LanguageTranslationsResponse.KEY_SELECT_DOB].toString()
                    }
//                            getFormattedDate (date).ifEmpty { stringResource(R.string.select_date_of_birth) }
//                    placeholder = stringResource(R.string.select_date_of_birth)
                )
//                println("Date format :- ${getFormattedDate(date)}")
                Text(
                    text = buildAnnotatedString {
                        append(
                            languageData[LanguageTranslationsResponse.KEY_STATE].toString()
                        )
                        pushStyle(SpanStyle(color = Color.Red))
                        append(" *")
                        pop()
                    },
                    modifier = Modifier.padding(top = 10.dp),
                    color = Gray,
                    fontStyle = FontStyle.Normal,
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    ),
                    fontSize = 14.sp
                )

                DropdownMenuUi(stateList.map { it.name }.toList(), onItemSelected = { selected ->
                    districtList.clear()
                    distrinName = "Select City/District"
                    stateList.find { it.name == selected }?.id?.let {
                        viewModel.getDistrict(
                            it
                        )
                        stateSelectedId.value = it
                    }
                }, placeholder = if (stateName != "") {
                    stateName.toString()
                } else {
//                    "Select"
                    languageData[LanguageTranslationsResponse.KEY_SELECT].toString()
                }, onClick = {})

                Text(
                    text = buildAnnotatedString {
                        append(
                            languageData[LanguageTranslationsResponse.KEY_CITY_DIST].toString()
                        )
                        pushStyle(SpanStyle(color = Color.Red))
                        append(" *")
                        pop()
                    },
                    modifier = Modifier.padding(top = 10.dp),
                    color = Gray,
                    fontStyle = FontStyle.Normal,
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    ),
                    fontSize = 14.sp
                )

                DropdownMenuUi(districtList.map { it.name }.toList(), onItemSelected = { selected ->
                    pincode.value = ""
                    districtList.find { it.name == selected }?.id?.let {
                        districtSelectedId.value = it
                        distrinName = ""
                        distrinName = selected
                    }
                },
                    placeholder = distrinName.toString(),
//                  if (distrinName != "") {
//                    distrinName.toString()
//                } else {
//                   "Select"
//                    languageData[LanguageTranslationsResponse.KEY_SELECT].toString()
//                },
                    onClick = {})

                Text(
//                    stringResource(R.string.pin_code),
                    text = buildAnnotatedString {
                        append(
                            languageData[LanguageTranslationsResponse.KEY_PIN_CODE].toString()
                        )
                        pushStyle(SpanStyle(color = Color.Red))
                        append(" *")
                        pop()
                    },
                    color = Gray,
                    fontStyle = FontStyle.Normal,
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    ),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )

                InputTextField(
                    modifier = Modifier.padding(top = 5.dp),
                    value = pincode,
//                    stringResource(R.string.pin_code),
                    placeholder = languageData[LanguageTranslationsResponse.KEY_PIN_CODE].toString() + " *",
                    keyboardType = KeyboardType.Number,
                    maxLength = 6
                )
            }

            Box(
                modifier = Modifier
                    .padding(all = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(color = Color.White), contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
//                        text = stringResource(id = R.string.txt_cancel),
                        text = languageData[LanguageTranslationsResponse.KEY_CANCEL].toString(),
                        color = PrimaryBlue,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .background(color = White)
                            .padding(vertical = 8.dp, horizontal = 5.dp)
                            .weight(1f)
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(AppRoute.ParentProfile.route)
                            },
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Text(
//                        text = stringResource(id = R.string.next),
                        text = languageData[LanguageTranslationsResponse.NEXT].toString(),
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .background(
                                color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 8.dp, horizontal = 5.dp)
                            .weight(1f)
                            .fillMaxWidth()
                            .clickable(onClick = {

                                val onBoarding1 = parentViewModel.parent.value
                                onBoarding1?.gender = when (selectedGender.value) {
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
                                println("Selected gender :- ${onBoarding1?.gender}")
                                onBoarding1?.id = viewModel.getUserType()
                                onBoarding1?.userId = viewModel.getUserId()
                                onBoarding1?.name = parentName.value
                                onBoarding1?.imageUrl = ""
                                onBoarding1?.phone = phoneNumber.value
                                    .encryptAES()
                                    ?.trim()
                                if (emailId.value.isEmpty()) {
                                    onBoarding1?.email = ""
                                } else {
                                    onBoarding1?.email = emailId.value
                                        .encryptAES()
                                        ?.trim()
                                }
//                                onBoarding1?.gender = selectedGender.value
                                onBoarding1?.state = stateSelectedId.value
                                onBoarding1?.district = districtSelectedId.value
                                onBoarding1?.pinCode = pincode.value
                                onBoarding1?.dob = date
                                if (parentName.value.isEmpty()) {
                                    Toast
                                        .makeText(
                                            context,
                                            languageData[LanguageTranslationsResponse.KEY_ENTER_PARENT_NAME].toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else if (phoneNumber.value.isEmpty()) {
                                    Toast
                                        .makeText(
                                            context,
                                            languageData[LanguageTranslationsResponse.KEY_ENTER_PHONE_NO].toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }/* else if (emailId.value.isEmpty()) {
                                    Toast
                                        .makeText(
                                            context, "Please enter your mail.", Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }*/ else if (selectedGender.value.isEmpty()) {
                                    Toast
                                        .makeText(
                                            context,
                                            "Please enter your select your gender.",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else if (date.isEmpty()) {
                                    Toast
                                        .makeText(
                                            context,
                                            languageData[LanguageTranslationsResponse.KEY_PLS_ENTER_DOB].toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else if (stateSelectedId.equals(-1)) {
                                    Toast
                                        .makeText(
                                            context,
                                            "Please enter your select state.",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else if (districtSelectedId.equals(-1)) {
                                    Toast
                                        .makeText(
                                            context,
                                            languageData[LanguageTranslationsResponse.KEY_PLS_ENTER_YOUR_DISTRICT].toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else if (onBoarding1!!.state == -1) {
                                    Toast
                                        .makeText(
                                            context,
                                            languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_STATE].toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else if (distrinName.toString() == "Select City/District") {
                                    Toast
                                        .makeText(
                                            context,
                                            languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_DISTRICT].toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
//                                else if (onBoarding1!!.district == -1) {
//                                    Toast
//                                        .makeText(
//                                            context,
//                                            languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_DISTRICT].toString(),
//                                            Toast.LENGTH_SHORT
//                                        )
//                                        .show()
//                                }

                                else if (pincode.value.isEmpty()) {
                                    Toast
                                        .makeText(
                                            context,
                                            languageData[LanguageTranslationsResponse.KEY_PLS_ENTER_PIN_CODE].toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else {
                                    Log.d("Update data :- ", "" + onBoarding1)
                                    parentViewModel.updateParent(onBoarding1)
                                    onNextClick()
                                }
                            }),
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

fun getFormattedDate(timeInMillis: String): String {
//    val calender = Calendar.getInstance()
//    calender.timeInMillis = timeInMillis.toLong()
//    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
//    return dateFormat.format(calender.timeInMillis)

    val isoDateString = "2002-09-21T00:00:00.000Z"

    // Parse and format date using SimpleDateFormat
    val formattedDate = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = sdf.parse(timeInMillis)
        // Format the date to the desired pattern
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        outputFormat.format(date!!)
    } catch (e: Exception) {
        "Invalid Date"
    }

    return formattedDate.toString()
}

@Composable
fun Modifier.verticalScrollbar(
    state: ScrollState, scrollbarWidth: Dp = 6.dp, color: Color = Color.LightGray,
): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (state.isScrollInProgress) 1f else 0f,
        animationSpec = tween(400, delayMillis = if (state.isScrollInProgress) 0 else 700)
    )

    return this then Modifier.drawWithContent {
        drawContent()

        val viewHeight = state.viewportSize.toFloat()
        val contentHeight = state.maxValue + viewHeight

        val scrollbarHeight =
            (viewHeight * (viewHeight / contentHeight)).coerceIn(10.dp.toPx()..viewHeight)
        val variableZone = viewHeight - scrollbarHeight
        val scrollbarYoffset = (state.value.toFloat() / state.maxValue) * variableZone

        drawRoundRect(
            cornerRadius = CornerRadius(scrollbarWidth.toPx() / 2, scrollbarWidth.toPx() / 2),
            color = color,
            topLeft = Offset(this.size.width - scrollbarWidth.toPx(), scrollbarYoffset),
            size = Size(scrollbarWidth.toPx(), scrollbarHeight),
            alpha = alpha
        )
    }
}


@Composable
fun ProfessionalDetails(
    updateButtonClicked: Boolean,
    profileData: ParentProfileData,
    navController: NavHostController = rememberNavController(),
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    onNextClick: () -> Unit,
) {

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val parentViewModel: ParentViewModel = hiltViewModel()
    val userName = rememberSaveable { mutableStateOf(decrypt(profileData.username)) }
    val alternateNumber = rememberSaveable { mutableStateOf(profileData.alternativePhone) }
    var isDialogVisible by remember { mutableStateOf(false) }
    var numberSelected by rememberSaveable { mutableStateOf(viewModel.getChildCount().toString()) }

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
//        message = "Loading your data..."
        message = msgLoader
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(horizontal = 15.dp, vertical = 10.dp)
            .border(0.5.dp, color = GrayLight02, shape = RoundedCornerShape(size = 10.dp)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxSize()
                .verticalScrollbar(scrollState)
                .verticalScroll(state = scrollState)
                .background(color = Color.White),
        ) {

            Text(
//                stringResource(R.string.create_username),
                text = buildAnnotatedString {
                    append(
                        languageData[LanguageTranslationsResponse.CREATE_USERNAME].toString()
                    )
                    pushStyle(SpanStyle(color = Color.Red))
                    append(" *")
                    pop()
                },
                modifier = Modifier.padding(top = 10.dp),
                color = Gray,
                fontStyle = FontStyle.Normal,
                fontFamily = FontFamily(
                    Font(R.font.inter_medium, FontWeight.Medium)
                ),
                fontSize = 14.sp
            )

            InputTextField(
                modifier = Modifier.padding(top = 5.dp),
                value = userName,
//                stringResource(R.string.enter_your_username),
                languageData[LanguageTranslationsResponse.ENTER_UNAME].toString(),
                keyboardType = KeyboardType.Text
            )

            val data =
                languageData[LanguageTranslationsResponse.KEY_INSTRUCTION_FIRST].toString() + "\n" +
                        languageData[LanguageTranslationsResponse.KEY_INSTRUCTION_SECOND].toString() + "\n" +
                        languageData[LanguageTranslationsResponse.KEY_INSTRUCTION_THIRD].toString() + "\n" +
                        languageData[LanguageTranslationsResponse.KEY_INSTRUCTION_FOURTH].toString() + "\n" +
                        languageData[LanguageTranslationsResponse.KEY_INSTRUCTION_FIFTH].toString()
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
//                stringResource(R.string.numberOfChildren),
                text = buildAnnotatedString {
                    append(
                        if (languageData[LanguageTranslationsResponse.KEY_NO_CHILDREN].toString() == "") {
                            stringResource(id = R.string.numberOfChildren)
                        } else {
                            languageData[LanguageTranslationsResponse.KEY_NO_CHILDREN].toString()
                        }
                    )
                    pushStyle(SpanStyle(color = Color.Red))
                    append(" *")
                    pop()
                },
                modifier = Modifier.padding(top = 10.dp),
                color = Gray,
                fontStyle = FontStyle.Normal,
                fontFamily = FontFamily(
                    Font(R.font.inter_medium, FontWeight.Medium)
                ),
                fontSize = 14.sp
            )

            DropdownMenuUi(listOf("1", "2", "3", "4", "5"),
                onItemSelected = {
                    numberSelected = it
                },
                placeholder = numberSelected,
                icon = painterResource(R.drawable.ic_down),
                onClick = {})

            Text(
//                stringResource(R.string.alternateMobileNumber),
                if (languageData[LanguageTranslationsResponse.KEY_ALTERNATE_NO].toString() == "") {
                    stringResource(id = R.string.alternateMobileNumber)
                } else {
                    languageData[LanguageTranslationsResponse.KEY_ALTERNATE_NO].toString()
                },
                modifier = Modifier.padding(top = 10.dp),
                color = Gray,
                fontStyle = FontStyle.Normal,
                fontFamily = FontFamily(
                    Font(R.font.inter_medium, FontWeight.Medium)
                ),
                fontSize = 14.sp
            )

            InputTextField(
                modifier = Modifier.padding(top = 5.dp),
                value = alternateNumber,
//                stringResource(R.string.enterAlternateMobileNumber),
                languageData[LanguageTranslationsResponse.KEY_ENTER_ALTERNATE_NO].toString(),
                keyboardType = KeyboardType.Phone,
                maxLength = 10
            )
        }
        Box(
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = Color.White), contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
//                    text = stringResource(id = R.string.txt_cancel),
                    text = languageData[LanguageTranslationsResponse.KEY_CANCEL].toString(),
                    color = PrimaryBlue,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .background(color = White)
                        .padding(vertical = 8.dp, horizontal = 5.dp)
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(AppRoute.ParentProfile.route)
                        },
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold,
                )

                Text(
//                    text = stringResource(id = R.string.txt_update),
                    text = if (languageData[LanguageTranslationsResponse.KEY_UPDATE].toString() == "") {
                        stringResource(id = R.string.txt_update)
                    } else {
                        languageData[LanguageTranslationsResponse.KEY_UPDATE].toString()
                    },
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .background(
                            color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 8.dp, horizontal = 5.dp)
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable {
                            if (updateButtonClicked) {
                                if (parentViewModel.parent.value?.name!!.isEmpty()) {
                                    Toast
                                        .makeText(
                                            context,
                                            languageData[LanguageTranslationsResponse.KEY_ENTER_PARENT_NAME].toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else if (parentViewModel.parent.value?.phone!!.isEmpty()) {
                                    Toast
                                        .makeText(
                                            context,
                                            languageData[LanguageTranslationsResponse.KEY_ENTER_PHONE_NO].toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } /*else if (parentViewModel.parent.value?.email!!.isEmpty()) {
                                Toast
                                    .makeText(
                                        context, "Please enter your mail.", Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }*/ else if (parentViewModel.parent.value!!.gender!!.isEmpty()) {
                                    Toast
                                        .makeText(
                                            context,
                                            "Please enter your select your gender.",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else if (parentViewModel.parent.value?.dob!!.isEmpty()) {
                                    Toast
                                        .makeText(
                                            context,
                                            languageData[LanguageTranslationsResponse.KEY_PLS_ENTER_DOB].toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else if (parentViewModel.parent.value?.state!! == -1) {
                                    Toast
                                        .makeText(
                                            context,
                                            "Please enter your select state.",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else if (parentViewModel.parent.value?.district!! == -1) {
                                    Toast
                                        .makeText(
                                            context,
                                            languageData[LanguageTranslationsResponse.KEY_PLS_ENTER_YOUR_DISTRICT].toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else if (parentViewModel.parent.value?.pinCode!!.isEmpty()) {
                                    Toast
                                        .makeText(
                                            context,
                                            languageData[LanguageTranslationsResponse.KEY_PLS_ENTER_PIN_CODE].toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else if (userName.value.isEmpty()) {
                                    Toast
                                        .makeText(
                                            context,
                                            languageData[LanguageTranslationsResponse.KEY_PLS_ENTER_USERNAME].toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else if (numberSelected.isEmpty()) {
                                    Toast
                                        .makeText(
                                            context,
                                            languageData[LanguageTranslationsResponse.KEY_PLS_ENTER_STUDENT].toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else {
                                    parentViewModel.getUpdateParentProfile(
                                        viewModel
                                            .getUserType()
                                            .toString(),
                                        viewModel
                                            .getUserId()
                                            .toString(),
                                        parentViewModel.parent.value?.name.toString(),
                                        "",
                                        parentViewModel.parent.value!!.phone
                                            ?.encryptAES()
                                            ?.trim()
                                            .toString(),
                                        parentViewModel.parent.value?.email.toString(),
                                        parentViewModel.parent.value?.gender.toString(),
                                        parentViewModel.parent.value?.state.toString(),
                                        parentViewModel.parent.value?.district.toString(),
                                        parentViewModel.parent.value?.pinCode.toString(),
                                        parentViewModel.parent.value?.dob.toString(),
                                        userName.value
                                            .encryptAES()
                                            .toString(),
                                        numberSelected,
                                        alternateNumber.value.toString()
                                    )
                                    onNextClick()
                                }
                            } else {
                                context.toast("Click the Update button in the Personal Details tab to update your information.")
                            }
                        },
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold,
                )
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

            lists.question.options.forEach { optionsData ->
                val answerId = optionsData.id.toInt()

                if (iSelectedChanged) {
                    optionsData.isSelected = selectedAnswerState.value == answerId
                } else {
                    optionsData.isSelected =
                        lists.answers.isNotEmpty() && optionsData.text == lists.answers[0].text
                }
                if (optionsData.isSelected) {
                    iSelectedChanged = true/* val answer = Answer(
                         id = answerId,
                         text = optionsData.text // Assuming text is empty; modify as needed
                     )*/

                    // Clear previous selection and add the new one
                    selectedAnswerState.value = answerId  // Update selected answer for the question
                    selectedAnswersList.clear()
                    selectedAnswersList.add(
                        Answer(
                            id = answerId,
                            text = optionsData.text // Assuming text is empty; modify as needed
                        )
                    )

                    // Update the question list with the new answer
                    /*val question = Question(
                        answer = selectedAnswersList,
                        id = questionId,
                        questionType = question.questionType
                    )*/
                    selectedQuestionList.removeAll { it.id == questionId }  // Remove old question
                    selectedQuestionList.add(
                        Question(
                            answer = selectedAnswersList,
                            id = questionId,
                            questionType = question.questionType
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, bottom = 5.dp, top = 5.dp)
                        .clickable {
                            iSelectedChanged = true/* val answer = Answer(
                                 id = answerId,
                                 text = optionsData.text // Assuming text is empty; modify as needed
                             )*/

                            // Clear previous selection and add the new one
                            selectedAnswerState.value =
                                answerId  // Update selected answer for the question
                            selectedAnswersList.clear()
                            selectedAnswersList.add(
                                Answer(
                                    id = answerId, text = optionsData.text
                                )
                            )

                            // Update the question list with the new answer
                            /* val question = Question(
                                 answer = selectedAnswersList,
                                 id = questionId,
                                 questionType = question.questionType
                             )*/
                            selectedQuestionList.removeAll { it.id == questionId }  // Remove old question
                            selectedQuestionList.add(
                                Question(
                                    answer = selectedAnswersList,
                                    id = questionId,
                                    questionType = question.questionType
                                )
                            )
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = optionsData.isSelected,
                        onClick = null, // Handled by the parent clickable
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
fun ViewProfiler(
    context: Context,
    navController: NavHostController,
    viewModel: LoginViewModel,
    parentViewModel: ParentViewModel,
    languageData: HashMap<String, String>,
    questionAnswerModelList: SnapshotStateList<GetCompleteProfilerQuestionOptionsResponseModel.Data>,
) {
    val selectedQuestionList = remember { mutableListOf<Question>() }
//    val selectedAnswersList = remember { mutableListOf<Answer>() }

    var currentIndex by remember { mutableIntStateOf(0) }

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
                onClick = { navController.navigate(AppRoute.ParentProfile.route) },

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
//                    text = stringResource(id = R.string.txt_cancel),
                    text = languageData[LanguageTranslationsResponse.KEY_CANCEL].toString(),
                    color = PrimaryBlue,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
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

                        val intent = Intent(context, ParentDashboardActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.also {
                            it.putExtra("", true)
                        }
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier
                    .background(color = PrimaryBlue)
                    .padding(vertical = 5.dp, horizontal = 5.dp)
                    .weight(1f)
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

data class Question(
    val text: String, val options: List<Option>,
)

data class Option(
    val text: String, var isSelected: Boolean = false,
)
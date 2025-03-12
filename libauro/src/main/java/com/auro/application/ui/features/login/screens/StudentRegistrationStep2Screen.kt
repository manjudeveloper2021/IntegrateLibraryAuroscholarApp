package com.auro.application.ui.features.login.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import androidx.navigation.compose.rememberNavController
import com.auro.application.App
import com.auro.application.R
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.Constants
import com.auro.application.data.api.NetworkStatus
import com.auro.application.repository.models.GetLanguageListResponse
import com.auro.application.ui.common_ui.BtnNextUi
import com.auro.application.ui.common_ui.DropdownMenuUi
import com.auro.application.ui.common_ui.InputTextField
import com.auro.application.ui.common_ui.ProgressBarCompose
import com.auro.application.ui.common_ui.components.DoubleBackPressHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.screens.models.AddStudent
import com.auro.application.ui.features.login.screens.models.GetBoardListResponseModel
import com.auro.application.ui.features.login.screens.models.GetDistrictResponseModel
import com.auro.application.ui.features.login.screens.models.GetSchoolLIstResponseModel
import com.auro.application.ui.features.login.screens.models.GetStateListResponseModel
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.PrimaryBlueLt
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegistrationStep2Screen(
    navHostController: NavHostController, args: String?, viewModel: LoginViewModel,
) {
    val context = LocalContext.current
    var currentProgressBar by remember { mutableIntStateOf(9) }
    var schoolListOpen by remember { mutableStateOf(false) }
//    var isAddNewSchool by remember { mutableStateOf(false) }
    var isChildAdd by remember { mutableStateOf(false) }

    val student by viewModel.student.observeAsState(initial = AddStudent())

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    DoubleBackPressHandler {
        navHostController.navigateUp()
    }

    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })
    var isDialogVisible by remember { mutableStateOf(false) }

    // Clear response when the screen is disposed
    DisposableEffect(Unit) {
        onDispose {
//            isAddNewSchool = false
            viewModel.clearResponse()
        }
    }

    // Use state variables for the lists
    val stateList = remember { mutableStateListOf<GetStateListResponseModel.Data.Result>() }
    val stateSelectedId = remember { mutableIntStateOf(-1) }
    val districtList = remember { mutableStateListOf<GetDistrictResponseModel.Data>() }
    val districtSelectedId = remember { mutableIntStateOf(-1) }
    val schoolList = remember { mutableStateListOf<GetSchoolLIstResponseModel.Data>() }
    val schoolSelectedId = remember { mutableIntStateOf(-1) }
    val boardList = remember { mutableStateListOf<GetBoardListResponseModel.Data.Result>() }
    val boardSelectId = remember { mutableIntStateOf(-1) }
    val mediumList = remember { mutableStateListOf<GetLanguageListResponse.Data.Result>() }
    val mediumSelectId = remember { mutableIntStateOf(1) }
    val pincode = remember { mutableStateOf("") }
    val new_school_name = remember { mutableStateOf("") }


    var selectedSchool by remember {
        mutableStateOf("Select")
    }

    LaunchedEffect(Unit) {
        viewModel.mediumResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = false
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    mediumList.clear()
                    it.data?.data?.results?.let { it1 -> mediumList.addAll(it1) }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
//                    context.toast(it.message)
                }
            }
        }
        viewModel.fetchLanguages()
        viewModel.addChildResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (isChildAdd) {
                        isDialogVisible = false
                        navHostController.popBackStack()
                        Log.e("TAG", "OnboardingStep2:--- > userId " + it.data?.data?.userId)

                        it.data?.data?.userId.let {
                            viewModel.saveUserId(it)

                            Log.e("TAG", "OnboardingStep2: saved user id " + viewModel.getUserId())
                            navHostController.navigate(AppRoute.CreatePin(it))
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
//                    context.toast(it.message)
                }
            }
        }
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
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
//                    context.toast(it.message)
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
                    schoolList.clear()
                    Log.e(
                        "TAG",
                        "School List is her " + schoolList.size + " selected school name " + selectedSchool
                    )
                    districtList.addAll(it.data?.data?.toMutableList() ?: mutableListOf())
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
//                    context.toast(it.message)
                }
            }
        }
        viewModel.getSchoolListResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    schoolList.clear()
                    schoolList.addAll(it.data?.data?.toMutableList() ?: mutableListOf())
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
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

        viewModel.getStateList()
        viewModel.getBoardList()
    }

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()
    CustomDialog(
        isVisible = isDialogVisible, onDismiss = { isDialogVisible = false },
//        message = "Loading your data..."
        message = msgLoader
    )

    StudentRegisterBackground(isShowBackButton = true, onBackButtonClick = {
        if (!navHostController.popBackStack()) {
            // If unable to pop back (i.e., no more screens), finish the activity
            (context as? Activity)?.finish()
        }
    }, content = {


        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(painter = painterResource(R.drawable.logo_auro_scholar),
                        contentDescription = "logo",
                        modifier = Modifier
                            .width(100.dp)
                            .height(60.dp) // Set the size of the image
                            .align(Alignment.CenterHorizontally)
                            .padding(5.dp)
                            .background(Color.Unspecified)
                            .clickable {

                            })
                    Text(
//                            text = stringResource(id = R.string.welcome_to_auro_scholar),
                        text = languageData[LanguageTranslationsResponse.WELCOME_TO_AURO_SCHOLAR].toString(),
                        modifier = Modifier.fillMaxWidth(),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Black,
                        textAlign = TextAlign.Center
                    )


                    Text(
//                            stringResource(id = R.string.tell_us_more_about_yourself),
                        text = languageData[LanguageTranslationsResponse.TELL_US_ABOUT_YOURSELF].toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        color = GrayLight01,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Thin,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    ProgressBarCompose(currentProgressBar)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()

                        .weight(weight = 1f, fill = false)
                        .padding(all = 8.dp)
                ) {
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .verticalScrollbar(scrollState)
                            .verticalScroll(state = scrollState)
                            .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.Center) // Center the column horizontally and vertically
                        ) {

                            Text(
//                                    stringResource(R.string.state),
                                text = buildAnnotatedString {
                                    append(
                                        languageData[LanguageTranslationsResponse.STATE].toString()
                                    )
                                    pushStyle(SpanStyle(color = Color.Red))
                                    append(" *")
                                    pop()
                                },
                                modifier = Modifier.padding(top = 10.dp)
                            )

                            DropdownMenuUi(stateList.map { it.name }.toList(),
                                onItemSelected = { selected ->
                                    districtList.clear()
                                    districtSelectedId.value = -1
                                    schoolSelectedId.value = -1
                                    selectedSchool =
                                        languageData[LanguageTranslationsResponse.KEY_SELECT].toString()
                                    stateList.find { it.name == selected }?.id?.let {
                                        viewModel.getDistrict(
                                            it
                                        )
                                        stateSelectedId.value = it
                                    }
                                },
//                                    placeholder = "Select",
                                placeholder = languageData[LanguageTranslationsResponse.KEY_SELECT].toString(),
                                onClick = {
                                    Log.e(
                                        "TAG", "OnboardingStep2: state list " + stateList.size
                                    )
                                })

                            Text(
//                                    stringResource(R.string.city_district),
                                text = buildAnnotatedString {
                                    append(
                                        languageData[LanguageTranslationsResponse.CITY_DISTRICT].toString()
                                    )
                                    pushStyle(SpanStyle(color = Color.Red))
                                    append(" *")
                                    pop()
                                },
                                modifier = Modifier.padding(top = 10.dp)
                            )

                            DropdownMenuUi(districtList.map { it.name }.toMutableList(),
                                onItemSelected = { selected ->
                                    schoolList.clear()
//                                        selectedSchool = "Select"
                                    schoolSelectedId.value = -1
                                    selectedSchool =
                                        languageData[LanguageTranslationsResponse.KEY_SELECT].toString()
                                    districtList.find { it.name == selected }?.id?.let {
                                        viewModel.getSchoolList(it, "")
                                        districtSelectedId.value = it
                                    }
                                },
                                placeholder = languageData[LanguageTranslationsResponse.KEY_SELECT].toString(),
                                onClick = {})


                            Text(
//                                    stringResource(R.string.school_name),
                                text = buildAnnotatedString {
                                    append(
                                        languageData[LanguageTranslationsResponse.KEY_SCHOOL_NAME].toString()
                                    )
                                    pushStyle(SpanStyle(color = Color.Red))
                                    append(" *")
                                    pop()
                                },
                                modifier = Modifier.padding(top = 10.dp)
                            )

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
                                onTextSelected = { it ->
                                    selectedSchool = it
                                    schoolList.find { it.name == selectedSchool }?.id?.let {
                                        schoolSelectedId.value = it
                                    }
                                },
                                schoolList.map { it.name }.toList()
                            )

                            if (selectedSchool == /*"Other"*/ languageData[LanguageTranslationsResponse.OTHER].toString()) {
//                        Text(stringResource(R.string.other_school))
                                InputTextField(
                                    modifier = Modifier.padding(top = 5.dp),
                                    value = new_school_name,
//                                        stringResource(R.string.enter_school_name),
                                    languageData[LanguageTranslationsResponse.KEY_ENTER_SCHOOL_NAME].toString(),
                                    keyboardType = KeyboardType.Text
                                )

//                                    Text(stringResource(R.string.pin_code))
                                Text(text = buildAnnotatedString {
                                    append(
                                        languageData[LanguageTranslationsResponse.KEY_PIN_CODE].toString()
                                    )
                                    pushStyle(SpanStyle(color = Color.Red))
                                    append(" *")
                                    pop()
                                })

                                InputTextField(
                                    modifier = Modifier.padding(top = 5.dp),
                                    value = pincode,
//                                        stringResource(R.string.enter_pin_code),
                                    languageData[LanguageTranslationsResponse.KEY_ENTER_PIN_CODE].toString(),
                                    keyboardType = KeyboardType.Number,
                                    maxLength = 6
                                )
                            } else {
                                DropdownMenuUi(listOf(), onItemSelected = {

                                }, modifier = Modifier.clickable {
                                    Log.e("TAG", "SchoolName ")
                                }, placeholder = selectedSchool, onClick = {
                                    schoolListOpen = true
                                    scope.launch {
                                        isBottomSheetVisible = true // true under development code
                                        sheetState.expand()
                                    }
                                })
                            }

                            Text(
//                                    stringResource(R.string.school_board),
                                buildAnnotatedString {
                                    append(
                                        languageData[LanguageTranslationsResponse.KEY_SCHOOL_BOARD].toString()
                                    )
                                    pushStyle(SpanStyle(color = Color.Red))
                                    append(" *")
                                    pop()
                                },
                                modifier = Modifier.padding(top = 10.dp)
                            )
                            DropdownMenuUi(boardList.map { it.name }.toMutableList(),
                                onItemSelected = { selected ->
                                    boardList.find { it.name == selected }?.id?.let {
                                        boardSelectId.value = it
                                    }
                                },
                                modifier = Modifier.clickable {

                                },
                                placeholder = languageData[LanguageTranslationsResponse.KEY_SELECT].toString(),
                                onClick = {
                                    schoolListOpen = true
                                })

                            /*SchoolListBottomSheet(
                                isBottomSheetVisible = isBottomSheetVisible,
                                sheetState = sheetState,
                                onDismiss = {
                                    scope.launch { sheetState.hide() }
                                        .invokeOnCompletion { isBottomSheetVisible = false }
                                }, onDecline = {
                                    navHostController.navigate(AppRoute.RegistrationNotice("").route)
                                }, onTextSelected = {
                                    selectedSchool = it
                                    val foundSchool =
                                        schoolList.find { it.name == selectedSchool }

                                    if (foundSchool != null) {
                                        // School found, update the selected ID and set isAddNewSchool to false
                                        selectedSchool = foundSchool.name
                                        new_school_name.value = ""
                                        schoolSelectedId.value = foundSchool.id
                                    } else {
                                        // School not found, set isAddNewSchool to true and hide the bottom sheet
                                        selectedSchool = "Other"
                                        isBottomSheetVisible = false
                                        schoolListOpen = false
                                        schoolSelectedId.value = -1
                                    }

//                                        Log.e("TAG", "StudentRegistrationStep2Screen: data is her --->  "+isAddNewSchool )

                                },
                                schoolList.map { it.name }.toList()

                            )*/

                            Text(
//                                stringResource(R.string.medium),
                                buildAnnotatedString {
                                    append(
                                        languageData[LanguageTranslationsResponse.KEY_MEDIUM].toString()
                                    )
                                    pushStyle(SpanStyle(color = Color.Red))
                                    append(" *")
                                    pop()
                                },
                                modifier = Modifier.padding(top = 10.dp)
                            )

                            DropdownMenuUi(mediumList.map { it.name }.toMutableList(),
                                onItemSelected = { selected ->
                                    mediumList.find { it.name == selected }?.id?.let {
                                        mediumSelectId.value = it
                                    }
                                },
                                placeholder = languageData[LanguageTranslationsResponse.KEY_SELECT].toString(),
                                onClick = {})


                            Spacer(modifier = Modifier.size(60.dp))

                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.BottomCenter)
                    .background(color = Color.White)
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(0.dp)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row {
                    BtnNextUi(
                        title = /*stringResource(R.string.previous)*/ languageData[LanguageTranslationsResponse.PREVIOUS].toString(),
                        onClick = {
                            navHostController.navigateUp()
                        }
                    )

                    BtnNextUi(
                        title = /*stringResource(R.string.continues)*/ languageData[LanguageTranslationsResponse.CONT].toString(),
                        onClick = {
                            currentProgressBar = 10

                            val onBoarding1 = viewModel.student.value

                            if (onBoarding1 != null) {

// Assume these are your selected IDs
                                val boardId = boardSelectId.intValue
                                val schoolId = schoolSelectedId.intValue
                                val stateId = stateSelectedId.intValue
                                val districtId = districtSelectedId.intValue
                                val userTypeId = viewModel.getUserType()
                                val mediumId = mediumSelectId.intValue

// Check for -1 values
                                if (stateId == -1) {
                                    // Show error message
                                    Toast.makeText(
                                        context,
                                        languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_STATE].toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (districtId == -1) {
                                    // Show error message
                                    /*Toast.makeText(
                                        context,
                                        "Please select city/district",
                                        Toast.LENGTH_SHORT
                                    ).show()*/  Toast.makeText(
                                        context,
                                        languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_DISTRICT].toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (schoolId == -1 && new_school_name.value.isNullOrBlank()) {
                                    // Show error message
                                    Toast.makeText(
                                        context,
                                        languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_SCHOOL].toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (schoolId == -1 && !new_school_name.value.isNullOrBlank() && pincode.value.isNullOrBlank()) {
                                    // Show error message
                                    Toast.makeText(
                                        context,
                                        languageData[LanguageTranslationsResponse.KEY_SCHOOL_PIN_CODE].toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (boardId == -1) {
                                    // Show error message
                                    Toast.makeText(
                                        context,
                                        languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_BOARD].toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (mediumId == -1) {
                                    // Show error message
                                    Toast.makeText(
                                        context,
                                        languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_MEDIUM].toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    isDialogVisible = true
                                    // Assign values to onBoarding1 only if all checks are passed
                                    onBoarding1.board = boardId.toString()
                                    if (selectedSchool == languageData[LanguageTranslationsResponse.OTHER].toString()) {
                                        onBoarding1.school = ""
                                        onBoarding1.pinCode = pincode.value
                                        onBoarding1.otherSchool = new_school_name.value
                                    } else {
                                        onBoarding1.otherSchool = ""
                                        onBoarding1.pinCode = ""
                                        onBoarding1.school = schoolSelectedId.value.toString()
                                    }
                                    onBoarding1.state = stateId.toString()
                                    onBoarding1.district = districtId.toString()
                                    onBoarding1.userTypeId = userTypeId
                                    onBoarding1.medium = mediumId
                                    onBoarding1.pinCode = pincode.value

                                    viewModel.updateStudent(onBoarding1)
                                    println("Student data :- $onBoarding1")
                                    Log.d(
                                        "SchoolSelected:",
                                        "" + onBoarding1.school + ".. " + selectedSchool + " .. " + onBoarding1.otherSchool
                                    )

                                    // call api
                                    isChildAdd = true
                                    if (Constants.imageUrlPart != null) {
                                        viewModel.getAddChild(
                                            student, image = Constants.imageUrlPart!!
                                        )
                                    } else {
                                        viewModel.getAddChild(student, image = null)
                                    }
                                }
                            }
                        },
                        enabled = true
                    )
                }
            }
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolListBottomSheet(
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDecline: () -> Unit,
    onTextSelected: (String) -> Unit = { "" },
    myList: List<String>,
) {
//    var mySchoolList: List<String> = myList

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
            windowInsets = WindowInsets(0, 0, 0, 0).add(WindowInsets.ime)
        ) {
            var text by remember { mutableStateOf("") }
            val filteredList = remember(myList, text) {
                myList.filter { it.toString().contains(text, ignoreCase = true) }
            }

            Column(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .background(color = Color.White)
                    .windowInsetsPadding(WindowInsets.ime)
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .background(color = Color.White)
                        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = "Search Icon",
                        modifier = Modifier
                            .background(color = Color.White)
                            .padding()
                            .padding(10.dp)
                            .background(Color.Unspecified)
                    )
                    TextField(
                        value = text,
                        onValueChange = { newText ->
                            text = newText
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.White), // Set background color to white
                        placeholder = {
                            Text(
                                text = languageData[LanguageTranslationsResponse.KEY_SEARCH_SCHOOL].toString(),
                                color = Color.Gray
                            )
                        }, // Placeholder text color
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            cursorColor = Black,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
                val selectedItem = remember { mutableStateOf<String?>(null) }
                val filteredListWithOther =
                    filteredList + listOf(languageData[LanguageTranslationsResponse.OTHER].toString())

                LazyColumn {
                    items(filteredListWithOther) { item ->
                        val isSelected = selectedItem.value == item.toString()
                        Text(
                            text = item,
                            modifier = Modifier
                                .background(color = if (isSelected) PrimaryBlueLt else Color.White)
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable {
                                    selectedItem.value = item
                                    onTextSelected.invoke(item)
                                    onDismiss.invoke()
                                },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (myList.isEmpty()) {
            onTextSelected.invoke(languageData[LanguageTranslationsResponse.KEY_SELECT].toString())
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun search(onDecline: () -> Unit = {}, onDismiss: () -> Unit = {}) {
    var text by remember { mutableStateOf("") }
    var myList = listOf("sdsd", "sd", "Goa School", "Govt School", "New School")
    val filteredList = remember(myList, text) {
        myList.filter { it.toString().contains(text, ignoreCase = true) }
    }

    Column(modifier = Modifier.background(color = Color.White)) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .background(color = Color.White)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Search Icon",
                modifier = Modifier
                    .background(color = Color.White)
                    .padding()
                    .padding(10.dp)
                    .background(Color.Unspecified)
            )
            TextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White), // Set background color to white
                placeholder = {
                    Text(
                        "Search School ", color = Color.Gray
                    )
                }, // Placeholder text color
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    cursorColor = Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
        }
        val contex = LocalContext.current
        LazyColumn {
            items(filteredList) { item ->
                val index = filteredList.indexOf(item) // Get the index of the current item
                Text(
                    text = item.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            // Show a Toast with the index and item
                            Toast
                                .makeText(
                                    contex, "Item: $item, Index: $index", Toast.LENGTH_SHORT
                                )
                                .show()
                        },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

}

// Extension function to prepend an item to a List<String>
fun List<String>.prepend(item: String): List<String> {
    return listOf(item) + this
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun RegistatoinStep2() {
    StudentRegistrationStep2Screen(
        navHostController = rememberNavController(), args = null, viewModel = hiltViewModel()
    )
}
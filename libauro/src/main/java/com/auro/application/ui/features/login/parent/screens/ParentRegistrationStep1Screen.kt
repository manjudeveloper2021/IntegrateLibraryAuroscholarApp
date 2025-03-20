package com.auro.application.ui.features.login.parent.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.api.aes.AESEncryption.encryptAES
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.BtnNextUi
import com.auro.application.ui.common_ui.BtnTextUi
import com.auro.application.ui.common_ui.GenderSelection
import com.auro.application.ui.common_ui.InputTextField
import com.auro.application.ui.common_ui.ProgressBarCompose
import com.auro.application.ui.common_ui.TextFieldWithIcon
import com.auro.application.ui.common_ui.components.DoubleBackPressHandler
import com.auro.application.ui.common_ui.components.WalkThroughModel
import com.auro.application.ui.common_ui.components.WalkthroughDialog
import com.auro.application.ui.common_ui.showDatePickerDialog
import com.auro.application.ui.common_ui.showParentDatePickerDialog
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.screens.models.ParentModel
import com.auro.application.ui.features.login.screens.models.ParentWalkthroughRequest
import com.auro.application.ui.features.login.screens.navigateAndClean
import com.auro.application.ui.features.login.screens.verticalScrollbar
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentRegistrationStep1Screen(
    navHostController: NavHostController = rememberNavController(),
    intent: String?,
    sharedPref: SharedPref?,
    viewModel: LoginViewModel
) {
    val isShow = remember { mutableStateOf(sharedPref?.getParentOnboardingWalkthrough() ?: true) }
    val list = listOf<WalkThroughModel>(
        WalkThroughModel(
            "Register and Complete KYC",
            "Begin your learning journey by registering and completing your KYC on the Auroscholar App.",
            R.drawable.select_your_subject
        ), WalkThroughModel(
            "Take tailored Quizzes and Practice",
            "Attempt concept based quizzes for subjects of your grade and practice to improve your skills.",
            R.drawable.select_concepts
        ), WalkThroughModel(
            "Track Your Progress",
            "Attempt concept based quizzes for subjects of your grade and practice to improve your skills.",
            R.drawable.other_subject
        ), WalkThroughModel(
            "Win Scholarships",
            "Score 8 or higher marks in quizzes to qualify for Micro-scholarships. Keep striving for excellence!",
            R.drawable.add_more_concept
        )
    )
    if (!isShow.value) {
        val onBoarding1 = ParentWalkthroughRequest(1)
        viewModel.getParentWalkthrough(onBoarding1)
        WalkthroughDialog(showDialog = true, onDismissRequest = {
            isShow.value = true
            sharedPref?.saveDashboardWalkthrough(isShow.value)
        }, list)
    }
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)
    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable {
        mutableStateOf(
            sharedPref?.isParentNoticeViewed(
                null
            ) ?: false
        )
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })
    var isDialogVisible by remember { mutableStateOf(false) }
    var disclaimer = remember { mutableStateOf("") }

    var progress = remember {
        mutableStateOf(5)
    }
    val context = LocalContext.current
    DoubleBackPressHandler {
        if (!navHostController.popBackStack()) {
            // If unable to pop back (i.e., no more screens), finish the activity
            (context as? Activity)?.finish()
        }
    }
    var date by rememberSaveable { mutableStateOf("") }
    val selectedGender = rememberSaveable { mutableStateOf("") }
    val parentName = rememberSaveable { mutableStateOf("") }
    var username = rememberSaveable { mutableStateOf("") }
    var email = rememberSaveable { mutableStateOf("") }
    var isUserCheck by rememberSaveable { mutableStateOf(false) }
//    viewModel.saveScreenName(parentStep1)
    // Clear response when the screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getDisclaimerResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {

                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true

                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (sharedPref?.isParentNoticeViewed(null) == false) {
                        isBottomSheetVisible = true
                    }
                    disclaimer.value = it?.data?.data?.text.toString()

                }
            }
        }

        viewModel.getDisclaimerAcceptResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }

                is NetworkStatus.Idle -> {}

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    isBottomSheetVisible = false
                    sharedPref?.isParentNoticeViewed(true)
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

            }
        }

        viewModel.checkUsernameResponse.observeForever {
            when (it) {
                NetworkStatus.Idle -> {
                    isDialogVisible = false
                }

                NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        if (it.data.data!!.exists == 0) {
                            navHostController.navigate(AppRoute.ParentRegistrationStep2()) {
                                popUpTo(AppRoute.ParentRegistrationStep2()) { inclusive = true }
                            }

                        } else {
                            Log.d("API:", "else: " + it.data.error)
                            context.toast(it.data.error.toString())
                        }
                    } else {
                        context.toast(it.data?.error.toString())
                    }
                }

                is NetworkStatus.Error -> {
                    Log.d("API:", "error: " + it.message)
                    isDialogVisible = false
                    context.toast(it.message)
                }
            }
        }

        viewModel?.getUserType()?.let { userId ->
            userId.toInt() // api {"isSuccess":false,"error":"No disclaimer found","data":{}}
            viewModel.getDisclaimerById(userTypeId = 1)
        }
    }
    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()
    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
//        message = "Loading your data..."
        message = msgLoader
    )
    StudentRegisterBackground(
        modifier = Modifier.background(Color.White),
        isShowBackButton = true,
        onBackButtonClick = {
            navHostController.navigateUp()
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
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo_auro_scholar),
                            contentDescription = "logo",
                            modifier = Modifier
                                .width(100.dp)
                                .height(60.dp) // Set the size of the image
                                .align(Alignment.CenterHorizontally)
                                .padding(5.dp)
                                .background(Color.Unspecified)
                        )
                        Text(
//                            text = stringResource(id = R.string.welcome_to_auro_scholar),
                            text = if (languageData[LanguageTranslationsResponse.WELCOME_TO_AURO_SCHOLAR].toString() == "") {
                                stringResource(id = R.string.welcome_to_auro_scholar)
                            } else {
                                languageData[LanguageTranslationsResponse.WELCOME_TO_AURO_SCHOLAR].toString()
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Black,
                            textAlign = TextAlign.Center
                        )


                        Text(
//                            stringResource(id = R.string.tell_us_more_about_yourself),
                            text = if (languageData[LanguageTranslationsResponse.TELL_US_ABOUT_YOURSELF].toString() == "") {
                                stringResource(id = R.string.tell_us_more_about_yourself)
                            } else {
                                languageData[LanguageTranslationsResponse.TELL_US_ABOUT_YOURSELF].toString()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            color = GrayLight01,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Thin,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )


                        ProgressBarCompose(progress.value)
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
                            /* Column(
                                 modifier = Modifier
                                     .fillMaxWidth()
                                     .wrapContentSize(Alignment.Center) // Center the column horizontally and vertically
                             ) {
                                 Box(
                                     modifier = Modifier
                                         .wrapContentSize(Alignment.Center) // Center the box within the column
                                 ) {
                                     Box(
                                         modifier = Modifier
                                             .padding(10.dp) // Add padding to the outer box
                                     ) {
                                         Image(
                                             painter = painterResource(R.drawable.ic_profile),
                                             contentDescription = "logo",
                                             modifier = Modifier
                                                 .size(100.dp) // Add size modifier to make the image visible
                                                 .clip(RoundedCornerShape(100.dp)) // Add clip modifier to make the image circular
                                                 .background(color = White)
                                                 .border( // Add border modifier to make image stand out
                                                     width = 1.dp,
                                                     color = GrayLight02,
                                                     shape = CircleShape
                                                 )
                                                 .padding(10.dp)
                                         )
                                         Icon(
                                             painter = painterResource(R.drawable.ic_add_photo), // Replace with your edit pen icon
                                             contentDescription = "Edit",
                                             modifier = Modifier
                                                 .size(50.dp) // Adjust the size of the icon
                                                 .offset(
                                                     x = 75.dp, // Offset the icon to overlap the image
                                                     y = 25.dp
                                                 )
                                                 .padding(end = 10.dp),
                                             tint = Color.Unspecified
                                         )
                                     }
                                 }
                             }*/

//                            Text(stringResource(R.string.ParentName))
                            Text(
                                text = buildAnnotatedString {
                                    append(
                                        if (languageData[LanguageTranslationsResponse.PARENTS_NAME].toString() == "") {
                                            stringResource(R.string.ParentName)
                                        } else {
                                            languageData[LanguageTranslationsResponse.PARENTS_NAME].toString()
                                        }
                                    )
                                    pushStyle(SpanStyle(color = Color.Red))
                                    append(" *")
                                    pop()
                                }
                            )

                            InputTextField(
                                modifier = Modifier.padding(top = 5.dp),
                                value = parentName,
                                placeholder = if (languageData[LanguageTranslationsResponse.ENTER_NAME].toString() == "") {
                                    stringResource(R.string.enter_your_name)
                                } else {
                                    languageData[LanguageTranslationsResponse.ENTER_NAME].toString()
                                },
                                keyboardType = KeyboardType.Text
                            )

                            Text(
//                                stringResource(R.string.create_username),
                                text = buildAnnotatedString {
                                    append(
                                        if (languageData[LanguageTranslationsResponse.CREATE_USERNAME].toString() == "") {
                                            languageData[LanguageTranslationsResponse.CREATE_USERNAME].toString()
                                        } else {
                                            stringResource(R.string.create_username)
                                        }
                                    )
                                    pushStyle(SpanStyle(color = Color.Red))
                                    append(" *")
                                    pop()
                                },
                                modifier = Modifier.padding(top = 5.dp)
                            )

                            InputTextField(
                                modifier = Modifier.padding(top = 5.dp),
                                value = username,
                                placeholder = if (languageData[LanguageTranslationsResponse.ENTER_UNAME].toString() == "") {
                                    stringResource(R.string.enter_your_username)
                                } else {
                                    languageData[LanguageTranslationsResponse.ENTER_UNAME].toString()
                                },
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
                                color = GrayLight01,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 10.dp)
                            )

                            // email
                            Text(
//                                stringResource(R.string.email),
                                text = if (languageData[LanguageTranslationsResponse.EMAIL_ID].toString() == "") {
                                    stringResource(R.string.email)
                                } else {
                                    languageData[LanguageTranslationsResponse.EMAIL_ID].toString()
                                },
                                modifier = Modifier.padding(top = 5.dp)
                            )

                            InputTextField(
                                modifier = Modifier.padding(top = 5.dp),
                                value = email,
                                placeholder = if (languageData[LanguageTranslationsResponse.KEY_ENTER_MAIL_ID].toString() == "") {
                                    stringResource(R.string.enter_email_id)
                                } else {
                                    languageData[LanguageTranslationsResponse.KEY_ENTER_MAIL_ID].toString()
                                },
                                true,
                                keyboardType = KeyboardType.Email
                            )


                            Text(
//                                stringResource(R.string.gender),
                                text = buildAnnotatedString {
                                    append(
                                        if (languageData[LanguageTranslationsResponse.KEY_GENDER].toString() == "") {
                                            stringResource(R.string.gender)
                                        } else {
                                            languageData[LanguageTranslationsResponse.KEY_GENDER].toString()
                                        }
                                    )
                                    pushStyle(SpanStyle(color = Color.Red))
                                    append(" *")
                                    pop()
                                },
                                modifier = Modifier.padding(top = 5.dp)
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

                            // date  of birth
                            Text(
//                                stringResource(R.string.date_of_birth),
                                text = buildAnnotatedString {
                                    append(
                                        if (languageData[LanguageTranslationsResponse.KEY_DATE_OF_BIRTH].toString() == "") {
                                            stringResource(R.string.date_of_birth)
                                        } else {
                                            languageData[LanguageTranslationsResponse.KEY_DATE_OF_BIRTH].toString()
                                        }
                                    )
                                    pushStyle(SpanStyle(color = Color.Red))
                                    append(" *")
                                    pop()
                                },
                                modifier = Modifier.padding(top = 5.dp)
                            )

                            TextFieldWithIcon(
                                modifier = Modifier.clickable {
                                    showParentDatePickerDialog(context) { year, month, dayOfMonth ->
                                        date = "$year-${
                                            month.toString().padStart(2, '0')
                                        }-${dayOfMonth.toString().padStart(2, '0')}"
                                    }
                                },
                                value = remember { mutableStateOf(date) },
                                placeholder = if (date.isEmpty()) /*stringResource(R.string.select_date_of_birth)*/ languageData[LanguageTranslationsResponse.KEY_SELECT_DOB].toString() else date
                            )
                        }
                    }

                    Box(modifier = Modifier.height(65.dp)) {

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
                )
                {
                    BtnNextUi(onClick = {

                        var message: String? = null
                        if (date.isEmpty() && selectedGender.value.isEmpty() && parentName.value.isEmpty() && username.value.isEmpty() && email.value.isEmpty()) {
                            // Checkpoint 1: All fields are empty
//                            message =
//                                "All fields are empty. Please fill in the required information."
                            message =
                                languageData[LanguageTranslationsResponse.KEY_REQUIRED_ALL_FIELD].toString()
                            context.toast(message)
                        } else if (date.isEmpty()) {
                            // Checkpoint 2: Date is empty
                            message = "Date is required. Please select a date."
                            context.toast(message)
                        } else if (selectedGender.value.isEmpty()) {
                            // Checkpoint 3: Selected item is empty
//                            message = "Please select gender."
                            message =
                                languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_GENDER].toString()
                            context.toast(message)
                        } else if (parentName.value.isEmpty()) {
                            // Checkpoint 5: Student name is empty
                            message = "Parent's name is required. Please enter the student's name."
                            context.toast(message)
                        } else if (username.value.isEmpty()) {
                            // Checkpoint 6: Username is empty
//                            message = "Username is required. Please enter a username."
                            message =
                                languageData[LanguageTranslationsResponse.KEY_ENTER_REQUIRED_USERNAME].toString()
                            context.toast(message)
                        }
                        /*else if (email.value.isEmpty()) {
                            // Checkpoint 7: Email is empty
                            message = "Email is required. Please enter a valid email address."
                            context.toast(message)
                        }*/
                        else {
                            if (email.value.isEmpty()) {
                                email.value = ""
                            } else {
                                email.value =  email.value.toString() //email.value.encryptAES()?.trim().toString()
                            }

                            val grandchildRequestBody = ParentModel(
                                name = parentName.value,
                                userName = username.value.encryptAES()?.trim(),
                                email = email.value.encryptAES()?.trim().toString(),
                                gender = selectedGender.value.first().uppercase(),
                                dob = date,
                                state = "",
                                district = "",
                                pinCode = "",
                                alternativePhone = "", students = ""
                            )
                            viewModel.checkUsernameExiting(username.value, "add")
                            viewModel.saveParentDetail(grandchildRequestBody)
                            // move to next step
                        // navHostController.navigate(AppRoute.ParentRegistrationStep2())
                        }
                    })
                }
            }

//            val title: String = stringResource(R.string.last_updated)
            val title: String =
                languageData[LanguageTranslationsResponse.KEY_LAST_UPDATE].toString()

            BottomSheetNotice(
                isBottomSheetVisible = isBottomSheetVisible,
                sheetState = sheetState,
                onAccept = {
                    scope.launch { sheetState.hide() }
                        .invokeOnCompletion {
                            viewModel.getDisclaimerAccept(6)
                        }
                }, onDecline = {
                    Log.e("TAG", "ParentRegistrationStep1Screen:cancel ")
                    scope.launch {
                        sheetState.hide() // Ensure the sheet is hidden before navigating
                        navHostController.popBackStack() // Navigate back to the previous screen
                        isBottomSheetVisible = false // Update the visibility state
                    }
                }, title, disclaimer.value
            )
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetNotice(
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    title: String,
    disclaimer: String
) {

    val viewModel: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    if (isBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = onAccept,
            sheetState = sheetState,
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = null,
            scrimColor = Color.Black.copy(alpha = 0.5f),
         //   windowInsets = WindowInsets.ime
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(vertical = 15.dp, horizontal = 7.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_alert),
                        contentDescription = "sahsga",
                        alignment = Alignment.Center,
                        modifier = Modifier.background(Color.Unspecified)
                    )
                    Text(
                        title,
                        modifier = Modifier.padding(10.dp),
                        color = PrimaryBlue
                    )
                    Text(
                        disclaimer,
                        modifier = Modifier.padding(10.dp),
                        color = PrimaryBlue,
                        textAlign = TextAlign.Center,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        BtnTextUi(/*stringResource(R.string.i_decline)*/ languageData[LanguageTranslationsResponse.I_DECLINE].toString(),
                            color = Color.Red,
                            onClick = {
                                onDecline.invoke()
                            })
                        BtnNextUi(/*stringResource(R.string.i_accept)*/ languageData[LanguageTranslationsResponse.I_ACCEPT].toString(),
                            onClick = {
                                onAccept.invoke()
                            },
                            enabled = true
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

package com.auro.application.ui.features.login.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.auro.application.App
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.Constants
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.api.aes.AESEncryption.encryptAES
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.data.utlis.CommonFunction.getFileFromUri
import com.auro.application.data.utlis.CommonFunction.uriToByteArray
import com.auro.application.ui.common_ui.BtnNextUi
import com.auro.application.ui.common_ui.BtnTextUi
import com.auro.application.ui.common_ui.GenderSelection
import com.auro.application.ui.common_ui.DropdownMenuUi
import com.auro.application.ui.common_ui.ImagePickerScreen
import com.auro.application.ui.common_ui.InputTextField
import com.auro.application.ui.common_ui.ProgressBarCompose
import com.auro.application.ui.common_ui.TextFieldWithIcon
import com.auro.application.ui.common_ui.components.DoubleBackPressHandler
import com.auro.application.ui.common_ui.components.WalkThroughModel
import com.auro.application.ui.common_ui.components.WalkthroughDialog
import com.auro.application.ui.common_ui.showDatePickerDialog
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.screens.models.AddStudent
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import kotlinx.coroutines.launch


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegistrationStep1Screen(
    navHostController: NavHostController,
    intent: String?,
    sharedPref: SharedPref?,
    viewModel: LoginViewModel
) {

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    val (isShow, list) = WalkThroughSetup(sharedPref)
    if (isShow.value) {
        WalkthroughDialog(showDialog = true, onDismissRequest = {
            isShow.value = false
            sharedPref?.saveLoginWalkThrough(false)
        }, list)
    }
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })
    var isDialogVisible by remember { mutableStateOf(false) }

    DoubleBackPressHandler {
        if (!navHostController.popBackStack()) {
            // If unable to pop back (i.e., no more screens), finish the activity
            (context as? Activity)?.finish()
        }
    }
    var progress = remember {
        mutableStateOf(5)
    }
    var date by remember { mutableStateOf("") }
    val selectedGender = remember { mutableStateOf("") }
    var selectedGrade by remember { mutableStateOf("") }
    val studentName = remember { mutableStateOf("") }
    var username = remember { mutableStateOf("") }
    var email = remember { mutableStateOf("") }
    var showImagePicker by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

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

    if (showImagePicker) {
        ImagePickerScreen(onImagePicked = { uri ->
            imageUri = uri  // Set the selected image URI
            showImagePicker = false  // Hide the picker once the image is picked
        }, onDismiss = {
            showImagePicker = false  // Hide the picker on dismissal
        })
    }

    // Clear response when the screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.checkUsernameResponse.observeForever {
            when (it) {
                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
//                    navHostController.navigate(AppRoute.RegistrationStep2())
                }

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
                            navHostController.navigate(AppRoute.RegistrationStep2())
                        } else {
                            context.toast(it.data.error.toString())
                        }

                    } else {
                        context.toast(it.data?.error.toString())
                    }
                }
            }
        }
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
                        modifier = Modifier.fillMaxWidth(),
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
                        modifier = Modifier/*.border(
                                    width = 0.dp,
                                    color = GrayLight02,
                                    shape = RoundedCornerShape(10.dp)
                                )*/
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
                            Box(
                                modifier = Modifier.wrapContentSize(Alignment.Center) // Center the box within the column
                            ) {
                                Box(
                                    modifier = Modifier.padding(10.dp) // Add padding to the outer box
                                ) {
                                    Image(
                                        painter = if (imageUri != null) {
                                            rememberAsyncImagePainter(
                                                ImageRequest.Builder(LocalContext.current)
                                                    .data(imageUri)
                                                    .placeholder(R.drawable.ic_profile)
                                                    .error(R.drawable.ic_profile)
                                                    .crossfade(true) // Optional: Add a fade transition
                                                    .build()
                                            )
                                        } else {
                                            painterResource(R.drawable.ic_profile)
                                        },
                                        contentDescription = "logo",
                                        modifier = Modifier
                                            .size(100.dp) // Add size modifier to make the image visible
                                            .clip(CircleShape) // Add clip modifier to make the image circular
                                            .background(shape = CircleShape,color = White)
                                            .border( // Add border modifier to make image stand out
                                                width = 1.dp,
                                                color = GrayLight02,
                                                shape = CircleShape
                                            ),  contentScale = ContentScale.Crop


                                    )

                                    Icon(painter = painterResource(R.drawable.ic_add_photo), // Replace with your edit pen icon
                                        contentDescription = "Edit",
                                        modifier = Modifier
                                            .size(50.dp) // Adjust the size of the icon
                                            .offset(
                                                x = 75.dp, // Offset the icon to overlap the image
                                                y = 25.dp
                                            )
                                            .padding(end = 10.dp)
                                            .clickable {
                                                showImagePicker = true
                                            }
                                            .background(Color.Unspecified),
                                        tint = Color.Unspecified)
                                }
                            }
                        }

//                            Text(stringResource(R.string.student_name))
                        Text(
                            text = buildAnnotatedString {
                                append(
                                    if (languageData[LanguageTranslationsResponse.NAME_STUDENT].toString() == "") {
                                        stringResource(R.string.student_name)
                                    } else {
                                        languageData[LanguageTranslationsResponse.NAME_STUDENT].toString()
                                    }
                                )
                                pushStyle(SpanStyle(color = Color.Red))
                                append(" *")
                                pop()
                            }
                        )

                        InputTextField(
                            modifier = Modifier.padding(top = 5.dp),
                            value = studentName,
                            placeholder = if (languageData[LanguageTranslationsResponse.KEY_NAME_AS_PER_AADHAR].toString() == "") {
                                stringResource(R.string.enter_your_name_as_per_aadhar_name)
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_NAME_AS_PER_AADHAR].toString()
                            },
                            keyboardType = KeyboardType.Text
                        )

                        Text(
//                                stringResource(R.string.create_username),
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
                            modifier = Modifier.padding(top = 5.dp)
                        )

                        InputTextField(
                            modifier = Modifier.padding(top = 5.dp),
                            value = username,
//                                stringResource(R.string.enter_your_username),
                            placeholder = if (languageData[LanguageTranslationsResponse.ENTER_UNAME].toString() == "") {
                                stringResource(R.string.enter_your_username)
                            } else {
                                languageData[LanguageTranslationsResponse.ENTER_UNAME].toString()
                            },
                            keyboardType = KeyboardType.Text
                        )

                        val data =
                            languageData[LanguageTranslationsResponse.KEY_INSTRUCTION_FIRST].toString() + "\n" + languageData[LanguageTranslationsResponse.KEY_INSTRUCTION_SECOND].toString() + "\n" + languageData[LanguageTranslationsResponse.KEY_INSTRUCTION_THIRD].toString() + "\n" + languageData[LanguageTranslationsResponse.KEY_INSTRUCTION_FOURTH].toString() + "\n" + languageData[LanguageTranslationsResponse.KEY_INSTRUCTION_FIFTH].toString()

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
//                                stringResource(R.string.enter_email_id),
                            placeholder = if (languageData[LanguageTranslationsResponse.KEY_ENTER_MAIL_ID].toString() == "") {
                                stringResource(R.string.enter_email_id)
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_ENTER_MAIL_ID].toString()
                            },
                            true,
                            keyboardType = KeyboardType.Email
                        )

                        // grade
                        Text(
//                                stringResource(R.string.grade),
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
                            modifier = Modifier.padding(top = 5.dp)
                        )

                        DropdownMenuUi(listOf(
                            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
                        ),
                            onItemSelected = {
                                selectedGrade = it
                                sharedPref?.saveGrade(it)
                                Log.e(
                                    "TAG",
                                    "OnboardingStep1: shared prefred ---- > " + sharedPref?.getGrade()
                                )
                            },
//                                placeholder = "Select Grade",
                            placeholder = if (languageData[LanguageTranslationsResponse.KEY_SELECT_GRADE].toString() == "") {
                                "Select Grade"
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_SELECT_GRADE].toString()
                            },
                            icon = painterResource(R.drawable.ic_down),
                            onClick = {})

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
                                    if (languageData[LanguageTranslationsResponse.DATE_OF_BIRTH].toString() == "") {
                                        stringResource(R.string.date_of_birth)
                                    } else {
                                        languageData[LanguageTranslationsResponse.DATE_OF_BIRTH].toString()
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
                                showDatePickerDialog(context) { year, month, dayOfMonth ->
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
            ) {
                BtnNextUi(onClick = {
                    var message: String? = null/* if (date.isEmpty() && selectedGender.value.isEmpty() && selectedGrade.isEmpty() && studentName.value.isEmpty() && username.value.isEmpty() && email.value.isEmpty()) {
                             // Checkpoint 1: All fields are empty
                             message =
                                 "All fields are empty. Please fill in the required information."
                             context.toast(message)
                         } else*/
                    if (studentName.value.isEmpty()) {
                        // Checkpoint 5: Student name is empty
//                            message = "Student name is required. Please enter the student's name."
                        message =
                            languageData[LanguageTranslationsResponse.KEY_REQUIRED_STUDENT_NAME].toString()
                        context.toast(message)
                    } else if (username.value.isEmpty()) {
                        // Checkpoint 6: Username is empty
//                            message = "Username is required. Please enter a username."
                        message =
                            languageData[LanguageTranslationsResponse.KEY_ENTER_REQUIRED_USERNAME].toString()
                        context.toast(message)
                    } else if (selectedGrade.isEmpty()) {
                        // Checkpoint 4: Grade menu is empty
//                            message = "Please select a grade"
                        message =
                            languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_GRADE].toString()
                        context.toast(message)
                    } else if (selectedGender.value.isEmpty()) {
                        // Checkpoint 3: Selected item is empty
//                            message = "Please select gender."
                        message =
                            languageData[LanguageTranslationsResponse.KEY_PLS_SELECT_GENDER].toString()
                        context.toast(message)
                    } else if (date.isEmpty()) {
                        // Checkpoint 2: Date is empty
                        message = "Date is required. Please select a date."
                        context.toast(message)
                    } else {
                        val addchildRequestBody = AddStudent(
                            name = studentName.value,
                            userName = username.value.encryptAES()!!.trim().toString(),
                            email = email.value.encryptAES()?.trim(),
                            gender = selectedGender.value.first().uppercase(),
                            dob = date,
                            state = "",
                            district = "",
                            pinCode = "",
                            alternativePhone = "",
                            grade = selectedGrade,
                            board = "",
                            language = viewModel.getLanguageId(),
                            school = ""
                        )

                        var byteArray: ByteArray? = null
                        if (byteArray != null) {
                            byteArray = uriToByteArray(context, imageUri!!)
                            val file = getFileFromUri(context, imageUri)
                            Constants.imageUrlPart = file

                            viewModel.saveStudentDetail(addchildRequestBody)
                            isDialogVisible = true
                            viewModel.checkUsernameExiting(
                                username.value,
                                "add"
                            ) // check after login, add for new user
                        } else {
                            viewModel.saveStudentDetail(addchildRequestBody)
                            isDialogVisible = true
                            viewModel.checkUsernameExiting(
                                username.value,
                                "add"
                            ) // check after login, add for new user
                        }
                    }
                })
            }
        }

        BottomSheetNotice(navHostController,
            isBottomSheetVisible = isBottomSheetVisible,
            sheetState = sheetState,
            onDismiss = {
                scope.launch { sheetState.hide() }
                    .invokeOnCompletion { isBottomSheetVisible = false }
            },
            onDecline = {

                scope.launch { sheetState.hide() }
                    .invokeOnCompletion { isBottomSheetVisible = false }

                if (!navHostController.popBackStack()) {
                    // If unable to pop back (i.e., no more screens), finish the activity
                    (context as? Activity)?.finish()
                }
//                    (context as? Activity)?.finish()

            })
        LaunchedEffect(Unit) {
            scope.launch {
                isBottomSheetVisible = true
                sheetState.expand()
            }
        }
    })
}

@Composable
private fun WalkThroughSetup(sharedPref: SharedPref?): Pair<MutableState<Boolean>, List<WalkThroughModel>> {
    val isShow = remember { mutableStateOf(sharedPref?.getLoginWalkThrough() ?: true) }

    val viewModel: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    val list = listOf<WalkThroughModel>(
        WalkThroughModel(
//            "Register and Complete KYC",
            languageData[LanguageTranslationsResponse.REGISTER_AND_COMPLETE_KYC].toString(),
            languageData[LanguageTranslationsResponse.KEY_KYC_LEARNING_TEXT].toString(),
            R.drawable.select_your_subject
        ), WalkThroughModel(
            languageData[LanguageTranslationsResponse.TAKE_TAILORED_QUIZZES_AND_PRACTICE].toString(),
            languageData[LanguageTranslationsResponse.KEY_PRACTICE_CONCEPT_TEXT].toString(),
            R.drawable.select_concepts
        ), WalkThroughModel(
            languageData[LanguageTranslationsResponse.TRACK_YOUR_PROGRESS].toString(),
            languageData[LanguageTranslationsResponse.KEY_PRACTICE_CONCEPT_TEXT].toString(),
            R.drawable.other_subject
        ), WalkThroughModel(
            languageData[LanguageTranslationsResponse.WIN_SCHOLARSHIPS].toString(),
            languageData[LanguageTranslationsResponse.KEY_HIGHER_MICRO_SCHOLER_TEXT].toString(),
            R.drawable.add_more_concept
        )
    )
    return Pair(isShow, list)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetNotice(
    navHostController: NavHostController,
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDecline: () -> Unit
) {

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
//                        stringResource(R.string.last_updated),
                        text = languageData[LanguageTranslationsResponse.KEY_LAST_UPDATE].toString(),
                        modifier = Modifier.padding(10.dp),
                        color = PrimaryBlue
                    )
                    Text(
//                        stringResource(R.string.i_we_thereby_state_that_all_information_provided_about_my_child_is_true),
                        text = languageData[LanguageTranslationsResponse.KEY_I_WE_THEREBY_STATE_THAT_ALL_INFO].toString(),
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
                                onDismiss.invoke()
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


@Composable
@Preview(showBackground = true, showSystemUi = true)
fun RegistrationStep1() {
    StudentRegistrationStep1Screen(
        navHostController = rememberNavController(),
        null,
        sharedPref = null,
        viewModel = hiltViewModel()
    )
}

@Composable
fun Modifier.verticalScrollbar(
    state: ScrollState, scrollbarWidth: Dp = 6.dp, color: Color = Color.LightGray
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
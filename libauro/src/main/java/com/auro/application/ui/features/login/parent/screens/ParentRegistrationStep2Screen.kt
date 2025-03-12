package com.auro.application.ui.features.login.parent.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavHostController
import com.auro.application.R
import com.auro.application.core.ConstantVariables.SIGNUP_PIN
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.api.aes.AESEncryption.encryptAES
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.BtnNextUi
import com.auro.application.ui.common_ui.DropdownMenuUi
import com.auro.application.ui.common_ui.InputTextField
import com.auro.application.ui.common_ui.ProgressBarCompose
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.components.DoubleBackPressHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.screens.models.AddStudent
import com.auro.application.ui.features.login.screens.models.GetDistrictResponseModel
import com.auro.application.ui.features.login.screens.models.GetStateListResponseModel
import com.auro.application.ui.features.login.screens.verticalScrollbar
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01

@Composable
fun ParentRegistrationStep2Screen(
    navHostController: NavHostController,
    sharedPref: SharedPref,
    viewModel: LoginViewModel
) {

    val context = LocalContext.current
    var currentProgressBar by remember { mutableIntStateOf(9) }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

//    viewModel.saveScreenName(onboarding2)

    val student by viewModel.student.observeAsState(initial = AddStudent())

    BackHandler {
        navHostController.navigateUp()
    }

    var isDialogVisible by remember { mutableStateOf(false) }

    // Clear response when the screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    // Use state variables for the lists
    val stateList = remember { mutableStateListOf<GetStateListResponseModel.Data.Result>() }
    val stateSelectedId = remember { mutableIntStateOf(-1) }
    val districtList = remember { mutableStateListOf<GetDistrictResponseModel.Data>() }
    val districtSelectedId = remember { mutableIntStateOf(-1) }
    val childCountList = remember { mutableStateListOf<String>("1", "2", "3", "4", "5") }
    val childCountSelected = remember { mutableIntStateOf(-1) }
    var pincode = remember { mutableStateOf("") }
    var alternativeNumber = remember { mutableStateOf("") }
    var isRegistered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getParentResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }
                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (isRegistered) {
                        isRegistered = false
                        navHostController.popBackStack()
                        it.data?.data?.let {
                            if (viewModel.getUserPin()) {
                                navHostController.navigate(
                                    AppRoute.EnterPin(
                                        viewModel.getUserPhoneNo(),
                                        SIGNUP_PIN,
                                        viewModel.getUserId()
                                    )
                                )
                            } else {
                                Log.d("Parent1:", "" + "CreatePIN")
                                navHostController.navigate(AppRoute.CreatePin(viewModel.getUserId()))
                            }
                        }
                    }
                }
                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
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
                    context.toast(it.message)
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
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                }
            }
        }

        viewModel.getStateList()
    }


    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()
    CustomDialog(
        isVisible = isDialogVisible, onDismiss = { isDialogVisible = false },
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
                                            if (languageData[LanguageTranslationsResponse.STATE].toString() == "") {
                                                stringResource(R.string.state)
                                            } else {
                                                languageData[LanguageTranslationsResponse.STATE].toString()
                                            }
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
                                        stateList.find { it.name == selected }?.id?.let {
                                            viewModel.getDistrict(
                                                it
                                            )
                                            stateSelectedId.value = it
                                        }
                                    },
//                                    placeholder = "Select",
                                    placeholder = if (languageData[LanguageTranslationsResponse.KEY_SELECT].toString() == "") {
                                        "Select"
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_SELECT].toString()
                                    },
                                    onClick = {
                                        Log.e(
                                            "TAG", "OnboardingStep2: state list " + stateList.size
                                        )
                                    })

                                Text(
//                                    stringResource(R.string.city_district),
                                    text = buildAnnotatedString {
                                        append(
                                            if (languageData[LanguageTranslationsResponse.KEY_CITY_DIST].toString() == "") {
                                                stringResource(R.string.city_district)
                                            } else {
                                                languageData[LanguageTranslationsResponse.KEY_CITY_DIST].toString()
                                            }
                                        )
                                        pushStyle(SpanStyle(color = Color.Red))
                                        append(" *")
                                        pop()
                                    },
                                    modifier = Modifier.padding(top = 10.dp)
                                )

                                DropdownMenuUi(districtList.map { it.name }.toMutableList(),
                                    onItemSelected = { selected ->
                                        districtList.find { it.name == selected }?.id?.let {
                                            viewModel.getSchoolList(it, "")
                                            districtSelectedId.value = it
                                        }
                                    },
//                                    placeholder = "Select",
                                    placeholder = if (languageData[LanguageTranslationsResponse.KEY_SELECT].toString() == "") {
                                        "Select"
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_SELECT].toString()
                                    },
                                    onClick = {})

                                // pincode
                                Text(
//                                    stringResource(R.string.pin_code),
                                    text = buildAnnotatedString {
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
                                    },
                                    modifier = Modifier.padding(top = 5.dp)
                                )


                                InputTextField(
                                    modifier = Modifier.padding(top = 5.dp),
                                    value = pincode,
                                    placeholder = if (languageData[LanguageTranslationsResponse.KEY_ENTER_PIN_CODE].toString() == "") {
                                        stringResource(R.string.enter_pincode)
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_ENTER_PIN_CODE].toString()
                                    },
                                    true,
                                    keyboardType = KeyboardType.Number,
                                    maxLength = 6
                                )

                                Text(
//                                    stringResource(R.string.numberOfChildren),
                                    text = buildAnnotatedString {
                                        append(
                                            if (languageData[LanguageTranslationsResponse.KEY_NO_CHILDREN].toString() == "") {
                                                stringResource(R.string.numberOfChildren)
                                            } else {
                                                languageData[LanguageTranslationsResponse.KEY_NO_CHILDREN].toString()
                                            }
                                        )
                                        pushStyle(SpanStyle(color = Color.Red))
                                        append(" *")
                                        pop()
                                    },
                                    modifier = Modifier.padding(top = 10.dp)
                                )

                                DropdownMenuUi(childCountList.toMutableList(),
                                    onItemSelected = { selected ->
                                        childCountSelected.value = selected.toInt()
                                    },
//                                    placeholder = "Select",
                                    placeholder = if (languageData[LanguageTranslationsResponse.KEY_SELECT].toString() == "") {
                                        "Select"
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_SELECT].toString()
                                    },
                                    onClick = {})

                                // pincode
                                Text(
//                                    stringResource(R.string.alternateMobileNumber),
                                    text = if (languageData[LanguageTranslationsResponse.KEY_ALTERNATE_NO].toString() == "") {
                                        stringResource(R.string.alternateMobileNumber)
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_ALTERNATE_NO].toString()
                                    },
                                    modifier = Modifier.padding(top = 5.dp)
                                )

                                InputTextField(
                                    modifier = Modifier.padding(top = 5.dp),
                                    value = alternativeNumber,
//                                    stringResource(R.string.enterAlternateMobileNumber),
                                    placeholder = if (languageData[LanguageTranslationsResponse.KEY_ENTER_ALTERNATE_NO].toString() == "") {
                                        stringResource(R.string.enterAlternateMobileNumber)
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_ENTER_ALTERNATE_NO].toString()
                                    },
                                    true,
                                    keyboardType = KeyboardType.Phone,
                                    maxLength = 10
                                )

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
                        BtnNextUi(title = /*stringResource(R.string.previous)*/ languageData[LanguageTranslationsResponse.PREVIOUS].toString(),
                            onClick = {
                                navHostController.navigate(AppRoute.ParentRegistrationStep1())
                           //     navHostController.navigateUp()
                            })

                        BtnNextUi(
                            title = /*stringResource(R.string.continues)*/ languageData[LanguageTranslationsResponse.CONT].toString(),
                            onClick = {
                                currentProgressBar = 10
                                val onBoarding1 = viewModel.parent.value
                                // Check if onBoarding1 is not null
                                if (onBoarding1 != null) {
                                    // Encrypt the username only if it exists
                                    val username = onBoarding1.userName?.encryptAES()
                                    // Update properties of the onBoarding1 object
                                    with(onBoarding1) {
                                        // Update username only if it's not null
                                        if (username != null) {
                                            userName = username
                                        }
                                        state = stateSelectedId.intValue.toString()
                                        district = districtSelectedId.intValue.toString()
                                        pinCode = pincode.value
                                        students = childCountSelected.value.toString()
                                        alternativePhone = alternativeNumber.value
                                    }
                                    // Update the parent profile in the ViewModel
                                    viewModel.updateParent(onBoarding1)

                                    val parent = viewModel.parent.value

                                    // Check for null or empty values and prepare error messages
                                    val errorMessages = mutableListOf<String>()

                                    if (parent == null) {
//                                        errorMessages.add("Parent data is not available.")
                                        errorMessages.add(languageData[LanguageTranslationsResponse.KEY_PARENT_DATA_NOT_AVAIL].toString())
                                    } else {
                                        // Check for required fields and add the first error message found
                                        when {
//                                            parent.name.isNullOrBlank() -> errorMessages.add("Name is required.")
                                            parent.name.isNullOrBlank() -> errorMessages.add(
                                                languageData[LanguageTranslationsResponse.KEY_NAME_REQUIRED].toString()
                                            )

//                                            parent.userName.isNullOrBlank() -> errorMessages.add("Username is required.")
                                            parent.userName.isNullOrBlank() -> errorMessages.add(
                                                languageData[LanguageTranslationsResponse.KEY_USERNAME_REQUIRED].toString()
                                            )

                                            parent.students.isNullOrBlank() -> errorMessages.add("Students information is required.")
//                                            parent.pinCode.isNullOrBlank() -> errorMessages.add("Pin Code is required.")
                                            parent.pinCode.isNullOrBlank() -> errorMessages.add(
                                                languageData[LanguageTranslationsResponse.KEY_PIN_CODE_REQUIRED].toString()
                                            )

                                            parent.district.isNullOrBlank() || districtSelectedId.value == -1 -> errorMessages.add(/*"District is required."*/
                                                languageData[LanguageTranslationsResponse.KEY_DISTRICT_REQUIRED].toString()
                                            )

                                            parent.state.isNullOrBlank() || stateSelectedId.value == -1 -> errorMessages.add(/*"State is required."*/
                                                languageData[LanguageTranslationsResponse.KEY_STATE_REQUIRED].toString()
                                            )

//                                            parent.dob.isNullOrBlank() -> errorMessages.add("Date of Birth is required.")
                                            parent.dob.isNullOrBlank() -> errorMessages.add(
                                                languageData[LanguageTranslationsResponse.KEY_DOB_REQUIRED].toString()
                                            )

//                                            parent.gender.isNullOrBlank() -> errorMessages.add("Gender is required.")
                                            parent.gender.isNullOrBlank() -> errorMessages.add(
                                                languageData[LanguageTranslationsResponse.KEY_GENDER_REQUIRED].toString()
                                            )

//                                            childCountSelected.value == -1 -> errorMessages.add("Please select children count ")
                                            childCountSelected.value == -1 -> errorMessages.add(
                                                languageData[LanguageTranslationsResponse.KEY_SELECT_CHILDREN_COUNT].toString()
                                            )
                                        }
                                    }
                                    if (errorMessages.isNotEmpty()) {
                                        val errorMessage =
                                            errorMessages.first() // Get only the first error message
                                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG)
                                            .show()
                                    } else {
                                        // Call API to save data
                                        isRegistered = true
                                        viewModel.getParentProfile(onBoarding1)
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
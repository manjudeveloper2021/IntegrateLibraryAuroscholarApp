package com.auro.application.ui.features.login.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.FlowRowScopeInstance.align
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.App
import com.auro.application.R
import com.auro.application.core.ConstantVariables.FORGOT_PIN
import com.auro.application.core.ConstantVariables.LOGIN_WITH_PIN
import com.auro.application.core.ConstantVariables.isRegistration
import com.auro.application.core.extions.toast
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.api.aes.AESEncryption.encryptAES
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.BottomSheetAlert
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.DefaultBackgroundUi
import com.auro.application.ui.common_ui.OtpInputField
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.ParentDashboardActivity
import com.auro.application.ui.features.student.StudentDashboardActivity
//import com.auro.application.ui.common_ui.PinInputUi
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightRed01
import com.auro.application.ui.theme.PrimaryBlue

@Composable
fun EnterPinScreen(
    navController: NavHostController,
    context: Context = LocalContext.current,
    sharedPref: SharedPref?,
    phoneNo: String?,
    isLoginWithOtp: String?,
    userId: String?,
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val context = LocalContext.current

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)


    var isDialogVisible by remember { mutableStateOf(false) }
    val encryptedPhoneNo = phoneNo?.encryptAES().toString().trim()
    // dialog for Recover Account
    var confirmRecoverState by remember { mutableStateOf(false) }

    if (confirmRecoverState) {
        ConfirmRecoverAccountBottomSheet(
            navController, viewModel, isDialogVisible, phoneNo.toString()
        ) {
            confirmRecoverState = false
        }
    }

    // Clear response when the screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loginResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        viewModel.saveUserName(it.data.data?.userDetails!!.name)
                        viewModel.saveUserEmail(it.data.data.userDetails.email)
                        viewModel.saveUserImage(it.data.data.userDetails.profilePic)
                        viewModel.setLoginInfo(it.data.data.userDetails)
                        if (it.data.data.userDetails.grade != null) {
                            viewModel.saveGrade(it.data.data.userDetails.grade.toString())
                        } else {
                            viewModel.saveGrade("1")
                        }
                        viewModel.saveParentInfo(it.data.data.userDetails)
                        viewModel.saveUserId(it.data.data.userDetails.userId)
                        viewModel.saveToken(it.data.data.token)
                        if (it.data.data.userDetails.isParent) {

                            //Parent
                            viewModel.saveUserType("1")
                            sharedPref?.saveLogin(true)
                            context.startActivity(Intent(
                                context, ParentDashboardActivity::class.java
                            ).apply { (context as Activity).finish() })
                        } else {
                            // Student
                            if (it.data.data.userDetails.isActiveUser == 1) {   // if active user
                                viewModel.saveUserType("2")
                                if (it.data.data.userDetails.grade == "11" || it.data.data.userDetails.grade == "12") {
                                    if (!it.data.data.userDetails.isSubjectPreferencesSet) {
                                        navController.popBackStack()
                                        navController.navigate(AppRoute.SelectSubject())
                                    } else {
                                        sharedPref?.saveLogin(true)
                                        context.startActivity(Intent(
                                            context, StudentDashboardActivity::class.java
                                        )/*.apply {
                                                putExtra(isRegistration, isRegistration)
                                            })*/.apply { (context as Activity).finish() })
                                    }
                                } else {
                                    context.startActivity(Intent(
                                        context, StudentDashboardActivity::class.java
                                    )/*.apply {
                                            putExtra(isRegistration, isRegistration)
                                        })*/.apply { (context as Activity).finish() })
                                }
                            } else {  // if deleted student/child
                                viewModel.saveUserId(it.data.data.userDetails.userId)
                                viewModel.saveUserPin(it.data.data.userDetails.isPinSet!!)
                                viewModel.saveUserType("2")
                                confirmRecoverState = true
                            }
                        }

                    } else {
                        context.toast(it.data?.error.toString())/* inValidPassword = true
                         textInvalidPassword = it.data!!.error*/
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message.toString())
                }
            }
        }
    }

    DefaultBackgroundUi(isShowBackButton = true, onBackButtonClick = {
        navController.navigateUp()
    }, content = {

        Text(
//                text = stringResource(id = R.string.enter_pin),
            text = languageData[LanguageTranslationsResponse.ENTER_PIN].toString(),
            modifier = Modifier.padding(top = 20.dp, start = 15.dp, end = 15.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )

        Text(
//                stringResource(id = R.string.please_enter_your_pin_to_proceed),
            text = languageData[LanguageTranslationsResponse.KEY_PIN_TO_PROCEED].toString(),
            modifier = Modifier.padding(top = 0.dp, start = 15.dp, end = 15.dp, bottom = 10.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Normal
        )

        var pinValue by remember { mutableStateOf("") }
        var isOtpFilled by remember { mutableStateOf(false) }
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        var isMasked by remember { mutableStateOf(false) }
        Log.d("otpText:", "" + "otp")
        OtpInputField(otpLength = 4,
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()

                .focusRequester(focusRequester),
            otpText = pinValue.format(pinValue),
            shouldCursorBlink = false,
            onOtpModified = { value, otpFilled ->
                pinValue = value
                isOtpFilled = otpFilled
                isMasked = true
                if (otpFilled) {
                    keyboardController?.hide()
                }
            })
        var showBottomSheet by remember { mutableStateOf(false) }

        Text(
//                stringResource(id = R.string.forgot_your_pin),
            text = languageData[LanguageTranslationsResponse.KEY_FORGOT_PIN].toString() + "?",
            modifier = Modifier
                .padding(top = 10.dp, start = 15.dp, end = 15.dp, bottom = 10.dp)
                .fillMaxWidth()
                .clickable {
                    navController.navigate(AppRoute.Login(FORGOT_PIN))
//                        showBottomSheet = true
                },
            textAlign = TextAlign.End, // Add this line to align the text to the right
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = PrimaryBlue
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            BtnUi(
                title =/* stringResource(R.string.continues)*/ languageData[LanguageTranslationsResponse.CONT].toString(),
                onClick = {

                    if (isOtpFilled) {
//                    println("Your entered pin code :- $pinValue")

                        if (encryptedPhoneNo.isNotEmpty()) {
                            if (isLoginWithOtp.equals(LOGIN_WITH_PIN)) {
                                isDialogVisible = true
                                viewModel.loginWithPinRequestCall(
                                    pinValue,
                                    encryptedPhoneNo,
                                    "",
                                    viewModel.getLanguageId().toInt()
                                )
                            } else {
                                isDialogVisible = true
                                viewModel.loginWithPinRequestCall(
                                    pinValue, "", userId!!, viewModel.getLanguageId().toInt()
                                )
                            }
                        } else {
//                        context.toast("Phone number is required")
                            context.toast(languageData[LanguageTranslationsResponse.KEY_REQUIRED_PHONE_NO].toString())
                        }
                    } else {
//                    context.toast("Please enter Pin")
                        context.toast(languageData[LanguageTranslationsResponse.KEY_PLS_ENTER_PIN].toString())
                    }
                },
                true
            )
        }

        if (showBottomSheet) {
            BottomSheetAlert(
                showBottomSheet,
                onHide = {
                    showBottomSheet = false
                }, /*stringResource(R.string.okay_got_it)*/
                languageData[LanguageTranslationsResponse.OKAY_GOT_IT].toString()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_group),
                        contentDescription = "sahsga",
                        alignment = Alignment.Center,
                        modifier = Modifier.background(Color.Unspecified)
                    )
                    Text("Oops!", color = PrimaryBlue)
                    Text(
//                        stringResource(R.string.you_havent_created_a_pin_yet_please_try_logging_in_with_a_password_or_otp),
                        text = languageData[LanguageTranslationsResponse.KEY_HAVE_NOT_CREATE_PIN].toString(),
                        modifier = Modifier.padding(10.dp),
                        color = PrimaryBlue,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmRecoverAccountBottomSheet(
    navController: NavHostController,
    viewModel: LoginViewModel,
    isDialogVisible: Boolean,
    phoneNo: String,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    var verifyOTPSheet by remember { mutableStateOf(false) }

    if (verifyOTPSheet) {
        VerifyOtpPINBottomSheet(navController, phoneNo, viewModel, context) {
            verifyOTPSheet = false
        }
    }
    // sending otp after getting password response
    viewModel.sendOtpResponse.observeForever {
        when (it) {
            is NetworkStatus.Idle -> {}
            is NetworkStatus.Loading -> {}
            is NetworkStatus.Success -> {
                verifyOTPSheet = true
            }

            is NetworkStatus.Error -> {
//                context.toast(it.message)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items in the Column
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile_bg), // Replace with your drawable resource
                tint = Color.Unspecified,
                contentDescription = null,
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Unspecified),
            )

            Text(
//                stringResource(id = R.string.txt_sure_recover),
                text = languageData[LanguageTranslationsResponse.KEY_RECOVER_THIS_ACC].toString(),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            Text(
//                stringResource(id = R.string.txt_sure_delete_desc),
                text = languageData[LanguageTranslationsResponse.DELETE_REQUIREMENTS].toString() + "\n" + languageData[LanguageTranslationsResponse.DELETE_DATA_WARNING].toString(),
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Gray, fontSize = 14.sp, fontWeight = FontWeight.Normal
                ),
                textAlign = TextAlign.Center
            )

            Divider(
                color = GrayLight02,
                thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Box(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text(
//                            text = stringResource(id = R.string.txt_cancel),
                            text = languageData[LanguageTranslationsResponse.KEY_CANCEL].toString(),
                            modifier = Modifier
                                .background(
                                    color = Color.White, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = PrimaryBlue,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    TextButton(
                        onClick = {
                            viewModel.sendOtp(phoneNo.encryptAES().toString())
                        }, modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
//                            text = stringResource(id = R.string.txt_recover),
                            text = languageData[LanguageTranslationsResponse.KEY_RECOVER].toString(),
                            modifier = Modifier
                                .background(
                                    color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyOtpPINBottomSheet(
    navController: NavHostController,
    phoneNo: String,
    viewModel: LoginViewModel,
    context: Context,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

//    var invalidOtpText = stringResource(id = R.string.invalid_OTP)
    var invalidOtpText = languageData[LanguageTranslationsResponse.KEY_INVALID_OTP].toString()

    var otpValue by remember { mutableStateOf("") }
    var inValidOTP by remember { mutableStateOf(false) }
    var successTitle by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    var successSheet by remember { mutableStateOf(false) }

    // show Success sheet while click on logout
    if (successSheet) {
        SuccessRecoverBottomSheet(navController, successTitle, successMessage, viewModel) {
            successSheet = false
        }
    }
    LaunchedEffect(Unit) {
        // ReSending otp
        viewModel.sendOtpResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {

                }

                is NetworkStatus.Error -> {
                    context.toast(it.message)
                }
            }
        }
        viewModel.verifyResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true && it.data.data == "OTP verify successfully") {
                        if (it.data.isSuccess) {
                            viewModel.getUserRecover()

                        } else {
                            inValidOTP = true
//                            invalidOtpText = "Invalid Pin"
                            invalidOtpText =
                                languageData[LanguageTranslationsResponse.KEY_INVALID_PIN].toString()
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    inValidOTP = true
                    invalidOtpText = it.message
                }
            }
        }
        viewModel.getUserInactiveResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data!!.isSuccess) {
//                        successTitle = "Account Successfully Deleted!"
//                        successTitle =
//                            languageData[LanguageTranslationsResponse.ACCOUNT_DELETED_SUCCESS].toString()
                        successTitle =
                            languageData[LanguageTranslationsResponse.ACCOUNT_DELETED_SUCCESS].toString()
                        successMessage = it.data.data.message
                        successSheet = true
                    } else {
                        inValidOTP = true
                        invalidOtpText = it.data.data.message
                    }
                }

                is NetworkStatus.Error -> {
                    inValidOTP = true
                    invalidOtpText = it.message
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items in the Column
        ) {
            /* Icon(
                 painter = painterResource(id = R.drawable.ic_lock_bg), // Replace with your drawable resource
                 contentDescription = null, // Provide a description for accessibility purposes
                 modifier = Modifier.wrapContentSize(),
             )*/

            Text(
//                stringResource(id = R.string.text_enter_otp),
                text = languageData[LanguageTranslationsResponse.ENTER_OTP].toString(),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
//                stringResource(id = R.string.txt_otp_has_been_sent),
                text = languageData[LanguageTranslationsResponse.KEY_OTP_HAS_BEEN_SENT].toString(),
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Gray,
                    fontSize = 14.sp,
                ),
                textAlign = TextAlign.Center
            )
            Text(
                "+91-$phoneNo",
                modifier = Modifier.padding(bottom = 10.dp),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Black,
                    fontSize = 14.sp,
                ),
                textAlign = TextAlign.Center
            )

            otpValue = otpTextField(/*loginViewModel*/)  // inside VerifyOTScreen
            if (inValidOTP) {
                Text(
                    invalidOtpText,
                    color = LightRed01,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp
                )
            }

            Divider(
                color = GrayLight02,
                thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            viewModel.sendOtp(phoneNo.encryptAES().toString())
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text(
//                            text = stringResource(id = R.string.text_resend),
                            text = languageData[LanguageTranslationsResponse.KEY_RESEND].toString(),
                            modifier = Modifier.background(
                                color = Transparent, shape = RoundedCornerShape(12.dp)
                            ),
                            color = PrimaryBlue,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    TextButton(
                        onClick = {
                            showError = otpValue.isEmpty()
                            if (showError || otpValue.length < 6) {
                                inValidOTP = true
                            } else {
                                viewModel.verifyOtp(otpValue, phoneNo.encryptAES().toString())
                                inValidOTP = false
                            }
                        },

                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
//                            text = stringResource(id = R.string.text_verify_otp),
                            text = languageData[LanguageTranslationsResponse.VERIFY].toString(),
                            modifier = Modifier
                                .background(
                                    color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessRecoverBottomSheet(
    navController: NavHostController,
    title: String,
    description: String,
    viewModel: LoginViewModel,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items in the Column
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_success), // Replace with your drawable resource
                contentDescription = null, // Provide a description for accessibility purposes
                tint = Color.Unspecified,
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Unspecified),
            )

            Text(
                title,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                description,
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Gray,
                    fontSize = 14.sp,
                ),
                textAlign = TextAlign.Center
            )
            TextButton(
                onClick = {
                    onDismiss()
                    viewModel.saveUserLogin(true)
                    context.startActivity(Intent(
                        context, StudentDashboardActivity::class.java
                    ).apply {
                        putExtra(isRegistration, isRegistration)
                    })
                },

                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                Text(
//                    text = stringResource(id = R.string.okay_got_it),
                    text = languageData[LanguageTranslationsResponse.OKAY_GOT_IT].toString(),
                    modifier = Modifier
                        .background(
                            color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                        )
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EnterPinPreview() {
    AuroscholarAppTheme {
        EnterPinScreen(
            navController = rememberNavController(),
            sharedPref = null,
            phoneNo = "",
            isLoginWithOtp = "",
            userId = ""
        )
    }
}

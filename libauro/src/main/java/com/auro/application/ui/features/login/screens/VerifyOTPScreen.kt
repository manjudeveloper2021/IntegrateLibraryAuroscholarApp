package com.auro.application.ui.features.login.screens

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.App
import com.auro.application.R
import com.auro.application.core.ConstantVariables.EDIT_MOBILE
import com.auro.application.core.ConstantVariables.FORGOT_PASSWORD
import com.auro.application.core.ConstantVariables.FORGOT_PIN
import com.auro.application.core.ConstantVariables.LOGIN_WITH_OTP
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.api.aes.AESEncryption.encryptAES
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.DefaultBackgroundUi
import com.auro.application.ui.common_ui.OtpInputField
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.authentication.screens.CountdownTimer
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.DarkRed2
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.LightRed01
import com.auro.application.ui.theme.PrimaryBlue
import java.util.HashMap

@Composable
fun VerifyOTPScreen(navController: NavHostController, phoneNo: String?, isLoginWithOtp: String?) {

    val viewModel: LoginViewModel = hiltViewModel()
    val context = LocalContext.current
    var lifecycleOwner = LocalLifecycleOwner.current

    var inValidOTP by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var otpValue by remember { mutableStateOf("") }
    var resendWithTime by remember { mutableStateOf("02:00") }
    var isTimerEnd by remember { mutableStateOf(false) }
    val languageListName = stringResource(id = R.string.key_lang_list)
//    val languageData = viewModel.getLanguageTranslationData()
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)
    var isDialogVisible by remember { mutableStateOf(false) }

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = "Loading your data..."
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    // Handle ON_CREATE event
                    if (phoneNo != null) {
                        sendOtp(viewModel, phoneNo.encryptAES().toString())
                    }
                }

                Lifecycle.Event.ON_START -> {
                    // Handle ON_START event
                }

                Lifecycle.Event.ON_RESUME -> {
                    // Handle ON_RESUME event
                }

                Lifecycle.Event.ON_PAUSE -> {
                    // Handle ON_PAUSE event
                }

                Lifecycle.Event.ON_STOP -> {
                    // Handle ON_STOP event
                }

                Lifecycle.Event.ON_DESTROY -> {
                    // Handle ON_DESTROY event
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BackHandler {
//        navController.previousBackStackEntry?.savedStateHandle?.set(
//            "mobile", phoneNo
//        )
        navController.navigateUp()
        navController.popBackStack()
        navController.navigate(AppRoute.Login(EDIT_MOBILE))
    }

    // Clear response when the screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.verifyResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data?.isSuccess == true && it.data.data == "OTP verify successfully") {
                        when (isLoginWithOtp) {
                            LOGIN_WITH_OTP -> {    // if user coming from Login with OTP
                                viewModel.loginWithOtpRequestCall(
                                    otpValue, phoneNo?.encryptAES().toString()
                                )
                            }

                            FORGOT_PIN -> {   // if user coming from Forgot pin
                               // navController.navigate(AppRoute.Login(FORGOT_PIN))
                                navController.navigate(AppRoute.CreatePin(phoneNo, FORGOT_PIN))
                            }

                            FORGOT_PASSWORD -> {
                                viewModel.setForgotPassword(FORGOT_PASSWORD)// if user coming from Forgot Password
                                navController.navigate(AppRoute.CreatePassword())
                            }

                            else -> {
                                navController.navigate(AppRoute.CreatePassword())
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

        viewModel.sendOtpResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {

                }

                is NetworkStatus.Error -> {
//                    context.toast(it.message)
                }
            }
        }

        viewModel.loginResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
//                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
//                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        viewModel.saveToken(it.data.data!!.token)
                        viewModel.saveUserName(it.data.data.userDetails!!.name)
                        viewModel.saveUserEmail(it.data.data.userDetails.email)
                        viewModel.saveUserImage(it.data.data.userDetails.profilePic)
                        viewModel.saveParentInfo(it.data.data.userDetails)
                        viewModel.setLoginInfo(it.data.data.userDetails)
                        if (it.data.data.userDetails.isPinSet == false) {
                            viewModel.saveUserPin(false)
                        } else {
                            viewModel.saveUserPin(true)
                        }
                        viewModel.saveUserId(it.data.data.userDetails.userId)
                        viewModel.saveToken(it.data.data.token.toString())

                        val userPin = viewModel.getUserPin()
                        val userId = viewModel.getUserId()

                        navController.navigate(
                            AppRoute.ChildList(
                                userId, userPin.toString()
                            )
                        )

                    } else {/* inValidPassword = true
                         textInvalidPassword = it.data!!.error*/
                    }
                }

                is NetworkStatus.Error -> {

                }
            }
        }

    }

    DefaultBackgroundUi(isShowBackButton = true, onBackButtonClick = {
//        navController.previousBackStackEntry?.savedStateHandle?.set(
//            "mobile", phoneNo
//        )

        navController.navigateUp()
        navController.popBackStack()
        navController.navigate(AppRoute.Login(EDIT_MOBILE))
    }, content = {

        val verifyOtp = languageData[LanguageTranslationsResponse.VERIFY_OTP].toString()
        Text(
            text = verifyOtp,
            modifier = Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Black
        )

        val str = languageData[LanguageTranslationsResponse.NOT_REGD_MSG].toString()
        Text(
            str,
            modifier = Modifier.padding(top = 0.dp, start = 10.dp, end = 10.dp),
            color = GrayLight01,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )

        Surface(
            modifier = Modifier.fillMaxSize(), color = Color.White
        ) {


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {

                Spacer(modifier = Modifier.height(10.dp))
                val tvMobNo = "+91-$phoneNo"
                val textEnterOtp = languageData[LanguageTranslationsResponse.ENTER_OTP].toString()

                val invalidOtpText = languageData[LanguageTranslationsResponse.KEY_INVALID_OTP].toString()
                Row(
                    modifier = Modifier.wrapContentHeight()
                ) {
                    Text(
                        tvMobNo,
                        modifier = Modifier.padding(bottom = 10.dp),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(
                        languageData[LanguageTranslationsResponse.KEY_EDIT].toString(),
                        modifier = Modifier
                            .padding(bottom = 10.dp, start = 10.dp)
                            .clickable {
//                                navController.previousBackStackEntry?.savedStateHandle?.set(
//                                    "mobile", phoneNo
//                                )
                                navController.navigateUp()
                                navController.popBackStack()
//                                    navController.popBackStack()
                                navController.navigate(AppRoute.Login(EDIT_MOBILE))
                                println("Go to the enter mobile number page...")
                            }, // Navigates back ,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = PrimaryBlue,
                    )
                }
                Text(
                    textEnterOtp,
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .fillMaxWidth(),
                    style = TextStyle(
                        textAlign = TextAlign.Center, // Center align the text
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = GrayLight01,
                    )
                )

                // otp input field
                otpValue = otpTextField()

                if (inValidOTP) {
                    Text(
                        invalidOtpText,
                        color = LightRed01,
                        modifier = Modifier.padding(start = 10.dp),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))


                var resendText = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = GrayLight01,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    ) {
                        append(languageData[LanguageTranslationsResponse.RESEND_OTP].toString())
                    }
                    withStyle(
                        style = SpanStyle(
                            color = DarkRed2, fontWeight = FontWeight.Medium, fontSize = 15.sp
                        )
                    ) {
                        append(" ($resendWithTime)")
                    }
                }
                CountdownTimer(time = resendWithTime, countDown = { timeLeft ->
                    resendWithTime = timeLeft
                    isTimerEnd = false
                }, onFinished = {
                    isTimerEnd = true
                    // Handle what happens when the countdown finishes
                })


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
// Resend OTP
                    if (resendWithTime == "00:00") {
                        Text(text = languageData[LanguageTranslationsResponse.RESEND_OTP].toString(),
                            modifier = Modifier
                                .clickable {
                                    if (resendWithTime == "00:00") {
                                        resendWithTime = "02:00"
                                        sendOtp(
                                            viewModel = viewModel, phoneNo = phoneNo
                                        )
                                    }
                                }
                                .wrapContentWidth()
                                .padding(start = 10.dp, end = 10.dp),
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Medium,
                            fontSize = 17.sp,
                            color = PrimaryBlue,
                            textAlign = TextAlign.Left)

                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (resendWithTime != "00:00") {
                        Text(
                            text = resendText,
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(start = 10.dp, end = 10.dp),
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Medium,
                            fontSize = 17.sp,
                            color = PrimaryBlue,
                            textAlign = TextAlign.Left
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
// Send otp on call
                    if (resendWithTime == "00:00") {
                        Text(text = languageData[LanguageTranslationsResponse.GET_OTP_ON_CALL].toString(),
                            modifier = Modifier
                                .clickable {
                                    if (resendWithTime == "00:00") {
                                        resendWithTime = "02:00"
                                    }
                                    sendOtpOnCall(viewModel, phoneNo)
                                }
                                .wrapContentWidth()
                                .padding(start = 10.dp, end = 10.dp),
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Medium,
                            fontSize = 17.sp,
                            color = PrimaryBlue,
                            textAlign = TextAlign.Left)
                    }

                }
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                ) {

                    BtnUi(languageData[LanguageTranslationsResponse.SUBMIT_OTP].toString(), onClick = {
                        showError = otpValue.isEmpty()
                        inValidOTP = showError || otpValue.length < 6
                        if (!inValidOTP) {
                            if (phoneNo != null) {
                                isDialogVisible = true
                                verifyOtp(viewModel, phoneNo.encryptAES().toString(), otpValue)
                            }
                        }
                    }, true)
                }
            }
        }
    })
}

fun sendOtp(
    viewModel: LoginViewModel, phoneNo: String?
) {
    viewModel.sendOtp(phoneNo?.encryptAES().toString())
}

fun sendOtpOnCall(
    viewModel: LoginViewModel, phoneNo: String?
) {
    viewModel.sendOtpOnCall(phoneNo?.encryptAES().toString())
}

fun verifyOtp(
    viewModel: LoginViewModel, phoneNo: String, otp: String
) {
    viewModel.verifyOtp(otp, phoneNo.encryptAES().toString())
}


@Composable
fun otpTextField(): String {
    var otpValue by remember { mutableStateOf("") }
    var isOtpFilled by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // 6 OTP field in common
    OtpInputField(modifier = Modifier
        .padding(top = 20.dp)
        .fillMaxWidth()
        .focusRequester(focusRequester),
        otpText = otpValue,
        shouldCursorBlink = false,
        onOtpModified = { value, otpFilled ->
            otpValue = value
            isOtpFilled = otpFilled
            if (otpFilled) {
                keyboardController?.hide()
            }
        }

    )
    return otpValue
}

@Preview(showBackground = true)
@Composable
fun OTPPreview() {
    AuroscholarAppTheme {
        VerifyOTPScreen(
            navController = rememberNavController(), phoneNo = "", isLoginWithOtp = LOGIN_WITH_OTP
        )
    }
}
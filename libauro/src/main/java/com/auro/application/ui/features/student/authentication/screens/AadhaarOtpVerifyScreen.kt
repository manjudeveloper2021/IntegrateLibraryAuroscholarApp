package com.auro.application.ui.features.student.authentication.screens

import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.ui.common_ui.OtpInputField
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.authentication.model.GetAadharOTPVerifyRequestModel
import com.auro.application.ui.features.student.authentication.model.GetAadharOtpSendRequestModel
import com.auro.application.ui.features.student.authentication.model.GetSaveAadharDataRequestModel
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import java.util.Locale

@Composable
fun AadhaarOtpVerifyScreen(
    navHostController: NavHostController,
    adharNo: String?,
    otpReferenceID: String?,
    viewModel: StudentViewModel
) {

//    Log.e("TAG", "AadhaarOtpVerifyScreen: adhar no --> $adharNo $otpReferenceID")
    var isDialogVisible by remember { mutableStateOf(false) }
    val loginViewModel: LoginViewModel = hiltViewModel()
    var userId by rememberSaveable { mutableStateOf("0") }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = msgLoader
    )
    val context = LocalContext.current
    var isOTPErrorSheetVisible by rememberSaveable { mutableStateOf(false) }
    userId =
        if (loginViewModel.getParentInfo()!!.isParent) {  // if login as parent & came to authentication
            loginViewModel.getStudentList().userId    // studentId
        } else {
            loginViewModel.getUserId().toString() // parent id
        }
    if (isOTPErrorSheetVisible) {
        OTPErrorBottomSheet(
            navHostController,
            viewModel,
            languageData,
            userId,
            adharNo.toString(),
            context
        ) {
            isOTPErrorSheetVisible = false
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getAadharSaveResponseMutableLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {
//                    isDialogVisible = false
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
//                    Log.e("TAG", "AadhaarOtpVerifyScreen: data is her --->  ", )
                    navHostController.navigate(AppRoute.PhotoUpload(adharNo.toString()))
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    isOTPErrorSheetVisible = true
                }
            }
        }
        viewModel.getAadhaarOTPVerifyResponseModelLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {
//                    isDialogVisible = false
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false

                    if (it.data?.data != null && adharNo != null) {
                        val aadharNo = adharNo
                        val dob = formatDate(it.data.data.dateOfBirth)
                        val profile = it.data.data.profilePic
                        val studentName = it.data.data.fullName

                        viewModel.getSaveAadharData(
                            GetSaveAadharDataRequestModel(
                                aadharNo,
                                dob,
                                profile = profile,
                                userId.toInt(),
                                studentName = studentName
                            )
                        )
                    } else {
                        isOTPErrorSheetVisible = true
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    isOTPErrorSheetVisible = true
                    Toast.makeText(context, "" + it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    // Clear response when the screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    var otp by remember { mutableStateOf("") }
    StudentRegisterBackground(
        isShowBackButton = true,
        onBackButtonClick = {
            navHostController.popBackStack()
            navHostController.navigate(AppRoute.AadharCheck(""))
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
                            .padding(start = 8.dp, end = 8.dp)
                    ) {
                        Text(
                            text = if (languageData[LanguageTranslationsResponse.COMPLETE_AUTHENTICATION].toString() == "") {
                                stringResource(id = R.string.complete_your_authentication)
                            } else {
                                languageData[LanguageTranslationsResponse.COMPLETE_AUTHENTICATION].toString()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp),
                            fontFamily = FontFamily(
                                Font(R.font.inter_bold, FontWeight.Bold)
                            ),
                            fontSize = 20.sp,
                            color = Black,
                            textAlign = TextAlign.Left
                        )


                        Text(
                            text = "Enter your details to confirm your identity and recieve scholarship securely",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            color = GrayLight01,
                            fontFamily = FontFamily(
                                Font(R.font.inter_regular, FontWeight.Normal)
                            ),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Start
                        )

                        Column(
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp)
                                .border(
                                    width = 1.dp,
                                    color = GrayLight02,
                                    shape = RoundedCornerShape(10.dp)
                                )
                        ) {

                            ProgressBarCompose(5)
                        }

                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()

                            .weight(weight = 1f, fill = false)
                            .padding(all = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .verticalScroll(rememberScrollState())
                                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 70.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = if (languageData[LanguageTranslationsResponse.VERIFY_OTP].toString() == "") {
                                    stringResource(R.string.text_verify_otp)
                                } else {
                                    languageData[LanguageTranslationsResponse.VERIFY_OTP].toString()
                                },
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily(
                                    Font(R.font.inter_medium, FontWeight.Medium)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                stringResource(R.string.please_enter_the_otp_code_sent_to),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                color = GrayLight01,
                                fontFamily = FontFamily(
                                    Font(R.font.inter_regular, FontWeight.Normal)
                                ),
                            )

                            Spacer(modifier = Modifier.height(10.dp))


                            otpTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                onOtpFilled = { filledOtp ->
                                    otp = filledOtp
                                    // Handle the filled OTP here
                                    println("OTP filled: $filledOtp")
                                }
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                stringResource(R.string.change_aadhar_number),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navHostController.navigateUp()
                                    },
                                fontFamily = FontFamily(
                                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                ),
                                color = PrimaryBlue,
                                fontSize = 17.sp
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter)
                        .background(color = Color.White)
                        .shadow(elevation = 0.5.dp, shape = RoundedCornerShape(0.dp)),
                    contentAlignment = Alignment.CenterEnd
                )
                {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween // Optional: Adjusts spacing between buttons
                    ) {


                        var resendWithTime by remember { mutableStateOf("02:00") }

                        CountdownTimer(
                            time = resendWithTime,
                            countDown = { timeLeft ->
                                resendWithTime = timeLeft
                            },
                            onFinished = {
                                // Handle what happens when the countdown finishes
                            }
                        )

                        val context = LocalContext.current
                        Button(
                            title = if (resendWithTime == "00:00") {
                                if (languageData[LanguageTranslationsResponse.RESEND_OTP].toString() == "") {
                                    "Resend OTP"
                                } else {
                                    languageData[LanguageTranslationsResponse.RESEND_OTP].toString()
                                }
                            } else {
                                if (languageData[LanguageTranslationsResponse.RESEND_OTP].toString() == "") {
                                    "Resend OTP"
                                } else {
                                    languageData[LanguageTranslationsResponse.RESEND_OTP].toString()
                                } + " $resendWithTime"
                            },
                            onClick = {

                                otp = ""
                                if (resendWithTime == "00:00") {
                                    resendWithTime = "02:00"

                                    // send otp
                                    val lat = viewModel.latitude.value
                                    val lng = viewModel.longitude.value
                                    Log.e("TAG", "AdharaCheckScreen: clicked$lat $lng")
                                    lng?.let {
                                        lat?.let {
                                            if (!adharNo.isNullOrBlank() && adharNo != "") {
                                                viewModel.getAadhaarOtpSend(
                                                    GetAadharOtpSendRequestModel(
//                                                        "Y",
                                                        adharNo.toString().trim().toLong(),
                                                        /*   latitude = lat,
                                                           longitude = lng,*/
                                                        userId.toInt()
                                                    )
                                                )
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Please enter Aadhar no",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                navHostController.navigateUp()

                                            }
                                        }
                                    }


                                }
                            },
                            enabled = resendWithTime == "00:00", // Set to true or false based on your logic
                            modifier = Modifier.weight(1f) // Equal width for Button 2
                        )

                        Button(
                            title = if (languageData[LanguageTranslationsResponse.VERIFY_OTP].toString() == "") {
                                stringResource(id = R.string.vrify_otp)
                            } else {
                                languageData[LanguageTranslationsResponse.VERIFY_OTP].toString()
                            },
                            onClick = {
/*                                navHostController.popBackStack()
                                navHostController.navigate(AppRoute.PhotoUpload(adharNo.toString()))*/

                                // Send OTP
                                val lat = viewModel.latitude.value
                                val lng = viewModel.longitude.value

                                Log.e("TAG", "AadhaarOtpVerifyScreen: ---otp is her --> " + otp)

                                // Check if latitude, longitude, otpReferenceID, and otp are not null
                                if (lat != null && lng != null && otpReferenceID != null) {
                                    // Check if otp is not empty
                                    if (otp.length == 6) {
                                        val requestModel = GetAadharOTPVerifyRequestModel(
                                            /*aadharConsent = "Y",
                                        latitude = lat,
                                        longitude = lng,*/
                                            otp = otp.toDouble(),
                                            otpReferenceID = otpReferenceID,
                                            userId.toInt()
                                        )

                                        // Call the ViewModel function
                                        isDialogVisible = true
                                        viewModel.getAadhaarOtpVerify(requestModel)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            if (languageData[LanguageTranslationsResponse.KEY_INVALID_OTP].toString() == "") {
                                                "Please enter valid OTP"
                                            } else {
                                                languageData[LanguageTranslationsResponse.KEY_INVALID_OTP].toString()
                                            },
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    // Handle the case where latitude, longitude, otpReferenceID, or otp is null
                                    Log.e(
                                        "AadhaarOtpVerify",
                                        "One or more required parameters are null"
                                    )
                                    // You can also show an error message to the user
                                }
                            },
                            enabled = otp.length == 6, // Set to true or false based on your logic
                            modifier = Modifier.weight(1f) // Equal width for Button 2
                        )
                    }
                }
            }

        }
    )
}

@Composable
fun otpTextField(
    modifier: Modifier = Modifier,
    onOtpFilled: (String) -> Unit = {}
): Unit {
    var otpValue by remember { mutableStateOf("") }
    var isOtpFilled by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    OtpInputField(
        modifier = modifier
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
                onOtpFilled(value)
            }
        }
    )
}

@Composable
fun CountdownTimer(
    time: String = "00:02",
    countDown: (String) -> Unit,
    onFinished: () -> Unit
) {
    // Parse the input time
    val (minutes, seconds) = time.split(":").map { it.toInt() }
    val millisInFuture = minutes * 60000 + seconds * 1000

    // Only proceed if the time is not zero
    if (millisInFuture > 0) {
        var countDownTimer by remember { mutableStateOf(millisInFuture) }

        DisposableEffect(key1 = countDownTimer) {
            val timer = object : CountDownTimer(countDownTimer.toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val minutesLeft = millisUntilFinished / 60000
                    val secondsLeft = (millisUntilFinished % 60000) / 1000
                    countDown.invoke(
                        String.format(
                            Locale.US,
                            "%02d:%02d",
                            minutesLeft,
                            secondsLeft
                        )
                    )
                    countDownTimer = millisUntilFinished.toInt()
                }

                override fun onFinish() {
                    onFinished.invoke()
                    countDownTimer = 0
                    countDown.invoke(String.format(Locale.US, "%02d:%02d", 0, 0))
                }
            }
            timer.start()
            onDispose {
                timer.cancel()
            }
        }
    } else {
        // If time is "00:00", invoke the finished callback immediately
        onFinished.invoke()
        countDown.invoke("00:00")
    }
}

fun formatDate(input: String): String {
    // Split the input string by "-"
    val parts = input.split("-").map { it.trim() }

    // Initialize year, month, and day with default values
    var year = "01"
    var month = "01"
    var day = "01"

    // Check the number of parts and assign values accordingly
    when (parts.size) {
        1 -> {
            // Only year is provided
            year = parts[0].padStart(4, '0') // Ensure year is 4 digits
        }

        2 -> {
            // Year and month are provided
            year = parts[0].padStart(4, '0') // Ensure year is 4 digits
            month = parts[1].padStart(2, '0') // Ensure month is 2 digits
        }

        3 -> {
            // Year, month, and day are provided
            year = parts[2].padStart(4, '0') // Year is the last part
            month = parts[1].padStart(2, '0') // Month is the second part
            day = parts[0].padStart(2, '0') // Day is the first part
        }

        else -> {
            throw IllegalArgumentException("Invalid date format")
        }
    }

    // Return the formatted date in YYYY-MM-DD format
    return "$year-$month-$day"
}

@Preview
@Composable
fun AdharOtpScreenPreview() {
    AadhaarOtpVerifyScreen(navHostController = rememberNavController(), null, null, hiltViewModel())
}
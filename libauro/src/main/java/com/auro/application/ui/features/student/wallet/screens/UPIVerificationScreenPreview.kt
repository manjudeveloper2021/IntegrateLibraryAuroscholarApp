package com.auro.application.ui.features.student.wallet.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
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
import com.auro.application.ui.common_ui.OtpInputField
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.StudentRegisterWalletBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.authentication.screens.Button
import com.auro.application.ui.features.student.authentication.screens.CountdownTimer
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.features.student.wallet.Models.UpiTransactionRequestModel
import com.auro.application.ui.features.student.wallet.Models.UpiTransactionResponse
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.DarkRed2
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun UPIVerificationScreenPreview(
    navController: NavHostController = rememberNavController(), status: String? = ""
) {

    val context = LocalContext.current
    val viewModel: LoginViewModel = hiltViewModel()
    val viewModels: StudentViewModel = hiltViewModel()
    var strMobileNo: String = ""
    var inValidOTP by remember { mutableStateOf(false) }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    var invalidOtpText =
        if (languageData[LanguageTranslationsResponse.KEY_INVALID_OTP].toString() == "") {
            stringResource(id = R.string.invalid_OTP)
        } else {
            languageData[LanguageTranslationsResponse.KEY_INVALID_OTP].toString()
        }

    var upiTransactionData by remember { mutableStateOf<UpiTransactionResponse?>(null) }
    val upiList by remember { mutableStateOf(mutableListOf<UpiTransactionRequestModel.WinningWallet?>()) }
    var strBankOrUpi by remember { mutableStateOf<String?>("") }
    var strBankAccountUpiID by remember { mutableStateOf<String?>("") }
    var strTotalAmount by remember { mutableStateOf<String?>("") }

    strBankOrUpi = viewModels.getBankUpi().toString()

    strMobileNo = viewModel.getUserPhoneNo().toString()

    println("Your mobile Number is :- $strMobileNo")

    var isDialogVisible by remember { mutableStateOf(false) }
    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    var isTimerEnd by remember { mutableStateOf(false) }
    var otpValue by remember { mutableStateOf("") }

    CustomDialog(
        isVisible = isDialogVisible, onDismiss = { isDialogVisible = false }, message = msgLoader
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sendOtpResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        println("Otp sent successfully.")
                    } else {
                        isDialogVisible = false
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                    println("Error Data :- ${it.message}")
                }
            }
        }

        viewModel.verifyResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true && it.data.data == "OTP verify successfully") {
                        if (it.data.isSuccess) {
                            isDialogVisible = false

                            viewModels.setTransactionId("")
                            viewModels.setTransactionDateTime("")
                            viewModels.setTransactionAmount("")
                            viewModels.setTransactionStatus("")
//                            otpValue = ""

                            if (isDialogVisible == false) {
                                isDialogVisible = true
                                strBankAccountUpiID = viewModels.getBankAccountUpiID().toString()
                                strTotalAmount =
                                    viewModels.getWalletInfoData().transactionAmount.toString()
                                viewModels.getWalletInfoData().wallet?.forEach { winningWalletId ->
                                    val strWalletId: Int = winningWalletId.id!!
                                    println("Your wallet Id is :- $strWalletId")
                                    upiList.add(UpiTransactionRequestModel.WinningWallet(strWalletId))
                                }
                                viewModels.getUpiTransaction(
                                    UpiTransactionRequestModel(
                                        strBankAccountUpiID!!, strTotalAmount!!.toInt(), upiList
                                    )
                                )
                            }

//                            navController.navigate(AppRoute.TransferStatusScreenPreview("true"))
                        } else {
                            inValidOTP = true
                            isDialogVisible = false
                            invalidOtpText =
                                if (languageData[LanguageTranslationsResponse.KEY_INVALID_PIN].toString() == "") {
                                    "Invalid Pin"
                                } else {
                                    languageData[LanguageTranslationsResponse.KEY_INVALID_PIN].toString()
                                }
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    inValidOTP = true
                    isDialogVisible = false
                    invalidOtpText = it.message
                    context.toast(invalidOtpText)
                }
            }
        }

        viewModels.upiTransactionResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        upiTransactionData = it.data
                        if (upiTransactionData != null) {
                            viewModels.setTransactionId(upiTransactionData!!.data?.transactionId.toString())
                            viewModels.setTransactionDateTime(upiTransactionData!!.data?.timestamp.toString())
                            viewModels.setTransactionAmount(upiTransactionData!!.data?.amount.toString())
                            viewModels.setTransactionStatus(upiTransactionData!!.data?.status.toString())
                            navController.navigate(AppRoute.TransferStatusScreenPreview("true"))
                        } else {
                            println("UPI transaction data not found")
                        }
                    } /*else {
                        context.toast(it.data?.error.toString())
                    }*/
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                    println("Error Data :- ${it.message}")
                }
            }
        }

        viewModel.sendOtp(strMobileNo.encryptAES().toString())
    }

    StudentRegisterWalletBackground(isShowBackButton = true,
        isDeleteBankAccount = false,
        isShowFullTopBarMenu = false,
        title = "",
        onBackButtonClick = {
            (context as ComponentActivity).finish()
        },
        content = {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.verify_UPI_by_OTP),
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
                        text = if (languageData[LanguageTranslationsResponse.OTP_SENT_TO_REGISTERED_NUMBER].toString() == "") {
                            stringResource(id = R.string.otp_has_been_sent_to_the_registered_number)
                        } else {
                            languageData[LanguageTranslationsResponse.OTP_SENT_TO_REGISTERED_NUMBER].toString()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        color = GrayLight01,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Medium)
                        ),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start
                    )

                    Text(
                        "+91 $strMobileNo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        color = Black,
                        fontFamily = FontFamily(
                            Font(R.font.inter_medium, FontWeight.Medium)
                        ),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start
                    )

                    Text(
                        text = if (languageData[LanguageTranslationsResponse.ENTER_YOUR_OTP_HERE].toString() == "") {
                            stringResource(id = R.string.text_enter_otp)
                        } else {
                            languageData[LanguageTranslationsResponse.ENTER_YOUR_OTP_HERE].toString()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        color = GrayLight01,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
                        ),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    var inValidOTP by remember { mutableStateOf(false) }

                    var resendWithTime by remember { mutableStateOf("00:20") }

                    var resendText = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = GrayLight01,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                        ) {
                            append(
                                if (languageData[LanguageTranslationsResponse.RESEND_OTP].toString() == "") {
                                    "Resend OTP "
                                } else {
                                    languageData[LanguageTranslationsResponse.RESEND_OTP].toString()
                                }
                            )
                        }
                        withStyle(
                            style = SpanStyle(
                                color = DarkRed2, fontWeight = FontWeight.Medium, fontSize = 15.sp
                            )
                        ) {
                            append("(" + resendWithTime + ")")
                        }
                    }

                    CountdownTimer(time = resendWithTime, countDown = { timeLeft ->
                        resendWithTime = timeLeft
                        isTimerEnd = false
                    }, onFinished = {
                        isTimerEnd = true
                        // Handle what happens when the countdown finishes
                    })

                    otpValue = otpTextField()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (resendWithTime == "00:00") {
                            Text(text = if (languageData[LanguageTranslationsResponse.RESEND_OTP].toString() == "") {
                                "Resend OTP "
                            } else {
                                languageData[LanguageTranslationsResponse.RESEND_OTP].toString()
                            },
                                modifier = Modifier
                                    .clickable {
                                        if (resendWithTime == "00:00") {
                                            resendWithTime = "02:00"
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
                                fontFamily = FontFamily(
                                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                ),
                                fontSize = 17.sp,
                                color = PrimaryBlue,
                                textAlign = TextAlign.Left
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        if (resendWithTime == "00:00") {
                            Text(text = if (languageData[LanguageTranslationsResponse.GET_OTP_CALL].toString() == "") {
                                "Get OTP on Call"
                            } else {
                                languageData[LanguageTranslationsResponse.GET_OTP_CALL].toString()
                            },
                                modifier = Modifier
                                    .clickable {
                                        if (resendWithTime == "00:00") {
                                            resendWithTime = "02:00"
                                        }
                                    }
                                    .wrapContentWidth()
                                    .padding(start = 10.dp, end = 10.dp),
                                fontFamily = FontFamily(
                                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                ),
                                fontSize = 17.sp,
                                color = PrimaryBlue,
                                textAlign = TextAlign.Left)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween // Optional: Adjusts spacing between buttons
                    ) {
                        VerifyButton(
                            title = if (languageData[LanguageTranslationsResponse.VERIFY_OTP].toString() == "") {
                                stringResource(id = R.string.vrify_otp)
                            } else {
                                languageData[LanguageTranslationsResponse.VERIFY_OTP].toString()
                            },
                            onClick = {
                                println("Entered otp data :- ${otpValue.toString()}")
//                                navController.navigate(AppRoute.TransferStatusScreenPreview("true"))
                                if (otpValue.isEmpty()) {
                                    Toast.makeText(context, "Please enter OTP!", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    upiList.clear()
                                    isDialogVisible = true
                                    viewModel.verifyOtp(
                                        otpValue.toString(), strMobileNo.encryptAES().toString()
                                    )
                                }
                            },
                            enabled = otpValue.length == 6, // Set to true or false based on your logic
                            modifier = Modifier
                                .weight(1f) // Equal width for Button 2
                        )
                    }

                    val annotatedString = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = GrayLight01,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                        ) {
                            append("Please contact ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                        ) {
                            append("+91-9289938818")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = GrayLight01,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                        ) {
                            append(" for support")
                        }
                    }

                    Text(
                        text = annotatedString,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        fontFamily = FontFamily(
                            Font(R.font.inter_medium, FontWeight.Medium)
                        ),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        })
}

@Composable
fun VerifyButton(
    title: String = "Continue",
    onClick: () -> Unit,
    enabled: Boolean = false,
    modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope()
    var clickEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(10.dp)
            .background(
                color = if (enabled) PrimaryBlue else GrayLight02, shape = RoundedCornerShape(
                    topStart = 15.dp,
                    topEnd = 15.dp,
                    bottomStart = 15.dp,
                    bottomEnd = 15.dp
                )
            )
            .clickable(enabled = clickEnabled) {
                onClick()
                clickEnabled = false
                scope.launch {
                    delay(15000)
                    clickEnabled = true
                }
            }
    ) {
        Text(
            title,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center) // Center both horizontally and vertically
            , fontWeight = FontWeight.SemiBold, color = if (enabled) White else Black
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun otpTextField(): String {
    var otpValue by remember { mutableStateOf("") }
    var isOtpFilled by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

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
        })
    return otpValue
}

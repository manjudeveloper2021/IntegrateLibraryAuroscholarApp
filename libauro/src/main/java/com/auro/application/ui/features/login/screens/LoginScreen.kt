package com.auro.application.ui.features.login.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalContext
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
import com.auro.application.core.ConstantVariables.EDIT_MOBILE
import com.auro.application.core.ConstantVariables.ENTER_PASSWORD
import com.auro.application.core.ConstantVariables.FORGOT_PASSWORD
import com.auro.application.core.ConstantVariables.FORGOT_PIN
import com.auro.application.core.ConstantVariables.LOGIN_WITH_PASSWORD
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.Constants.PRIVACY_POLICY
import com.auro.application.data.api.Constants.TERMS_CONDITION
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.api.aes.AESEncryption.encryptAES
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.DefaultBackgroundUi
import com.auro.application.ui.common_ui.MobileTextField
import com.auro.application.ui.common_ui.PasswordTextField
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.components.DoubleBackPressHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightRed01
import com.auro.application.ui.theme.PrimaryBlue
import java.util.HashMap

@Composable
fun LoginScreen(navController: NavHostController, isForgotPinPassword: String?, mobileNo: String) {
    var isDialogVisible by remember { mutableStateOf(false) }
    val viewModel: LoginViewModel = hiltViewModel()
    var inValidMobNo by remember { mutableStateOf(false) }
    var mobNo = rememberSaveable { mutableStateOf(mobileNo) }
    var showError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var buttonClicked by remember { mutableStateOf(false) }
    val languageListName = stringResource(id = R.string.key_lang_list)
//    val languageData = viewModel.getLanguageTranslationData()
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)
    val tvMobNo = languageData[LanguageTranslationsResponse.ENTER_MOB].toString()
//    var apiResult by remember { mutableStateOf<String?>(null) }
    val strMobileNumber = rememberSaveable { mutableStateOf(viewModel.getUserPhoneNo()) }
//    val languageData = viewModel.getLanguageTranslationData()
    // dialog for Recover Account
    var confirmRecoverState by remember { mutableStateOf(false) }
    if (confirmRecoverState) {
        ConfirmationRecoverAccountBottomSheet(
            navController,
            viewModel,
            isDialogVisible,
            mobNo.value,
            languageData
        ) {
            confirmRecoverState = false
        }
    }

    // Only trigger the API call when the button is clicked
    if (buttonClicked) {
        LaunchedEffect(Unit) {
            mobNo.value.encryptAES()?.trim().let { viewModel.checkPhoneNo(it.toString()) }
            buttonClicked = false  // Reset the button click state
        }
    }

    BackHandler {
        if (!navController.popBackStack()) {
            // If unable to pop back (i.e., no more screens), finish the activity
            (context as? Activity)?.finish()
        }
    }

    /* DoubleBackPressHandler {
         if (!navController.popBackStack()) {
             // If unable to pop back (i.e., no more screens), finish the activity
             (context as? Activity)?.finish()
         }
     }*/

    DefaultBackgroundUi(
        isShowBackButton = true,
        onBackButtonClick = {
            if (!navController.popBackStack()) {
                // If unable to pop back (i.e., no more screens), finish the activity
                (context as? Activity)?.finish()
            }
//            navController.navigate(AppRoute.SelectRole.route)
        },
        content = {

            val welcome = languageData[LanguageTranslationsResponse.WELCOME].toString()
            Text(
                text = welcome,
                modifier = Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Black
            )

            val str = languageData[LanguageTranslationsResponse.START_LEARN].toString()
            Text(
                str,
                modifier = Modifier.padding(top = 0.dp, start = 10.dp, end = 10.dp, bottom = 10.dp),
                color = GrayLight01,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {

                // Clear response when the screen is disposed
                DisposableEffect(Unit) {
                    onDispose {
                        viewModel.clearResponse()
                    }
                }
                LaunchedEffect(Unit) {
                    viewModel.phoneNoResponse.observeForever { result ->
                        when (result) {
                            is NetworkStatus.Idle -> {}
                            is NetworkStatus.Loading -> {
                                isDialogVisible = true
                            }

                            is NetworkStatus.Success -> {
                                isDialogVisible = false
//                                apiResult = result.data?.error
                                if (result.data?.isSuccess == true) {
                                    if (isForgotPinPassword == FORGOT_PIN) {
                                        navController.navigate(
                                            AppRoute.VerifyOtp(
                                                mobNo.value,
                                                FORGOT_PIN
                                            )
                                        )
                                    } else if (isForgotPinPassword == FORGOT_PASSWORD) {
                                        navController.navigate(
                                            AppRoute.VerifyOtp(
                                                mobNo.value,
                                                FORGOT_PASSWORD
                                            )
                                        )
                                    } else {
                                        if (result.data?.data?.isActiveUser == 0) {
                                            confirmRecoverState = true
                                        } else {
                                            navController.navigate(AppRoute.EnterPassword(mobNo.value))
                                        }
                                    }
                                } else {
                                    navController.navigate(
                                        AppRoute.VerifyOtp(
                                            mobNo.value,
                                            LOGIN_WITH_PASSWORD
                                        )
                                    )
                                }
                            }

                            is NetworkStatus.Error -> {
                                isDialogVisible = false
                                if (result.message == "User does not exists") {
                                    context.toast("OTP sent successfully")
                                    navController.navigate(
                                        AppRoute.VerifyOtp(
                                            mobNo.value,
                                            LOGIN_WITH_PASSWORD
                                        )
                                    )
                                } else {
                                    context.toast(result.message)
                                }
                            }
                        }
                    }
                }

                CustomDialog(
                    isVisible = isDialogVisible,
                    onDismiss = { isDialogVisible = false },
                    message = "Loading your data..."
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

//                    val invalidMobNo = stringResource(id = R.string.text_enter_no)
                    val invalidMobNo =
                        languageData[LanguageTranslationsResponse.ENTER_MOB].toString()
                    val txtContinue = languageData[LanguageTranslationsResponse.CONTINUE].toString()
                    Text(
                        tvMobNo,
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Gray
                    )

                    // common mobile number input field
                    val enterMobile by remember { mutableStateOf(languageData[LanguageTranslationsResponse.ENTER_MOB].toString()) }
                    if (isForgotPinPassword == FORGOT_PIN) {
                        println("Your entered mobile number :- $strMobileNumber")
                        mobNo = strMobileNumber
                        MobileTextField(number = mobNo, trueFalse = false, hint = enterMobile)
                    } else if (isForgotPinPassword == EDIT_MOBILE) {
                        println("Your entered mobile number :- $strMobileNumber")
                        mobNo = strMobileNumber
                        MobileTextField(number = mobNo, trueFalse = true, hint = enterMobile)
                    } else if (isForgotPinPassword == FORGOT_PASSWORD) {
                        println("Your entered mobile number :- $strMobileNumber")
                        mobNo = strMobileNumber
                        MobileTextField(number = mobNo, trueFalse = false, hint = enterMobile)
                    } else if (isForgotPinPassword == ENTER_PASSWORD) {
                        println("Your entered mobile number :- $strMobileNumber")
                        mobNo = strMobileNumber
                        MobileTextField(number = mobNo, trueFalse = true, hint = enterMobile)
                    } else {
                        MobileTextField(number = mobNo, trueFalse = true, hint = enterMobile)
                    }
                    if (inValidMobNo) {
                        Text(
                            invalidMobNo, color = LightRed01,
                            modifier = Modifier.padding(start = 5.dp),
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Light,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {


                        var countNumber by remember { mutableStateOf<Int?>(null) }
                        if (mobNo.value.length == 10) {
                            countNumber = 1
                        } else {
                            countNumber = null
                        }

                        BtnUi(
                            txtContinue,
                            onClick = {
                                if (mobNo.value.isEmpty()) {
                                    Toast.makeText(
                                        context,
                                        "Please enter mobile number.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    showError = mobNo.value.isEmpty()
                                    val firstDigitChar = mobNo.value.toString().first()
                                    // Convert the first character back to an integer
                                    val firstDigit = firstDigitChar.digitToInt()
                                    if (showError || mobNo.value.length < 10) {
                                        inValidMobNo = true
                                    } else {
                                        // if first digit of mobile is less than 6 then error will show
                                        if (firstDigit < 6) {
                                            inValidMobNo = true
                                        } else {
                                            isDialogVisible = true
                                            viewModel.saveUserPhoneNo(mobNo.value)
                                            buttonClicked = true
                                        }
                                    }
                                }
                            }, countNumber != null
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.weight(1f)) // This will push the content below to the bottom

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.BottomCenter)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = languageData[LanguageTranslationsResponse.KEY_CONTINUE_AGREE].toString() + " ",
                                        color = Color.Black,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(start = 2.dp)
                                    )

                                    Text(
                                        text = languageData[LanguageTranslationsResponse.KEY_PRIVACY_POLICY].toString(),
                                        color = PrimaryBlue,
                                        fontSize = 12.sp,
                                        modifier = Modifier.clickable {
                                            val url =
                                                PRIVACY_POLICY // Replace with your privacy policy URL
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            context.startActivity(intent)
                                        }
                                    )

                                    Text(
                                        text = " " + languageData[LanguageTranslationsResponse.KEY_AND].toString(),
                                        color = Color.DarkGray,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(end = 2.dp)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = languageData[LanguageTranslationsResponse.KEY_TERMS_CONDITION].toString(),
                                        color = PrimaryBlue,
                                        fontSize = 12.sp,
                                        modifier = Modifier.clickable {
                                            val url =
                                                TERMS_CONDITION // Replace with your privacy policy URL
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            context.startActivity(intent)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationRecoverAccountBottomSheet(
    navController: NavHostController,
    viewModel: LoginViewModel,
    isDialogVisible: Boolean,
    phoneNo: String,
    languageData: HashMap<String, String>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var verifyPassword by remember { mutableStateOf(false) }
    // if pin verified successfully for delete account
    if (verifyPassword) {
        VerifyPasswordBottomSheet(navController, phoneNo, viewModel, context, languageData) {
            verifyPassword = false
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
                stringResource(id = R.string.txt_sure_recover),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            Text(
                stringResource(id = R.string.txt_sure_delete_desc),
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
                            text = stringResource(id = R.string.txt_cancel),
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
                            // Verify with password
                            verifyPassword = true

                        }, modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_recover),
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
fun VerifyOtpBottomSheet(
    navController: NavHostController,
    phoneNo: String,
    viewModel: LoginViewModel,
    context: Context,
    languageData: HashMap<String, String>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var invalidOtpText = stringResource(id = R.string.invalid_OTP)

    var otpValue by remember { mutableStateOf("") }
    var inValidOTP by remember { mutableStateOf(false) }
    var successTitle by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    var successSheet by remember { mutableStateOf(false) }

    // show Success sheet while click on logout
    if (successSheet) {
        SuccessBottomSheet(
            navController,
            successTitle,
            successMessage,
            viewModel,
            languageData = languageData
        ) {
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
//                    context.toast(it.message)
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
                            invalidOtpText = "Invalid Pin"
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
                        successTitle = "Account Successfully Recovered!"
                        successMessage =
                            it.data.data.message
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
                languageData[LanguageTranslationsResponse.ENTER_OTP].toString(),
//                stringResource(id = R.string.text_enter_otp),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                stringResource(id = R.string.txt_otp_has_been_sent),
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
                                color = Transparent,
                                shape = RoundedCornerShape(12.dp)
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
                            text = languageData[LanguageTranslationsResponse.VERIFY_OTP].toString(),
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
fun VerifyPasswordBottomSheet(
    navController: NavHostController,
    phoneNo: String,
    viewModel: LoginViewModel,
    context: Context,
    languageData: HashMap<String, String>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val password = remember { mutableStateOf("") }
    val showPassword = remember { mutableStateOf(false) }
    var inValidPassword by remember { mutableStateOf(false) }
    val invalidPassword = languageData[LanguageTranslationsResponse.ENTER_PASS].toString()
    var textInvalidPassword by remember { mutableStateOf(invalidPassword) }
    val enterYourPassword =
        languageData[LanguageTranslationsResponse.KEY_ENTER_YOUR_PASS].toString()
    var showError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var verifyOTPSheet by remember { mutableStateOf(false) }

    if (verifyOTPSheet) {
        VerifyOtpBottomSheet(
            navController,
            phoneNo,
            viewModel,
            context,
            languageData = languageData
        ) {
            verifyOTPSheet = false
        }
    }

    LaunchedEffect(Unit) {
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
                        viewModel.saveUserId(it.data.data.userDetails.userId)
                        viewModel.saveUserPin(it.data.data.userDetails.isPinSet!!)
                        viewModel.setLoginInfo(it.data.data.userDetails)
                        viewModel.sendOtp(phoneNo.encryptAES().toString())

                    } else {
                        inValidPassword = true
                        textInvalidPassword = it.data!!.error
                    }
                }

                is NetworkStatus.Error -> {
                    inValidPassword = true
                    textInvalidPassword = it.message

                }
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
//                    context.toast(it.message)
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
            Icon(
                painter = painterResource(id = R.drawable.ic_lock_bg), // Replace with your drawable resource
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Unspecified),
            )

            Text(
                stringResource(id = R.string.txt_verify_password),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            PasswordTextField(
                password = password,
                showPassword = showPassword,
                hint = enterYourPassword
            )

            if (inValidPassword) {
                Text(
                    textInvalidPassword, color = LightRed01,
                    modifier = Modifier.padding(start = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Light,
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
                            onDismiss()
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_cancel),
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
                            showError = password.value.isEmpty()
                            if (showError || password.value.length < 5) {
                                inValidPassword = true
                            } else {
                                inValidPassword = false
                                phoneNo.toString().encryptAES()?.trim()
                                    ?.let {
                                        viewModel.loginRequestCall(
                                            phoneNo = it,
                                            password = password.value
                                        )
                                    }
                            }

                        },

                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_confirm),
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
fun SuccessBottomSheet(
    navController: NavHostController,
    title: String,
    description: String,
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

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
                    navController.navigate(
                        AppRoute.ChildList(
                            viewModel.getUserId(),
                            viewModel.getUserPin().toString()
                        )
                    )
                },

                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.okay_got_it),
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
fun DefaultPreview() {
    AuroscholarAppTheme {
        LoginScreen(
            navController = rememberNavController(),
            isForgotPinPassword = "isForgotPinPassword",
            mobileNo = ""
        )
    }
}
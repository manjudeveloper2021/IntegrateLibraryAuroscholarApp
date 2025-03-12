package com.auro.application.ui.features.login.screens

import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.App
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.Constants
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.api.aes.AESEncryption.encryptAES
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.DefaultBackgroundUi
import com.auro.application.ui.common_ui.PasswordTextField
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.models.SaveReferralModel
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.BackgroundGray
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.LightRed01
import com.auro.application.ui.theme.PrimaryBlue

@Composable
fun CreatePasswordScreen(navController: NavHostController, args: String?, sharedPref: SharedPref?) {

    val viewModel: LoginViewModel = hiltViewModel()
    val viewModelStudent: StudentViewModel = hiltViewModel()
    val context = LocalContext.current
    var isDialogVisible by remember { mutableStateOf(false) }
    var inValidPassword by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var createPasswordText by remember { mutableStateOf("") }
    var str by remember { mutableStateOf("") }
    var referredBy by remember { mutableStateOf(0) }
    var isReferred by remember { mutableStateOf(false) }
    var referredTypeId by remember { mutableStateOf(0) }

    val passKey: String = viewModel.getForgotPassword().toString()

    referredBy = viewModelStudent.getReferredBy()!!.toInt()
    isReferred = viewModelStudent.getIsReferred()!!
    referredTypeId = viewModelStudent.getReferredTypeId()!!.toInt()

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    // Handle back press
    val onBackPressed = {
        if (isDialogVisible) {
            // If the dialog is visible, dismiss it
            isDialogVisible = false
        } else {
            // If the dialog is not visible, navigate back to the previous screen
            navController.popBackStack()
        }
    }

    // Set up the back press callback
    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
    }

    BackHandler {
        navController.navigateUp()
        navController.popBackStack()

    }

    // Clear response when the screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        if (passKey == "forgotPassword") {
            viewModel.passwordResetRequestModel.observeForever {
                when (it) {
                    is NetworkStatus.Idle -> {
                        isDialogVisible = false
                    }

                    is NetworkStatus.Loading -> {
                        isDialogVisible = true
                    }

                    is NetworkStatus.Success -> {
                        isDialogVisible = false
                        if (it.data?.isSuccess == true) {
                            Toast.makeText(
                                context,
                                languageData[LanguageTranslationsResponse.KEY_PASS_RESET].toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate(
                                AppRoute.EnterPassword(
                                    viewModel.getUserPhoneNo().encryptAES()
                                )
                            )
                        }
                    }

                    is NetworkStatus.Error -> {
                        isDialogVisible = false
                        errorMsg = it.message.toString()
                        inValidPassword = true
                    }
                }
            }
        } else {
            viewModel.passwordCreateLiveResponse.observeForever {
                when (it) {
                    is NetworkStatus.Idle -> {
                        isDialogVisible = false
                    }

                    is NetworkStatus.Loading -> {
                        isDialogVisible = true
                    }

                    is NetworkStatus.Success -> {
                        isDialogVisible = false/* if (it.data?.isSuccess == true) {
                         viewModel.saveToken(it.data.data?.token)
                         Constants.token = viewModel.getToken().toString()
                         sharedPref?.saveUserId(it.data.data.userDetails.userId)
                         navController.navigate(AppRoute.ChildList(viewModel.getUserId(),"0"))
                     }*/
                        if (it.data?.isSuccess == true) {
                            val token = it.data.data?.token
                            if (token != null) {
                                viewModel.saveToken(token)
                                Constants.token = token

                                val userDetails = it.data.data.userDetails
                                val userId = userDetails?.userId
                                sharedPref?.saveUserId(userId)
                                viewModel.saveParentInfo(it.data.data.userDetails)
                                if (isReferred) {
                                    isDialogVisible = true
                                    viewModelStudent.saveReferralCode(
                                        referredBy,          // User Id of teacher or person referred by
                                        userId!!.toInt(),     //  User Id of current user
                                        referredTypeId,
                                        "true",
                                        "Data saved successfully"
                                    )
                                }
                                else {
                                    val childListRoute =
                                        AppRoute.ChildList(viewModel.getUserId(), "0")
                                    navController.navigate(childListRoute)
                                }
                            }
                        }
                    }

                    is NetworkStatus.Error -> {
                        isDialogVisible = false
                        errorMsg = it.message.toString()
                        inValidPassword = true
//                    context.toast(it.message)
                    }
                }
            }
        }

        viewModelStudent.saveReferralResponseData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {
                    isDialogVisible = false
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        context.toast(it.data.data.toString())
                        val childListRoute =
                            AppRoute.ChildList(viewModel.getUserId(), "0")
                        navController.navigate(childListRoute)
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
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

    DefaultBackgroundUi(isShowBackButton = true, onBackButtonClick = {
        navController.popBackStack()
        navController.navigateUp()
    }, content = {
//            var createPasswordText = stringResource(id = R.string.txt_create_password)
        if (passKey == "forgotPassword") {
//            createPasswordText = stringResource(id = R.string.txt_reset_password)
            createPasswordText =
                languageData[LanguageTranslationsResponse.KEY_RESET_PASS].toString()
        } else {
//            createPasswordText = stringResource(id = R.string.txt_create_password)
            createPasswordText = languageData[LanguageTranslationsResponse.CREATE_PASS].toString()
        }
//        val enterPassword = stringResource(id = R.string.enter_password)
        val enterPassword = languageData[LanguageTranslationsResponse.ENTER_PASS].toString()
//        val enterYourPassword = stringResource(id = R.string.enter_your_password)
        val enterYourPassword =
            languageData[LanguageTranslationsResponse.KEY_ENTER_YOUR_PASS].toString()
//        val confirmPasswordText = stringResource(id = R.string.confirm_password)
        val confirmPasswordText = languageData[LanguageTranslationsResponse.CONFIRM_PASS].toString()
//        val confirmYourPassword = stringResource(id = R.string.confirm_your_password)
        val confirmYourPassword =
            languageData[LanguageTranslationsResponse.KEY_CONF_PASS].toString()
        Text(
            text = createPasswordText,
            modifier = Modifier.padding(top = 20.dp, start = 15.dp, end = 15.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Black
        )

//            val str = stringResource(id = R.string.create_password_signup)
        if (passKey == "forgotPassword") {
//            str = stringResource(id = R.string.reset_password_signup)
            str = languageData[LanguageTranslationsResponse.KEY_SET_NEW_PASS_LOGIN].toString()
        } else {
//            str = stringResource(id = R.string.create_password_signup)
            str = languageData[LanguageTranslationsResponse.SECURE_FUTURE_LOGINS].toString()
        }
        Text(
            str,
            modifier = Modifier.padding(top = 0.dp, start = 15.dp, end = 15.dp, bottom = 10.dp),
            color = GrayLight01,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )
        Text(
            enterPassword, modifier = Modifier.padding(
                top = 20.dp, start = 15.dp, end = 15.dp, bottom = 10.dp
            ), color = BackgroundGray, fontWeight = FontWeight.Medium, fontSize = 14.sp
        )

        val password = remember { mutableStateOf("") }
        val showPassword = remember { mutableStateOf(false) }

        PasswordTextField(
            password = password, showPassword = showPassword, hint = enterYourPassword
        )

        Text(
            confirmPasswordText, modifier = Modifier.padding(
                top = 20.dp, start = 15.dp, end = 15.dp, bottom = 10.dp
            ), color = BackgroundGray, fontWeight = FontWeight.Medium, fontSize = 14.sp
        )

        val confirmPassword = remember { mutableStateOf("") }
        val showConfirmPassword = remember { mutableStateOf(false) }

        PasswordTextField(
            password = confirmPassword,
            showPassword = showConfirmPassword,
            hint = confirmYourPassword
        )

        if (inValidPassword) {
            Text(
                errorMsg,
                color = LightRed01,
                modifier = Modifier.padding(start = 10.dp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp, start = 10.dp, end = 10.dp)
        ) {
            BtnUi(/*stringResource(id = R.string.txt_create)*/
                languageData[LanguageTranslationsResponse.KEY_CREATE].toString(),
                onClick = {
                    if (password.value.isEmpty()) {
//                    errorMsg = "Enter your password"
                        errorMsg =
                            languageData[LanguageTranslationsResponse.KEY_ENTER_YOUR_PASS].toString()
                        inValidPassword = true
                    } else if (confirmPassword.value.isEmpty()) {
//                    errorMsg = "Enter your confirm password"
                        errorMsg =
                            languageData[LanguageTranslationsResponse.KEY_CONFIRM_PASS].toString()
                        inValidPassword = true
                    } else {
                        if (password.value == confirmPassword.value) {
                            if (passKey == "forgotPassword") {
                                viewModel.getUserPhoneNo().encryptAES()?.let {
                                    viewModel.getResetPassword(
                                        it, viewModel.getUserType().toString(), password.value
                                    )
                                }
                            } else {
                                inValidPassword = false
                                viewModel.getUserPhoneNo().encryptAES()?.let {
                                    viewModel.setPassword(
                                        password = password.value, smsNumber = it
                                    )
                                }
                            }
                        } else {
//                        errorMsg = "Confirm Password & Password do not match"
                            errorMsg =
                                languageData[LanguageTranslationsResponse.KEY_NOT_MATCH_PASS].toString()
                            inValidPassword = true
                        }
                    }
                },
                true
            )
        }
    })
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun CreatePassword() {
    CreatePasswordScreen(
        navController = rememberNavController(), args = "", sharedPref = null
    )
}
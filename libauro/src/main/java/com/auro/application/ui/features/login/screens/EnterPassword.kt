package com.auro.application.ui.features.login.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
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
import com.auro.application.core.ConstantVariables.ENTER_PASSWORD
import com.auro.application.core.ConstantVariables.FORGOT_PASSWORD
import com.auro.application.core.ConstantVariables.LOGIN
import com.auro.application.core.ConstantVariables.LOGIN_WITH_OTP
import com.auro.application.core.ConstantVariables.LOGIN_WITH_PIN
import com.auro.application.core.ConstantVariables.isLogout
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.Constants.PRIVACY_POLICY
import com.auro.application.data.api.Constants.TERMS_CONDITION
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.api.aes.AESEncryption.encryptAES
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.DefaultBackgroundUi
import com.auro.application.ui.common_ui.PasswordTextField
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.components.CreatePinDoubleBackPressHandler
import com.auro.application.ui.common_ui.components.DoubleBackPressHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.LoginMainActivity
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightRed01
import com.auro.application.ui.theme.PrimaryBlue

@Composable
fun EnterPassword(navController: NavHostController, userPhoneNo: String?) {
    val viewModel: LoginViewModel = hiltViewModel()
    var isDialogVisible by remember { mutableStateOf(false) }
    val password = remember { mutableStateOf("") }
    val showPassword = remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var inValidPassword by remember { mutableStateOf(false) }
    lateinit var appclass: App
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

//    val invalidPassword = stringResource(id = R.string.invalid_password)
    val invalidPassword = languageData[LanguageTranslationsResponse.ENTER_PASS].toString()
    var textInvalidPassword by remember { mutableStateOf(invalidPassword) }
//    val enterPassword = stringResource(id = R.string.enter_password)
    val enterPassword = invalidPassword
//    val enterYourPassword = stringResource(id = R.string.enter_your_password)
    val enterYourPassword =
        languageData[LanguageTranslationsResponse.KEY_ENTER_YOUR_PASS].toString()
    val context = LocalContext.current

//    CreatePinDoubleBackPressHandler(onExit = {
//        viewModel.saveScreenName(isLogout)
//        viewModel.clearPreferenceData(context)
//        context.startActivity(Intent(context, LoginMainActivity::class.java))
//            .also {
//                if (context is Activity) {
//                    context.finish()
//                }
//            }
//    })

    BackHandler {
        navController.navigateUp()
        navController.popBackStack()
        navController.navigate(AppRoute.Login(ENTER_PASSWORD))

    }

//    DoubleBackPressHandler {
//        if (!navController.popBackStack()) {
//            // If unable to pop back (i.e., no more screens), finish the activity
//            (context as? Activity)?.finish()
//        }
//    }

    DefaultBackgroundUi(
        isShowBackButton = true,
        onBackButtonClick = {
            navController.navigateUp()
            navController.popBackStack()
            navController.navigate(AppRoute.Login(ENTER_PASSWORD))
        },
        content = {

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
                                viewModel.setLoginInfo(it.data.data?.userDetails)
                                viewModel.saveUserName(it.data.data?.userDetails!!.name)
                                viewModel.saveUserEmail(it.data.data.userDetails.email)
                                viewModel.saveUserImage(it.data.data.userDetails.profilePic)
                                viewModel.saveParentInfo(it.data.data.userDetails)
                                viewModel.saveUserId(it.data.data.userDetails.userId)
                                viewModel.saveToken(it.data.data.token.toString())

                                if (it.data.data.userDetails.isPinSet == false) {
                                    viewModel.saveUserPin(false)
                                } else {
                                    viewModel.saveUserPin(true)
                                }

                                val userPin = viewModel.getUserPin()
                                val userId = viewModel.getUserId()
                                navController.navigate(
                                    AppRoute.ChildList(
                                        userId,
                                        userPin.toString()
                                    )
                                )

                            } else {
                                inValidPassword = true
                                textInvalidPassword = it.data!!.error
                            }
                        }

                        is NetworkStatus.Error -> {
                            isDialogVisible = false
                            inValidPassword = true
                            textInvalidPassword = it.message
//                            navController.navigate(AppRoute.VerifyOtp(mobNo.value))
                        }
                    }
                }
            }

            val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()
            CustomDialog(
                isVisible = isDialogVisible,
                onDismiss = { isDialogVisible = false },
//                message = "Loading your data..."
                message = msgLoader
            )
            Text(
                text = enterPassword,
                modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )

            val str =
                languageData[LanguageTranslationsResponse.KEY_ENTER_PASS_TO_LOGIN].toString() ?: ""
            Text(
                str,
                modifier = Modifier.padding(top = 0.dp, start = 10.dp, end = 10.dp, bottom = 10.dp),
                textAlign = TextAlign.Start,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Gray
            )

            Text(
                enterPassword,
                modifier = Modifier.padding(
                    top = 20.dp,
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 10.dp
                ),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            PasswordTextField(
                password = password,
                showPassword = showPassword,
                hint = enterYourPassword
            )

            if (inValidPassword) {
                Text(
                    textInvalidPassword, color = LightRed01,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Light,
                    fontSize = 10.sp
                )
            }

            Text(
                stringResource(id = R.string.forgot_your_password),
                modifier = Modifier
                    .padding(top = 5.dp, start = 15.dp, end = 15.dp, bottom = 10.dp)
                    .fillMaxWidth()
                    .clickable {
                        // login with pin needed phn no only
                        navController.navigate(
                            AppRoute.Login(
                                FORGOT_PASSWORD
                            )
                        )
                    },
                textAlign = TextAlign.End, // Add this line to align the text to the right
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = PrimaryBlue
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BtnUi(/*"Login"*/ languageData[LanguageTranslationsResponse.KEY_LOGIN].toString(),
                    onClick = {
                        if (password.value.isEmpty()) {

                        }
                        showError = password.value.isEmpty()
                        if (showError || password.value.length < 5) {
                            inValidPassword = true
                        } else {
                            inValidPassword = false
                            isDialogVisible = true
                            userPhoneNo.toString().encryptAES()?.trim()
                                ?.let {
                                    viewModel.loginRequestCall(
                                        phoneNo = it,
                                        password = password.value
                                    )
                                }
                        }
                    },
                    true
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left line
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp),
                    shape = RoundedCornerShape(corner = CornerSize(4.dp)),
                    color = GrayLight02
                ) {
                    // Empty surface to create a line
                }

                // Text
                Text(
                    modifier = Modifier.padding(horizontal = 15.dp),
//                    text = "or login with",
                    text = languageData[LanguageTranslationsResponse.KEY_OR_LOGIN_WITH].toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start
                    )
                )

                // Right line
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp),
                    shape = RoundedCornerShape(corner = CornerSize(4.dp)),
                    color = GrayLight02
                ) {
                    // Empty surface to create a line
                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Spacer(modifier = Modifier.width(30.dp)) // Add left spacer

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = 1.dp,
                            color = GrayLight02,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(end = 12.dp) // Add right padding to Column 1
                        .height(IntrinsicSize.Max) // Set max height to 100.dp
                        .height(70.dp) // Set max height to 100.dp
                ) {
                    // Column 1 Content
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(10.dp)
                            .clickable {
                                navController.navigate(
                                    AppRoute.VerifyOtp(
                                        userPhoneNo,
                                        LOGIN_WITH_OTP
                                    )
                                )
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_otp),
                            contentDescription = "Image",
                            modifier = Modifier
                                .size(50.dp)
                                .padding(5.dp)
                                .background(Color.Unspecified)
                        )

                        Text(
//                            text = stringResource(R.string.otp),
                            text = languageData[LanguageTranslationsResponse.KEY_OTP].toString(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(alignment = Alignment.CenterVertically)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp)) // Add spacer between columns

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = 1.dp,
                            color = GrayLight02,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(start = 12.dp) // Add left padding to Column 2
                        .height(IntrinsicSize.Max) // Set max height to 100.dp
                        .height(70.dp)
                    // Set max height to 100.dp
                ) {

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(10.dp)
                            .clickable {
                                Log.d("userPhoneNo:", "" + userPhoneNo)
                                navController.navigate(
                                    AppRoute.EnterPin(
                                        userPhoneNo,
                                        LOGIN_WITH_PIN, "" // login with pin needed phn no only
                                    )
                                )
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_unlock),
                            contentDescription = "Image",
                            modifier = Modifier
                                .size(50.dp)
                                .padding(5.dp)
                                .background(Color.Unspecified)
                        )

                        Text(
//                            text = stringResource(R.string.pin),
                            text = languageData[LanguageTranslationsResponse.KEY_PIN].toString(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(alignment = Alignment.CenterVertically)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(30.dp)) // Add right spacer

            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                // Your content here

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
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun EnterPassword() {
    val navController = rememberNavController() // Create a NavHostController instance
    EnterPassword(navController, "") // Pass the navController instance
}
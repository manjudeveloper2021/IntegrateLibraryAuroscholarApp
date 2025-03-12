package com.auro.application.ui.features.login.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.App
import com.auro.application.R
import com.auro.application.core.ConstantVariables.FORGOT_PIN
import com.auro.application.core.ConstantVariables.LOGIN
import com.auro.application.core.ConstantVariables.createpin
import com.auro.application.core.ConstantVariables.isLogout
import com.auro.application.core.ConstantVariables.isRegistration
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.api.aes.AESEncryption.encryptAES
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.DefaultBackgroundUi
import com.auro.application.ui.common_ui.PinInputUi
import com.auro.application.ui.common_ui.components.CreatePinDoubleBackPressHandler
import com.auro.application.ui.common_ui.components.DoubleBackPressHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.LoginMainActivity
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.ParentDashboardActivity
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.LightRed01

fun NavHostController.navigateAndClean(route: String) {
    navigate(route = route) {
        popUpTo(graph.startDestinationId) { inclusive = true }
    }
    graph.setStartDestination(route)
}

@Composable
fun CreatePinScreen(
    navController: NavHostController,
    sharedPref: SharedPref?,
    viewModel: LoginViewModel,
    userId: String?,
    isForgotPinPassword: String?,
) {
    val context: Context = LocalContext.current
    val password = remember { mutableStateOf("") }
    val showPassword = remember { mutableStateOf(false) }
    val confirmPin = remember { mutableStateOf("") }
    val confirmPinInput = remember { mutableStateOf(false) }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    var createPin = ""
    val psw = password.value
    val cnfpsw = confirmPin.value
    val userTypeId = viewModel.getUserId()
    var inValidPin by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
//    var enterPin by remember { mutableStateOf("Enter PIN") }
    var enterPin by remember { mutableStateOf(languageData[LanguageTranslationsResponse.ENTER_PIN].toString()) }
//    var enterConfirmPin by remember { mutableStateOf("Enter Confirm PIN") }
    var enterConfirmPin by remember { mutableStateOf(languageData[LanguageTranslationsResponse.KEY_CONFIRM_PIN].toString()) }
    var isDialogVisible by remember { mutableStateOf(false) }

    var isPin by remember { mutableStateOf(false) }

    Log.d("isForgotPinPassword", " .. $isForgotPinPassword ")
    if (isForgotPinPassword != FORGOT_PIN)
        viewModel.saveScreenName(createpin)

//    DoubleBackPressHandler {
//        viewModel.saveScreenName("")
//        navController.navigate(AppRoute.Login()) {
//            popUpTo(navController.graph.findStartDestination().id) {
//                inclusive = true
//            }
//        }
//    }

    CreatePinDoubleBackPressHandler(onExit = {
        viewModel.saveScreenName(isLogout)
        viewModel.clearPreferenceData(context)
        context.startActivity(Intent(context, LoginMainActivity::class.java))
            .also {
                if (context is Activity) {
                    context.finish()
                }
            }
    })

    // Clear response when the screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
//        message = "Loading your data..."
        message = msgLoader
    )

    LaunchedEffect(Unit) {
        viewModel.setPinResponseLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (isPin) {
                        Toast.makeText(context, it.data!!.data, Toast.LENGTH_SHORT).show()
                        isDialogVisible = false
                        viewModel.saveUserPin(true)
                        if (sharedPref?.getUserTypeId() == "1") {

                            // Move to Child list & let decide there, where to go
                            //Parent Dashboard

                            when (isForgotPinPassword) {
                                FORGOT_PIN -> {
                                    navController.navigate(AppRoute.Login(LOGIN))
                                    /*viewModel.saveScreenName(isLogout)
                                viewModel.clearPreferenceData(context)
                                context.startActivity(
                                    Intent(
                                        context,
                                        LoginMainActivity::class.java
                                    )
                                ).also {
                                    if (context is Activity) {
                                        context.finish()
                                    }
                                }*/
                                }

                                else -> {
                                    if (viewModel.getAddStudentFromParentDashboard() != "ParentDashboard") {
                                        navController.navigate(
                                            AppRoute.ChildList(
                                                viewModel.getUserId(), "1"
                                            )
                                        )
                                    } else {
                                        context.startActivity(Intent(
                                            context, ParentDashboardActivity::class.java
                                        ).apply { (context as Activity).finish() })
                                    }
                                }
                            }
                        } else if (sharedPref?.getUserTypeId() == "2") {

                            when (isForgotPinPassword) {
                                FORGOT_PIN -> {/*  context.startActivity(
                                          Intent(
                                              context,
                                              StudentDashboardActivity::class.java
                                          ).apply {
                                              putExtra(isRegistration, isRegistration)
                                          })*/

                                    navController.navigate(AppRoute.Login(LOGIN))
                                    /* viewModel.saveScreenName(isLogout)
                                 viewModel.clearPreferenceData(context)
                                 context.startActivity(
                                     Intent(
                                         context,
                                         LoginMainActivity::class.java
                                     )
                                 ).also {
                                     if (context is Activity) {
                                         context.finish()
                                     }
                                 }*/
                                }

                                else -> {
                                    navController.navigate(
                                        AppRoute.ChildList(
                                            viewModel.getUserId(), "1"
                                        )
                                    )
                                }
                            }
                        } else if (sharedPref?.getUserTypeId() == "3") {
                            when (isForgotPinPassword) {
                                FORGOT_PIN -> {
                                    /* context.startActivity(Intent(
                                     context, StudentDashboardActivity::class.java
                                 ).apply {
                                     putExtra(isRegistration, isRegistration)
                                 })*/

                                    /*viewModel.saveScreenName(isLogout)
                                viewModel.clearPreferenceData(context)
                                context.startActivity(
                                    Intent(
                                        context,
                                        LoginMainActivity::class.java
                                    )
                                ).also {
                                    if (context is Activity) {
                                        context.finish()
                                    }
                                }*/
                                    navController.navigate(AppRoute.Login(LOGIN))
                                }

                                else -> {
                                    navController.navigate(
                                        AppRoute.ChildList(
                                            viewModel.getUserId(), "1"
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    errorMsg = it.message.toString()
                    inValidPin = true
//                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    DefaultBackgroundUi(isShowBackButton = false, onBackButtonClick = {

    }, content = {
        createPin = if (isForgotPinPassword == FORGOT_PIN) {
//            stringResource(id = R.string.reset_pin)
            languageData[LanguageTranslationsResponse.KEY_RESET_PIN].toString()
        } else {
//            stringResource(id = R.string.create_pin)
            languageData[LanguageTranslationsResponse.CREATE_PIN].toString()
        }
        Text(
            text = createPin,
            modifier = Modifier.padding(top = 20.dp, start = 15.dp, end = 15.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )

        Text(
//            stringResource(id = R.string.create_a_pin_for_secure_access_of_your_account),
            text = languageData[LanguageTranslationsResponse.SECURE_ACCESS_PIN].toString(),
            modifier = Modifier.padding(top = 0.dp, start = 15.dp, end = 15.dp, bottom = 10.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Normal
        )

        Text(
//            stringResource(id = R.string.enter_pin),
            text = languageData[LanguageTranslationsResponse.ENTER_PIN].toString(),
            modifier = Modifier.padding(top = 20.dp, start = 15.dp, end = 15.dp, bottom = 0.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp
        )


        PinInputUi(
            password = password, showPassword = showPassword, enterPIN = enterPin
        )

        Text(
//            stringResource(id = R.string.confirm_pin),
            text = languageData[LanguageTranslationsResponse.CONFIRM_PIN].toString(),
            modifier = Modifier.padding(top = 20.dp, start = 15.dp, end = 15.dp, bottom = 0.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp
        )

        PinInputUi(
            password = confirmPin,
            showPassword = confirmPinInput,
            isError = remember { mutableStateOf(false) },
            enterPIN = enterConfirmPin
        )

        if (inValidPin) {
            Text(
                errorMsg,
                color = LightRed01,
                modifier = Modifier.padding(start = 10.dp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp
            )
        }

        BtnUi(title = "Create", onClick = {
            if (psw.isEmpty()) {
//                errorMsg = "Please Enter Pin"
                errorMsg = languageData[LanguageTranslationsResponse.KEY_PLS_ENTER_PIN].toString()
                inValidPin = true
            } else if (cnfpsw.isEmpty()) {
//                errorMsg = "Please Enter Confirm Pin"
                errorMsg =
                    languageData[LanguageTranslationsResponse.KEY_PLS_ENTER_CONF_PIN].toString()
                inValidPin = true
            } else if (psw != cnfpsw) {
//                errorMsg = "Pin and Confirm Pin should be same"
                errorMsg =
                    languageData[LanguageTranslationsResponse.KEY_PIN_SHOULD_BE_SAME].toString()
                inValidPin = true
            } else {
                when (isForgotPinPassword) {
                    FORGOT_PIN -> {     // if user coming from Forgot pin
                        val user = viewModel.getUserPhoneNo().encryptAES().toString()
//                        viewModel.getResetPin(user, password.value)
                        println("User type data :- $userTypeId")
                        if (userTypeId != null) {
                            isDialogVisible = true
                            isPin = true
                            viewModel.getResetPin(user, password.value, userTypeId.toInt())
                        } else {
                            isPin = true
                            isDialogVisible = true
                            viewModel.getResetPin(user, password.value, 0)
                        }
                    }

                    else -> {
                        Log.d("CreatePIn", "" + userId + " .. " + viewModel.getUserId())
                        if (viewModel.getUserId() != null) {
                            isDialogVisible = true
                            isPin = true
                            viewModel.getSetPin(viewModel.getUserId()!!, password.value)
                        } else {
                            isPin = true
                            isDialogVisible = true
                            viewModel.getSetPin(userId!!, password.value)
                        }
                    }
                }
            }

//            if (psw.isEmpty() || cnfpsw.isEmpty()) {
//                errorMsg = "Confirm Password & Pin Should not be empty"
//                inValidPin = true
//            } else {
//                if (psw == cnfpsw) {
//                    Log.d("isForgotPinPassword", " .. $isForgotPinPassword .. $psw")
//                    when (isForgotPinPassword) {
//                        FORGOT_PIN -> {     // if user coming from Forgot pin
//                            val user = viewModel.getUserPhoneNo().encryptAES().toString()
//                            viewModel.getResetPin(user, password.value)
//                        }
//
//                        else -> {
//                            if (userId != null) viewModel.getSetPin(userId, password.value)
//                        }
//                    }
//                } else {
//                    errorMsg = "Confirm Pin & Pin do not match"
//                    inValidPin = true
//                }
//            }
        }, true)
    })
}

@Preview(showBackground = true)
@Composable
fun CreatePinPreview() {
    AuroscholarAppTheme {
        CreatePinScreen(
            navController = rememberNavController(),
            sharedPref = null,
            viewModel = hiltViewModel(),
            userId = null,
            isForgotPinPassword = ""
        )
    }
}

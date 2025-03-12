package com.auro.application.ui.features.login

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.auro.application.App
import com.auro.application.core.ConstantVariables.addNewChild
import com.auro.application.core.ConstantVariables.createpin
import com.auro.application.core.ConstantVariables.onboarding1
import com.auro.application.core.ConstantVariables.onboarding2
import com.auro.application.data.api.Constants
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.CommandAppKey
import com.auro.application.ui.features.login.parent.screens.ParentRegistrationStep1Screen
import com.auro.application.ui.features.login.parent.screens.ParentRegistrationStep2Screen
import com.auro.application.ui.features.login.screens.ChildListScreen
import com.auro.application.ui.theme.AuroscholarAppTheme
import dagger.hilt.android.AndroidEntryPoint
import com.auro.application.ui.features.login.screens.ChooseLanguage
import com.auro.application.ui.features.login.screens.ChooseUserTypeUI
import com.auro.application.ui.features.login.screens.CreatePinScreen
import com.auro.application.ui.features.login.screens.CreatePasswordScreen
import com.auro.application.ui.features.login.screens.EnterPassword
import com.auro.application.ui.features.login.screens.EnterPinScreen
import com.auro.application.ui.features.login.screens.LoginScreen
import com.auro.application.ui.features.login.screens.StudentRegistrationStep1Screen
import com.auro.application.ui.features.login.screens.StudentRegistrationStep2Screen
import com.auro.application.ui.features.login.screens.RegistrationNotice
import com.auro.application.ui.features.login.screens.SelectSubjectScreen
import com.auro.application.ui.features.login.screens.VerifyOTPScreen
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import javax.inject.Inject

@Suppress("IMPLICIT_CAST_TO_ANY")
@AndroidEntryPoint
class LoginMainActivity : ComponentActivity() {
    @Inject
    lateinit var sharedPref: SharedPref
    private val viewModel: LoginViewModel by viewModels()
    private val appclass: App? = null
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        appclass?.disableANR()
        setContent {
            val loadScreenName = intent.getStringExtra(CommandAppKey.screenName)

            Log.e("TAG", "onCreate: login main activity $loadScreenName")

            Log.e("TAG", "onCreate: Screen name ---> "+viewModel.getScreenName() )

            val startDestination = if (viewModel.getScreenName() == onboarding2) {
                AppRoute.RegistrationStep2()
            } else if (loadScreenName == onboarding1 || viewModel.getScreenName() == onboarding1) {
                AppRoute.RegistrationStep1()
            } else if (viewModel.getScreenName() == createpin) {
                AppRoute.CreatePin()
            } else if (viewModel.getScreenName() == Constants.parentStep1) {
                AppRoute.ParentRegistrationStep1()
            } else if (viewModel.getScreenName() == addNewChild) {
                val userId = viewModel.getUserId().toString()
                val userPin = viewModel.getUserPin().toString()

                AppRoute.ChildList(userId, userPin)
            } else {
                AppRoute.LanguageSelect
            }
            AuroscholarAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.background(Color.White)


                ) {
                    composable<AppRoute.LanguageSelect>() {
                        ChooseLanguage(navController)
                    }
                    composable(AppRoute.SelectRole.route) {
                        ChooseUserTypeUI(navController)
                    }

                    composable<AppRoute.Login> { backStackEntry ->
                        val args = backStackEntry.toRoute<AppRoute.Login>()
                        val isForgotPinPassword = args.isForgotPinPassword
                        val mobileNumber = backStackEntry.savedStateHandle.get<String>("mobile") ?: ""
                        LoginScreen(navController, isForgotPinPassword, mobileNumber)
                    }

                    composable<AppRoute.VerifyOtp>() { backStackEntry ->
                        val args = backStackEntry.toRoute<AppRoute.VerifyOtp>()
                        val phoneNo = args.phoneNo
                        val isLoginWithOtp = args.isLoginWithOtp
                        Log.d("VerifyOTPScreen:", "$phoneNo .. $isLoginWithOtp")
                        VerifyOTPScreen(navController, phoneNo, isLoginWithOtp)
                    }

                    composable<AppRoute.CreatePassword>() {
                        val args = it.toRoute<AppRoute.CreatePassword>()
                        val user = args.user
                        CreatePasswordScreen(navController, user, sharedPref)
                    }
                    composable<AppRoute.EnterPassword>() {
                        val args = it.toRoute<AppRoute.EnterPassword>()
                        val userPhoneNo = args.user
                        EnterPassword(navController, userPhoneNo)
                    }
                    composable<AppRoute.ChildList>() {
                        val args = it.toRoute<AppRoute.ChildList>()
                        val isUserPin = args.isUserPin
                        ChildListScreen(
                            navController,
                            this@LoginMainActivity,
                            viewModel,
                            sharedPref,isUserPin
                        )
                    }
                    composable<AppRoute.CreatePin> {
                        val args = it.toRoute<AppRoute.CreatePin>().userId
                        val isForgotPinPassword =
                            it.toRoute<AppRoute.CreatePin>().isForgotPinPassword

                        CreatePinScreen(
                            navController,
                            sharedPref = sharedPref,
                            viewModel,
                            userId = args,
                            isForgotPinPassword
                        )
                    }

                    composable(AppRoute.RegistrationNotice("").route) {
                        RegistrationNotice(navController) { isAccepted ->
                            navController.navigate("Registration_Step_1/$isAccepted") // send bundle
                        }
                    }

                    composable<AppRoute.RegistrationStep1>() {
                        val args = it.toRoute<AppRoute.RegistrationStep1>().isAccepted
                        StudentRegistrationStep1Screen(
                            navController,
                            args,
                            sharedPref = sharedPref,
                            viewModel
                        )
                    }

                    composable<AppRoute.RegistrationStep2>() {
                        val args = it.toRoute<AppRoute.RegistrationStep2>().userDetails
                        StudentRegistrationStep2Screen(navController, args, viewModel)
                    }

                    composable<AppRoute.EnterPin>() {
                        val args = it.toRoute<AppRoute.EnterPin>()
                        val isLoginWithOtp = args.isLoginWithPin
                        val phoneNo = args.phoneNo
                        val userId = args.userId
                        EnterPinScreen(navController, this@LoginMainActivity, sharedPref, phoneNo, isLoginWithOtp,userId)
                    }
                    composable<AppRoute.SelectSubject>() {
                        val args = it.toRoute<AppRoute.SelectSubject>().grade
                        SelectSubjectScreen(navController, args, viewModel, sharedPref)
                    }

                    //Parent
                    composable<AppRoute.ParentRegistrationStep1>() {
                        val args = it.toRoute<AppRoute.ParentRegistrationStep1>()
                        val userId = args.userId.toString()
                        ParentRegistrationStep1Screen(

                            navHostController = navController,
                            intent = userId,
                            sharedPref = sharedPref,
                            viewModel = viewModel
                        )
                    }

                    composable<AppRoute.ParentRegistrationStep2>() {
                        ParentRegistrationStep2Screen(
                            navHostController = navController,
                            sharedPref = sharedPref,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun LoginScreen() {
    ChooseLanguage(navController = rememberNavController())
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun MobileNoScreen() {
    ChooseLanguage(navController = rememberNavController())
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
fun ChooseRoles() {
    ChooseUserTypeUI(navController = rememberNavController())
}




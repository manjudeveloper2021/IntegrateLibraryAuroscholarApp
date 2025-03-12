package com.auro.application.ui.features.login

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.auro.application.core.ConstantVariables.addNewChild
import com.auro.application.core.ConstantVariables.createpin
import com.auro.application.core.ConstantVariables.onboarding2
import com.auro.application.data.api.Constants
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RegisterActivity : ComponentActivity() {
    @Inject
    lateinit var sharedPref: SharedPref

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var getIntent = intent.getStringExtra(addNewChild)

            if (getIntent != null && getIntent == addNewChild) {

            }
            val startDestination = if (viewModel.getScreenName() ==  onboarding2) {
                AppRoute.RegistrationStep2()
            }else if (viewModel.getScreenName() == createpin) {
                AppRoute.CreatePin()
            } else {
                AppRoute.RegistrationStep1()
            }
                val navController = rememberNavController()

            AuroscholarAppTheme {

               /* NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {

                    composable(AppRoute.RegistrationNotice("").route) {
                        RegistrationNotice(navController) { isAccepted ->
                            navController.navigate("Registration_Step_1/$isAccepted") // send bundle
                        }
                    }

                    composable<AppRoute.RegistrationStep1>() {
                        val args = it.toRoute<AppRoute.RegistrationStep1>()
                        OnboardingStep1(
                            navController,
                            getIntent,
                            sharedPref = sharedPref,
                            viewModel
                        )
                    }

                    composable<AppRoute.RegistrationStep2>() {
                        val args = it.toRoute<AppRoute.RegistrationStep2>().userDetails
                        OnboardingStep2(navController, args, viewModel)
                    }

                    composable<AppRoute.CreatePin>() {
                        val args = it.toRoute<AppRoute.CreatePin>().userId
                        CreatePinScreen(navController, sharedPref,viewModel,args)
                    }

                    composable(AppRoute.EnterPin.route) {
                        EnterPinScreen(navController, this@RegisterActivity)
                    }
                    composable<AppRoute.SelectSubject>() {
                        SelectSubjectScreen(navController, this@RegisterActivity)
                    }

                }*/
            }

            // Alternatively, you can use LaunchedEffect to navigate immediately after composition
            LaunchedEffect(viewModel.getScreenName()) {
                Log.e("TAG", "onCreate: launch effect --- > "+viewModel.getScreenName() )
                if (viewModel.getScreenName() == onboarding2) {
                    navController.navigate(AppRoute.RegistrationStep2()) {
                        popUpTo(AppRoute.RegistrationStep1()) { inclusive = true }
                    }
                }
            }
        }
    }

}
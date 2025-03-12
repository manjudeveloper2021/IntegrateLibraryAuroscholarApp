package com.auro.application.ui.features.student.passport

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.student.passport.screens.QuizAttemptsScreens
import com.auro.application.ui.features.student.passport.screens.QuizVerificationScreen
import com.auro.application.ui.features.student.passport.screens.StudentQuizScoreScreen
import com.auro.application.ui.features.student.passport.screens.StudentReportScreen
import com.auro.application.ui.features.student.passport.screens.WeakAndTopPerformingTopicsScreens
import com.auro.application.ui.theme.AuroscholarAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StudentPassportRouteActivity : ComponentActivity() {

    @Inject
    lateinit var sharedPref: SharedPref

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuroscholarAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = AppRoute.StudentQuizReport(null)
                ) {
                    composable<AppRoute.StudentQuizReport>() {
                        val args = it.toRoute<AppRoute.StudentQuizReport>()
                        StudentReportScreen(navController, sharedPref, "12345")
                    }
                    composable<AppRoute.StudentQuizAttempts>() {
                        val args = it.toRoute<AppRoute.StudentQuizAttempts>()
                        QuizAttemptsScreens(navController, sharedPref, "12345")
                    }
                    composable<AppRoute.StudentQuizScore>() {
                        val args = it.toRoute<AppRoute.StudentQuizScore>()
                        StudentQuizScoreScreen(navController, sharedPref, "12345")
                    }
                    composable<AppRoute.StudentQuizVerification>() {
                        val args = it.toRoute<AppRoute.StudentQuizVerification>()
                        QuizVerificationScreen(navController, sharedPref, "12345")
                    }
                    composable<AppRoute.StudentTopPerformingTopics>() {
                        val args = it.toRoute<AppRoute.StudentTopPerformingTopics>()
                        WeakAndTopPerformingTopicsScreens(navController, sharedPref, "12345", args.performingTopics.toString())
                    }
                }
            }
        }
    }
}
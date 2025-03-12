package com.auro.application.ui.features.student.practice

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
import com.auro.application.ui.features.student.practice.screens.PracticeConceptListScreen
import com.auro.application.ui.features.student.practice.screens.PracticeResultScreen
import com.auro.application.ui.theme.AuroscholarAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PracticeQuizActivity : ComponentActivity() {

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
                    startDestination = AppRoute.PracticeConceptList("")
                ) {
                    composable<AppRoute.PracticeConceptList>() {
                        val args = it.toRoute<AppRoute.PracticeConceptList>()
                        PracticeConceptListScreen(navController)
                    }

                   /* composable<AppRoute.PracticeQuestion>() {
                        val args = it.toRoute<AppRoute.PracticeQuestion>()
                        PracticeQuestionScreen(navController)
                    }*/

                    composable<AppRoute.PracticeResult>() {
                        val args = it.toRoute<AppRoute.PracticeResult>()
                        PracticeResultScreen(navController)
                    }
                }
            }
        }
    }
}
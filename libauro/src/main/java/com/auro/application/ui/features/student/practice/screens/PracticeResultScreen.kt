package com.auro.application.ui.features.student.practice.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.components.TextWithIconOnLeft
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.Orange
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White

@Composable
fun PracticeResultScreen(navHostController: NavHostController) {

    val context = LocalContext.current
    var isDialogVisible by remember { mutableStateOf(false) }

    val loginViewModel: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)
    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = msgLoader
    )

    BackHandler {
        context.startActivity(
            Intent(
                context, StudentDashboardActivity::class.java
            ).apply { (context as Activity).finish() })
    }

    AuroscholarAppTheme {
        PracticeResultData(context, navHostController, languageData)
    }
}

@Composable
fun PracticeResultData(
    context: Context,
    navHostController: NavHostController,
    languageData: HashMap<String, String>
) {
    val loginViewModel: LoginViewModel = hiltViewModel()

//    val languageListName = stringResource(id = R.string.key_lang_list)
//    var languageData :HashMap<String, String>
//    languageData = loginViewModel.getLanguageTranslationData(languageListName)
    val passStatus by remember { mutableStateOf(loginViewModel.getStudentQuizResultData().submitExamAPIResult.PassStatus) }

    val navController = rememberNavController()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .fillMaxWidth()
                .background(White),
            horizontalAlignment = Alignment.End
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier
                    .padding(20.dp)
                    .clickable {
                        context.startActivity(Intent(
                            context, StudentDashboardActivity::class.java
                        ).apply { (context as Activity).finish() })
                    },
            )
        }

        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                if (passStatus != "Failed") {
                    Icon(
                        ImageVector.vectorResource(id = R.drawable.icon_party_popper),
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(top = 10.dp),
                        contentDescription = "Pass/Fail"
                    )
                }
                Icon(
                    if (passStatus != "Failed") {
                        ImageVector.vectorResource(id = R.drawable.ic_win_quiz)
                    } else {
                        ImageVector.vectorResource(id = R.drawable.icon_failed_assessment)
                    },
                    tint = Color.Unspecified,
                    contentDescription = "Expand/Collapse Button",
                )
            }
            Text(
                languageData[LanguageTranslationsResponse.YOUR_SCORE].toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 40.dp),
                fontFamily = FontFamily(
                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                ),
                fontSize = 14.sp,
                color = Gray,
                textAlign = TextAlign.Center
            )

            Text(
                loginViewModel.getStudentQuizResultData().submitExamAPIResult.Score.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                fontFamily = FontFamily(
                    Font(R.font.inter_bold, FontWeight.Bold)
                ),
                fontSize = 28.sp,
                color = Orange,
                textAlign = TextAlign.Center
            )

            Text(
                if (passStatus != "Pass") {
                    languageData[LanguageTranslationsResponse.NICE_TRY_KEEP_PRACTICING].toString()
//                    "Congrats on finishing the quiz!"
                } else {
                    "Congrats on finishing the quiz!"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                fontFamily = FontFamily(
                    Font(R.font.inter_bold, FontWeight.Bold)
                ),
                fontSize = 16.sp,
                color = Black,
                textAlign = TextAlign.Center
            )

            TextWithIconOnLeft(languageData[LanguageTranslationsResponse.SHARE_WITH_FRIENDS].toString(),
                icon = ImageVector.vectorResource(id = R.drawable.ic_share),
                textColor = PrimaryBlue,
                iconColor = PrimaryBlue,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .background(color = White)
                    .padding(vertical = 25.dp),
                onClick = {
                    try {
                        val appPackageName = context.packageName
                        val appLink =
                            "https://play.google.com/store/apps/details?id=$appPackageName"
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT, "Hey, check out this awesome app:- $appLink"
                            )
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share via")
                        context.startActivity(shareIntent)
                    } catch (exp: Exception) {
                        exp.message
                        println("Share with friends find error is :- ${exp.message}")
                    }
                })
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
//                    navHostController.navigate(AppRoute.StudentPartner.route)
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                border = BorderStroke(1.dp, PrimaryBlue),
                shape = RoundedCornerShape(8.dp), // Adjust the corner radius as needed
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 15.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = languageData[LanguageTranslationsResponse.LEARN_WITH_PARTNER].toString(),
                    modifier = Modifier.padding(vertical = 5.dp),
                    color = PrimaryBlue,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                    ),
                )
            }
            if (loginViewModel.getStudentQuizResultData().conceptText.isNotEmpty()) {
                Button(
                    onClick = { /* Handle click */
//                        navHostController.navigate(AppRoute.StudentQuizList.route)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    border = BorderStroke(1.dp, PrimaryBlue),
                    shape = RoundedCornerShape(8.dp), // Adjust the corner radius as needed
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, bottom = 15.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = loginViewModel.getStudentQuizResultData().conceptText,
                        modifier = Modifier.padding(vertical = 5.dp),
                        color = PrimaryBlue,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                        ),
                    )
                }
            }

            Text(
                text = if (passStatus != "Pass") {
                    "You can re-attempt quiz twice to win micro scholarship."
                } else {
                    "Choose a new quiz to win micro-scholarship. Tap 'Practice' to practice this concept."
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                fontFamily = FontFamily(
                    Font(R.font.inter_medium, FontWeight.Medium)
                ),
                fontSize = 12.sp,
                color = Gray,
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            BtnUi(languageData[LanguageTranslationsResponse.TAKE_NEXT_QUIZ].toString(), onClick = {
                /* val intent =
                     Intent(context, StudentDashboardActivity::class.java)
                 intent.flags =
                     Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                 intent.also {
                     it.putExtra("", true)
                 }
                 context.startActivity(intent).apply { (context as Activity).finish() }*/
//                navController.navigate(AppRoute.StudentQuizzes.route)
//                navHostController.navigate(AppRoute.StudentAssessmentConcept.route)
                navController.popBackStack()
            }, true)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PracticeResultScreenPreview() {
    AuroscholarAppTheme {
        val navHostController = rememberNavController()
        val context = LocalContext.current
        val loginViewModel: LoginViewModel = hiltViewModel()
        val languageListName = stringResource(id = R.string.key_lang_list)
        var languageData: HashMap<String, String>
        languageData = loginViewModel.getLanguageTranslationData(languageListName)
        PracticeResultData(context, navHostController, languageData)
    }
}
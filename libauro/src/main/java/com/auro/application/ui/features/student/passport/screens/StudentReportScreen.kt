package com.auro.application.ui.features.student.passport.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.StatusReview
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.passport.StudentPassportRouteActivity
import com.auro.application.ui.features.student.passport.models.AwardData
import com.auro.application.ui.features.student.passport.models.ReportsPerformingResponse
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.White

@Composable
fun StudentReportScreen(
    navHostController: NavHostController,
    sharedPref: SharedPref? = null,
    intent: String? = remember { mutableStateOf("") }.toString()
) {
    val context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()

    val viewModels: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModels.getLanguageTranslationData(languageListName)

    val topPerformingText: String = stringResource(id = R.string.txt_top_performing_topics)
    val weakPerformingText: String = stringResource(id = R.string.txt_weak_performing_topics)
    var reportPerformingData =
        remember { mutableListOf<ReportsPerformingResponse.ReportsPerformingData>() }

    var quizAttempts by remember { mutableStateOf<String?>("") }
    var topPerformance by remember { mutableStateOf<String?>("") }
    var weakPerformance by remember { mutableStateOf<String?>("") }
    var quizScore by remember { mutableStateOf<String?>("") }
    var quizVerification by remember { mutableStateOf<String?>("") }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    BackHandler {
        context.startActivity(Intent(
            context, StudentDashboardActivity::class.java
        ).apply { (context as Activity).finish() })
    }

    LaunchedEffect(Unit) {
        viewModel.reportPerformingResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        reportPerformingData.addAll(it.data.data)
                        println("Performing reports data :- $reportPerformingData")
                        quizAttempts = reportPerformingData[0].quizAttemptCount.toString()
                        topPerformance = reportPerformingData[0].topPerformingTopicCount.toString()
                        weakPerformance =
                            reportPerformingData[0].weakPerformingTopicCount.toString()
                        if (reportPerformingData[0].avgQuizScore != null) {
                            quizScore = reportPerformingData[0].avgQuizScore.toString()
                        } else {
                            quizScore = "0"
                        }
                        quizVerification = reportPerformingData[0].verificationQuizCount.toString()
                    }
                }

                is NetworkStatus.Error -> {
                    context.toast(it.message)
                }
            }
        }

        viewModel.getReportsPerformingData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
        {
            Surface(
                tonalElevation = 10.dp, // Set the elevation here
                color = White,
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.back_icon),
                        contentDescription = "logo",
                        modifier = Modifier
                            .clickable {
                                val intent =
                                    Intent(context, StudentDashboardActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.also {
                                    it.putExtra("", true)
                                }
                                context.startActivity(intent)
//                                navHostController.popBackStack()
//                                navHostController.navigate(AppRoute.StudentPassport.route)
                            },

                        colorFilter = ColorFilter.tint(Black)
                    )
                    Text(
                        text = if (languageData[LanguageTranslationsResponse.REPORT].toString() == "") {
                            stringResource(id = R.string.txt_report)
                        } else {
                            languageData[LanguageTranslationsResponse.REPORT].toString()
                        },
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(start = 25.dp),
                        fontFamily = FontFamily(
                            Font(R.font.inter_bold, FontWeight.Bold)
                        ),
                        fontSize = 18.sp,
                        color = Black,
                        textAlign = TextAlign.Start
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(top = 20.dp, bottom = 20.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawLine(
                        color = GrayLight02,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 0.8.dp.toPx()
                    )
                }
            }
        }

        val quizStatusReviews = listOf(
            StatusReview(
                leftIcon = painterResource(id = R.drawable.icon_quiz_attempts),
                rightIcon = painterResource(id = R.drawable.ic_right_side),
                text1 = stringResource(id = R.string.txt_quiz_attempts),
                text2 = if (quizAttempts != null) {
                    "Total Attempts: $quizAttempts Quizes"
                } else {
                    "Total Attempts: 0 Quizes"
                }
            ),
            StatusReview(
                leftIcon = painterResource(id = R.drawable.icon_quiz_score),
                rightIcon = painterResource(id = R.drawable.ic_right_side),
                text1 = stringResource(id = R.string.txt_quiz_score),
                text2 = if (quizScore != null) {
                    "Average Score: $quizScore"
                } else {
                    "Average Score: 0"
                }/*"Average Score: 0"*/
            ),
            StatusReview(
                leftIcon = painterResource(id = R.drawable.icon_quiz_verification),
                rightIcon = painterResource(id = R.drawable.ic_right_side),
                text1 = stringResource(id = R.string.txt_quiz_verification),
                text2 = if (quizVerification != null) {
                    "Total Attempts: $quizVerification Quizes"
                } else {
                    "Total Attempts: 0 Quizes"
                }/*"Total Attempts: 0 Quizes"*/
            ),
            StatusReview(
                leftIcon = painterResource(id = R.drawable.icon_top_performing),
                rightIcon = painterResource(id = R.drawable.ic_right_side),
                text1 = if (languageData[LanguageTranslationsResponse.TOP_PERFORMING_TOPICS].toString() == "") {
                    stringResource(id = R.string.txt_top_performing_topics)
                } else {
                    languageData[LanguageTranslationsResponse.TOP_PERFORMING_TOPICS].toString()
                },
                text2 = if (topPerformance != null) {
                    "Total: $topPerformance Quizes"
                } else {
                    "Total: 0 Quizes"
                },
            ),
            StatusReview(
                leftIcon = painterResource(id = R.drawable.icon_weak_performing),
                rightIcon = painterResource(id = R.drawable.ic_right_side),
                text1 = if (languageData[LanguageTranslationsResponse.WEAK_PERFORMING_TOPICS].toString() == "") {
                    stringResource(id = R.string.txt_weak_performing_topics)
                } else {
                    languageData[LanguageTranslationsResponse.WEAK_PERFORMING_TOPICS].toString()
                },
                text2 = if (weakPerformance != null) {
                    "Total: $weakPerformance Topics"
                } else {
                    "Total: 0 Topics"
                },
            ),
            // Add more items as needed
        )

        LazyColumn(modifier = Modifier.padding(top = 30.dp)) {

            items(items = quizStatusReviews) { item ->
                QuizStatus(
                    leftIcon = item.leftIcon,
                    rightIcon = item.rightIcon,
                    text1 = item.text1,
                    text2 = item.text2,
                    onClick = {
                        val position = quizStatusReviews.indexOf(item)
                        when (position) {
                            0 -> {
                                navHostController.navigate(AppRoute.StudentQuizAttempts())
                            }

                            1 -> {
                                navHostController.navigate(AppRoute.StudentQuizScore())
                            }

                            2 -> {
                                navHostController.navigate(AppRoute.StudentQuizVerification(""))
                            }

                            3 -> {
                                navHostController.navigate(
                                    AppRoute.StudentTopPerformingTopics(
                                        "",
                                        topPerformingText
                                    )
                                )
                            }

                            4 -> {
                                navHostController.navigate(
                                    AppRoute.StudentTopPerformingTopics(
                                        "",
                                        weakPerformingText
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun QuizStatus(
    leftIcon: Painter, rightIcon: Painter,
    text1: String,
    text2: String,
    onClick: () -> Unit // Add a parameter for the click action
) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {

        Column(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = GrayLight02,
                    shape = RoundedCornerShape(15)
                )
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(bottom = 5.dp, start = 2.dp, end = 2.dp)
                    .clickable(onClick = onClick) // Make the entire row clickable
            ) {
                Image(
                    painter = leftIcon,
                    contentDescription = "leftIcon",
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Unspecified)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = text1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                        fontFamily = FontFamily(
                            Font(R.font.inter_medium, FontWeight.Medium)
                        ),
                        fontSize = 14.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Left
                    )

                    Text(
                        text = text2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        color = Color.Gray.copy(alpha = 0.6f), // Adjusting for GrayLight01
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
                        ),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                }

                Image(
                    painter = rightIcon,
                    contentDescription = "rightIcon",
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color.Unspecified)
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
    }
}

@Preview
@Composable
fun StudentReportScreenPreview() {
    StudentReportScreen(navHostController = rememberNavController())
}
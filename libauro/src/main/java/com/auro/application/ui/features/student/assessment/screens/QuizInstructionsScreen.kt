package com.auro.application.ui.features.student.assessment.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
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
import com.auro.application.data.api.NetworkStatus
import com.auro.application.ui.common_ui.RectangleBtnUi
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Bg_Gray
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.White
import java.util.HashMap

@Composable
fun QuizInstructionsScreen(
    navHostController: NavHostController,
//    sharedPref: SharedPref
) {
    val viewModel: StudentViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.conceptWiseQuizResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {

                    if (it.data?.isSuccess == true) {
                        /*for (i in it.data.data) {
//                            if (it.data.data[i].concept_id == ){
//
//                            }
                        }*/
                    }
                }

                is NetworkStatus.Error -> {

                }
            }
        }

        viewModel.getAssessmentWiseData(
            loginViewModel.getStudentSelectedConceptData().ID.toString()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
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
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.back_icon),
                        contentDescription = "logo",
                        modifier = Modifier
                            .clickable {
                                navHostController.popBackStack()
                                navHostController.navigate(AppRoute.StudentDashboard.route)
                            },

                        colorFilter = ColorFilter.tint(Black)
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp)
                            .weight(1f)
                            .wrapContentSize(),
                    ) {
                        Text(
                            text = "Quiz Instructions",
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontFamily = FontFamily(
                                Font(R.font.inter_bold, FontWeight.Bold)
                            ),
                            fontSize = 18.sp,
                            color = Black,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(bottom = 20.dp)
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

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(start = 18.dp, end = 18.dp, top = 20.dp)
                .fillMaxWidth(),
            colors = CardColors(
                containerColor = Bg_Gray,
                contentColor = Bg_Gray,
                disabledContentColor = Bg_Gray,
                disabledContainerColor = Bg_Gray
            ),
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    Text(
                        text = stringResource(id = R.string.txt_questions),
                        modifier = Modifier
                            .fillMaxWidth(),
                        fontFamily = FontFamily(
                            Font(R.font.inter_medium, FontWeight.Medium)
                        ),
                        fontSize = 12.sp,
                        color = GrayLight01,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "10",
                        modifier = Modifier
                            .fillMaxWidth(),
                        fontFamily = FontFamily(
                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                        ),
                        fontSize = 16.sp,
                        color = Black,
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.txt_marks),
                        modifier = Modifier
                            .wrapContentSize(),
                        fontFamily = FontFamily(
                            Font(R.font.inter_medium, FontWeight.Medium)
                        ),
                        fontSize = 12.sp,
                        color = GrayLight01,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "10",
                        modifier = Modifier
                            .wrapContentSize(),
                        fontFamily = FontFamily(
                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                        ),
                        fontSize = 16.sp,
                        color = Black,
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 4.dp)
                        .weight(1f),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = stringResource(id = R.string.txt_time),
                        modifier = Modifier
                            .fillMaxWidth(),
                        fontFamily = FontFamily(
                            Font(R.font.inter_medium, FontWeight.Medium)
                        ),
                        fontSize = 12.sp,
                        color = GrayLight01,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "10 min",
                        modifier = Modifier
                            .fillMaxWidth(),
                        fontFamily = FontFamily(
                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                        ),
                        fontSize = 16.sp,
                        color = Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Text(
            text = "I/We hereby acknowledge and consent to our child using the Auro Scholar application, allowing pictures to be taken during quizzes for proctoring. Auro Scholar upholds the confidentiality of provided information, capturing pictures to prevent fraud.",
            modifier = Modifier
                .wrapContentSize()
                .padding(18.dp),
            fontFamily = FontFamily(
                Font(R.font.inter_medium, FontWeight.Medium)
            ),
            fontSize = 14.sp,
            color = Gray,
            textAlign = TextAlign.Start
        )

        Text(
            text = "I will take care of the following scenarios that need to be forbidden during the quiz:",
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 18.dp),
            fontFamily = FontFamily(
                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
            ),
            fontSize = 14.sp,
            color = Black,
            textAlign = TextAlign.Start
        )

        Row(
            modifier = Modifier
                .padding(vertical = 18.dp, horizontal = 15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_mouse_inst),
                contentDescription = "",
                tint = Color.Unspecified,
                modifier = Modifier.size(36.dp)
            )

            Text(
                text = "Right-clicking (disable context menus)",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                fontFamily = FontFamily(
                    Font(R.font.inter_regular, FontWeight.Normal)
                ),
                fontSize = 14.sp,
                color = Black,
                textAlign = TextAlign.Start
            )
        }

        Row(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                modifier = Modifier.size(36.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.icon_screenshot),
                contentDescription = "",
                tint = Color.Unspecified,
            )

            Text(
                text = "Taking screenshots",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                fontFamily = FontFamily(
                    Font(R.font.inter_regular, FontWeight.Normal)
                ),
                fontSize = 14.sp,
                color = Black,
                textAlign = TextAlign.Start
            )
        }
        Row(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.icon_screensharing),
                contentDescription = "",
                tint = Color.Unspecified,
                modifier = Modifier.size(36.dp)
            )

            Text(
                text = "Broadcasting and screen sharing",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                fontFamily = FontFamily(
                    Font(R.font.inter_regular, FontWeight.Normal)
                ),
                fontSize = 14.sp,
                color = Black,
                textAlign = TextAlign.Start
            )
        }
        RectangleBtnUi(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            onClick = {
                navHostController.popBackStack()
                navHostController.navigate(AppRoute.QuizDisclaimerScreen.route)
            },
            title = stringResource(R.string.txt_accept_continue),
            enabled = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuizInstScreen() {
    AuroscholarAppTheme {
        QuizInstructionsScreen(navHostController = rememberNavController())
    }
}

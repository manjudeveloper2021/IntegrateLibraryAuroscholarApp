package com.auro.application.ui.features.student.assessment.screens

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.auro.application.data.utlis.CommonFunction.currentDateDisplay
import com.auro.application.ui.common_ui.RectangleAddBtnUi
import com.auro.application.ui.common_ui.RectangleBtnUi
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.assessment.model.AssessmentConceptsRequestModel
import com.auro.application.ui.features.student.assessment.model.AssessmentConceptsResponseModel
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Bg_Gray
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.Orange
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import kotlinx.coroutines.launch
import java.util.HashMap

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuizListScreen(
    navHostController: NavHostController
//                                viewModel: LoginViewModel,
) {

    val studentViewModel: StudentViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val context: Context = LocalContext.current
    val scrollState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    var currentMonth = currentDateDisplay(false)
    val scrollStates = rememberScrollState()

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)
    var sizeOfList by remember { mutableStateOf(0) }

    var quizList by remember { mutableStateOf(mutableListOf<AssessmentConceptsResponseModel.AssessmentConcept>()) }


    DisposableEffect(Unit) {
        onDispose {
            loginViewModel.clearResponse()
        }
    }
    BackHandler {
        navHostController.popBackStack()
        navHostController.navigate(AppRoute.StudentDashboard.route)
    }
    LaunchedEffect(Unit) {

        studentViewModel.getStudentConceptResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {

                    if (it.data?.isSuccess == true) {
//                        quizList.clear()
                        quizList = it.data.data.toMutableList()
                    }
                }

                is NetworkStatus.Error -> {
                    context.toast(it.message)
                }
            }
        }

        // Api for Concept wise Quiz
        studentViewModel.getAssessmentConceptData(
            AssessmentConceptsRequestModel(
                loginViewModel.getStudentSelectedSubjectData().subjectId,
                loginViewModel.getGrade()!!.toInt(),
                currentMonth,
                loginViewModel.getLanguageId().toInt()
            )
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
        ) {
            Surface(
                tonalElevation = 10.dp, // Set the elevation here
                color = White,
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 14.dp, end = 14.dp, top = 20.dp)
                        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.back_icon),
                        contentDescription = "logo",
                        modifier = Modifier.clickable {
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
                            text = loginViewModel.getStudentSelectedSubjectData().subjectName,
                            modifier = Modifier.fillMaxWidth(),
                            fontFamily = FontFamily(
                                Font(R.font.inter_bold, FontWeight.Bold)
                            ),
                            fontSize = 18.sp,
                            color = Black,
                            textAlign = TextAlign.Start
                        )
                    }
                    Text(
                        text = "Change Concept",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(start = 10.dp)
                            .clickable {
                                navHostController.popBackStack()
                                navHostController.navigate(AppRoute.StudentAssessmentConcept.route)
                            },
                        fontFamily = FontFamily(
                            Font(R.font.inter_bold, FontWeight.SemiBold)
                        ),
                        fontSize = 12.sp,
                        color = PrimaryBlue,
                        textAlign = TextAlign.Left
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 16.dp, start = 16.dp, end = 15.dp)
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                text = "Concept changes aren't allowed after the quiz starts",
                modifier = Modifier
                    .padding(start = 2.dp, end = 2.dp, top = 15.dp, bottom = 10.dp)
                    .wrapContentSize(),
                fontSize = 14.sp,
                color = Color.Gray,
                fontFamily = FontFamily(
                    Font(R.font.inter_regular, FontWeight.Normal)
                ),
                textAlign = TextAlign.Left
            )

            sizeOfList = loginViewModel.getSelectedConcept()!!.toInt()
//            println("Select size :- $sizeOfList")
            if (sizeOfList < 4) {
                Column(
                    /* modifier = Modifier
                         .padding(start = 10.dp, end = 10.dp)*/
                ) {
                    Column(
                        modifier = Modifier
                            .border(
                                width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12)
                            )
                            .padding(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.more_quiz_icon),
                            contentDescription = "leftIcon",
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(10.dp)
                        )

                        Text(
                            text = "You can select a maximum of four concepts for subjects in a month.",
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(start = 10.dp, end = 10.dp),
                            fontFamily = FontFamily(
                                Font(R.font.inter_regular, FontWeight.Medium)
                            ),
                            fontSize = 12.sp,
                            color = Gray,
                            textAlign = TextAlign.Center
                        )

                        RectangleAddBtnUi(
                            modifier = Modifier.padding(bottom = 5.dp, start = 10.dp, end = 10.dp),
                            onClick = {
                                navHostController.popBackStack()
                                navHostController.navigate(AppRoute.StudentAssessmentConcept.route)

                            },
                            title = stringResource(R.string.txt_add_more),
                            enabled = true
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    )
                }
            }

            if (quizList.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    state = scrollState,
                    contentPadding = PaddingValues(bottom = 15.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .draggable(orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                coroutineScope.launch {
                                    scrollState.scrollBy(-delta)
                                }
                            })
                ) {
                    itemsIndexed(quizList) { index, item ->
                        if (item.isSelected == "1" || item.isSelected == "2") {
                            // selected or quiz started
                            QuizItem(index,
                                loginViewModel,
                                languageData,
                                navHostController,
                                item,
                                text2 = item.Name.toString(),
                                onClick = {
                                    val position = quizList.indexOf(item)
                                })
                        }
                    }
                }
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
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    navHostController.popBackStack()
                    navHostController.navigate(AppRoute.StudentDashboard.route)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                border = BorderStroke(1.dp, PrimaryBlue),
                shape = RoundedCornerShape(8.dp), // Adjust the corner radius as needed
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.txt_select_concept_for_other),
                    modifier = Modifier.padding(vertical = 5.dp),
                    color = PrimaryBlue,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                    )
                )
            }

            /*RectangleBtnUi(
                onClick = {
                    navHostController.popBackStack()
                    navHostController.navigate(AppRoute.StudentDashboard.route)
//                    navHostController.navigate(AppRoute.StudentAssessmentConcept.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                title = stringResource(R.string.txt_select_concept_for_other),
                enabled = true
            )*/
        }
    }
}

@Composable
fun QuizItem(
    index: Int, // this will be exam name
    loginViewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    navHostController: NavHostController,
    item: AssessmentConceptsResponseModel.AssessmentConcept,
    text2: String,
    onClick: () -> Unit // Add a parameter for the click action){}){}){}){}
) {

    Column(
        /*  modifier = Modifier
              .padding(start = 10.dp, end = 10.dp)*/
    ) {

        Column(
            modifier = Modifier
                .border(
                    width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12)
                )
                .background(
                    if (item.nextAttempt!!.toInt() < 7) {
                        White
                    } else {
                        Bg_Gray
                    }
                )
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(5.dp)
                    .clickable(onClick = onClick) // Make the entire row clickable
            ) {
                Text(
                    text = if (languageData[LanguageTranslationsResponse.KEY_QUIZ].toString() == "") {
                        "Quiz " + (index + 1)
                    } else {
                        languageData[LanguageTranslationsResponse.KEY_QUIZ].toString() + " " + (index + 1)
                    },
                    modifier = Modifier
                        .wrapContentSize()
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(),
                    fontFamily = FontFamily(
                        Font(R.font.inter_regular, FontWeight.Normal)
                    ),
                    fontSize = 12.sp,
                    color = Gray,
                    textAlign = TextAlign.Left
                )
                Text(
                    text = "10 Questions",
                    modifier = Modifier
                        .wrapContentSize()
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 10.dp),
                    fontFamily = FontFamily(
                        Font(R.font.inter_regular, FontWeight.Normal)
                    ),
                    fontSize = 12.sp,
                    color = Gray,
                    textAlign = TextAlign.End
                )
            }
            Text(
                text = text2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                color = Black,
                fontFamily = FontFamily(
                    Font(R.font.inter_medium, FontWeight.SemiBold)
                ),
                fontSize = 14.sp,
                textAlign = TextAlign.Start
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(5.dp)
                    .clickable(onClick = onClick) // Make the entire row clickable
            ) {
                Text(
                    text = item.conceptWinningText.toString(),
                    modifier = Modifier
                        .wrapContentSize()
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(),
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    ),
                    fontSize = 12.sp,
                    color = Orange,
                    textAlign = TextAlign.Left
                )
                if (item.nextAttempt.toInt() < 7) {
                    RectangleBtnUi(
                        onClick = {
                            loginViewModel.saveStudentSelectedConceptData(item) // saving selected concept data to local
                            loginViewModel.saveExamName(index.toString())
                            loginViewModel.setSelectedConceptName(text2)
                            navHostController.popBackStack()
                            navHostController.navigate(AppRoute.StudentQuizInstructions.route)
                        }, title = if (item.nextAttempt == "1") {
                            stringResource(R.string.txt_start)
                        } else if (item.nextAttempt == "2") {
                            stringResource(R.string.txt_retake1)
                        } else if (item.nextAttempt == "3") {
                            stringResource(R.string.txt_retake2)
                        } else if (item.nextAttempt == "4") {
                            stringResource(R.string.txt_practice1)
                        } else if (item.nextAttempt == "5") {
                            stringResource(R.string.txt_practice2)
                        } else {
                            stringResource(R.string.txt_practice3)
                        }, enabled = true
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun QuizListPreview() {
    AuroscholarAppTheme {
        QuizListScreen(navHostController = rememberNavController())
    }
}
package com.auro.application.ui.features.student.passport.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.GetSubjectListResponseModel
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.passport.models.TopWeakPerformingResponse
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Bg_Gray
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.DarkRed2
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeakAndTopPerformingTopicsScreens(
    navHostController: NavHostController,
    sharedPref: SharedPref? = null,
    intent: String? = remember { mutableStateOf("") }.toString(),
    performingString: String = remember { mutableStateOf("") }.toString()
) {

    val context: Context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
    var topWeakPerformanceData by remember { mutableStateOf(mutableListOf<TopWeakPerformingResponse.TopWeakPerformingData>()) }
    var subjectData by remember { mutableStateOf(mutableListOf<GetSubjectListResponseModel.Data>()) }
    var isDialogVisible by remember { mutableStateOf(false) }
    val strUserId = loginViewModel.getUserId().toString()
    var subjectList = remember { mutableStateListOf(TabItems("", 0)) }
    var selectedSubjectName by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableStateOf(0) }
    var selectedConcept by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = "Loading your data..."
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getSubjectListResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        subjectList.clear()
                        subjectData = it.data.data.toMutableList()
                        println("All subjects data :- $subjectData")
                        selectedSubjectName = subjectData[0].subjectName
                        selectedSubjectId = subjectData[0].subjectId

                        subjectData.forEach { subjectData ->
                            if (subjectData.isSelected) {
                                if (subjectData.subjectName.isNotEmpty()) {
                                    subjectList.add(
                                        TabItems(
                                            subjectData.subjectName,
                                            subjectData.subjectId
                                        )
                                    )
                                }
                            } else {
                                println("Subject not selected by student...")
                            }

                            subjectList.map {
                                TabItems(
                                    subjectData.subjectName,
                                    subjectData.subjectId
                                )
                            }
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                }
            }
        }

        viewModel.getTopWeakPerformingResponseData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                    topWeakPerformanceData.clear()
                }

                is NetworkStatus.Success -> {
                    topWeakPerformanceData.clear()
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        topWeakPerformanceData = it.data.data.toMutableList()
                        println("All weak performance score data :- $topWeakPerformanceData")
                    } else {
                        isDialogVisible = false
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    topWeakPerformanceData.clear()
                }
            }
        }

        viewModel.getTopWeakPerformingResponseData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                    topWeakPerformanceData.clear()
                }

                is NetworkStatus.Success -> {
                    topWeakPerformanceData.clear()
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        topWeakPerformanceData = it.data.data.toMutableList()
                        println("All top performance score data :- $topWeakPerformanceData")
                    } else {
                        isDialogVisible = false
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    topWeakPerformanceData.clear()
                }
            }
        }

        viewModel.getSubjectList()
    }

    LaunchedEffect(key1 = subjectData.firstOrNull()) {
        coroutineScope.launch {
            subjectData.firstOrNull()?.let { firstItem ->
                if (performingString == "Top Performing Topics") {
                    isDialogVisible = true
                    viewModel.getTopPerformingReports(
                        strUserId, selectedSubjectName, selectedConcept
                    )
                } else {
                    isDialogVisible = true
                    viewModel.getWeakPerformingReports(
                        strUserId, selectedSubjectName, selectedConcept
                    )
                }
            }
        }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { subjectList.size }

    LaunchedEffect(selectedTabIndex) {
        // if tab selected then scroll the content
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        // if scrolling the content change the tab as well
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
        ) {
            Surface(
                tonalElevation = 10.dp, // Set the elevation here
                color = White,
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.back_icon),
                        contentDescription = "logo",
                        modifier = Modifier.clickable {
                            navHostController.popBackStack()
                            navHostController.navigate(AppRoute.StudentQuizReport(""))
                        },

                        colorFilter = ColorFilter.tint(Black)
                    )
                    Text(
                        text = performingString,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp)
                            .weight(1f),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Black,
                        textAlign = TextAlign.Start
                    )
                }
            }

            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex, edgePadding = 16.dp
            ) {
                subjectList.forEachIndexed { index, tabItem ->
                    Tab(selected = index == selectedTabIndex, onClick = {
                        selectedTabIndex = index
                        coroutineScope.launch {
                            selectedSubjectName = tabItem.title
                            selectedSubjectId = tabItem.subjectId
                            isDialogVisible = true
                            if (performingString == "Top Performing Topics") {
                                viewModel.getTopPerformingReports(
                                    strUserId, selectedSubjectName, selectedConcept
                                )
                            } else {
                                viewModel.getWeakPerformingReports(
                                    strUserId, selectedSubjectName, selectedConcept
                                )
                            }
                        }
                    }, modifier = Modifier.background(White), text = {
                        Text(
                            text = tabItem.title,
                            modifier = Modifier.padding(vertical = 5.dp),
                            color = if (index == selectedTabIndex) PrimaryBlue else GrayLight01,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    })
                }
            }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(White),
                    userScrollEnabled = false, // Disable page scrolling
                ) { index ->
                    when (index) {
                        0 -> {
                            QuizTopPerformingList(
                                topWeakPerformanceData,
                                performingString,
                                selectedSubjectName,
                                selectedConcept,
                                viewModel
                            )
                        }

                        1 -> {
                            QuizTopPerformingList(
                                topWeakPerformanceData,
                                performingString,
                                selectedSubjectName,
                                selectedConcept,
                                viewModel
                            )
                        }

                        2 -> {
                            QuizTopPerformingList(
                                topWeakPerformanceData,
                                performingString,
                                selectedSubjectName,
                                selectedConcept,
                                viewModel
                            )
                        }

                        3 -> {
                            QuizTopPerformingList(
                                topWeakPerformanceData,
                                performingString,
                                selectedSubjectName,
                                selectedConcept,
                                viewModel
                            )
                        }

                        4 -> {
                            QuizTopPerformingList(
                                topWeakPerformanceData,
                                performingString,
                                selectedSubjectName,
                                selectedConcept,
                                viewModel
                            )
                        }

                        else -> {
                            QuizTopPerformingList(
                                topWeakPerformanceData,
                                performingString,
                                selectedSubjectName,
                                selectedConcept,
                                viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTopPerformingList(
    topWeakPerformanceData: MutableList<TopWeakPerformingResponse.TopWeakPerformingData>,
    performance: String,
    subject: String,
    selectedConcept: String,
    viewModel: StudentViewModel
) {

    val loginViewModel: LoginViewModel = hiltViewModel()
    var searchText by remember { mutableStateOf("") }
    val conceptKeyText = rememberDebounce(searchText, 4000L)
    var isDialogVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val strUserId = loginViewModel.getUserId().toString()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = "Loading your data..."
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(conceptKeyText) {
        if (conceptKeyText.isNotBlank()) {
            coroutineScope.launch {
                topWeakPerformanceData.clear()
                println("Performance data :- $performance")
                if (performance == "Top Performing Topics") {
                    viewModel.getTopPerformingReports(strUserId, subject, conceptKeyText)
                } else {
                    viewModel.getWeakPerformingReports(strUserId, subject, conceptKeyText)
                }
            }
        } else {
            coroutineScope.launch {
                topWeakPerformanceData.clear()
                println("Performance data :- $performance")
                if (performance == "Top Performing Topics") {
                    viewModel.getTopPerformingReports(strUserId, subject, conceptKeyText)
                } else {
                    viewModel.getWeakPerformingReports(strUserId, subject, conceptKeyText)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(5.dp)
                .border(
                    width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .background(color = Color.White)
                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "Search Icon",
                    modifier = Modifier
                        .background(color = Color.White)
                        .padding()
                        .padding(10.dp)
                )

                TextField(
                    value = searchText,
                    onValueChange = { newText ->
                        searchText = newText
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White), // Set background color to white
                    placeholder = {
                        Text(
                            "Search by concept name ", color = Color.Gray
                        )
                    }, // Placeholder text color
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        cursorColor = Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

            }
        }

        var scoreData = 0
        if (topWeakPerformanceData.size != 0) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                topWeakPerformanceData.forEach { performanceScore ->
                    performanceScore.quizzes.forEach { quizzData ->
                        quizzData.quiz!!.quizs.forEach { quizz ->
                            scoreData = quizz.score!!.toInt()
                        }
                    }
                }

                topWeakPerformanceData.forEach { performanceData ->
                    item {
                        if (performance == "Top Performing Topics") {
                            println("Top Performing Topics")
                            WeakPerformanceDataItem(performanceData, performance)

                            /* if (scoreData >= 8) {
                                 WeakPerformanceDataItem(performanceData)
                             } else {
                                 Column(
                                     modifier = Modifier
                                         .fillMaxSize()
                                         .fillMaxWidth(),
                                     horizontalAlignment = Alignment.CenterHorizontally,
                                     verticalArrangement = Arrangement.Center
                                 ) {
                                     Image(
                                         painter = painterResource(R.drawable.no_data_found),
                                         contentDescription = "logo",
                                         modifier = Modifier
                                             .padding(vertical = 20.dp)
                                             .background(Color.Unspecified),
                                     )
                                     Text(
                                         text = "No Quiz Found",
                                         modifier = Modifier.wrapContentSize(),
                                         fontStyle = FontStyle.Normal,
                                         fontWeight = FontWeight.Bold,
                                         fontSize = 18.sp,
                                         color = Black,
                                         textAlign = TextAlign.Start
                                     )
                                 }
                             }*/
                        } else {
                            println("Weak Performing Topics")
                            WeakPerformanceDataItem(performanceData, performance)
                            /* if (scoreData < 8) {
                                 WeakPerformanceDataItem(performanceData)
                             } else {
                                 Column(
                                     modifier = Modifier
                                         .fillMaxSize()
                                         .fillMaxWidth(),
                                     horizontalAlignment = Alignment.CenterHorizontally,
                                     verticalArrangement = Arrangement.Center
                                 ) {
                                     Image(
                                         painter = painterResource(R.drawable.no_data_found),
                                         contentDescription = "logo",
                                         modifier = Modifier
                                             .padding(vertical = 20.dp)
                                             .background(Color.Unspecified),
                                     )
                                     Text(
                                         text = "No Quiz Found",
                                         modifier = Modifier.wrapContentSize(),
                                         fontStyle = FontStyle.Normal,
                                         fontWeight = FontWeight.Bold,
                                         fontSize = 18.sp,
                                         color = Black,
                                         textAlign = TextAlign.Start
                                     )
                                 }
                             }*/
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.no_data_found),
                    contentDescription = "logo",
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .background(Color.Unspecified),
                )
                Text(
                    text = "No Quiz Found",
                    modifier = Modifier.wrapContentSize(),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun WeakPerformanceDataItem(
    weakPerformanceData: TopWeakPerformingResponse.TopWeakPerformingData,
    performance: String
) {

    Text(text = if (weakPerformanceData.examMonth != null) {
        val formattedDate = remember(weakPerformanceData.examMonth.toString()) {
            try {
                val inputFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                val date = inputFormat.parse(weakPerformanceData.examMonth.toString())
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                "Invalid date"
            }
        }

        formattedDate.toString()
    } else {
        "N/A"
    },
        modifier = Modifier
            .wrapContentSize()
            .padding(start = 20.dp, top = 20.dp),
        fontStyle = FontStyle.Normal,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = Gray,
        textAlign = TextAlign.Start)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .border(
                width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
            )
    ) {

        weakPerformanceData.quizzes.forEach { quizzesData ->
            QuizzesDataItem(quizzesData, performance)
        }
    }
}

@Composable
fun QuizzesDataItem(
    quizzesData: TopWeakPerformingResponse.TopWeakPerformingData.TopWeakQuizzes,
    performance: String
) {

    Text(
        text = if (quizzesData.quiz?.concept != null) {
            quizzesData.quiz?.concept.toString()
        } else {
            "N/A"
        },
        modifier = Modifier
            .wrapContentSize()
            .padding(start = 15.dp, end = 15.dp, top = 15.dp),
        fontStyle = FontStyle.Normal,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = Black,
        textAlign = TextAlign.Start
    )

    quizzesData.quiz?.quizs!!.forEach { quizsData ->
        if (performance == "Top Performing Topics") {
            if (quizsData.score!!.toInt() >= 8) {
                QuizDataItem(quizsData)
            }
        } else {
            if (quizsData.score!!.toInt() <= 7) {
                QuizDataItem(quizsData)
            }
        }
    }
}

@Composable
fun QuizDataItem(
    quizData: TopWeakPerformingResponse.TopWeakPerformingData.TopWeakQuizzes.TopWeakQuiz.TopWeakQuizData
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(top = 10.dp, bottom = 20.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth()) {
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
            .wrapContentHeight()
            .background(color = Color.White)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (quizData.quizAttempt != null) {
                "Quiz Attempt : ${quizData.quizAttempt}"
            } else {
                "Quiz Attempt : 0"
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 15.dp, top = 10.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = Gray,
            textAlign = TextAlign.Start
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(end = 15.dp, top = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stringResource(id = R.string.txt_micro_scholarship),
                modifier = Modifier.wrapContentSize(),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = GrayLight01,
                textAlign = TextAlign.End
            )
            Text(
                text = if (quizData.microScholarshipAmount != null) {
                    quizData.microScholarshipAmount.toString()
                } else {
                    "N/A"
                },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 5.dp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = DarkRed2,
                textAlign = TextAlign.End
            )
        }
    }

    Row(
        modifier = Modifier
            .wrapContentHeight()
            .padding(vertical = 10.dp, horizontal = 15.dp)
            .border(
                width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
            )
            .background(color = Bg_Gray)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (quizData.score != null) {
                "Score : ${quizData.score}"
            } else {
                "Score : 0"
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 10.dp, end = 5.dp, top = 10.dp, bottom = 10.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = Black,
            textAlign = TextAlign.Start
        )
        Text(
            text = if (quizData.panScore != null) {
                val num = quizData.panScore
                val df = DecimalFormat("#.##")
                df.roundingMode = RoundingMode.CEILING
                val floatValue = df.format(num)
                println("Number format data :- $floatValue")
                "Pan India Score: $floatValue"
            } else {
                "Pan India Score: 0"
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(end = 10.dp, top = 10.dp, bottom = 10.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = Black,
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuizTopPerformingPreview() {
    AuroscholarAppTheme {
//        QuizAttemptsScreens()
//        QuizTopPerformingList("English", viewModel)
    }
}
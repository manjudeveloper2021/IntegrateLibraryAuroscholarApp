package com.auro.application.ui.features.student.passport.screens

import android.content.Context
import android.os.Build
import android.util.Log
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.components.YearMonthGridPicker
import com.auro.application.ui.common_ui.components.monthYearFilterBottomSheet
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.GetSubjectListResponseModel
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.passport.models.QuizScoreResponse
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Bg_Gray
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.Orange
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Year
import java.time.YearMonth
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudentQuizScoreScreen(
    navHostController: NavHostController,
    sharedPref: SharedPref? = null,
    intent: String? = remember { mutableStateOf("") }.toString()
) {

    val context: Context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()

    var subjectData by remember { mutableStateOf(mutableListOf<GetSubjectListResponseModel.Data>()) }
    var quizScoreData by remember { mutableStateOf(mutableListOf<QuizScoreResponse.QuizScoreData>()) }
    val filterTextHint = stringResource(id = R.string.txt_practice)
    var isDialogVisible by remember { mutableStateOf(false) }
    var monthYearFilterShow by remember { mutableStateOf(false) }
    var dateValueQuizAttempt by remember { mutableStateOf(filterTextHint) }

    var subjectList = remember { mutableStateListOf(TabItems("", 0)) }
    var selectedSubjectName by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableStateOf(0) }
    var selectedConcept by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var selectedDate by remember { mutableStateOf("") }
    selectedDate = viewModel.getSelectedDate().toString()

    if (dateValueQuizAttempt.equals("Practice")) {
        val sdf = SimpleDateFormat("yyyyMM")
        val currentDateAndTime = sdf.format(Date())
        val dateMonth = currentDateAndTime.toString()
//        println("Date and year :- $dateMonth")
        dateValueQuizAttempt = dateMonth
//        println("Selected date with year :- $dateValueQuizAttempt")
    } else {
        if (monthYearFilterShow) {
            dateValueQuizAttempt = monthYearFilterBottomSheet(dateValueQuizAttempt) {
                monthYearFilterShow = false
            }
        }
//        println("Selected date with year :- $dateValueQuizAttempt")
    }

    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })

    FilterScoreDialogScreen(quizScoreData = quizScoreData,
        isBottomSheetVisible = isBottomSheetVisible,
        sheetState = sheetState,
        selectedSubjectId = selectedSubjectName,
        onDismiss = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                isBottomSheetVisible = false
            }
        })

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

//                        viewModel.getQuizScoreReports(
//                            selectedSubjectName, dateValueQuizAttempt, ""
//                        )

                        subjectData.forEach { subjectData ->
                            if (subjectData.isSelected) {
                                if (subjectData.subjectName.isNotEmpty()) {
                                    subjectList.add(
                                        TabItems(
                                            subjectData.subjectName, subjectData.subjectId
                                        )
                                    )
                                }
                            } else {
                                println("Subject not selected by student...")
                            }

                            subjectList.map {
                                TabItems(
                                    subjectData.subjectName, subjectData.subjectId
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

        viewModel.getQuizScoreResponseData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                    quizScoreData.clear()
                }

                is NetworkStatus.Success -> {
                    quizScoreData.clear()
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        quizScoreData = it.data.data.toMutableList()
                        println("All quiz score data :- $quizScoreData")
                    } else {
                        isDialogVisible = false
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    quizScoreData.clear()
                }
            }
        }

        viewModel.getSubjectList()
    }

    LaunchedEffect(key1 = subjectData.firstOrNull()) {
        coroutineScope.launch {
            subjectData.firstOrNull()?.let { firstItem ->
                isDialogVisible = true
                viewModel.setSelectedDate("")
                viewModel.getQuizScoreReports(
                    selectedSubjectName, "", ""
                )
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
                        text = stringResource(id = R.string.txt_quiz_score),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 25.dp)
                            .weight(1f),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Black,
                        textAlign = TextAlign.Start
                    )

                    Image(
                        painter = painterResource(R.drawable.calender_icon),
                        contentDescription = "logo",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                scope.launch {
                                    isBottomSheetVisible = true
                                    sheetState.expand()
                                }
                            },
                        colorFilter = ColorFilter.tint(Black)
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
                            if (selectedDate == "") {
                                viewModel.getQuizScoreReports(
                                    selectedSubjectName, "", ""
                                )
                            } else {
                                viewModel.getQuizScoreReports(
                                    selectedSubjectName, selectedDate, ""
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
                            QuizScoreSubjectList(
                                quizScoreData,
                                selectedSubjectName,
                                selectedDate,
                                selectedConcept,
                                viewModel
                            )
                        }

                        1 -> {
                            QuizScoreSubjectList(
                                quizScoreData,
                                selectedSubjectName,
                                selectedDate,
                                selectedConcept,
                                viewModel
                            )
                        }

                        2 -> {
                            QuizScoreSubjectList(
                                quizScoreData,
                                selectedSubjectName,
                                selectedDate,
                                selectedConcept,
                                viewModel
                            )
                        }

                        3 -> {
                            QuizScoreSubjectList(
                                quizScoreData,
                                selectedSubjectName,
                                selectedDate,
                                selectedConcept,
                                viewModel
                            )
                        }

                        4 -> {
                            QuizScoreSubjectList(
                                quizScoreData,
                                selectedSubjectName,
                                selectedDate,
                                selectedConcept,
                                viewModel
                            )
                        }

                        else -> {
                            QuizScoreSubjectList(
                                quizScoreData,
                                selectedSubjectName,
                                selectedDate,
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
fun QuizScoreSubjectList(
    quizScoreData: MutableList<QuizScoreResponse.QuizScoreData>,
    subject: String,
    dateMonth: String,
    selectedConcept: String,
    viewModel: StudentViewModel
) {

    var searchText by remember { mutableStateOf("") }
    val conceptKeyText = rememberDebounce(searchText, 4000L)
    var isDialogVisible by remember { mutableStateOf(false) }
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

    LaunchedEffect(conceptKeyText) {
        if (conceptKeyText.isNotBlank()) {
            coroutineScope.launch {
                quizScoreData.clear()
                viewModel.getQuizScoreReports(subject, dateMonth, conceptKeyText)
            }
        } else {
            coroutineScope.launch {
                quizScoreData.clear()
                viewModel.getQuizScoreReports(subject, dateMonth, conceptKeyText)
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

        if (quizScoreData.size != 0) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {

                // New Pattern for practice and attempted data
                val updatedConcepts = processQuizAttempts(quizScoreData)
                updatedConcepts.forEach { scoreData ->
                    item {
                        QuizScoreDataItem(scoreData)
                    }
                }

                // // Old Pattern for practice and attempted data
//                quizScoreData.forEach { scoreData ->
//                    item {
//                        QuizScoreDataItem(scoreData)
//                    }
//                }
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

fun processQuizAttempts(quizScoreResponse: List<QuizScoreResponse.QuizScoreData>): List<QuizScoreResponse.QuizScoreData> {
    return quizScoreResponse.map { concept ->
        var practiceCounter = 1 // Initialize counter for practice == 1

        val practiceQuizzes = mutableListOf<QuizScoreResponse.QuizScoreData.QuizAttempt>()
        val coreQuizzes = mutableListOf<QuizScoreResponse.QuizScoreData.QuizAttempt>()

        concept.practiceQuizzes.forEach { quizAttempt ->
            if (quizAttempt.practice == 1) {
                practiceQuizzes.add(
                    quizAttempt.copy(new_key = practiceCounter++) // Increment newKey for practice
                )
            } else {
                coreQuizzes.add(
                    quizAttempt.copy(new_key = quizAttempt.quizAttempt) // Use quiz_attempt for core
                )
            }
        }

        QuizScoreResponse.QuizScoreData(
            concept = concept.concept,
            subject = concept.subject,
            conceptId = concept.conceptId,
            practiceQuizzes = practiceQuizzes,
            coreQuizzes = coreQuizzes,
            improvementPercentage = concept.improvementPercentage
        )
    } ?: emptyList()
}

@Composable
fun QuizScoreDataItem(quizScoreData: QuizScoreResponse.QuizScoreData) {

    /*Text(
        text = "July 2024",
        modifier = Modifier
            .wrapContentSize()
            .padding(start = 20.dp, top = 20.dp),
        fontStyle = FontStyle.Normal,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = Gray,
        textAlign = TextAlign.Start
    )*/

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .border(
                width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            text = if (quizScoreData.concept != null) {
                quizScoreData.concept.toString()
            } else {
                "N/A"
            },
            modifier = Modifier
                .wrapContentSize()
                .padding(start = 12.dp, end = 12.dp, top = 12.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Black,
            textAlign = TextAlign.Start
        )

        // New pattern for practice and attempt
//        var practiceQuiz = "0"
//        var practiceScore = "0"
//        quizScoreData.practiceQuizzes.forEach { attemptData ->
//            practiceQuiz = attemptData.practice.toString()
//            practiceScore = attemptData.score.toString()
////            QuizAttemptDataItem(attemptData)
//        }

       /* quizScoreData.coreQuizzes.forEach { attemptData ->
            QuizAttemptDataItem(attemptData*//*, practiceQuiz, practiceScore*//*)
        }*/

//        val allQuizzes = quizScoreData.coreQuizzes + quizScoreData.practiceQuizzes
//        allQuizzes.forEach{ attemptData ->
//            QuizAttemptDataItem(attemptData) //*, practiceQuiz, practiceScore*//*)
//        }
        QuizAttemptRow(quizScoreData)

        // Old pattern for practice and attempt
//        quizScoreData.quizAttempts.forEach { attemptData ->
//            QuizAttemptDataItem(attemptData)
//        }

        Text(
            text = if (quizScoreData.improvementPercentage != null) {
                "Overall Improvement : ${quizScoreData.improvementPercentage} %"
            } else {
                "Overall Improvement : 0 %"
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, bottom = 15.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = Orange,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun QuizAttemptRow(quizScoreData: QuizScoreResponse.QuizScoreData) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween // Ensures spacing between left and right
    ) {
        // Left Column: Core Quizzes
        Column(
            modifier = Modifier.weight(1f) // Allows proper spacing
        ) {
            quizScoreData.coreQuizzes.forEach { attemptData ->
                QuizAttemptDataItem(attemptData)
            }
        }

//        Spacer(modifier = Modifier.width(16.dp)) // Optional space between the columns

        // Right Column: Practice Quizzes
        Column(
            modifier = Modifier.weight(1f) // Balances the layout
        ) {
            quizScoreData.practiceQuizzes.forEach { attemptData ->
                QuizAttemptDataItem(attemptData)
            }
        }
    }
}

@Composable
fun QuizAttemptDataItem(
    quizScoreData: QuizScoreResponse.QuizScoreData.QuizAttempt/*,
    practiceQuiz: String,
    practiceScore: String*/
) {
    println("Quiz score data :- $quizScoreData")

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
            .padding(vertical = 10.dp, horizontal = 5.dp)
            .background(color = Color.White)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

    }

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(vertical = 10.dp, horizontal = 10.dp)
            .border(
                width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
            )
            .background(color = Bg_Gray)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (quizScoreData.practice == 0)
                Text(
                    text = if (quizScoreData.quizAttempt != null && quizScoreData.score != null) {
                        "Attempt ${quizScoreData.quizAttempt}: ${quizScoreData.score} Score"
                    } else {
                        "Attempt 0: 0 Score"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(5.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Black,
                    textAlign = TextAlign.Center
                )

            if (quizScoreData.practice != null && quizScoreData.practice!! > 0)
                Text(
                    text = if (quizScoreData.score != null) {
                        "Practice ${quizScoreData.new_key}: ${quizScoreData.score} Score"
                    } else {
                        "Practice 0: 0 Score"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(5.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Black,
                    textAlign = TextAlign.Center
                )

        }
        }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScoreDialogScreen(
    quizScoreData: MutableList<QuizScoreResponse.QuizScoreData>,
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    selectedSubjectId: String,
    onDismiss: () -> Unit
) {
    val context: Context = LocalContext.current
    val viewModel: LoginViewModel = hiltViewModel()
    val viewModelStudent: StudentViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)
    val strUserId = viewModel.getUserId().toString()

    var selectedDateTab by remember { mutableStateOf(true) }
    var selectedYear by remember { mutableStateOf(Year.now().value) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now().monthValue) }
    var selectedValue by remember { mutableStateOf("") }

    if (isBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = null,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            windowInsets = WindowInsets.ime
        ) {
            Column(
                modifier = Modifier.wrapContentSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource(R.drawable.line),
                        contentDescription = "logo",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(top = 8.dp)
                            .clickable {
                                onDismiss()
                                selectedValue = ""

                                selectedDateTab = true
                            }
                            .clip(RoundedCornerShape(100.dp))
                            .border( // Add border modifier to make image stand out
                                width = 2.dp, color = Color.Black, shape = CircleShape
                            ))

                    Row(
                        modifier = Modifier.padding(start = 12.dp, top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Filter",
                            fontSize = 18.sp,
                            color = Color.Black,
                            fontFamily = FontFamily(
                                Font(R.font.inter_bold, FontWeight.Bold)
                            )
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Spacer(modifier = Modifier.weight(1f))

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(onClick = {
                            onDismiss()
                            selectedValue = ""

                            selectedDateTab = true
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Divider(color = Color.Gray, thickness = 1.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        // Left Menu
                        Column(
                            modifier = Modifier
                                .background(Color(0xFFF6F7F9)) // Light gray background
                                .width(125.dp)
                                .height(350.dp)
                        ) {
                            ScoreLeftMenuItem("Month & Year", selectedDateTab, onClick = {
                                if (!selectedDateTab) {
                                    selectedDateTab = true
                                }
                            })
                        }

                        // Right Content for select month and year
                        if (selectedDateTab == true) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    "Select Month & Year",
                                    color = Color.Gray,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_regular, FontWeight.Normal)
                                    ),
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Start Date calender
                                YearMonthGridPicker(
                                    selectedYear = selectedYear, selectedMonth = selectedMonth
                                ) { year, month ->
                                    selectedYear = year
                                    selectedMonth = month
                                    if (selectedMonth < 10) {
                                        println("Selected Year: $selectedYear, Month: 0$selectedMonth")
                                        selectedValue = "$selectedYear" + "0$selectedMonth"
//                                        viewModelStudent.setSelectedDate(selectedValue)
                                    } else {
                                        println("Selected Year: $selectedYear, Month: $selectedMonth")
                                        selectedValue = "$selectedYear" + "$selectedMonth"
//                                        viewModelStudent.setSelectedDate(selectedValue)
                                    }
                                }
                            }
                        }
                    }

                    Divider(color = GrayLight02, thickness = 1.dp)

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp, top = 4.dp)
                    ) {
                        val sdf = SimpleDateFormat("yyyyMM")
                        val currentDateAndTime = sdf.format(Date())
                        val dateMonth = currentDateAndTime.toString()
//                        println("Selected date with year :- $dateMonth")

                        Button(
                            onClick = {
                                onDismiss()
                                quizScoreData.clear()
                                viewModelStudent.getQuizScoreReports(
                                    selectedSubjectId, "", ""
                                )
                                selectedValue = ""
                                viewModelStudent.setSelectedDate("")
                                selectedDateTab = true
                            },
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (selectedValue != "") {
                                    "Clear All"
                                } else {
                                    ""
                                }, color = PrimaryBlue, fontSize = 16.sp, fontFamily = FontFamily(
                                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                )
                            )
                        }

                        Button(
                            onClick = {
                                onDismiss()
                                println("Selected month & year $selectedValue")
                                quizScoreData.clear()
                                viewModelStudent.setSelectedDate(selectedValue)
                                viewModelStudent.getQuizScoreReports(
                                    selectedSubjectId, selectedValue, ""
                                )
                                selectedDateTab = true
                            },
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .width(150.dp)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(PrimaryBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Apply",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(
                                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreLeftMenuItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(start = 10.dp, top = 8.dp, bottom = 8.dp)
            .background(if (isSelected) Color.White else Color.Transparent),
        color = if (isSelected) Color(0xFF008CFF) else Color.Gray,
        fontSize = 14.sp)
}

@Preview(showBackground = true)
@Composable
fun QuizScorePreview() {
    AuroscholarAppTheme {
//        QuizAttemptsScreens()
//        QuizScoreSubjectList("English", viewModel)
    }
}
package com.auro.application.ui.features.student.passport.screens

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.components.TextWithIconOnRight
import com.auro.application.ui.common_ui.components.YearMonthGridPicker
import com.auro.application.ui.common_ui.components.monthYearFilterBottomSheet
import com.auro.application.ui.common_ui.components.quizAttemptsFilterBottomSheet
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.GetSubjectListResponseModel
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.passport.models.QuizAttemptResponse
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Bg_Gray
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.Orange
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuizAttemptsScreens(
    navHostController: NavHostController,
    sharedPref: SharedPref? = null,
    intent: String? = remember { mutableStateOf("") }.toString()
) {

    val context: Context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()

    var subjectData by remember { mutableStateOf(mutableListOf<GetSubjectListResponseModel.Data>()) }
    var quizAttemptsData by remember { mutableStateOf(mutableListOf<QuizAttemptResponse.QuizAttemptListData>()) }
    val filterTextHint = stringResource(id = R.string.txt_practice)
    var quizAttemptFilterShow by remember { mutableStateOf(false) }
    var filterValueQuizAttempt by remember { mutableStateOf(filterTextHint) }

    if (filterValueQuizAttempt.equals("Practice")) {
        filterValueQuizAttempt = ""
//        println("Quiz Attempts :- $filterValueQuizAttempt")
    } else {
        if (quizAttemptFilterShow) {
            filterValueQuizAttempt = quizAttemptsFilterBottomSheet(filterValueQuizAttempt) {
                quizAttemptFilterShow = false
            }
        }
//        println("Quiz Attempts :- $filterValueQuizAttempt")
    }

    var monthYearFilterShow by remember { mutableStateOf(false) }
    var dateValueQuizAttempt by remember { mutableStateOf(filterTextHint) }

    var subjectList = remember { mutableStateListOf(TabItems("", 0)) }
    var selectedSubjectName by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableStateOf(0) }

    var isDialogVisible by remember { mutableStateOf(false) }
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
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })

    FilterQuizDialogScreen(quizAttemptsData = quizAttemptsData,
        isBottomSheetVisible = isBottomSheetVisible,
        sheetState = sheetState,
        subjectName = selectedSubjectName,
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
//                        println("All subjects data :- $subjectData")
                        selectedSubjectName = subjectData[0].subjectName
                        selectedSubjectId = subjectData[0].subjectId

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

        viewModel.getQuizAttemptResponseData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                    quizAttemptsData.clear()
                }

                is NetworkStatus.Success -> {
                    quizAttemptsData.clear()
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        quizAttemptsData = it.data.data.toMutableList()
                    } else {
                        isDialogVisible = false
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    quizAttemptsData.clear()
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
                viewModel.getQuizAttempt(
                    selectedSubjectName, "", "", "", ""
                )
            }
        }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { subjectList.size }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
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
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.back_icon),
                        contentDescription = "logo",
                        modifier = Modifier
                            .background(Color.Unspecified)
                            .clickable {
                                navHostController.popBackStack()
                                navHostController.navigate(AppRoute.StudentQuizReport(""))
                            },

                        colorFilter = ColorFilter.tint(Black)
                    )
                    Text(
                        text = stringResource(id = R.string.txt_quiz_attempts),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp)
                            .weight(1f),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Black,
                        textAlign = TextAlign.Start
                    )

                    Image(
                        painter = painterResource(R.drawable.filter_icon),
                        contentDescription = "logo",
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .background(Color.Unspecified)
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
//                            println("Selected filtered date :- $selectedDate")
                            if (selectedDate == "") {
                                viewModel.getQuizAttempt(
                                    selectedSubjectName, "", "", "", ""
                                )
                            } else {
                                viewModel.getQuizAttempt(
                                    selectedSubjectName, "", "", selectedDate, ""
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
                ) { tabItem ->
                    when (tabItem) {
                        0 -> {
                            SubjectList(
                                quizAttemptsData,
                                selectedSubjectName,
                                selectedDate,
                                filterValueQuizAttempt,
                                viewModel
                            )
                        }

                        1 -> {
                            SubjectList(
                                quizAttemptsData,
                                selectedSubjectName,
                                selectedDate,
                                filterValueQuizAttempt,
                                viewModel
                            )
                        }

                        2 -> {
                            SubjectList(
                                quizAttemptsData,
                                selectedSubjectName,
                                selectedDate,
                                filterValueQuizAttempt,
                                viewModel
                            )
                        }

                        3 -> {
                            SubjectList(
                                quizAttemptsData,
                                selectedSubjectName,
                                selectedDate,
                                filterValueQuizAttempt,
                                viewModel
                            )
                        }

                        4 -> {
                            SubjectList(
                                quizAttemptsData,
                                selectedSubjectName,
                                selectedDate,
                                filterValueQuizAttempt,
                                viewModel
                            )
                        }

                        else -> {
                            SubjectList(
                                quizAttemptsData,
                                selectedSubjectName,
                                selectedDate,
                                filterValueQuizAttempt,
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
fun SubjectList(
    quizAttemptsData: MutableList<QuizAttemptResponse.QuizAttemptListData>,
    subject: String,
    selectedDate: String,
    filterQuizAttempt: String,
    viewModel: StudentViewModel
) {

    var searchText by remember { mutableStateOf("") }
    val conceptKeyText = rememberDebounce(searchText, 2000L)
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(conceptKeyText) {
        if (conceptKeyText.isNotBlank()) {
            coroutineScope.launch {
                quizAttemptsData.clear()
                viewModel.getQuizAttempt(
                    subject,
                    conceptKeyText,
                    filterQuizAttempt,
                    selectedDate,
                    if (filterQuizAttempt.contains("Yes")) {
                        "Y"
                    } else if (filterQuizAttempt.contains("No")) {
                        "N"
                    } else {
                        ""
                    }
                )
            }
        } else {
            coroutineScope.launch {
                quizAttemptsData.clear()
                viewModel.getQuizAttempt(
                    subject,
                    conceptKeyText,
                    filterQuizAttempt,
                    selectedDate,
                    if (filterQuizAttempt.contains("Yes")) {
                        "Y"
                    } else if (filterQuizAttempt.contains("No")) {
                        "N"
                    } else {
                        ""
                    }
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
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
                        println("Searched by concept :- $searchText")
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

        if (quizAttemptsData.size != 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                quizAttemptsData.forEach { quizData ->
                    item {
                        QuizAttemptsDataItem(quizData)
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
fun rememberDebounce(input: String, delayMillis: Long): String {
    var debouncedValue by remember { mutableStateOf(input) }
    LaunchedEffect(input) {
        delay(delayMillis)
        debouncedValue = input
    }

    return debouncedValue
}

@SuppressLint("SimpleDateFormat")
@Composable
fun QuizAttemptsDataItem(quizAttemptsData: QuizAttemptResponse.QuizAttemptListData) {

    Text(text = if (quizAttemptsData.examMonth != null) {
        val formattedDate = remember(quizAttemptsData.examMonth) {
            try {
                val inputFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                val date = inputFormat.parse(quizAttemptsData.examMonth.toString())
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

        quizAttemptsData.concepts.forEach { conceptData ->
            ConceptsAttemptsDataItem(conceptData)
        }
    }
}

@Composable
fun ConceptsAttemptsDataItem(
    conceptAttemptsData: QuizAttemptResponse.QuizAttemptListData.Concepts
) {
    val context: Context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (conceptAttemptsData.concept != null) {
                conceptAttemptsData.concept.toString()
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

//        Divider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp, end = 8.dp))

        conceptAttemptsData.quizAttempts.forEach { attemptData ->
            AttemptsDataItem(attemptData)
        }

        TextWithIconOnRight(if (conceptAttemptsData.concept != null) {
            "Start Practice(${conceptAttemptsData.practiceQuizCount}/3)"
        } else {
            "Start Practice(0/3)"
        },
            icon = ImageVector.vectorResource(id = R.drawable.ic_down),
            textColor = PrimaryBlue,
            iconColor = PrimaryBlue,
            modifier = Modifier
                .padding(vertical = 15.dp)
                .wrapContentSize()
                .align(Alignment.CenterHorizontally),
            onClick = {
                context.toast("Coming soon...")
            })

//        Divider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp, end = 8.dp))
    }
}

@Composable
fun AttemptsDataItem(attemptsData: QuizAttemptResponse.QuizAttemptListData.Concepts.QuizAttempts) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
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
                text = if (attemptsData.quizAttempt != null) {
                    "Quiz Attempt : ${attemptsData.quizAttempt}"
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
                color = Orange,
                textAlign = TextAlign.Start
            )
            Text(
                text = if (attemptsData.examStart != null) {
                    attemptsData.examStart.toString()
                } else {
                    "N/A"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(end = 15.dp, top = 10.dp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = GrayLight01,
                textAlign = TextAlign.End
            )
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
                text = if (attemptsData.score != null) {
                    "Score : ${attemptsData.score}"
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
                text = if (attemptsData.eligibleScholarshipAmount != null) {
                    "Eligible for Micro-scholarship: ${attemptsData.eligibleScholarshipAmount}"
                } else {
                    "Eligible for Micro-scholarship: N/A"
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
}

data class TabItems(
    var title: String, var subjectId: Int
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterQuizDialogScreen(
    quizAttemptsData: MutableList<QuizAttemptResponse.QuizAttemptListData>,
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    subjectName: String,
    onDismiss: () -> Unit
) {
    val context: Context = LocalContext.current
    val viewModel: LoginViewModel = hiltViewModel()
    val viewModelStudent: StudentViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    var selectedQuizAttempt by remember { mutableStateOf<String?>("") }
    var selectedAttempted by remember { mutableStateOf<String?>("") }
    var selectedMicroscholarship by remember { mutableStateOf<String?>("") }
    var selectedDateTab by remember { mutableStateOf(true) }
    var selectedQuizAttemptTab by remember { mutableStateOf(false) }
    var selectedMicroscholarshipTab by remember { mutableStateOf(false) }
//    var selectedYear by remember { mutableStateOf(Year.now().value) }
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now().monthValue) }
    var selectedValue by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

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
                                selectedQuizAttempt = ""
                                selectedMicroscholarship = ""

                                selectedDateTab = true
                                selectedQuizAttemptTab = false
                                selectedMicroscholarshipTab = false
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
                            selectedQuizAttempt = ""
                            selectedMicroscholarship = ""

                            selectedDateTab = true
                            selectedQuizAttemptTab = false
                            selectedMicroscholarshipTab = false
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
                            QuizLeftMenuItem("Month & Year", selectedDateTab, onClick = {
                                if (!selectedDateTab) {
                                    selectedDateTab = true
                                    selectedQuizAttemptTab = false
                                    selectedMicroscholarshipTab = false
                                }
                            })

                            QuizLeftMenuItem("Quiz Attempt", selectedQuizAttemptTab, onClick = {
                                if (!selectedQuizAttemptTab) {
                                    selectedQuizAttemptTab = true
                                    selectedDateTab = false
                                    selectedMicroscholarshipTab = false
                                }
                            })

                            QuizLeftMenuItem("Microscholarship",
                                selectedMicroscholarshipTab,
                                onClick = {
                                    if (!selectedMicroscholarshipTab) {
                                        selectedMicroscholarshipTab = true
                                        selectedDateTab = false
                                        selectedQuizAttemptTab = false
                                    }
                                })
                        }

                        // Right Content for select month and year
                        if (selectedDateTab == true && selectedQuizAttemptTab == false && selectedMicroscholarshipTab == false) {
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

                        // Right Content for quiz attempt
                        if (selectedQuizAttemptTab == true && selectedDateTab == false && selectedMicroscholarshipTab == false) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Select Quiz Attempt",
                                    color = GrayLight01,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_regular, FontWeight.Normal)
                                    ),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 4.dp, end = 4.dp, top = 4.dp, bottom = 16.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Core", color = Color.Black, fontFamily = FontFamily(
                                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                        ), fontSize = 12.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(24.dp) // Size of the circular checkbox
                                            .border(
                                                2.dp, PrimaryBlue, CircleShape
                                            ) // Border for the circle
                                            .background(
                                                if (selectedQuizAttempt == "Core") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedQuizAttempt =
                                                    if (selectedQuizAttempt == "Core") "" else "Core"
                                                selectedAttempted = "1"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedQuizAttempt == "Core") {
                                            Icon(
                                                imageVector = Icons.Default.Check, // Check icon
                                                contentDescription = "Checked",
                                                tint = Color.White, // Color for the check icon
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(Color.Unspecified) // Size of the check icon
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 4.dp, end = 4.dp, top = 4.dp, bottom = 16.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Try again 1",
                                        color = Color.Black,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                        ),
                                        fontSize = 12.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(24.dp) // Size of the circular checkbox
                                            .border(
                                                2.dp, PrimaryBlue, CircleShape
                                            ) // Border for the circle
                                            .background(
                                                if (selectedQuizAttempt == "Try again 1") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedQuizAttempt =
                                                    if (selectedQuizAttempt == "Try again 1") "" else "Try again 1"
                                                selectedAttempted = "2"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedQuizAttempt == "Try again 1") {
                                            Icon(
                                                imageVector = Icons.Default.Check, // Check icon
                                                contentDescription = "Checked",
                                                tint = Color.White, // Color for the check icon
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(Color.Unspecified) // Size of the check icon
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 4.dp, end = 4.dp, top = 4.dp, bottom = 16.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Try again 2",
                                        color = Color.Black,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                        ),
                                        fontSize = 12.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(24.dp) // Size of the circular checkbox
                                            .border(
                                                2.dp, PrimaryBlue, CircleShape
                                            ) // Border for the circle
                                            .background(
                                                if (selectedQuizAttempt == "Try again 2") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedQuizAttempt =
                                                    if (selectedQuizAttempt == "Try again 2") "" else "Try again 2"
                                                selectedAttempted = "3"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedQuizAttempt == "Try again 2") {
                                            Icon(
                                                imageVector = Icons.Default.Check, // Check icon
                                                contentDescription = "Checked",
                                                tint = Color.White, // Color for the check icon
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(Color.Unspecified) // Size of the check icon
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 4.dp, end = 4.dp, top = 4.dp, bottom = 16.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Practice 1",
                                        color = Color.Black,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                        ),
                                        fontSize = 12.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(24.dp) // Size of the circular checkbox
                                            .border(
                                                2.dp, PrimaryBlue, CircleShape
                                            ) // Border for the circle
                                            .background(
                                                if (selectedQuizAttempt == "Practice 1") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedQuizAttempt =
                                                    if (selectedQuizAttempt == "Practice 1") "" else "Practice 1"
                                                selectedAttempted = "4"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedQuizAttempt == "Practice 1") {
                                            Icon(
                                                imageVector = Icons.Default.Check, // Check icon
                                                contentDescription = "Checked",
                                                tint = Color.White, // Color for the check icon
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(Color.Unspecified) // Size of the check icon
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 4.dp, end = 4.dp, top = 4.dp, bottom = 16.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Practice 2",
                                        color = Color.Black,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                        ),
                                        fontSize = 12.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(24.dp) // Size of the circular checkbox
                                            .border(
                                                2.dp, PrimaryBlue, CircleShape
                                            ) // Border for the circle
                                            .background(
                                                if (selectedQuizAttempt == "Practice 2") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedQuizAttempt =
                                                    if (selectedQuizAttempt == "Practice 2") "" else "Practice 2"
                                                selectedAttempted = "5"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedQuizAttempt == "Practice 2") {
                                            Icon(
                                                imageVector = Icons.Default.Check, // Check icon
                                                contentDescription = "Checked",
                                                tint = Color.White, // Color for the check icon
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(Color.Unspecified) // Size of the check icon
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 4.dp, end = 4.dp, top = 4.dp, bottom = 16.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Practice 3",
                                        color = Color.Black,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                        ),
                                        fontSize = 12.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(24.dp) // Size of the circular checkbox
                                            .border(
                                                2.dp, PrimaryBlue, CircleShape
                                            ) // Border for the circle
                                            .background(
                                                if (selectedQuizAttempt == "Practice 3") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedQuizAttempt =
                                                    if (selectedQuizAttempt == "Practice 3") "" else "Practice 3"
                                                selectedAttempted = "6"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedQuizAttempt == "Practice 3") {
                                            Icon(
                                                imageVector = Icons.Default.Check, // Check icon
                                                contentDescription = "Checked",
                                                tint = Color.White, // Color for the check icon
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(Color.Unspecified) // Size of the check icon
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Right Content for microscholarship
                        if (selectedMicroscholarshipTab == true && selectedDateTab == false && selectedQuizAttemptTab == false) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Select Microscholarship",
                                    color = GrayLight01,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_regular, FontWeight.Normal)
                                    ),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 4.dp, end = 4.dp, top = 4.dp, bottom = 16.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Yes", color = Color.Black, fontFamily = FontFamily(
                                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                        ), fontSize = 12.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(24.dp) // Size of the circular checkbox
                                            .border(
                                                2.dp, PrimaryBlue, CircleShape
                                            ) // Border for the circle
                                            .background(
                                                if (selectedMicroscholarship == "Yes") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedMicroscholarship =
                                                    if (selectedMicroscholarship == "Yes") "" else "Yes"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedMicroscholarship == "Yes") {
                                            Icon(
                                                imageVector = Icons.Default.Check, // Check icon
                                                contentDescription = "Checked",
                                                tint = Color.White, // Color for the check icon
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(Color.Unspecified) // Size of the check icon
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 4.dp, end = 4.dp, top = 4.dp, bottom = 16.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "No", color = Color.Black, fontFamily = FontFamily(
                                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                        ), fontSize = 12.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(24.dp) // Size of the circular checkbox
                                            .border(
                                                2.dp, PrimaryBlue, CircleShape
                                            ) // Border for the circle
                                            .background(
                                                if (selectedMicroscholarship == "No") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedMicroscholarship =
                                                    if (selectedMicroscholarship == "No") "" else "No"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedMicroscholarship == "No") {
                                            Icon(
                                                imageVector = Icons.Default.Check, // Check icon
                                                contentDescription = "Checked",
                                                tint = Color.White, // Color for the check icon
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(Color.Unspecified) // Size of the check icon
                                            )
                                        }
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
                                quizAttemptsData.clear()
                                viewModelStudent.getQuizAttempt(
                                    subjectName, "", "", "", ""
                                )
                                selectedValue = ""
                                viewModelStudent.setSelectedDate("")
                                selectedQuizAttempt = ""
                                selectedMicroscholarship = ""
                                selectedDateTab = true
                                selectedQuizAttemptTab = false
                                selectedMicroscholarshipTab = false
                            },
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (selectedValue != "" || selectedQuizAttempt != "" || selectedMicroscholarship != "") {
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
                                println("Selected month & year $selectedValue, and quiz attempt :- $selectedAttempted, and microscholarship :- $selectedMicroscholarship")
                                quizAttemptsData.clear()
                                viewModelStudent.setSelectedDate(selectedValue)
                                viewModelStudent.getQuizAttempt(
                                    subjectName,
                                    "",
                                    selectedAttempted.toString(),
                                    selectedValue,
                                    if (selectedMicroscholarship!!.contains("Yes")) {
                                        "Y"
                                    } else if (selectedMicroscholarship!!.contains("No")) {
                                        "N"
                                    } else {
                                        ""
                                    }
                                )

                                selectedDateTab = true
                                selectedQuizAttemptTab = false
                                selectedMicroscholarshipTab = false
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
fun QuizLeftMenuItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
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
fun StudentQuizAttemptsPreview() {
    AuroscholarAppTheme {
//        QuizAttemptsScreens()
//        SubjectList("English", viewModel)
    }
}
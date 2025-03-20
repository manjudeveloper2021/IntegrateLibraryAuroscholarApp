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
import androidx.compose.material.OutlinedTextField
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
import com.auro.application.ui.common_ui.components.quizVerificationStatusBottomSheet
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.GetSubjectListResponseModel
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.passport.models.QuizVerificationResponse
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Bg_Gray
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.DarkRed2
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.GreenDark01
import com.auro.application.ui.theme.Orange
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Year
import java.time.YearMonth
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuizVerificationScreen(
    navHostController: NavHostController,
    sharedPref: SharedPref? = null,
    intent: String? = remember { mutableStateOf("") }.toString()
) {

    val context: Context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()
    val viewModelLogin: LoginViewModel = hiltViewModel()

    var subjectData by remember { mutableStateOf(mutableListOf<GetSubjectListResponseModel.Data>()) }
    var isDialogVisible by remember { mutableStateOf(false) }
//    var quizVerificationData by remember { mutableStateOf(mutableListOf<QuizVerificationResponse.QuizVerificationData>()) }
    var quizVerificationDate by remember { mutableStateOf("") }
    var quizVerificationData by remember { mutableStateOf(mutableListOf<QuizVerificationResponse.QuizVerificationData.QuizConcepts>()) }
    val filterTextHint = stringResource(id = R.string.txt_practice)
    var quizAttemptFilterShow by remember { mutableStateOf(false) }
    var filterValueQuizAttempt by remember { mutableStateOf(filterTextHint) }

    var selectedDate by remember { mutableStateOf("") }
    selectedDate = viewModel.getSelectedDate().toString()

    if (filterValueQuizAttempt.equals("Practice")) {
        filterValueQuizAttempt = ""
//        println("Quiz Attempts :- $filterValueQuizAttempt")
    } else {
        if (quizAttemptFilterShow) {
            filterValueQuizAttempt = quizVerificationStatusBottomSheet(filterValueQuizAttempt) {
                quizAttemptFilterShow = false
            }
        }
//        println("Quiz Attempts :- $filterValueQuizAttempt")
    }

//    println("Selected verification status :- $filterValueQuizAttempt")
    var monthYearFilterShow by remember { mutableStateOf(false) }
    var dateValueQuizAttempt by remember { mutableStateOf(filterTextHint) }

    var subjectList = remember { mutableStateListOf(TabItems("", 0)) }
    var selectedSubjectName by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableStateOf(0) }
    val strUserId = viewModelLogin.getUserId().toString()
    val coroutineScope = rememberCoroutineScope()

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

    FilterVerificationDialogScreen(quizVerificationData = quizVerificationData,
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

        viewModel.getQuizVerificationResponseData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                    quizVerificationData.clear()
                }

                is NetworkStatus.Success -> {
                    quizVerificationData.clear()
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
//                        quizVerificationData = it.data.data.toMutableList()
                        quizVerificationDate = it.data.data.month.toString()
                        quizVerificationData = it.data.data.concepts.toMutableList()
                        println("All quiz verification data :- $quizVerificationData")
                    } else {
                        isDialogVisible = false
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    quizVerificationData.clear()
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
                viewModel.getQuizVerificationReports(
                    strUserId, selectedSubjectName, dateValueQuizAttempt, "", ""
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
                        modifier = Modifier
                            .background(Color.Unspecified)
                            .clickable {
                                navHostController.popBackStack()
                                navHostController.navigate(AppRoute.StudentQuizReport(""))
                            },

                        colorFilter = ColorFilter.tint(Black)
                    )
                    Text(
                        text = stringResource(id = R.string.txt_quiz_verification),
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
                            if (selectedDate == "") {
                                viewModel.getQuizVerificationReports(
                                    strUserId, selectedSubjectName, dateValueQuizAttempt, "", ""
                                )
                            } else {
                                viewModel.getQuizVerificationReports(
                                    strUserId, selectedSubjectName, selectedDate, "", ""
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
                            VerificationSubjectList(
                                quizVerificationDate,
                                quizVerificationData,
                                selectedSubjectName,
                                dateValueQuizAttempt,
                                selectedDate,
                                filterValueQuizAttempt,
                                viewModel
                            )
                        }

                        1 -> {
                            VerificationSubjectList(
                                quizVerificationDate,
                                quizVerificationData,
                                selectedSubjectName,
                                dateValueQuizAttempt,
                                selectedDate,
                                filterValueQuizAttempt,
                                viewModel
                            )
                        }

                        2 -> {
                            VerificationSubjectList(
                                quizVerificationDate,
                                quizVerificationData,
                                selectedSubjectName,
                                dateValueQuizAttempt,
                                selectedDate,
                                filterValueQuizAttempt,
                                viewModel
                            )
                        }

                        3 -> {
                            VerificationSubjectList(
                                quizVerificationDate,
                                quizVerificationData,
                                selectedSubjectName,
                                dateValueQuizAttempt,
                                selectedDate,
                                filterValueQuizAttempt,
                                viewModel
                            )
                        }

                        4 -> {
                            VerificationSubjectList(
                                quizVerificationDate,
                                quizVerificationData,
                                selectedSubjectName,
                                dateValueQuizAttempt,
                                selectedDate,
                                filterValueQuizAttempt,
                                viewModel
                            )
                        }

                        else -> {
                            VerificationSubjectList(
                                quizVerificationDate,
                                quizVerificationData,
                                selectedSubjectName,
                                dateValueQuizAttempt,
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
fun VerificationSubjectList(
    quizVerificationDate: String,
    quizVerificationData: MutableList<QuizVerificationResponse.QuizVerificationData.QuizConcepts>,
    subject: String,
    dateMonth: String,
    selectedDate: String,
    filterQuizAttempt: String,
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
                quizVerificationData.clear()
                viewModel.getQuizVerificationReports(
                    strUserId, subject, selectedDate, filterQuizAttempt, conceptKeyText
                )
            }
        } else {
            coroutineScope.launch {
                quizVerificationData.clear()
                viewModel.getQuizVerificationReports(
                    strUserId, subject, selectedDate, filterQuizAttempt, conceptKeyText
                )
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

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                        .background(color = Color.White),
                    placeholder = {
                        Text(
                            "Search by concept name ", color = Color.Gray
                        )
                    },
                )


//                TextField(
//                    value = searchText,
//                    onValueChange = { newText ->
//                        searchText = newText
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(color = Color.White), // Set background color to white
//                    placeholder = {
//                        Text(
//                            "Search by concept name ", color = Color.Gray
//                        )
//                    }, // Placeholder text color
//                    colors = TextFieldDefaults,
////                        .textFieldColors(
////                        containerColor = Color.White,
////                        cursorColor = Black,
////                        focusedIndicatorColor = Color.Transparent,
////                        unfocusedIndicatorColor = Color.Transparent
////                    ),
//                    singleLine = true
//                )

            }
        }

        if (quizVerificationData.size != 0) {
            Text(text = if (quizVerificationDate != null) {
                val formattedDate = remember(quizVerificationDate) {
                    try {
                        val inputFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                        val date = inputFormat.parse(quizVerificationDate)
                        outputFormat.format(date ?: Date())
                    } catch (e: Exception) {
                        "N/A"
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

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                quizVerificationData.forEach { verificationData ->
                    item {
                        println("Verification Data :- $verificationData")
//                        QuizVerificationDataItem(verificationData)
                        QuizVerificationDataItem(verificationData)
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
}/*
@Composable
fun QuizVerificationDataItem(quizVerificationData: QuizVerificationResponse.QuizVerificationData) {

    Text(text = if (quizVerificationData.month != null) {
        val formattedDate = remember(quizVerificationData.month) {
            try {
                val inputFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                val date = inputFormat.parse(quizVerificationData.month.toString())
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                "N/A"
            }
        }

        formattedDate.toString()
    } else if (quizVerificationData.examMonth != null) {
        val formattedDate = remember(quizVerificationData.examMonth) {
            try {
                val inputFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                val date = inputFormat.parse(quizVerificationData.examMonth.toString())
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                "N/A"
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

    if (quizVerificationData.concepts != null) {
        quizVerificationData.concepts.forEach { conceptsData ->
            QuizConceptsDataItem(conceptsData)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .border(
                    width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
                )
        ) {
            Text(
                text = if (quizVerificationData.concept != null) {
                    quizVerificationData.concept.toString()
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
                    text = if (quizVerificationData.attempt != null) {
                        "Attempt : ${quizVerificationData.attempt}"
                    } else {
                        "Attempt : 0"
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
                        text = "Status: ",
                        modifier = Modifier.wrapContentSize(),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = GrayLight01,
                        textAlign = TextAlign.End
                    )

                    Text(
                        text = if (quizVerificationData.quizStatus != null) {
                            quizVerificationData.quizStatus.toString()
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

            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(vertical = 10.dp, horizontal = 15.dp)
                    .border(
                        width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
                    )
                    .background(color = Bg_Gray)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (quizVerificationData.score != null) {
                        "Score : ${quizVerificationData.score.toString()}"
                    } else {
                        "Score : 0"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 5.dp, top = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = if (quizVerificationData.microScholarshipDisbursal != null) {
                        "Eligible for Microscholarship: ${quizVerificationData.microScholarshipDisbursal.toString()}"
                    } else {
                        "Eligible for Microscholarship: N/A"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 5.dp, top = 5.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = if (quizVerificationData.verificationRemark != null) {
                        "Remark: ${quizVerificationData.verificationRemark}"
                    } else {
                        "Remark: N/A"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 5.dp, top = 5.dp, bottom = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )
            }

            *//* conceptsData.attempts.forEach { attemptsData ->
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
                         text = if (attemptsData.attempt != null) {
                             "Attempt : ${attemptsData.attempt}"
                         } else {
                             "Attempt : 0"
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
                             text = "Status: ",
                             modifier = Modifier.wrapContentSize(),
                             fontStyle = FontStyle.Normal,
                             fontWeight = FontWeight.Medium,
                             fontSize = 12.sp,
                             color = GrayLight01,
                             textAlign = TextAlign.End
                         )

                         Text(
                             text = if (attemptsData.quizStatus != null) {
                                 attemptsData.quizStatus.toString()
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

                 Column(
                     modifier = Modifier
                         .wrapContentHeight()
                         .padding(vertical = 10.dp, horizontal = 15.dp)
                         .border(
                             width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
                         )
                         .background(color = Bg_Gray)
                         .fillMaxWidth(),
                     horizontalAlignment = Alignment.CenterHorizontally
                 ) {
                     Text(
                         text = if (attemptsData.score != null) {
                             "Score : ${attemptsData.score.toString()}"
                         } else {
                             "Score : 0"
                         },
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(start = 10.dp, end = 5.dp, top = 10.dp),
                         fontStyle = FontStyle.Normal,
                         fontWeight = FontWeight.Medium,
                         fontSize = 12.sp,
                         color = Black,
                         textAlign = TextAlign.Start
                     )

                     Text(
                         text = if (attemptsData.microScholarshipDisbursal != null) {
                             "Eligible for Microscholarship: ${attemptsData.microScholarshipDisbursal.toString()}"
                         } else {
                             "Eligible for Microscholarship: N/A"
                         },
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(start = 10.dp, end = 5.dp, top = 5.dp),
                         fontStyle = FontStyle.Normal,
                         fontWeight = FontWeight.Normal,
                         fontSize = 12.sp,
                         color = Black,
                         textAlign = TextAlign.Start
                     )

                     Text(
                         text = if (attemptsData.verificationRemark != null) {
                             "Remark: ${attemptsData.verificationRemark}"
                         } else {
                             "Remark: N/A"
                         },
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(start = 10.dp, end = 5.dp, top = 5.dp, bottom = 10.dp),
                         fontStyle = FontStyle.Normal,
                         fontWeight = FontWeight.Normal,
                         fontSize = 12.sp,
                         color = Black,
                         textAlign = TextAlign.Start
                     )
                 }
             }*//*
        }
    }
}*/

@Composable
fun QuizVerificationDataItem(quizVerificationData: QuizVerificationResponse.QuizVerificationData.QuizConcepts) {

    /*Text(text = if (quizVerificationData.month != null) {
        val formattedDate = remember(quizVerificationData.month) {
            try {
                val inputFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                val date = inputFormat.parse(quizVerificationData.month.toString())
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                "N/A"
            }
        }

        formattedDate.toString()
    } else if (quizVerificationData.examMonth != null) {
        val formattedDate = remember(quizVerificationData.examMonth) {
            try {
                val inputFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                val date = inputFormat.parse(quizVerificationData.examMonth.toString())
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                "N/A"
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
        textAlign = TextAlign.Start)*/

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .border(
                width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            text = if (quizVerificationData.name != null) {
                quizVerificationData.name.toString()
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

        quizVerificationData.attempts.forEach { attemptsData ->
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
                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (attemptsData.attempt != null) {
                        "Attempt : ${attemptsData.attempt}"
                    } else {
                        "Attempt : 0"
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
                        text = "Status: ",
                        modifier = Modifier.wrapContentSize(),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = GrayLight01,
                        textAlign = TextAlign.End
                    )

                    Text(
                        text = if (attemptsData.quizStatus != null) {
                            attemptsData.quizStatus.toString()
                        } else {
                            "N/A"
                        },
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(horizontal = 5.dp),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (attemptsData.quizStatus.equals("Inprocess")) {
                            Orange
                        } else if (attemptsData.quizStatus.equals("Approve") || attemptsData.quizStatus.equals(
                                "Approved"
                            )
                        ) {
                            GreenDark01
                        } else if (attemptsData.quizStatus.equals("Disapprove") || attemptsData.quizStatus.equals(
                                "Disapproved"
                            )
                        ) {
                            DarkRed2
                        } else {
                            DarkRed2
                        },
                        textAlign = TextAlign.End
                    )
                }
            }

            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(vertical = 10.dp, horizontal = 15.dp)
                    .border(
                        width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
                    )
                    .background(color = Bg_Gray)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (attemptsData.score != null) {
                        "Score : ${attemptsData.score.toString()}"
                    } else {
                        "Score : 0"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 5.dp, top = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = if (attemptsData.microScholarshipDisbursal != null) {
                        "Eligible for Microscholarship: ${attemptsData.microScholarshipDisbursal.toString()}"
                    } else {
                        "Eligible for Microscholarship: N/A"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 5.dp, top = 5.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = if (attemptsData.verificationRemark != null) {
                        "Remark: ${attemptsData.verificationRemark}"
                    } else {
                        "Remark: N/A"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 5.dp, top = 5.dp, bottom = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )
            }
        }
    }

    /*if (quizVerificationData.attempts != null) {
        quizVerificationData.attempts.forEach { conceptsData ->
            QuizConceptsDataItem(conceptsData)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .border(
                        width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Text(
                    text = if (conceptsData.name != null) {
                        conceptsData.name.toString()
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

                conceptsData.attempts.forEach { attemptsData ->
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
                            text = if (attemptsData.attempt != null) {
                                "Attempt : ${attemptsData.attempt}"
                            } else {
                                "Attempt : 0"
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
                                text = "Status: ",
                                modifier = Modifier.wrapContentSize(),
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = GrayLight01,
                                textAlign = TextAlign.End
                            )

                            Text(
                                text = if (attemptsData.quizStatus != null) {
                                    attemptsData.quizStatus.toString()
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

                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(vertical = 10.dp, horizontal = 15.dp)
                            .border(
                                width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
                            )
                            .background(color = Bg_Gray)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (attemptsData.score != null) {
                                "Score : ${attemptsData.score.toString()}"
                            } else {
                                "Score : 0"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 5.dp, top = 10.dp),
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = Black,
                            textAlign = TextAlign.Start
                        )

                        Text(
                            text = if (attemptsData.microScholarshipDisbursal != null) {
                                "Eligible for Microscholarship: ${attemptsData.microScholarshipDisbursal.toString()}"
                            } else {
                                "Eligible for Microscholarship: N/A"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 5.dp, top = 5.dp),
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = Black,
                            textAlign = TextAlign.Start
                        )

                        Text(
                            text = if (attemptsData.verificationRemark != null) {
                                "Remark: ${attemptsData.verificationRemark}"
                            } else {
                                "Remark: N/A"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 5.dp, top = 5.dp, bottom = 10.dp),
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = Black,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }

        }
    }*/ /*else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .border(
                    width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
                )
        ) {
            Text(
                text = if (quizVerificationData.concept != null) {
                    quizVerificationData.concept.toString()
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
                    text = if (quizVerificationData.attempt != null) {
                        "Attempt : ${quizVerificationData.attempt}"
                    } else {
                        "Attempt : 0"
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
                        text = "Status: ",
                        modifier = Modifier.wrapContentSize(),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = GrayLight01,
                        textAlign = TextAlign.End
                    )

                    Text(
                        text = if (quizVerificationData.quizStatus != null) {
                            quizVerificationData.quizStatus.toString()
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

            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(vertical = 10.dp, horizontal = 15.dp)
                    .border(
                        width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
                    )
                    .background(color = Bg_Gray)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (quizVerificationData.score != null) {
                        "Score : ${quizVerificationData.score.toString()}"
                    } else {
                        "Score : 0"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 5.dp, top = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = if (quizVerificationData.microScholarshipDisbursal != null) {
                        "Eligible for Microscholarship: ${quizVerificationData.microScholarshipDisbursal.toString()}"
                    } else {
                        "Eligible for Microscholarship: N/A"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 5.dp, top = 5.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = if (quizVerificationData.verificationRemark != null) {
                        "Remark: ${quizVerificationData.verificationRemark}"
                    } else {
                        "Remark: N/A"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 5.dp, top = 5.dp, bottom = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )
            }

            *//* conceptsData.attempts.forEach { attemptsData ->
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
                         text = if (attemptsData.attempt != null) {
                             "Attempt : ${attemptsData.attempt}"
                         } else {
                             "Attempt : 0"
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
                             text = "Status: ",
                             modifier = Modifier.wrapContentSize(),
                             fontStyle = FontStyle.Normal,
                             fontWeight = FontWeight.Medium,
                             fontSize = 12.sp,
                             color = GrayLight01,
                             textAlign = TextAlign.End
                         )

                         Text(
                             text = if (attemptsData.quizStatus != null) {
                                 attemptsData.quizStatus.toString()
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

                 Column(
                     modifier = Modifier
                         .wrapContentHeight()
                         .padding(vertical = 10.dp, horizontal = 15.dp)
                         .border(
                             width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
                         )
                         .background(color = Bg_Gray)
                         .fillMaxWidth(),
                     horizontalAlignment = Alignment.CenterHorizontally
                 ) {
                     Text(
                         text = if (attemptsData.score != null) {
                             "Score : ${attemptsData.score.toString()}"
                         } else {
                             "Score : 0"
                         },
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(start = 10.dp, end = 5.dp, top = 10.dp),
                         fontStyle = FontStyle.Normal,
                         fontWeight = FontWeight.Medium,
                         fontSize = 12.sp,
                         color = Black,
                         textAlign = TextAlign.Start
                     )

                     Text(
                         text = if (attemptsData.microScholarshipDisbursal != null) {
                             "Eligible for Microscholarship: ${attemptsData.microScholarshipDisbursal.toString()}"
                         } else {
                             "Eligible for Microscholarship: N/A"
                         },
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(start = 10.dp, end = 5.dp, top = 5.dp),
                         fontStyle = FontStyle.Normal,
                         fontWeight = FontWeight.Normal,
                         fontSize = 12.sp,
                         color = Black,
                         textAlign = TextAlign.Start
                     )

                     Text(
                         text = if (attemptsData.verificationRemark != null) {
                             "Remark: ${attemptsData.verificationRemark}"
                         } else {
                             "Remark: N/A"
                         },
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(start = 10.dp, end = 5.dp, top = 5.dp, bottom = 10.dp),
                         fontStyle = FontStyle.Normal,
                         fontWeight = FontWeight.Normal,
                         fontSize = 12.sp,
                         color = Black,
                         textAlign = TextAlign.Start
                     )
                 }
             }*//*
        }
    }*/
}

@Composable
fun QuizConceptsDataItem(conceptsData: QuizVerificationResponse.QuizVerificationData.QuizConcepts) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .border(
                width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            text = if (conceptsData.name != null) {
                conceptsData.name.toString()
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

        conceptsData.attempts.forEach { attemptsData ->
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
                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (attemptsData.attempt != null) {
                        "Attempt : ${attemptsData.attempt}"
                    } else {
                        "Attempt : 0"
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
                        text = "Status: ",
                        modifier = Modifier.wrapContentSize(),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = GrayLight01,
                        textAlign = TextAlign.End
                    )

                    Text(
                        text = if (attemptsData.quizStatus != null) {
                            attemptsData.quizStatus.toString()
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

            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(vertical = 10.dp, horizontal = 15.dp)
                    .border(
                        width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
                    )
                    .background(color = Bg_Gray)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (attemptsData.score != null) {
                        "Score : ${attemptsData.score.toString()}"
                    } else {
                        "Score : 0"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 5.dp, top = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = if (attemptsData.microScholarshipDisbursal != null) {
                        "Eligible for Microscholarship: ${attemptsData.microScholarshipDisbursal.toString()}"
                    } else {
                        "Eligible for Microscholarship: N/A"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 5.dp, top = 5.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = if (attemptsData.verificationRemark != null) {
                        "Remark: ${attemptsData.verificationRemark}"
                    } else {
                        "Remark: N/A"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 5.dp, top = 5.dp, bottom = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterVerificationDialogScreen(
    quizVerificationData: MutableList<QuizVerificationResponse.QuizVerificationData.QuizConcepts>,
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
    var isDialogVisible by remember { mutableStateOf(false) }

    var selectedQuizAttempt by remember { mutableStateOf<String?>("") }
    var selectedDateTab by remember { mutableStateOf(true) }
    var selectedQuizStatusTab by remember { mutableStateOf(false) }
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
          //  windowInsets = WindowInsets.ime
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

                                selectedDateTab = true
                                selectedQuizStatusTab = false
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

                            selectedDateTab = true
                            selectedQuizStatusTab = false
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
                            VerificationLeftMenuItem("Month & Year", selectedDateTab, onClick = {
                                if (!selectedDateTab) {
                                    selectedDateTab = true
                                    selectedQuizStatusTab = false
                                }
                            })

                            VerificationLeftMenuItem(
                                "Quiz Status",
                                selectedQuizStatusTab,
                                onClick = {
                                    if (!selectedQuizStatusTab) {
                                        selectedQuizStatusTab = true
                                        selectedDateTab = false
                                    }
                                })
                        }

                        // Right Content for select month and year
                        if (selectedDateTab == true && selectedQuizStatusTab == false) {
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
                        if (selectedQuizStatusTab == true && selectedDateTab == false) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Select Quiz Status",
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
                                        text = "Approved",
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
                                                if (selectedQuizAttempt == "Approved") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedQuizAttempt =
                                                    if (selectedQuizAttempt == "Approved") "" else "Approved"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedQuizAttempt == "Approved") {
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
                                        text = "Disapproved",
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
                                                if (selectedQuizAttempt == "Disapproved") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedQuizAttempt =
                                                    if (selectedQuizAttempt == "Disapproved") "" else "Disapproved"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedQuizAttempt == "Disapproved") {
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
                                        text = "Inprocess",
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
                                                if (selectedQuizAttempt == "Inprocess") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedQuizAttempt =
                                                    if (selectedQuizAttempt == "Inprocess") "" else "Inprocess"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedQuizAttempt == "Inprocess") {
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
                                        text = "Request for Viva",
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
                                                if (selectedQuizAttempt == "Request for Viva") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedQuizAttempt =
                                                    if (selectedQuizAttempt == "Request for Viva") "" else "Request for Viva"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedQuizAttempt == "Request for Viva") {
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
                        println("Selected date with year :- $dateMonth")

                        Button(
                            onClick = {
                                onDismiss()
                                isDialogVisible = true
                                selectedValue = ""
                                selectedQuizAttempt = ""
                                selectedDateTab = true
                                selectedQuizStatusTab = false
                                viewModelStudent.setSelectedDate("")
                                quizVerificationData.clear()
                                viewModelStudent.getQuizVerificationReports(
                                    strUserId, selectedSubjectId, dateMonth, "", ""
                                )
                            },
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (selectedValue != "" || selectedQuizAttempt != "") {
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
                                println("Selected month & year $selectedValue, and quiz status :- $selectedQuizAttempt")
                                isDialogVisible = true
                                viewModelStudent.setSelectedDate(selectedValue)
                                quizVerificationData.clear()
                                viewModelStudent.getQuizVerificationReports(
                                    strUserId,
                                    selectedSubjectId,
                                    selectedValue,
                                    selectedQuizAttempt.toString(),
                                    ""
                                )
                                selectedDateTab = true
                                selectedQuizStatusTab = false
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
fun VerificationLeftMenuItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
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
fun StudentVerificationQuizPreview() {
    AuroscholarAppTheme {
//        QuizAttemptsScreens()
//        VerificationSubjectList("English", viewModel)
    }
}
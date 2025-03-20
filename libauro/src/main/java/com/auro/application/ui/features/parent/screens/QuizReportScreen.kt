package com.auro.application.ui.features.parent.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.BtnNextUi
import com.auro.application.ui.common_ui.CircleCheckbox
import com.auro.application.ui.common_ui.TextFieldWithIcon
import com.auro.application.ui.common_ui.components.TextWithIconOnLeft
import com.auro.application.ui.common_ui.showWalletDatePickerDialog
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.parent.model.GetQuizReportResposeModel
import com.auro.application.ui.features.parent.model.GetQuizVerificationTableResponseModel
import com.auro.application.ui.features.parent.model.GradeWiseSubjectResponseModel
import com.auro.application.ui.features.parent.viewmodel.ParentViewModel
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.GreenDark02
import com.auro.application.ui.theme.LightRed01
import com.auro.application.ui.theme.LightYellow02
import com.auro.application.ui.theme.Orange
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.PrimaryBlueLt
import com.google.accompanist.pager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun QuizReportScreen(
    navController: NavHostController = rememberNavController(),
    sharedPref: SharedPref? = null,
    title: (String) -> Unit = {},
    isFilter: Boolean = false,
    viewModel: ParentViewModel = hiltViewModel()
) {

    val tabItems = listOf("Quiz Performance Table", " Quiz Verification Table")
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()


    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    // LaunchedEffect to show/hide the bottom sheet based on isFilter
    LaunchedEffect(isFilter) {
        title.invoke("Reports")
        if (isFilter) {
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
        }
    }



    // Wrap the entire content in ModalBottomSheetLayout
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            TopBarFilterUi(scope = coroutineScope, bottomSheetState = bottomSheetState, viewModel)
        }, sheetShape = RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp)
    ) {
        // Main content of the screen
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.background(color = Color.White),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = PrimaryBlue // Set your desired indicator color here
                    )
                }
            ) {
                tabItems.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        unselectedContentColor = GrayLight01,
                        selectedContentColor = PrimaryBlue,
                        modifier = Modifier.background(color = Color.White)
                    )
                }
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(color = GrayLight02)
            )

            HorizontalPager(
                state = pagerState,
                count = tabItems.size
            ) { page ->
                // Content for each page
                when (page) {
                    0 -> QuizPerformanceTable(viewModel)
                    1 -> QuizVerificationTable(viewModel)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun TopBarFilterUi(
    scope: CoroutineScope = rememberCoroutineScope(),
    bottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
    viewModel: ParentViewModel = hiltViewModel()
) {
    var selectedDateTab by remember { mutableStateOf(true) }
    var selectedTransferTypeTab by remember { mutableStateOf(false) }
    var isDialogVisible by remember { mutableStateOf(false) }

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = "Loading your data..."
    )
    val dateList = remember { mutableStateListOf<String>() }
    // State to hold the selected subject and month
    var selectedSubject by remember { mutableStateOf<GradeWiseSubjectResponseModel.Data?>(null) }
    var selectedMonth by remember { mutableStateOf<String?>(null) }

    // Collect selected items
    val selectedItems = listOfNotNull(selectedSubject?.subjectName, selectedMonth)

    LaunchedEffect(Unit) {

        viewModel.grade.value?.let { viewModel.getGradeWiseSubject(it) }

    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {



            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(
                    text = "Filter",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 18.sp
                )

            }
            Image(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier.clickable {
                    selectedSubject = null
                    selectedMonth = null
                    scope.launch {
                        bottomSheetState.hide()
                    }
                }.background(Color.Unspecified))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White), Alignment.BottomCenter
        ) {

            var subjectList = remember {
                mutableStateListOf<GradeWiseSubjectResponseModel.Data>()
            }

            LaunchedEffect(Unit) {
                viewModel.gradeWiseSubjectResponseModel.observeForever { it ->
                    when (it) {
                        is NetworkStatus.Idle -> {}
                        is NetworkStatus.Loading -> {}
                        is NetworkStatus.Success -> {
                            subjectList.clear()
                            it.data?.data.let {
                                if (it != null) {
                                    subjectList.addAll(it)
                                }
                            }
                        }

                        is NetworkStatus.Error -> {}
                        else -> {}
                    }
                }

                viewModel.getMonthYearResponseModelLiveData.observeForever {
                    when (it) {
                        is NetworkStatus.Idle -> {}
                        is NetworkStatus.Loading -> {
                            isDialogVisible = true
                        }

                        is NetworkStatus.Success -> {
                            isDialogVisible = false
                            dateList.clear()
                            it.data?.let { it1 -> dateList.addAll(it1.data) }

                        }

                        is NetworkStatus.Error -> {
                            isDialogVisible = false
                        }
                    }
                }

            }


            Row(modifier = Modifier.fillMaxSize()) {

                Column(
                    modifier = Modifier
                        .background(Color(0xFFF6F7F9)) // Light gray background
                        .width(120.dp)
                        .height(350.dp)
                )
                {
                  LeftMenuItem(
                        "Subject",
                        selectedDateTab,
                        onClick = {
                            if (!selectedDateTab) {
                                selectedDateTab = true
                                selectedTransferTypeTab = false

                            }
                        })

                   LeftMenuItem(
                        "Months",
                        selectedTransferTypeTab,
                        onClick = {
                            if (!selectedTransferTypeTab) {
                                selectedTransferTypeTab = true
                                selectedDateTab = false

                            }
                        })


                }



                if (selectedDateTab == true && selectedTransferTypeTab == false) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                    {
                        androidx.compose.material3.Text(
                            "Select Subject",
                            color = Color.Gray,
                            fontFamily = FontFamily(
                                Font(R.font.inter_regular, FontWeight.Normal)
                            ),
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            item {
                                Text(text = "Subject", modifier = Modifier.fillMaxWidth())
                            }
                            // Start Date Input
                            items(subjectList) { subject ->
                                FilterItemRow(
                                    item = subject.subjectName,
                                    isSelected = selectedSubject == subject,
                                    onClick = {
                                        selectedSubject = subject
                                        viewModel.getQuizPerformanceTable(
                                            subjectId = subject.subjectId,
                                            date = viewModel.date.value.toString(),
                                            userId = viewModel.userId.value!!.toInt(),
                                            conceptname = "",
                                            languageId = 1
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                // Right Content for transfer type
                if (selectedTransferTypeTab == true && selectedDateTab == false) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "Select Month",
                            color = GrayLight01,
                            fontFamily = FontFamily(
                                Font(R.font.inter_regular, FontWeight.Normal)
                            ),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            item {
                                Text(text = "Months", modifier = Modifier.fillMaxWidth())
                            }
                            items(dateList) { month ->
                                FilterItemRow(
                                    item = month,
                                    isSelected = selectedMonth == month,
                                    onClick = {
                                        selectedMonth = month

                                        viewModel.getQuizVerificationTable(
                                            subjectId = (selectedSubject?.subjectId?.toString()
                                                ?: viewModel.selectedSubjectId).toString(),
                                            date = month,
                                            userId = viewModel.userId.value.toString()
                                        )


                                    }
                                )
                            }
                        }
                    }
                }


                //old ui

//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .fillMaxHeight()
//                        .padding(horizontal = 10.dp)
//                        .background(color = Color.White)
//                ) {
//
//                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
//                        item {
//                            Text(text = "Subject", modifier = Modifier.fillMaxWidth())
//                        }
//                        items(subjectList) { subject ->
//                            FilterItemRow(
//                                item = subject.subjectName,
//                                isSelected = selectedSubject == subject,
//                                onClick = {
//                                    selectedSubject = subject
//                                    viewModel.getQuizPerformanceTable(
//                                        subjectId = 0, //subject.subjectId.toString(),
//                                        date = "202502" ,//iewModel.date.value.toString(),
//                                        userId = 1905006, //iewModel.userId.value.toString(),
//                                        conceptname = "",
//                                        languageId = 1
//                                    )
//                                }
//                            )
//                        }
//                        item {
//                            Text(text = "Months", modifier = Modifier.fillMaxWidth())
//                        }
//                        items(dateList) { month ->
//                            FilterItemRow(
//                                item = month,
//                                isSelected = selectedMonth == month,
//                                onClick = {
//                                    selectedMonth = month
//
//                                    viewModel.getQuizVerificationTable(
//                                        subjectId = (selectedSubject?.subjectId?.toString() ?: viewModel.selectedSubjectId).toString(),
//                                        date = month,
//                                        userId = viewModel.userId.value.toString()
//                                    )
//
//
//                                }
//                            )
//                        }
//                    }
//                }

            }
            Card(
                elevation = 10.dp, modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {

              // new ui
                Divider(color = GrayLight02, thickness = 1.dp)

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, top = 4.dp)
                ) {
                    val currentDate = remember { LocalDate.now() }
                    val dateBefore15Days = remember { currentDate.minusDays(30) }
                    val startDates = remember {
                        dateBefore15Days.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    }

                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    val currentDateAndTime = sdf.format(Date())
                    val endDates = currentDateAndTime.toString()

                    androidx.compose.material3.Button(
                        onClick = {

                        },
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "Clear All",
                            color = PrimaryBlue,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                            )
                        )
                    }

                    androidx.compose.material3.Button(
                        onClick = {

                        },
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .width(150.dp)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(PrimaryBlue),
                        shape = RoundedCornerShape(8.dp)
                    )
                    {
                        androidx.compose.material3.Text(
                            text = "Apply",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                            )
                        )
                    }
                }



              // old ui
//                Row(modifier = Modifier.fillMaxWidth()) {
//                    Spacer(modifier = Modifier.weight(1f))
//                    BtnNextUi(title = "Show Result ", onClick = {
//                        scope.launch {
//                            bottomSheetState.hide()
//                        }
//                    })
//                }

            }
        }
    }
}

@Composable
private fun FilterItemRow(item: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleCheckbox(
            selected = isSelected,
            enabled = true, // or false based on your logic
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = item,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            fontSize = 18.sp
        )
    }
}


@Preview
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Topbar(
    scope: CoroutineScope = rememberCoroutineScope(),
    bottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(
                text = "Filter",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 18.sp
            )

        }
        Image(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = null,
            modifier = Modifier.clickable {
                scope.launch {
                    bottomSheetState.hide()
                }
            }.background(Color.Unspecified))
    }
}

data class QuizVerificationModel(
    val name: String,
    val quizId: String,
    val score: String,
    val stateUs: String
)
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun QuizPerformanceTable(viewModel: ParentViewModel = hiltViewModel()) {

    val userId = viewModel.userId.collectAsState().value
    val selectedSubjectId = viewModel.selectedSubjectId.collectAsState().value
    val date = viewModel.date.collectAsState().value

    Log.e(
        "TAG",
        "QuizReportScreen: -----> " + userId + " subject id->" + selectedSubjectId + "date ->" + date
    )
    val quizReport = remember { mutableStateListOf<GetQuizReportResposeModel.Data>() }
    var isDialogVisible by remember { mutableStateOf(false) }

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = "Loading your data..."
    )
    viewModel.getQuizPerformanceTable(
        subjectId = 0, //subject.subjectId.toString(),
        date = "202502" ,//iewModel.date.value.toString(),
        userId = 1905006, //iewModel.userId.value.toString(),
        conceptname = "",
        languageId = 1
    )

    LaunchedEffect(Unit) {
        viewModel.getQuizReportResponseModelLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {
                    isDialogVisible = false
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    quizReport.clear()
                    it.data?.let { it1 -> quizReport.addAll(it1.data) }
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }

                else -> {}
            }
        }


    }

    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        SearchBar(searchText)
        LazyColumn {
            items(quizReport) { item ->
                conceptDetails(item)
            }
        }
    }

}

@Composable
fun conceptDetails(item: GetQuizReportResposeModel.Data) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .border(
                width = 1.dp,
                color = GrayLight02,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            text = item.concept,
            fontSize = 14.sp, // Use 'sp' for font size
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp)
        )

        Row(
            modifier = Modifier
                .wrapContentHeight()
                .padding(vertical = 10.dp, horizontal = 15.dp)
                .border(
                    width = 1.dp,
                    color = GrayLight02,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(color = LightYellow02)
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Highest Score - " + item.maxScore,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 10.dp, end = 5.dp, top = 10.dp, bottom = 10.dp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = GrayLight01,
                textAlign = TextAlign.Center
            )
            Text(text = "•", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Orange)
            Text(
                text = "Topic Improvement - " + item.improvement,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(end = 10.dp, top = 10.dp, bottom = 10.dp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = GrayLight01,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchBar(searchText: String) {
    var searchText1 = searchText
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 5.dp)
            .border(
                width = 1.dp,
                color = GrayLight02,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .background(color = Color.White)
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Search Icon",
                modifier = Modifier
                    .background(color = Color.White)
                    .padding()
                    .padding(10.dp)
            )


            OutlinedTextField(
                value = searchText1,
                onValueChange = { searchText1 = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
                    .background(color = Color.White),
                placeholder = {
                    Text(
                        "Concept name ",
                        color = Color.Gray
                    )
                },
            )

//            TextField(
//                value = searchText1,
//                onValueChange = { newText ->
//                    searchText1 = newText
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(color = Color.White), // Set background color to white
//                placeholder = {
//                    Text(
//                        "Concept name ",
//                        color = Color.Gray
//                    )
//                }, // Placeholder text color
//                colors = TextFieldDefaults.textFieldColors(
//                    containerColor = Color.White,
//                    cursorColor = Black,
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent
//                ),
//                singleLine = true
//            )
        }
    }
}

@Preview
@Composable
fun QuizVerificationTable(viewModel: ParentViewModel = hiltViewModel()) {
    val userId = viewModel.userId.collectAsState().value
    val selectedSubjectId = viewModel.selectedSubjectId.collectAsState().value
    val date = viewModel.date.collectAsState().value
    Log.e(
        "TAG",
        "QuizReportScreen: -----> " + userId + " subject id->" + selectedSubjectId + "date ->" + date
    )
//    /reports/quiz-verification

    var data = remember { mutableStateListOf<GetQuizVerificationTableResponseModel.Data>() }

    var isDialogVisible by remember { mutableStateOf(false) }

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = "Loading your data..."
    )
    LaunchedEffect(Unit) {
        viewModel.getQuizVerificationTableResponseModelLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {
                    isDialogVisible = false
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    data.clear()
                    it.data?.let { it1 -> data.addAll(it1.data) }
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
                else -> {}
            }
        }
        viewModel.getQuizVerificationTable(
            subjectId = selectedSubjectId.toString(),
            date = date.toString(),
            userId = userId.toString()
        )
    }


    var searchText by remember { mutableStateOf("") }

    var subjectList = remember {
        mutableStateListOf<QuizVerificationModel>()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        SearchBar(searchText)

        subjectList.add(
            QuizVerificationModel(
                name = "Bird Talk (poem) and Nina And The Baby Sparrows (story) Bird ",
                quizId = "",
                score = "",
                stateUs = ""
            )
        )

        LazyColumn {
            items(data) { item ->
                QuizVerificationItem(item)
            }
        }
    }
}

@Composable
fun QuizVerificationItem(item: GetQuizVerificationTableResponseModel.Data) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .border(
                width = 1.dp,
                color = GrayLight02,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            text = item.concept,
            fontSize = 14.sp, // Use 'sp' for font size
            fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, top = 10.dp)
        )

        Row(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 15.dp)
                .border(
                    width = 1.dp,
                    color = GrayLight02,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(color = LightYellow02)
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            TextSplitColor("Quiz - " + item.concept)
            Text(text = " • ", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Orange)
            TextSplitColor("Score - " + item.score)

        }
        status(item, modifier = Modifier.padding(bottom = 10.dp, start = 10.dp))
    }
}

@Composable
fun status(item: GetQuizVerificationTableResponseModel.Data, modifier: Modifier) {
    when (item.status) {
        "approve".toUpperCase() -> {
            TextWithIconOnLeft(
                item.status.toUpperCase(),
                icon = ImageVector.vectorResource(id = R.drawable.checked),
                textColor = Color.White,
                iconColor = Color.White,
                modifier = modifier
                    .padding(horizontal = 5.dp)
                    .background(color = GreenDark02, shape = RoundedCornerShape(10.dp))
                    .padding(5.dp),
                onClick = {

                }
            )
        }

        "disapproved".toUpperCase() -> {
            TextWithIconOnLeft(
                item.status.toUpperCase(),
                icon = ImageVector.vectorResource(id = R.drawable.ic_cancel_v1),
                textColor = Color.White,
                iconColor = Color.White,
                modifier = modifier
                    .padding(horizontal = 5.dp)
                    .background(color = LightRed01, shape = RoundedCornerShape(10.dp))
                    .padding(5.dp),
                onClick = {

                }
            )
        }

        else -> {
            TextWithIconOnLeft(
                item.status.toUpperCase(),
                icon = ImageVector.vectorResource(id = R.drawable.in_progress),
                textColor = Color.White,
                iconColor = Color.White,
                modifier = modifier
                    .padding(horizontal = 5.dp)
                    .background(color = Orange, shape = RoundedCornerShape(10.dp))
                    .padding(5.dp),
                onClick = {

                }
            )
        }
    }
}

@Composable
fun TextSplitColor(quizText: String = "Quiz - 123") {
    // Split the input string into parts
    val parts = quizText.split(" - ")

    // Ensure the input is in the expected format
    if (parts.size == 2) {
        val quizPart = parts[0] + " - "
        val scorePart = parts[1]
        // Create an AnnotatedString with different styles
        val styledText = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = GrayLight01,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Normal,
                    fontSize = 12.sp
                )
            ) {
                append(quizPart)
            }
            withStyle(
                style = SpanStyle(
                    color = Black,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Normal,
                    fontSize = 12.sp
                )
            ) {
                append(scorePart)
            }
        }
        Text(
            text = styledText,
            modifier = Modifier
                .wrapContentWidth()
                .padding(start = 5.dp, end = 5.dp, top = 10.dp, bottom = 10.dp),
            textAlign = TextAlign.Center
        )
    } else {
        // Handle invalid input format
        Text(
            text = quizText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 5.dp, top = 10.dp, bottom = 10.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = Black,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun LeftMenuItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    androidx.compose.material3.Text(text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(start = 12.dp, top = 8.dp, bottom = 8.dp)
            .background(if (isSelected) Color.White else Color.Transparent),
        color = if (isSelected) Color(0xFF008CFF) else Color.Gray,
        fontSize = 14.sp)
}
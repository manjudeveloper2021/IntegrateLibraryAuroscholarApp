package com.auro.application.ui.features.student.wallet.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.auro.application.core.extions.toast
import com.auro.application.data.api.NetworkStatus
import com.auro.application.ui.common_ui.Btn12PXUi
import com.auro.application.ui.common_ui.BtnTextUi
import com.auro.application.ui.common_ui.components.YearMonthWalletGridPicker
import com.auro.application.ui.features.login.componets.StudentRegisterWalletBackground
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.features.student.wallet.Models.QuizStatusDetailsResponse
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.GreenDark02
import com.auro.application.ui.theme.LightGreen03
import com.auro.application.ui.theme.LightOrange
import com.auro.application.ui.theme.Orange
import com.auro.application.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun ScholarshipDisbursedScreen(
    navHostController: NavHostController = rememberNavController(),
    args: String? = "Scholarship Disbursed"
) {

    val context = LocalContext.current
    var strUserId: String = ""
    val viewModelLogin: LoginViewModel = hiltViewModel()
    val viewModel: StudentViewModel = hiltViewModel()
    var quizQuizStatusDataList by remember { mutableStateOf(mutableListOf<QuizStatusDetailsResponse.QuizStatusData>()) }

    strUserId = viewModelLogin.getUserId().toString()

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModelLogin.getLanguageTranslationData(languageListName)

    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })

    var dateMonth by remember { mutableStateOf("") }
    val sdf = SimpleDateFormat("yyyyMM")
    val currentDateAndTime = sdf.format(Date())
    dateMonth = currentDateAndTime.toString()
    var dateValueQ by remember { mutableStateOf("") }

    dateValueQ = BottomSheetMonthYearOfDisbursedDialog(isBottomSheetVisible = isBottomSheetVisible,
        sheetState = sheetState,
        filterValue = dateValueQ,
        onDismiss = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                isBottomSheetVisible = false
            }
        })

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.quizQuizStatusDetailsResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        quizQuizStatusDataList =
                            it.data.data as MutableList<QuizStatusDetailsResponse.QuizStatusData>
                        println("Quiz status reports data :- $quizQuizStatusDataList")
                    }
                }

                is NetworkStatus.Error -> {
                    context.toast(it.message)
                    println("Error Data :- ${it.message}")
                }
            }
        }

        if (dateMonth.isNotBlank()) {
            viewModel.getQuizStatusDetails(strUserId, currentDateAndTime.toString())
        }

//        viewModel.getQuizStatusDetails(strUserId, dateMonth)
    }

    StudentRegisterWalletBackground(isShowBackButton = true,
        isShowFullTopBarMenu = false,
        title = args.toString(),
        onBackButtonClick = {
            (context as ComponentActivity).finish()
        },
        content = {
//            val sdfStatus = SimpleDateFormat("MMM yyyy")
//            val currentDateAndTimeStatus = sdfStatus.format(Date())
//            val dateMonthStatus = currentDateAndTimeStatus.toString()
//            println("Month and year :- $dateMonthStatus")
            val pendingTasksCount =
                quizQuizStatusDataList.count { it.amountDescription == "DISBURSED" }
//            topQuizzCountWithDateFilter(pendingTasksCount, dateMonthStatus)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$pendingTasksCount Quizzes ", fontSize = 12.sp, fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    )
                )

                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.padding(end = 5.dp)) {
                    if (dateValueQ.equals("")) {
                        val sdf = SimpleDateFormat("MMM yyyy")
                        val currentDateAndTime = sdf.format(Date())
                        dateMonth = currentDateAndTime.toString()
                    } else {
                        dateValueQ = FormatMonthYear(dateValueQ)
                        dateMonth = dateValueQ
                    }

                    Text(text = " $dateMonth", fontSize = 12.sp, fontFamily = FontFamily(
                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                    ), modifier = Modifier.clickable {
                        scope.launch {
                            isBottomSheetVisible = true
                            sheetState.expand()
                        }
                    })

                    Icon(
                        painter = painterResource(id = R.drawable.ic_down),
                        contentDescription = "asas",
                        tint = GrayLight01,
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.Unspecified)
                    )
                }
            }

            LazyColumn {
                val statusReviews = quizQuizStatusDataList
                items(items = statusReviews) { quizQuizStatusDataList ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Handle click event here
                            // You can access the item's position using the 'statusReviews.indexOf(item)'
                            val position = statusReviews.indexOf(quizQuizStatusDataList)
                        }) {
                        if (quizQuizStatusDataList.amountDescription!!.contains("DISBURSED")) {
                            ScholarShipDisbursedStatusCard(
                                languageData = languageData,
                                date = quizQuizStatusDataList.transactionDate.toString(),
                                amount = quizQuizStatusDataList.amount.toString(),
                                QuizId = quizQuizStatusDataList.id.toString(),
                                subjectName = quizQuizStatusDataList.subject.toString(),
                                concept = quizQuizStatusDataList.concept.toString()
                            )
                        } else {
//                            println("Amount description is disbursed")
                        }
                    }
                }
            }
        })
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat")
@Composable
fun ScholarShipDisbursedStatusCard(
    languageData: HashMap<String, String>,
    date: String,
    amount: String,
    QuizId: String,
    subjectName: String,
    concept: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
            .border(
                1.dp,
                GrayLight02,
                shape = RoundedCornerShape(20.dp),
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp)) {
            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {

                val strDate: String = getCurDate(date)

                Text(
                    text = strDate, color = Orange, fontSize = 12.sp, fontFamily = FontFamily(
                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                    ), modifier = Modifier.padding(start = 0.dp)
                )
                Text(
                    text = "Quiz ID: $QuizId",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 0.dp, top = 10.dp),
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    )
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(50.dp)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(LightGreen03)
                    .border(
                        1.dp,
                        LightGreen03,
                        shape = RoundedCornerShape(20.dp),
                    )
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(10.dp),
                    text = "₹ $amount",
                    color = GreenDark02,
                    fontFamily = FontFamily(
                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                    ),
                    fontSize = 12.sp
                )
            }
        }
        Text(
            text = subjectName, fontFamily = FontFamily(
                Font(R.font.inter_bold, FontWeight.Bold)
            ), fontSize = 14.sp, modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )
        Text(
            text = concept,
            fontFamily = FontFamily(
                Font(R.font.inter_medium, FontWeight.Medium)
            ),
            fontSize = 14.sp,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))
    }

}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurDate(date: String): String {
    val isoDateString = date
    val instant = Instant.parse(isoDateString)
    val formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy").withZone(ZoneId.systemDefault())
    val formattedDate = formatter.format(instant)
    return formattedDate
}

@SuppressLint("SimpleDateFormat")
@Composable
fun topQuizzCountWithDateFilter(size: Int, dateMonth: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$size Quizzes ", fontSize = 12.sp, fontFamily = FontFamily(
                Font(R.font.inter_medium, FontWeight.Medium)
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.padding(end = 5.dp)) {
            Text(text = " $dateMonth", fontSize = 12.sp, fontFamily = FontFamily(
                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
            ), modifier = Modifier.clickable {
//                    dateFilter.invoke()
            })
            Icon(
                painter = painterResource(id = R.drawable.ic_down),
                contentDescription = "asas",
                tint = GrayLight01,
                modifier = Modifier
                    .size(20.dp)
                    .background(Color.Unspecified)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetMonthYearOfDisbursedDialog(
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    filterValue: String,
    onDismiss: () -> Unit
): String {

    val context: Context = LocalContext.current
    val viewModel: LoginViewModel = hiltViewModel()
    val studentViewModel: StudentViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    var selectedYear by remember { mutableStateOf(Year.now().value) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now().monthValue) }
    var selectedValue by remember { mutableStateOf(filterValue) }
    val strUserId = viewModel.getUserId().toString()

    if (isBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                onDismiss()
            },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 15.dp, end = 15.dp, bottom = 20.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = stringResource(id = R.string.txt_select_month_year),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start
                            )
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(top = 10.dp, bottom = 20.dp)
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

                    // calender
                    YearMonthWalletGridPicker(
                        selectedYear = selectedYear, selectedMonth = selectedMonth
                    ) { year, month ->
                        selectedYear = year
                        selectedMonth = month
                        if (selectedMonth < 10) {
                            println("Selected Year: $selectedYear, Month: 0$selectedMonth")
                            selectedValue = "$selectedYear" + "0$selectedMonth"
                        } else {
                            selectedValue = "$selectedYear$selectedMonth"
                            println("Selected Year: $selectedYear, Month: $selectedMonth")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(top = 10.dp)
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
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BtnTextUi(stringResource(id = R.string.txt_cancel),
                            color = PrimaryBlue,
                            onClick = {
                                onDismiss()
                                val sdf = SimpleDateFormat("yyyyMM")
                                val currentDateAndTime = sdf.format(Date())
                                studentViewModel.getQuizStatusDetails(
                                    strUserId,
                                    currentDateAndTime.toString()
                                )
                            })

                        Btn12PXUi(title = stringResource(R.string.txt_apply_result), onClick = {
                            onDismiss()
                            studentViewModel.getQuizStatusDetails(strUserId, selectedValue)
//                            println("Selected month and year :- $selectedValue")
                        }, false)
                    }
                }
            }
        }
    }

    return selectedValue
}
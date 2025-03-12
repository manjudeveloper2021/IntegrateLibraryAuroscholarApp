package com.auro.application.ui.features.student.wallet.screens

import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.ui.common_ui.TextFieldWithIcon
import com.auro.application.ui.common_ui.showDatePickerDialog
import com.auro.application.ui.common_ui.showWalletDatePickerDialog
import com.auro.application.ui.features.login.componets.StudentRegisterWalletHistoryBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.features.student.wallet.Models.TransactionHistoryResponse.TransactionData
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun WalletHistoryScreenPreview(
    navController: NavHostController = rememberNavController(), status: String? = "History"
) {
    // State to control the visibility of the OpenDateRange bottom sheet
    var showDateRangePicker by remember { mutableStateOf(false) }
    var showFilter by remember { mutableStateOf(false) }
    val context: Context = LocalContext.current

    val viewModel: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState =
        androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true,
            confirmValueChange = { it != SheetValue.Hidden })

    FilterDialogScreen(isBottomSheetVisible = isBottomSheetVisible,
        sheetState = sheetState,
        onDismiss = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                isBottomSheetVisible = false
            }
        })

    StudentRegisterWalletHistoryBackground(isShowBackButton = true,
        isShowFullTopBarMenu = true,
        title = status.toString(),
        onBackButtonClick = {
            (context as ComponentActivity).finish()
        },

        onFilterClick = {
            showFilter = true
            showDateRangePicker = true

        },

        content = {
            // Show the OpenDateRange bottom sheet when showDateRangePicker is true
            if (showDateRangePicker) {
                scope.launch {
                    isBottomSheetVisible = true
                    showDateRangePicker = false
                    sheetState.expand()
                }

//                FilterDialog()
//                OpenDateRange(
//                    languageData = languageData, onDismiss = {
//                        showDateRangePicker = false
//                        showFilter = false
//                    }, showFilter
//                )
            }

            if (showDateRangePicker == true || showDateRangePicker == false || showFilter == true || showFilter == false) {  // very import making refreshable UI
                PagerWithTabs("", languageData)
            }
        })
}

/*@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun OpenDateRange(
    languageData: HashMap<String, String>, onDismiss: () -> Unit, showFilter: Boolean
) {
    val state = rememberDateRangePickerState()
    var strType: String = ""
    val viewModel: StudentViewModel = hiltViewModel()
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden,
            confirmStateChange = { targetValue ->
                if (targetValue == ModalBottomSheetValue.Hidden) {
                    onDismiss() // Call the onDismiss callback when the bottom sheet is hidden
                    true // Allow the state change
                } else {
                    true // Allow other state changes
                }
            })

    val coroutineScope = rememberCoroutineScope()

    val currentDate = remember { LocalDate.now() }
    val dateBefore15Days = remember { currentDate.minusDays(30) }
    val startDate = remember {
        dateBefore15Days.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val currentDateAndTime = sdf.format(Date())
    val endDate = currentDateAndTime.toString()

    println("Start date :- $startDate, end date :- $endDate")

    // Show the bottom sheet when the composable is first launched
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            bottomSheetState.show()
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .background(Color.White)
            ) {
                if (showFilter) {
                    var selectedOption by remember { mutableStateOf<String?>(null) }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    ) {
                        Row {
                            Text(text = "Filter", color = Color.Black)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = "Clear All",
                                color = PrimaryBlue,
                                modifier = Modifier.clickable {
                                    selectedOption = null
                                    onDismiss()
                                })
                        }
                        Text(text = "Select Transaction Type", color = GrayLight01)
                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "UPI", color = Color.Black)

                            Box(
                                modifier = Modifier
                                    .size(24.dp) // Size of the circular checkbox
                                    .border(2.dp, PrimaryBlue, CircleShape) // Border for the circle
                                    .background(
                                        if (selectedOption == "UPI") PrimaryBlue else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable {
                                        selectedOption =
                                            if (selectedOption == "UPI") null else "UPI"
                                    }, contentAlignment = Alignment.Center
                            ) {
                                if (selectedOption == "UPI") {
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
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Bank", color = Color.Black)

                            Box(
                                modifier = Modifier
                                    .size(24.dp) // Size of the circular checkbox
                                    .border(2.dp, PrimaryBlue, CircleShape) // Border for the circle
                                    .background(
                                        if (selectedOption == "Bank") PrimaryBlue else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable {
                                        selectedOption =
                                            if (selectedOption == "Bank") null else "Bank"
                                    }, contentAlignment = Alignment.Center
                            ) {
                                if (selectedOption == "Bank") {
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
                                .padding(start = 32.dp, top = 32.dp, end = 32.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (languageData[LanguageTranslationsResponse.KEY_CANCEL].toString() == "") {
                                "Cancel"
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_CANCEL].toString()
                            }, color = Color.White, modifier = Modifier
                                .background(
                                    color = PrimaryBlue, // Replace with your actual color
                                    shape = RoundedCornerShape(16.dp) // Adjust the corner radius as needed
                                )
                                .padding(8.dp)
                                .clickable {
                                    onDismiss()
                                })

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
//                                text = "Show Results (50) ",
                                text = "Apply", color = Color.White, modifier = Modifier
                                    .background(
                                        color = PrimaryBlue, // Replace with your actual color
                                        shape = RoundedCornerShape(16.dp) // Adjust the corner radius as needed
                                    )
                                    .padding(8.dp)
                                    .clickable {
                                        if (!selectedOption.isNullOrEmpty()) {
                                            selectedOption = selectedOption.toString()
                                            coroutineScope.launch {
                                                bottomSheetState.hide()
                                                onDismiss() // Notify that the bottom sheet is dismissed
                                                strType = selectedOption.toString()
                                                println("Selected item is :- $strType")
//                                                PagerWithTabs(selectedTnsType = strType)
                                                viewModel.getTransactionHistory(
                                                    strType, "", startDate, endDate
                                                )
                                            }
                                        } else {
                                            selectedOption = null
                                        }
                                    })// Add padding for better appearance
                        }
                    }
                } else {
                    var selectedStartDate by remember { mutableStateOf("") }
                    var selectedEndDate by remember { mutableStateOf("") }
                    if (selectedStartDate == "") {
                        selectedStartDate = viewModel.getStartDate().toString()
                    }

                    if (selectedEndDate == "") {
                        selectedEndDate = viewModel.getEndDate().toString()
                    }

                    DateRangePickerSample(state, languageData)
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                bottomSheetState.hide()
                                onDismiss() // Notify that the bottom sheet is dismissed
                            }
                            viewModel.getTransactionHistory(
                                strType, "", selectedStartDate, selectedEndDate
                            )
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black, contentColor = Color.White
                        ), modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp)
                    ) {
                        Text("Done", color = Color.White)
                    }
                }
            }
        },
        content = {
            PagerWithTabs(strType, languageData)
        },
        scrimColor = Color.Black.copy(alpha = 0.5f),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    )
}*/

/*fun getFormattedDate(timeInMillis: Long): String {
    val calender = Calendar.getInstance()
    calender.timeInMillis = timeInMillis
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    return dateFormat.format(calender.timeInMillis)
}*/

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerSample(
    state: DateRangePickerState, languageData: HashMap<String, String>
) {
    val viewModel: StudentViewModel = hiltViewModel()
    DateRangePicker(state, modifier = Modifier, dateFormatter = object : DatePickerFormatter {
        override fun formatDate(
            dateMillis: Long?, locale: CalendarLocale, forContentDescription: Boolean
        ): String? {
            return SimpleDateFormat("yyyy MM dd", Locale.getDefault()).format(dateMillis)
        }

        override fun formatMonthYear(monthMillis: Long?, locale: CalendarLocale): String? {
            return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(monthMillis)
        }
    }, title = {
        Text(
            text = "Select date range to assign the chart", modifier = Modifier.padding(16.dp)
        )
    }, headline = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(Modifier.weight(1f)) {
                (if (state.selectedStartDateMillis != null) state.selectedStartDateMillis?.let {
                    getFormattedDate(
                        it
                    )
                } else "Start Date")?.let {
                    Text(text = it)
                    viewModel.setStartDate(it)
                    println("Selected start date :- $it")
                }
            }

            Box(Modifier.weight(1f)) {
                (if (state.selectedEndDateMillis != null) state.selectedEndDateMillis?.let {
                    getFormattedDate(
                        it
                    )
                } else "End Date")?.let {
                    Text(text = it)
                    viewModel.setEndDate(it)
                    println("Selected end date :- $it ")
                }
            }

            Box(Modifier.weight(0.2f)) {
                Icon(imageVector = Icons.Default.Done, contentDescription = "Okk")
            }
        }
    }, showModeToggle = true, colors = DatePickerDefaults.colors(
        containerColor = Color.Blue,
        titleContentColor = Color.Black,
        headlineContentColor = Color.Black,
        weekdayContentColor = Color.Black,
        subheadContentColor = Color.Black,
        yearContentColor = Color.Green,
        currentYearContentColor = Color.Red,
        selectedYearContainerColor = Color.Red,
        disabledDayContentColor = Color.Gray,
        todayDateBorderColor = Color.Blue,
        dayInSelectionRangeContainerColor = Color.LightGray,
        dayInSelectionRangeContentColor = Color.White,
        selectedDayContainerColor = Color.Black
    )
    )
}*/

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PagerWithTabs(selectedTnsType: String, languageData: HashMap<String, String>) {
    val tabTitles = listOf("Disbursed", "Inprocess")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabTitles.size })
    val coroutineScope = rememberCoroutineScope() // Create coroutine scope

    println("Selected Tns type :- $selectedTnsType")

    val context: Context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()

    var transactionDataList by remember { mutableStateOf(mutableListOf<TransactionData>()) }

    var isDialogVisible by remember { mutableStateOf(false) }
    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    val currentDate = remember { LocalDate.now() }
    val dateBefore15Days = remember { currentDate.minusDays(30) }
    val startDate = remember {
        dateBefore15Days.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val currentDateAndTime = sdf.format(Date())
    val endDate = currentDateAndTime.toString()

    println("Start date :- $startDate, end date :- $endDate")

    CustomDialog(
        isVisible = isDialogVisible, onDismiss = { isDialogVisible = false }, message = msgLoader
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.quizTransactionHistoryResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                    transactionDataList.clear()
                }

                is NetworkStatus.Success -> {
                    transactionDataList.clear()
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        transactionDataList = it.data.data as MutableList<TransactionData>
                        println("Transaction reports data :- $transactionDataList")
                    } else {
                        isDialogVisible = false
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    transactionDataList.clear()
                    println("Error Data :- ${it.message}")
                }
            }
        }

        viewModel.getTransactionHistory("", "", startDate, endDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        // Tab Row
        Disbursed(transactionDataList)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialogScreen(
    isBottomSheetVisible: Boolean, sheetState: SheetState, onDismiss: () -> Unit
) {
    val context: Context = LocalContext.current
    val viewModel: LoginViewModel = hiltViewModel()
    val viewModelStudent: StudentViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var selectedTransferType by remember { mutableStateOf<String?>("") }
    var selectedTransferStatus by remember { mutableStateOf<String?>("") }
    var transferStatus by remember { mutableStateOf<String?>("") }
    var selectedDateTab by remember { mutableStateOf(true) }
    var selectedTransferTypeTab by remember { mutableStateOf(false) }
    var selectedTransferStatusTab by remember { mutableStateOf(false) }

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
                                onDismiss.invoke()
                                startDate = ""
                                endDate = ""
                                selectedTransferType = ""
                                selectedTransferStatus = ""
                                transferStatus = ""
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
                            onDismiss.invoke()
                            startDate = ""
                            endDate = ""
                            selectedTransferType = ""
                            selectedTransferStatus = ""
                            transferStatus = ""
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
                                .width(120.dp)
                                .height(350.dp)
                        ) {
                            LeftMenuItem("Date Range", selectedDateTab, onClick = {
                                if (!selectedDateTab) {
                                    selectedDateTab = true
                                    selectedTransferTypeTab = false
                                    selectedTransferStatusTab = false
                                }
                            })

                            LeftMenuItem("Transfer Type", selectedTransferTypeTab, onClick = {
                                if (!selectedTransferTypeTab) {
                                    selectedTransferTypeTab = true
                                    selectedDateTab = false
                                    selectedTransferStatusTab = false
                                }
                            })

                            LeftMenuItem("Transfer Status", selectedTransferStatusTab, onClick = {
                                if (!selectedTransferStatusTab) {
                                    selectedTransferStatusTab = true
                                    selectedDateTab = false
                                    selectedTransferTypeTab = false
                                }
                            })
                        }

                        // Right Content for select date range
                        if (selectedDateTab == true && selectedTransferTypeTab == false && selectedTransferStatusTab == false) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    "Select Date Range",
                                    color = Color.Gray,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_regular, FontWeight.Normal)
                                    ),
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Start Date Input
                                TextFieldWithIcon(
                                    modifier = Modifier.clickable {
                                        var selectedMonth: String = ""
                                        var selectedDay: String = ""
                                        showWalletDatePickerDialog(context) { year, month, dayOfMonth ->
                                            selectedMonth = if (month < 10) {
                                                "0$month"
                                            } else {
                                                month.toString()
                                            }
                                            selectedDay = if (dayOfMonth < 10) {
                                                "0$dayOfMonth"
                                            } else {
                                                dayOfMonth.toString()
                                            }
                                            startDate = "$year-$selectedMonth-$selectedDay"
                                        }
                                    },
                                    value = remember { mutableStateOf(startDate) },
                                    placeholder = if (startDate.isEmpty()) "Start date" else startDate
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // End Date Input
                                TextFieldWithIcon(
                                    modifier = Modifier.clickable {
                                        var selectedMonth: String = ""
                                        var selectedDay: String = ""
                                        showWalletDatePickerDialog(context) { year, month, dayOfMonth ->
                                            selectedMonth = if (month < 10) {
                                                "0$month"
                                            } else {
                                                month.toString()
                                            }
                                            selectedDay = if (dayOfMonth < 10) {
                                                "0$dayOfMonth"
                                            } else {
                                                dayOfMonth.toString()
                                            }
                                            endDate = "$year-$selectedMonth-$selectedDay"
                                        }
                                    },
                                    value = remember { mutableStateOf(endDate) },
                                    placeholder = if (endDate.isEmpty()) "End date" else endDate
                                )
                            }
                        }

                        // Right Content for transfer type
                        if (selectedTransferTypeTab == true && selectedDateTab == false && selectedTransferStatusTab == false) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Select Transfer Type",
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
                                        text = "UPI", color = Color.Black, fontFamily = FontFamily(
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
                                                if (selectedTransferType == "UPI") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedTransferType =
                                                    if (selectedTransferType == "UPI") null else "UPI"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedTransferType == "UPI") {
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
                                        text = "Bank", color = Color.Black, fontFamily = FontFamily(
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
                                                if (selectedTransferType == "Bank") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedTransferType =
                                                    if (selectedTransferType == "Bank") null else "Bank"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedTransferType == "Bank") {
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

                        // Right Content for transfer status
                        if (selectedTransferStatusTab == true && selectedDateTab == false && selectedTransferTypeTab == false) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Select Transfer Status",
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
                                        text = "Disbursed",
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
                                                if (selectedTransferStatus == "Disbursed") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedTransferStatus =
                                                    if (selectedTransferStatus == "Disbursed") null else "Disbursed"
                                                transferStatus = "S"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedTransferStatus == "Disbursed") {
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
                                                if (selectedTransferStatus == "Inprocess") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedTransferStatus =
                                                    if (selectedTransferStatus == "Inprocess") null else "Inprocess"
                                                transferStatus = "P"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedTransferStatus == "Inprocess") {
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
                                        text = "Failed",
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
                                                if (selectedTransferStatus == "Failed") PrimaryBlue else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable {
                                                selectedTransferStatus =
                                                    if (selectedTransferStatus == "Failed") null else "Failed"
                                                transferStatus = "F"
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedTransferStatus == "Failed") {
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
                        val currentDate = remember { LocalDate.now() }
                        val dateBefore15Days = remember { currentDate.minusDays(30) }
                        val startDates = remember {
                            dateBefore15Days.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        }

                        val sdf = SimpleDateFormat("yyyy-MM-dd")
                        val currentDateAndTime = sdf.format(Date())
                        val endDates = currentDateAndTime.toString()

                        Button(
                            onClick = {
                                onDismiss()
                                startDate = ""
                                endDate = ""
                                selectedTransferType = ""
                                selectedTransferStatus = ""
                                transferStatus = ""

                                viewModelStudent.getTransactionHistory("", "", startDates, endDates)

                                selectedDateTab = true
                                selectedTransferTypeTab = false
                                selectedTransferStatusTab = false
                            },
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Clear All",
                                color = PrimaryBlue,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(
                                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                )
                            )
                        }

                        Button(
                            onClick = {
                                onDismiss()
                                println("Selected date range from $startDate to $endDate, and transfer type :- $selectedTransferType, and status :- $transferStatus")
                                viewModelStudent.getTransactionHistory(
                                    selectedTransferType.toString(),
                                    transferStatus.toString(), startDate, endDate
                                )
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
fun LeftMenuItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(text = text,
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
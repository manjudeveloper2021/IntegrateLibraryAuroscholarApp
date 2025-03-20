package com.auro.application.ui.features.student.wallet.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.ConstantVariables.isKYC
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.ui.common_ui.Btn12PXUi
import com.auro.application.ui.common_ui.BtnNextUi
import com.auro.application.ui.common_ui.BtnTextUi
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.components.YearMonthGridPicker
import com.auro.application.ui.common_ui.components.YearMonthWalletGridPicker
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.CommandAppKey
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.authentication.StudentAuthenticationActivity
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.features.student.wallet.Models.QuizWinningData
import com.auro.application.ui.features.student.wallet.Models.WinningAmountData
import com.auro.application.ui.features.student.wallet.WalletActivity
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.Transparent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Year
import java.time.YearMonth
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat")
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentWalletScreen(
    navHostController: NavHostController = rememberNavController(), openMenu: () -> Unit = {}
) {
//    val context = LocalContext.current
    val context: Context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()
    val viewModelLogin: LoginViewModel = hiltViewModel()
    var withdrawDialog by remember { mutableStateOf(false) }
    var strUserId: String = ""
    var winningAmountDataList by remember { mutableStateOf(mutableListOf<WinningAmountData.WinningWallet>()) }
    var winningAmountData by remember { mutableStateOf<WinningAmountData?>(null) }

    var quizWinningDataList by remember { mutableStateOf(mutableListOf<QuizWinningData.WinningStatus>()) }
    var quizWinningData by remember { mutableStateOf<QuizWinningData?>(null) }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModelLogin.getLanguageTranslationData(languageListName)

    strUserId = viewModelLogin.getUserId().toString()

    var isDialogVisible by remember { mutableStateOf(false) }
    var kycStatus by remember { mutableStateOf("") }
    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })
    var dateMonth by remember { mutableStateOf("") }
    val sdf = SimpleDateFormat("yyyyMM")
    val currentDateAndTime = sdf.format(Date())
    dateMonth = currentDateAndTime.toString()
//    println("Selected date with year :- $dateMonth")
    var dateValueQ by remember { mutableStateOf("") }
    val debouncedText = rememberDebounce(dateValueQ, 2000L)

    dateValueQ = BottomSheetMonthYearOfWalletDialog(isBottomSheetVisible = isBottomSheetVisible,
        sheetState = sheetState,
        filterValue = dateValueQ,
        onDismiss = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                isBottomSheetVisible = false
            }
        })

    CustomDialog(
        isVisible = isDialogVisible, onDismiss = { isDialogVisible = false }, message = msgLoader
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.walletWinningAmountResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        winningAmountData = it.data.data
                        winningAmountDataList =
                            it.data.data?.wallet as MutableList<WinningAmountData.WinningWallet>
                        println("Winning amount reports data :- $winningAmountDataList")

                        viewModel.setWalletInfoData(it.data.data!!)

                        winningAmountDataList.forEach { winningWalletId ->
                            val strWalletId: String = winningWalletId.id.toString()
                            println("Your wallet Id is :- $strWalletId")
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                    println("Error Data :- ${it.message}")
                }
            }
        }

        viewModel.quizWinningStatusResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                    quizWinningDataList.clear()
                }

                is NetworkStatus.Success -> {
                    quizWinningDataList.clear()
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        quizWinningData = it.data.data
                        quizWinningDataList =
                            it.data.data?.winningStatus as MutableList<QuizWinningData.WinningStatus>
                        println("Quiz Winning reports data :- $quizWinningDataList")
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    quizWinningDataList.clear()
                    context.toast(it.message)
                    println("Error Data :- ${it.message}")
                }
            }
        }

        viewModel.paymentCheckConfigResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        withdrawDialog = true
                        println("Payment check config reports data :- ${it.data.data}")
                    } else {
                        isDialogVisible = false
                        println("Payment check config reports data :- ${it.data?.error}")
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    withdrawDialog = false
                    context.toast(it.message)
                    println("Error Data :- ${it.message}")
                }
            }
        }

        // API for KYC response
        viewModel.getKycAadhaarStatusLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {

                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        viewModelLogin.saveKycStatusData(it.data.data)
                        kycStatus = it.data.data.studentKycStatus
                        println("Kyc Approved or not :- $kycStatus")
                    }
                }

                is NetworkStatus.Error -> {
                    println("Kyc Aadhaar Status Live Data Error :-${it.message}")
                }
            }
        }

        viewModel.getKycAadhaarStatus(strUserId.toInt())
        viewModel.getWalletWinningAmount(strUserId)
        if (dateMonth.isNotBlank()) {
            viewModel.getWalletQuizWinningStatus(strUserId, dateMonth)
        } else {
//            println("Selected date Q :- $dateValueQ")
//            if (dateValueQ.isNotBlank()) {
//                dateMonth = dateValueQ
//                viewModel.getWalletQuizWinningStatus(strUserId, dateMonth)
//            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        TopAppBar(title = {
            Row(
                modifier = Modifier
                    .height(80.dp)
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(id = R.string.MyWallet),
                    fontSize = 15.sp,
                    fontFamily = FontFamily(
                        Font(R.font.inter_bold, FontWeight.Bold)
                    ),
                    modifier = Modifier.clickable {

                    })
                Spacer(modifier = Modifier.weight(1f))
                if (kycStatus.contains("Approve")) {
                    Text(
                        modifier = Modifier.clickable {
                            context.startActivity(Intent(
                                context, WalletActivity::class.java
                            ).apply {
                                putExtra(CommandAppKey.status, CommandAppKey.history)
                            })
                        },
                        text = if (languageData[LanguageTranslationsResponse.VIEW_HISTORY].toString() == "") {
                            stringResource(id = R.string.viewHistory)
                        } else {
                            languageData[LanguageTranslationsResponse.VIEW_HISTORY].toString()
                        },
                        fontSize = 12.sp,
                        color = PrimaryBlue,
                        fontFamily = FontFamily(
                            Font(R.font.inter_bold, FontWeight.Bold)
                        )
                    )
                } else {
                    println("Kyc not approved.")
                }
            }
        },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            modifier = Modifier
                .background(Transparent)
                .zIndex(1f),
            navigationIcon = {
                IconButton(onClick = {
                    openMenu.invoke()
                }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Toggle drawer",
                        tint = Black,
                        modifier = Modifier.background(Color.Unspecified)
                    )
                }
            })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp)
                .background(color = Color.White)
        ) {
            if (kycStatus.contains("Approve")) {
                Column {
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Use a Box to contain the Image and ensure it fills the width
                        Image(
                            painter = painterResource(id = R.drawable.card_bg),
                            contentDescription = "Background Image",
                            contentScale = ContentScale.Crop, // Use Crop to fill the width while maintaining aspect ratio
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Unspecified) // Ensure the image fills the width of the Box

                        )

                        // Column for text and button
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.Center)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = if (winningAmountData != null) {
                                    "\u20B9 ${winningAmountData!!.totalAmount?.toDouble()}"
                                } else {
                                    "\u20B9 0.00"
                                },
                                fontFamily = FontFamily(
                                    Font(R.font.inter_bold, FontWeight.Bold)
                                ),
                                fontSize = 28.sp,
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                                color = Black
                            )
                            Text(
                                text = if (languageData[LanguageTranslationsResponse.MICRO_SCHOLARSHIP_WON].toString() == "") {
                                    "Micro-scholarship Won"
                                } else {
                                    languageData[LanguageTranslationsResponse.MICRO_SCHOLARSHIP_WON].toString()
                                }, fontSize = 14.sp, fontFamily = FontFamily(
                                    Font(R.font.inter_regular, FontWeight.Normal)
                                ), modifier = Modifier.padding(start = 10.dp)
                            )

                            Spacer(modifier = Modifier.size(10.dp))

                            BtnUi(
                                onClick = {
//                                withdrawDialog = true
                                    isDialogVisible = true
                                    viewModel.getPaymentCheckConfig()
                                },
                                title = if (languageData[LanguageTranslationsResponse.WITHDRAW].toString() == "") {
                                    stringResource(id = R.string.withdraw)
                                } else {
                                    languageData[LanguageTranslationsResponse.WITHDRAW].toString()
                                },
                                enabled = true,
                                cornerRadius = 10.dp,
                                modifier = Modifier
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp, top = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (languageData[LanguageTranslationsResponse.WINNING_QUIZ_STATUS].toString() == "") {
                                    "Winning Quiz Status"
                                } else {
                                    languageData[LanguageTranslationsResponse.WINNING_QUIZ_STATUS].toString()
                                }, fontFamily = FontFamily(
                                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                ), fontSize = 14.sp
                            )

                            Text(
                                text = if (quizWinningData != null) {
                                    "${quizWinningData?.totalWinningQuizCount.toString()}" + if (languageData[LanguageTranslationsResponse.QUIZZES_WON].toString() == "") {
                                        " Quizzes Won"
                                    } else {
                                        languageData[LanguageTranslationsResponse.QUIZZES_WON].toString()
                                    }
                                } else {
                                    "0 " + if (languageData[LanguageTranslationsResponse.QUIZZES_WON].toString() == "") {
                                        " Quizzes Won"
                                    } else {
                                        languageData[LanguageTranslationsResponse.QUIZZES_WON].toString()
                                    }
                                }, color = GrayLight01, fontSize = 12.sp, fontFamily = FontFamily(
                                    Font(R.font.inter_medium, FontWeight.Medium)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Column(
                            modifier = Modifier.wrapContentWidth(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(modifier = Modifier) {
                                if (dateValueQ.equals("")) {
                                    val sdf = SimpleDateFormat("MMM yyyy")
                                    val currentDateAndTime = sdf.format(Date())
                                    dateMonth = currentDateAndTime.toString()
                                } else {
                                    dateValueQ = FormatMonthYear(dateValueQ)
                                    dateMonth = dateValueQ
                                }

                                Text(text = dateMonth, fontSize = 12.sp, fontFamily = FontFamily(

                                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                ), modifier = Modifier.clickable {
                                    scope.launch {
                                        isBottomSheetVisible = true
                                        sheetState.expand()
                                    }
                                })

                                Icon(painter = painterResource(id = R.drawable.ic_down),
                                    contentDescription = "asas",
                                    tint = GrayLight01,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(Color.Unspecified)
                                        .clickable {
                                            scope.launch {
                                                isBottomSheetVisible = true
                                                sheetState.expand()
                                            }
                                        })
                            }
                        }
                    }

                    if (quizWinningDataList.size != 0) {
                        LazyColumn {
                            val statusReviews = quizWinningDataList
                            items(items = statusReviews) { quizWinningDataList ->
                                status(leftIcon = if (quizWinningDataList.status.equals("Disapproved")) {
                                    painterResource(id = R.drawable.ic_disapprove)
                                } else if (quizWinningDataList.status.equals("Approved") || quizWinningDataList.status.equals(
                                        "Approve"
                                    )
                                ) {
                                    painterResource(id = R.drawable.ic_verified)
                                } else if (quizWinningDataList.status.equals("DISBURSED")) {
                                    painterResource(id = R.drawable.disbursed_icon)
                                } else {
                                    painterResource(id = R.drawable.ic_verification_inprogress)
                                },
                                    rightIcon = painterResource(id = R.drawable.right_arrow_icon),
                                    text1 = if (quizWinningDataList.status.equals("Approve") || quizWinningDataList.status.equals(
                                            "Approved"
                                        )
                                    ) {
                                        if (languageData[LanguageTranslationsResponse.SCHOLARSHIP_APPROVED].toString() == "") {
                                            "Scholarship Approved"
                                        } else {
                                            languageData[LanguageTranslationsResponse.SCHOLARSHIP_APPROVED].toString()
                                        }
                                    } else if (quizWinningDataList.status.equals("Disapproved")) {
                                        if (languageData[LanguageTranslationsResponse.SCHOLARSHIP_DISAPPROVED].toString() == "") {
                                            "Scholarship Disapproved"
                                        } else {
                                            languageData[LanguageTranslationsResponse.SCHOLARSHIP_DISAPPROVED].toString()
                                        }
                                    } else if (quizWinningDataList.status.equals("Unapprove")) {
                                        if (languageData[LanguageTranslationsResponse.VERIFICATION_IN_PROGRESS].toString() == "") {
                                            "Verification In progress"
                                        } else {
                                            languageData[LanguageTranslationsResponse.VERIFICATION_IN_PROGRESS].toString()
                                        }
                                    } else if (quizWinningDataList.status.equals("Pending")) {
                                        if (languageData[LanguageTranslationsResponse.SCHOLARSHIP_PENDING].toString() == "") {
                                            "Scholarship Pending"
                                        } else {
                                            languageData[LanguageTranslationsResponse.SCHOLARSHIP_PENDING].toString()
                                        }
                                    } else {
                                        "Scholarship Disbursed"
                                    },
                                    //quizWinningDataList.status.equals("Unapprove") ||
                                    text2 = if (quizWinningDataList.quizCount != null) {
                                        "${quizWinningDataList.quizCount.toString()} Quizzes | ₹ ${quizWinningDataList.amount.toString()}"
                                    } else {
                                        "0 Quizzes | ₹ ${quizWinningDataList.amount.toString()}"
                                    },
                                    onClick = {
                                        val position = statusReviews.indexOf(quizWinningDataList)
                                        Log.e(
                                            "TAG",
                                            "StudentWalletScreen: clicked  -- > " + position + " value " + quizWinningDataList.status.toString()
                                        )

                                        context.startActivity(Intent(
                                            context, WalletActivity::class.java
                                        ).apply {
                                            if (quizWinningDataList.status.equals("Approve") || quizWinningDataList.status.equals(
                                                    "Approved"
                                                )
                                            ) {
                                                putExtra(
                                                    CommandAppKey.status,/* if (languageData[LanguageTranslationsResponse.SCHOLARSHIP_APPROVED].toString() == "") {
                                                         "Scholarship Approved"
                                                     } else {
                                                         languageData[LanguageTranslationsResponse.SCHOLARSHIP_APPROVED].toString()
                                                     }*/"Scholarship Approved"
                                                )
                                            } else if (quizWinningDataList.status.equals("Disapproved")) {
                                                putExtra(
                                                    CommandAppKey.status,/*if (languageData[LanguageTranslationsResponse.SCHOLARSHIP_DISAPPROVED].toString() == "") {
                                                        "Scholarship Disapproved"
                                                    } else {
                                                        languageData[LanguageTranslationsResponse.SCHOLARSHIP_DISAPPROVED].toString()
                                                    }*/"Scholarship Disapproved"
                                                )
                                                //quizWinningDataList.status.equals("Unapprove") ||
                                            } else if (quizWinningDataList.status.equals("Unapprove")) {
                                                putExtra(
                                                    CommandAppKey.status,/*if (languageData[LanguageTranslationsResponse.VERIFICATION_IN_PROGRESS].toString() == "") {
                                                        "Verification In progress"
                                                    } else {
                                                        languageData[LanguageTranslationsResponse.VERIFICATION_IN_PROGRESS].toString()
                                                    }*/"Verification In progress"
                                                )
                                                //quizWinningDataList.status.equals("Unapprove") ||
                                            } else if (quizWinningDataList.status.equals("Pending")) {
                                                putExtra(
                                                    CommandAppKey.status,/* if (languageData[LanguageTranslationsResponse.SCHOLARSHIP_PENDING].toString() == "") {
                                                         "Scholarship Pending"
                                                     } else {
                                                         languageData[LanguageTranslationsResponse.SCHOLARSHIP_PENDING].toString()
                                                     }*/"Scholarship Pending"
                                                )
                                                //quizWinningDataList.status.equals("Unapprove") ||
                                            } else {
                                                putExtra(
                                                    CommandAppKey.status,
                                                    "Scholarship Disbursed"
                                                )
                                            }
                                        })
                                    })
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
                                text = stringResource(id = R.string.txt_oops_no_data_found),
                                modifier = Modifier.wrapContentSize(),
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Black,
                                textAlign = TextAlign.Start
                            )
                        }
                    }

//                Declaration(navHostController)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_kyc_img), // Replace with your lock icon drawable
                            contentDescription = "KYC Lock",
                            modifier = Modifier
                                .size(100.dp)
                                .padding(bottom = 16.dp)
                        )

                        Text(
                            text = "Apologies! KYC is mandatory to withdraw Scholarship",
                            fontFamily = FontFamily(
                                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                            ),
                            fontSize = 16.sp,
                            color = Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "You must complete KYC to withdraw Scholarship. If your KYC is not verified within 3 months of quiz, the quiz will be disapproved and the scholarship amount will be returned.",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(
                                Font(R.font.inter_regular, FontWeight.Normal)
                            ),
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                        )

                        BtnUi(
                            onClick = {
                                context.startActivity(Intent(
                                    context, StudentAuthenticationActivity::class.java
                                ).apply {
                                    putExtra(isKYC, isKYC)
                                })
                            },
                            title = "Continue to KYC Update",
                            enabled = true,
                            cornerRadius = 10.dp,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                    }
                }
            }
        }
    }

    if (withdrawDialog) {
        if (winningAmountData?.totalAmount == 0) {
            withdrawDialog = false
            Toast.makeText(
                context, "Please firstly play quiz and win your amount!", Toast.LENGTH_SHORT
            ).show()
        } else {
            Withdraw(languageData = languageData, onDismiss = {
                withdrawDialog = false
            }, clickedTitle = { type ->
                when (type) {
                    "BANK" -> {
                        viewModel.setBankUpi("Bank")
                        viewModel.setWalletAmount((winningAmountData?.totalAmount?.toDouble()).toString())
                        context.startActivity(Intent(
                            context, WalletActivity::class.java
                        ).apply {
                            putExtra(
                                CommandAppKey.status, "BankAdd"
                            )
                        })
                    }

                    "UPI" -> {
                        viewModel.setBankUpi("UPI")
                        viewModel.setWalletAmount((winningAmountData?.totalAmount?.toDouble()).toString())
                        context.startActivity(Intent(
                            context, WalletActivity::class.java
                        ).apply {
                            putExtra(
                                CommandAppKey.status, type // UPI
                            )
                        })
                    }
                }
            })
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun Withdraw(
    languageData: HashMap<String, String>,
    onDismiss: () -> Unit = {},
    clickedTitle: (String) -> Unit
) {
    val state = rememberDateRangePickerState()
    val bottomSheetState = rememberModalBottomSheetState(
//        initialValue = ModalBottomSheetValue.Hidden,
//            confirmStateChange = { targetValue ->
//                if (targetValue == ModalBottomSheetValue.Hidden) {
//                    onDismiss() // Call the onDismiss callback when the bottom sheet is hidden
//                    true // Allow the state change
//                } else {
//                    true // Allow other state changes
//                }
//            }
    )
    val coroutineScope = rememberCoroutineScope()

    // Show the bottom sheet when the composable is first launched
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            bottomSheetState.show()
        }
    }

    ModalBottomSheetLayout(
      //  sheetState = bottomSheetState,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.White)
                    .padding(bottom = 64.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, bottom = 32.dp, end = 16.dp)
                ) {
                    Text(
                        text = "Choose Transfer Option",
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                    )

                    Row(modifier = Modifier
                        .clickable {
                            clickedTitle.invoke("BANK")
                            coroutineScope.launch {
                                bottomSheetState.hide()
                            }
                        }
                        .fillMaxWidth()
                        .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_bank),
                            contentDescription = "dsd",
                            modifier = Modifier.background(Color.Unspecified)
                        )
                        Text(text = " BANK", color = Color.Black)
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            clickedTitle.invoke("UPI")
                            coroutineScope.launch {
                                bottomSheetState.hide()
                            }
                        }
                        .padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_upi),
                            contentDescription = "dsd",
                            modifier = Modifier.background(Color.Unspecified)
                        )
                        Text(text = " UPI", color = Color.Black)
                    }

                    val annotatedString = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = GrayLight01,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        ) {
                            append("Please contact ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        ) {
                            append("+91-9289938818")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = GrayLight01,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        ) {
                            append(" for support")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp, end = 16.dp
                            ), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = annotatedString,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(
                                start = 10.dp, top = 10.dp, bottom = 10.dp, end = 10.dp
                            )
                        )
                    }
                }
            }
        },
        content = {
//            PagerWithTabs()
        },
        scrimColor = Color.Black.copy(alpha = 0.5f),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    )
}

@Composable
fun status(
    leftIcon: Painter,
    rightIcon: Painter,
    text1: String,
    text2: String,
    onClick: () -> Unit // Add a parameter for the click action
) {
    Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {

        Column(
            modifier = Modifier
                .border(
                    width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(15)
                )
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(bottom = 5.dp, start = 2.dp, end = 0.dp)
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
                        .padding(end = 5.dp)
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


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Declaration(
    navHostController: NavHostController
) {
    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })
    BottomSheetNotice(
        isBottomSheetVisible = isBottomSheetVisible,
        sheetState = sheetState,
        onDismiss = {
            scope.launch { sheetState.hide() }.invokeOnCompletion { isBottomSheetVisible = false }
        },
        onDecline = {
            navHostController.navigate(AppRoute.StudentWalletDisclaimer.route)
        },
        title = "", //Last Updated 29th June 2024
        description = "I hereby state that all information provided about my or my child's banking information is true. I understand that Auro Scholar utilizes this information to transfer the microscholarship and to ensure security and compliance. I understand that Auro Scholar does not share or sell this information provided by me."
    )

//    if (sharedPref?.getKycStatus() != null && intent != "true") {
    LaunchedEffect(Unit) {
        scope.launch {
            isBottomSheetVisible = true
            sheetState.expand()
        }

//            if (intent == null && intent != addNewChild || intent != "true") {
//                scope.launch {
//                    isBottomSheetVisible = true
//                    sheetState.expand()
//                }
//            }
    }
//    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetNotice(
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDecline: () -> Unit,
    icon: Int = R.drawable.ic_alert,
    title: String,
    description: String,
) {
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
                modifier = Modifier
                    .wrapContentSize()
                    .padding(vertical = 15.dp, horizontal = 7.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = title,
                        alignment = Alignment.Center,
                        modifier = Modifier.background(Color.Unspecified)
                    )
                    Text(
                        stringResource(id = R.string.disclaimer),
                        modifier = Modifier.padding(5.dp),
                        color = Black,
                        fontFamily = FontFamily(
                            Font(R.font.inter_bold, FontWeight.Bold)
                        ),
                    )
                    Text(
                        description,
                        modifier = Modifier.padding(10.dp),
                        color = Gray,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        BtnTextUi(stringResource(id = R.string.i_decline),
                            color = Color.Red,
                            onClick = {
                                onDecline.invoke()
                            })
                        BtnNextUi(stringResource(id = R.string.i_accept), onClick = {
                            onDismiss.invoke()
                        }, enabled = true)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun monthYearOfWalletBottomSheet(
    filterValue: String, onDismiss: () -> Unit
): String {
    var selectedYear by remember { mutableStateOf(Year.now().value) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now().monthValue) }

    val sheetState = androidx.compose.material3.rememberModalBottomSheetState()
    var selectedValue by remember { mutableStateOf(filterValue) }


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
                YearMonthGridPicker(
                    selectedYear = selectedYear, selectedMonth = selectedMonth
                ) { year, month ->
                    selectedYear = year
                    selectedMonth = month
                    if (selectedMonth < 10) {
                        println("Selected Year: $selectedYear, Month: 0$selectedMonth")
                        selectedValue = "$selectedYear" + "0$selectedYear"
                    } else {
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
                        })

                    Btn12PXUi(title = stringResource(R.string.txt_apply_result), onClick = {
                        onDismiss()
                        println("Selected months :- $selectedValue")
                    }, false)
                }
            }
        }
    }
    return selectedValue
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetMonthYearOfWalletDialog(
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
                                studentViewModel.getWalletQuizWinningStatus(
                                    strUserId,
                                    currentDateAndTime.toString()
                                )
                            })

                        Btn12PXUi(title = stringResource(R.string.txt_apply_result), onClick = {
                            onDismiss()
                            studentViewModel.getWalletQuizWinningStatus(strUserId, selectedValue)
//                            println("Selected month and year :- $selectedValue")
                        }, false)
                    }
                }
            }
        }
    }

    return selectedValue
}

@Composable
fun FormatMonthYear(input: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyyMM", Locale.getDefault())
        val date = inputFormat.parse(input)

        val outputFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        outputFormat.format(date!!)
    } catch (e: Exception) {
        "N/A"
    }
}

@Preview
@Composable
fun StudentWalletScreenPreview() {
//    StudentWalletScreen(navHostController = rememberNavController())
}

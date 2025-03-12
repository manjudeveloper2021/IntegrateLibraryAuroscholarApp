package com.auro.application.ui.features.student.wallet.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.ParentDashboardActivity
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.authentication.screens.Button
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.features.student.wallet.Models.AccountTransactionRequestModel
import com.auro.application.ui.features.student.wallet.Models.AccountTransactionResponse
import com.auro.application.ui.features.student.wallet.Models.UpiTransactionRequestModel
import com.auro.application.ui.features.student.wallet.Models.UpiTransactionResponse
import com.auro.application.ui.theme.DarkRed2
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GreenDark02
import com.auro.application.ui.theme.Orange
import com.auro.application.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun TransferStatusScreenPreview(
    navController: NavHostController = rememberNavController(), status: String? = ""
) {
    val context: Context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()
//    var upiTransactionData by remember { mutableStateOf<UpiTransactionResponse?>(null) }
    var accountTransactionData by remember { mutableStateOf<AccountTransactionResponse?>(null) }
    val walletList by remember { mutableStateOf(mutableListOf<AccountTransactionRequestModel.WalletId?>()) }
    val upiList by remember { mutableStateOf(mutableListOf<UpiTransactionRequestModel.WinningWallet?>()) }
    var strBankOrUpi by remember { mutableStateOf<String?>("") }
    var strBankAccountUpiID by remember { mutableStateOf<String?>("") }
    var strTotalAmount by remember { mutableStateOf<String?>("") }

    var strTrnAmount by remember { mutableStateOf<String?>("") }
    var strTrnStatus by remember { mutableStateOf<String?>("") }
    var strTrnDateTime by remember { mutableStateOf<String?>("") }
    var strTrnId by remember { mutableStateOf<String?>("") }

    strBankOrUpi = viewModel.getBankUpi().toString()
    var isDialogVisible by remember { mutableStateOf(false) }
    var isEnable by remember { mutableStateOf(false) }

    val viewModelLogin: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModelLogin.getLanguageTranslationData(languageListName)

    if (strTrnStatus == "") {
        isDialogVisible = true
        strTrnAmount = viewModel.getTransactionAmount().toString()
        strTrnStatus = viewModel.getTransactionStatus().toString()
        strTrnDateTime = viewModel.getTransactionDateTime().toString()
        strTrnId = viewModel.getTransactionId().toString()
    } else {
        isDialogVisible = false
    }

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = msgLoader
    )

    if (strTrnId != "") {
        isDialogVisible = false
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    BackHandler {
        navController.popBackStack()
        startActivity(
            context, Intent(context, StudentDashboardActivity::class.java).apply {
                (context as Activity).finish()
            }, null
        )
    }

    LaunchedEffect(Unit) {
        /* viewModel.upiTransactionResponse.observeForever {
             when (it) {
                 is NetworkStatus.Idle -> {}
                 is NetworkStatus.Loading -> {
                     isDialogVisible = true
                 }

                 is NetworkStatus.Success -> {
                     if (it.data?.isSuccess == true) {
                         isDialogVisible = false
                         upiTransactionData = it.data
                     } *//*else {
                        context.toast(it.data?.error.toString())
                    }*//*
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                    println("Error Data :- ${it.message}")
                }
            }
        }*/

        /*  viewModel.accountTransactionResponse.observeForever {
              when (it) {
                  is NetworkStatus.Idle -> {}
                  is NetworkStatus.Loading -> {
                      isDialogVisible = true
                  }

                  is NetworkStatus.Success -> {
                      if (it.data?.isSuccess == true) {
                          isDialogVisible = false
                          accountTransactionData = it.data
                      } *//*else {
                        context.toast(it.data?.error.toString())
                    }*//*
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
//                    context.toast(it.message)
                    println("Error Data :- ${it.message}")
                }
            }
        }*/

        println("Transaction type :- $strBankOrUpi")
        when (strBankOrUpi) {
            "UPI" -> {
//                isEnable = false
//                isDialogVisible = true
//                strBankAccountUpiID = viewModel.getBankAccountUpiID().toString()
//                strTotalAmount = viewModel.getWalletInfoData().totalAmount.toString()
//                viewModel.getWalletInfoData().wallet?.forEach { winningWalletId ->
//                    val strWalletId: Int = winningWalletId.id!!
//                    println("Your wallet Id is :- $strWalletId")
//                    upiList.add(UpiTransactionRequestModel.WinningWallet(strWalletId))
//                }
//                viewModel.getUpiTransaction(
//                    UpiTransactionRequestModel(
//                        strBankAccountUpiID!!, strTotalAmount!!.toInt(), upiList
//                    )
//                )
            }

            "Bank" -> {
                isEnable = true
//                strBankAccountUpiID = viewModel.getBankAccountUpiID().toString()
//                strTotalAmount = viewModel.getWalletInfoData().totalAmount.toString()
//                viewModel.getWalletInfoData().wallet?.forEach { winningWalletId ->
//                    val strWalletId: Int = winningWalletId.id!!
//                    println("Your wallet Id is :- $strWalletId")
//                    walletList.add(AccountTransactionRequestModel.WalletId(strWalletId))
//                }
//                if (isEnable) {
//                    isDialogVisible = true
//                    viewModel.getAccountTransaction(
//                        AccountTransactionRequestModel(
//                            strBankAccountUpiID!!, strTotalAmount!!.toInt(), walletList
//                        )
//                    )
//                } else {
//                    println("Please select bank details...")
//                }
            }
        }
    }

    Column(
        modifier = /*when (strBankOrUpi) {
            "UPI" -> {
                if (strTrnStatus.equals("S")) {
                    Modifier
                        .fillMaxSize()
                        .background(color = GreenDark02)
                } else if (strTrnStatus.equals("F")) {
                    Modifier
                        .fillMaxSize()
                        .background(color = DarkRed2)
                } else {
                    Modifier
                        .fillMaxSize()
                        .background(color = Orange)
                }
            }

            "Bank" -> {
                if (strTrnStatus.equals("S")) {
                    Modifier
                        .fillMaxSize()
                        .background(color = GreenDark02)
                } else if (strTrnStatus.equals("F")) {
                    Modifier
                        .fillMaxSize()
                        .background(color = DarkRed2)
                } else {
                    Modifier
                        .fillMaxSize()
                        .background(color = Orange)
                }
            }

            else -> {
                Modifier
                    .fillMaxSize()
                    .background(color = if (status == "true") GreenDark02 else DarkRed2)
            }
        }*/if (strTrnStatus.equals("S")) {
            Modifier
                .fillMaxSize()
                .background(color = GreenDark02)
        } else if (strTrnStatus.equals("F")) {
            Modifier
                .fillMaxSize()
                .background(color = DarkRed2)
        } else {
            Modifier
                .fillMaxSize()
                .background(color = Orange)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            /*when (strBankOrUpi) {
                "UPI" -> {
                    Image(
                        painter = painterResource(
                            id = if (strTrnStatus.equals("S")) {
                                R.drawable.ic_approve
                            } else if (strTrnStatus.equals("F")) {
                                R.drawable.ic_faild
                            } else {
                                R.drawable.ic_pending
                            }
                        ),
//                        painter = painterResource(id = if (status == "true") R.drawable.ic_approve else R.drawable.ic_faild),
                        contentDescription = "sdsd"
                    )
                }

                "Bank" -> {
                    Image(
//                        painter = painterResource(id = if (status == "true") R.drawable.ic_approve else R.drawable.ic_faild),
                        painter = painterResource(
                            id = if (strTrnStatus.equals("S")) {
                                R.drawable.ic_approve
                            } else if (strTrnStatus.equals("F")) {
                                R.drawable.ic_faild
                            } else {
                                R.drawable.ic_pending
                            }
                        ), contentDescription = "sdsd"
                    )
                }
            }*/
            Image(
                painter = painterResource(
                    id = if (strTrnStatus.equals("S")) {
                        R.drawable.ic_approve
                    } else if (strTrnStatus.equals("F")) {
                        R.drawable.ic_faild
                    } else {
                        R.drawable.ic_pending
                    }
                ),
//                        painter = painterResource(id = if (status == "true") R.drawable.ic_approve else R.drawable.ic_faild),
                contentDescription = "sdsd",
                modifier = Modifier.background(Color.Unspecified)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(
                    color = Color.White, // Add a background color for visibility
                    shape = RoundedCornerShape(
                        topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Transaction ID",
                    fontSize = MaterialTheme.typography.subtitle1.fontSize, // Use fontSize from typography
                    color = Color.Black,
                    fontFamily = FontFamily(
                        Font(R.font.inter_regular, FontWeight.Medium)
                    )
                )
                Text(
                    text = if (strTrnId != "") {
                        strTrnId.toString()
                    } else {
                        "****9090"
                    }/*when (strBankOrUpi) {
                        "UPI" -> {
                            strTrnId ?: "****9090"
                        }

                        "Bank" -> {
                            strTrnId ?: "****9090"
                        }

                        else -> {
                            "****9090"
                        }
                    }*/,
//                    text = "2972A828",
                    fontSize = MaterialTheme.typography.subtitle1.fontSize, // Use fontSize from typography
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    )
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Amount",
                    fontSize = MaterialTheme.typography.subtitle1.fontSize, // Use fontSize from typography
                    color = Color.Black,
                    fontFamily = FontFamily(
                        Font(R.font.inter_regular, FontWeight.Medium)
                    )
                )
                Text(
                    text = if (strTrnAmount != "") {
                        "₹ $strTrnAmount"
                    } else {
                        "₹ 0.0"
                    }/*when (strBankOrUpi) {
                        "UPI" -> {
                            "₹ ${strTrnAmount ?: "0.0"}"
                        }

                        "Bank" -> {
                            "₹ ${strTrnAmount ?: "0.0"}"
                        }

                        else -> {
                            "₹ 0.00"
                        }
                    }*/,
                    fontSize = MaterialTheme.typography.subtitle1.fontSize, // Use fontSize from typography
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    )
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Time & Date",
                    fontSize = MaterialTheme.typography.subtitle1.fontSize, // Use fontSize from typography
                    color = Color.Black,
                    fontFamily = FontFamily(
                        Font(R.font.inter_regular, FontWeight.Medium)
                    )
                )
                Text(
                    text = /*when (strBankOrUpi) {
                        "UPI" -> {
                            try {
                                if (strTrnDateTime != null) {
                                    val isoDateString = strTrnDateTime.toString()
                                    parseAndFormatDate(isoDateString)

//                                    val isoDateString =
//                                        upiTransactionData?.data?.timestamp.toString()
//                                    val instant = Instant.parse(isoDateString)
//                                    val formatter =
//                                        DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")
//                                            .withZone(ZoneId.systemDefault())
//                                    "${formatter.format(instant) ?: "01/03/22, 11:00 AM"} "
                                } else {
//                                    "01/03/22, 11:00 AM"
                                    getCurrentDateTime()
                                }
                            } catch (exp: Exception) {
                                exp.message.toString()
                            }
                        }

                        "Bank" -> {
                            try {
                                if (strTrnDateTime != null) {
                                    val isoDateString = strTrnDateTime.toString()
                                    parseAndFormatDate(isoDateString)
//                                    val instant = Instant.parse(isoDateString)
//                                    val formatter =
//                                        DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")
//                                            .withZone(ZoneId.systemDefault())
//                                    "${formatter.format(instant) ?: "01/03/22, 11:00 AM"} "
                                } else {
//                                    "01/03/22, 11:00 AM"
                                    getCurrentDateTime()
                                }
                            } catch (exp: Exception) {
                                exp.message.toString()
                            }
                        }

                        else -> {
                            getCurrentDateTime()
                        }
                    }*/ try {
                        if (strTrnDateTime != "") {
                            val isoDateString = strTrnDateTime.toString()
                            parseAndFormatDate(isoDateString)
                        } else {
                            getCurrentDateTime()
                        }
                    } catch (exp: Exception) {
                        exp.message.toString()
                    },
                    fontSize = MaterialTheme.typography.subtitle1.fontSize, // Use fontSize from typography
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            val annotatedString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = GrayLight01, fontWeight = FontWeight.Medium, fontSize = 15.sp
                    )
                ) {
                    append("Please contact ")
                }
                withStyle(
                    style = SpanStyle(
                        color = PrimaryBlue, fontWeight = FontWeight.Medium, fontSize = 15.sp
                    )
                ) {
                    append("+91-9289938818")
                }
                withStyle(
                    style = SpanStyle(
                        color = GrayLight01, fontWeight = FontWeight.Medium, fontSize = 15.sp
                    )
                ) {
                    append(" for support")
                }
            }
            if (status != "true") {
                Text(
                    text = annotatedString,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                title = /*when (strBankOrUpi) {
                    "UPI" -> {
                        if (strTrnStatus.equals("S")) {
                            "Okay, Got it"
                        } else if (strTrnStatus.equals("F")) {
                            "Okay, Got it"
                        } else {
                            "Retry"
                        }
                    }

                    "Bank" -> {
                        if (strTrnStatus.equals("S")) {
                            "Okay, Got it"
                        } else if (strTrnStatus.equals("F")) {
                            "Okay, Got it"
                        } else {
                            "Retry"
                        }
                    }

                    else -> {
                        if (status == "true") "Okay, Got it" else "Retry"
                    }
                }*/ if (strTrnStatus.equals("S")) {
                    if (languageData[LanguageTranslationsResponse.OKAY_GOT_IT].toString() == "") {
                        "Okay, Got it"
                    } else {
                        languageData[LanguageTranslationsResponse.OKAY_GOT_IT].toString()
                    }
                } else if (strTrnStatus.equals("F")) {
                    if (languageData[LanguageTranslationsResponse.OKAY_GOT_IT].toString() == "") {
                        "Okay, Got it"
                    } else {
                        languageData[LanguageTranslationsResponse.OKAY_GOT_IT].toString()
                    }
                } else {
                    "Retry"
                },
//                if (status == "true") "Okay, Got it" else "Retry",
                enabled = true, onClick = {
                    if (status == "true") {
//                        (context as ComponentActivity).finish()
                        startActivity(
                            context, Intent(context, StudentDashboardActivity::class.java).apply {
                                (context as? Activity)?.finish()
                            }, null
                        )
                    } else {
//                        navController.navigate(
//                            AppRoute.UPIVerificationScreen("")
//                        )
                        startActivity(
                            context, Intent(context, StudentDashboardActivity::class.java).apply {
                                (context as? Activity)?.finish()
                            }, null
                        )
                    }
                }, modifier = Modifier
                    .padding(16.dp) // Add padding around the button
                    .align(Alignment.CenterHorizontally) // Center the button horizontally
            )
        }
    }
}

fun getCurrentDateTime(): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a", Locale.getDefault())
    return current.format(formatter)
}

@SuppressLint("SimpleDateFormat")
fun parseAndFormatDate(apiDateTime: String): String {
    try {
        val inputFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
        val date = inputFormat.parse(apiDateTime)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        return date?.let { outputFormat.format(it) } ?: "N/A"
    } catch (e: Exception) {
        return "01/03/22, 11:00 AM"
    }
}
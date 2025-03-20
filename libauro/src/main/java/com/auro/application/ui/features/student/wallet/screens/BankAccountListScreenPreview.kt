package com.auro.application.ui.features.student.wallet.screens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
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
import com.auro.application.core.ConstantVariables.LOGIN
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.CircleCheckbox
import com.auro.application.ui.common_ui.components.ReferBottomSheet
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.StudentRegisterWalletBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.authentication.screens.Button
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.features.student.wallet.Models.StudentAccountsListResponse
import com.auro.application.ui.features.student.wallet.Models.StudentUpiListResponse
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.GrayLight03
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.PrimaryBlueLt
import com.auro.application.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BankAccountListScreenPreview(
    navController: NavHostController = rememberNavController(), status: String? = ""
) {
    val context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()
    var strBankOrUpi: String = ""

    val isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })

    var selectedAccountIndex by remember { mutableStateOf(-1) }

    var addedUpiDataList by remember { mutableStateOf(mutableListOf<StudentUpiListResponse.UpiListData>()) }
    var createdAccountDataList by remember { mutableStateOf(mutableListOf<StudentAccountsListResponse.AccountsData>()) }

    val viewModels: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModels.getLanguageTranslationData(languageListName)

    var isDialogVisible by remember { mutableStateOf(false) }
    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = msgLoader
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.studentUpiListResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        addedUpiDataList =
                            it.data.data as MutableList<StudentUpiListResponse.UpiListData>
                        viewModel.setUPIListData(addedUpiDataList = addedUpiDataList)
                    } else {
                        isDialogVisible = false
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                    navController.popBackStack()
                    viewModel.setBankUpi("UPI")
                    navController.navigate(AppRoute.UPITransferScreen("UPI"))
                    println("Error Data :- ${it.message}")
                    viewModel.setUPIListData(addedUpiDataList = addedUpiDataList)
                }
            }
        }

        viewModel.studentAccountsListResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        createdAccountDataList =
                            it.data.data as MutableList<StudentAccountsListResponse.AccountsData>
                    } else {
                        isDialogVisible = false
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                    navController.popBackStack()
                    viewModel.setBankUpi("Bank")
                    navController.navigate(AppRoute.BankAddAccountScreen("Bank"))
                    println("Error Data :- ${it.message}")
                }
            }
        }

        strBankOrUpi = viewModel.getBankUpi().toString()
        println("Coming data from :- $strBankOrUpi")
        when (strBankOrUpi) {
            "UPI" -> {
                isDialogVisible = true
                viewModel.getStudentUpi()
            }

            "Bank" -> {
                isDialogVisible = true
                viewModel.getStudentAccounts()
            }
        }
    }

    /*var accounts: List<BankAccount> = arrayListOf(
        BankAccount(
            bankName = "HDFC", accountNo = "1234567", bankLogoResId = R.drawable.ic_bank
        ),
        BankAccount(
            bankName = "HDFC", accountNo = "1234567", bankLogoResId = R.drawable.ic_bank
        ),
        BankAccount(
            bankName = "HDFC", accountNo = "1234567", bankLogoResId = R.drawable.ic_bank
        ),
    )*/

    StudentRegisterWalletBackground(isShowBackButton = true,
        isDeleteBankAccount = true,
        isShowFullTopBarMenu = false,
        title = status.toString(),
        onDelete = {
            navController.navigate(AppRoute.BankAccountDeleteScreen())
        },
        onBackButtonClick = {
            (context as ComponentActivity).finish()
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                Column {
                    strBankOrUpi = viewModel.getBankUpi().toString()
                    Text(
                        text = when (strBankOrUpi) {
                            "UPI" -> {
                                "Select Your UPI ID"
                            }

                            "Bank" -> {
                                "Select Your Bank Account"
                            }

                            else -> {
                                ""
                            }
                        },
//                        text = stringResource(id = R.string.selectBankAccount),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        fontFamily = FontFamily(
                            Font(R.font.inter_bold, FontWeight.Bold)
                        ),
                        fontSize = 20.sp,
                        color = Black,
                        textAlign = TextAlign.Left
                    )

                    Text(
                        text = when (strBankOrUpi) {
                            "UPI" -> {
                                "Select UPI to withdraw your amount"
                            }

                            "Bank" -> {
                                "Select bank to withdraw your amount"
                            }

                            else -> {
                                ""
                            }
                        },
//                        stringResource(id = R.string.select_bank_account_to_withdraw_your_amount),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        color = GrayLight01,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Medium)
                        ),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .wrapContentHeight()
                            .padding(10.dp)
                    ) {
                        strBankOrUpi = viewModel.getBankUpi().toString()
                        when (strBankOrUpi) {
                            "UPI" -> {
                                val accounts = addedUpiDataList
                                accounts.forEachIndexed { index, account ->
                                    BankAccountListItem(accountNo = account.upiNumber.toString(),
                                        bankName = account.name.toString(),
                                        bankLogo = R.drawable.ic_upi,
                                        isChecked = selectedAccountIndex == index,
                                        onCheckedChange = { isChecked ->
                                            selectedAccountIndex = if (isChecked) index else -1
                                            viewModel.setBankAccountUpiID(account.upiNumber.toString())
                                        })

                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                                if (addedUpiDataList.size != 4) {
                                    AddAccount(languageData,
                                        clickAdd = {
                                            navController.popBackStack()
                                            viewModel.setBankUpi("New UPI")
                                            navController.navigate(AppRoute.UPITransferScreen("New UPI"))
                                        })
                                } else {
                                    println("Please delete any one already added UPI.")
                                }
                            }

                            "Bank" -> {
                                val accounts = createdAccountDataList
                                accounts.forEachIndexed { index, account ->
                                    BankAccountListItem(accountNo = account.bankAccount.toString(),
                                        bankName = account.name.toString(),
                                        bankLogo = R.drawable.ic_bank,
                                        isChecked = selectedAccountIndex == index,
                                        onCheckedChange = { isChecked ->
                                            selectedAccountIndex = if (isChecked) index else -1
                                            viewModel.setBankAccountUpiID(account.bankAccount.toString())
                                        })
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                                if (createdAccountDataList.size != 4) {
                                    AddAccount(languageData,
                                        clickAdd = {
                                            navController.popBackStack()
                                            viewModel.setBankUpi("New Bank")
                                            navController.navigate(AppRoute.BankAddAccountScreen("New Bank"))
                                        })
                                } else {
                                    println("Please delete any one already added account.")
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter)
                        .background(color = Color.White)
                        .shadow(elevation = 0.3.dp, shape = RoundedCornerShape(0.dp)),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    var showSheet by remember { mutableStateOf(false) }
                    if (showSheet) {
                        BottomSheetWithdraw(
                            languageData,
                            isBottomSheetVisible = isBottomSheetVisible, sheetState = sheetState
                        ) {
                            showSheet = false
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween // Optional: Adjusts spacing between buttons
                    ) {
                        BtnUi(
                            onClick = {
                                strBankOrUpi = viewModel.getBankUpi().toString()
                                when (strBankOrUpi) {
                                    "UPI" -> {
                                        if (selectedAccountIndex == -1) {
                                            Toast.makeText(
                                                context, "Please select UPI", Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            viewModel.setBankUpi("UPI")
                                            navController.navigate(AppRoute.UPIVerificationScreen("UPI"))
                                        }
                                    }

                                    "Bank" -> {
                                        if (selectedAccountIndex == -1) {
                                            Toast.makeText(
                                                context,
                                                "Please select bank account",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            viewModel.setBankUpi("Bank")
                                            showSheet = true
//                                        extracted(onDecline, onWithdraw,)
//                                        BottomSheetWithdraw( isBottomSheetVisible = isBottomSheetVisible, sheetState = sheetState, "", "", ""
//                                        )
                                            navController.navigate(
                                                AppRoute.BankAccountVerificationPreview(
                                                    "Account"
                                                )
                                            )
                                        }
                                    }
                                }
                            },
                            title = if (languageData[LanguageTranslationsResponse.CONT].toString() == "") {
                                stringResource(id = R.string.Continue)
                            } else {
                                languageData[LanguageTranslationsResponse.CONT].toString()
                            },
                            enabled = true,
                            cornerRadius = 10.dp,
                            modifier = Modifier
                        )
                    }

//                    Button(
//                        title = stringResource(id = R.string.Continue),
//                        onClick = {
//                            strBankOrUpi = viewModel.getBankUpi().toString()
//                            println("Coming data from :- $strBankOrUpi")
//                            when (strBankOrUpi) {
//                                "UPI" -> {
//                                    if (selectedAccountIndex == -1) {
//                                        Toast.makeText(
//                                            context, "Please select UPI", Toast.LENGTH_SHORT
//                                        ).show()
//                                    } else {
//                                        viewModel.setBankUpi("UPI")
//                                        navController.navigate(AppRoute.UPIVerificationScreen("UPI"))
//                                    }
//                                }
//
//                                "Bank" -> {
//                                    if (selectedAccountIndex == -1) {
//                                        Toast.makeText(
//                                            context,
//                                            "Please select bank account",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    } else {
//                                        viewModel.setBankUpi("Bank")
//                                        showSheet = true
//                                        println("Hi Compose program... $showSheet")
////                                        extracted(onDecline, onWithdraw,)
////                                        BottomSheetWithdraw( isBottomSheetVisible = isBottomSheetVisible, sheetState = sheetState, "", "", ""
////                                        )
//                                        navController.navigate(
//                                            AppRoute.BankAccountVerificationPreview(
//                                                "Account"
//                                            )
//                                        )
//                                    }
//                                }
//                            }
//                        },
//                        enabled = false, // Set to true or false based on your logic
//                    )
                }

//                BottomSheetWithdraw(
//                    isBottomSheetVisible = isBottomSheetVisible, sheetState = sheetState
//                )
            }
        })
}

//@Preview
@Composable
fun AddAccount(
    languageData: HashMap<String, String>,
    accountNo: String = "123456*****",
    bankName: String = "HDFC Bank",
    bankLogo: Int = R.drawable.ic_add,
    isChecked: Boolean = true,
    onCheckedChange: (Boolean) -> Unit = {},
    clickAdd: () -> Unit = {}
) {

    val viewModel: StudentViewModel = hiltViewModel()
    var strBankOrUpi: String = ""

    strBankOrUpi = viewModel.getBankUpi().toString()
    println("Coming data from :- $strBankOrUpi")

    Row(modifier = Modifier
        .clickable {
            clickAdd.invoke()
        }
        .height(100.dp)
        .fillMaxWidth()
        .padding(start = 2.dp, end = 2.dp, top = 10.dp, bottom = 10.dp)
        .border(
            1.dp, GrayLight02, shape = RoundedCornerShape(15.dp)
        )
        .drawBehind {
            val strokeWidth = 2.dp.toPx()
            val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

            drawRoundRect(
                color = PrimaryBlue,
                size = size,
                style = Stroke(width = strokeWidth, pathEffect = dashPathEffect),
                cornerRadius = CornerRadius(15.dp.toPx(), 15.dp.toPx())
            )
        }, verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = bankLogo),
            contentDescription = "Bank Logo",
            modifier = Modifier
                .size(70.dp)
                .padding(10.dp)
                .background(Color.Unspecified)
                .padding(start = 15.dp)
        )
        Column(
            modifier = Modifier
                .padding(10.dp)
                .weight(1f)
        ) {
            Text(
                text = when (strBankOrUpi) {
                    "UPI" -> {
                        "Add New UPI"
                    }

                    "Bank" -> {
                        "Add Bank Account"
                    }

                    else -> {
                        ""
                    }
                }, fontFamily = FontFamily(
                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                ), fontSize = 17.sp, color = PrimaryBlue
            )
        }
        Image(
            painter = painterResource(id = R.drawable.right_arrow_icon),
            contentDescription = "Right Arrow",
            modifier = Modifier
                .height(50.dp)
                .background(Color.Unspecified)
                .padding(end = 20.dp),
            colorFilter = ColorFilter.tint(PrimaryBlue)
        )
    }
}

data class BankAccount(
    val accountNo: String, val bankName: String, @DrawableRes val bankLogoResId: Int
)

@Preview
@Composable
fun BankAccountListItem(
    accountNo: String = "123456*****",
    bankName: String = "HDFC Bank",
    bankLogo: Int = R.drawable.ic_bank,
    isChecked: Boolean = true,
    onSelect: () -> Unit = {},
    onCheckedChange: (Boolean) -> Unit = {}
) {

    val selectedBorder = if (isChecked) BorderStroke(
        width = 1.dp,
        PrimaryBlue
    ) else BorderStroke(width = 0.5.dp, GrayLight02)

    val backGroundColor = if (isChecked) PrimaryBlueLt else White

    Row(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .background(color = backGroundColor)
            .padding(start = 1.dp, end = 1.dp)
            .border(
                BorderStroke(
                    1.dp,
                    if (isChecked) PrimaryBlue else GrayLight02
                ), // Border width and color
                shape = RoundedCornerShape(10.dp) // Same shape as the card
            )
            .clickable {
//                onSelect.invoke()
                onCheckedChange.invoke(isChecked)
            }, verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = bankLogo),
            contentDescription = "Bank Logo",
            modifier = Modifier
                .size(70.dp)
                .padding()
                .background(Color.Unspecified)
                .padding(start = 15.dp)
        )
        Column(
            modifier = Modifier
                .padding(10.dp)
                .weight(1f)
        ) {
            Text(
                text = bankName, fontFamily = FontFamily(
                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                ), fontSize = 17.sp
            )
            Text(
                text = accountNo,
                fontFamily = FontFamily(
                    Font(R.font.inter_medium, FontWeight.Medium)
                )
            )
        }

        RoundedCheckbox(
            checked = isChecked, onCheckedChange = onCheckedChange
        )
    }
}


@Composable
fun RoundedCheckbox(
    checked: Boolean, onCheckedChange: ((Boolean) -> Unit)? = null
) {
    val borderColor = if (checked) Color.Transparent else GrayLight02
    val backgroundColor = if (checked) PrimaryBlue else Color.White

    Box(modifier = Modifier
        .padding(end = 15.dp)
        .size(40.dp)
        .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(50.dp))
        .background(backgroundColor, RoundedCornerShape(50.dp))
        .clickable { onCheckedChange?.invoke(!checked) }) {
        Checkbox(
            checked = checked, onCheckedChange = onCheckedChange, colors = CheckboxDefaults.colors(
                checkedColor = PrimaryBlue,
                uncheckedColor = Color.Transparent,
                checkmarkColor = Color.White
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetWithdraw(
    languageData: HashMap<String, String>,
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit = {},
    onWithdraw: () -> Unit = {},
    onDecline: () -> Unit = {}
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
           // windowInsets = WindowInsets.ime
        ) {
            extracted(languageData, onDecline, onWithdraw)
        }
    }
}

//@Preview
@Composable
fun extracted(
    languageData: HashMap<String, String>,
    onDecline: () -> Unit = {},
    onWithdraw: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(vertical = 15.dp, horizontal = 7.dp)
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (languageData[LanguageTranslationsResponse.CONFRMATION].toString() == "") {
                    stringResource(R.string.confirmation)
                } else {
                    languageData[LanguageTranslationsResponse.CONFRMATION].toString()
                },
                modifier = Modifier.padding(10.dp),
                color = Black,
                fontWeight = FontWeight.Bold
            )
            Text(
                stringResource(R.string.lorem_ispsum_is_a_dummy_text_dummy_text),
                modifier = Modifier.padding(10.dp),
                color = Black,
                fontWeight = FontWeight.Bold
            )
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .padding(10.dp)
                    .fillMaxWidth()
                    .background(color = PrimaryBlueLt, shape = RoundedCornerShape(20.dp))
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_rupress_symble),
                    contentDescription = "rups",
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Unspecified)
                )
                Text(
                    "100.00",
                    modifier = Modifier.padding(10.dp),
                    color = Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Transaction ID",
                    fontSize = androidx.compose.material.MaterialTheme.typography.subtitle1.fontSize, // Use fontSize from typography
                    color = Color.Black
                )
                Text(
                    text = "2972A828",
                    fontSize = androidx.compose.material.MaterialTheme.typography.subtitle1.fontSize, // Use fontSize from typography
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Amount",
                    fontSize = androidx.compose.material.MaterialTheme.typography.subtitle1.fontSize, // Use fontSize from typography
                    color = Color.Black
                )
                Text(
                    text = "₹ 100.00",
                    fontSize = androidx.compose.material.MaterialTheme.typography.subtitle1.fontSize, // Use fontSize from typography
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Time & Date",
                    fontSize = androidx.compose.material.MaterialTheme.typography.subtitle1.fontSize, // Use fontSize from typography
                    color = Color.Black
                )
                Text(
                    text = "01/03/22, 11:00 AM",
                    fontSize = androidx.compose.material.MaterialTheme.typography.subtitle1.fontSize, // Use fontSize from typography
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ButtonDelete("Cancel", onClick = {
                    onDecline.invoke()

                }, modifier = Modifier.weight(1f), enabled = false)

                Button("Withdraw", onClick = {
                    onWithdraw.invoke()
                }, modifier = Modifier.weight(1f), enabled = true)

            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

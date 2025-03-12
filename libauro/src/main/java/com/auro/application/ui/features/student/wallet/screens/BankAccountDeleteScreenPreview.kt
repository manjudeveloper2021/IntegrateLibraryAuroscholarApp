package com.auro.application.ui.features.student.wallet.screens

import android.widget.Toast
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.StudentRegisterWalletBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.authentication.screens.Button
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.features.student.wallet.Models.DeleteUpiBankResponse
import com.auro.application.ui.features.student.wallet.Models.StudentAccountsListResponse
import com.auro.application.ui.features.student.wallet.Models.StudentUpiListResponse
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightRed01
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.PrimaryBlueLt
import com.auro.application.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BankAccountDeleteScreenPreview(
    navController: NavHostController = rememberNavController(), status: String? = ""
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })

//    val selectedAccountIndices = remember { mutableStateListOf<BankAccount>() }
    var selectedAccountIndex by remember { mutableStateOf(-1) }
    val upiId = remember { mutableStateOf("") }
    val accountNo = remember { mutableStateOf("") }

    var strBankOrUpi: String = ""
    val viewModel: StudentViewModel = hiltViewModel()
    var addedUpiDataList by remember { mutableStateOf(mutableListOf<StudentUpiListResponse.UpiListData>()) }
    var createdAccountDataList by remember { mutableStateOf(mutableListOf<StudentAccountsListResponse.AccountsData>()) }
    var deleteUpiBankData by remember { mutableStateOf<DeleteUpiBankResponse?>(null) }

    strBankOrUpi = viewModel.getBankUpi().toString()

    val viewModels: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModels.getLanguageTranslationData(languageListName)

    var isDialogVisible by remember { mutableStateOf(false) }
    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
//        message = "Loading your data..."
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
                    } else {
                        isDialogVisible = false
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                    println("Error Data :- ${it.message}")
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
                    println("Error Data :- ${it.message}")
                }
            }
        }

        viewModel.deleteUpiBankResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        deleteUpiBankData = it.data
                    } else {
                        isDialogVisible = false
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                    println("Error Data :- ${it.message}")
                }
            }
        }

        viewModel.deleteUpiBankResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        deleteUpiBankData = it.data
                        Toast.makeText(context, it.data.data.toString(), Toast.LENGTH_SHORT).show()
                        when (strBankOrUpi) {
                            "UPI" -> {
                                viewModel.setBankUpi("UPI")
                                navController.navigate(
                                    AppRoute.BankAccountListScreen(
                                        "UPI"
                                    )
                                )
                            }

                            "Bank" -> {
                                viewModel.setBankUpi("Bank")
                                navController.navigate(
                                    AppRoute.BankAccountListScreen(
                                        "Bank"
                                    )
                                )
                            }
                        }
                    } else {
                        isDialogVisible = false
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                    println("Error Data :- ${it.message}")
                }
            }
        }

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

    when (strBankOrUpi) {
        "UPI" -> {
//            var accounts = addedUpiDataList
            var accounts: MutableList<StudentUpiListResponse.UpiListData> = addedUpiDataList
        }

        "Bank" -> {
            var accounts: MutableList<StudentAccountsListResponse.AccountsData> =
                createdAccountDataList
//            var accounts = createdAccountDataList
        }
    }

//    var accounts: MutableList<BankAccount> = mutableListOf(
//        BankAccount(
//            bankName = "HDFC", accountNo = "12345sdsds67", bankLogoResId = R.drawable.ic_bank
//        ),
//        BankAccount(
//            bankName = "HDFC sdsd", accountNo = "1234567", bankLogoResId = R.drawable.ic_bank
//        ),
//        BankAccount(
//            bankName = "HDFC", accountNo = "1234567sdsdsds", bankLogoResId = R.drawable.ic_bank
//        ),
//    )

    StudentRegisterWalletBackground(isShowBackButton = true,
        isDeleteBankAccount = false,
        isShowFullTopBarMenu = false,
        title = "",
        onBackButtonClick = {
            navController.popBackStack()
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                Column {
                    Text(
                        text = when (strBankOrUpi) {
                            "UPI" -> {
                                "Delete UPI ID"
                            }

                            "Bank" -> {
                                "Delete Bank Account"
                            }

                            else -> {
                                "Delete Bank Account"
                            }
                        },
//                        text = stringResource(id = R.string.deleteBankAccount),
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
                                "Select UPI to delete your amount"
                            }

                            "Bank" -> {
                                "Select bank account to delete your amount"
                            }

                            else -> {
                                "Select bank account to delete your amount"
                            }
                        },
//                        stringResource(id = R.string.select_bank_account_to_withdraw_your_amount),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        color = GrayLight01,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
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
                        when (strBankOrUpi) {
                            "UPI" -> {
                                if (addedUpiDataList.size != 0) {
                                    val accounts = addedUpiDataList
                                    accounts.forEachIndexed { index, account ->
                                        BankAccountListItems(accountNo = account.upiNumber.toString(),
                                            bankName = account.name.toString(),
                                            bankLogo = R.drawable.ic_upi,
                                            isChecked = selectedAccountIndex == index,
//                                        isChecked = true,
//                                        isChecked = selectedAccountIndices.contains(account),
                                            onCheckedChange = { isChecked ->
                                                selectedAccountIndex = if (isChecked) index else -1
                                                upiId.value = account.upiNumber.toString()
                                            })

                                        Spacer(modifier = Modifier.height(10.dp))
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
                                            modifier = Modifier
                                                .wrapContentSize(),
                                            fontStyle = FontStyle.Normal,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Black,
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                }
                            }

                            "Bank" -> {
                                if (createdAccountDataList.size != 0) {
                                    val accounts = createdAccountDataList
                                    accounts.forEachIndexed { index, account ->
                                        BankAccountListItems(accountNo = account.bankAccount.toString(),
                                            bankName = account.name.toString(),
                                            bankLogo = R.drawable.ic_bank,
                                            isChecked = selectedAccountIndex == index,
//                                        isChecked = true,
//                                        isChecked = selectedAccountIndices.contains(account),
                                            onCheckedChange = { isChecked ->
                                                selectedAccountIndex = if (isChecked) index else -1
                                                accountNo.value = account.bankAccount.toString()
                                            })

                                        Spacer(modifier = Modifier.height(10.dp))
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
                                            modifier = Modifier
                                                .wrapContentSize(),
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
                                            isBottomSheetVisible = true
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
                                            isBottomSheetVisible = true
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

                    /* Button(
                         title = stringResource(id = R.string.Continue),
                         onClick = {
                             isBottomSheetVisible = true
                         },
                         enabled = false, // Set to true or false based on your logic
                     )*/
                }
            }

            if (isBottomSheetVisible) {
                BottomSheetDelete(languageData,
                    isBottomSheetVisible = isBottomSheetVisible,
                    sheetState = sheetState,
                    onDismiss = { isBottomSheetVisible = false },
                    onDeleted = {
                        when (strBankOrUpi) {
                            "UPI" -> {
                                isDialogVisible = true
                                viewModel.getDeleteUpi(upiId.value)
                            }

                            "Bank" -> {
                                isDialogVisible = true
                                viewModel.getDeleteAccount(accountNo.value)
                            }
                        }
                        isBottomSheetVisible = false
                    },
                    onDecline = {
                        isBottomSheetVisible = false
                    })
            }
        })
}


@Composable
fun BankAccountList(
    accounts: List<BankAccount>? = null, selectedAccountIndices: MutableSet<Int> = mutableSetOf()
) {
    accounts?.forEachIndexed { index, account ->
        BankAccountListItems(accountNo = account.accountNo,
            bankName = account.bankName,
            bankLogo = account.bankLogoResId,
            isChecked = selectedAccountIndices.contains(index), // Use contains to check if index is selected
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    selectedAccountIndices.add(index) // Add index to selected set
                } else {
                    selectedAccountIndices.remove(index) // Remove index from selected set
                }
            })
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Preview
@Composable
fun BankAccountListItems(
    accountNo: String = "123456*****",
    bankName: String = "HDFC Bank",
    bankLogo: Int = R.drawable.ic_bank,
    isChecked: Boolean = false,
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
                text = accountNo, fontFamily = FontFamily(
                    Font(R.font.inter_medium, FontWeight.Medium)
                )
            )
        }
        RoundedCheckboxDelete(
            checked = isChecked, onCheckedChange = onCheckedChange
        )
    }
}


@Composable
fun RoundedCheckboxDelete(
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
fun BottomSheetDelete(
    languageData: HashMap<String, String>,
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit = {},
    onDeleted: () -> Unit = {},
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
            windowInsets = WindowInsets.ime
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
                    Image(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "sahsga",
                        alignment = Alignment.Center,
                        modifier = Modifier.background(Color.Unspecified)
                    )
                    Text(
                        stringResource(R.string.are_you_sure_you_want_to_delete_bank_account),
                        modifier = Modifier.padding(10.dp),
                        color = Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(R.string.this_action_cannot_be_undone_all_values_associated_with_this_field_will_be_lost),
                        modifier = Modifier.padding(10.dp),
                        color = GrayLight01,
                        textAlign = TextAlign.Center,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {

                        ButtonDelete(
                            if (languageData[LanguageTranslationsResponse.KEY_CANCEL].toString() == "") {
                                "Cancel"
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_CANCEL].toString()
                            }, onClick = {
                                onDecline.invoke()

                            }, modifier = Modifier.weight(1f), enabled = false
                        )

                        ButtonDelete(
                            if (languageData[LanguageTranslationsResponse.KEY_DELETE].toString() == "") {
                                "Delete"
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_DELETE].toString()
                            }, onClick = {
                                onDeleted.invoke()
                            }, modifier = Modifier.weight(1f), enabled = true
                        )

                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}


@Composable
fun ButtonDelete(
    title: String = "Delete",
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(10.dp)
            .background(
                color = if (enabled) LightRed01 else {
                    White
                }, shape = RoundedCornerShape(
                    topStart = 15.dp, topEnd = 15.dp, bottomStart = 15.dp, bottomEnd = 15.dp
                )
            )
            .clickable(
                onClick = onClick
            )
    ) {
        Text(
            title,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center) // Center both horizontally and vertically
            ,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) White else {
                PrimaryBlue
            }
        )
    }
}

package com.auro.application.ui.features.student.wallet.screens

import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
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
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.features.student.wallet.Models.AddUpiResponse
import com.auro.application.ui.features.student.wallet.Models.StudentUpiListResponse
import com.auro.application.ui.features.student.wallet.titleAndEditText
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01

@Preview
@Composable
fun UPITransferScreenPreview(
    navController: NavHostController = rememberNavController(), status: String? = ""
) {

    val context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()
    val useranme = remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val userUpiId = remember { mutableStateOf("") }
    var upiIdAdded by remember { mutableStateOf("") }
    var strBankOrUpi: String = ""
    var addUpiData by remember { mutableStateOf<AddUpiResponse?>(null) }
    var addedUpiDataList by remember { mutableStateOf(mutableListOf<StudentUpiListResponse.UpiListData>()) }
    val upiRegex = Regex("^[a-zA-Z0-9._-]+@[a-zA-Z]+$")

    val viewModelLogin: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModelLogin.getLanguageTranslationData(languageListName)

    if (viewModel.getWalletAmount()!!.isNotEmpty()) {
        amount = viewModel.getWalletAmount().toString()
    } else {
        amount = "0.0"
    }

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
                        if (addedUpiDataList.isNotEmpty()) {
                            viewModel.setUPIListData(addedUpiDataList = addedUpiDataList)
                            viewModel.setBankUpi("UPI")
                            navController.navigate(
                                AppRoute.BankAccountListScreen(
                                    "UPI"
                                )
                            )
                        } else {
                            println("Data not found.")
                            viewModel.setUPIListData(addedUpiDataList = addedUpiDataList)
                        }
                    } else {
                        isDialogVisible = false
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    println("Error Data :- ${it.message}")
                    viewModel.setUPIListData(addedUpiDataList = addedUpiDataList)
                }
            }
        }

        viewModel.addUpiResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false

                        if (viewModel.getUPIListData().isNotEmpty()) {
                            viewModel.getUPIListData().forEach { item ->
                                upiIdAdded = item.upiNumber.toString()
                                println("Upi id :- $upiIdAdded")
                            }
                        } else {
                            upiIdAdded = ""
                            println("Upi id :- $upiIdAdded")
                        }

                        if (it.data.data?.virtualAddress.toString() == upiIdAdded) {
                            Toast.makeText(
                                context, "Your UPI ID already exist.", Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context, "Your UPI ID Successfully Added Now!", Toast.LENGTH_SHORT
                            ).show()
                            viewModel.setBankUpi("UPI")
                            viewModel.setBankAccountUpiID(it.data.data?.virtualAddress.toString())
                            /* navController.navigate(
                                 AppRoute.UPIVerificationScreen(
                                     "UPI"
                                 )
                             )*/
                            navController.navigate(AppRoute.BankAccountListScreen("Bank"))
                        }
                    } else {
                        isDialogVisible = false
                        Toast.makeText(
                            context,
                            "This UPI Id does not exist. Please check",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                    println("Error Data :- ${it.message}")
                }
            }
        }

        strBankOrUpi = viewModel.getBankUpi().toString()
        println("Come back with value :- $strBankOrUpi")
        if (strBankOrUpi == "New UPI") {
            /* viewModel.addUpiResponse.observeForever {
                 Log.d("UPI Validation  :- ", " &&&&&&&&&&&&&&&&")
                 when (it) {
                     is NetworkStatus.Idle -> {}
                     is NetworkStatus.Loading -> {
                         isDialogVisible = true
                         Log.d("UPI Validation Loading :- ", " Your UPI ID Successfully Added Now!")
                     }

                     is NetworkStatus.Success -> {
                         Log.d("UPI Validation Success :- ", " Your UPI ID Successfully Added Now!")
                         if (it.data?.isSuccess == true) {
                             isDialogVisible = false
                             Log.d("UPI Validation :- ", " Your UPI ID Successfully Added Now!")
                             Toast.makeText(
                                 context, "Your UPI ID Successfully Added Now!", Toast.LENGTH_SHORT
                             ).show()
                             viewModel.setBankUpi("UPI")
                             viewModel.setBankAccountUpiID(it.data.data?.virtualAddress.toString())
                             navController.navigate(
                                 AppRoute.UPIVerificationScreen(
                                     "UPI"
                                 )
                             )
                         } else {
                             isDialogVisible = false
                             Toast.makeText(
                                 context,
                                 "This UPI Id does not exist. Please check",
                                 Toast.LENGTH_SHORT
                             ).show()
                         }
                     }

                     is NetworkStatus.Error -> {
                         isDialogVisible = false
                         context.toast(it.message)
                         println("Error Data :- ${it.message}")
                     }
                 }
             }*/
        } else {
            isDialogVisible = true
            viewModel.getStudentUpi()
        }
    }

    StudentRegisterWalletBackground(isShowBackButton = true,
        isDeleteBankAccount = false,
        isShowFullTopBarMenu = false,
        title = "",
        onBackButtonClick = {
            (context as ComponentActivity).finish()
        },
        content = {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 12.dp)
                ) {
                    Text(
                        text = "Add UPI ID",
                        textAlign = TextAlign.Start,
                        color = Black,
                        fontFamily = FontFamily(
                            Font(R.font.inter_bold, FontWeight.Bold)
                        ),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    )

                    Text(
                        text = "You can add up to 4 UPI IDs",
                        textAlign = TextAlign.Start,
                        color = GrayLight01,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
                        ),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(top = 32.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(top = 32.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_upi),
                                contentDescription = "rupees",
                                modifier = Modifier
                                    .size(75.dp)
                                    .background(Color.Unspecified)
                            )

                            Text(
                                text = /*if (amount != null) amount else "0.0"*/"",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.h5,
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    titleAndEditText(
                        title = if (languageData[LanguageTranslationsResponse.ENTER_UPI].toString() == "") {
                            "Enter UPI ID"
                        } else {
                            languageData[LanguageTranslationsResponse.ENTER_UPI].toString()
                        },
                        username = userUpiId,
                        placeholder = if (languageData[LanguageTranslationsResponse.ENTER_UPI].toString() == "") {
                            "Enter UPI ID"
                        } else {
                            languageData[LanguageTranslationsResponse.ENTER_UPI].toString()
                        },
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(10.dp)
                            .background(color = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween // Optional: Adjusts spacing between buttons
                        ) {
                            BtnUi(
                                onClick = {
                                    if (userUpiId.value.isEmpty()) {
                                        Toast.makeText(
                                            context, "Enter your UPI ID", Toast.LENGTH_SHORT
                                        ).show()
                                    } else if (!upiRegex.matches(userUpiId.value)) {
                                        Toast.makeText(
                                            context, "Enter valid UPI ID", Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        isDialogVisible = true
                                        viewModel.getValidateUpi(userUpiId.value.trim())
                                    }
                                },
                                title = if (languageData[LanguageTranslationsResponse.KEY_CONFIRM].toString() == "") {
                                    stringResource(id = R.string.txt_confirm)
                                } else {
                                    languageData[LanguageTranslationsResponse.KEY_CONFIRM].toString()
                                },
                                enabled = true,
                                cornerRadius = 10.dp,
                                modifier = Modifier
                            )
                        }
                    }
                }
            }
        })
}

// Extension function to check if the keyboard is visible
fun View.isKeyboardVisible(): Boolean {
    val heightDiff = rootView.height - height
    return heightDiff > (rootView.height * 0.15) // 15% of the height of the root view
}

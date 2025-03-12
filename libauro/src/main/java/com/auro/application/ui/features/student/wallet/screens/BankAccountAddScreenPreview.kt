package com.auro.application.ui.features.student.wallet.screens

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.auro.application.ui.features.student.wallet.Models.CreateAccountData
import com.auro.application.ui.features.student.wallet.Models.CreateAccountResponse
import com.auro.application.ui.features.student.wallet.Models.StudentAccountsListResponse
import com.auro.application.ui.features.student.wallet.titleAndEditText
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01

@Preview
@Composable
fun BankAccountAddScreenPreview(
    navController: NavHostController = rememberNavController(), status: String? = ""
) {

    val context = LocalContext.current
    val username = remember { mutableStateOf("") }
    val accountNo = remember { mutableStateOf("") }
    val confirmAccountNumber = remember { mutableStateOf("") }
    val ifscCode = remember { mutableStateOf("") }
    var strBankOrUpi: String = ""
    val viewModel: StudentViewModel = hiltViewModel()
    var createAccountData by remember { mutableStateOf<CreateAccountResponse?>(null) }
    var createdAccountDataList by remember { mutableStateOf(mutableListOf<StudentAccountsListResponse.AccountsData>()) }
    val ifscPattern = "^[A-Z]{4}0[A-Z0-9]{6}\$".toRegex()
    var isValid by remember { mutableStateOf(true) }

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
                        if (createdAccountDataList.isNotEmpty()) {
                            viewModel.setBankUpi("Bank")
                            navController.navigate(
                                AppRoute.BankAccountListScreen(
                                    "Bank"
                                )
                            )
                        }
                    } else {
                        isDialogVisible = false
                    }
                }

                is NetworkStatus.Error -> {
//                    context.toast(it.message)
                    isDialogVisible = false
                    println("Error Data :- ${it.message}")
                }
            }
        }

        viewModel.createAccountResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        createAccountData = it.data
                        if (createAccountData?.data != null) {
                            isDialogVisible = false
                            Toast.makeText(
                                context,
                                createAccountData!!.data!!.Message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.setBankUpi("Bank")
                            navController.navigate(AppRoute.BankAccountListScreen("Bank"))
                        }
                    } else {
                        isDialogVisible = false
                        Toast.makeText(
                            context, it.data?.error.toString(), Toast.LENGTH_SHORT
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
        if (strBankOrUpi == "New Bank") {
            /*  viewModel.createAccountResponse.observeForever {
                  when (it) {
                      is NetworkStatus.Idle -> {}
                      is NetworkStatus.Loading -> {
                          isDialogVisible = true
                      }

                      is NetworkStatus.Success -> {
                          if (it.data?.isSuccess == true) {
                              isDialogVisible = false
                              createAccountData = it.data
                              if (createAccountData?.data != null) {
                                  isDialogVisible = false
                                  Toast.makeText(
                                      context,
                                      createAccountData!!.data!!.Message.toString(),
                                      Toast.LENGTH_SHORT
                                  ).show()
                                  viewModel.setBankUpi("Bank")
                                  navController.navigate(AppRoute.BankAccountListScreen("Bank"))
                              }
                          } else {
                              isDialogVisible = false
                              Toast.makeText(
                                  context, it.data?.error.toString(), Toast.LENGTH_SHORT
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
            viewModel.getStudentAccounts()
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
                modifier = Modifier.fillMaxSize().padding(start = 8.dp, end = 8.dp, top = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Enter Details",
                        textAlign = TextAlign.Center,
                        color = Black,
                        fontFamily = FontFamily(
                            Font(R.font.inter_bold, FontWeight.Bold)
                        ),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )

                    Text(
                        text = "You can add up to 4 Bank Account",
                        textAlign = TextAlign.Center,
                        color = GrayLight01,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Medium)
                        ),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )

                    titleAndEditText(
                        title = if (languageData[LanguageTranslationsResponse.NAME_BENEFICIARY].toString() == "") {
                            "Beneficiary Name "
                        } else {
                            languageData[LanguageTranslationsResponse.NAME_BENEFICIARY].toString()
                        },
                        username = username,
                        placeholder = if (languageData[LanguageTranslationsResponse.ENTER_BENEFECIARY].toString() == "") {
                            "Enter Beneficiary Name"
                        } else {
                            languageData[LanguageTranslationsResponse.ENTER_BENEFECIARY].toString()
                        },
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 16.dp)
                    )

                    titleAndEditText(
                        title = if (languageData[LanguageTranslationsResponse.ACC_NO].toString() == "") {
                            "Account Number"
                        } else {
                            languageData[LanguageTranslationsResponse.ACC_NO].toString()
                        },
                        username = accountNo,
                        placeholder = if (languageData[LanguageTranslationsResponse.ENTR_ACC_NO].toString() == "") {
                            "Enter Account Number"
                        } else {
                            languageData[LanguageTranslationsResponse.ENTR_ACC_NO].toString()
                        },
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 4.dp)
                    )

                    titleAndEditText(
                        title = if (languageData[LanguageTranslationsResponse.CONFRM_PIN].toString() == "") {
                            "Confirm Account Number"
                        } else {
                            languageData[LanguageTranslationsResponse.CONFRM_PIN].toString()
                        },
                        username = confirmAccountNumber,
                        "Enter Confirm Account Number",
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 4.dp)
                    )

                    titleAndEditText(
                        title = if (languageData[LanguageTranslationsResponse.IFSC_CODE].toString() == "") {
                            "IFSC Code"
                        } else {
                            languageData[LanguageTranslationsResponse.IFSC_CODE].toString()
                        },
                        username = ifscCode,
                        placeholder = if (languageData[LanguageTranslationsResponse.ENTER_IFSC].toString() == "") {
                            "Enter IFSC Code"
                        } else {
                            languageData[LanguageTranslationsResponse.ENTER_IFSC].toString()
                        },
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 4.dp)
                    )
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
                                if (username.value.isEmpty()) {
                                    Toast.makeText(
                                        context,
                                        "Please Enter Beneficiary Name!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (accountNo.value.isEmpty()) {
                                    Toast.makeText(
                                        context, "Please Enter Account Number!", Toast.LENGTH_SHORT
                                    ).show()
                                } else if (confirmAccountNumber.value.isEmpty()) {
                                    Toast.makeText(
                                        context,
                                        "Please Enter Confirm Account Number",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (accountNo.value != confirmAccountNumber.value) {
                                    Toast.makeText(
                                        context,
                                        "Account number and confirm account number should be same!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (ifscCode.value.isEmpty()) {
                                    Toast.makeText(
                                        context, "Please Enter IFSC Code!", Toast.LENGTH_SHORT
                                    ).show()
                                } else if (!ifscPattern.matches(ifscCode.value)) {
                                    Toast.makeText(
                                        context, "Please Enter valid IFSC Code!", Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    isDialogVisible = true
                                    viewModel.getCreateAccount(
                                        accountNo.value.trim(),
                                        username.value.trim(),
                                        ifscCode.value.trim()
                                    )
                                }
                            },
                            title = stringResource(id = R.string.addBankAccount),
                            enabled = true,
                            cornerRadius = 10.dp,
                            modifier = Modifier
                        )
                    }
                }
            }
        })
}
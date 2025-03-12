package com.auro.application.ui.features.student.wallet

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.auro.application.R
import com.auro.application.ui.common_ui.InputTextField
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.CommandAppKey
import com.auro.application.ui.features.login.componets.StudentRegisterWalletBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.authentication.screens.Button
import com.auro.application.ui.features.student.wallet.screens.BankAccountAddScreenPreview
import com.auro.application.ui.features.student.wallet.screens.BankAccountDeleteScreenPreview
import com.auro.application.ui.features.student.wallet.screens.BankAccountListScreenPreview
import com.auro.application.ui.features.student.wallet.screens.BankAccountVerificationScreenPreview
import com.auro.application.ui.features.student.wallet.screens.ScholarshipApprovedScreen
import com.auro.application.ui.features.student.wallet.screens.ScholarshipDisapprovedScreen
import com.auro.application.ui.features.student.wallet.screens.ScholarshipDisbursedScreen
import com.auro.application.ui.features.student.wallet.screens.ScholarshipInProgressScreen
import com.auro.application.ui.features.student.wallet.screens.ScholarshipPendingScreen
import com.auro.application.ui.features.student.wallet.screens.TransferStatusScreenPreview
import com.auro.application.ui.features.student.wallet.screens.UPITransferScreenPreview
import com.auro.application.ui.features.student.wallet.screens.UPIVerificationScreenPreview
import com.auro.application.ui.features.student.wallet.screens.WalletHistoryScreenPreview
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalletActivity : ComponentActivity() {
    var status: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuroscholarAppTheme {
                status = intent.getStringExtra(CommandAppKey.status)
                val navController = rememberNavController()

                val viewModelLogin: LoginViewModel = hiltViewModel()
                val languageListName = stringResource(id = R.string.key_lang_list)
                var languageData = HashMap<String, String>()
                languageData = viewModelLogin.getLanguageTranslationData(languageListName)


                NavHost(
                    navController = navController,
                    startDestination = when (status) {
                        "UPI" -> {
                            AppRoute.UPITransferScreen(status)
                        }

                        "BankAdd" -> {
                            AppRoute.BankAddAccountScreen(status)
                        }

                        "BankList" -> {
                            AppRoute.BankAccountListScreen(status)
                        }

                        "Verification In progress" -> {
                            AppRoute.ScholarshipInProgressScreen(status)
                        }

                        "Scholarship Approved" -> {
                            AppRoute.ScholarshipApprovedScreen(status)
                        }

                        "Scholarship Disapproved" -> {
                            AppRoute.ScholarshipDisapprovedScreen(status)
                        }

                        "Scholarship Disbursed" -> {
                            AppRoute.ScholarshipDisbursedScreen(status)
                        }

                        "Scholarship Pending" -> {
                            AppRoute.ScholarshipPendingScreen(status)
                        }

                        CommandAppKey.history -> {
                            AppRoute.WalletHistoryScreen(CommandAppKey.history)
                        }

                        else -> {}
                    }
                ) {

                    composable<AppRoute.ScholarshipInProgressScreen>() {
                        val args = it.toRoute<AppRoute.ScholarshipInProgressScreen>()
                        ScholarshipInProgressScreen(navController, args.status)
                    }

                    composable<AppRoute.ScholarshipDisapprovedScreen>() {
                        val args = it.toRoute<AppRoute.ScholarshipDisapprovedScreen>()
                        ScholarshipDisapprovedScreen(navController, args.status)
                    }

                    composable<AppRoute.ScholarshipApprovedScreen>() {
                        val args = it.toRoute<AppRoute.ScholarshipApprovedScreen>()
                        ScholarshipApprovedScreen(navController, args.status)
                    }

                    composable<AppRoute.ScholarshipDisbursedScreen>() {
                        val args = it.toRoute<AppRoute.ScholarshipDisbursedScreen>()
                        ScholarshipDisbursedScreen(navController, args.status)
                    }

                    composable<AppRoute.ScholarshipPendingScreen>() {
                        val args = it.toRoute<AppRoute.ScholarshipPendingScreen>()
                        ScholarshipPendingScreen(navController, args.status)
                    }

                    composable<AppRoute.WalletHistoryScreen>() {
                        val args = it.toRoute<AppRoute.WalletHistoryScreen>()
                        WalletHistoryScreenPreview(navController, args.status)
                    }

                    composable<AppRoute.BankAddAccountScreen>() {
                        val args = it.toRoute<AppRoute.BankAddAccountScreen>()
                        BankAccountAddScreenPreview(navController, args.userid)
                    }

                    composable<AppRoute.BankAccountListScreen>() {
                        val args = it.toRoute<AppRoute.BankAccountListScreen>()
                        BankAccountListScreenPreview(navController, args.userid)
                    }

                    composable<AppRoute.BankAccountDeleteScreen>() {
                        val args = it.toRoute<AppRoute.BankAccountDeleteScreen>()
                        BankAccountDeleteScreenPreview(navController, args.userid)
                    }

                    composable<AppRoute.UPITransferScreen>() {
                        val args = it.toRoute<AppRoute.UPITransferScreen>()
                        UPITransferScreenPreview(navController, args.userid)
                    }

                    composable<AppRoute.UPIVerificationScreen>() {
                        val args = it.toRoute<AppRoute.UPIVerificationScreen>()
                        UPIVerificationScreenPreview(navController, args.upiId)
                    }

                    composable<AppRoute.TransferStatusScreenPreview>() {
                        val args = it.toRoute<AppRoute.TransferStatusScreenPreview>()
                        TransferStatusScreenPreview(navController, args.upiId)
                    }

                    composable<AppRoute.BankAccountVerificationPreview>() {
                        val args = it.toRoute<AppRoute.TransferStatusScreenPreview>()
                        BankAccountVerificationScreenPreview(navController, args.upiId)
                    }
                }
            }
        }
    }
}

@Composable
fun titleAndEditText(
    title: String,
    username: MutableState<String>,
    placeholder: String = "",
    modifier: Modifier = Modifier
) {

    val viewModelLogin: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModelLogin.getLanguageTranslationData(languageListName)


    Column(modifier = modifier) {
        Text(
            text = title,
            modifier = Modifier.padding(top = 5.dp),
            fontFamily = FontFamily(
                Font(R.font.inter_medium, FontWeight.Medium)
            )
        )

        InputTextField(
            modifier = Modifier.padding(top = 5.dp),
            value = username,
            placeholder = placeholder,
            keyboardType = when (placeholder) {
                if (languageData[LanguageTranslationsResponse.ENTER_BENEFECIARY].toString() == "") {
                    "Enter Beneficiary Name"
                } else {
                    languageData[LanguageTranslationsResponse.ENTER_BENEFECIARY].toString()
                } -> {
                    KeyboardType.Text
                }

                if (languageData[LanguageTranslationsResponse.ENTR_ACC_NO].toString() == "") {
                    "Enter Account Number"
                } else {
                    languageData[LanguageTranslationsResponse.ENTR_ACC_NO].toString()
                } -> {
                    KeyboardType.Number
                }

                "Enter Confirm Account Number" -> {
                    KeyboardType.Number
                }

                else -> {
                    KeyboardType.Text
                }
            }
        )
    }
}
package com.auro.application.ui.features.student.wallet.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray

//
//@Composable
//fun StudentWalletDisclaimerPreview() {
//    StudentWalletDisclaimer(navController = rememberNavController())
//}

@Preview
@Composable
fun StudentWalletDisclaimer(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current

    val viewModelLogin: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModelLogin.getLanguageTranslationData(languageListName)

    StudentRegisterBackground(
        isShowBackButton = true,
        onBackButtonClick = {
            navController.popBackStack()
            navController.navigate(AppRoute.StudentWallet)
        },
        content = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo_auro_scholar),
                            contentDescription = "dskjdhsgd",
                            modifier = Modifier
                                .align(
                                    Alignment.Center
                                )
                                .background(Color.Unspecified)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clickable {
                                (context as ComponentActivity).finish()
                            }
                            .align(alignment = Alignment.CenterVertically)
                            .weight(1f)
                            .height(100.dp)
                    ) {
                        Image(painter = painterResource(R.drawable.ic_close),
                            contentDescription = "dskjdhsgd",
                            modifier = Modifier
                                .clickable {
//                                navigateWithArgs(navController,AppRoute.AadharCheck("%s").route,"false", removeBackStack = true)

                                    navController.popBackStack()
                                    navController.navigate(AppRoute.AadharCheck("false"))
                                }
                                .align(
                                    Alignment.Center
                                )
                                .background(Color.Unspecified))
                    }
                }

                Text(
                    text = if (languageData[LanguageTranslationsResponse.KEY_THANK_YOUR_RESPONSE].toString() == "") {
                        stringResource(id = R.string.thank_you_for_your_response)
                    } else {
                        languageData[LanguageTranslationsResponse.KEY_THANK_YOUR_RESPONSE].toString()
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (languageData[LanguageTranslationsResponse.KEY_FEATURE_MAY_BE_LIMITED_UNTIL_YOU].toString() == "") {
                        stringResource(R.string.please_note_that_some_features_may_be_limited_until_you)
                    } else {
                        languageData[LanguageTranslationsResponse.KEY_FEATURE_MAY_BE_LIMITED_UNTIL_YOU].toString()
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(10.dp),
                    color = Gray,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.weight(1f))

                BtnUi(
                    title = if (languageData[LanguageTranslationsResponse.I_ACCEPT].toString() == "") {
                        stringResource(R.string.i_accept)
                    } else {
                        languageData[LanguageTranslationsResponse.I_ACCEPT].toString()
                    },
                    onClick = {
                        navController.popBackStack()
                        navController.navigate(AppRoute.StudentWallet.route)
                    }, enabled = true
                )
            }
        }
    )
}
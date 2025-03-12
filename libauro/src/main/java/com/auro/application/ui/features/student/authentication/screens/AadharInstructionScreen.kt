package com.auro.application.ui.features.student.authentication.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.ParentDashboardActivity
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.Transparent
import com.auro.application.ui.theme.White

@Composable
fun AadharInstructionScreen(
    navController: NavHostController,
    isAccept: String?,
    context: Context = LocalContext.current
) {
    val loginViewModel: LoginViewModel = hiltViewModel()

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    BackHandler {
        if (loginViewModel.getParentInfo()!!.isParent) {
            startActivity(
                context,
                Intent(context, ParentDashboardActivity::class.java).apply {
                    (context as Activity).finish()
                },
                null
            )
        } else {
            val intent =
                Intent(context, StudentDashboardActivity::class.java).apply {
                    (context as Activity).finish()
                }
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.also {
                it.putExtra("", true)
            }
            context.startActivity(intent)
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = White)
                .padding(end = 8.dp, bottom = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                contentDescription = "Close",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable {
//                        navController.popBackStack()
//                        navController.navigate(AppRoute.AadharCheck("false"))
                        if (loginViewModel.getParentInfo()!!.isParent) {
                            startActivity(
                                context,
                                Intent(context, ParentDashboardActivity::class.java).apply {
                                    (context as Activity).finish()
                                },
                                null
                            )
                        } else {
                            val intent =
                                Intent(context, StudentDashboardActivity::class.java).apply {
                                    (context as Activity).finish()
                                }
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.also {
                                it.putExtra("", true)
                            }
                            context.startActivity(intent)
                        }
                    }
                    .padding(16.dp)
                    .size(24.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(.1f))

                Image(
                    painter = painterResource(id = R.drawable.logo_auro_scholar), // Replace with your logo
                    contentDescription = "Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = if (languageData[LanguageTranslationsResponse.KEY_THANK_YOUR_RESPONSE].toString() == "") {
                        stringResource(id = R.string.thank_you_for_your_response)
                    } else {
                        languageData[LanguageTranslationsResponse.KEY_THANK_YOUR_RESPONSE].toString()
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )

                Text(
                    text = if (languageData[LanguageTranslationsResponse.KEY_FEATURE_MAY_BE_LIMITED_UNTIL_YOU].toString() == "") {
                        stringResource(R.string.please_note_that_some_features_may_be_limited_until_you)
                    } else {
                        languageData[LanguageTranslationsResponse.KEY_FEATURE_MAY_BE_LIMITED_UNTIL_YOU].toString()
                    },
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )

                Spacer(modifier = Modifier.weight(.1f))

                BtnUi(
                    title = if (languageData[LanguageTranslationsResponse.I_ACCEPT].toString() == "") {
                        stringResource(R.string.i_accept)
                    } else {
                        languageData[LanguageTranslationsResponse.I_ACCEPT].toString()
                    },
                    onClick = {
                        navController.popBackStack()
                        navController.navigate(AppRoute.AadharCheck("true"))
                    }, enabled = true,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }

    /* StudentRegisterBackground(
         isShowBackButton = true,
         onBackButtonClick = {
             navController.popBackStack()
             navController.navigate(AppRoute.AadharCheck("false"))
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
                     ){
                         Image(painter = painterResource(R.drawable.logo_auro_scholar), contentDescription = "dskjdhsgd", modifier = Modifier.align(
                             Alignment.Center))
                     }
                     Box(
                         modifier = Modifier
                             .clickable {

                                 (context as ComponentActivity).finish()

                             }
                             .align(alignment = Alignment.CenterVertically)
                             .weight(1f)
                             .height(100.dp)
                     ){
                         Image(painter = painterResource(R.drawable.ic_close), contentDescription = "dskjdhsgd", modifier = Modifier
                             .clickable {
 //                                navigateWithArgs(navController,AppRoute.AadharCheck("%s").route,"false", removeBackStack = true)

                                 navController.popBackStack()
                                 navController.navigate(AppRoute.AadharCheck("false"))
                             }
                             .align(
                                 Alignment.Center))

                     }
                 }


                 Text(
                     text = stringResource(id = R.string.thank_you_for_your_response),
                     modifier = Modifier
                         .fillMaxWidth(),
                     fontStyle = FontStyle.Normal,
                     fontWeight = FontWeight.Bold,
                     fontSize = 20.sp,
                     color = Black,
                     textAlign = TextAlign.Center
                 )

                 Text(
                     stringResource(R.string.please_note_that_some_features_may_be_limited_until_you),
                     modifier = Modifier
                         .wrapContentWidth()
                         .padding(10.dp),
                     color = Gray,
                     textAlign = TextAlign.Center,
                 )
                 Spacer(modifier = Modifier.weight(1f))

                 BtnUi(title = stringResource(R.string.i_accept),
                     onClick = {
                         navController.popBackStack()
                         navController.navigate(AppRoute.AadharCheck("true"))
                     }, enabled = true
                 )
             }
         }
     )*/
}

@Preview
@Composable
fun AadharInstrtion() {
    AadharInstructionScreen(navController = rememberNavController(), null)
}
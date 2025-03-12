package com.auro.application.ui.features.student.authentication.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.ParentDashboardActivity
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.White


@Preview
@Composable
fun AuthenticationStatus(
    navHostController: NavHostController = rememberNavController(),
    sharedPref: SharedPref? = null,
    userId: String? = null
) {
    val context: Context = LocalContext.current
    val loginViewModel: LoginViewModel = hiltViewModel()

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    BackHandler {
        navHostController.popBackStack()
        if (loginViewModel.getParentInfo()!!.isParent) {
            startActivity(
                context,
                Intent(context, ParentDashboardActivity::class.java),
                null
            ).apply { (context as Activity).finish() }
        } else {
            startActivity(
                context,
                Intent(context, StudentDashboardActivity::class.java),
                null
            ).apply { (context as Activity).finish() }
        }
    }

    StudentRegisterBackground(
        isShowBackButton = false,
        onBackButtonClick = {

        },
        content = {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Image(
                        painter = painterResource(id = R.drawable.ic_approve),
                        contentDescription = "dsds",
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.size(30.dp))
                    Text(
                        text = if (languageData[LanguageTranslationsResponse.AUTHENTICATION_APPROVED].toString() == "") {
                            "Authentication Approved"
                        } else {
                            languageData[LanguageTranslationsResponse.AUTHENTICATION_APPROVED].toString()
                        }, fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(30.dp))
                    Text(
                        text = "Thanks for submitting your document. All Documents are approved for Authentication",
                        modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                    )

                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter)
                        .background(color = Color.White)
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(0.dp)),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    BtnUi(
                        onClick = {
                            navHostController.popBackStack()
                            if (loginViewModel.getParentInfo()!!.isParent) {
                                startActivity(
                                    context,
                                    Intent(context, ParentDashboardActivity::class.java),
                                    null
                                ).apply { (context as Activity).finish() }
                            } else {
                                startActivity(
                                    context,
                                    Intent(context, StudentDashboardActivity::class.java),
                                    null
                                ).apply { (context as Activity).finish() }
                            }
                        },
                        title = if (languageData[LanguageTranslationsResponse.FINISH].toString() == "") {
                            stringResource(id = R.string.finish)
                        } else {
                            languageData[LanguageTranslationsResponse.FINISH].toString()
                        },
                        enabled = true
                    )

                }
            }
        }
    )
}

package com.auro.application.ui.features.login.screens

import android.content.Context
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.App
import com.auro.application.R
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationNotice(
    navHostController: NavHostController,
    context: Context = LocalContext.current,
    sendBundle: (Any) -> Unit = {}
) {

    StudentRegisterBackground(
        isShowBackButton = false,
        onBackButtonClick = {
            navHostController.navigateUp()
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
                                .align(Alignment.Center)
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
                        Image(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "dskjdhsgd",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(Color.Unspecified)
                        )

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

                BtnUi(
                    title = stringResource(R.string.i_accept),
                    onClick = {
                        sendBundle("true")
                    }, enabled = true
                )
            }

        }
    )
}


@Composable
@Preview
fun RegistrationNotice() {
    RegistrationNotice(navHostController = rememberNavController())
}

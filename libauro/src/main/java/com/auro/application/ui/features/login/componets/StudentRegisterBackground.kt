package com.auro.application.ui.features.login.componets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.auro.application.R
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White

@Preview
@Composable
fun StudentRegisterBackground(
    modifier: Modifier = Modifier,
    isShowBackButton: Boolean = true,
    onBackButtonClick: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),color = White
    ) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isShowBackButton) {
                    Image(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(y = 10.dp, x = 10.dp)
                            .size(30.dp) // adjust the size as needed
                            .clickable(onClick = onBackButtonClick),
                        painter = painterResource(id = R.drawable.back_icon),
                        contentDescription = "back_button",
                        colorFilter = ColorFilter.tint(Gray)
                    )
                }
            }


            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
fun StudentRegisterWalletBackground(
    modifier: Modifier = Modifier,
    isShowBackButton: Boolean = true,
    isShowFullTopBarMenu: Boolean = false,
    title: String = "KYC TITLE",
    isDeleteBankAccount: Boolean = false,
    onBackButtonClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onCalenderClick: () -> Unit = {},
    onDelete: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    var strBankOrUpi: String = ""
    val viewModel: StudentViewModel = hiltViewModel()

    strBankOrUpi = viewModel.getBankUpi().toString()
    println("Coming data from :- $strBankOrUpi")

    Surface(
        modifier = modifier.fillMaxWidth(), color = White
    )
    {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = White),
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isShowBackButton) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(start = 10.dp, end = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .size(30.dp)
                                .padding(end = 5.dp) // adjust the size as needed
                                .clickable(onClick = onBackButtonClick),
                            painter = painterResource(id = R.drawable.back_icon),
                            colorFilter = ColorFilter.tint(Color.Black),
                            contentDescription = "back_button"
                        )

                        Text(text = title, fontWeight = FontWeight.Medium, fontSize = 15.sp)

                        Spacer(modifier = Modifier.weight(1f))
                        if (isShowFullTopBarMenu) {
                            Image(
                                modifier = Modifier
                                    .size(30.dp) // adjust the size as needed
                                    .clickable(onClick = onCalenderClick),
                                painter = painterResource(id = R.drawable.calender_icon),
                                contentDescription = "back_button"
                            )
                            Image(
                                modifier = Modifier
                                    .size(30.dp) // adjust the size as needed
                                    .clickable(onClick = onFilterClick),
                                painter = painterResource(id = R.drawable.filter_icon),
                                contentDescription = "back_button"
                            )
                        }
                        if (isDeleteBankAccount) {
                            Text(text = when (strBankOrUpi) {
                                "UPI" -> {
                                    "Delete UPI"
                                }

                                "Bank" -> {
                                    "Delete Account"
                                }

                                else -> {
                                    "Delete Account"
                                }
                            },
//                                text = "Delete Account",
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                color = PrimaryBlue,
                                modifier = Modifier.clickable {
                                    onDelete.invoke()
                                })
                        }
                    }
                }
            }

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
fun StudentRegisterWalletHistoryBackground(
    modifier: Modifier = Modifier,
    isShowBackButton: Boolean = true,
    isShowFullTopBarMenu: Boolean = false,
    title: String = "KYC TITLE",
    isDeleteBankAccount: Boolean = false,
    onBackButtonClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onDelete: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    var strBankOrUpi: String = ""
    val viewModel: StudentViewModel = hiltViewModel()

    strBankOrUpi = viewModel.getBankUpi().toString()
    println("Coming data from :- $strBankOrUpi")

    Surface(
        modifier = modifier.fillMaxWidth(), color = White
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = White),
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isShowBackButton) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(start = 10.dp, end = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .size(30.dp)
                                .padding(end = 5.dp) // adjust the size as needed
                                .clickable(onClick = onBackButtonClick),
                            painter = painterResource(id = R.drawable.back_icon),
                            colorFilter = ColorFilter.tint(Color.Black),
                            contentDescription = "back_button"
                        )

                        Text(text = title, fontWeight = FontWeight.Medium, fontSize = 15.sp)

                        Spacer(modifier = Modifier.weight(1f))
                        if (isShowFullTopBarMenu) {
                            Image(
                                modifier = Modifier
                                    .size(30.dp) // adjust the size as needed
                                    .clickable(onClick = onFilterClick),
                                painter = painterResource(id = R.drawable.filter_icon),
                                contentDescription = "back_button"
                            )
                        }
                        if (isDeleteBankAccount) {
                            Text(text = when (strBankOrUpi) {
                                "UPI" -> {
                                    "Delete UPI"
                                }

                                "Bank" -> {
                                    "Delete Account"
                                }

                                else -> {
                                    "Delete Account"
                                }
                            },
//                                text = "Delete Account",
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                color = PrimaryBlue,
                                modifier = Modifier.clickable {
                                    onDelete.invoke()
                                })
                        }
                    }
                }
            }

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                content()
            }
        }
    }
}
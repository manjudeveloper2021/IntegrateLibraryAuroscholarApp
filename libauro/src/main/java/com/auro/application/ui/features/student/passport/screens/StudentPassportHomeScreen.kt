package com.auro.application.ui.features.student.passport.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.data.api.NetworkStatus
import com.auro.application.ui.common_ui.components.CustomHorizontalProgressBar
import com.auro.application.ui.common_ui.components.TextWithIconOnRight
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.passport.BadgesAndAwardsActivity
import com.auro.application.ui.features.student.passport.StudentLeaderboardActivity
import com.auro.application.ui.features.student.passport.StudentPassportRouteActivity
import com.auro.application.ui.features.student.passport.StudentUnlockAwardsActivity
import com.auro.application.ui.features.student.passport.models.AwardData
import com.auro.application.ui.features.student.passport.models.BadgeAwardData
import com.auro.application.ui.features.student.passport.models.BadgeData
import com.auro.application.ui.features.student.passport.models.MonthlyReport
import com.auro.application.ui.features.student.passport.models.ReportsPerformingResponse
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Bg_Gray
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightPink02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.Transparent
import com.auro.application.ui.theme.White
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
fun StudentPassportHomeScreen(
    innerPadding: PaddingValues, context: Context, openDrawMenuItem: () -> Unit
) {
    val context: Context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()

    var navHostController: NavHostController = rememberNavController()
    val selectedBorder = BorderStroke(width = 0.5.dp, GrayLight02)

    val viewModels: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModels.getLanguageTranslationData(languageListName)

    var coreQuiz = stringResource(id = R.string.txt_core_quiz)
    var txtRetakeQuiz = stringResource(id = R.string.txt_retake_quiz)
    var practiceQuiz = stringResource(id = R.string.txt_practice_quiz)
    var microScholarship = stringResource(id = R.string.txt_micro_scholarship)
    var reportPerformingList by remember { mutableStateOf(mutableListOf<ReportsPerformingResponse.ReportsPerformingData>()) }
    var monthlyReport by remember { mutableStateOf<MonthlyReport?>(null) }
    var badgeAwardReportData by remember { mutableStateOf<BadgeAwardData?>(null) }
    var badgeReport by remember { mutableStateOf<BadgeData?>(null) }
    var awardReport by remember { mutableStateOf<AwardData?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.reportPerformingResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        reportPerformingList = it.data.data.toMutableList()
                        println("Performing reports :- $reportPerformingList")
                    }
                }

                is NetworkStatus.Error -> {
                    context.toast(it.message)
                }
            }
        }

        viewModel.monthlyReportResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        if (it.data.data != null) {
                            monthlyReport = it.data.data
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    println("Performing Reports Data Error :-${it.message}")
                    context.toast(it.message)
                }
            }
        }

        viewModel.getBadgeAwardDataResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        if (it.data.data != null) {
                            badgeAwardReportData = it.data.data
                            println("Badge and Award Data :- $badgeAwardReportData")
                            if (badgeAwardReportData?.badge != null) {
                                badgeReport = badgeAwardReportData!!.badge
                                println("Badge Report Data :- $badgeReport")
                            } else {
                                println("Badge Report :- No badge yet")
                            }

                            if (badgeAwardReportData?.award != null) {
                                awardReport = badgeAwardReportData!!.award
                                println("Award Report Data :- $awardReport")
                            } else {
                                println("Award Report :- No award yet")
                            }
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    context.toast(it.message)
                }
            }
        }

        viewModel.getReportsPerformingData()
        viewModel.getMonthlyReportDataData()
        viewModel.getBadgeAwardDataReport()
    }

    var monthlyScore: String = ""
    if (monthlyReport != null) {
        val num = monthlyReport!!.monthScore
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        monthlyScore = df.format(num)
    } else {
        monthlyScore = "0"
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBarPassport(languageData, openDrawMenuItem)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 20.dp, end = 16.dp),
                text = if (languageData[LanguageTranslationsResponse.ACTIVITY_MONTH].toString() == "") {
                    stringResource(id = R.string.txt_monthly_activity)
                } else {
                    languageData[LanguageTranslationsResponse.ACTIVITY_MONTH].toString()
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(
                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                    ),
                )
            )

            Card(shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardColors(
                    containerColor = White,
                    contentColor = White,
                    disabledContentColor = White,
                    disabledContainerColor = White
                ),
                border = selectedBorder,
                onClick = {
//                languageIndex.isSelected = true
//                onItemClicked(true)
                }) {
                Column(
                    modifier = Modifier.padding(
                        start = 5.dp, end = 5.dp, top = 5.dp, bottom = 15.dp
                    ), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier.padding(horizontal = 5.dp)) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(horizontal = 5.dp, vertical = 10.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .weight(1f),
                            colors = CardColors(
                                containerColor = Bg_Gray,
                                contentColor = White,
                                disabledContentColor = White,
                                disabledContainerColor = White
                            ),
                            border = selectedBorder,
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(
                                    modifier = Modifier.padding(start = 5.dp),
                                ) {
                                    Text(
                                        text = if (monthlyReport != null) {
                                            "${monthlyReport!!.coreQuizes.toString()}/${monthlyReport!!.totalCoreQuizes.toString()}"
                                        } else {
                                            "0/0"
                                        },
                                        textAlign = TextAlign.Start,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_bold, FontWeight.Bold)
                                        ),
                                        color = Black,
                                        modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                                    )

                                    Text(
                                        coreQuiz,
                                        textAlign = TextAlign.Start,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_medium, FontWeight.Medium)
                                        ),
                                        color = Gray,
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .padding(bottom = 10.dp, start = 10.dp)
                                    )

                                    if (monthlyReport != null) {
                                        val num: Float =
                                            (monthlyReport?.coreQuizes!!.toFloat() / monthlyReport?.totalCoreQuizes!!.toFloat())
                                        println("Divided total core quizes data are :- $num")
                                        CustomHorizontalProgressBar(num)
                                    } else {
                                        CustomHorizontalProgressBar(0.0f)
                                    }
                                }
                            }
                        }
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(horizontal = 5.dp, vertical = 10.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .weight(1f),
                            colors = CardColors(
                                containerColor = Bg_Gray,
                                contentColor = White,
                                disabledContentColor = White,
                                disabledContainerColor = White
                            ),
                            border = selectedBorder,
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(
                                    modifier = Modifier.padding(start = 5.dp),
                                ) {
                                    Text(
                                        text = if (monthlyReport != null) {
                                            "${monthlyReport!!.retakeQuizes.toString()}/${monthlyReport!!.totalRetakeQuizes.toString()}"
                                        } else {
                                            "0/0"
                                        },
                                        textAlign = TextAlign.Start,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_bold, FontWeight.Bold)
                                        ),
                                        color = Black,
                                        modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                                    )
                                    Text(
                                        txtRetakeQuiz,
                                        textAlign = TextAlign.Start,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_medium, FontWeight.Medium)
                                        ),
                                        color = Gray,
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .padding(bottom = 10.dp, start = 10.dp)
                                    )

                                    if (monthlyReport != null) {
                                        val num: Float = (monthlyReport?.retakeQuizes!!.toFloat()
                                            .div(monthlyReport?.totalRetakeQuizes!!.toFloat()))
                                        println("Divided total retake quizes data are :- $num")
                                        CustomHorizontalProgressBar(num)
                                    } else {
                                        CustomHorizontalProgressBar(0.0f)
                                    }
                                }
                            }
                        }
                    }

                    Row(modifier = Modifier.padding(horizontal = 5.dp)) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(horizontal = 5.dp, vertical = 10.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .weight(1f),
                            colors = CardColors(
                                containerColor = Bg_Gray,
                                contentColor = White,
                                disabledContentColor = White,
                                disabledContainerColor = White
                            ),
                            border = selectedBorder,
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(
                                    modifier = Modifier.padding(start = 5.dp),
                                ) {
                                    Text(
                                        text = if (monthlyReport != null) {
                                            "${monthlyReport!!.practiceQuizes.toString()}/${monthlyReport!!.totalPracticeQuizes.toString()}"
                                        } else {
                                            "0/0"
                                        },
                                        textAlign = TextAlign.Start,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_bold, FontWeight.Bold)
                                        ),
                                        color = Black,
                                        modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                                    )
                                    Text(
                                        practiceQuiz,
                                        textAlign = TextAlign.Start,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_medium, FontWeight.Medium)
                                        ),
                                        color = Gray,
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .padding(bottom = 10.dp, start = 10.dp)
                                    )

                                    if (monthlyReport != null) {
                                        val num: Float = (monthlyReport?.practiceQuizes!!.toFloat()
                                            .div(monthlyReport?.totalPracticeQuizes!!.toFloat()))
                                        println("Divided total practice quizes data are :- $num")
                                        CustomHorizontalProgressBar(num)
                                    } else {
                                        CustomHorizontalProgressBar(0.0f)
                                    }
                                }
                            }
                        }
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(horizontal = 5.dp, vertical = 10.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .weight(1f),
                            colors = CardColors(
                                containerColor = Bg_Gray,
                                contentColor = White,
                                disabledContentColor = White,
                                disabledContainerColor = White
                            ),
                            border = selectedBorder,
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(
                                    modifier = Modifier.padding(start = 5.dp),
                                ) {
                                    Text(
                                        text = if (monthlyReport != null) {
                                            "${monthlyReport!!.microscholarshipQuizes.toString()}/${monthlyReport!!.totalmicroscholarshipQuizes.toString()}"
                                        } else {
                                            "0/0"
                                        },
                                        textAlign = TextAlign.Start,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_bold, FontWeight.Bold)
                                        ),
                                        color = Black,
                                        modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                                    )
                                    Text(
                                        microScholarship,
                                        textAlign = TextAlign.Start,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_medium, FontWeight.Medium)
                                        ),
                                        color = Gray,
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .padding(bottom = 10.dp, start = 10.dp)
                                    )

                                    if (monthlyReport != null) {
                                        val num: Float =
                                            (monthlyReport?.microscholarshipQuizes!!.toFloat()
                                                .div(monthlyReport?.totalmicroscholarshipQuizes!!.toFloat()))
                                        println("Divided total micro scholarship quizes data are :- $num")
                                        CustomHorizontalProgressBar(num)
                                    } else {
                                        CustomHorizontalProgressBar(0.0f)
                                    }
                                }
                            }
                        }
                    }
                    TextWithIconOnRight(if (languageData[LanguageTranslationsResponse.ACHIEVE].toString() == "") {
                        stringResource(id = R.string.txt_how_to_achieve)
                    } else {
                        languageData[LanguageTranslationsResponse.ACHIEVE].toString()
                    },
                        icon = ImageVector.vectorResource(id = R.drawable.ic_right_side),
                        textColor = PrimaryBlue,
                        iconColor = PrimaryBlue,
                        modifier = Modifier.padding(bottom = 10.dp, top = 20.dp),
                        onClick = {
                            val intent =
                                Intent(context, StudentUnlockAwardsActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.also {
                                it.putExtra("", true)
                            }
                            context.startActivity(intent)
//                        navHostController.navigateUp()
                        })
                }
            }

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 10.dp, end = 16.dp),
                text = if (languageData[LanguageTranslationsResponse.LDRBOARD].toString() == "") {
                    stringResource(id = R.string.txt_leaderboard)
                } else {
                    languageData[LanguageTranslationsResponse.LDRBOARD].toString()
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(
                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                    ),
                    textAlign = TextAlign.Start
                )
            )
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp, top = 10.dp)
                    .wrapContentHeight(),
                colors = CardColors(
                    containerColor = White,
                    contentColor = White,
                    disabledContentColor = White,
                    disabledContainerColor = White
                ),
                border = selectedBorder,
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = 5.dp, end = 5.dp, top = 5.dp, bottom = 15.dp
                    ), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier.padding(horizontal = 5.dp)) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(horizontal = 5.dp, vertical = 10.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .weight(1f),
                            colors = CardColors(
                                containerColor = White,
                                contentColor = White,
                                disabledContentColor = White,
                                disabledContainerColor = White
                            ),
                            border = selectedBorder,
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.score_icon),
                                    contentDescription = "logo",
                                    modifier = Modifier
                                        .size(36.dp) // Add size modifier to make the image visible
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(color = Transparent)// Add clip modifier to make the image circular
                                        .border( // Add border modifier to make image stand out
                                            width = 1.dp, color = LightPink02, shape = CircleShape
                                        )
                                )
                                Column(
                                    modifier = Modifier.padding(start = 5.dp),
                                ) {

                                    Text(
                                        text = monthlyScore,
                                        textAlign = TextAlign.Start,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_bold, FontWeight.Bold)
                                        ),
                                        color = Black,
                                        modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                                    )
                                    Text(
                                        text = if (languageData[LanguageTranslationsResponse.KEY_SCORE].toString() == "") {
                                            "Score"
                                        } else {
                                            languageData[LanguageTranslationsResponse.KEY_SCORE].toString()
                                        },
                                        textAlign = TextAlign.Start,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_medium, FontWeight.Medium)
                                        ),
                                        color = Gray,
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .padding(bottom = 10.dp, start = 10.dp)
                                    )
                                }
                            }
                        }
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(horizontal = 5.dp, vertical = 10.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .weight(1f),
                            colors = CardColors(
                                containerColor = White,
                                contentColor = White,
                                disabledContentColor = White,
                                disabledContainerColor = White
                            ),
                            border = selectedBorder,
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.icon_quiz_score),
                                    contentDescription = "logo",
                                    modifier = Modifier
                                        .size(36.dp) // Add size modifier to make the image visible
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(color = Transparent)// Add clip modifier to make the image circular
                                        .border( // Add border modifier to make image stand out
                                            width = 1.dp, color = LightPink02, shape = CircleShape
                                        )
                                )
                                Column(
                                    modifier = Modifier.padding(start = 5.dp),
                                ) {
                                    Text(
                                        text = if (monthlyReport != null) {
                                            "#${monthlyReport!!.rank.toString()}"
                                        } else {
                                            "#0"
                                        },
                                        textAlign = TextAlign.Start,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_bold, FontWeight.Bold)
                                        ),
                                        color = Black,
                                        modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                                    )
                                    Text(
                                        "Rank",
                                        textAlign = TextAlign.Start,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_medium, FontWeight.Medium)
                                        ),
                                        color = Gray,
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .padding(bottom = 10.dp, start = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                    TextWithIconOnRight(stringResource(id = R.string.txt_more_details),
                        icon = ImageVector.vectorResource(id = R.drawable.ic_right_side),
                        textColor = PrimaryBlue,
                        iconColor = PrimaryBlue,
                        modifier = Modifier.padding(bottom = 10.dp, top = 20.dp),
                        onClick = {
//                            navHostController.popBackStack()
                            val intent = Intent(context, StudentLeaderboardActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.also {
                                it.putExtra("", true)
                            }
                            context.startActivity(intent)
//
                        })
                }
            }

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 10.dp, end = 16.dp),
                text = stringResource(id = R.string.txt_badges_awards),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(
                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                    ),
                    textAlign = TextAlign.Start
                )
            )

            Row(
                modifier = Modifier.padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 10.dp)
                        .fillMaxWidth()
                        .height(260.dp)
                        .weight(1f),
                    colors = CardColors(
                        containerColor = White,
                        contentColor = White,
                        disabledContentColor = White,
                        disabledContainerColor = White
                    ),
                    border = selectedBorder,
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = if (badgeReport != null) {
                                rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(badgeReport!!.imageUrl.toString())
                                        .decoderFactory(SvgDecoder.Factory()) // Adds SVG support
                                        .size(Size.ORIGINAL) // Use original or specify size
                                        .placeholder(R.drawable.no_badges)
                                        .error(R.drawable.no_badges)
                                        .build()
                                )
//                                rememberImagePainter(badgeReport!!.imageUrl.toString())
                            } else {
                                painterResource(R.drawable.no_badges)
                            },
//                            painter = painterResource(R.drawable.no_badges),
                            contentDescription = "logo",
                            alignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp) // Add size modifier to make the image visible
                                .clip(RoundedCornerShape(100.dp))
                                .background(color = Transparent)// Add clip modifier to make the image circular
                                .border( // Add border modifier to make image stand out
                                    width = 1.dp, color = LightPink02, shape = CircleShape
                                )
                        )
                        Text(
                            text = if (badgeReport != null) {
                                badgeReport!!.name.toString()
                            } else {
                                if (languageData[LanguageTranslationsResponse.NULL_BADGE].toString() == "") {
                                    "No Badge"
                                } else {
                                    languageData[LanguageTranslationsResponse.NULL_BADGE].toString()
                                }
                            },
                            textAlign = TextAlign.Center,
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily(
                                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                            ),
                            color = Black,
                            modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                        )
                        Text(
//                            text = "Badges reward your dedication and performance. Collect and let them inspire you to achieve more.",
                            text = if (badgeReport != null) {
                                badgeReport!!.description.toString()
                            } else {
                                if (languageData[LanguageTranslationsResponse.BADGES_REWARD_DEDICATION].toString() == "") {
                                    "Badges reward your dedication and performance. Collect and let them inspire you to achieve more."
                                } else {
                                    languageData[LanguageTranslationsResponse.BADGES_REWARD_DEDICATION].toString()
                                }
                            },
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp,
                            fontFamily = FontFamily(
                                Font(R.font.inter_regular, FontWeight.Normal)
                            ),
                            color = Gray,
                            modifier = Modifier.size(200.dp, 115.dp)
//                            modifier = Modifier.wrapContentWidth()
                        )
                        TextWithIconOnRight(text = if (badgeReport != null) {
                            stringResource(id = R.string.txt_view_all)
                        } else {
                            if (languageData[LanguageTranslationsResponse.HOW_TO_UNLOCK_BADGES].toString() == "") {
                                "How to unlock badges"
                            } else {
                                languageData[LanguageTranslationsResponse.HOW_TO_UNLOCK_BADGES].toString()
                            }
                        },
                            icon = ImageVector.vectorResource(id = R.drawable.ic_right_side),
                            textColor = PrimaryBlue,
                            iconColor = PrimaryBlue,
                            modifier = Modifier.padding(bottom = 10.dp, top = 20.dp),
                            onClick = {
                                if (badgeReport != null) {
                                    val intent =
                                        Intent(context, BadgesAndAwardsActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.also {
                                        it.putExtra("", true)
                                    }
                                    context.startActivity(intent)
                                } else {
                                    val intent =
                                        Intent(context, StudentUnlockAwardsActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.also {
                                        it.putExtra("", true)
                                    }
                                    context.startActivity(intent)
                                }
                            })
                    }
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 10.dp)
                        .fillMaxWidth()
                        .height(260.dp)
                        .weight(1f),
                    colors = CardColors(
                        containerColor = White,
                        contentColor = White,
                        disabledContentColor = White,
                        disabledContainerColor = White
                    ),
                    border = selectedBorder,
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = if (awardReport != null) {
                                rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(awardReport!!.imageUrl.toString())
                                        .decoderFactory(SvgDecoder.Factory()) // Adds SVG support
                                        .size(Size.ORIGINAL) // Use original or specify size
                                        .placeholder(R.drawable.no_badges)
                                        .error(R.drawable.no_badges)
                                        .build()
                                )
//                                rememberImagePainter(awardReport!!.imageUrl.toString())
                            } else {
                                painterResource(R.drawable.no_badges)
                            },/* painter = painterResource (R.drawable.no_badges),*/
                            contentDescription = "logo",
                            alignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp) // Add size modifier to make the image visible
                                .clip(RoundedCornerShape(100.dp))
                                .background(color = Transparent)// Add clip modifier to make the image circular
                                .border( // Add border modifier to make image stand out
                                    width = 1.dp, color = LightPink02, shape = CircleShape
                                )
                        )
                        Text(
                            text = if (awardReport != null) {
                                awardReport!!.name.toString()
                            } else {
                                "No Award"
                            },
                            textAlign = TextAlign.Center,
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily(
                                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                            ),
                            color = Black,
                            modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                        )
                        Text(
//                            text = "Win awards to celebrate your exceptional achievements in your State ,District and School.",
                            text = if (awardReport != null) {
                                awardReport!!.description.toString()
                            } else {
                                if (languageData[LanguageTranslationsResponse.WIN_AWARDS].toString() == "") {
                                    "Win awards to celebrate your exceptional achievements in your State ,District and School."
                                } else {
                                    languageData[LanguageTranslationsResponse.WIN_AWARDS].toString()
                                }
                            },
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp,
                            fontFamily = FontFamily(
                                Font(R.font.inter_regular, FontWeight.Normal)
                            ),
                            color = Gray,
                            modifier = if (awardReport != null) {
                                Modifier.size(200.dp, 90.dp)
                            } else {
                                Modifier.size(200.dp, 115.dp)
                            }
//                            Modifier.size(200.dp, 90.dp)
//                            modifier = Modifier.wrapContentWidth()
                        )
                        TextWithIconOnRight(text = if (awardReport != null) {
                            stringResource(id = R.string.txt_view_all)
                        } else {
                            "How to unlock Awards"
                        },
                            icon = ImageVector.vectorResource(id = R.drawable.ic_right_side),
                            textColor = PrimaryBlue,
                            iconColor = PrimaryBlue,
                            modifier = Modifier.padding(bottom = 10.dp, top = 20.dp),
                            onClick = {
                                if (awardReport != null) {
                                    val intent =
                                        Intent(context, BadgesAndAwardsActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.also {
                                        it.putExtra("", true)
                                    }
                                    context.startActivity(intent)
                                } else {
                                    val intent =
                                        Intent(context, StudentUnlockAwardsActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.also {
                                        it.putExtra("", true)
                                    }
                                    context.startActivity(intent)
                                }
                            })
                    }
                }
            }

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 10.dp, end = 16.dp),
                text = if (languageData[LanguageTranslationsResponse.REPORT].toString() == "") {
                    stringResource(id = R.string.txt_reports)
                } else {
                    languageData[LanguageTranslationsResponse.REPORT].toString()
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(
                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                    ),
                    textAlign = TextAlign.Start
                )
            )
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 10.dp)
                    .wrapContentHeight(),
                colors = CardColors(
                    containerColor = White,
                    contentColor = White,
                    disabledContentColor = White,
                    disabledContainerColor = White
                ),
                border = selectedBorder,
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = 5.dp, end = 5.dp, top = 5.dp, bottom = 10.dp
                    ), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier.padding(horizontal = 5.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.score_icon),
                                contentDescription = "logo",
                                modifier = Modifier
                                    .size(36.dp) // Add size modifier to make the image visible
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(color = Transparent)// Add clip modifier to make the image circular
                                    .border( // Add border modifier to make image stand out
                                        width = 1.dp, color = LightPink02, shape = CircleShape
                                    )
                            )
                            Column(
                                modifier = Modifier.padding(start = 5.dp),
                            ) {
                                Text(
                                    if (reportPerformingList.isNotEmpty()) {
                                        reportPerformingList[0].quizAttemptCount.toString()
                                    } else {
                                        "0"
                                    },
                                    textAlign = TextAlign.Start,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_bold, FontWeight.Bold)
                                    ),
                                    color = Black,
                                    modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                                )
                                Text(
                                    "Quiz Attempts",
                                    textAlign = TextAlign.Start,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_regular, FontWeight.Normal)
                                    ),
                                    color = Gray,
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(bottom = 5.dp, start = 10.dp)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.icon_top_performing),
                                contentDescription = "logo",
                                modifier = Modifier
                                    .size(36.dp) // Add size modifier to make the image visible
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(color = Transparent)// Add clip modifier to make the image circular
                                    .border( // Add border modifier to make image stand out
                                        width = 1.dp, color = LightPink02, shape = CircleShape
                                    )
                            )
                            Column(
                                modifier = Modifier.padding(start = 5.dp),
                            ) {
                                Text(
                                    if (reportPerformingList.isNotEmpty()) {
                                        "#${reportPerformingList[0].topPerformingTopicCount.toString()}"
                                    } else {
                                        "#50"
                                    },
                                    textAlign = TextAlign.Start,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_bold, FontWeight.Bold)
                                    ),
                                    color = Black,
                                    modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                                )
                                Text(
                                    if (languageData[LanguageTranslationsResponse.TOP_PERFORMING_TOPICS].toString() == "") {
                                        "top performing topics"
                                    } else {
                                        languageData[LanguageTranslationsResponse.TOP_PERFORMING_TOPICS].toString()
                                    },
                                    textAlign = TextAlign.Start,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_regular, FontWeight.Normal)
                                    ),
                                    color = Gray,
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(bottom = 5.dp, start = 10.dp)
                                )
                            }
                        }
                    }

                    Row(modifier = Modifier.padding(start = 5.dp, end = 10.dp)) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.icon_weak_performing),
                                contentDescription = "logo",
                                modifier = Modifier
                                    .size(36.dp) // Add size modifier to make the image visible
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(color = Transparent)// Add clip modifier to make the image circular
                                    .border( // Add border modifier to make image stand out
                                        width = 1.dp, color = LightPink02, shape = CircleShape
                                    )
                            )
                            Column(
                                modifier = Modifier.padding(start = 5.dp),
                            ) {
                                Text(
                                    if (reportPerformingList.isNotEmpty()) {
                                        reportPerformingList[0].weakPerformingTopicCount.toString()
                                    } else {
                                        "500"
                                    },
                                    textAlign = TextAlign.Start,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_bold, FontWeight.Bold)
                                    ),
                                    color = Black,
                                    modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                                )
                                Text(
                                    text = if (languageData[LanguageTranslationsResponse.WEAK_PERFORMING_TOPICS].toString() == "") {
                                        "Weak performing topics"
                                    } else {
                                        languageData[LanguageTranslationsResponse.WEAK_PERFORMING_TOPICS].toString()
                                    },
                                    textAlign = TextAlign.Start,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_regular, FontWeight.Normal)
                                    ),
                                    color = Gray,
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(bottom = 10.dp, start = 10.dp)
                                )
                            }
                        }
                        TextWithIconOnRight(stringResource(id = R.string.txt_more_details),
                            icon = ImageVector.vectorResource(id = R.drawable.ic_right_side),
                            textColor = PrimaryBlue,
                            iconColor = PrimaryBlue,
                            modifier = Modifier.padding(bottom = 10.dp, top = 20.dp),
                            onClick = {
//                                Toast.makeText(context, "Coming soon!!", Toast.LENGTH_SHORT).show()
                                val intent =
                                    Intent(context, StudentPassportRouteActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.also {
                                    it.putExtra("", true)
                                }
                                context.startActivity(intent)
                            })
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopBarPassport(languageData: HashMap<String, String>, openDrawMenuItem: () -> Unit) {
    val context: Context = LocalContext.current

    TopAppBar(title = {
        Row(
            modifier = Modifier.height(80.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.text_passport),
                fontSize = 18.sp,
                fontFamily = FontFamily(
                    Font(R.font.inter_bold, FontWeight.Bold)
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(painter = painterResource(id = R.drawable.ic_passport_help), // Replace with your drawable resource
                contentDescription = null, // Provide a description for accessibility purposes
                modifier = Modifier
                    .wrapContentSize()
                    .clickable {
                        val intent = Intent(context, StudentUnlockAwardsActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.also {
                            it.putExtra("", true)
                        }
                        context.startActivity(intent)
                    })
        }
    },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        modifier = Modifier
            .background(Transparent)
            .zIndex(1f),
        navigationIcon = {
            IconButton(onClick = {
                openDrawMenuItem.invoke()
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Toggle drawer",
                    tint = Black
                )
            }
        })
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun ShowPassportUI() {
    AuroscholarAppTheme {
        val context = LocalContext.current
        val selectedBorder = BorderStroke(width = 0.5.dp, GrayLight02)
        StudentPassportHomeScreen(innerPadding = PaddingValues(), context = context, {})
//        StudentHomePage(navController = rememberNavController(), hiltViewModel())
    }
}
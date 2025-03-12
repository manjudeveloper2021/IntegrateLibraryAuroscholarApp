package com.auro.application.ui.features.student.passport

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.data.api.NetworkStatus
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.components.TextWithIconOnRight
import com.auro.application.ui.common_ui.components.monthlyFilterBottomSheet
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.menu.TabItem
import com.auro.application.ui.features.student.passport.models.LeaderboardDataResponse
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.BlueBackground
import com.auro.application.ui.theme.DarkBlue
import com.auro.application.ui.theme.FullTransparent
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class StudentLeaderboardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuroscholarAppTheme {
                val context = LocalContext.current
                var navHostController: NavHostController = rememberNavController()

                val viewModels: LoginViewModel = hiltViewModel()
                val languageListName = stringResource(id = R.string.key_lang_list)
                var languageData = HashMap<String, String>()
                languageData = viewModels.getLanguageTranslationData(languageListName)

                val filterTextHint = stringResource(id = R.string.txt_monthly)
                var showFilterSheet by remember { mutableStateOf(false) }
                var filterValue by remember { mutableStateOf(filterTextHint) }
                var filterTypeId by remember { mutableStateOf(1) }
                if (showFilterSheet) {
                    monthlyFilterBottomSheet(filterValue = filterValue,
                        onValueSelected = { selectedValue ->
                            filterValue = selectedValue
                            Log.d("ParentComposible", "Selected Value: $filterValue")
                            // You can now use selectedValue in another class
                        }) {
                        showFilterSheet = false
                        if (filterValue == "Monthly") {
                            filterTypeId = 1
                        } else if (filterValue == "Yearly") {
                            filterTypeId = 2
                        } else if (filterValue == "Lifetime") {
                            filterTypeId = 3
                        } else {
                            filterTypeId = 1
                        }
//                        println("Filtered data :- $filterValue, filter id :- $filterTypeId")
                    }
                }

                // tabs for location
                val tabItems = listOf(
                    TabItem(
                        title = stringResource(id = R.string.txt_all_india)
                    ), TabItem(
                        title = if (languageData[LanguageTranslationsResponse.STATE].toString() == "") {
                            stringResource(id = R.string.txt_state)
                        } else {
                            languageData[LanguageTranslationsResponse.STATE].toString()
                        }
                    ), TabItem(
                        title = stringResource(id = R.string.txt_district)
                    ), TabItem(
                        title = if (languageData[LanguageTranslationsResponse.SCHOOL].toString() == "") {
                            stringResource(id = R.string.txt_school)
                        } else {
                            languageData[LanguageTranslationsResponse.SCHOOL].toString()
                        }
                    )
                )

                var selectedTabIndex by remember { mutableIntStateOf(0) }
                val pagerState = rememberPagerState { tabItems.size }
                LaunchedEffect(selectedTabIndex) {
                    // if tab selected then scroll the content
                    pagerState.animateScrollToPage(selectedTabIndex)
                }

                BackHandler {
                    navHostController.popBackStack()
                    ContextCompat.startActivity(
                        context, Intent(context, StudentDashboardActivity::class.java).apply {
                            (context as Activity).finish()
                        }, null
                    )
                }

                LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
                    // if scrolling the content change the tab as well
                    if (!pagerState.isScrollInProgress) {
                        selectedTabIndex = pagerState.currentPage
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    Surface(
                        tonalElevation = 10.dp, // Set the elevation here
                        color = White,
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.back_icon),
                                contentDescription = "logo",
                                modifier = Modifier.clickable {
//                                    navHostController.popBackStack()
                                    val intent =
                                        Intent(context, StudentDashboardActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.also {
                                        it.putExtra("", true)
                                    }
                                    context.startActivity(intent)
                                },
                                colorFilter = ColorFilter.tint(Black)
                            )

                            Text(
                                text = if (languageData[LanguageTranslationsResponse.LDRBOARD].toString() == "") {
                                    stringResource(id = R.string.txt_leaderboard)
                                } else {
                                    languageData[LanguageTranslationsResponse.LDRBOARD].toString()
                                },
                                modifier = Modifier
                                    .wrapContentSize()
                                    .weight(1f),
                                fontFamily = FontFamily(
                                    Font(R.font.inter_bold, FontWeight.Bold)
                                ),
                                fontSize = 18.sp,
                                color = Black,
                                textAlign = TextAlign.Start
                            )

                            TextWithIconOnRight(filterValue,
                                icon = ImageVector.vectorResource(id = R.drawable.ic_down),
                                textColor = Black,
                                iconColor = GrayLight01,
                                modifier = Modifier.padding(end = 5.dp),
                                onClick = {
                                    showFilterSheet = true
                                })
                        }
                    }

                    var selectedTabItemId by remember { mutableStateOf(1) }
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        selectedTabItemId = selectedTabIndex + 1
                        tabItems.forEachIndexed { index, tabItem ->
                            Tab(selected = index == selectedTabIndex, onClick = {
                                selectedTabIndex = index
                            }, modifier = Modifier.background(White), text = {
                                Text(
                                    text = tabItem.title,
                                    modifier = Modifier.padding(vertical = 5.dp),
                                    color = if (index == selectedTabIndex) PrimaryBlue else GrayLight01,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                    ),
                                    fontSize = 14.sp
                                )
                            })
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        HorizontalPager(
                            state = pagerState, modifier = Modifier.fillMaxSize()

                        ) { index ->
                            when (index) {
                                0 -> {
                                    println("Filtered data :- $filterValue, filter id :- $filterTypeId")
                                    LeaderboardList(selectedTabItemId, filterTypeId, languageData)
                                }

                                1 -> {
                                    println("Filtered data :- $filterValue, filter id :- $filterTypeId")
                                    LeaderboardList(selectedTabItemId, filterTypeId, languageData)
                                }

                                2 -> {
                                    println("Filtered data :- $filterValue, filter id :- $filterTypeId")
                                    LeaderboardList(selectedTabItemId, filterTypeId, languageData)
                                }

                                3 -> {
                                    println("Filtered data :- $filterValue, filter id :- $filterTypeId")
                                    LeaderboardList(selectedTabItemId, filterTypeId, languageData)
                                }

                                /*else -> {
                                    LeaderboardList(selectedTabItemId, filterTypeId)
                                }*/
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardList(tabCount: Int, monthCount: Int, languageData: HashMap<String, String>) {
    val viewModel: StudentViewModel = hiltViewModel()
    val languageState by viewModel.languageResponse.collectAsState()
    val networkStatus = languageState
    val navController = rememberNavController()
    val context: Context = LocalContext.current
    var leaderboardDataList by remember { mutableStateOf(mutableListOf<LeaderboardDataResponse.LeaderboardData.Leaderboard>()) }
    var leaderboardData by remember { mutableStateOf<LeaderboardDataResponse.LeaderboardData?>(null) }

    val strCurrentMonthYear: String = getCurrentMonthYear()
    println("Current Year with month :- $strCurrentMonthYear")

    println("Selected tab id :- $tabCount, and time data id :- $monthCount")
    viewModel.getLeaderboardReport(
        tabCount.toString(), monthCount.toString(), strCurrentMonthYear, 1, 6
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.leaderboardResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        leaderboardData = it.data.data
                        leaderboardDataList =
                            it.data.data!!.leaderboard as MutableList<LeaderboardDataResponse.LeaderboardData.Leaderboard>
                        println("Leaderboard reports data :- $leaderboardData, $leaderboardDataList")
                        println("Student Name :- ${leaderboardData?.userData!!.studentName}")
                    }
                }

                is NetworkStatus.Error -> {
                    context.toast(it.message)
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(top = 10.dp, bottom = 10.dp)
    ) {
        item {
            Column(
                modifier = Modifier.wrapContentSize()
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.leaderboard_bg),
                        contentDescription = null,
                        modifier = Modifier
                            .height(135.dp)
                            .fillMaxWidth()
                    )
                    Column(
                        modifier = Modifier
                            .height(135.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 10.dp)

                    ) {
                        Row(
                            modifier = Modifier.wrapContentSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_profile),
                                    contentDescription = "logo",
                                    modifier = Modifier
                                        .size(35.dp) // Add size modifier to make the image visible
                                        .clip(RoundedCornerShape(100.dp)) // Add clip modifier to make the image circular
                                        .background(color = White)
                                        .border( // Add border modifier to make image stand out
                                            width = 1.dp, color = GrayLight02, shape = CircleShape
                                        )
                                        .padding(10.dp)
                                )
                            }
                            Text(
                                text = if (leaderboardData?.userData != null) {
                                    leaderboardData?.userData!!.studentName.toString()
                                } else {
                                    "You (You)"
                                }, style = MaterialTheme.typography.bodyMedium.copy(
                                    color = White, fontSize = 14.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_bold, FontWeight.Bold)
                                    ),
                                )
                            )
                        }

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp, top = 15.dp)
                                .fillMaxWidth(),
                            colors = CardColors(
                                containerColor = BlueBackground,
                                contentColor = BlueBackground,
                                disabledContentColor = BlueBackground,
                                disabledContainerColor = BlueBackground
                            ),
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 10.dp, horizontal = 15.dp)
                                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .weight(1f),
                                ) {
                                    Text(
                                        text = if (leaderboardData?.userData != null) {
                                            "#${leaderboardData?.userData!!.rank.toString()}"
                                        } else {
                                            "#0"
                                        },
                                        modifier = Modifier.wrapContentSize(),
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_bold, FontWeight.Bold)
                                        ),
                                        fontSize = 14.sp,
                                        color = White,
                                        textAlign = TextAlign.Start
                                    )
                                    Text(
                                        text = stringResource(id = R.string.txt_rank),
                                        modifier = Modifier.wrapContentSize(),
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_regular, FontWeight.Normal)
                                        ),
                                        fontSize = 10.sp,
                                        color = White,
                                        textAlign = TextAlign.Start
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .weight(1f),
                                ) {
                                    Text(
                                        text = if (leaderboardData?.userData != null) {
                                            leaderboardData?.userData!!.score.toString()
                                        } else {
                                            "0"
                                        },
                                        modifier = Modifier.wrapContentSize(),
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_bold, FontWeight.Bold)
                                        ),
                                        fontSize = 14.sp,
                                        color = White,
                                        textAlign = TextAlign.Start
                                    )
                                    Text(
                                        text = if (languageData[LanguageTranslationsResponse.KEY_SCORE].toString() == "") {
                                            stringResource(id = R.string.txt_score)
                                        } else {
                                            languageData[LanguageTranslationsResponse.KEY_SCORE].toString()
                                        },
                                        modifier = Modifier.wrapContentSize(),
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_regular, FontWeight.Normal)
                                        ),
                                        fontSize = 10.sp,
                                        color = White,
                                        textAlign = TextAlign.Start
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .weight(1f),
                                ) {
                                    Text(
                                        text = if (leaderboardData?.userData != null) {
                                            leaderboardData?.userData!!.badgeCount.toString()
                                        } else {
                                            "0"
                                        },
                                        modifier = Modifier.wrapContentSize(),
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_bold, FontWeight.Bold)
                                        ),
                                        fontSize = 14.sp,
                                        color = White,
                                        textAlign = TextAlign.Start
                                    )
                                    Text(
                                        text = stringResource(id = R.string.txt_badges),
                                        modifier = Modifier.wrapContentSize(),
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_regular, FontWeight.Normal)
                                        ),
                                        fontSize = 10.sp,
                                        color = White,
                                        textAlign = TextAlign.Start
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .weight(1f),
                                ) {
                                    Text(
                                        text = if (leaderboardData?.userData != null) {
                                            leaderboardData?.userData!!.awardData.toString()
                                        } else {
                                            "0"
                                        },
                                        modifier = Modifier.wrapContentSize(),
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_bold, FontWeight.Bold)
                                        ),
                                        fontSize = 14.sp,
                                        color = White,
                                        textAlign = TextAlign.Start
                                    )
                                    Text(
                                        text = stringResource(id = R.string.txt_awards),
                                        modifier = Modifier.wrapContentSize(),
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_regular, FontWeight.Normal)
                                        ),
                                        fontSize = 10.sp,
                                        color = White,
                                        textAlign = TextAlign.Start
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Subject List  using API
        val list = leaderboardDataList
        items(list) { leaderboardDataList ->
            StudentLeaderBoardItem(
                leaderboardData,
                leaderboardDataList, navController, index = list.indexOf(leaderboardDataList)
            )
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun getCurrentMonthYear(): String {
    val sdf = SimpleDateFormat("yyyyMM")
    val currentDateAndTime = sdf.format(Date())
    return currentDateAndTime.toString()
}

@Composable
fun StudentLeaderBoardItem(
    leaderboardData: LeaderboardDataResponse.LeaderboardData?,
    child: LeaderboardDataResponse.LeaderboardData.Leaderboard,
    navController: NavHostController,
    index: Int
) {
    Log.d("studentIndex: ", "" + index)
    var rank by remember { mutableIntStateOf(0) }
    rank = index + 1
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (leaderboardData?.userData?.studentName.toString() == child.studentName) {
                    DarkBlue
                } else {
                    FullTransparent
                }
                /* when (index) {
                     2 -> GrayLight03
                     4 -> DarkBlue
                     else -> FullTransparent
                 }*/
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${child.rank.toString()}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (leaderboardData?.userData?.studentName.toString() == child.studentName) {
                        White
                    } else {
                        Gray
                    }
                    /*(when (index) {
                        2 -> Black
                        4 -> White
                        else -> Gray
                    })*/,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    ),
                    textAlign = TextAlign.Start
                )
            )
            Image(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(52.dp),
                painter = painterResource(R.drawable.ic_parent),
                contentDescription = "Logo"
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
            ) {
                Text(
                    text = child.studentName.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (leaderboardData?.userData?.studentName.toString() == child.studentName) {
                            White
                        } else {
                            Gray
                        },
                        fontSize = 12.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_medium, FontWeight.Medium)
                        ),
                        textAlign = TextAlign.Start
                    )
                )

                Text(
                    text = "Award - ${child.awardData.toString()}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (leaderboardData?.userData?.studentName.toString() == child.studentName) {
                            White
                        } else {
                            Gray
                        },
                        fontSize = 10.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
                        ),
                        textAlign = TextAlign.Start
                    )
                )
            }
            Image(
                painter = painterResource(R.drawable.score_achievement_icon),
                contentDescription = "sdsds",
                modifier = Modifier
                    .size(25.dp)
                    .padding(end = 5.dp)
            )
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(5.dp)
            ) {
                Text(
                    text = child.score.toString(), style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (leaderboardData?.userData?.studentName.toString() == child.studentName) {
                            White
                        } else {
                            Gray
                        },
                        fontSize = 12.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_medium, FontWeight.Medium)
                        ),
                        textAlign = TextAlign.Start
                    )
                )

                Text(
                    text = "Score", style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (leaderboardData?.userData?.studentName.toString() == child.studentName) {
                            White
                        } else {
                            Gray
                        },
                        fontSize = 10.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
                        ),
                        textAlign = TextAlign.Start
                    )
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(0.8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    color = GrayLight02,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 0.8.dp.toPx()
                )
            }
        }
    }
}

/*
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun StudentLeaderboardPreview() {
    LeaderboardList(1, 1)
}*/

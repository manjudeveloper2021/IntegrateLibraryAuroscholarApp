package com.auro.application.ui.features.student.passport

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.data.api.NetworkStatus
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.components.TextWithIconOnRight
import com.auro.application.ui.common_ui.components.monthlyFilterBottomSheet
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.menu.TabItem
import com.auro.application.ui.features.student.passport.models.AllAwardData
import com.auro.application.ui.features.student.passport.models.BadgesListData
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.GreenDark02
import com.auro.application.ui.theme.LightPink02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.Transparent
import com.auro.application.ui.theme.White
import com.auro.application.ui.theme.Yellow
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class BadgesAndAwardsActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuroscholarAppTheme {
                val context: Context = LocalContext.current
                var navHostController: NavHostController = rememberNavController()

                val viewModels: LoginViewModel = hiltViewModel()
                val languageListName = stringResource(id = R.string.key_lang_list)
                var languageData = HashMap<String, String>()
                languageData = viewModels.getLanguageTranslationData(languageListName)

                // tabs for Unlock awards
                val tabItems = listOf(
                    TabItem(
                        title = stringResource(id = R.string.txt_badges)
                    ),
                    TabItem(
                        title = stringResource(id = R.string.txt_awards)
                    ),
                )
                var selectedTabIndex by remember { mutableIntStateOf(0) }
                val pagerState = rememberPagerState { tabItems.size }

                BackHandler {
                    ContextCompat.startActivity(
                        context, Intent(context, StudentDashboardActivity::class.java).apply {
                            (context as Activity).finish()
                        }, null
                    )
                }

                LaunchedEffect(selectedTabIndex) {
                    // if tab selected then scroll the content
                    pagerState.animateScrollToPage(selectedTabIndex)
                }

                LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
                    // if scrolling the content change the tab as well
                    if (!pagerState.isScrollInProgress) {
                        selectedTabIndex = pagerState.currentPage
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(White)
                ) {
                    Surface(
                        tonalElevation = 10.dp, // Set the elevation here
                        color = White,
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 10.dp)
                                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.back_icon),
                                contentDescription = "logo",
                                modifier = Modifier.clickable {
                                    navHostController.popBackStack()
                                    ContextCompat.startActivity(
                                        context,
                                        Intent(
                                            context,
                                            StudentDashboardActivity::class.java
                                        ).apply {
                                            (context as Activity).finish()
                                        },
                                        null
                                    )
                                },

                                colorFilter = ColorFilter.tint(Black)
                            )
                            Text(
                                text = stringResource(id = R.string.txt_badges_awards),
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(start = 16.dp),
                                fontFamily = FontFamily(
                                    Font(R.font.inter_bold, FontWeight.Bold)
                                ),
                                fontSize = 18.sp,
                                color = Black,
                                textAlign = TextAlign.Start
                            )
                            Image(
                                painter = painterResource(R.drawable.ic_passport_help),
                                contentDescription = "logo",
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val intent =
                                            Intent(context, StudentUnlockAwardsActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intent.also {
                                            it.putExtra("", true)
                                        }
                                        context.startActivity(intent)
                                    },
                                colorFilter = ColorFilter.tint(Black),
                                alignment = Alignment.TopEnd
                            )
                        }
                    }

                    TabRow(
                        selectedTabIndex = selectedTabIndex, Modifier.padding(16.dp)
                    ) {
                        tabItems.forEachIndexed { index, tabItem ->
                            Tab(selected = index == selectedTabIndex,
                                onClick = { selectedTabIndex = index },
                                modifier = Modifier.background(White),
                                text = {
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
                            state = pagerState, modifier = Modifier
                                .fillMaxSize()
                                .background(White)

                        ) { index ->
                            when (index) {
                                0 -> {
                                    UnBlockBadges(languageData)
                                }

                                1 -> {
                                    UnLockAwards(languageData)
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun UnBlockBadges(languageData: HashMap<String, String>) {

    val context: Context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()
    val languageState by viewModel.languageResponse.collectAsState()
    val networkStatus = languageState
    val navController = rememberNavController()

    var badgesDataList by remember { mutableStateOf(mutableListOf<BadgesListData.BadgesList>()) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getBadgesListDataResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        badgesDataList =
                            it.data.data?.badges as MutableList<BadgesListData.BadgesList>
                        println("Badges reports data :- $badgesDataList")
                    }
                }

                is NetworkStatus.Error -> {
                    context.toast(it.message)
                }
            }
        }

        viewModel.getBadgesListReport()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp, bottom = 10.dp)
    ) {
        val list = badgesDataList
        items(list) { badgesDataList ->
            UnBlockBadgesItem(
                badgesDataList,
                languageData,
                navController,
                index = list.indexOf(badgesDataList)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun UnLockAwards(languageData: HashMap<String, String>) {
    val filterTextHint = stringResource(id = R.string.txt_monthly)
    val filterLocationText = stringResource(id = R.string.txt_all_location)

    var showFilterSheet by remember { mutableStateOf(false) }
    var filterValue by remember { mutableStateOf(filterTextHint) }
    if (showFilterSheet) {
        filterValue = monthlyFilterBottomSheet(filterValue = filterValue,
            onValueSelected = { selectedValue ->
                filterValue = selectedValue
                Log.d("ParentComposible", "Selected Value: $filterValue")
                // You can now use selectedValue in another class
            }) {
            showFilterSheet = false
        }
    }

    var showFilterLocation by remember { mutableStateOf(false) }
    var filterValueLoc by remember { mutableStateOf(filterLocationText) }
    if (showFilterLocation) {
        filterValueLoc = monthlyFilterBottomSheet(filterValue = filterValue,
            onValueSelected = { selectedValue ->
                filterValue = selectedValue
                Log.d("ParentComposible", "Selected Value: $filterValue")
                // You can now use selectedValue in another class
            }) {
            showFilterLocation = false
        }
    }

    val context: Context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()
    val languageState by viewModel.languageResponse.collectAsState()
    val networkStatus = languageState
    val navController = rememberNavController()

    var awardDataList by remember { mutableStateOf(mutableListOf<AllAwardData.AwardsData>()) }


    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.allAwardsResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        if (it.data.data!!.awards != null) {
                            awardDataList =
                                it.data.data!!.awards as MutableList<AllAwardData.AwardsData>
                        } else {
                            println("Awards reports data not found!!")
                        }
                        println("Awards reports data :- $awardDataList")
                    }
                }

                is NetworkStatus.Error -> {
                    context.toast(it.message)
                }
            }
        }

        viewModel.getAllAwardsReport("country", "yearly")
    }

    /* var isMonthlyEnabled by remember { mutableStateOf(true) }
     var isYearlyEnabled by remember { mutableStateOf(false) }*/

    Column(
        modifier = Modifier.padding(start = 5.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .border(
                        width = 0.8.dp, color = GrayLight02, shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                TextWithIconOnRight(filterValueLoc,
                    icon = ImageVector.vectorResource(id = R.drawable.ic_down),
                    textColor = Black,
                    iconColor = GrayLight01,
                    modifier = Modifier.padding(horizontal = 5.dp),
                    onClick = {
                        showFilterLocation = true
                    })
            }

            Column(
                modifier = Modifier
                    .border(
                        width = 0.8.dp, color = GrayLight02, shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                TextWithIconOnRight(filterValue,
                    icon = ImageVector.vectorResource(id = R.drawable.ic_down),
                    textColor = Black,
                    iconColor = GrayLight01,
                    modifier = Modifier.padding(horizontal = 5.dp),
                    onClick = {
                        showFilterSheet = true
                    })
            }

        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp, bottom = 10.dp)
        ) {
            val listAwards = awardDataList
            items(listAwards) { awardDataList ->
                UnLockAwardsItem(
                    awardDataList, navController, index = listAwards.indexOf(awardDataList)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun UnLockAwardsItem(
    awards: AllAwardData.AwardsData, navController: NavHostController, index: Int
) {
    val selectedBorder = BorderStroke(width = 0.5.dp, GrayLight02)
    val badgesDesc = stringResource(id = R.string.txt_badges_desc)

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp)
            .fillMaxSize(),
        colors = CardColors(
            containerColor = White,
            contentColor = White,
            disabledContentColor = White,
            disabledContainerColor = White
        ),
        border = selectedBorder,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 5.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
//                painter = if (awards != null) {
//                    rememberImagePainter(awards.imageUrl.toString())
//                } else {
//                    painterResource(R.drawable.icon_teacher_of_the_month)
//                },
                painter = painterResource(R.drawable.icon_teacher_of_the_month),
                contentDescription = "logo",
                modifier = Modifier.wrapContentSize(),
                Alignment.Center
            )

            Text(
                text = awards.name?.toString() ?: "",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontFamily = FontFamily(
                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                ),
                color = Black,
                modifier = Modifier.padding(top = 10.dp)
            )

            val strDateWithMonth: String = getActualDateWithMonth(awards.awardDate.toString())
            Text(
                text = strDateWithMonth,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontFamily = FontFamily(
                    Font(R.font.inter_medium, FontWeight.Medium)
                ),
                color = Black
            )

            Text(
                text = awards.description?.toString() ?: "",
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                fontFamily = FontFamily(
                    Font(R.font.inter_regular, FontWeight.Normal)
                ),
                color = Gray,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(bottom = 10.dp, start = 10.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getActualDateWithMonth(toString: String): String {
    val isoDateString = toString
    val instant = Instant.parse(isoDateString)
    val formatter = DateTimeFormatter.ofPattern("MMMM-yyyy")
        .withZone(ZoneId.systemDefault())
    val formattedDate = formatter.format(instant)
    return formattedDate
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun UnBlockBadgesItem(
    badges: BadgesListData.BadgesList,
    languageData: HashMap<String, String>,
    navController: NavHostController,
    index: Int
) {
    val selectedBorder = BorderStroke(width = 0.5.dp, GrayLight02)
    val badgesDesc = stringResource(id = R.string.txt_badges_desc)
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 5.dp, bottom = 5.dp)
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
            modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = if (badges != null) {
                        rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(badges.imageUrl.toString())
                                .decoderFactory(SvgDecoder.Factory()) // Adds SVG support
                                .size(Size.ORIGINAL) // Use original or specify size
                                .placeholder(R.drawable.in_progress)
                                .error(R.drawable.in_progress)
                                .build()
                        )
                    } else {
                        painterResource(R.drawable.striver_icon)
                    },
                    contentDescription = "logo",
                    modifier = Modifier
                        .size(50.dp) // Add size modifier to make the image visible
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
                        text = badges.name?.toString() ?: "",
                        textAlign = TextAlign.Start,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                        ),
                        color = Black,
                        modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                    )

                    if (badges.status.equals("")) {
                        Text(
                            text = badges.description?.toString() ?: "",
                            textAlign = TextAlign.Start,
                            fontSize = 12.sp,
                            fontFamily = FontFamily(
                                Font(R.font.inter_regular, FontWeight.Normal)
                            ),
                            color = Gray,
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(bottom = 10.dp, start = 10.dp)
                        )
                    } else {
                        if (badges.status.equals("Inprogress")) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Status: ",
                                    textAlign = TextAlign.Start,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_regular, FontWeight.Normal)
                                    ),
                                    color = Gray,
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(bottom = 10.dp, start = 5.dp)
                                )

                                Text(
                                    text = badges.status.toString(),
                                    textAlign = TextAlign.Start,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_medium, FontWeight.Medium)
                                    ),
                                    color = Yellow,
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(bottom = 10.dp, start = 1.dp)
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = "Status:",
                                    textAlign = TextAlign.Start,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_regular, FontWeight.Normal)
                                    ),
                                    color = Gray,
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(bottom = 10.dp, start = 5.dp)
                                )

                                Text(
                                    text = badges.status.toString(),
                                    textAlign = TextAlign.Start,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_medium, FontWeight.Medium)
                                    ),
                                    color = GreenDark02,
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(bottom = 10.dp, start = 1.dp)
                                )

                                Text(
                                    text = "Date:",
                                    textAlign = TextAlign.Start,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_regular, FontWeight.Normal)
                                    ),
                                    color = Gray,
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(bottom = 10.dp, start = 5.dp)
                                )

                                val strDate: String = getActualDate(badges.dateAwarded.toString())
                                println("Date :- $strDate")
                                Text(
                                    text = strDate,
                                    textAlign = TextAlign.Start,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_regular, FontWeight.Normal)
                                    ),
                                    color = Black,
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(bottom = 10.dp, start = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getActualDate(date: String): String {
    val isoDateString = date
    val instant = Instant.parse(isoDateString)
    val formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy")
        .withZone(ZoneId.systemDefault())
    val formattedDate = formatter.format(instant)
    return formattedDate
}

//@RequiresApi(Build.VERSION_CODES.O)
//@Preview(showBackground = true)
//@Composable
//fun StudentAwardsPreview() {
//    AuroscholarAppTheme {
//        UnLockAwards()
//    }
//}
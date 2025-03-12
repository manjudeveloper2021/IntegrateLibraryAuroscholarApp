package com.auro.application.ui.features.student.passport

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.auro.application.ui.common_ui.components.monthlyFilterBottomSheet
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.menu.TabItem
import com.auro.application.ui.features.student.passport.models.AllAwardListResponse
import com.auro.application.ui.features.student.passport.models.AllBadgesResponse
import com.auro.application.ui.features.student.passport.models.ScoreCalculationResponseModel
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Bg_Gray
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.DarkBlue
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightPink02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.Transparent
import com.auro.application.ui.theme.White
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentUnlockAwardsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuroscholarAppTheme {
                val context: Context = LocalContext.current

                val viewModels: LoginViewModel = hiltViewModel()
                val languageListName = stringResource(id = R.string.key_lang_list)
                var languageData = HashMap<String, String>()
                languageData = viewModels.getLanguageTranslationData(languageListName)

                val filterTextHint = stringResource(id = R.string.txt_monthly)
                var showFilterSheet by remember { mutableStateOf(false) }
                var filterValue by remember { mutableStateOf("monthly") }
                if (showFilterSheet) {
                    monthlyFilterBottomSheet(filterValue = filterValue,
                        onValueSelected = { selectedValue ->
                            filterValue = selectedValue
                            Log.d("ParentComposible", "Selected Value: $filterValue")
                            // You can now use selectedValue in another class
                        }) {
                        showFilterSheet = false
                    }
                }
                Log.d("filterValue:", "" + filterValue)
                // tabs for Unlock awards
                val tabItems = listOf(
                    TabItem(
                        title = stringResource(id = R.string.txt_unlock_badges)
                    ), TabItem(
                        title = stringResource(id = R.string.txt_unlock_awards)
                    ), TabItem(
                        title = stringResource(id = R.string.txt_score_calculations)
                    )
                )
                var selectedTabIndex by remember { mutableIntStateOf(0) }
                val pagerState = rememberPagerState { tabItems.size }

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

                Column(modifier = Modifier.fillMaxSize()) {
                    Surface(
                        tonalElevation = 10.dp, // Set the elevation here
                        color = White,
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(start = 12.dp, end = 12.dp, top = 20.dp, bottom = 10.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.back_icon),
                                contentDescription = "logo",
                                modifier = Modifier.clickable {
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
                                text = stringResource(id = R.string.txt_score_guide),
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(start = 12.dp),
                                fontFamily = FontFamily(
                                    Font(R.font.inter_bold, FontWeight.Bold)
                                ),
                                fontSize = 18.sp,
                                color = Black,
                                textAlign = TextAlign.Start
                            )
                        }
                    }

                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex, edgePadding = 16.dp
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
                                    UnBlockBadges()
                                }

                                1 -> {
                                    UnLockAwards()
                                }

                                else -> {
                                    ScoreCalculations()
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun UnBlockBadges() {
    val context: Context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()
    val languageState by viewModel.languageResponse.collectAsState()
    val networkStatus = languageState
    val navController = rememberNavController()
    var badgesDataList by remember { mutableStateOf(mutableListOf<AllBadgesResponse.AllBadgesData>()) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.allBadgesResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        badgesDataList =
                            it.data.data as MutableList<AllBadgesResponse.AllBadgesData>
                        println("Badges reports data :- $badgesDataList")
                    }
                }

                is NetworkStatus.Error -> {
                    context.toast(it.message)
                }
            }
        }

        viewModel.getAllBadgesReport()
    }

    val list = badgesDataList
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp, bottom = 10.dp)
    ) {
        items(list) { badgesDataList ->
            UnBlockBadgesItem(badgesDataList, navController, index = list.indexOf(badgesDataList))
        }
    }
}

@Composable
private fun UnLockAwards() {
    val context: Context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()
    val languageState by viewModel.languageResponse.collectAsState()
    val networkStatus = languageState
    val navController = rememberNavController()
    var strFrequency by remember { mutableStateOf("monthly") }
    var isMonthlyEnabled by remember { mutableStateOf(true) }
    var isYearlyEnabled by remember { mutableStateOf(false) }

    var allAwardDataList by remember { mutableStateOf(mutableListOf<AllAwardListResponse.AwardListData>()) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
//        viewModel.getAllAwardListReport(strFrequency)

        viewModel.allAwardsListResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        allAwardDataList =
                            it.data.data as MutableList<AllAwardListResponse.AwardListData>
                        println("Award list reports data :- $allAwardDataList")
                    }
                }

                is NetworkStatus.Error -> {
                    context.toast(it.message)
                }
            }
        }
    }
    if (isMonthlyEnabled) {
        strFrequency = "monthly"
        viewModel.getAllAwardListReport(strFrequency)
    }

    if (isYearlyEnabled) {
        strFrequency = "yearly"
        viewModel.getAllAwardListReport(strFrequency)
    }

    Column(
        modifier = Modifier.padding(start = 5.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = {
                    isMonthlyEnabled = true
                    isYearlyEnabled = false
                }, modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 5.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp
                        )
                    ), colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMonthlyEnabled) PrimaryBlue else Bg_Gray,
                    contentColor = if (isMonthlyEnabled) White else GrayLight01
                )
            ) {
                Text(
                    text = stringResource(id = R.string.txt_monthly),
                    fontFamily = FontFamily(
                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                    ),
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    isMonthlyEnabled = false
                    isYearlyEnabled = true
                }, modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 5.dp, start = 20.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp
                        )
                    ), colors = ButtonDefaults.buttonColors(
                    containerColor = if (isYearlyEnabled) PrimaryBlue else Bg_Gray,
                    contentColor = if (isYearlyEnabled) White else GrayLight01
                )
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 3.dp),
                    text = stringResource(id = R.string.txt_yearly),
                    fontFamily = FontFamily(
                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                    ),
                    fontSize = 14.sp
                )
            }

        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp, bottom = 10.dp)
        ) {
            val listAwards = allAwardDataList
            items(listAwards) { allAwardDataList ->
                UnLockAwardsItem(
                    allAwardDataList,
                    navController,
                    index = listAwards.indexOf(allAwardDataList)
                )
            }
        }
    }
}

@Composable
private fun UnLockAwardsItem(
    badges: AllAwardListResponse.AwardListData,
    navController: NavHostController,
    index: Int
) {
    val selectedBorder = BorderStroke(width = 0.5.dp, GrayLight02)
    val badgesDesc = stringResource(id = R.string.txt_badges_desc)

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 5.dp)
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
//                        rememberImagePainter(badges.imageUrl.toString())
                    } else {
                        painterResource(R.drawable.striver_icon)
                    },
//                            painterResource (R.drawable.striver_icon),
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
                }
            }
        }
    }
}

@Composable
private fun UnBlockBadgesItem(
    badges: AllBadgesResponse.AllBadgesData, navController: NavHostController, index: Int
) {
    val selectedBorder = BorderStroke(width = 0.5.dp, GrayLight02)
    val badgesDesc = stringResource(id = R.string.txt_badges_desc)
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 5.dp)
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
//                        rememberImagePainter(badges.imageUrl.toString())
                    } else {
                        painterResource(R.drawable.striver_icon)
                    },
//                    painterResource(R.drawable.striver_icon),
                    contentDescription = "logo",
                    modifier = Modifier
                        .size(54.dp) // Add size modifier to make the image visible
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
                }
            }
        }
    }
}

@Composable
fun ScoreCalculations() {
    val viewModel: StudentViewModel = hiltViewModel()
    val languageState by viewModel.languageResponse.collectAsState()
    val networkStatus = languageState
    val navController = rememberNavController()
    val scrollState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    var scoreData by remember {
        mutableStateOf<ScoreCalculationResponseModel.ScoreCalculationData?>(
            null
        )
    }
    LaunchedEffect(Unit) {
        viewModel.getScoreResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        scoreData = it.data.scoreCalculationData
                    }
                }

                is NetworkStatus.Error -> {
//                    context.toast(it.message)
                }
            }
        }
        viewModel.getScoreCalculation()
    }

    Column(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 15.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 15.dp)
                .background(
                    color = DarkBlue, shape = RoundedCornerShape(12.dp)
                )
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                coroutineScope.launch {
                                    scrollState.scrollBy(-delta)
                                }
                            })
                ) {
                    if (scoreData != null)
                        items(scoreData!!.scoring.size) { index ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp)
                                    .weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_star),
                                    contentDescription = "logo",
                                    modifier = Modifier.clickable {

                                    },
                                )

                                Column(
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .wrapContentSize()
                                ) {
                                    Text(
                                        text = if (scoreData != null) {
                                            scoreData!!.scoring[index].score.toString() + " Points"
                                        } else {
                                            "0 Points"
                                        },
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .padding(start = 5.dp),
                                        fontFamily = FontFamily(
                                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                        ),
                                        fontSize = 12.sp,
                                        color = White,
                                        textAlign = TextAlign.Start
                                    )

                                    Text(
                                        text = if (scoreData != null) {
                                            scoreData!!.scoring[index].name.toString()
                                        } else {
                                            "Core Quiz"
                                        },
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .padding(start = 5.dp),
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

        Text(
            text = stringResource(id = R.string.txt_scoring),
            modifier = Modifier
                .padding(top = 15.dp, bottom = 10.dp, end = 15.dp, start = 15.dp)
                .fillMaxWidth(),
            fontStyle = FontStyle.Normal,
            fontFamily = FontFamily(
                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
            ),
            fontSize = 14.sp,
            color = Black,
            textAlign = TextAlign.Start
        )
        if (scoreData != null)
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(scoreData!!.scoring.size) { index ->
                    ExpandableQuizText(index, scoreData!!.scoring)
                }
            }

        Text(
            text = stringResource(id = R.string.txt_mark_distribution),
            modifier = Modifier
                .padding(top = 15.dp, bottom = 10.dp, end = 15.dp, start = 15.dp)
                .fillMaxWidth(),
            fontStyle = FontStyle.Normal,
            fontFamily = FontFamily(
                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
            ),
            fontSize = 14.sp,
            color = Black,
            textAlign = TextAlign.Start
        )
        if (scoreData != null)
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(scoreData!!.marks_distribution.size) { index ->
                    ExpandableMarkDistributionText(index, scoreData!!.marks_distribution)
                }
            }
    }
}

@Composable
fun ExpandableQuizText(index: Int, leaderboardData: List<ScoreCalculationResponseModel.Scoring>) {
    var isExpanded by remember { mutableStateOf(false) }
    /*val scoreTextHint = stringResource(id = R.string.txt_score_list)
    var expandedContent by remember { mutableStateOf(scoreTextHint) }*/
    var data = leaderboardData[index]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.auro.application.ui.theme.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.core_quiz_icon),
                contentDescription = "logo",
                modifier = Modifier.clickable {

                },
            )
            Text(
                data.name.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp)
                    .weight(1f),
                fontFamily = FontFamily(
                    Font(R.font.inter_medium, FontWeight.Medium)
                ),
                fontSize = 12.sp,
                color = Black,
                textAlign = TextAlign.Start
            )

            IconButton(onClick = {
                isExpanded = !isExpanded
            }) {
                Icon(
                    imageVector = if (isExpanded) ImageVector.vectorResource(id = R.drawable.ic_remove)
                    else ImageVector.vectorResource(id = R.drawable.ic_add),
                    tint = Color.Unspecified,
                    contentDescription = "Expand/Collapse Button"
                )
            }
        }
        if (isExpanded) {
            Text(
                data.description.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                fontFamily = FontFamily(
                    Font(R.font.inter_medium, FontWeight.Normal)
                ),
                fontSize = 12.sp,
                color = Gray,
                textAlign = TextAlign.Start
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
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

@Composable
fun ExpandableMarkDistributionText(
    index: Int,
    leaderboardData: List<ScoreCalculationResponseModel.MarksDistribution>
) {
    var isExpanded by remember { mutableStateOf(false) }
    /*  val scoreTextHint = stringResource(id = R.string.txt_score_list)
      var expandedContent by remember { mutableStateOf(scoreTextHint) }*/
    var data = leaderboardData[index]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.auro.application.ui.theme.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.core_quiz_icon),
                contentDescription = "logo",
                modifier = Modifier.clickable {

                },
            )
            Text(
                data.name.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp)
                    .weight(1f),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = Black,
                textAlign = TextAlign.Start
            )

            IconButton(onClick = {
                isExpanded = !isExpanded
            }) {
                Icon(
                    imageVector = if (isExpanded) ImageVector.vectorResource(id = R.drawable.ic_remove)
                    else ImageVector.vectorResource(id = R.drawable.ic_add),
                    tint = Color.Unspecified,
                    contentDescription = "Expand/Collapse Button"
                )
            }
        }
        if (isExpanded) {
            Text(
                data.description.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Gray,
                textAlign = TextAlign.Start
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
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

@Preview(showBackground = true)
@Composable
fun StudentScoreCalculationPreview() {
    AuroscholarAppTheme {
        ScoreCalculations()
    }
}

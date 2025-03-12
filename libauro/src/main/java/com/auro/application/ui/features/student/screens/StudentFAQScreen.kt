package com.auro.application.ui.features.student.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.ConstantVariables.isKYC
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.components.list
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.ParentDashboardActivity
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.authentication.StudentAuthenticationActivity
import com.auro.application.ui.features.student.models.FaqCategoryResponse
import com.auro.application.ui.features.student.models.FaqResponse
import com.auro.application.ui.features.student.passport.screens.TabItems
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StudentFaqActivity : ComponentActivity() {

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuroscholarAppTheme {
                ShowFaqData()
            }
        }
    }
}

@Composable
fun ShowFaqData() {
    var isSearchClicked by remember { mutableStateOf(false) }
    val searchText = remember { mutableStateOf(TextFieldValue("")) }
    val navHostController: NavHostController = rememberNavController()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val viewModel: StudentViewModel = hiltViewModel()
    val viewModelLogin: LoginViewModel = hiltViewModel()
    var faqDataList by remember { mutableStateOf(mutableListOf<FaqResponse.FaqData>()) }
    var faqCategoryData by remember { mutableStateOf(mutableListOf<FaqCategoryResponse.Data>()) }
    var isDialogVisible by remember { mutableStateOf(false) }

    var categoryList = remember { mutableStateListOf(CategoryTabItem("", 0)) }
    var selectedCategoryName by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(0) }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModelLogin.getLanguageTranslationData(languageListName)
    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
//        message = "Loading your data..."
        message = msgLoader
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    BackHandler {
        try {
            navHostController.popBackStack()
            if (viewModelLogin.getParentInfo()!!.isParent) {
                startActivity(
                    context,
                    Intent(context, ParentDashboardActivity::class.java).apply {
                        (context as? Activity)?.finish()
                    },
                    null
                )
            } else {
                startActivity(
                    context,
                    Intent(context, StudentDashboardActivity::class.java).apply {
                        (context as? Activity)?.finish()
                    },
                    null
                )
            }
        } catch (exp: Exception) {
            exp.message
            println("Student FAQ error :- ${exp.message}")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.faqCategoryResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        categoryList.clear()
                        faqCategoryData = it.data.data as MutableList<FaqCategoryResponse.Data>
                        println("Faq category reports data :- $faqCategoryData")
                        selectedCategoryName = faqCategoryData[0].name.toString()
                        selectedCategoryId = faqCategoryData[0].id!!.toInt()

                        faqCategoryData.forEach { categoryData ->
                            if (categoryData.name.toString().isNotEmpty()) {
                                categoryList.add(
                                    CategoryTabItem(
                                        categoryData.name.toString(), categoryData.id!!.toInt()
                                    )
                                )
                            } else {
                                println("Subject not selected by student...")
                            }

                            categoryList.map {
                                CategoryTabItem(
                                    categoryData.name.toString(), categoryData.id!!.toInt()
                                )
                            }
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    println("Error category Data :- ${it.message}")
                }
            }
        }

        viewModel.faqResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        faqDataList = it.data.data as MutableList<FaqResponse.FaqData>
                        println("Faq reports data :- $faqDataList")
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    println("Error Data :- ${it.message}")
                }
            }
        }

//        viewModel.getFaq(
//            viewModelLogin.getUserType().toString(), viewModelLogin.getLanguageId().toInt()
//        )

        viewModel.getFaqCategory(
            viewModelLogin.getLanguageId().toInt(), viewModelLogin.getUserType()!!.toInt()
        )
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { faqCategoryData.size }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }

    LaunchedEffect(key1 = faqCategoryData.firstOrNull()) {
        coroutineScope.launch {
            faqCategoryData.firstOrNull()?.let { firstItem ->
                isDialogVisible = true
                viewModel.getFaq(
                    viewModelLogin.getLanguageId().toInt(),
                    viewModelLogin.getUserType()!!.toInt(),
                    selectedCategoryId
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        if (isSearchClicked) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .wrapContentHeight()
                    .border(
                        width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.back_icon),
                        contentDescription = "logo",
                        modifier = Modifier
                            .clickable {
//                            navHostController.popBackStack()
//                            navHostController.navigateUp()
                                try {
                                    navHostController.popBackStack()
                                    if (viewModelLogin.getParentInfo()!!.isParent) {
                                        startActivity(
                                            context,
                                            Intent(
                                                context,
                                                ParentDashboardActivity::class.java
                                            ).apply {
                                                (context as? Activity)?.finish()
                                            },
                                            null
                                        )
                                    } else {
                                        startActivity(
                                            context,
                                            Intent(
                                                context,
                                                StudentDashboardActivity::class.java
                                            ).apply {
                                                (context as? Activity)?.finish()
                                            },
                                            null
                                        )
                                    }
                                } catch (exp: Exception) {
                                    exp.message
                                }
                            }
                            .background(Color.Unspecified),
                        colorFilter = ColorFilter.tint(Black)
                    )
                    var searchList by remember { mutableStateOf(faqDataList) }
//                    SearchScreen(searchList)
                    SearchBar(query = searchText.value, onQueryChange = { query ->
                        searchText.value = query
                        searchList = searchList(query.text, faqDataList)
                    })/* SearchBar(
                         value = searchList,
                         "Search", true, keyboardType = KeyboardType.Text
                     )*/
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 15.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.back_icon),
                    contentDescription = "logo",
                    modifier = Modifier
                        .background(Color.Unspecified)
                        .clickable {
                            try {
                                navHostController.popBackStack()
                                if (viewModelLogin.getParentInfo()!!.isParent) {
                                    startActivity(
                                        context,
                                        Intent(context, ParentDashboardActivity::class.java).apply {
                                            (context as? Activity)?.finish()
                                        },
                                        null
                                    )
                                } else {
                                    startActivity(
                                        context,
                                        Intent(
                                            context,
                                            StudentDashboardActivity::class.java
                                        ).apply {
                                            (context as? Activity)?.finish()
                                        },
                                        null
                                    )
                                }
                            } catch (exp: Exception) {
                                exp.message
                            }
                        },
                    colorFilter = ColorFilter.tint(Black)
                )
                Text(
//                    text = stringResource(id = R.string.txt_faq),
                    text = languageData[LanguageTranslationsResponse.FAQ].toString() + "s",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 20.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )
                IconButton(onClick = {
                    isSearchClicked = !isSearchClicked
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_search),
                        tint = Color.Unspecified,
                        contentDescription = "Expand/Collapse Button",
                        modifier = Modifier.background(Color.Unspecified)
                    )
                }
            }
        }

        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex, edgePadding = 2.dp
        ) {
            categoryList.forEachIndexed { index, tabItem ->
                Tab(selected = index == selectedTabIndex, onClick = {
                    selectedTabIndex = index
                    coroutineScope.launch {
                        selectedCategoryName = tabItem.categoryName
                        selectedCategoryId = tabItem.categoryId
                        isDialogVisible = true
                        viewModel.getFaq(
                            viewModelLogin.getLanguageId().toInt(),
                            viewModelLogin.getUserType()!!.toInt(),
                            selectedCategoryId
                        )
                    }
                }, modifier = Modifier.background(White), text = {
                    Text(
                        text = tabItem.categoryName,
                        modifier = Modifier.padding(vertical = 5.dp),
                        color = if (index == selectedTabIndex) PrimaryBlue else GrayLight01,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                })
            }
        }

        if (faqDataList.size != 0) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                val list = faqDataList
                items(list) { faqDataList ->
                    ExpandableText(faqDataList)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.no_data_found),
                    contentDescription = "logo",
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .background(Color.Unspecified),
                )
                Text(
                    text = stringResource(id = R.string.txt_oops_no_data_found),
                    modifier = Modifier
                        .wrapContentSize(),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

data class CategoryTabItem(
    var categoryName: String, var categoryId: Int,
)

fun searchList(
    text: String, faqDataList: MutableList<FaqResponse.FaqData>,
): MutableList<FaqResponse.FaqData> {
    return if (text.isEmpty()) {
        faqDataList // Return the full list if the query is empty
    } else ({
        faqDataList.filter { it.answer.toString().contains(text, ignoreCase = true) }
    }) as MutableList<FaqResponse.FaqData>
}

@Composable
fun ExpandableText(child: FaqResponse.FaqData) {
    var isExpanded by remember { mutableStateOf(false) }
    var expandedContent by remember { mutableStateOf("This is the Expanded text.") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                child.question.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Medium,
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
                    contentDescription = "Expand/Collapse Button",
                    modifier = Modifier.background(Color.Unspecified)
                )
            }
        }
        if (isExpanded) {
            Text(
                child.answer.toString(),
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
    }
}

@Composable
fun SearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    placeholder: String = "Search",
    editable: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
    val navHostController: NavHostController = rememberNavController()
    val viewModelLogin: LoginViewModel = hiltViewModel()

    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        println("New value for search data :- $query")
        TextField(
            value = query,
            onValueChange = { newValue ->
                newValue.toString()
                onQueryChange.toString()
            },
            enabled = editable,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = keyboardType, imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }),
            visualTransformation = VisualTransformation.None,
            placeholder = {
                Text(
                    text = placeholder,
                    color = GrayLight01,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .wrapContentHeight(),
            textStyle = TextStyle(
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Gray
            ),
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = Color.White,
                focusedTextColor = Gray,
                unfocusedTextColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent
            ),
        )
        IconButton(modifier = Modifier
            .height(25.dp)
            .width(25.dp), onClick = {

        }) {
            Icon(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Unspecified)
                    .clickable {
                        /*try {
                        navHostController.popBackStack()
                        if (viewModelLogin.getParentInfo()!!.isParent) {
                            startActivity(
                                context,
                                Intent(context, ParentDashboardActivity::class.java).apply {
                                    (context as? Activity)?.finish()
                                },
                                null
                            )
                        } else {
                            startActivity(
                                context,
                                Intent(context, StudentDashboardActivity::class.java).apply {
                                    (context as? Activity)?.finish()
                                },
                                null
                            )
                        }
                    } catch (exp: Exception) {
                        exp.message
                        println("Student FAQ error :- ${exp.message}")
                    }*/
                    },
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_close),
                tint = Color.Unspecified,
                contentDescription = "Expand/Collapse Button"
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun StudentFAQPreview() {
    AuroscholarAppTheme {
        ShowFaqData()
    }
}
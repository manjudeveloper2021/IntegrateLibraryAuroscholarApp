package com.auro.application.ui.features.login.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.auro.application.App
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.repository.models.GetLanguageListResponse
import com.auro.application.repository.models.GetLanguageListResponse.Data.Result
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.CircleCheckbox
import com.auro.application.ui.common_ui.DefaultBackgroundUi
import com.auro.application.ui.common_ui.components.DoubleBackPressHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.PrimaryBlueLt
import com.auro.application.ui.theme.White
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ChooseLanguage(navController: NavHostController, viewModel: LoginViewModel = hiltViewModel()) {
    var isDialogVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var hmLangValue: HashMap<String, String> = HashMap()
    var langKeyList: HashMap<String, String> = HashMap()

    val languageListName = stringResource(id = R.string.key_lang_list)


    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = "Loading your data..."
    )
    LaunchedEffect(Unit) {
        viewModel.translationsLanguageResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    Log.e("TAG", "ChooseUserTypeUI: " + it.data?.data)
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        for (i in it.data.data) {
                            langKeyList[i.key!!] = i.TranslatedName!!
                        }

                        hmLangValue = HashMap(langKeyList)
//                        hmLangValue = HashMap(getHashMap(this, getString(R.string.key_lang_list)))
                        viewModel.saveLanguageTranslationData(languageListName, langKeyList)
//                        langKeyList = HashMap()
//                        viewModel.saveLanguageTranslationData(it.data.data)
                        navController.popBackStack()
                        navController.navigate(AppRoute.SelectRole.route)
                    } else {
                        isDialogVisible = false
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                }
            }
        }
    }

    DoubleBackPressHandler {
        (context as? Activity)?.finish()
    }

    DefaultBackgroundUi(
        isShowBackButton = false,
        onBackButtonClick = {},
        content = {

            val selectYourRole = stringResource(id = R.string.choose_your_language)
            Text(
                text = selectYourRole,
                modifier = Modifier
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                    .clickable {
                        navController.navigate(AppRoute.SelectRole.route)
                    },
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Black
            )

            val str = stringResource(id = R.string.select_to_proceed)
            Text(
                str,
                modifier = Modifier.padding(top = 0.dp, start = 16.dp, end = 16.dp),
                color = GrayLight01,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )

            LanguageScreen(navController, viewModel)
        }
    )
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun LanguageScreen(navController: NavHostController, viewModel: LoginViewModel = hiltViewModel()) {
    val languageState by viewModel.languageResponse.collectAsState()
    val languageResult = languageState
    LanguageMethod(navController, languageResult, viewModel)
}

@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "NewApi")
@Composable
fun LanguageMethod(
    navController: NavHostController,
    networkStatus: NetworkStatus<GetLanguageListResponse?>,
    viewModel: LoginViewModel
) {
    val context = LocalContext.current
    var isDialogVisible by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var selectedLanguage = remember { mutableStateOf<String?>(null) }

    when (networkStatus) {
        is NetworkStatus.Idle -> {
            isDialogVisible = false
        }

        is NetworkStatus.Success -> {
            isDialogVisible = false
            val languageData = networkStatus.data?.data?.results
            val scrollState = rememberLazyGridState()
            val coroutineScope = rememberCoroutineScope()
            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .background(color = Color.White)
                    .padding(4.dp), // Add horizontal padding,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(bottom = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .weight(1f)
                    ) {
                        if (languageData != null) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                state = scrollState,
                                contentPadding = PaddingValues(bottom = 15.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp, start = 8.dp, end = 8.dp)
                                    .draggable(
                                        orientation = Orientation.Vertical,
                                        state = rememberDraggableState { delta ->
                                            coroutineScope.launch {
                                                scrollState.scrollBy(-delta)
                                            }
                                        })
                            ) {
                                items(languageData.size) { index ->
                                    ItemLanguageCard(
                                        context,
                                        isSelected = selectedIndex == index,
                                        navController,
                                        index,
                                        selectedLanguage,
                                        viewModel,
                                        language = languageData,
                                        onItemClicked = {
                                            selectedIndex =
                                                if (selectedIndex == index) null else index // Toggle selection
                                            selectedLanguage.value =
                                                languageData[index].id.toString()
                                            viewModel.saveLanguageId(languageData[index].id.toString())
                                            viewModel.saveLanguageCode(languageData[index].name[0].toString())
                                            println("Selected Item :- $selectedIndex")
                                        })
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val msg = stringResource(id = R.string.choose_your_language)
                        BtnUi(
                            "Continue",
                            onClick = {
                                if (selectedIndex != null) {
                                    isDialogVisible = true
//                                    viewModel.languageTranslationAPI(selectedLanguage.value!!.toInt())
                                    viewModel.getTranslationsLanguageAPI(selectedLanguage.value!!.toInt())

                                } else {
                                    context.toast(msg)
                                }
                            }, selectedIndex != null
                        )
                    }
                }
            }
        }

        is NetworkStatus.Error -> {
            isDialogVisible = false
            val errorMessage = networkStatus.message
            // Render UI with errorMessage
            ShowError(errorMessage)
            println("SSL error :- $errorMessage")
        }

        NetworkStatus.Loading -> {
            isDialogVisible = true
            // Handle loading state for regular quotes
            // Render loading indicator or any UI
            //   CenteredCircularProgressIndicator()
        }
    }

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = "Loading your data..."
    )

}

@Composable
fun ItemLanguageCard(
    context: Context,
    isSelected: Boolean = true,
    navController: NavHostController,
    index: Int,
    selectedLanguage: MutableState<String?>,
    viewModel: LoginViewModel,
    language: List<Result>,
    onItemClicked: () -> Unit = {}
) {
    val languageIndex = language[index]

    var selectedBorder = if (isSelected) BorderStroke(
        width = 1.dp,
        PrimaryBlue
    ) else BorderStroke(width = 0.5.dp, GrayLight02)
    var backGroundColor = if (isSelected) PrimaryBlueLt else White

    Card(
        modifier = Modifier
            .clickable {
                if (languageIndex.status == "Active")
                    onItemClicked.invoke()
                else {
                    context.toast("Choose Language Hindi & English")
                }
            }
            .padding(5.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp),
        border = selectedBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = backGroundColor),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 5.dp)
                        .background(Color.Unspecified)
                        .size(25.dp),
//                        .wrapContentSize(),
                    contentScale = ContentScale.Fit,
                    painter = if (languageIndex.icon != null) {
                        rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(languageIndex.icon)
                                .decoderFactory(SvgDecoder.Factory()) // Adds SVG support
                                .size(Size.ORIGINAL) // Use original or specify size
                                .placeholder(R.drawable.icon_hindi)
                                .error(R.drawable.icon_hindi)
                                .build()
                        )
                    } else {
                        painterResource(R.drawable.icon_hindi)
                    },
                    contentDescription = "Logo"
                )

                Column(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        languageIndex.nativeName.toString(),
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis,
                        color = Black,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 10.dp)
                    )

                    Text(
                        "${languageIndex.name.toString()} ",
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        fontSize = 12.sp,
                        overflow = TextOverflow.Ellipsis,
                        color = Gray,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(bottom = 10.dp, start = 10.dp)
//                            .heightIn(min = 40.dp)
                    )
                }
                CircleCheckbox(
                    selected = isSelected,
                    enabled = true, // or false based on your logic
                    onChecked = {
                        if (languageIndex.status == "Active") {
                            onItemClicked()
                            selectedLanguage.value = language[index].id.toString()
                            viewModel.saveLanguageId(language[index].id.toString())
                            viewModel.saveLanguageCode(language[index].name[0].toString())

                            selectedBorder = BorderStroke(
                                width = 1.dp,
                                PrimaryBlue
                            )
                            backGroundColor = PrimaryBlueLt
                        } else {
                            context.toast("Choose Language Hindi & English")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CenteredCircularProgressIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(50.dp) // Set the size of the CircularProgressIndicator
                .align(Alignment.Center) // Align it to the center of the Box
        )
    }
}

@Composable
fun ShowError(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)

            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(message)
        }

    }
}

/*@Composable
fun LanguageItem(
    language: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .border(width = 1.dp, color = PrimaryBlue)
            .padding(4.dp), // Add horizontal paddin,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo"
        )

        Text(
            text = language,
            modifier = Modifier
        )

        CircleCheckbox(
            selected = isSelected,
            enabled = true, // or false based on your logic
            onChecked = { if (isSelected) onSelect() }
        )
    }
}*/

//@SuppressLint("UnrememberedMutableState")
//@Composable
//@Preview(showBackground = true, showSystemUi = true)
//fun ShowLanguageUI() {
//    AuroscholarAppTheme {
//        var languageData = mutableStateListOf<Result>()
////        ChooseLanguage(navController = rememberNavController())
//        ItemLanguageCard(true, navController = rememberNavController(), 0,
//            language = languageData,
//            selectedLanguage,
//            viewModel,
//            onItemClicked = {
//
//            })
//    }
//}
package com.auro.application.ui.features.student.partner.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.auro.application.R
import com.auro.application.core.ConstantVariables.PARTNER_NAME
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.partner.PartnerViewModel
import com.auro.application.ui.features.student.partner.PartnerWebViewActivity
import com.auro.application.ui.features.student.partner.models.PartnerDetailsResponse
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.Transparent
import com.auro.application.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerDashboardScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController = rememberNavController(),
    openDrawMenuItem: () -> Unit = {}
) {
    val context = LocalContext.current
    val partnerViewModel: PartnerViewModel = hiltViewModel()
    var isSearchClicked by remember { mutableStateOf(false) }
    val searchText = remember { mutableStateOf("") }
    var partnerData by remember { mutableStateOf(mutableListOf<PartnerDetailsResponse.PartnerDetails.PartnerListData>()) }

    val viewModel: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    var isDialogVisible by remember { mutableStateOf(false) }
    val msgLoader =
        languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = msgLoader
    )

    BackHandler {
        context.startActivity(Intent(
            context, StudentDashboardActivity::class.java
        ).apply { (context as Activity).finish() })
    }

    LaunchedEffect(Unit) {
        partnerViewModel.getPartnerDetailsResponseData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        partnerData =
                            it.data.data.data as MutableList<PartnerDetailsResponse.PartnerDetails.PartnerListData>
                        println("Partner data :- $partnerData")
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
//                    context.toast(it.message)
                    println("Partner details response error :- ${it.message}")
                }
            }
        }

        partnerViewModel.getPartnerDetails(searchText.value)
    }

    Column(
        modifier = Modifier.fillMaxSize()
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
                        .background(White)
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.back_icon),
                        contentDescription = "logo",
                        modifier = Modifier
                            .background(White)
                            .clickable {
                                isSearchClicked = false
                            },
                        colorFilter = ColorFilter.tint(Black)
                    )

                    var searchList by remember { mutableStateOf(partnerData) }
                    PartnerSearchBar(
                        query = searchText.value,
                        onQueryChange = { query ->
                            searchText.value = query
                            searchList = partnerSearchList(
                                query, partnerData
                            )
                        },
                        placeholder = "Search partner",
                        editable = true,
                        keyboardType = KeyboardType.Text
                    )
                }
            }
        } else {
            TopAppBar(title = {
                Row(
                    modifier = Modifier.height(80.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (languageData[LanguageTranslationsResponse.PARTNER].toString() == "") {
                            stringResource(id = R.string.txt_partner)
                        } else {
                            languageData[LanguageTranslationsResponse.PARTNER].toString()
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(painter = painterResource(id = R.drawable.ic_search), // Replace with your drawable resource
                        contentDescription = null, // Provide a description for accessibility purposes
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .wrapContentSize()
                            .clickable {
                                isSearchClicked = !isSearchClicked
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
                .padding(innerPadding)
        ) {
            if (partnerData.size != 0) {
                LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                    val quizStatusReviews = partnerData
                    items(items = quizStatusReviews) { item ->
                        PartnerStatus(navHostController, item)
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
}

fun partnerSearchList(
    text: String,
    partnerDataList: MutableList<PartnerDetailsResponse.PartnerDetails.PartnerListData>
): MutableList<PartnerDetailsResponse.PartnerDetails.PartnerListData> {
    if (text.isNotEmpty()) {
        partnerDataList.filter { it.name.toString().contains(text, ignoreCase = true) }
    } else {
        null
    }
    return partnerDataList
}

@Composable
fun PartnerStatus(
    navHostController: NavHostController,
    partnerDataDetails: PartnerDetailsResponse.PartnerDetails.PartnerListData,
) {
    val context: Context = LocalContext.current
    val partnerViewModel: PartnerViewModel = hiltViewModel()

    Column(
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp)
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(15)
                )
                .padding(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 4.dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
                    .clickable {
                        if (partnerDataDetails.companyUrl!!.contains("https://play.google.com/store/apps/details?id=")) {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(partnerDataDetails.companyUrl.toString())
                                )
                            )
                        } else {
                            try {
                                if (partnerDataDetails.companyUrl!!.isEmpty()) {
                                    Toast
                                        .makeText(
                                            context,
                                            "No data found for ${partnerDataDetails.companyName}.",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else {
                                    partnerViewModel.setPartnerCompanyInfo(partnerDataDetails)
                                    val intent = Intent(context, PartnerWebViewActivity::class.java)
                                    context.startActivity(intent)
                                }
                            } catch (exp: Exception) {
                                exp.message
                                println("Partner dashboard page for redirection on web-view :- ${exp.message}")
                            }
                        }
                    }// Make the entire row clickable
            ) {
                if (partnerDataDetails.logoUrl != "0") {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(partnerDataDetails.logoUrl.toString())
                                .placeholder(R.drawable.logo_auro_scholar)
                                .error(R.drawable.logo_auro_scholar).crossfade(true).build()
                        ),
                        contentDescription = "leftIcon",
                        modifier = Modifier
                            .size(60.dp)
                            .padding(8.dp)
                    )
                } else {
                    if (partnerDataDetails.companyName!!.isNotEmpty()) {
                        val initials =
                            partnerDataDetails.companyName!!.split(" ").take(2)
                                .joinToString("") { it.firstOrNull()?.toString() ?: "" }
                                .uppercase()
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(50.dp) // set the size of the circle
                                .border(BorderStroke(1.dp, GrayLight02), CircleShape)
                                .background(
                                    White, CircleShape
                                ) // background color with circle shape
                        ) {
                            Text(
                                text = initials,
                                color = PrimaryBlue,
                                fontSize = (50 / 2).sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.logo_auro_scholar),
                            contentDescription = "leftIcon",
                            modifier = Modifier
                                .size(60.dp)
                                .padding(8.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = partnerDataDetails.name ?: "N/A",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                        fontFamily = FontFamily(
                            Font(R.font.inter_semi_bold, FontWeight.Bold)
                        ),
                        fontSize = 14.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Left
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.ic_right_side),
                    contentDescription = "rightIcon",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 5.dp)
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
    }
}

@Composable
fun PartnerSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    editable: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val partnerViewModel: PartnerViewModel = hiltViewModel()

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
                onQueryChange(newValue)
                partnerViewModel.getPartnerDetails(newValue)
            },
            enabled = editable,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = keyboardType, imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
                keyboardController?.show()
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
            )
        )

        IconButton(modifier = Modifier
            .height(25.dp)
            .width(25.dp)
            .background(White), onClick = {
            if (query.isNotEmpty()) {
                onQueryChange("")
                partnerViewModel.getPartnerDetails("")
            }
        }) {
            Icon(
                modifier = Modifier.wrapContentSize(),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_close),
                tint = Color.Unspecified,
                contentDescription = "Expand/Collapse Button"
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun ShowPartnerDashboardUI() {
    AuroscholarAppTheme {
//        val navHostController: NavHostController = rememberNavController()
//        val context = LocalContext.current
//        PartnerDashboardScreen(
//            innerPadding = PaddingValues(), navHostController = navHostController
//        ) {}
    }
}
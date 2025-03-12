package com.auro.application.ui.features.student.practice.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
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
import com.auro.application.core.ConstantVariables.isKYC
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.components.DoubleBackPressHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.GetSubjectListResponseModel
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.authentication.StudentAuthenticationActivity
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.Transparent
import com.auro.application.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentPracticeDashboardScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController = rememberNavController(),
    openDrawMenuItem: () -> Unit = {}
) {

    val context: Context = LocalContext.current
    val activity = LocalContext.current as? Activity
    val viewModel: StudentViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)
    var isDialogVisible by remember { mutableStateOf(false) }
    var kycStatus by remember { mutableStateOf("") }
    var subjectData by remember { mutableStateOf(mutableListOf<GetSubjectListResponseModel.Data>()) }
    val selectedBorder = BorderStroke(width = 0.5.dp, GrayLight02)

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()
    CustomDialog(
        isVisible = isDialogVisible, onDismiss = { isDialogVisible = false }, message = msgLoader
    )

    BackHandler {
        navHostController.popBackStack()
        navHostController.navigateUp()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {

        isDialogVisible = true
        viewModel.getSubjectList()

        viewModel.getSubjectListResponseModel.observeForever {
            subjectData.clear()
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        subjectData = it.data.data.toMutableList()
                        loginViewModel.saveStudentSubject(it.data.data)
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    println("Subject List Data Error :-${it.message}")
                }
            }
        }

        viewModel.getKycAadhaarStatusLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        loginViewModel.saveKycStatusData(it.data.data)
                        kycStatus = it.data.data.studentKycStatus
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    println("Kyc Aadhaar Status Live Data Error :-${it.message}")
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(title = {
            Row(
                modifier = Modifier.height(80.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Practice Quiz", fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
                .background(Color.White)
                .padding(innerPadding)
        ) {
            Text(
                text = if (languageData[LanguageTranslationsResponse.PRACTICE_QUIZ].toString() == "") {
                    "These are practice quizzes for your learning; no scholarship will be rewarded for these attempts."
                } else {
                    languageData[LanguageTranslationsResponse.PRACTICE_QUIZ].toString()
                },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 16.dp, end = 16.dp),
                fontFamily = FontFamily(
                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                ),
                fontSize = 14.sp,
                color = Gray,
                textAlign = TextAlign.Start
            )

            if (subjectData.size != 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)
                ) {
                    items(subjectData.size) { index ->
                        ItemSubjectNameCard(context,
                            kycStatus,
                            viewModel,
                            loginViewModel,
                            languageData,
                            navHostController,
                            selectedBorder,
                            index,
                            subject = subjectData)
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
                        text = "No Subject Found",
                        modifier = Modifier.wrapContentSize(),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Black,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
        /*Column(
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
                text = "No Subject Found",
                modifier = Modifier.wrapContentSize(),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Black,
                textAlign = TextAlign.Start
            )
        }*/
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSubjectNameCard(
    context: Context,
    kycStatus: String,
    studentViewModel: StudentViewModel,
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    navController: NavHostController,
    selectedBorder: BorderStroke,
    index: Int,
    subject: MutableList<GetSubjectListResponseModel.Data>,
) {
    val subjectIndex = subject[index]
    val backGroundColor = White

    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })

    var userId by remember { mutableStateOf("") }
    userId = viewModel.getUserId().toString()

    if (subjectIndex.isSelected) Card(shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp, top = 5.dp)
            .fillMaxWidth(),
        colors = CardColors(
            containerColor = White,
            contentColor = White,
            disabledContentColor = White,
            disabledContainerColor = White
        ),
        border = selectedBorder,
        onClick = {
            if (viewModel.getChildCount() > 1) {
                viewModel.saveStudentSelectedSubjectData(subjectIndex)
                navController.navigate(AppRoute.PracticeConceptList(""))

                /*if (kycStatus.contains("Approve")) {
                    viewModel.saveStudentSelectedSubjectData(subjectIndex)
//                    if (subjectIndex.conceptSelected > 0) {
                    navController.navigate(AppRoute.PracticeConceptList(""))
//                        navController.navigate(AppRoute.StudentQuizList.route)
//                    } else {
//                        navController.navigate(AppRoute.StudentAssessmentConcept.route)
//                    }
                } else {
                    scope.launch {
                        isBottomSheetVisible = true
                        sheetState.expand()
                    }
                    println("Currently no required for this role in this panel.")
                }*/
            } else {
            viewModel.saveStudentSelectedSubjectData(subjectIndex)
            navController.navigate(AppRoute.PracticeConceptList(""))
        }
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 10.dp)
                .background(color = backGroundColor),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .width(45.dp)
                    .background(Color.Unspecified)
                    .height(45.dp),
                painter = if (subjectIndex.icons.isNotEmpty()) {
                    rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current).data(subjectIndex.icons)
                            .decoderFactory(SvgDecoder.Factory()) // Adds SVG support
                            .size(Size.ORIGINAL) // Use original or specify size
                            .placeholder(R.drawable.english_icon).error(R.drawable.english_icon)
                            .build()
                    )
                } else {
                    painterResource(R.drawable.english_icon)
                },
                contentDescription = "Logo"
            )

            Column(
                modifier = Modifier.padding(start = 5.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    subjectIndex.subjectName,
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(
                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                    ),
                    color = Black,
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                )

                /*Text(
                    subjectIndex.conceptSelected.toString() + if (languageData[LanguageTranslationsResponse.MONTHLY_CONCEPT_SELECTED].toString() == "") {
                        "/4 Monthly concept selected"
                    } else {
                        "/4 " + languageData[LanguageTranslationsResponse.MONTHLY_CONCEPT_SELECTED].toString()
                    },
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(
                        Font(R.font.inter_regular, FontWeight.Normal)
                    ),
                    color = Gray,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(bottom = 10.dp, start = 10.dp)
                )*/
            }

            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.ic_right_side),
                contentDescription = stringResource(id = R.string.icon_description),
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Unspecified)
            )
        }

        BottomSheetPracticeKYCDialogNotice(
            isBottomSheetVisible = isBottomSheetVisible,
            sheetState = sheetState,
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    isBottomSheetVisible = false
                }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetPracticeKYCDialogNotice(
    isBottomSheetVisible: Boolean, sheetState: SheetState, onDismiss: () -> Unit
) {

    val context: Context = LocalContext.current
    val viewModel: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    if (isBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = null,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            windowInsets = WindowInsets.ime
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(vertical = 15.dp, horizontal = 15.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource(R.drawable.line),
                        contentDescription = "logo",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(top = 8.dp)
                            .clickable {
                                onDismiss.invoke()
                            }
                            .clip(RoundedCornerShape(100.dp))
                            .border( // Add border modifier to make image stand out
                                width = 2.dp, color = Color.Black, shape = CircleShape
                            ))

                    Image(
                        painter = painterResource(id = R.drawable.icon_kyc_img), // Replace with your lock icon drawable
                        contentDescription = "KYC Lock",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp, top = 24.dp)
                    )

                    Text(
                        text = "Apologies! KYC is mandatory to withdraw Scholarship",
                        fontFamily = FontFamily(
                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                        ),
                        fontSize = 16.sp,
                        color = Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "You must complete KYC to withdraw Scholarship. If your KYC is not verified within 3 months of quiz, the quiz will be disapproved and the scholarship amount will be returned.",
                        fontSize = 14.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
                        ),
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                    )

                    BtnUi(
                        onClick = {
                            onDismiss.invoke()
                            context.startActivity(Intent(
                                context, StudentAuthenticationActivity::class.java
                            ).apply {
                                putExtra(isKYC, isKYC)
                            })
                        },
                        title = "Continue to KYC Update",
                        enabled = true,
                        cornerRadius = 10.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun StudentPracticeDashboardScreenUI() {
    AuroscholarAppTheme {
//        val navHostController: NavHostController = rememberNavController()
//        val context = LocalContext.current
//        StudentPracticeDashboardScreen(
//            innerPadding = PaddingValues(), navHostController = navHostController
//        ) {}
    }
}
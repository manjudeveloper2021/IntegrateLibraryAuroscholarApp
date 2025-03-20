package com.auro.application.ui.features.student.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.auro.application.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun StudentQuizzesScreen(navHostController: NavHostController = rememberNavController()) {

    val context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()
    val viewModelLogin: LoginViewModel = hiltViewModel()
    val selectedBorder = BorderStroke(width = 0.5.dp, GrayLight02)

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModelLogin.getLanguageTranslationData(languageListName)

    var isDialogVisible by remember { mutableStateOf(false) }
    var kycStatus by remember { mutableStateOf("") }
    var subjectData by remember { mutableStateOf(mutableListOf<GetSubjectListResponseModel.Data>()) }

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()
    val textQuiz = stringResource(id = R.string.txt_quizzes)
    val textPlayQuiz = stringResource(id = R.string.txt_play_quiz)

    val userId = if (viewModelLogin.getParentInfo() != null) {
        if (viewModelLogin.getParentInfo()!!.isParent) {  // if login as parent & came to authentication
            viewModelLogin.getUserId().toString()// studentId
        } else {
            viewModelLogin.getUserId().toString() // parent id / current userID
        }
    } else {
        viewModelLogin.getUserId().toString()
    }

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = msgLoader
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    if (subjectData.isEmpty()) {
        viewModel.getSubjectList()
    }

    LaunchedEffect(Unit) {

        viewModel.getSubjectList()
        viewModel.getKycAadhaarStatus(userId.toInt())  // check id status

        viewModel.getSubjectListResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
                        subjectData = it.data.data.toMutableList()
                        viewModelLogin.saveStudentSubject(it.data.data)
                    }
                }

                is NetworkStatus.Error -> {
                    println("Subject List Data Error :-${it.message}")
                }
            }
        }

        viewModel.getKycAadhaarStatusLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {

                }

                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true) {
                        viewModelLogin.saveKycStatusData(it.data.data)
                        kycStatus = it.data.data.studentKycStatus
                    }
                }

                is NetworkStatus.Error -> {
                    println("Kyc Aadhaar Status Live Data Error :-${it.message}")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .background(White)
    ) {
        Surface(
            tonalElevation = 10.dp, // Set the elevation here
            color = White,
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 10.dp, end = 20.dp, top = 20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.back_icon),
                    contentDescription = "logo",
                    modifier = Modifier
                        .clickable {
                            navHostController.popBackStack()
//                                    navHostController.navigate(AppRoute.StudentDashboard.route)
                        },

                    colorFilter = ColorFilter.tint(Black)
                )
                Column(
                    modifier = Modifier
                        .padding(start = 5.dp, end = 20.dp)
                        .weight(1f)
                        .wrapContentSize(),
                ) {
                    Text(
                        text = textQuiz,
                        modifier = Modifier.padding(top = 10.dp, start = 5.dp, end = 15.dp),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Text(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 5.dp),
                        text = textPlayQuiz,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Black,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Unspecified)
                        .padding(top = 20.dp, bottom = 40.dp, start = 5.dp, end = 5.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (subjectData.size != 0) {
                        items(subjectData.size) { index ->
                            ItemSubjectCards(context,
                                kycStatus,
                                viewModelLogin,
                                languageData,
                                navHostController,
                                selectedBorder,
                                index,
                                subject = subjectData,
                                onItemClicked = {

                                })
                        }
                    } else {
                        Log.d("subjectData", " else: $subjectData")
                        viewModel.getSubjectList()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSubjectCards(
    context: Context,
    kycStatus: String,
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    navController: NavHostController,
    selectedBorder: BorderStroke,
    index: Int,
    subject: MutableList<GetSubjectListResponseModel.Data>,
    onItemClicked: (Boolean) -> Unit,
) {
    val subjectIndex = subject[index]
    val backGroundColor = White

    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })

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
                if (kycStatus.contains("Approve")) {
                    onItemClicked(true)
                    viewModel.saveStudentSelectedSubjectData(subjectIndex)
                    if (subjectIndex.conceptSelected > 0) {
                        navController.navigate(AppRoute.StudentQuizList.route)
                    } else {
                        navController.navigate(AppRoute.StudentAssessmentConcept.route)
                    }
                } else {
                    scope.launch {
                        isBottomSheetVisible = true
                        sheetState.expand()
                    }
//                    context.toast(
//                        "KYC is not Approved!"
//                    )
                }
            } else {
                onItemClicked(true)
                viewModel.saveStudentSelectedSubjectData(subjectIndex)
                if (subjectIndex.conceptSelected > 0) {
                    navController.navigate(AppRoute.StudentQuizList.route)
                } else {
                    navController.navigate(AppRoute.StudentAssessmentConcept.route)
                }
            }
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 10.dp)
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
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                )

                Text(
                    subjectIndex.conceptSelected.toString() + if (languageData[LanguageTranslationsResponse.MONTHLY_CONCEPT_SELECTED].toString() == "") {
                        "/4 Monthly concept selected"
                    } else {
                        "/4 " + languageData[LanguageTranslationsResponse.MONTHLY_CONCEPT_SELECTED].toString()
                    },
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Gray,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(bottom = 10.dp, start = 10.dp)
                )
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

        BottomSheetKYCDialog(
            isBottomSheetVisible = isBottomSheetVisible,
            sheetState = sheetState,
            onDismiss = {
                scope.launch { sheetState.hide() }
                    .invokeOnCompletion {
                        isBottomSheetVisible = false
                    }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetKYCDialog(
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
        //    windowInsets = WindowInsets.ime
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
                    Image(
                        painter = painterResource(id = R.drawable.icon_kyc_img), // Replace with your lock icon drawable
                        contentDescription = "KYC Lock",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp)
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
                            context.startActivity(
                                Intent(
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


@Preview(showBackground = true)
@Composable
fun StudentQuizzesScreenPreview() {
    AuroscholarAppTheme {
        StudentQuizzesScreen()
    }
}
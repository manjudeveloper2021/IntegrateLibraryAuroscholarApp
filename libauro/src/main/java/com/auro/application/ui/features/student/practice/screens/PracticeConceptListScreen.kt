package com.auro.application.ui.features.student.practice.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.utlis.CommonFunction.currentMonth
import com.auro.application.data.utlis.CommonFunction.currentYear
import com.auro.application.ui.common_ui.RectangleBtnUi
import com.auro.application.ui.common_ui.components.YearMonthGridPicker
import com.auro.application.ui.common_ui.components.YearMonthPicker
import com.auro.application.ui.common_ui.components.YearMonthWalletGridPicker
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.assessment.model.AssessmentGetQuizRequestModel
import com.auro.application.ui.features.student.assessment.model.AssessmentQuizQuestionsResponseModel.SendQuestionToCandidateWebApiResult
import com.auro.application.ui.features.student.passport.screens.rememberDebounce
import com.auro.application.ui.features.student.practice.models.PracticeConceptsResponse
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Year
import java.time.YearMonth
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PracticeConceptListScreen(
    navHostController: NavHostController = rememberNavController()
) {

    val context: Context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
//    val currentMonth = currentDateDisplay()
//    var conceptList by remember { mutableStateOf(mutableListOf<AssessmentConceptsResponseModel.AssessmentConcept>()) }
    var practiceConceptList by remember { mutableStateOf(mutableListOf<PracticeConceptsResponse.ConceptsData>()) }
    var isDialogVisible by remember { mutableStateOf(false) }
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)
    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()
    val currentMonth = currentMonth(true)
    val currentYear = currentYear(true)
    val scrollState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
//    val currentYear = currentYear()

    var assessmentExamId by remember { mutableStateOf("") }
    var examId by remember { mutableStateOf("") }

    var isSearchClicked by remember { mutableStateOf(false) }
    val searchText = remember { mutableStateOf(TextFieldValue("")) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    CustomDialog(
        isVisible = isDialogVisible, onDismiss = { isDialogVisible = false }, message = msgLoader
    )

    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })

    FilterPracticeDialogScreen(
        isBottomSheetVisible = isBottomSheetVisible,
        sheetState = sheetState,
        onDismiss = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                isBottomSheetVisible = false
            }
        })

    LaunchedEffect(Unit) {

        viewModel.getQuizQuestionResponse.observeForever { networkStatus ->
            when (networkStatus) {
                is NetworkStatus.Idle -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (networkStatus.data!!.isSuccess) {
                        if (networkStatus.data.data.setComplete != 0) {
                            if (networkStatus.data.data.data.size != 10) {
                                context.toast("Question set is incomplete!")
                                navHostController.navigateUp()
                            } else {
                                examId = networkStatus.data.data.exam_id
                                assessmentExamId =
                                    networkStatus.data.data.AssessmentExamID.toString()
                                loginViewModel.saveAssessmentQuestion(networkStatus.data.data)
                                loginViewModel.saveExamId(examId)
                                loginViewModel.saveIsPractice(true)

                                navHostController.popBackStack()
                                navHostController.navigate(AppRoute.QuizQuestionScreen.route)
                            }
                        } else {
                            context.toast(networkStatus.data.data.message)
                            navHostController.navigateUp()
                        }
                    }
                    isDialogVisible = false

//                    networkStatus.data?.data?.let { quizListData.add(it) }

                }

                is NetworkStatus.Error -> {
                    context.toast(networkStatus.message)
                    isDialogVisible = false
                    // Handle the error case, e.g., show a message to the user
                }
            }
        }

        viewModel.savePracticeConceptsResponseData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                    practiceConceptList.clear()
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    practiceConceptList.clear()
                    if (it.data?.isSuccess == true) {
                        practiceConceptList = it.data.data.toMutableList()
//                        println("Practice data list :- $practiceConceptList")
                    }
                }

                is NetworkStatus.Error -> {
                    context.toast(it.message)
                    isDialogVisible = false
                    practiceConceptList.clear()
                }
            }
        }

        viewModel.getPracticeConcepts(
            viewModel.getStudentPracticeSelectedSubjectData().subjectId,
            loginViewModel.getGrade()!!.toInt(),
            "",
            ""
        )
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
                    Image(painter = painterResource(R.drawable.back_icon),
                        contentDescription = "logo",
                        modifier = Modifier
                            .clickable {
                                isSearchClicked = false
                            }
                            .background(Color.Unspecified),
                        colorFilter = ColorFilter.tint(Black))

                    PracticeSearchBar(query = searchText.value, onQueryChange = { query ->
                        searchText.value = query
                    })
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Surface(
                    tonalElevation = 10.dp, // Set the elevation here
                    color = White,
                ) {
                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.back_icon),
                            contentDescription = "logo",
                            modifier = Modifier.clickable {
                                navHostController.popBackStack()
                                val intent = Intent(context, StudentDashboardActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.also {
                                    it.putExtra("", true)
                                }
                                context.startActivity(intent)
                            },

                            colorFilter = ColorFilter.tint(Black)
                        )
                        Column(
                            modifier = Modifier
                                .padding(start = 25.dp, end = 20.dp)
                                .weight(1f)
                                .wrapContentSize(),
                        ) {
                            Text(
                                text = viewModel.getStudentPracticeSelectedSubjectData().subjectName,
                                modifier = Modifier.fillMaxWidth(),
                                fontFamily = FontFamily(
                                    Font(R.font.inter_bold, FontWeight.Bold)
                                ),
                                fontSize = 18.sp,
                                color = Black,
                                textAlign = TextAlign.Start
                            )
                        }

                        Image(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "logo",
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .size(28.dp)
                                .clickable {
                                    isSearchClicked = !isSearchClicked
                                },

                            colorFilter = ColorFilter.tint(Black)
                        )

                        Image(
                            painter = painterResource(R.drawable.calender_icon),
                            contentDescription = "logo",
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(28.dp)
                                .clickable {
                                    scope.launch {
                                        isBottomSheetVisible = true
                                        sheetState.expand()
                                    }
                                },

                            colorFilter = ColorFilter.tint(Black)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .padding(top = 20.dp, bottom = 20.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            ) {
                drawLine(
                    color = GrayLight02,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
        if (practiceConceptList.size != 0) {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                    .weight(1f)
                    .fillMaxSize()
                    .background(White)
            ) {

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(practiceConceptList) { index, item ->
                        Column(
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        ) {
                            PracticeConceptItem(
                                index,
                                loginViewModel,
                                viewModel,
                                languageData,
                                navHostController,
                                text2 = item.concept.toString(),
                                conceptId = item.conceptId.toString(),
                                nextAttempt = item.nextAttempt,
                                currentMonth,
                                currentYear
                            )
                        }
                    }
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
                    text = "No Data Found!",
                    modifier = Modifier.wrapContentSize(),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .padding(top = 5.dp, bottom = 20.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            ) {
                drawLine(
                    color = GrayLight02,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}

@Composable
fun PracticeSearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    placeholder: String = "Search",
    editable: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val viewModel: LoginViewModel = hiltViewModel()
    val viewModelStudent: StudentViewModel = hiltViewModel()

    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        TextField(
            value = query,
            onValueChange = { newValue ->
                onQueryChange(newValue)
                if (newValue.text.isNotEmpty()) {
                    viewModelStudent.getPracticeConcepts(
                        viewModelStudent.getStudentPracticeSelectedSubjectData().subjectId,
                        viewModel.getGrade()!!.toInt(),
                        newValue.text,
                        ""
                    )
                }
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
            trailingIcon = null
        )

        IconButton(modifier = Modifier
            .height(25.dp)
            .width(25.dp), onClick = {
            onQueryChange(TextFieldValue(""))
            viewModelStudent.getPracticeConcepts(
                viewModelStudent.getStudentPracticeSelectedSubjectData().subjectId,
                viewModel.getGrade()!!.toInt(),
                "",
                ""
            )
        }) {
            Icon(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Unspecified),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_close),
                tint = Color.Unspecified,
                contentDescription = "Expand/Collapse Button"
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PracticeConceptItem(
    index: Int, // this will be exam name
    loginViewModel: LoginViewModel,
    viewModel: StudentViewModel = hiltViewModel(),
    languageData: HashMap<String, String>,
    navHostController: NavHostController,
    text2: String,
    conceptId: String,
    nextAttempt: Int?,
    currentMonth: String,
    currentYear: String
) {

    val currentMonth = currentMonth(false)  // for current month quiz
    val currentYear = currentYear(false)    // for current month quiz

//    println("Current year and month :- $currentYear$currentMonth")
    Column(
        modifier = Modifier
            .border(
                width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(12)
            )
            .background(
                White
            )
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .wrapContentHeight()
                .padding(5.dp)
        ) {
            Text(
                text = if (languageData[LanguageTranslationsResponse.KEY_QUIZ].toString() == "") {
                    "Quiz " + 1/*(index + 1)*/
                } else {
                    languageData[LanguageTranslationsResponse.KEY_QUIZ].toString() + " " + 1/*(index + 1)*/
                },
                modifier = Modifier
                    .wrapContentSize()
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(),
                fontFamily = FontFamily(
                    Font(R.font.inter_regular, FontWeight.Normal)
                ),
                fontSize = 12.sp,
                color = Gray,
                textAlign = TextAlign.Left
            )

            Text(
                text = "10 Questions",
                modifier = Modifier
                    .wrapContentSize()
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 10.dp),
                fontFamily = FontFamily(
                    Font(R.font.inter_regular, FontWeight.Normal)
                ),
                fontSize = 12.sp,
                color = Gray,
                textAlign = TextAlign.End
            )
        }

        Text(
            text = text2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            color = Black,
            fontFamily = FontFamily(
                Font(R.font.inter_medium, FontWeight.SemiBold)
            ),
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .wrapContentHeight()
                .padding(5.dp)
        ) {

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            RectangleBtnUi(
                onClick = {
                    loginViewModel.setSelectedConceptName(text2) // saving selected concept data to local
                    loginViewModel.setSelectedConceptId(conceptId)
                    loginViewModel.setSelectedNextAttempt(nextAttempt!!)  // it has to be changed
                    loginViewModel.saveExamName(index.toString())
//                        viewModel.setPracticePage("Practice")

                    viewModel.getAssessmentQuestionData(
                        AssessmentGetQuizRequestModel(
                            loginViewModel.getUserId()!!.toInt(),
                            loginViewModel.getExamName(),
                            nextAttempt,    //it has to be changed
                            loginViewModel.getLanguageCode(),
                            loginViewModel.getStudentSelectedSubjectData().subjectName,
                            loginViewModel.getGrade()!!.toInt(),
                            loginViewModel.getSelectedConceptId(),
                            currentMonth.toInt(),
                            currentYear.toInt(),
                            0,
                            1
                        )
                    )
                }, title = "Practice", enabled = true
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPracticeDialogScreen(
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val viewModelStudent: StudentViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    var selectedYear by remember { mutableStateOf(Year.now().value) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now().monthValue) }
    var selectedValue by remember { mutableStateOf("") }

    if (isBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = null,
            scrimColor = Color.Black.copy(alpha = 0.5f),
           // windowInsets = WindowInsets.ime
        ) {
            Column(
                modifier = Modifier.wrapContentSize()
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
                                onDismiss()
                                selectedValue = ""
                            }
                            .clip(RoundedCornerShape(100.dp))
                            .border( // Add border modifier to make image stand out
                                width = 2.dp, color = Color.Black, shape = CircleShape
                            ))

                    Row(
                        modifier = Modifier.padding(start = 12.dp, top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Select Month & Year",
                            modifier = Modifier.padding(top = 8.dp),
                            fontSize = 18.sp,
                            color = Color.Black,
                            fontFamily = FontFamily(
                                Font(R.font.inter_bold, FontWeight.Bold)
                            )
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Spacer(modifier = Modifier.weight(1f))

                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            // Start Date calender
                            YearMonthPicker(
                                selectedYear = selectedYear, selectedMonth = selectedMonth
                            ) { year, month ->
                                selectedYear = year
                                selectedMonth = month
                                if (selectedMonth < 10) {
                                    println("Selected Year: $selectedYear, Month: 0$selectedMonth")
                                    selectedMonth = ("0$selectedMonth").toInt()
                                    selectedValue = "$selectedYear" + "0$selectedMonth"
                                } else {
                                    println("Selected Year: $selectedYear, Month: $selectedMonth")
                                    selectedValue = "$selectedYear" + "$selectedMonth"
                                }

                            }
                        }
                    }
                }

                Divider(
                    color = GrayLight02,
                    thickness = 1.dp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, top = 8.dp)
                ) {
                    val sdf = SimpleDateFormat("yyyyMM")
                    val currentDateAndTime = sdf.format(Date())
                    val dateMonth = currentDateAndTime.toString()

                    Button(
                        onClick = {
                            onDismiss()
                            selectedValue = ""
                            viewModelStudent.getPracticeConcepts(
                                viewModelStudent.getStudentPracticeSelectedSubjectData().subjectId,
                                viewModel.getGrade()!!.toInt(),
                                "",
                                ""
                            )
                        },
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (selectedValue != "") {
                                "Cancel"
                            } else {
                                ""
                            }, color = PrimaryBlue, fontSize = 16.sp, fontFamily = FontFamily(
                                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                            )
                        )
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            println("Selected month & year $selectedValue")
                            viewModelStudent.getPracticeConcepts(
                                viewModelStudent.getStudentPracticeSelectedSubjectData().subjectId,
                                viewModel.getGrade()!!.toInt(),
                                "",
                                selectedValue
                            )
                        },
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .width(160.dp)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(PrimaryBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Show Results",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(
                                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                            )
                        )
                    }
                }
            }
        }
    }
}
package com.auro.application.ui.features.parent.screens
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.Btn12PXUi
import com.auro.application.ui.common_ui.BtnNextUi
import com.auro.application.ui.common_ui.BtnTextUi
import com.auro.application.ui.common_ui.CircleCheckbox
import com.auro.application.ui.common_ui.DropdownMenuUi
import com.auro.application.ui.common_ui.components.DoubleBackPressHandler
import com.auro.application.ui.common_ui.components.YearMonthWalletGridPicker
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionAnswersSubmitRequestModel
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionAnswersSubmitRequestModel.Question
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionAnswersSubmitRequestModel.Question.Answer
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionOptionsResponseModel
import com.auro.application.ui.features.parent.model.GetMonthYearResponseModel
import com.auro.application.ui.features.parent.model.GradeWiseSubjectResponseModel
import com.auro.application.ui.features.parent.viewmodel.ParentViewModel
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.features.student.wallet.screens.rememberDebounce
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

//code push
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Preview
@Composable
fun QuizPerformanceScreen(
    navController: NavHostController = rememberNavController(),
    sharedPref: SharedPref? = null,
    title: (String) -> Unit = {},
    name: String? = null,
    grade: Int = -1,
    userId: Int = -1,
    viewModel: ParentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    viewModel.saveGrade(grade)
    Log.e("TAG", "QuizPerformanceScreen: data received ---> $name grade$grade")
    LaunchedEffect(Unit) {
        title.invoke("Quiz Performance")
    }
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    val scope = rememberCoroutineScope()
    val dataList = remember { mutableStateListOf<QuizItem>() }
    DoubleBackPressHandler {
        navController.popBackStack()
        (context as? Activity)?.finish()
    }
    var isDialogVisible by remember { mutableStateOf(false) }

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = "Loading your data..."
    )
    val subjectList = remember { mutableStateListOf<GradeWiseSubjectResponseModel.Data>() }
    val dateList = remember { mutableStateListOf<String>() }
    var selectedSubjectId = remember { mutableStateOf("") }
    var selectedDate = remember { mutableStateOf("") }
    var profilercheck = remember { mutableStateOf("") }
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }

    var dateMonth by remember { mutableStateOf("") }
    val sdf = SimpleDateFormat("yyyyMM")
    val currentDateAndTime = sdf.format(Date())
    dateMonth = currentDateAndTime.toString()
//    println("Selected date with year :- $dateMonth")
    var dateValueQ by remember { mutableStateOf("") }
    val debouncedText = rememberDebounce(dateValueQ, 2000L)


    viewModel.getStudentQuizPerformance(
        "202502",
        userId
    )
    viewModel.getProfilerCheckData()
    viewModel.getGradeWiseSubject(grade)
    viewModel.getMonthYear()
    LaunchedEffect(Unit) {
        viewModel.gradeWiseSubjectResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    subjectList.clear()
                    it.data?.let { it1 -> subjectList.addAll(it1.data) }

                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
            }
        }
        viewModel.getMonthYearResponseModelLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    dateList.clear()
                    it.data?.let { it1 -> dateList.addAll(it1.data) }

                }
                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
            }
        }
        viewModel.getQuizPerformanceModelResponseModelLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {
                    isDialogVisible = false
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    dataList.clear()
                    it.data?.let { item ->
                        dataList.addAll(
                            listOf<QuizItem>(
                                QuizItem(
                                    R.drawable.ic_idea,
                                    "Total quizzes played",
                                    item.data.totalQuizCount.toString()
                                ),
                                QuizItem(
                                    R.drawable.ic_graph,
                                    "Average score",
                                    item.data.averageScore.toString()
                                ),
                                QuizItem(
                                    R.drawable.id_top_performing,
                                    "Top performing topics",
                                    item.data.topPerformingTopicCount.toString()
                                ),
                                QuizItem(
                                    R.drawable.ic_weak_performing,
                                    "Weak performing topics",
                                    item.data.weakPerformingTopicCount.toString()
                                ),
                                QuizItem(
                                    R.drawable.ic_microscholarship,
                                    "Microscholarship pending",
                                    item.data.pendingMicroscholarshipAmount.toString()
                                ),
                                QuizItem(
                                    R.drawable.ic_microscholarship_won,
                                    "Microscholarship won",
                                    item.data.wonMicroscholarshipAmount.toString()
                                ),
                            )
                        )
                    }

                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
            }
        }
        viewModel.getProfilerCheckResponseModelLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {
                    isDialogVisible = false
                }
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    dataList.clear()
                    it.data?.let { item ->
                        profilercheck.value = it.data.data.profiler_completed
                    }
                }
                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
            }
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val connection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    return super.onPreScroll(available, source)
                }
            }
        }
        val context = LocalContext.current
        Box(Modifier.nestedScroll(connection)) {
            Column {
                Row {
                    DropdownMenuUi(
                        subjectList.map { it.subjectName }, // Renamed for clarity
                        onItemSelected = { selectedSubjectName ->
                            // Using safe call to avoid null pointer exception
                            selectedSubjectId.value =
                                subjectList.find { it.subjectName == selectedSubjectName }?.subjectId.toString() // Default value if not found

                            if (!selectedSubjectId.value.isNullOrBlank() && !selectedDate.value.isNullOrBlank()) {
                                viewModel.getStudentPerformance(
                                    userId,
                                    selectedSubjectId.value.toInt(),
                                    selectedDate.value
                                )
                                viewModel.getStudentQuizPerformance(
                                    "202502",
                                    userId
                                )
                                Log.e(
                                    "TAG",
                                    "OnboardingStep1: shared prefred ---- > " + userId
                                )
                            } else {
                                if (selectedDate.value.isNullOrBlank()) {
//                                    Toast.makeText(
//                                        context,
//                                        "Please select date",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                                } else {
//                                    Toast.makeText(
//                                        context,
//                                        "Please select subject",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                                }
                            }
                        },
                        placeholder = "All Subject",
                        icon = painterResource(id = R.drawable.ic_down),
                        onClick = { /* Handle click if needed */ },
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp)
                    )

                    DropdownMenuUi(
                        dateList,
                        onItemSelected = {
                            selectedDate.value = it
                            if (!selectedSubjectId.value.isNullOrBlank() && !selectedDate.value.isNullOrBlank()) {
                                viewModel.getStudentPerformance(
                                    userId,
                                    selectedSubjectId.value.toInt(),
                                    selectedDate.value
                                )
                                viewModel.getStudentQuizPerformance(
                                    "202502",
                                    userId
                                )
                                Log.e(
                                    "TAG",
                                    "OnboardingStep1: shared prefred ---- > " + selectedDate.value
                                )
                            } else {
                                if (selectedDate.value.isNullOrBlank()) {
//                                    Toast.makeText(
//                                        context,
//                                        "Please select date",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                                } else {
//                                    Toast.makeText(
//                                        context,
//                                        "Please select subject",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                                }
                            }


                        },
                        placeholder = "Select Date",
                        icon = painterResource(R.drawable.ic_down),
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp)
                    )

                }

                val context = LocalContext.current
                QuizGrid(navController, dataList) {
                    // Handle the click event and navigate with the selected quiz
                    if (!selectedSubjectId.value.isNullOrBlank() && !selectedDate.value.isNullOrBlank()) {
                        viewModel.setData(
                            userId.toString(),
                            selectedSubjectId.value,
                            selectedDate.value
                        )
                        navController.navigate(AppRoute.QuizReport.route)
                    } else if (!selectedDate.value.isNullOrBlank()) {
                      //  Toast.makeText(context, "Please select date", Toast.LENGTH_SHORT).show()
                    } else {
                       // Toast.makeText(context, "Please select subject", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Align to the bottom end
                    .height(65.dp)
                    .background(color = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding()
                        .background(color = Color.White)
                        .height(65.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.ViewReports),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = PrimaryBlue,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                scope.launch {
                                    if (profilercheck.value == "Yes") {
                                        navController.navigate(AppRoute.QuizReport.route)
                                    } else {

                                        bottomSheetState.show()
                                    }
                                }
                            }// Align text horizontally
                    )
                }
            }
            val questionAnswerModelList =
                remember { mutableStateListOf<GetCompleteProfilerQuestionOptionsResponseModel.Data>() }

            LaunchedEffect(Unit) {
                viewModel.getCompleteProfilerQuestionAndOptionsModelLiveData.observeForever { networkStatus ->
                    when (networkStatus) {
                        is NetworkStatus.Idle -> {
                            isDialogVisible = false
                            Log.e("TAG", "QuizPerformanceScreen: loading")
                        }

                        is NetworkStatus.Loading -> {
                            isDialogVisible = true
                            Log.e("TAG", "QuizPerformanceScreen: loading")
                        }
                        is NetworkStatus.Success -> {
                            isDialogVisible = false
                            Log.e(
                                "TAG",
                                "QuizPerformanceScreen: done ---> " + networkStatus.data?.data.toString()
                            )
                            // Clear the existing list before adding new questions
                            questionAnswerModelList.clear()
                            // Check if data and data.questions are not null
                            networkStatus.data?.data?.let { questionAnswerModelList.addAll(it) }

                        }

                        is NetworkStatus.Error -> {
                            isDialogVisible = false
                            // Handle the error case, e.g., show a message to the user
                        }
                    }
                }
                viewModel.getCompleteProfilerQuestionAndOption()
            }
            ModalBottomSheetLayout(
                sheetState = bottomSheetState,
                sheetContent = {
                    // State to track the current question index
                    var currentIndex by remember { mutableIntStateOf(0) }
                    BottomSheetData(


                        lists = questionAnswerModelList,
                        currentIndex = currentIndex,
                        onNext = {
                            viewModel.getCompleteProfilerQuestionAndOptionSave(it)
                            if (currentIndex < questionAnswerModelList.size - 1) {
                                currentIndex++
                            }
                        },
                        onPrevious = {
                            if (currentIndex > 0) {
                                currentIndex--
                            }
                        }, hide = {
                            scope.launch {
                                bottomSheetState.hide()
                            }
                        }
                    )
                }
            ) {}
        }
    }
}
@Composable
fun QuizGrid(
    navController: NavController,
    quizzes: List<QuizItem>, onClick: (QuizItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        items(quizzes) { quiz ->
            QuizItemCard(quiz = quiz) { selectedQuiz ->
                onClick.invoke(quiz)
            }
        }
    }
}

data class QuizItem(val iconResId: Int, val name: String, val value: String)

@Composable
fun QuizItemCard(quiz: QuizItem, onClick: (QuizItem) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable() {
                onClick(quiz)
            }
            .heightIn(min = 110.dp) // Set a minimum height for uniformity
            .padding(10.dp)
            .border(
                1.dp,
                GrayLight02,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(start = 5.dp, top = 10.dp, bottom = 10.dp, end = 10.dp)
    ) {
        Image(
            painter = painterResource(id = quiz.iconResId),
            contentDescription = null,
            modifier = Modifier
                .width(32.dp)
                .height(32.dp)
                .background(Color.Unspecified)
                .weight(1f), alignment = Alignment.Center
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .weight(1.5f)
        ) {
            Text(
                text = quiz.value,
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 14.sp, fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
            )
            Text(
                text = quiz.name,
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 10.sp,
                textAlign = TextAlign.Start
            )
        }

    }
}
@SuppressLint("SuspiciousIndentation")
@Preview

@Composable
fun BottomSheetData(
    lists: SnapshotStateList<GetCompleteProfilerQuestionOptionsResponseModel.Data> = mutableStateListOf(),
    currentIndex: Int = -1,
    onNext: (GetCompleteProfilerQuestionAnswersSubmitRequestModel) -> Unit = {},
    onPrevious: () -> Unit = {},
    hide: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    )
    {
        var isSelected1 by remember { mutableStateOf(false) }
        var isSelected2 by remember { mutableStateOf(false) }
        var answerIdmain: Int
        // Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Complete Profiler",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 18.sp
                )
                Text(
                    text = "Complete Profiler to View Report",
                    fontSize = 14.sp,
                    color = GrayLight01
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier
                    .clickable { hide.invoke() }
                    .background(Color.Unspecified)
            )
        }
        // Progress Bar
        val steps = List(lists.size) { it }
        ProgressBarCompose(currentIndex + 1, steps, modifier = Modifier.padding(horizontal = 20.dp))
        // Store selected answers in a state variable
        val selectedAnswers = remember { mutableStateMapOf<Int, MutableState<List<Int>>>() }
        val selectedValue = remember { mutableStateOf("") }
        val answersList = mutableListOf<Answer>()
        val isSelectedItem: (String) -> Boolean = { selectedValue.value == it}
        val onChangeState: (String) -> Unit = { selectedValue.value = it }
            // Display the current question and options
        if (lists.isNotEmpty()) {
            val question = lists[currentIndex]
                Text(
                    text = question.question.questionText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 10.dp),
                    textAlign = TextAlign.Center
                )
            // Get the current question
            val currentQuestion = if (currentIndex in lists.indices) lists[currentIndex] else null

            // Display the current question and options
            currentQuestion?.let { question ->
                // Initialize the selected answers for the current question if not already done
                val questionId = question.question.id.toInt()
                val answersState =
                    selectedAnswers.getOrPut(questionId) { mutableStateOf(emptyList()) }

                question.question.options.let { optionsData ->
                    LazyColumn {
                        items(optionsData) { item ->
                            val answerId = item.id.toInt()
                            answerIdmain = item.id.toInt()
                            val isSelected = answersState.value.contains(answerId)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, bottom = 5.dp, top = 5.dp)
                                    .clickable {
                                        when (question.question.questionType) {
                                            "singlechoice" -> {
                                                // If it's a single choice question
                                                answersState.value = listOf(answerId)
                                            }

                                            "multiplechoice" -> {
                                                // If it's a multiple choice question
                                                answersState.value = listOf(answerId)
                                                val currentAnswers =
                                                    answersState.value.toMutableList()
                                                if (currentAnswers.contains(answerId)) {

                                                    // If the clicked answer is already selected, remove it
                                                    currentAnswers.remove(answerId)
                                                    answerIdmain = answerId

                                                } else {


                                                    currentAnswers.add(answerId)
                                                }
                                                // Update the state with the new list of answers
                                                answersState.value = currentAnswers
                                            }
                                        }
                                    }
                                    .selectable(

                                        selected = isSelectedItem(item.toString()),
                                        onClick = { onChangeState(item.toString()) },
                                        role = Role.RadioButton
                                    ),

                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                RadioButton(
                                    selected = isSelectedItem(item.toString()),
                                    onClick = { selectedValue.value

                                        val answer = Answer(
                                            id = answerId.toInt(),
                                            text = "" // Assuming text is empty; you can modify as needed
                                        )
                                        answersList.add(answer)},

                                    enabled = true
                                )
                                Text(
                                    text = item.text,
                                    modifier = Modifier.padding(start = 10.dp),
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
        // Navigation Buttons
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            BtnNextUi(
                title = stringResource(R.string.previous),
                onClick = onPrevious,
                enabled = currentIndex > 0
            )
            val context = LocalContext.current
            BtnNextUi(
                title = stringResource(R.string.continues),
                onClick = {
                    val mutableList = mutableListOf<Question>()
                    mutableList.clear()
                    // Iterate through selected answers
                    for ((questionId, answersState) in selectedAnswers) {
                        Log.d("SelectedAnswers", "Question ID: $questionId")
                   answersState.value
                        // Create a mutable list to hold answers for the current question
                     //   val answersList = mutableListOf<Answer>()
                        // Iterate through each selected answer ID
//                        for (answerId in selectedValue.value) {
//                            Log.e("SelectedAnswers", "   Answer ID: $answerId")
//                            val answer = Answer(
//                                id = answerId.toInt(),
//                                text = "" // Assuming text is empty; you can modify as needed
//                            )
//                            answersList.add(answer)
//                        }
                        // Create a Question object with the collected answers
                        val question = Question(
                            answer = answersList,
                            id = questionId,
                            questionType = "radio"
                        )
                        // Add the question to the mutable list
                        mutableList.add(question)
                    }
// Prepare the request model with the collected questions
                    val request = GetCompleteProfilerQuestionAnswersSubmitRequestModel(mutableList)
                    println("Request data is here --> $request")
                    onNext(request)
                  //  mutableList.clear()
                    answersList.clear()
                },
                enabled = currentIndex < lists.size - 1
            )
        }
    }
}
@Preview
@Composable
fun ProgressBarCompose(
    progress: Int = 1,
    steps: List<Int> = listOf(1, 2, 4, 5, 6),
    modifier: Modifier = Modifier.padding(horizontal = 25.dp, vertical = 5.dp)
) {
    // Ensure the steps list is not empty
    val stepsSize = steps.size
    if (stepsSize == 0) {
        // Handle the case where steps is empty
        return
    }
    // Ensure progress is within the valid range of 0 to the number of steps
    val normalizedProgress = remember { mutableStateOf(progress.coerceIn(1, steps.size)) }
    // Update the normalized progress based on the input progress value
    LaunchedEffect(progress) {
        normalizedProgress.value = progress.coerceIn(1, steps.size)
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Progress Bar
        LinearProgressIndicator(
          //  progress = (normalizedProgress.value - 1) / (steps.size - 1).toFloat(),
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(2.dp)),
            color = PrimaryBlue
            //backgroundColor = GrayLight02
        )
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Create circles for each step
            steps.forEachIndexed { index, step ->
                // Determine the color based on the progress
                val circleColor = when {
                    normalizedProgress.value > index + 1 -> Color.Green // Completed steps
                    normalizedProgress.value == 1 && index == 0 -> PrimaryBlue // First step is green
                    normalizedProgress.value == steps.size && index == steps.lastIndex -> Color.Green // Last step is green
                    normalizedProgress.value == index + 1 -> PrimaryBlue // Current step
                    else -> GrayLight02 // Future steps
                }
                // Determine the text to display based on the circle color
                val displayText = if (circleColor == Color.Green) {
                    "✓" // Checked text
                } else {
                    (step + 1).toString() // Regular step count
                }
                // Circle for the current step
                Text(
                    text = displayText,
                    modifier = Modifier
                        .background(shape = CircleShape, color = circleColor)
                        .padding(1.dp)
                        .size(25.dp)
                        .wrapContentSize(Alignment.Center),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if (index != steps.lastIndex) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
/*
@Preview
@Composable
fun ProgressBarCompose(
    progress: Int = 1,
    steps: List<Int> = listOf(1, 2, 4, 5, 6),
    modifier: Modifier = Modifier.padding(horizontal = 25.dp, vertical = 5.dp)
) {
    // Ensure the steps list is not empty
    val stepsSize = steps.size
    if (stepsSize == 0) {
        // Handle the case where steps is empty
        return
    }

    // Ensure progress is within the valid range of 0 to the number of steps
    val normalizedProgress = remember { mutableStateOf(progress.coerceIn(1, steps.size)) }

    // Update the normalized progress based on the input progress value
    LaunchedEffect(progress) {
        normalizedProgress.value = progress.coerceIn(1, steps.size)
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Progress Bar
        LinearProgressIndicator(
            progress = (normalizedProgress.value - 1) / (steps.size - 1).toFloat(),
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(2.dp)),
            color = PrimaryBlue, backgroundColor = GrayLight02
        )

        // Horizontal Scroll
        LazyRow(
            modifier = modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 25.dp)
        ) {
            items(steps.size) { index ->
                // Determine the color based on the progress
                val circleColor = when {
                    normalizedProgress.value > index + 1 -> Color.Green // Completed steps
                    normalizedProgress.value == 1 && index == 0 -> PrimaryBlue // First step is green
                    normalizedProgress.value == steps.size && index == steps.lastIndex -> Color.Green // Last step is green
                    normalizedProgress.value == index + 1 -> PrimaryBlue // Current step
                    else -> GrayLight02 // Future steps
                }

                // Determine the text to display based on the circle color
                val displayText = if (circleColor == Color.Green) {
                    "✓" // Checked text
                } else {
                    (steps[index] + 1).toString() // Regular step count
                }

                // Circle for the current step
                Box(
                    modifier = Modifier
                        .background(shape = CircleShape, color = circleColor)
                        .padding(1.dp)
                        .size(25.dp)
                        .wrapContentSize(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayText,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (index != steps.lastIndex) {
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}*/
/*
@Composable
fun ProgressBarCompose(
    progress: Int = 1,
    steps: List<Int> = listOf(1, 2, 4, 5, 6),
    modifier: Modifier = Modifier.padding(horizontal = 25.dp, vertical = 5.dp)
) {
    // Ensure the steps list is not empty
    val stepsSize = steps.size
    if (stepsSize == 0) {
        // Handle the case where steps is empty
        return
    }

    // Ensure progress is within the valid range of 0 to the number of steps
    val normalizedProgress = remember { mutableStateOf(progress.coerceIn(1, steps.size)) }

    // Update the normalized progress based on the input progress value
    LaunchedEffect(progress) {
        normalizedProgress.value = progress.coerceIn(1, steps.size)
    }

    // Create a LazyListState to control the scroll position
    val lazyListState = rememberLazyListState()

    // Update the scroll position when the progress changes
    LaunchedEffect(normalizedProgress.value) {
        lazyListState.scrollToItem(normalizedProgress.value - 1)
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Progress Bar
        LinearProgressIndicator(
            progress = (normalizedProgress.value - 1) / (steps.size - 1).toFloat(),
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(2.dp)),
            color = PrimaryBlue, backgroundColor = GrayLight02
        )

        Log.e("TAG", "ProgressBarCompose: ", )
        // Horizontal Scroll
        LazyRow(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 25.dp),
            state = lazyListState
        ) {
            items(steps.size) { index ->
                // Determine the color based on the progress
                val circleColor = when {
                    normalizedProgress.value > index + 1 -> Color.Green // Completed steps
                    normalizedProgress.value == 1 && index == 0 -> PrimaryBlue // First step is green
                    normalizedProgress.value == steps.size && index == steps.lastIndex -> Color.Green // Last step is green
                    normalizedProgress.value == index + 1 -> PrimaryBlue // Current step
                    else -> GrayLight02 // Future steps
                }

                // Determine the text to display based on the circle color
                val displayText = if (circleColor == Color.Green) {
                    "✓" // Checked text
                } else {
                    (steps[index] + 1).toString() // Regular step count
                }

                // Circle for the current step
                Box(
                    modifier = Modifier
                        .background(shape = CircleShape, color = circleColor)
                        .padding(1.dp)
                        .size(25.dp)
                        .wrapContentSize(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayText,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (index != steps.lastIndex) {
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}*/



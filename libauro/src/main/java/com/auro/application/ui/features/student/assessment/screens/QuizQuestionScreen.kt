package com.auro.application.ui.features.student.assessment.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.utlis.CommonFunction.getFileFromUri
import com.auro.application.data.utlis.CommonFunction.parsedHtmlText
import com.auro.application.ui.common_ui.RectangleBtnPrev
import com.auro.application.ui.common_ui.RectangleBtnUi1
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.components.TextWithIconOnLeft
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.assessment.model.AssessmentQuizQuestionsResponseModel
import com.auro.application.ui.features.student.assessment.model.AssessmentQuizQuestionsResponseModel.SendQuestionToCandidateWebApiResult
import com.auro.application.ui.features.student.assessment.model.AssessmentSaveQuestionRequestModel
import com.auro.application.ui.features.student.assessment.model.SubmitQuizRequestModel
import com.auro.application.ui.features.student.assessment.model.SubmittedQuestionData
import com.auro.application.ui.features.student.authentication.screens.Button
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.DarkRed1
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.GreenDark02
import com.auro.application.ui.theme.LightGreen03
import com.auro.application.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.HashMap

@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuizQuestionScreen(navHostController: NavHostController) {
    val context = LocalContext.current

    var assessmentPermissionDialog by remember { mutableStateOf(false) }
    // State to track the current question index
    var currentIndex by remember { mutableIntStateOf(0) }
    var studentViewModel: StudentViewModel = hiltViewModel()
    var loginViewModel: LoginViewModel = hiltViewModel()
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraExecutor = ContextCompat.getMainExecutor(context)
    var hasCameraPermission by remember { mutableStateOf(false) }

    var quizListData by remember {
        mutableStateOf<AssessmentQuizQuestionsResponseModel.QuizListData?>(
            null
        )
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
    }
    // Request camera permission if not already granted
    // Check permission and request if necessary
    if (ContextCompat.checkSelfPermission(
            LocalContext.current,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        // Camera permission granted
        hasCameraPermission = true
    }
    val questionAnswerModelList =
        remember { mutableStateListOf<SendQuestionToCandidateWebApiResult>() }
    var isDialogVisible by remember { mutableStateOf(false) }
    var assessmentClose by remember { mutableStateOf(false) }
    var assessmentReview by remember { mutableStateOf(false) }
    var isPreviewSelected by remember { mutableStateOf(false) }
    var isPractice = loginViewModel.getIsPractice()
    /* val currentMonth = currentMonth(isPractice)
     val currentYear = currentYear(isPractice)*/

    val nextConcept = loginViewModel.getSelectedNextConcept()!!.toInt()

    // Total time for the timer in milliseconds (10 minutes = 600000 ms)
    val totalTimeMillis = 10 * 60 * 1000L

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    // Remember the remaining time state
    var timeRemaining by remember { mutableStateOf(totalTimeMillis) }

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = msgLoader
    )
    /*    AssessmentPermissionDialogSheet() {
        assessmentPermissionDialog = false
    }*/
    questionAnswerModelList.clear()
    // Check if data and data.questions are not null
    quizListData = loginViewModel.getAssessmentQuestion()!!
    questionAnswerModelList.addAll(loginViewModel.getAssessmentQuestion()!!.data)
    if (assessmentClose) {
        AssessmentCloseSheet(
            studentViewModel,
            loginViewModel,
            languageData,
            navHostController,
            quizListData!!.AssessmentExamID
        ) {
            assessmentClose = false
        }
    }
    if (assessmentReview) {
        AssessmentReviewSheet(
            currentIndex = currentIndex,
            studentViewModel,
            loginViewModel,
            languageData,
            navHostController,
            quizListData!!.AssessmentExamID,
            onReview = {
                isPreviewSelected = true
                assessmentReview = false
                currentIndex = 0
            }
        ) {
            assessmentReview = false
        }
//
    }
    BackHandler {
        assessmentClose = true
    }

    // Timer logic using LaunchedEffect and coroutine delay
    LaunchedEffect(key1 = timeRemaining) {
        if (timeRemaining > 0) {
            delay(1000L) // Wait for 1 second
            timeRemaining -= 1000L // Decrease time by 1 second (1000 milliseconds)
        }
        if (timeRemaining <= 0L) {
            context.toast("Time's Up!")
            studentViewModel.submitQuiz(
                SubmitQuizRequestModel(
                    quizListData!!.AssessmentExamID.toString(),
                    loginViewModel.getExamId().toInt(),
                    "student"
                )
            )
        }
    }

    // Format the remaining time to minutes and seconds
    val minutes = (timeRemaining / 1000) / 60
    val seconds = (timeRemaining / 1000) % 60

    LaunchedEffect(Unit) {

        /*  // to fetch all questions
          studentViewModel.getQuizQuestionResponse.observeForever { networkStatus ->
              when (networkStatus) {
                  is NetworkStatus.Idle -> {
                      isDialogVisible = false
                  }

                  is NetworkStatus.Loading -> {
                      isDialogVisible = true
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
                      quizListData = networkStatus.data?.data
                      networkStatus.data?.data?.data?.let { questionAnswerModelList.addAll(it) }
  //                    networkStatus.data?.data?.let { quizListData.add(it) }

                  }

                  is NetworkStatus.Error -> {
                      isDialogVisible = false
                      // Handle the error case, e.g., show a message to the user
                  }
              }
          }*/

        // to save single que & answer
        studentViewModel.saveQuestionModelResponse.observeForever { networkStatus ->
            when (networkStatus) {
                is NetworkStatus.Idle -> {
                    isDialogVisible = false
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (currentIndex < questionAnswerModelList.size - 1) {
                        currentIndex++
                    }
                }
                // Clear the existing list before adding new questions

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    if (networkStatus.message == "answerId should not be empty") {
                        if (currentIndex < questionAnswerModelList.size - 1) {
                            currentIndex++
                        }
                    } else {
                        context.toast(networkStatus.message)
                    }
                    // Handle the error case, e.g., show a message to the user
                }
            }
        }

//        to get final Result
        studentViewModel.submitQuizResponse.observeForever { networkStatus ->
            when (networkStatus) {
                is NetworkStatus.Idle -> {
                    isDialogVisible = false
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    navHostController.popBackStack()
                    loginViewModel.saveStudentQuizResultData(networkStatus.data!!.data)
                    navHostController.navigate(AppRoute.QuizResultScreen.route)

                }
                // Clear the existing list before adding new questions

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(networkStatus.message)
                    // Handle the error case, e.g., show a message to the user
                }
            }
        }


        /*  // Static data for getting question
          studentViewModel.getAssessmentQuestionData(
              AssessmentGetQuizRequestModel(
                  loginViewModel.getUserId()!!.toInt(),
                  loginViewModel.getExamName(),
                  loginViewModel.getStudentSelectedConceptData().nextAttempt!!.toInt(),
                  loginViewModel.getLanguageCode(),
                  loginViewModel.getStudentSelectedSubjectData().subjectName,
                  loginViewModel.getGrade()!!.toInt(),
                  loginViewModel.getStudentSelectedConceptData().ID.toString(),
                  currentMonth.toInt(),
                  currentYear.toInt(),
                  0
              )
          )*/

        if (!isPractice) {
            while (true) {

                imageCapture?.takePicture(
//                0,
                    cameraExecutor,
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {

                            val buffer = image.planes[0].buffer
                            val bytes = ByteArray(buffer.capacity())
                            buffer.get(bytes)
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

//                    }(outputFileResults: ImageCapture.OutputFileResults) {
                            capturedImageUri = bitmapToUri(context, bitmap)

                            if (capturedImageUri != null) {
                                val file =
                                    getFileFromUri(context, capturedImageUri)
                                studentViewModel.saveQuizImage(
                                    loginViewModel.getExamId(),
                                    "1",
                                    file
                                )
                            }

                        }

                        override fun onError(exception: ImageCaptureException) {
                            // Handle error
//                        context.toast(exception.message.toString())
                            /*Log.d(
                                "ImageException1:",
                                "" + exception.message.toString()
                            )*/
                        }
                    })


                // Delay for 10 sec (10,000 milliseconds)
                delay(10 * 1000L)
            }
        }

    }

    AuroscholarAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(com.auro.application.ui.theme.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .wrapContentHeight()
            )
            {
                Surface(
                    tonalElevation = 10.dp, // Set the elevation here
                    color = White,
                ) {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .wrapContentHeight()
                        ) {
                            Image(
                                painter = painterResource(R.drawable.back_icon),
                                contentDescription = "logo",
                                modifier = Modifier
                                    .clickable {
//                                        navHostController.popBackStack()
                                        assessmentClose = true
//                                        navHostController.navigate(AppRoute.StudentDashboard.route)
                                    },

                                colorFilter = ColorFilter.tint(Black)
                            )
                            Text(
                                text = loginViewModel.getSelectedConceptName().toString(),
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .wrapContentSize(),
                                fontFamily = FontFamily(
                                    Font(R.font.inter_bold, FontWeight.Bold)
                                ),
                                fontSize = 16.sp,
                                color = Black,
                                textAlign = TextAlign.Start
                            )
                            TextWithIconOnLeft(
                                String.format("%02d:%02d", minutes, seconds),
                                icon = ImageVector.vectorResource(id = R.drawable.ic_clock),
                                textColor = if (minutes < 1) {
                                    DarkRed1
                                } else {
                                    GreenDark02
                                },
                                iconColor = if (minutes < 1) {
                                    DarkRed1
                                } else {
                                    GreenDark02
                                },
                                modifier = Modifier
                                    .padding(vertical = 5.dp)
                                    .background(
                                        color = LightGreen03,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(5.dp),
                                onClick = {

                                }
                            )
                        }

                        // image
                        Column(
                            modifier = Modifier.wrapContentSize()

                        ) {

                            // for camera frame
                            if (!isPractice) {
                                if (hasCameraPermission) {
                                    val lifecycleOwner = LocalLifecycleOwner.current

                                    AndroidView(
                                        modifier = Modifier
                                            .width(90.dp)
                                            .height(100.dp)
                                            .padding(20.dp),
                                        factory = { ctx ->
                                            val previewView = PreviewView(ctx).apply {
                                                layoutParams = android.view.ViewGroup.LayoutParams(
                                                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                                )
                                                scaleType =
                                                    PreviewView.ScaleType.FILL_CENTER  // Other options: FILL_CENTER, FIT_END
                                            }

                                            // Set up the CameraX Preview use case
                                            val cameraProviderFuture =
                                                ProcessCameraProvider.getInstance(ctx)
                                            cameraProviderFuture.addListener({
                                                val cameraProvider = cameraProviderFuture.get()
                                                val preview = androidx.camera.core.Preview.Builder()
                                                    .setTargetResolution(
                                                        Size(
                                                            100,
                                                            100
                                                        )
                                                    ) // Target size 100x100
                                                    .build().also {
                                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                                    }

                                                // Setup Image Capture
                                                imageCapture = ImageCapture.Builder()
                                                    .setTargetResolution(
                                                        Size(
                                                            100,
                                                            100
                                                        )
                                                    )  // Target Resolution
                                                    .build()
                                                // Select back camera as default
                                                val cameraSelector =
                                                    CameraSelector.DEFAULT_FRONT_CAMERA

                                                try {
                                                    // Unbind use cases before rebinding
                                                    cameraProvider.unbindAll()

                                                    // Bind camera to the lifecycle and preview use case
                                                    cameraProvider.bindToLifecycle(
                                                        lifecycleOwner,
                                                        cameraSelector,
                                                        preview,
                                                        imageCapture
                                                    )
                                                } catch (exc: Exception) {
                                                    // Handle exception
                                                }
                                            }, ContextCompat.getMainExecutor(ctx))

                                            previewView
                                        }
                                    )
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    Box(modifier = Modifier.size(100.dp)) {
                                        Text(text = "Camera permission not granted")
                                    }
                                }
                            }
                            LaunchedEffect(Unit) {

                            }
                        }
                    }
                }
                // Progress Bar
//                val steps = List(lists.size) { it }
                val steps = List(1) { it }
                val skipped = List(1) { it }
                /*ProgressBarQuiz(
                    currentIndex + 1,
                    steps,
                    modifier = Modifier.padding(bottom = 10.dp)
                )*/

                QuizProgressBar(
                    questionIndex = currentIndex,
                    questionAnswerModelList.size,
                    steps, skipped, currentIndex + 1
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(bottom = 20.dp)
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

                AssessmentQuestionSheet(
                    isPreviewSelected = mutableStateOf(isPreviewSelected),
                    isDialogVisible, loginViewModel,
                    languageData,
                    studentViewModel, context,
                    quizData = quizListData,
                    lists = questionAnswerModelList,
                    currentIndex = currentIndex,
                    onNext = {
                        isDialogVisible = true
                        studentViewModel.saveAssessmentQuestionData(it)
                        if (currentIndex >= 9) {
                            assessmentReview = true
//                            context.toast("You are on last question! please Submit all questions")
                        }
                    },
                    onPrevious = {
                        if (currentIndex > 0) {
                            currentIndex--
                        } else {
                            context.toast("You are on 1st question")
                        }
                    },
                    onSubmit = {
                        if (currentIndex >= 9) {
                            assessmentReview = true
                        }
                    },
                    hide = {
                        /* scope.launch {
                             bottomSheetState.hide()
                         }*/
                    }
                )
            }
        }
    }
}

@Composable
fun AssessmentQuestionSheet(
    isPreviewSelected: MutableState<Boolean>,
    isDialogVisible: Boolean,
    loginViewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    studentViewModel: StudentViewModel,
    context: Context,
    quizData: AssessmentQuizQuestionsResponseModel.QuizListData?,
    lists: SnapshotStateList<SendQuestionToCandidateWebApiResult> = mutableStateListOf(),
    currentIndex: Int = -1,
    onNext: (AssessmentSaveQuestionRequestModel) -> Unit = {},
    onPrevious: () -> Unit = {},
    onSubmit: () -> Unit = {},
    hide: () -> Unit = {},
) {
    var isPreviousSelected by remember { mutableStateOf(false) }
    val submittedQuestionList = remember { mutableStateListOf<SubmittedQuestionData>() }
    val questionDataItem by remember { mutableStateOf<SubmittedQuestionData?>(null) }
//    var isAnsSelectedIndex by remember { mutableStateOf(0) }

    var isSelected1 by remember { mutableStateOf(false) }
    var isSelected2 by remember { mutableStateOf(false) }
    var isSelected3 by remember { mutableStateOf(false) }
    var isSelected4 by remember { mutableStateOf(false) }
    var selectedAns by remember { mutableStateOf("") }

    var isQuestImage by remember { mutableStateOf(false) }
    var isImage1 by remember { mutableStateOf(false) }
    var isImage2 by remember { mutableStateOf(false) }
    var isImage3 by remember { mutableStateOf(false) }
    var isImage4 by remember { mutableStateOf(false) }

    if (submittedQuestionList.isNotEmpty()) {
        for (questionData in submittedQuestionList) {
            // Do something with each question

            LaunchedEffect(currentIndex) {
                if (isPreviousSelected || isPreviewSelected.value) {
                    if (questionData.questionNo == currentIndex.toString()) {

                        selectedAns = questionData.selectedAnswer
                        isSelected1 = selectedAns == "Answer1"
                        isSelected2 = selectedAns == "Answer2"
                        isSelected3 = selectedAns == "Answer3"
                        isSelected4 = selectedAns == "Answer4"
                    } else {
                        // if answers not given
                        if (submittedQuestionList.size <= currentIndex) {
                            isSelected1 = false
                            isSelected2 = false
                            isSelected3 = false
                            isSelected4 = false
                            selectedAns = ""
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = White)
            .verticalScroll(rememberScrollState())
    ) {

        // Display the current question and options
        if (lists.isNotEmpty()) {
            val question = lists[currentIndex]
            val htmlString = parsedHtmlText(question.QuestionText)
            var questionString = ""
            questionString = if (question.QuestionText.contains("<img src=")) {
                parsedHtmlText(question.QuestionText.replace(Regex("<img[^>]*>"), ""))
            } else {
                htmlString
            }
            Text(
                text = questionString,
                fontSize = 14.sp,
                fontFamily = FontFamily(
                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                color = Black,
                textAlign = TextAlign.Start
            )
            if (question.QuestionText.contains("<img src=")) {
                val htmlString = question.QuestionText.trimIndent()
                val imgSrcRegex = "<img\\s+[^>]*src=['\"]([^'\"]+)['\"]".toRegex()

                val questionImageUrl = imgSrcRegex.find(htmlString)?.groups?.get(1)?.value
//                println("ImageFormat:$questionImageUrl")
                if (isQuestImage) {
                    ExpandableImage(questionImageUrl.toString()) {

                        isQuestImage = false
                    }
                }
                // Image part
                AsyncImage(
                    model = questionImageUrl,
                    contentDescription = "Question image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isQuestImage = true }
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // option 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, bottom = 5.dp)
                    .clickable {
                        isSelected1 = true
                        isSelected2 = false
                        isSelected3 = false
                        isSelected4 = false
                        selectedAns = "Answer1"
                        // If it's a single choice question
//                        answersState.value = listOf(answerId)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = isSelected1,
                    onClick = {
                        isSelected1 = true
                        isSelected2 = false
                        isSelected3 = false
                        isSelected4 = false
                        selectedAns = "Answer1"
                    }
                )
                val answer1String = parsedHtmlText(question.Answer1)
                var answer1 = ""
                answer1 = if (question.Answer1.contains("<img src=")) {
                    parsedHtmlText(question.Answer1.replace(Regex("<img[^>]*>"), ""))
                } else {
                    answer1String
                }
                Text(
                    text = answer1,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 10.dp),
                    color = Color.Black,
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    )
                )
                if (question.Answer1.contains("<img src=")) {

                    val htmlString = question.Answer1.trimIndent()
                    val imgSrcRegex = "<img\\s+[^>]*src=['\"]([^'\"]+)['\"]".toRegex()

                    val optionImageUrl1 = imgSrcRegex.find(htmlString)?.groups?.get(1)?.value
//                    println(optionImageUrl1)
                    if (isImage1) {
                        ExpandableImage(optionImageUrl1.toString()) {

                            isImage1 = false
                        }
                    }
                    // Image part
                    AsyncImage(
                        model = optionImageUrl1,
                        contentDescription = "Question image",
                        modifier = Modifier
                            .clickable { isImage1 = true }
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // option 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, bottom = 5.dp, top = 5.dp)
                    .clickable {
                        // If it's a single choice question
//                        answersState.value = listOf(answerId)
                        isSelected1 = false
                        isSelected2 = true
                        isSelected3 = false
                        isSelected4 = false
                        selectedAns = "Answer2"
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                /* CircleCheckbox(
                     selected = isSelected2,
                     enabled = true,
                     onChecked = { *//* No action needed here *//* }
                )*/
                RadioButton(
                    selected = isSelected2,
                    onClick = {
                        isSelected1 = false
                        isSelected2 = true
                        isSelected3 = false
                        isSelected4 = false
                        selectedAns = "Answer2"
                    }
                )

                val answer2String = parsedHtmlText(question.Answer2)
                var answer2 = ""
                answer2 = if (question.Answer2.contains("<img src=")) {
                    parsedHtmlText(question.Answer2.replace(Regex("<img[^>]*>"), ""))
                } else {
                    answer2String
                }

                Text(
                    text = answer2,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 10.dp),
                    color = Color.Black,
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    )
                )
                if (question.Answer2.contains("<img src=")) {
                    val htmlString = question.Answer2.trimIndent()
                    val imgSrcRegex = "<img\\s+[^>]*src=['\"]([^'\"]+)['\"]".toRegex()

                    val optionImageUrl2 = imgSrcRegex.find(htmlString)?.groups?.get(1)?.value
//                    println(optionImageUrl2)
                    if (isImage2) {

                        ExpandableImage(optionImageUrl2.toString()) {
                            isImage2 = false
                        }
                    }
                    // Image part
                    AsyncImage(
                        model = optionImageUrl2,
                        contentDescription = "Question image",
                        modifier = Modifier
                            .clickable { isImage2 = true }
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // option 3
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, bottom = 5.dp, top = 5.dp)
                    .clickable {
                        // If it's a single choice question
//                        answersState.value = listOf(answerId)
                        isSelected1 = false
                        isSelected2 = false
                        isSelected3 = true
                        isSelected4 = false
                        selectedAns = "Answer3"
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                /*CircleCheckbox(
                    selected = isSelected3,
                    enabled = true,
                    onChecked = {  }
                )*/
                RadioButton(
                    selected = isSelected3,
                    onClick = {
                        isSelected1 = false
                        isSelected2 = false
                        isSelected3 = true
                        isSelected4 = false
                        selectedAns = "Answer3"
                    }
                )
                val answer3String = parsedHtmlText(question.Answer3)
                var answer3 = ""
                answer3 = if (question.Answer3.contains("<img src=")) {
                    parsedHtmlText(question.Answer3.replace(Regex("<img[^>]*>"), ""))
                } else {
                    answer3String
                }

                Text(
                    text = answer3,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 10.dp),
                    color = Color.Black,
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    )
                )
                if (question.Answer3.contains("<img src=")) {
                    val htmlString = question.Answer3.trimIndent()
                    val imgSrcRegex = "<img\\s+[^>]*src=['\"]([^'\"]+)['\"]".toRegex()

                    val optionImageUrl3 = imgSrcRegex.find(htmlString)?.groups?.get(1)?.value
//                    println(optionImageUrl3)
                    if (isImage3) {

                        ExpandableImage(optionImageUrl3.toString()) {
                            isImage3 = false

                        }
                    }
                    // Image part
                    AsyncImage(
                        model = optionImageUrl3,
                        contentDescription = "Question image",
                        modifier = Modifier
                            .clickable { isImage3 = true }
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // option 4
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, bottom = 5.dp, top = 5.dp)
                    .clickable {
                        // If it's a single choice question
//                        answersState.value = listOf(answerId)
                        isSelected1 = false
                        isSelected2 = false
                        isSelected3 = false
                        isSelected4 = true
                        selectedAns = "Answer4"
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                /* CircleCheckbox(
                     selected = isSelected4,
                     enabled = true,
                     onChecked = { *//* No action needed here *//* }
                )*/
                RadioButton(
                    selected = isSelected4,
                    onClick = {
                        isSelected1 = false
                        isSelected2 = false
                        isSelected3 = false
                        isSelected4 = true
                        selectedAns = "Answer4"
                    }
                )
                val answer4String = parsedHtmlText(question.Answer4)
                var answer4 = ""
                answer4 = if (question.Answer4.contains("<img src=")) {
                    parsedHtmlText(question.Answer4.replace(Regex("<img[^>]*>"), ""))
                } else {
                    answer4String
                }
                Text(
                    text = answer4,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 10.dp),
                    color = Color.Black,
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    )
                )
                if (question.Answer4.contains("<img src=")) {
                    val htmlString = question.Answer4.trimIndent()
                    val imgSrcRegex = "<img\\s+[^>]*src=['\"]([^'\"]+)['\"]".toRegex()

                    val optionImageUrl4 = imgSrcRegex.find(htmlString)?.groups?.get(1)?.value
//                    println(optionImageUrl4)
                    if (isImage4) {
                        ExpandableImage(optionImageUrl4.toString()) {
                            isImage4 = false

                        }
                    }
                    // Image part
                    AsyncImage(
                        model = optionImageUrl4,
                        contentDescription = "Question image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable { isImage4 = true },
                        contentScale = ContentScale.Crop,
                    )
                }
            }

            // Get the current question
            val currentQuestion = if (currentIndex in lists.indices) lists[currentIndex] else null

            // Display the current question and options
            currentQuestion?.let { question ->
                // Initialize the selected answers for the current question if not already done

            }

            // Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 10.dp,
                        vertical = 10.dp
                    ), // optional padding around the row
                verticalAlignment = Alignment.CenterVertically,
            ) {

                RectangleBtnPrev(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f),
                    onClick = {
//                        isNextSelected = false
                        isPreviousSelected = true
                        onPrevious()
                    },
                    title = if (languageData[LanguageTranslationsResponse.PREVIOUS].toString() == "") {
                        stringResource(R.string.previous)
                    } else {
                        languageData[LanguageTranslationsResponse.PREVIOUS].toString()
                    },
                    enabled = currentIndex > 0
                )

                RectangleBtnUi1(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .weight(1f),
                    onClick = {
                        if (currentIndex <= 9) {
                            val selectedOption = selectedAns
                            // create an obj to save Question and ans
                            val answer = AssessmentSaveQuestionRequestModel(
                                quizData!!.AssessmentExamID.toString(),
                                selectedOption,
                                question.ID,
                                currentIndex + 1,
                                loginViewModel.getExamId().toInt()
                                // Assuming text is empty; you can modify as needed
                            )
//                            questionDataItem!!.selectedAnswer = selectedAns
                            println("Request data is here --> $answer")
                            if (currentIndex < 9) {
                                isSelected1 = false
                                isSelected2 = false
                                isSelected3 = false
                                isSelected4 = false
                                selectedAns = ""
                            }
//                            isAnsSelectedIndex = currentIndex
//                            isNextSelected = true
//                            isPreviousSelected = false
                            onNext(answer)

                            // save data for review
                            val submittedData = SubmittedQuestionData(
                                currentIndex.toString(),
                                (currentIndex + 1).toString(),  // current Question No
                                question.ID,
                                selectedOption,
                                true
                            )
                            // Check if a question with the same ID already exists in the list
                            val existingIndex =
                                submittedQuestionList.indexOfFirst { it.questionId == question.ID }

                            if (existingIndex != -1) {
                                // If found, replace the existing entry
                                submittedQuestionList[existingIndex] = submittedData
                            } else {
                                // If not found, add the new entry
                                submittedQuestionList.add(submittedData)
                            }
                        } else {
                            onSubmit()
                        }

                    },
                    title = if (currentIndex >= 9) {
                        stringResource(R.string.txt_submit)
                    } else {
                        if (languageData[LanguageTranslationsResponse.NEXT].toString() == "") {
                            stringResource(R.string.next)
                        } else {
                            languageData[LanguageTranslationsResponse.NEXT].toString()
                        }
                    },
                    enabled = true
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentPermissionDialogSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items in the Column
        ) {
            Icon(
                painter = painterResource(id = R.drawable.bottomsheet_disclaimer), // Replace with your drawable resource
                contentDescription = null, // Provide a description for accessibility purposes
                modifier = Modifier.wrapContentSize(),
                tint = Unspecified
            )

            Text(
                stringResource(id = R.string.disclaimer),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                "To enable Do Not Disturb Mode, you need to grant permission",
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = GrayLight01, fontSize = 12.sp, fontWeight = FontWeight.Normal
                ),
                textAlign = TextAlign.Center
            )
            Text(
                stringResource(id = R.string.camera_assessment_disclaimer),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                color = Gray, fontSize = 14.sp, fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Divider(
                color = GrayLight02,
                thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        },

                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_go_to_permission),
                            modifier = Modifier
                                .background(
                                    color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ProgressBarQuiz(
    progress: Int = 1,
    steps: List<Int> = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
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

        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable {

                },
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

@Composable
fun QuizProgressBar(
    questionIndex: Int,
    totalQuestions: Int,
    answeredQuestions: List<Int>,
    skippedQuestions: List<Int>,
    currentQuestion: Int
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 1..totalQuestions) {
//            Log.d("SelectedIndicator:", "$i ..$questionIndex")
            when {
                answeredQuestions.contains(i) -> {
                    // Question answered
                    CircleIndicator(
                        number = i,
                        color = PrimaryBlue, // Color for done
                        textColor = White,
                        onClick = {

                        }
                    )
                }

                skippedQuestions.contains(i) -> {
                    // Question skipped
                    CircleIndicator(
                        number = i,
                        color = Gray.copy(alpha = 0.5f), // Color for skipped
                        textColor = Gray,
                        onClick = {

                        }
                    )
                }

                i == currentQuestion -> {
                    // Current question
                    CircleIndicator(
                        number = i,
                        color = White, // No fill for current
                        border = true, // Show border for current question
                        textColor = PrimaryBlue,
                        onClick = {

                        }
                    )
                }

                else -> {
                    // Unanswered and unskipped questions
                    CircleIndicator(
                        number = i,
                        color = GrayLight02, // Default color
                        border = false,
                        textColor = Gray,
                        onClick = {

                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CircleIndicator(
    number: Int,
    color: Color,
    border: Boolean = false,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(28.dp)
            .border(
                width = if (border) 2.dp else 0.dp,
                color = PrimaryBlue,
                shape = CircleShape
            )
            .background(
                color = color,
                shape = CircleShape
            )
    ) {
        Text(
            text = number.toString(),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = textColor,
            textAlign = TextAlign.Start
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentCloseSheet(
    studentViewModel: StudentViewModel,
    loginViewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    navHostController: NavHostController,
    ExamAssessmentId: String?,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 25.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items in the Column
        ) {
            Icon(
                painter = painterResource(id = R.drawable.bottomsheet_disclaimer), // Replace with your drawable resource
                contentDescription = null, // Provide a description for accessibility purposes
                modifier = Modifier.wrapContentSize(),
                tint = Unspecified
            )

            Text(
                stringResource(id = R.string.txt_exit_quiz),
                modifier = Modifier.padding(4.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                stringResource(id = R.string.txt_exit_quiz_desc),
                modifier = Modifier.padding(4.dp),
                color = Gray, fontSize = 14.sp, fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Divider(
                color = GrayLight02,
                thickness = 0.8.dp
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (languageData[LanguageTranslationsResponse.KEY_CANCEL].toString() == "") {
                                stringResource(id = R.string.txt_cancel)
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_CANCEL].toString()
                            },
                            modifier = Modifier.background(
                                color = Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ),
                            color = PrimaryBlue,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    TextButton(
                        onClick = {
                            onDismiss()
                            studentViewModel.submitQuiz(
                                SubmitQuizRequestModel(
                                    ExamAssessmentId.toString(),
                                    loginViewModel.getExamId().toInt(),
                                    "student"
                                )
                            )
                        },
                        modifier = Modifier
                            .background(
                                color = PrimaryBlue,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .weight(1f)
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_yes_exit_quiz),
                            modifier = Modifier
                                .background(
                                    color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            color = White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentReviewSheet(
    currentIndex: Int,
    studentViewModel: StudentViewModel,
    loginViewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    navHostController: NavHostController,
    ExamAssessmentId: String?,
    onReview: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    // Total time for the timer in milliseconds (20 seconds = 20000 ms)
    val totalTimeMillis = 20 * 1000L

    // Remember the remaining time state
    var timeRemaining by remember { mutableStateOf(totalTimeMillis) }

    // Timer logic using LaunchedEffect and coroutine delay
    LaunchedEffect(key1 = timeRemaining) {
        if (timeRemaining > 0) {
            delay(1000L) // Wait for 1 second
            timeRemaining -= 1000L // Decrease time by 1 second (1000 milliseconds)
        }
        if (timeRemaining <= 0L) {
            onDismiss()
            studentViewModel.submitQuiz(
                SubmitQuizRequestModel(
                    ExamAssessmentId.toString(),
                    loginViewModel.getExamId().toInt(),
                    "student"
                )
            )
        }
    }

    // Format the remaining time to minutes and seconds
    val seconds = (timeRemaining / 1000) % 60

    val sec = seconds.toString() + "sec"

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 25.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items in the Column
        ) {
            Icon(
                painter = painterResource(id = R.drawable.bottomsheet_disclaimer), // Replace with your drawable resource
                contentDescription = null, // Provide a description for accessibility purposes
                modifier = Modifier.wrapContentSize(),
                tint = Unspecified
            )

            Text(
                stringResource(id = R.string.txt_sure_submit_quiz),
                modifier = Modifier.padding(4.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            /* Text(
                 stringResource(id = R.string.txt_sure_submit),
 //                stringResource(id = R.string.txt_sure_submit_quiz),
                 modifier = Modifier.padding(4.dp),
                 color = Gray, fontSize = 14.sp, fontWeight = FontWeight.Normal,
                 textAlign = TextAlign.Center
             )*/

            Divider(
                color = GrayLight02,
                thickness = 0.8.dp
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 15.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                            studentViewModel.submitQuiz(
                                SubmitQuizRequestModel(
                                    ExamAssessmentId.toString(),
                                    loginViewModel.getExamId().toInt(),
                                    "student"
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(5.dp)
                            .background(
                                color = White,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_submit),
                            modifier = Modifier
                                .background(color = White)
                                .padding(vertical = 5.dp),
                            color = PrimaryBlue,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    TextButton(
                        onClick = {
                            onReview()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(5.dp)
                            .background(
                                color = PrimaryBlue,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Text(

                            text = stringResource(id = R.string.txt_review) + " (" + sec + ")",
                            modifier = Modifier
                                .background(color = PrimaryBlue)
                                .padding(vertical = 5.dp),
                            color = White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun QuizButton(
    currentIndex: Int = -1,
    onNext: (AssessmentSaveQuestionRequestModel) -> Unit = {},
    onPrevious: () -> Unit = {}
) {
    var isSelected1 by remember { mutableStateOf(false) }
    var isSelected2 by remember { mutableStateOf(false) }
    var isSelected3 by remember { mutableStateOf(false) }
    var isSelected4 by remember { mutableStateOf(false) }
    var selectedAns by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp), // optional padding around the row
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RectangleBtnPrev(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .weight(1f),
            onClick = {
                isSelected1 = false
                isSelected2 = false
                isSelected3 = false
                isSelected4 = false
                selectedAns = ""
                onPrevious()
            },
            title = stringResource(R.string.previous),
            enabled = currentIndex > 0
        )

        RectangleBtnUi1(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .weight(1f),
            onClick = {
                isSelected1 = false
                isSelected2 = false
                isSelected3 = false
                isSelected4 = false
                selectedAns = ""
                onPrevious()
            },
            title = stringResource(R.string.next),
            enabled = currentIndex >= 9
        )
    }
}*/

@Composable
fun ExpandableImage(
    optionImageUrl1: String,
    onDismissRequest: () -> Unit ) {
    /* var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { isExpanded = true }
    ) {
        AsyncImage(
            model = optionImageUrl1,
            contentDescription = "Question image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )*/

    // Show dialog when the image is clicked
    // if (isExpanded) {
    //dialog for zoom out image
//    Dialog(onDismissRequest = { onDismissRequest() },) {
//        Box(modifier = Modifier.fillMaxSize()) {
//            AsyncImage(
//                model = optionImageUrl1,
//                contentDescription = "Expanded Question image",
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Fit, // Change scale for better view
//            )
//        }
//        Spacer(modifier = Modifier.padding(50.dp))
//        Row(
//            modifier = Modifier.fillMaxWidth().padding(15.dp),
//            horizontalArrangement = Arrangement.End,
//        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_cancel), // Replace with your drawable resource
//                tint = Color.Unspecified,
//                contentDescription = null,
//                modifier = Modifier.size(40.dp, 40.dp)
//                    .clickable { onDismissRequest }
//                    .wrapContentSize()
//                    .background(Color.Unspecified),
//            )
//        }
//    }


    var openDialog by remember { mutableStateOf(true) }
   if (openDialog) {
        Dialog(
            onDismissRequest = {
                openDialog = false
            },
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = optionImageUrl1,
                    contentDescription = "Expanded Question image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit, // Change scale for better view
                )
            }

            Spacer(modifier = Modifier.padding(50.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(15.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    modifier = Modifier.align(Alignment.CenterVertically).padding(bottom = 10.dp),
                    onClick = {openDialog = false},
                    title = "Close"
                )
            }

        }

   }


}




fun bitmapToUri(context: Context, bitmap: Bitmap): Uri? {
    // Create a temporary file in the cache directory
    val cachePath = File(context.cacheDir, "images")
    cachePath.mkdirs() // Ensure the directory exists
    val file = File(cachePath, "temp_image.jpg")

    try {
        // Save the bitmap to the file
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }

    // Get the URI of the file using FileProvider
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}

@Preview(showBackground = true)
@Composable
fun QuizQuestionScreen() {
    AuroscholarAppTheme {
//        QuizQuestionScreen(navHostController = rememberNavController())
    }
}
package com.auro.application.ui.features.student.assessment.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.utlis.CommonFunction.currentMonth
import com.auro.application.data.utlis.CommonFunction.currentYear
import com.auro.application.data.utlis.CommonFunction.getFileFromUri
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.assessment.model.AssessmentGetQuizRequestModel
import com.auro.application.ui.features.student.assessment.model.CreateExamImageRequestModel
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightRed01
import com.auro.application.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.HashMap
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuizDisclaimerScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val studentViewModel: StudentViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
    var assessmentDisclaimer by remember { mutableStateOf(true) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraExecutor = ContextCompat.getMainExecutor(context)
    var assessmentExamId by remember { mutableStateOf("") }
    var examId by remember { mutableStateOf("") }
    var isDialogVisible by remember { mutableStateOf(false) }
    var isQuestionFetched = remember { mutableStateOf(false) }
    val currentMonth = currentMonth(false)
    val currentYear = currentYear(false)
    var hasCameraPermission by remember { mutableStateOf(false) }
    var disclaimer = remember { mutableStateOf("") }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = msgLoader
    )
    BackHandler {
        navHostController.popBackStack()
        navHostController.navigate(AppRoute.StudentQuizList.route)
    }

    if (assessmentDisclaimer) {
        AssessmentDisclaimerSheet(
            isQuestionFetched = isQuestionFetched,
            currentMonth,
            currentYear,
            disclaimer,
            navHostController,
            loginViewModel,
            languageData,
            studentViewModel
        ) {
            assessmentDisclaimer = false
        }
    }
    LaunchedEffect(Unit) {

        studentViewModel.getQuizQuestionResponse.observeForever { networkStatus ->
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
                            examId = networkStatus.data.data.exam_id
                            assessmentExamId = networkStatus.data.data.AssessmentExamID.toString()
                            loginViewModel.saveAssessmentQuestion(networkStatus.data.data)
                            loginViewModel.saveExamId(examId)
                            isQuestionFetched.value = true
//                            loginViewModel.saveAssessmentExamId(assessmentExamId)
                        } else {
                          //  context.toast(networkStatus.data.data.message)
                            navHostController.navigateUp()
                        }
                    }
                    isDialogVisible = false

//                    networkStatus.data?.data?.let { quizListData.add(it) }

                }

                is NetworkStatus.Error -> {

                    isDialogVisible = false
                  //  Toast.makeText(context, networkStatus.message, Toast.LENGTH_SHORT).show()

                    // Handle the error case, e.g., show a message to the user
                }
            }
        }

        studentViewModel.saveExamFolderResponse.observeForever { networkStatus ->
            when (networkStatus) {
                is NetworkStatus.Idle -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (networkStatus.data!!.isSuccess) {
                        if (capturedImageUri != null) {
                            val file = getFileFromUri(context, capturedImageUri)
                            // 2 for preQuiz Image
                            studentViewModel.saveQuizImage(loginViewModel.getExamId(), "2", file)
                        }
                    }
                    isDialogVisible = false

                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                 //   Toast.makeText(context, networkStatus.message, Toast.LENGTH_SHORT).show()

                    // Handle the error case, e.g., show a message to the user
                }
            }
        }

        studentViewModel.saveExamImageResponse.observeForever { networkStatus ->
            when (networkStatus) {
                is NetworkStatus.Idle -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    if (networkStatus.data!!.isSuccess) {
                        navHostController.popBackStack()
                        loginViewModel.saveIsPractice(false)
                        navHostController.navigate(AppRoute.QuizQuestionScreen.route)
                    }
                    isDialogVisible = false

//                    networkStatus.data?.data?.let { quizListData.add(it) }

                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                   // Toast.makeText(context, networkStatus.message, Toast.LENGTH_SHORT).show()

                    // Handle the error case, e.g., show a message to the user
                }
            }
        }

        loginViewModel.getDisclaimerResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {

                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                //    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()

                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true

                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false

                    disclaimer.value = it.data?.data?.text.toString()

                }
            }
        }
        loginViewModel.getNoticeTypeListResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data!!.isSuccess) {
                        var dataDisclaimer = it.data.data
                        for (data in dataDisclaimer)
                            if (data.name == "disclaimer_before_quiz_proctoring") {
                                loginViewModel.getDisclaimerById(
                                    loginViewModel.getLanguageId().toInt(),
                                    data.id.toInt(), loginViewModel.getUserType()!!.toInt()

                                )
                            }
                    }
                }

                is NetworkStatus.Error -> {
                 //   Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        loginViewModel.getNoticeTypeList()
    }
    AuroscholarAppTheme {
        val cameraPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            hasCameraPermission = isGranted
        }

        // Check permission and request if necessary
        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                hasCameraPermission = true
            }
        }

        // checking if question set fetched then hide loader else show loader
        isDialogVisible = !isQuestionFetched.value

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
            {
                Surface(
                    tonalElevation = 10.dp, // Set the elevation here
                    color = com.auro.application.ui.theme.White,
                ) {
                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.back_icon),
                            contentDescription = "logo",
                            modifier = Modifier
                                .clickable {
                                    navHostController.popBackStack()
                                    navHostController.navigate(AppRoute.StudentQuizList.route)
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
                                text = "Quiz Disclaimer",
                                modifier = Modifier
                                    .fillMaxWidth(),
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Black,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }

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
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.Center

            ) {
                // for camera frame
                if (hasCameraPermission) {
                    val lifecycleOwner = LocalLifecycleOwner.current

                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .height(250.dp)
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
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder()
                                    .setTargetResolution(Size(100, 100)) // Target size 100x100
                                    .build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }

                                // Setup Image Capture
                                imageCapture = ImageCapture.Builder()
                                    .setTargetResolution(Size(100, 100))  // Target Resolution
                                    .build()
                                // Select back camera as default
                                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

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
                    try {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    } catch (exc: Exception) {
                        // Handle exception
                        Log.d("PermissionException:", "" + exc.toString())
                    }

                    /*Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Camera permission not granted")
                }*/

                    val cameraPermissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted: Boolean ->
                        hasCameraPermission = isGranted
                    }

                    // Check permission and request if necessary
                    LaunchedEffect(Unit) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            hasCameraPermission = true
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Please keep your face inside the box",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Black,
                        textAlign = TextAlign.Center
                    )
                    Image(
                        painter = painterResource(R.drawable.assessment_camera),
                        contentDescription = "logo",
                        modifier = Modifier
                            .padding(vertical = 20.dp)
                            .background(color = Unspecified)
                            .clickable {
                                val fileName =
                                    SimpleDateFormat(
                                        "yyyy-MM-dd-HH-mm-ss-SSS",
                                        Locale.US
                                    ).format(
                                        System.currentTimeMillis()
                                    ) + ".jpg"
                                val contentValues = ContentValues().apply {
                                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//                                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                                }

                                /*val outputOptions = ImageCapture.OutputFileOptions
                                .Builder(
                                    context.contentResolver,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    contentValues
                                )
                                .build()*/

                                imageCapture?.takePicture(
//                                    outputOptions,
                                    cameraExecutor,
                                    object : ImageCapture.OnImageCapturedCallback() {
                                        override fun onCaptureSuccess(image: ImageProxy) {

                                            val buffer = image.planes[0].buffer
                                            val bytes = ByteArray(buffer.capacity())
                                            buffer.get(bytes)
                                            val bitmap =
                                                BitmapFactory.decodeByteArray(
                                                    bytes,
                                                    0,
                                                    bytes.size
                                                )

//                    }(outputFileResults: ImageCapture.OutputFileResults) {
                                            capturedImageUri = bitmapToUri(context, bitmap)

                                            studentViewModel.saveAssessmentImageFolder(
                                                CreateExamImageRequestModel(loginViewModel.getExamId())
                                            )
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            // Handle error
//                                            context.toast(exception.message.toString())
                                            /* Log.d(
                                             "ImageException1:",
                                             "" + exception.message.toString()
                                         )*/
                                        }
                                    })
                            }
                    )
                }
            }
        }
    }
}

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AssessmentDisclaimerSheet(
    isQuestionFetched: MutableState<Boolean>,
    currentMonth: String,
    currentYear: String,
    disclaimer: MutableState<String>,
    navHostController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel(),
    languageData: HashMap<String, String>,
    studentViewModel: StudentViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    isQuestionFetched.value = true
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { newState ->
                newState != SheetValue.Hidden //  Stop bottom sheet from hiding on outside press
            })

    /* initialValue = ModalBottomSheetValue.Expanded,
        skipHalfExpanded = true // Prevents the sheet from stopping at half state*/
    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.currentValue }
            .collect { value ->
                if (value.equals(ModalBottomSheetValue.Hidden)) {
                    // Re-expand the sheet if it becomes hidden
                    sheetState.show()
                }
            }
    }
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            if (sheetState.isVisible) {
                // Keep the modal open or provide custom behavior if needed
            }
        },
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
                stringResource(id = R.string.disclaimer),
                modifier = Modifier.padding(4.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                disclaimer.value.ifEmpty {
                    stringResource(id = R.string.camera_assessment_disclaimer)
                },
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
                    .wrapContentHeight()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                            navHostController.popBackStack()
                            navHostController.navigate(AppRoute.StudentQuizList.route)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.i_decline),
                            modifier = Modifier.background(
                                color = Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ),
                            color = LightRed01,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    TextButton(
                        onClick = {
                            var quizPractice = 0
                            quizPractice = if (loginViewModel.getStudentSelectedConceptData().nextAttempt!!.toInt() >= 4){
                                1
                            }else{
                                0
                            }
                            loginViewModel.setSelectedNextAttempt(loginViewModel.getStudentSelectedConceptData().nextAttempt!!.toInt())
//                            getRunTimePermission()
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
                                    0,
                                    quizPractice
                                )
                            )
                            isQuestionFetched.value = false
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = PrimaryBlue,
                                shape = RoundedCornerShape(12.dp)
                            )

                    ) {
                        Text(
                            text = if (languageData[LanguageTranslationsResponse.I_ACCEPT].toString() == "") {
                                stringResource(id = R.string.i_accept)
                            } else {
                                languageData[LanguageTranslationsResponse.I_ACCEPT].toString()
                            },
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

@Composable
fun getRunTimePermission() {

    val context = LocalContext.current

    var hasCameraPermission by remember { mutableStateOf(false) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
    }

    if (hasCameraPermission) {

    } else {
        try {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } catch (exc: Exception) {
            // Handle exception
        }
    }

    // Check permission and request if necessary
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            hasCameraPermission = true
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun QuizDisclaimerScreenPreview() {
    AuroscholarAppTheme {
    }
}


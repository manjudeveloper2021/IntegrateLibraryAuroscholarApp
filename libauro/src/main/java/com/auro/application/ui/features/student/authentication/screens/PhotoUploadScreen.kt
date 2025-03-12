package com.auro.application.ui.features.student.authentication.screens

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.utlis.CommonFunction
import com.auro.application.data.utlis.CommonFunction.getFileFromUri
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.ParentDashboardActivity
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.authentication.model.GetKycStatusResponseModel
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import java.io.File

@Composable
fun PhotoUploadScreen(
    navHostController: NavHostController,
    sharedPref: SharedPref?,
    userID: String?,
    viewModel: StudentViewModel = hiltViewModel(),
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
//    var userId by rememberSaveable { mutableStateOf("0") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Get the function to launch the camera
    val launchCamera = CaptureImage { uri ->
        imageUri = uri // Handle the captured image URI
    }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    var isDialogVisible by remember { mutableStateOf(false) }
    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = msgLoader
    )
    var hasKycStatusBeenChecked by remember { mutableStateOf(false) }
    var kycStatusResponse by remember { mutableStateOf<GetKycStatusResponseModel.Data?>(null) }

    val context = LocalContext.current
    var userId =
        if (loginViewModel.getParentInfo()!!.isParent) {  // if login as parent & came to authentication
            loginViewModel.getStudentList().userId   // student id
        } else {
            loginViewModel.getUserId().toString()    // parent id
        }

    BackHandler {
        navHostController.popBackStack()
        if (loginViewModel.getParentInfo()!!.isParent) {
            startActivity(
                context,
                Intent(context, ParentDashboardActivity::class.java),
                null
            )
        } else {
            startActivity(
                context,
                Intent(context, StudentDashboardActivity::class.java),
                null
            )
        }
    }
    Log.e("PhotoUpload", "Before launch: USER_ID  " + userId)
    LaunchedEffect(Unit) {
//        Log.e("PhotoUpload", "Before launch: USER_ID  "+userId )
        viewModel.getKycDocUplaodResposneModelLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {
                    isDialogVisible = false
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false

//                    if (!hasKycStatusBeenChecked) {
//                        hasKycStatusBeenChecked = true
                    isDialogVisible = true
                    viewModel.getKycStatus(userId.toInt())
//                    }

                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.getKycStateLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
//                    kycStatusResponse = null
                    kycStatusResponse = it.data?.data
                    if (!hasKycStatusBeenChecked) {
                        hasKycStatusBeenChecked = true
                        if (it.data?.data?.isSchoolCardRequired == 1 && it.data.data.isSchoolCardUploaded == 0) {
                            Log.d(
                                "kycStatusResponse:",
                                "" + it.data.data.isSchoolCardRequired + " .. " + it.data.data.isSchoolCardUploaded
                            )
                            isDialogVisible = false
//                            if (it.data.data.isSchoolCardUploaded == 0) {
                            navHostController.popBackStack()
                            navHostController.navigate(
                                AppRoute.AuthenticationSchoolIdUpload(
                                    userId
                                )
                            )
                        } else {
                            Log.d(
                                "kycStatusResponse:1",
                                "" + it.data!!.data.isSchoolCardRequired + " .. " + it.data.data.isSchoolCardUploaded
                            )
                            isDialogVisible = false
                            navHostController.popBackStack()
                            navHostController.navigate(AppRoute.AuthenticationReview(userId))
                        }
                        /*} else {
                        navHostController.popBackStack()
                        navHostController.navigate(AppRoute.AuthenticationReview(userId))
                    }*/
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
            }
        }
    }


    StudentRegisterBackground(
        isShowBackButton = true,
        onBackButtonClick = {
            navHostController.popBackStack()
            if (loginViewModel.getParentInfo()!!.isParent) {
                startActivity(
                    context,
                    Intent(context, ParentDashboardActivity::class.java),
                    null
                )
            } else {
                startActivity(
                    context,
                    Intent(context, StudentDashboardActivity::class.java),
                    null
                )
            }
        },
        content = {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp)
                    ) {
                        Text(
                            text = if (languageData[LanguageTranslationsResponse.COMPLETE_AUTHENTICATION].toString() == "") {
                                stringResource(id = R.string.complete_your_authentication)
                            } else {
                                languageData[LanguageTranslationsResponse.COMPLETE_AUTHENTICATION].toString()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp),
                            fontFamily = FontFamily(
                                Font(R.font.inter_bold, FontWeight.Bold)
                            ),
                            fontSize = 20.sp,
                            color = Black,
                            textAlign = TextAlign.Left
                        )


                        Text(
                            text = "Enter your details to confirm your identity and recieve scholarship securely",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            color = GrayLight01,
                            fontFamily = FontFamily(
                                Font(R.font.inter_regular, FontWeight.Normal)
                            ),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Start
                        )

                        Column(
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp)
                                .border(
                                    width = 1.dp,
                                    color = GrayLight02,
                                    shape = RoundedCornerShape(10.dp)
                                )
                        ) {

                            ProgressBarCompose(10)
                        }

                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(weight = 1f, fill = false)
                            .padding(all = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 70.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = if (languageData[LanguageTranslationsResponse.UPLOAD_PHOTO].toString() == "") {
                                    stringResource(R.string.upload_photo)
                                } else {
                                    languageData[LanguageTranslationsResponse.UPLOAD_PHOTO].toString()
                                },
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily(
                                    Font(R.font.inter_medium, FontWeight.Medium)
                                ),
                                fontSize = 16.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = if (languageData[LanguageTranslationsResponse.COMPARE_PHOTO_AADHAAR].toString() == "") {
                                    stringResource(R.string.well_compare_this_photo_with_your_aadhaar_card_picture)
                                } else {
                                    languageData[LanguageTranslationsResponse.COMPARE_PHOTO_AADHAAR].toString()
                                },
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily(
                                    Font(R.font.inter_regular, FontWeight.Normal)
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                color = GrayLight01,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .clip(RoundedCornerShape(10.dp)) // Add a border radius of 10.dp
                                    .drawBehind {
                                        val path = Path().apply {
                                            moveTo(0f, 0f)
                                            lineTo(size.width, 0f)
                                            lineTo(size.width, size.height)
                                            lineTo(0f, size.height)
                                            close()
                                        }
                                        drawPath(
                                            path = path,
                                            color = Color.Gray,
                                            style = Stroke(
                                                width = 2f,
                                                pathEffect = PathEffect.dashPathEffect(
                                                    floatArrayOf(
                                                        10f,
                                                        10f
                                                    )
                                                )
                                            )
                                        )
                                    }

                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentSize(Alignment.Center) // Center the column horizontally and vertically
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .padding(20.dp)
                                            .wrapContentSize(Alignment.Center) // Center the box within the column
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(10.dp) // Add padding to the outer box
                                        ) {

                                            Image(
                                                painter = if (imageUri != null) rememberAsyncImagePainter(
                                                    imageUri
                                                ) else painterResource(R.drawable.ic_profile),
                                                contentDescription = "logo",
                                                modifier = Modifier
                                                    .clickable {
                                                        launchCamera()
                                                    }
                                                    .size(100.dp) // Add size modifier to make the image visible
                                                    .clip(RoundedCornerShape(100.dp)) // Add clip modifier to make the image circular
                                                    .background(color = White)
                                                    .border( // Add border modifier to make image stand out
                                                        width = 1.dp,
                                                        color = GrayLight02,
                                                        shape = CircleShape
                                                    )
                                                    .padding(10.dp),
                                                contentScale = ContentScale.Crop
                                            )
                                            Icon(
                                                painter = painterResource(R.drawable.ic_add_photo), // Replace with your edit pen icon
                                                contentDescription = "Edit",
                                                modifier = Modifier
                                                    .clickable {
                                                        launchCamera()
                                                    }
                                                    .size(50.dp) // Adjust the size of the icon
                                                    .offset(
                                                        x = 75.dp, // Offset the icon to overlap the image
                                                        y = 25.dp
                                                    )
                                                    .padding(end = 10.dp),
                                                tint = Color.Unspecified
                                            )
                                        }
                                    }
                                }

                                Text(
                                    if (imageUri != null) stringResource(R.string.recapture_your_photo) else if (languageData[LanguageTranslationsResponse.CAPTURE_PHOTO].toString() == "") {
                                        stringResource(R.string.capture_your_photo)
                                    } else {
                                        languageData[LanguageTranslationsResponse.CAPTURE_PHOTO].toString()
                                    },
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                    ),
                                    fontSize = 15.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { launchCamera() },
                                    color = if (imageUri != null) PrimaryBlue else Color.Black
                                )
                                Text(
                                    stringResource(R.string.tap_to_open_yout_camera),
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_regular, FontWeight.Normal)
                                    ),
                                    fontSize = 12.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    color = GrayLight01
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                        }
                    }


                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter)
                        .background(color = Color.White)
                        .shadow(elevation = 0.5.dp, shape = RoundedCornerShape(0.dp)),
                    contentAlignment = Alignment.CenterEnd
                )
                {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween // Optional: Adjusts spacing between buttons
                    ) {

                        Spacer(modifier = Modifier.weight(1f))

                        Spacer(modifier = Modifier.width(10.dp))

                        val context = LocalContext.current
                        Button(
                            title = if (languageData[LanguageTranslationsResponse.UPLOAD_PHOTO].toString() == "") {
                                stringResource(id = R.string.upload_photo)
                            } else {
                                languageData[LanguageTranslationsResponse.UPLOAD_PHOTO].toString()
                            },
                            onClick = {
                                if (imageUri != null) {
                                    imageUri?.let { uri ->
                                        try {

                                            val file = getFileFromUri(context, uri)
                                            val fileSizeInBytes = file.length()
                                            // Convert the size to MB
                                            val fileSizeInMB =
                                                fileSizeInBytes.toDouble() / (1024 * 1024)
                                            Log.d("imageUrl:", "" + fileSizeInMB)
//                                            val file = uri.toFile()
                                            userId =
                                                if (loginViewModel.getParentInfo()!!.isParent) {  // if login as parent & came to authentication
                                                    loginViewModel.getStudentList().userId    // studentId
                                                } else {
                                                    loginViewModel.getUserId()
                                                        .toString() // parent id
                                                }

                                            if (CommonFunction.isNetworkAvailable(context)) {
                                                viewModel.getKycDocUpload("4", userId, "0", file)
                                            } else {
                                                context.toast("No internet connection")
                                            }
                                            // school id -3
                                            // photo-4
                                        } catch (e: Exception) {
                                            // Handle the error, e.g., show a toast or log the error
                                            Log.e("UploadError", "Failed to upload KYC document", e)
                                            Toast.makeText(
                                                context,
                                                "Upload failed: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Please upload photo",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            enabled = imageUri != null, // Set to true or false based on your logic
                            modifier = Modifier.weight(1f) // Equal width for Button 2
                        )
                    }
                }
            }

        }
    )
}

@Composable
fun CaptureImage(onImageCaptured: (Uri) -> Unit): () -> Unit {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
//    val file = remember { File(context.filesDir, "image.jpg") }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && imageUri != null) {
                onImageCaptured(imageUri!!)
            } else {
                // Handle failure case
            }
        })

    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File.createTempFile(
        "JPEG_${System.currentTimeMillis()}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
    val photoUri = FileProvider.getUriForFile(
        context, context.packageName + ".provider", imageFile
    )
    /* cameraLauncher.launch(photoUri)
     imageUri = photoUri*/
    return {
        cameraLauncher.launch(photoUri)
        imageUri = photoUri// Launch the camera when called
    }
}

/*val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicture(),
    onResult = { success ->
        if (success) {
            onImageCaptured(uri) // Pass the image URI back
        }
    }
)*/

// Return a function to launch the camera

@Preview
@Composable
fun PhotoUploadScreenPreview() {
//    PhotoUploadScreen(rememberNavController(), null, null, hiltViewModel())
}

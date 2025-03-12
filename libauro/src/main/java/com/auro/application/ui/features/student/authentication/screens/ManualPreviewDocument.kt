package com.auro.application.ui.features.student.authentication.screens

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.data.utlis.CommonFunction
import com.auro.application.data.utlis.CommonFunction.getFileFromUri
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.DarkRed1
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightYellow02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import java.io.File

@Preview
@Composable
fun ManualPreviewDocument(
    navHostController: NavHostController = rememberNavController(),
    sharedPref: SharedPref? = null,
    kycStatus: String? = null,
    kycText: String? = null,
    kycDocType: String? = null,
    userId: String? = null,
) {
    val context: Context = LocalContext.current
    val loginViewModel: LoginViewModel = hiltViewModel()
    val studentViewModel: StudentViewModel = hiltViewModel()
    var docType = remember { mutableStateOf("0") }
    var isCameraLaunch by remember { mutableStateOf(true) }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Get the function to launch the camera
    val launchCamera = captureManualImage { uri ->
        imageUri = uri // Handle the captured image URI
        isCameraLaunch = false
    }
    LaunchedEffect(Unit) {
        if (isCameraLaunch) {
            launchCamera()
        }
    }
    BackHandler {
        isCameraLaunch = false
        navHostController.navigate(
            AppRoute.ManualAuthenticationReviewScreen(
                userId, ""
            )
        )
//        navHostController.popBackStack() // Navigate back
    }
    StudentRegisterBackground(
        isShowBackButton = true,
        onBackButtonClick = {
            isCameraLaunch = false
            navHostController.popBackStack()
            navHostController.navigate(
                AppRoute.ManualAuthenticationReviewScreen(
                    userId, ""
                )
            )
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
                            .padding(start = 10.dp, end = 10.dp)
                    ) {
                        Text(
//                            "School Id Preview ",
                            text = kycText.toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp),
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Black,
                            textAlign = TextAlign.Left
                        )

                        Text(
                            "Confirm the captured picture is clear with all the information visible",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            color = GrayLight01,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Start
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(weight = 1f, fill = false)
                            .padding(all = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 70.dp)
                        ) {

                            Column {

                                Column(
                                    modifier = Modifier
                                        .padding(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .wrapContentHeight()
                                            .padding(bottom = 5.dp, start = 2.dp, end = 0.dp)

                                            .clickable {
//                                            click.invoke()
                                            }) {

                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight(),
                                            verticalArrangement = Arrangement.Center
                                        )
                                        {
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
                                                                )
                                                                else painterResource(R.drawable.ic_profile),
                                                                contentDescription = "logo",
                                                                modifier = Modifier
                                                                    .clickable {
                                                                        launchCamera()
                                                                    }
                                                                    .height(200.dp)
                                                                    .width(250.dp)
                                                                    // Add size modifier to make the image visible
                                                                    .background(color = White)
                                                                    /*.border( // Add border modifier to make image stand out
                                                                        width = 1.dp,
                                                                        color = GrayLight02,
                                                                        shape = CircleShape
                                                                    )*/
                                                                    .padding(10.dp),
                                                                contentScale = ContentScale.Crop
                                                            )
                                                        }
                                                    }
                                                }

                                                Text(
                                                    if (imageUri != null) stringResource(R.string.recapture_your_photo) else
                                                        if (languageData[LanguageTranslationsResponse.CAPTURE_PHOTO].toString() == "") {
                                                            stringResource(R.string.capture_your_photo)
                                                        } else {
                                                            languageData[LanguageTranslationsResponse.CAPTURE_PHOTO].toString()
                                                        },
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { launchCamera() },
                                                    color = if (imageUri != null) PrimaryBlue else Color.Black
                                                )

                                                Spacer(modifier = Modifier.height(10.dp))
                                            }
                                        }
                                    }
                                }
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                )
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
                ) {
                    BtnUi(
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

                                        if (CommonFunction.isNetworkAvailable(context)) {
                                            studentViewModel.getKycDocUpload(
                                                kycDocType.toString(),
                                                userId.toString(), "1", file
                                            )
                                            navHostController.navigate(AppRoute.ManualAuthenticationReviewScreen())
                                        } else {
                                            context.toast("No internet connection")
                                        }
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
                        title = if (languageData[LanguageTranslationsResponse.KEY_CONFIRM].toString() == "") {
                            stringResource(id = R.string.txt_confirm)
                        } else {
                            languageData[LanguageTranslationsResponse.KEY_CONFIRM].toString()
                        },
                        enabled = imageUri != null
                    )

                }
            }

        }
    )
}

@Composable
fun captureManualImage(onImageCaptured: (Uri) -> Unit): () -> Unit {
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
        try {
            cameraLauncher.launch(photoUri)
            imageUri = photoUri// Launch the camera when called
        } catch (e: Exception) {
            Log.d("", "" + e.message)
        }
    }
}
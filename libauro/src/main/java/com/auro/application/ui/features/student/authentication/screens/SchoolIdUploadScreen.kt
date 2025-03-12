package com.auro.application.ui.features.student.authentication.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.data.utlis.CommonFunction
import com.auro.application.data.utlis.CommonFunction.getFileFromUri
import com.auro.application.ui.common_ui.BottomSheetAlert
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.ParentDashboardActivity
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.White
import java.io.File

@Composable
fun SchoolIdUploadScreen(
    navHostController: NavHostController = rememberNavController(),
    sharedPref: SharedPref? = null,
    userId: String? = null,
    viewModel: StudentViewModel = hiltViewModel(),
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    var userId by rememberSaveable { mutableStateOf("0") }
    var isDialogVisible by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var isAPIHit by remember { mutableStateOf(false) }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)
    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = msgLoader
    )
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Get the function to launch the camera
    val launchCamera = CaptureImage { uri ->
        imageUri = uri // Handle the captured image URI
    }

    userId =
        if (loginViewModel.getParentInfo()!!.isParent) {  // if login as parent & came to authentication
            loginViewModel.getStudentList().userId    // studentId
        } else {
            loginViewModel.getUserId().toString() // parent id
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
    LaunchedEffect(Unit) {
        Log.d("LaunchedEffect", "SchoolIdUpload")
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
                    if (it.data!!.isSuccess) {
                        if (isAPIHit) {
                            navHostController.popBackStack()
                            navHostController.navigate(AppRoute.AuthenticationReview(userId))
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
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
                            text = "Upload School ID",
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
                            text = "Please upload a clear copy of your school id.",
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

                            Column(
                                modifier = Modifier
                                    .clickable {
//                                        showBottomSheet = !showBottomSheet
                                    }
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
                                                ) else painterResource(R.drawable.school_id_upload_icon),
                                                contentDescription = "logo",
                                                modifier = Modifier
                                                    .clickable {
                                                        launchCamera()
                                                    }
                                                    .size(110.dp) // Add size modifier to make the image visible
                                                    .clip(RoundedCornerShape(110.dp)) // Add clip modifier to make the image circular
                                                    .background(color = White)
                                                    .border( // Add border modifier to make image stand out
                                                        width = 1.dp,
                                                        color = GrayLight02,
                                                        shape = CircleShape
                                                    )
                                                    .padding(10.dp),
                                                contentScale = ContentScale.Crop
                                            )

                                            /*Icon(
                                                painter = painterResource(R.drawable.ic_add_photo), // Replace with your edit pen icon
                                                contentDescription = "Edit",
                                                modifier = Modifier
                                                    .size(50.dp) // Adjust the size of the icon
                                                    .offset(
                                                        x = 75.dp, // Offset the icon to overlap the image
                                                        y = 25.dp
                                                    )
                                                    .padding(end = 10.dp),
                                                tint = Color.Unspecified
                                            )*/
                                        }
                                    }
                                }

                                Text(
                                    stringResource(R.string.choose_file_to_upload_for_front_side),
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(
                                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                                    ),
                                    fontSize = 14.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    stringResource(R.string.select_image_and_maximum_image_size),
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

//                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            title = stringResource(id = R.string.upload_id_card),
                            onClick = {
                                if (imageUri != null) {
                                    isDialogVisible = true
                                    imageUri?.let { uri ->
                                        try {
//                                            Log.d("imageUrl:", "" + uri)
                                            val file = getFileFromUri(context, uri)
//                                            val file = uri.toFile()
                                            /* val fileSizeInBytes = file.length()
                                             // Convert the size to MB
                                             val fileSizeInMB = fileSizeInBytes.toDouble() / (1024 * 1024)
                                             Log.d("imageUrl:", "" + fileSizeInMB)*/
                                            userId =
                                                if (loginViewModel.getParentInfo()!!.isParent) {  // if login as parent & came to authentication
                                                    loginViewModel.getStudentList().userId    // studentId
                                                } else {
                                                    loginViewModel.getUserId()
                                                        .toString() // parent id
                                                }
//                                            viewModel.getKycStatus(userId.toInt())
                                            if (CommonFunction.isNetworkAvailable(context)) {
                                                isAPIHit = true
                                                viewModel.getKycDocUpload("3", userId, "0", file)
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

    if (showBottomSheet) {
        BottomSheetAlert(
            showBottomSheet,
            onHide = {
                showBottomSheet = false
            }, if (languageData[LanguageTranslationsResponse.OKAY_GOT_IT].toString() == "") {
                stringResource(R.string.okay_got_it)
            } else {
                languageData[LanguageTranslationsResponse.OKAY_GOT_IT].toString()
            }, content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(text = "Choose Option", color = Color.Black)
                    Spacer(modifier = Modifier.height(20.dp))
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "camera icon",
                            tint = Color.Black
                        )
                        Text(text = "Open Camera", color = Color.Black)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_image),
                            contentDescription = "camera icon",
                            tint = Color.Black
                        )
                        Text(text = "Upload from Gallery", color = Color.Black)
                    }

                }
            }
        )

    }

}
//
//@Composable
//fun CaptureImage(onImageCaptured: (Uri) -> Unit): () -> Unit {
//    val context = LocalContext.current
//    val file = remember { File(context.filesDir, "image.jpg") }
//    val uri = FileProvider.getUriForFile(
//        context,
//        "${context.packageName}.provider",
//        file
//    )
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicture(),
//        onResult = { success ->
//            if (success) {
//                onImageCaptured(uri) // Pass the image URI back
//            }
//        }
//    )
//
//    // Return a function to launch the camera
//    return {
//        launcher.launch(uri) // Launch the camera when called
//    }
//}

@Preview
@Composable
fun SchoolIdUploadScreenPreview() {
//    SchoolIdUploadScreen(rememberNavController(), null, null, hiltViewModel())
}
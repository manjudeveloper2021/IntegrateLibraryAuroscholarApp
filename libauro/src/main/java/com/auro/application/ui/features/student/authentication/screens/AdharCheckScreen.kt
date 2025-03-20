package com.auro.application.ui.features.student.authentication.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.ConstantVariables.addNewChild
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.BtnNextUi
import com.auro.application.ui.common_ui.BtnTextUi
import com.auro.application.ui.common_ui.InputTextFieldWithError
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.ParentDashboardActivity
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.authentication.model.GetAadharOtpSendRequestModel
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdharCheckScreen(
    navHostController: NavHostController = rememberNavController(),
    sharedPref: SharedPref? = null,
    intent: String? = null,
    viewModel: StudentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = hiltViewModel()
    var userId by rememberSaveable { mutableStateOf("0") }
    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    var isAccepted by rememberSaveable { mutableStateOf(false) }
    var isOTPErrorSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    val adharNo = remember { mutableStateOf("") }

    userId =
        if (loginViewModel.getParentInfo()!!.isParent) {  // if login as parent & came to authentication
            loginViewModel.getStudentList().userId    // studentId
        } else {
            loginViewModel.getUserId().toString() // parent id
        }

    var isDialogVisible by remember { mutableStateOf(false) }
    if (isOTPErrorSheetVisible) {
        OTPErrorBottomSheet(
            navHostController,
            viewModel,
            languageData,
            userId,
            adharNo.value,
            context
        ) {
            isOTPErrorSheetVisible = false
        }
    }

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = msgLoader
    )

    LaunchedEffect(Unit) {
        viewModel.getAadhaarOTPredisposeModellLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {
                    isDialogVisible = false
                }

                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data!!.isSuccess && it.data.data != null) {
//                        Log.d("NetworkStatus: ","success: "+it.data)
                        val refKeyId = it.data.data.otpReferenceID
                        navHostController.navigate(
                            AppRoute.AadharOtpVerify(
                                adharNo.value,
                                refKeyId
                            )
                        )
                    } else {
                        isDialogVisible = false
                        if (it.data.error.contains("invalid") || it.data.error.contains("Aadhaar is already linked")) {
                            Toast.makeText(context, it.data.error, Toast.LENGTH_SHORT).show()
                        } else {
                            isOTPErrorSheetVisible = true
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    Log.d("NetworkStatus:1 ", "message: " + it.message)
                    isDialogVisible = false
                    if (it.message.contains("invalid")
                        || it.message.contains("Aadhaar is already linked")
                        || it.message.contains("No internet")) {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    } else {
                        isOTPErrorSheetVisible = true
                    }
                }
            }
        }
    }

    // Clear response when the screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    BackHandler {
        try {
            navHostController.popBackStack()
            if (loginViewModel.getParentInfo()!!.isParent) {
                startActivity(
                    context,
                    Intent(context, ParentDashboardActivity::class.java).apply {
                        (context as? Activity)?.finish()
                    },
                    null
                )
            } else {
                startActivity(
                    context,
                    Intent(context, StudentDashboardActivity::class.java).apply {
                        (context as? Activity)?.finish()
                    },
                    null
                )
            }
        } catch (exp: Exception) {
            exp.message
            println("Authentication error :- ${exp.message}")
        }
    }

    StudentRegisterBackground(
        isShowBackButton = true,
        onBackButtonClick = {
//            (context as ComponentActivity).finish()
            try {
                navHostController.popBackStack()
                if (loginViewModel.getParentInfo()!!.isParent) {
                    startActivity(
                        context,
                        Intent(context, ParentDashboardActivity::class.java).apply {
                            (context as? Activity)?.finish()
                        },
                        null
                    )
                } else {
                    startActivity(
                        context,
                        Intent(context, StudentDashboardActivity::class.java).apply {
                            (context as? Activity)?.finish()
                        },
                        null
                    )
                }
            } catch (exp: Exception) {
                exp.message
                println("Authentication error :- ${exp.message}")
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
                        if (isAccepted) {
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
                        } else {
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
                                text = if (languageData[LanguageTranslationsResponse.ENTER_INFORMATION_CONFIRM_IDENTITY].toString() == "") {
                                    stringResource(id = R.string.enter_your_information_so_we_can_confirm_your_identity_and_send_microscholarships_securely)
                                } else {
                                    languageData[LanguageTranslationsResponse.ENTER_INFORMATION_CONFIRM_IDENTITY].toString()
                                },
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

                        Column(
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp)
                                .border(
                                    width = 1.dp,
                                    color = GrayLight02,
                                    shape = RoundedCornerShape(10.dp)
                                )
                        ) {

                            if (isAccepted) {
                                ProgressBarCompose()
                            } else {
                                println("False values...")
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(weight = 1f, fill = false)
                            .padding(all = 8.dp)
                    ) {
                        if (isAccepted) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .verticalScroll(rememberScrollState())
                                    .padding(
                                        start = 10.dp,
                                        end = 10.dp,
                                        top = 10.dp,
                                        bottom = 70.dp
                                    )
                                    .clip(RoundedCornerShape(10.dp))
                            ) {

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    stringResource(R.string.aadhaar_number),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (languageData[LanguageTranslationsResponse.ENTER_AADHAR_CARD_NUMBER].toString() == "") {
                                        stringResource(R.string.please_enter_your_aadhar_card_number)
                                    } else {
                                        languageData[LanguageTranslationsResponse.ENTER_AADHAR_CARD_NUMBER].toString()
                                    },
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                    color = GrayLight01,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    stringResource(R.string.aadhar_card_number),
                                    modifier = Modifier.padding(top = 20.dp),
                                    fontSize = 14.sp
                                )

                                InputTextFieldWithError(
                                    value = adharNo,
                                    placeholder = stringResource(id = R.string.enter_aadhar_card_number),
                                    maxChars = 12, // Set your desired max character limit
                                    errorMessage = "Please enter correct aadhar no.",
                                    keyboardType = KeyboardType.Phone
                                )
                            }
                        } else {
                            println("data false found A...")
                        }
                    }
                }

                if (isAccepted) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .align(Alignment.BottomCenter)
                            .background(color = Color.White)
                            .shadow(elevation = 0.5.dp, shape = RoundedCornerShape(0.dp)),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween // Optional: Adjusts spacing between buttons
                        ) {

                            Spacer(modifier = Modifier.weight(1f))

                            Spacer(modifier = Modifier.width(10.dp)) // Optional: Adds space between buttons

                            Button(
                                title = if (languageData[LanguageTranslationsResponse.NEXT].toString() == "") {
                                    stringResource(id = R.string.next)
                                } else {
                                    languageData[LanguageTranslationsResponse.NEXT].toString()
                                },
                                onClick = {
                                    val lat = viewModel.latitude.value
                                    val lng = viewModel.longitude.value
                                    Log.e("TAG", "AdharCheckScreen: clicked$lat $lng")
                                    lng?.let {
                                        lat?.let {
                                            if (adharNo.value == "") {
                                                Toast.makeText(
                                                    context,
                                                    "Please enter Aadhar no",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else if (adharNo.value.length < 12 && adharNo.value != "") {
                                                Toast.makeText(
                                                    context,
                                                    "Please enter correct Aadhaar no.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                isDialogVisible = true
                                                viewModel.getAadhaarOtpSend(
                                                    GetAadharOtpSendRequestModel(
                                                        adharNo.value.trim().toLong(),
                                                        userId.toInt()
                                                    )
                                                )
                                            }

                                            /*if (!adharNo.value.isNullOrBlank() && adharNo.value != "") {
                                                isDialogVisible = true
                                                viewModel.getAadhaarOtpSend(
                                                    GetAadharOtpSendRequestModel(
                                                        adharNo.value.trim().toLong(),
                                                        userId.toInt()
                                                    )
                                                )
                                            } else if (adharNo.value.length < 12 && adharNo.value != "") {
                                                Toast.makeText(
                                                    context,
                                                    "Please enter correct Aadhaar no.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Please enter Aadhar no",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            }*/
                                        }
                                    }
                                },
                                enabled = adharNo.value.length == 12, // Set to true or false based on your logic
                                modifier = Modifier.weight(1f) // Equal width for Button 2
                            )
                        }
                    }
                } else {
                    println("data false found B...")
                }
            }

            BottomSheetDialogNotice(
                navHostController = navHostController,
                isBottomSheetVisible = isBottomSheetVisible,
                sheetState = sheetState,
                onDismiss = {
                    scope.launch { sheetState.hide() }
                        .invokeOnCompletion {
                            isBottomSheetVisible = false
                            isAccepted = true
                        }
                }, onDecline = {
//                    navHostController.navigate(AppRoute.AadhaarInstruction("false"))
                    try {
                        navHostController.popBackStack()
                        if (loginViewModel.getParentInfo()!!.isParent) {
                            startActivity(
                                context,
                                Intent(context, ParentDashboardActivity::class.java).apply {
                                    (context as? Activity)?.finish()
                                },
                                null
                            )
                        } else {
                            startActivity(
                                context,
                                Intent(context, StudentDashboardActivity::class.java).apply {
                                    (context as? Activity)?.finish()
                                },
                                null
                            )
                        }
                    } catch (exp: Exception) {
                        exp.message
                        println("Authentication error :- ${exp.message}")
                    }
                }
            )

            if (sharedPref?.getKycStatus() != null && intent != "true") {
                LaunchedEffect(Unit) {
                    if (intent == null && intent != addNewChild || intent != "true") {
                        scope.launch {
                            isBottomSheetVisible = true
                            sheetState.expand()
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDialogNotice(
    navHostController: NavHostController,
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDecline: () -> Unit
) {

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
           // windowInsets = WindowInsets.ime
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(vertical = 15.dp, horizontal = 7.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_alert),
                        contentDescription = "sahsga",
                        alignment = Alignment.Center,
                        modifier = Modifier.background(Color.Unspecified)
                    )

                    Text(
                        text = "Disclaimer",
                        modifier = Modifier.padding(10.dp),
                        fontSize = 18.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_bold, FontWeight.Bold)
                        ),
                        color = Black
                    )

                    Text(
                        text = languageData[LanguageTranslationsResponse.STATE_THAT_ALL_INFORMATION].toString(),
                        modifier = Modifier.padding(10.dp),
                        color = GrayLight01,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
                        ),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        BtnTextUi(languageData[LanguageTranslationsResponse.I_DECLINE].toString(),
                            color = Color.Red,
                            onClick = {
                                onDecline.invoke()
                            })

                        BtnNextUi(
                            languageData[LanguageTranslationsResponse.I_ACCEPT].toString(),
                            onClick = {
                                onDismiss.invoke()
                            },
                            enabled = true
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPErrorBottomSheet(
    navHostController: NavHostController,
    viewModel: StudentViewModel,
    languageData: HashMap<String, String>,
    userId: String,
    aadhaarNo: String,
    context: Context = LocalContext.current,
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
                painter = painterResource(id = R.drawable.icon_otp_retry), // Replace with your drawable resource
                contentDescription = null, // Provide a description for accessibility purposes
                modifier = Modifier
                    .wrapContentSize()
                    .size(80.dp),
                tint = Unspecified
            )

            Text(
                stringResource(id = R.string.txt_trouble_otp),
                modifier = Modifier.padding(4.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                stringResource(id = R.string.txt_otp_trouble_desc),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 5.dp),
                color = Gray, fontSize = 14.sp, fontFamily = FontFamily(
                    Font(R.font.inter_regular, FontWeight.Normal)
                ),
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
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                            if (aadhaarNo.isNotBlank() && aadhaarNo != "") {
                                viewModel.getAadhaarOtpSend(
                                    GetAadharOtpSendRequestModel(
                                        aadhaarNo.trim().toLong(),
                                        userId.toInt()
                                    )
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter Aadhar no",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = if (languageData[LanguageTranslationsResponse.RESEND_OTP].toString() == "") {
                                stringResource(id = R.string.resend_otp)
                            } else {
                                languageData[LanguageTranslationsResponse.RESEND_OTP].toString()
                            },
                            modifier = Modifier.background(
                                color = Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ),
                            color = PrimaryBlue,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily(
                                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                            )
                        )
                    }
                    TextButton(
                        onClick = {
                            onDismiss()
                            navHostController.popBackStack()
                            navHostController.navigate(
                                AppRoute.ManualAuthenticationReviewScreen(
                                    userId, ""
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_upload_manually),
                            modifier = Modifier
                                .background(
                                    color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily(
                                Font(R.font.inter_regular, FontWeight.Normal)
                            )
                        )
                    }
                }
            }
        }
    }
}

/*@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AdharCheckScreenPreview() {
    var viewModel: StudentViewModel = hiltViewModel()
    OTPErrorBottomSheet(
        navHostController = rememberNavController(),
        viewModel,
        "",
        "",
        context = LocalContext.current
    ) {

    }
}*/

@Composable
fun ProgressBarCompose(progress: Int = 2) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))
        var progressState by remember { mutableStateOf(progress.toDouble() / 10) }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "1",
                modifier = Modifier
                    .background(
                        shape = CircleShape, color = when {
                            progress <= 1 -> GrayLight01
                            progress in 2..10 -> PrimaryBlue
                            else -> GrayLight01
                        }
                    )
                    .padding(5.dp)
                    .size(25.dp)
                    .wrapContentSize(Alignment.Center),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Aadhaar\nVerification", modifier = Modifier.padding(top = 10.dp),
                color = when {
                    progress <= 1 -> GrayLight01
                    progress in 2..10 -> PrimaryBlue
                    else -> GrayLight01
                },
                fontSize = 12.sp,
                fontFamily = FontFamily(
                    Font(R.font.inter_regular, FontWeight.Normal)
                ),
                textAlign = TextAlign.Center
            )
        }
        LinearProgressIndicator(
            progress = { (progressState).toFloat() },
            modifier = Modifier
                .padding(5.dp, bottom = 40.dp)
                .width(100.dp),
            color = when {
                progress <= 1 -> GrayLight01
                progress in 2..10 -> PrimaryBlue
                else -> GrayLight01
            },
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "2",
                modifier = Modifier
                    .background(
                        shape = CircleShape,
                        color = when {
                            progress <= 1 -> GrayLight01
                            progress in 10..10 -> PrimaryBlue
                            else -> GrayLight01
                        }
                    )
                    .padding(5.dp)
                    .size(25.dp)
                    .wrapContentSize(Alignment.Center),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Upload \nPhoto",
                modifier = Modifier.padding(start = 15.dp, top = 10.dp, end = 15.dp),
                color = when {
                    progress <= 1 -> GrayLight01
                    progress in 10..10 -> PrimaryBlue
                    else -> GrayLight01
                },
                fontSize = 12.sp,
                fontFamily = FontFamily(
                    Font(R.font.inter_regular, FontWeight.Normal)
                ),
                textAlign = TextAlign.Center
            )
        }



        Spacer(modifier = Modifier.weight(1f))
    }
}


@Composable
fun Button(
    title: String = "Continue",
    onClick: () -> Unit,
    enabled: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(10.dp)
            .background(
                color = if (enabled) PrimaryBlue else GrayLight02, shape = RoundedCornerShape(
                    topStart = 15.dp,
                    topEnd = 15.dp,
                    bottomStart = 15.dp,
                    bottomEnd = 15.dp
                )
            )
            .clickable(
                onClick = onClick
            )
    ) {
        Text(
            title,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center) // Center both horizontally and vertically
            , fontWeight = FontWeight.SemiBold, color = if (enabled) White else Black
        )
    }
}
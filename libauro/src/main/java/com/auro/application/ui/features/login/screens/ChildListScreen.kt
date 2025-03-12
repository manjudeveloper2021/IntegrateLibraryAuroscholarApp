package com.auro.application.ui.features.login.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import coil.request.ImageRequest
import com.auro.application.App
import com.auro.application.R
import com.auro.application.core.ConstantVariables.SIGNUP_PIN
import com.auro.application.core.ConstantVariables.addNewChild
import com.auro.application.core.ConstantVariables.isLogout
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.api.aes.AESEncryption.encryptAES
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.data.utlis.CommonFunction.getGenderIconState
import com.auro.application.ui.common_ui.ChildListBackgroundUi
import com.auro.application.ui.common_ui.OtpInputField
import com.auro.application.ui.common_ui.components.DoubleBackPressHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.LoginMainActivity
import com.auro.application.ui.features.login.models.ChildListResponse
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Bg_Gray
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightRed01
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import java.util.HashMap

@Composable
fun ChildListScreen(
    navHostController: NavHostController,
    context: Context = LocalContext.current,
    viewModel: LoginViewModel,
    sharedPref: SharedPref?,
    isUserPinSet: String?,
) {
    val context: Context = LocalContext.current
    var childList by remember { mutableStateOf(mutableListOf<ChildListResponse.Data.Student>()) }
    var parentDetail by remember { mutableStateOf<ChildListResponse.Data.Parent?>(null) }
    viewModel.saveScreenName(addNewChild)
    val userId = viewModel.getUserId().toString()
    val parent = viewModel.getParentInfo()
    var isDialogVisible by remember { mutableStateOf(false) }
    lateinit var appclass: App
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    Log.e("TAG", "ChildListScreen: Data is her percent $parent")

    var logOutSheet by remember { mutableStateOf(false) }
    // show logout sheet while click on logout
    if (logOutSheet) {
        LogoutChildBottomSheetMenu(viewModel, languageData) {
            logOutSheet = false
        }
    }


    DoubleBackPressHandler {
//        if (!navHostController.popBackStack()) {
        // If unable to pop back (i.e., no more screens), finish the activity
        (context as? Activity)?.finish()
//        }
    }

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()
    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
//        message = "Loading your data..."
        message = msgLoader
    )

    var isParentExit = remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.childListLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        viewModel.saveChildCount(it.data.data.student.size)
                        childList = it.data.data.student.toMutableList()
                        parentDetail = it.data.data.parent
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
//                    context.toast(it.message)
                }
            }
        }

        viewModel.getNoticeTypeListResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (!isParentExit.value) {

                    }
                    viewModel.getDisclaimerById()
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
            }
        }
        viewModel.getNoticeTypeList()
        viewModel.getChildListApi()

    }

    ChildListBackgroundUi(
        isShowBackButton = false,
        onClick = {
            logOutSheet = true
            /* viewModel.saveScreenName(isLogout)
             viewModel.clearPreferenceData(context)
             context.startActivity(Intent(context, LoginMainActivity::class.java))
                 .also {
                     if (context is Activity) {
                         context.finish()
                     }
                 }*/
        },
        onBackButtonClick = {
            if (!navHostController.popBackStack()) {
                // If unable to pop back (i.e., no more screens), finish the activity
                (context as? Activity)?.finish()
            }
        },
        content = {

            Text(
//                text = stringResource(id = R.string.start_your_journey),
                text = languageData[LanguageTranslationsResponse.START_JOURNEY].toString(),
                modifier = Modifier.padding(top = 20.dp, start = 15.dp, end = 15.dp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Text(
//                stringResource(id = R.string.choose_your_user_type_to_proceed),
                text = languageData[LanguageTranslationsResponse.CHOOSE_USER_TYPE].toString(),
                modifier = Modifier.padding(top = 0.dp, start = 15.dp, end = 15.dp, bottom = 10.dp),
                color = GrayLight01,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {

                if (parentDetail != null)
                    ParentChildList(
                        navHostController,
                        context,
                        userId,
                        parentDetail!!,
                        childList,
                        languageData,
                        sharedPref, viewModel, isUserPinSet
                    )
            }
        }
    )
}


@Composable
private fun ParentChildList(
    navHostController: NavHostController,
    context: Context,
    user: String,
    parentDetail: ChildListResponse.Data.Parent,
    childList: MutableList<ChildListResponse.Data.Student>,
    languageData: HashMap<String, String>,
    sharedPref: SharedPref?,
    viewModel: LoginViewModel,
    isUserPinSet: String?,
) {
    var childSize = childList.size.toString()

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .clickable {
//                    val parent = viewModel.getParentInfo()
                    viewModel.saveUserType("1")
                    viewModel.saveUserName(parentDetail.name)
                    viewModel.saveUserEmail(parentDetail.email_id)
                    sharedPref!!.saveParentOnboardingWalkthrough(parentDetail.isWalkthroughSet == "1")

                    viewModel.saveUserImage("")

                    // moving to parent
//                    if (!isUserPinSet.isNullOrBlank() && isUserPinSet == "0") {
                    if (parentDetail.isWalkthroughSet != "1" && parentDetail.isUserPin != true) {
                        navHostController.navigate(AppRoute.ParentRegistrationStep1())
                    } else if (parentDetail.name != "") {
                        if (parentDetail.isUserPin != false) {
                            navHostController.navigate(
                                AppRoute.EnterPin(
                                    phoneNo = parentDetail.user_mobile,
                                    SIGNUP_PIN,
                                    parentDetail.user_id
                                )
                            )
                        } else {
                            viewModel.saveUserId(parentDetail.user_id)
                            navHostController.navigate(AppRoute.CreatePin())
                        }
                    } else {
                        if (parentDetail.isUserPin == false) {
                            navHostController.navigate(AppRoute.ParentRegistrationStep1())
                        } else {
                            navHostController.navigate(
                                AppRoute.EnterPin(
                                    phoneNo = parentDetail.user_mobile,
                                    SIGNUP_PIN,
                                    parentDetail.user_id
                                )
                            )
                        }
                    }
                }
                .padding(10.dp)
                .fillMaxWidth(),
            border = BorderStroke(1.dp, GrayLight02)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Set background color based on selection
                    .padding(horizontal = 16.dp, vertical = 8.dp), // Add horizontal padding
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(52.dp)
                        .background(Unspecified),
                    painter = painterResource(R.drawable.ic_parent),
                    contentDescription = "Logo",
                )
                Text(
//                    text = "View Parent Profile",
                    text = languageData[LanguageTranslationsResponse.VIEW_PARENT_PROF].toString(),
                    modifier = Modifier
                        .weight(0.1f)
                        .padding(10.dp),
                    textAlign = TextAlign.Start,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Black
                )

                Image(
                    painter = painterResource(R.drawable.ic_right_side),
                    contentDescription = "sdsds",
                    modifier = Modifier
                        .size(25.dp)
                        .background(Color.Unspecified)
                )
            }
        }

        Row(modifier = Modifier.padding(10.dp)) {
            Text(
//                "Student List",
                text = languageData[LanguageTranslationsResponse.STUDENT_LIST].toString(),
                modifier = Modifier
                    .padding(start = 5.dp, top = 5.dp),
                textAlign = TextAlign.Start,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Black
            )
            Spacer(modifier = Modifier.weight(0.1f))
            Text(
                "$childSize/5",
                modifier = Modifier
                    .padding(horizontal = 5.dp),
                textAlign = TextAlign.End,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = PrimaryBlue
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(childList) { child ->
                ChildItem(child, navHostController, user, viewModel, languageData, parentDetail)
            }
            item {
                if (childList.size == 5) {
                    println("No more add students")
                } else {
                    AddStudent(viewModel, navHostController, languageData)
                }
            }
        }
    }
}

@Composable
private fun AddStudent(
    viewModel: LoginViewModel,
    navHostController: NavHostController,
    languageData: HashMap<String, String>,
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(16.dp)
            .height(80.dp)
            .clickable {
                viewModel.saveUserType("2")
//                navHostController.popBackStack(route = AppRoute.ChildList, inclusive = true)
                navHostController.navigate(AppRoute.RegistrationStep1())

            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .height(80.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val dashWidth = 8f
                val dashGap = 10f

                val pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(dashWidth, dashGap),
                    phase = 1f
                )

                drawRoundRect(
                    color = PrimaryBlue,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = pathEffect
                    ),
                    cornerRadius = CornerRadius(13.dp.toPx())
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .background(White)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Bg_Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(10.dp)
                                .size(30.dp)
                                .background(Color.Unspecified),
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = "Logo"
                        )
                    }

                    Text(
//                        "Add Your Student",
                        text = languageData[LanguageTranslationsResponse.ADD_YOUR_STUDENT].toString(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp),
                        textAlign = TextAlign.Start,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = PrimaryBlue
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_right_side),
                        colorFilter = ColorFilter.tint(
                            PrimaryBlue
                        ),
                        contentDescription = "sdsds",
                        modifier = Modifier
                            .wrapContentSize()
                            .background(Color.Unspecified)
                    )
                }
            }
        }
    }
}

@Composable
fun ChildItem(
    child: ChildListResponse.Data.Student,
    navHostController: NavHostController,
    user: String,
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    parentDetail: ChildListResponse.Data.Parent,
) {
    var confirmDeleteState by remember { mutableStateOf(false) }
    // show delete confirmation screen while click on logout
    val context = LocalContext.current

    if (confirmDeleteState) {
        ConfirmationDeleteAccountBottomSheet(
            navHostController,
            viewModel,
            languageData,
            viewModel.getUserId()
        ) {
            confirmDeleteState = false
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .clickable {
            }
            .padding(10.dp)
            .fillMaxWidth(),
        border = BorderStroke(1.dp, GrayLight02),
        onClick = {
            viewModel.saveUserType("2")
            viewModel.saveUserId(child.userId)
            viewModel.saveUserName(child.name)
            viewModel.saveUserEmail("")
            viewModel.saveUserImage(child.imageUrl!!)
            viewModel.saveStudentData(child)
            if (child.isActiveUser == 1) {
                if (!child.isUserPin) { // onboarding done but Pin not created
                    navHostController.navigate(AppRoute.CreatePin(user))
                } else {
                    // Pin Created so move to Enter Pin Screen
                    navHostController.navigate(
                        AppRoute.EnterPin(
                            viewModel.getUserPhoneNo(),
                            SIGNUP_PIN, child.userId
                        )
                    )
                }
            } else {
                confirmDeleteState = true
//                context.toast("Bhag yahan se, bade aaye login krne!!\n Ja pahle apna account activate kr...")
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White) // Set background color based on selection
                .padding(horizontal = 16.dp, vertical = 8.dp), // Add horizontal padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color.Unspecified),
                painter = if (child.imageUrl != null) {
                    rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(child.imageUrl)
                            .placeholder(
                                if (child.gender != null) {
                                    getGenderIconState(child.gender)
                                } else {
                                    R.drawable.icon_male_student
                                }
                            )
                            .error(
                                if (child.gender != null) {
                                    getGenderIconState(child.gender)
                                } else {
                                    R.drawable.icon_male_student
                                }
                            )
                            .crossfade(true) // Optional: Add a fade transition
                            .build()
                    )
                } else {
                    painterResource(
                        if (child.gender != null) {
                            getGenderIconState(child.gender)
                        } else {
                            R.drawable.icon_male_student
                        }
                    )
                },
                contentDescription = "Logo"
            )

            Column(
                modifier = Modifier
                    .weight(0.1f)
                    .padding(10.dp)
                    .align(Alignment.CenterVertically)

            ) {
                Text(
                    text = child.name,
                    textAlign = TextAlign.Start,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Black
                )
                if (child.isActiveUser == 0) {
                    Text(
                        text = child.deactivateMsg,
                        textAlign = TextAlign.Start,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = LightRed01
                    )
                } else if (child.isActiveUser == 3) {
                    Text(
                        text = "User Blocked",
                        textAlign = TextAlign.Start,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = LightRed01
                    )
                }
            }
            Image(
                painter = painterResource(R.drawable.ic_right_side),
                contentDescription = "img",
                modifier = Modifier
                    .size(25.dp)
                    .background(Color.Unspecified)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDeleteAccountBottomSheet(
    navHostController: NavHostController,
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    userId: String?,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var verifyPinSheet by remember { mutableStateOf(false) }

    // show verify pin sheet while click on logout
    if (verifyPinSheet) {
//        onDismiss()
        VerifyPinBottomSheet(
            navHostController,
            viewModel,
            languageData,
            userId
        ) {
            verifyPinSheet = false
        }
    }

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
                painter = painterResource(id = R.drawable.ic_profile_bg), // Replace with your drawable resource
                tint = Color.Unspecified,
                contentDescription = null,
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Unspecified),
            )

            Text(
//                stringResource(id = R.string.txt_sure_delete_acc),
                text = languageData[LanguageTranslationsResponse.KEY_RECOVER_THIS_ACC].toString(),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            Text(
//                stringResource(id = R.string.txt_sure_delete_desc),
                text = languageData[LanguageTranslationsResponse.DELETE_REQUIREMENTS].toString() + "\n" + languageData[LanguageTranslationsResponse.DELETE_DATA_WARNING].toString(),
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Gray, fontSize = 14.sp, fontWeight = FontWeight.Normal
                ),
                textAlign = TextAlign.Center
            )

            Divider(
                color = GrayLight02,
                thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Box(
                modifier = Modifier
                    .padding(bottom = 20.dp)
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
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text(
//                            text = stringResource(id = R.string.txt_cancel),
                            text = languageData[LanguageTranslationsResponse.KEY_CANCEL].toString(),
                            modifier = Modifier
                                .background(
                                    color = Color.White, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = PrimaryBlue,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    TextButton(
                        onClick = {
                            verifyPinSheet = true
//                            onDismiss()
                        }, modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
//                            text = stringResource(id = R.string.txt_delete),
                            text = languageData[LanguageTranslationsResponse.KEY_RECOVER].toString(),
                            modifier = Modifier
                                .background(
                                    color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = Color.White,
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
fun VerifyPinBottomSheet(
    navHostController: NavHostController,
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    userId: String?,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var isPinFilled by remember { mutableStateOf(false) }
    var inValidPin by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var errorMsg by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }   // get token from child pin APi
    val phoneNo = viewModel.getUserPhoneNo()
//    var textPhoneNo by remember { mutableStateOf(phoneNo) }
    var pinValue by remember { mutableStateOf("") }
    val context = LocalContext.current

    var verifyOTPSheet by remember { mutableStateOf(false) }
    // if pin verified successfully for delete account
    if (verifyOTPSheet) {
        VerifyRecoverOtpBottomSheet(
            navHostController,
            phoneNo,
            viewModel,
            context
        ) {
            verifyOTPSheet = false
        }
    }

    LaunchedEffect(Unit) {
        // Verify with pin response
        viewModel.loginResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
//                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
//                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        token = it.data.data!!.token
                        viewModel.saveUserName(it.data.data.userDetails!!.name)
                        viewModel.saveUserEmail(it.data.data.userDetails.email)
                        viewModel.saveUserImage(it.data.data.userDetails.profilePic)
                        viewModel.setLoginInfo(it.data.data.userDetails)
                        viewModel.sendOtp(phoneNo.encryptAES().toString())

                        if (it.data.data.userDetails.grade != null) {
                            viewModel.saveGrade(it.data.data.userDetails.grade.toString())
                        } else {
                            viewModel.saveGrade("1")
                        }
                        viewModel.saveUserId(it.data.data.userDetails.userId)
                        viewModel.saveToken(it.data.data.token)
                        viewModel.saveUserId(it.data.data.userDetails.userId)
                        viewModel.saveUserPin(it.data.data.userDetails.isPinSet!!)
                        viewModel.saveUserType(it.data.data.userDetails.userTypeId.toString())
                    } else {
                        inValidPin = true
//                        errorMsg = "Invalid Pin"
                        errorMsg =
                            languageData[LanguageTranslationsResponse.KEY_INVALID_PIN].toString()
                    }
                }

                is NetworkStatus.Error -> {
                    inValidPin = true
                    errorMsg = it.message
                }
            }
        }

        // sending otp after getting pin response
        viewModel.sendOtpResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    verifyOTPSheet = true
                }

                is NetworkStatus.Error -> {
//                    context.toast(it.message)
                }
            }
        }
    }

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
                painter = painterResource(id = R.drawable.ic_lock_bg), // Replace with your drawable resource
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Unspecified),
            )

            Text(
//                stringResource(id = R.string.txt_verify_pin),
                text = languageData[LanguageTranslationsResponse.KEY_VERIFY_WITH_PIN].toString(),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            OtpInputField(otpLength = 4,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                otpText = pinValue,
                shouldCursorBlink = false,
                onOtpModified = { value, otpFilled ->
                    pinValue = value
                    isPinFilled = otpFilled
                    if (otpFilled) {
                        keyboardController?.hide()
                    }
                })
            if (inValidPin) {
                Text(
                    errorMsg, color = LightRed01,
                    modifier = Modifier.padding(start = 15.dp, top = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp
                )
            }

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
                        onClick = { onDismiss() }, modifier = Modifier.weight(1f)
                    ) {
                        Text(
//                            text = stringResource(id = R.string.txt_cancel),
                            text = languageData[LanguageTranslationsResponse.KEY_CANCEL].toString(),
                            modifier = Modifier
                                .background(
                                    color = Color.White, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = PrimaryBlue,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    TextButton(
                        onClick = {
                            if (isPinFilled) {
                                // enter either phone or userID
                                viewModel.loginWithPinRequestCall(
                                    pinValue,
                                    "", userId.toString(),
                                    viewModel.getLanguageId().toInt()
                                )
                            } else {
                                inValidPin = true
//                                errorMsg = "Please enter Pin"
                                errorMsg =
                                    languageData[LanguageTranslationsResponse.KEY_PLS_ENTER_PIN].toString()
                            }
                        },

                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
//                            text = stringResource(id = R.string.txt_confirm),
                            text = languageData[LanguageTranslationsResponse.KEY_CONFIRM].toString(),
                            modifier = Modifier
                                .background(
                                    color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = Color.White,
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
fun VerifyRecoverOtpBottomSheet(
    navController: NavHostController,
    phoneNo: String,
    viewModel: LoginViewModel,
    context: Context,
    onDismiss: () -> Unit,
) {
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

//    var invalidOtpText = stringResource(id = R.string.invalid_OTP)
    var invalidOtpText = languageData[LanguageTranslationsResponse.KEY_INVALID_OTP].toString()

    var otpValue by remember { mutableStateOf("") }
    var inValidOTP by remember { mutableStateOf(false) }
    var successTitle by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    var successSheet by remember { mutableStateOf(false) }

    // show Success sheet while click on logout
    if (successSheet) {
        RecoverSuccessBottomSheet(
            navController,
            successTitle,
            successMessage,
            viewModel,
            languageData
        ) {
            successSheet = false
        }
    }
    LaunchedEffect(Unit) {
        // ReSending otp
        viewModel.sendOtpResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {

                }

                is NetworkStatus.Error -> {
//                    context.toast(it.message)
                }
            }
        }
        viewModel.verifyResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true && it.data.data == "OTP verify successfully") {
                        if (it.data.isSuccess) {
                            viewModel.getUserRecover()

                        } else {
                            inValidOTP = true
//                            invalidOtpText = "Invalid Pin"
                            invalidOtpText =
                                languageData[LanguageTranslationsResponse.KEY_INVALID_PIN].toString()
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    inValidOTP = true
                    invalidOtpText = it.message
                }
            }
        }
        viewModel.getUserInactiveResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data!!.isSuccess) {
//                        successTitle = "Account Successfully Recovered!"
                        successTitle =
                            languageData[LanguageTranslationsResponse.KEY_RECOVERED_ACC].toString()
                        successMessage =
                            it.data.data.message
                        successSheet = true
                    } else {
                        inValidOTP = true
                        invalidOtpText = it.data.data.message
                    }
                }

                is NetworkStatus.Error -> {
                    inValidOTP = true
                    invalidOtpText = it.message
                }
            }
        }
    }

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
            /* Icon(
                 painter = painterResource(id = R.drawable.ic_lock_bg), // Replace with your drawable resource
                 contentDescription = null, // Provide a description for accessibility purposes
                 modifier = Modifier.wrapContentSize(),
             )*/

            Text(
//                stringResource(id = R.string.text_enter_otp),
                text = languageData[LanguageTranslationsResponse.ENTER_OTP].toString(),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
//                stringResource(id = R.string.txt_otp_has_been_sent),
                text = languageData[LanguageTranslationsResponse.KEY_OTP_HAS_BEEN_SENT].toString(),
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Gray,
                    fontSize = 14.sp,
                ),
                textAlign = TextAlign.Center
            )
            Text(
                "+91-$phoneNo",
                modifier = Modifier.padding(bottom = 10.dp),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Black,
                    fontSize = 14.sp,
                ),
                textAlign = TextAlign.Center
            )

            otpValue = otpTextField(/*loginViewModel*/)  // inside VerifyOTScreen
            if (inValidOTP) {
                Text(
                    invalidOtpText,
                    color = LightRed01,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp
                )
            }

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
                            viewModel.sendOtp(phoneNo.encryptAES().toString())
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text(
//                            text = stringResource(id = R.string.text_resend),
                            text = languageData[LanguageTranslationsResponse.KEY_RESEND].toString(),
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
                            showError = otpValue.isEmpty()
                            if (showError || otpValue.length < 6) {
                                inValidOTP = true
                            } else {
                                viewModel.verifyOtp(otpValue, phoneNo.encryptAES().toString())
                                inValidOTP = false
                            }
                        },

                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
//                            text = stringResource(id = R.string.text_verify_otp),
                            text = languageData[LanguageTranslationsResponse.VERIFY].toString(),
                            modifier = Modifier
                                .background(
                                    color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = Color.White,
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
fun RecoverSuccessBottomSheet(
    navController: NavHostController,
    title: String,
    description: String,
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

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
                painter = painterResource(id = R.drawable.ic_success), // Replace with your drawable resource
                contentDescription = null, // Provide a description for accessibility purposes
                tint = Color.Unspecified,
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Unspecified),
            )

            Text(
                title,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                description,
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Gray,
                    fontSize = 14.sp,
                ),
                textAlign = TextAlign.Center
            )
            TextButton(
                onClick = {
                    onDismiss()
//                    navController.navigate(
//                        AppRoute.ChildList(
//                            viewModel.getUserId(),
//                            viewModel.getUserPin().toString()
//                        )
//                    )

                    viewModel.saveUserLogin(true)
                    context.startActivity(Intent(
                        context, StudentDashboardActivity::class.java
                    ).apply { (context as Activity).finish() })
                },

                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.okay_got_it),
                    modifier = Modifier
                        .background(
                            color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                        )
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}


/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoverSuccessBottomSheet(
    navController: NavHostController,
    title: String,
    description: String,
    viewModel: LoginViewModel,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

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
                painter = painterResource(id = R.drawable.ic_success), // Replace with your drawable resource
                contentDescription = null, // Provide a description for accessibility purposes
                tint = Color.Unspecified,
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Unspecified),
            )

            Text(
                title,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                description,
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Gray,
                    fontSize = 14.sp,
                ),
                textAlign = TextAlign.Center
            )
            TextButton(
                onClick = {
                    onDismiss()
                    navController.navigate(
                        AppRoute.ChildList(
                            viewModel.getUserId(),
                            viewModel.getUserPin().toString()
                        )
                    )
                },

                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.okay_got_it),
                    modifier = Modifier
                        .background(
                            color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                        )
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoutChildBottomSheetMenu(
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 25.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items in the Column
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile_bg), // Replace with your drawable resource
                contentDescription = null, // Provide a description for accessibility purposes
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Unspecified),
                tint = Unspecified
            )
            TextButton(onClick = {

            }) {
                Text(
//                    stringResource(id = R.string.txt_sure_logout),
                    text = if (languageData[LanguageTranslationsResponse.KEY_SURE_WANT_LOGOUT].toString() == "") {
                        stringResource(id = R.string.txt_sure_logout)
                    } else {
                        languageData[LanguageTranslationsResponse.KEY_SURE_WANT_LOGOUT].toString()
                    },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                    )
                )
            }
            Divider(
                color = GrayLight02,
                thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TextButton(
                        onClick = {
                            onDismiss()
                        },
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 5.dp)
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
//                            stringResource(id = R.string.txt_cancel),
                            text = languageData[LanguageTranslationsResponse.KEY_CANCEL].toString(),
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 5.dp)
                                .wrapContentWidth(),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = PrimaryBlue,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.SemiBold,
                            )
                        )
                    }
                    TextButton(
                        onClick = {
//                            onDismiss()
                            viewModel.saveScreenName(isLogout)
                            viewModel.clearPreferenceData(context)
                            context.startActivity(Intent(context, LoginMainActivity::class.java))
                                .also {
                                    if (context is Activity) {
                                        context.finish()
                                    }
                                }

                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White, containerColor = LightRed01
                        ),
                        modifier = Modifier
                            .padding(vertical = 5.dp, horizontal = 5.dp)
                            .fillMaxWidth()
                            .weight(1f),

                        ) {
                        Text(
//                            stringResource(id = R.string.txt_logout),
                            text = languageData[LanguageTranslationsResponse.LOGOUT].toString(),
                            modifier = Modifier.wrapContentWidth(),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Color.White,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.SemiBold,
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChildAndParentListUi() {

    AuroscholarAppTheme {
        ChildListScreen(
            navHostController = rememberNavController(),
            context = LocalContext.current,
            viewModel = hiltViewModel(),
            sharedPref = null, isUserPinSet = null
        )
    }
}

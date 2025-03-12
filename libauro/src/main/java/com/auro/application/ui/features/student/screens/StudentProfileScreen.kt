package com.auro.application.ui.features.student.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.CardColors
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
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
import com.auro.application.R
import com.auro.application.core.ConstantVariables.isLogout
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.api.aes.AESEncryption.decrypt
import com.auro.application.data.api.aes.AESEncryption.encryptAES
import com.auro.application.ui.common_ui.OtpInputField
import com.auro.application.ui.common_ui.PasswordTextField
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.LoginMainActivity
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.screens.otpTextField
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.model.StudentInformetionResponseModel
import com.auro.application.ui.features.student.models.StudentProfileResponseModel
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightRed01
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White

@Composable
fun StudentProfileScreen(
    navHostController: NavHostController = rememberNavController(),
    context: Context,
    sendBundle: (Any) -> Unit
) {
    val viewModel: LoginViewModel = hiltViewModel()   // to get sharedPref data
    val studentViewModel: StudentViewModel = hiltViewModel() // to call api

    var showSheetMenu by remember { mutableStateOf(false) }
    var isDialogVisible by remember { mutableStateOf(false) }
    var profileData by remember { mutableStateOf<StudentProfileResponseModel.ProfileData?>(null) }
    val profilerData =
        remember { mutableStateListOf<StudentProfileResponseModel.ProfileData.ProfilerData>() }


    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    if (showSheetMenu) {
        ProfileBottomSheetMenu(viewModel, languageData, isDialogVisible) {
            showSheetMenu = false
        }
    }

    var schoolListOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        studentViewModel.getStudentProfileResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    profilerData.clear()
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        when (it.data.data.gender) {
                            "M", "Male" -> {
                                it.data.data.gender = "Male"
                            }

                            "F", "Female" -> {
                                it.data.data.gender = "Female"
                            }

                            else -> {
                                it.data.data.gender = "Others"
                            }
                        }
                        profileData = it.data.data
                        profilerData.addAll(profileData!!.profiler!!)
                        viewModel.saveStudentProfileData(profileData!!)
                        println("Profile data saved :- $profileData")
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                }
            }
        }
    }

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()
    // Profile API
    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = true },
//        message = "Loading your data..."
        message = msgLoader
    )
    // APi to get Profile Data
    studentViewModel.getStudentProfile()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.back_icon),
                    contentDescription = "logo",
                    modifier = Modifier
                        .clickable {
                            navHostController.popBackStack()
                            navHostController.navigateUp()
                        }
                        .background(Color.Unspecified),
                    colorFilter = ColorFilter.tint(Black)
                )
                Text(
                    text = if (languageData[LanguageTranslationsResponse.PROFILE].toString() == "") {
                        stringResource(id = R.string.txt_profile)
                    } else {
                        languageData[LanguageTranslationsResponse.PROFILE].toString()
                    },
                    modifier = Modifier
                        .wrapContentSize()
                        .weight(1f)
                        .padding(start = 20.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )
                Image(
                    painter = painterResource(R.drawable.edit_profile_icon),
                    contentDescription = "logo",
                    modifier = Modifier
                        .clickable {
                            navHostController.navigate(AppRoute.StudentEditProfile.route)
                        }
                        .background(Color.Unspecified),
                    colorFilter = ColorFilter.tint(Black)
                )
                Image(
                    painter = painterResource(R.drawable.more_icon),
                    contentDescription = "logo",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable {
                            showSheetMenu = true
                        }
                        .background(Color.Unspecified),
                    colorFilter = ColorFilter.tint(Black)
                )
            }
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 5.dp)
                    .wrapContentSize(),
                colors = CardColors(
                    containerColor = PrimaryBlue,
                    contentColor = PrimaryBlue,
                    disabledContentColor = PrimaryBlue,
                    disabledContainerColor = PrimaryBlue
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (viewModel.getUserImage().isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(72.dp) // set the size of the circle
                                .border(BorderStroke(1.dp, GrayLight02), CircleShape)
                                .background(
                                    White, CircleShape
                                ) // background color with circle shape
                        ) {
                            Image(
                                painter = if (viewModel.getUserImage().isNotEmpty()) {
                                    rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data(viewModel.getUserImage())
                                            .placeholder(R.drawable.icon_male_student)
                                            .error(R.drawable.icon_male_student)
                                            .crossfade(true)
                                            .build()
                                    )
                                } else {
                                    painterResource(R.drawable.ic_profile)
                                },
                                contentDescription = "logo",
                                modifier = Modifier
                                    .size(100.dp) // Add size modifier to make the image visible
                                    .clip(CircleShape) // Add clip modifier to make the image circular
                                    .background(shape = CircleShape,color = White)
                                    .border( // Add border modifier to make image stand out
                                        width = 1.dp, color = GrayLight02, shape = CircleShape
                                    ),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        if (viewModel.getUserName().isNotEmpty()) {
                            val initials =
                                viewModel.getUserName().split(" ").take(2)
                                    .joinToString("") { it.firstOrNull()?.toString() ?: "" }
                                    .uppercase()
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(100.dp) // set the size of the circle
                                    .border(BorderStroke(1.dp, GrayLight02), CircleShape)
                                    .background(
                                        White, CircleShape
                                    ) // background color with circle shape
                            ) {
                                Text(
                                    text = initials,
                                    color = PrimaryBlue,
                                    fontSize = (100 / 2).sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Image(
                                painterResource(R.drawable.ic_profile),
                                contentDescription = "logo",
                                modifier = Modifier
                                    .size(36.dp) // Set the image size
                                    .clip(CircleShape) // Clip the image to a circular shape
                                    .background(color = Color.White, shape = CircleShape)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .weight(1f),
                    ) {
                        Text(
                            text = if (profileData != null) {
                                profileData!!.name
                            } else "", style = MaterialTheme.typography.bodyMedium.copy(
                                color = White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = if (profileData != null) {
                                if (profileData!!.userId != null) {
                                    "UserId : " + profileData!!.userId
                                } else {
                                    "UserId : "
                                }
                            } else "UserId :", style = MaterialTheme.typography.bodyMedium.copy(
                                color = White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(text = if (profileData != null) {
//                                profileData!!.email
                            if (profileData!!.email != null) {
                                val isEncrypted = remember { isEncrypted(profileData!!.email) }
                                if (isEncrypted) {
                                    decrypt(profileData!!.email)
                                } else {
                                    profileData!!.email
                                }
                            } else {
                                ""
                            }
//                                decrypt(profileData!!.email)
                        } else "", style = MaterialTheme.typography.bodyMedium.copy(
                            color = White, fontSize = 12.sp, fontWeight = FontWeight.Normal
                        ))
                        Text(text = if (profileData != null) {
                            if (profileData!!.phone != null) {
                                val isEncrypted = remember { isEncryptedPhone(profileData!!.phone) }
                                if (isEncrypted) {
                                    decrypt(profileData!!.phone)
                                } else {
                                    profileData!!.phone
                                }
                            } else {
                                ""
                            }
//                                decrypt(profileData!!.phone)
                        } else "", style = MaterialTheme.typography.bodyMedium.copy(
                            color = White, fontSize = 12.sp, fontWeight = FontWeight.Normal
                        ))
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 5.dp, bottom = 10.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxSize()

                    ) {
                        Column(
                            modifier = Modifier
                                .border(
                                    width = 0.5.dp,
                                    color = GrayLight02,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = if (languageData[LanguageTranslationsResponse.KEY_PERSONAL_DETAILS].toString() == "") {
                                        stringResource(id = R.string.txt_personal_details)
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_PERSONAL_DETAILS].toString()
                                    },
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp, bottom = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = Black,
                                    textAlign = TextAlign.Start
                                )
                                Text(
//                                    text = stringResource(id = R.string.txt_gender),
                                    text = languageData[LanguageTranslationsResponse.GENDER].toString(),
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = GrayLight01,
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = if (profileData != null) {
                                        if (profileData!!.gender != null) {
                                            profileData!!.gender
                                        } else {
                                            ""
                                        }
//                                        profileData!!.gender
                                    } else "",
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp, bottom = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Black,
                                    textAlign = TextAlign.Start
                                )
                                Text(
//                                    text = stringResource(id = R.string.txt_state),
                                    text = languageData[LanguageTranslationsResponse.KEY_STATE].toString(),
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = GrayLight01,
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = if (profileData != null) {
                                        if (profileData!!.stateName != null) {
                                            profileData!!.stateName
                                        } else {
                                            ""
                                        }
//                                        profileData!!.stateName
                                    } else "",
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp, bottom = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Black,
                                    textAlign = TextAlign.Start
                                )

                                Text(
//                                    text = stringResource(id = R.string.txt_district_city),
                                    text = languageData[LanguageTranslationsResponse.KEY_CITY_DIST].toString(),
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = GrayLight01,
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = if (profileData != null) {
                                        if (profileData!!.districtName != null) {
                                            profileData!!.districtName
                                        } else {
                                            ""
                                        }
//                                        profileData!!.districtName
                                    } else "",
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp, bottom = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Black,
                                    textAlign = TextAlign.Start
                                )

                                Text(
//                                    text = stringResource(id = R.string.txt_date_of_birth),
                                    text = languageData[LanguageTranslationsResponse.DATE_OF_BIRTH].toString(),
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = GrayLight01,
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = if (profileData != null) {
                                        if (profileData!!.dateOfBirth != null) {
                                            profileData!!.dateOfBirth
                                        } else {
                                            ""
                                        }
//                                        profileData!!.dateOfBirth
                                    } else "",
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp, bottom = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Black,
                                    textAlign = TextAlign.Start
                                )

                            }
                        }

                        Column(
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = GrayLight02,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (languageData[LanguageTranslationsResponse.KEY_PROFESSIONAL_DETAILS].toString() == "") {
                                        stringResource(id = R.string.txt_professional_details)
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_PROFESSIONAL_DETAILS].toString()
                                    },
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = Black,
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = if (languageData[LanguageTranslationsResponse.USER_NAME].toString() == "") {
                                        stringResource(id = R.string.txt_username)
                                    } else {
                                        languageData[LanguageTranslationsResponse.USER_NAME].toString()
                                    },
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = GrayLight01,
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = if (profileData != null) {
//                                        profileData!!.userName
                                        if (profileData!!.userName != null) {
                                            decrypt(profileData!!.userName)
                                        } else {
                                            ""
                                        }
//                                        decrypt(profileData!!.userName)
                                    } else "",
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp, bottom = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Black,
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = if (languageData[LanguageTranslationsResponse.KEY_GRADE].toString() == "") {
                                        stringResource(id = R.string.txt_grade)
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_GRADE].toString()
                                    },
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = GrayLight01,
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = if (profileData != null) {
                                        if (profileData!!.grade != null) {
                                            profileData!!.grade.toString()
                                        } else {
                                            ""
                                        }
//                                        profileData!!.grade.toString()
                                    } else "",
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp, bottom = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Black,
                                    textAlign = TextAlign.Start
                                )

                                Text(
                                    text = if (languageData[LanguageTranslationsResponse.KEY_SCHOOL_NAME].toString() == "") {
                                        stringResource(id = R.string.txt_school_name)
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_SCHOOL_NAME].toString()
                                    },
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = GrayLight01,
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = if (profileData != null) {
                                        if (profileData!!.schoolName != null) {
                                            profileData!!.schoolName.toString()
                                        } else {
                                            ""
                                        }
//                                        profileData!!.schoolName.toString()
                                    } else "",
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp, bottom = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Black,
                                    textAlign = TextAlign.Start
                                )

                                Text(
                                    text = if (languageData[LanguageTranslationsResponse.KEY_SCHOOL_BOARD].toString() == "") {
                                        stringResource(id = R.string.school_board)
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_SCHOOL_BOARD].toString()
                                    },
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = GrayLight01,
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = if (profileData != null) {
                                        if (profileData!!.boardName != null) {
                                            profileData!!.boardName
                                        } else {
                                            ""
                                        }
//                                        profileData!!.boardName
                                    } else "",
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp, bottom = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Black,
                                    textAlign = TextAlign.Start
                                )

                                Text(
                                    text = if (languageData[LanguageTranslationsResponse.SCHOOL_MED].toString() == "") {
                                        stringResource(id = R.string.school_medium)
                                    } else {
                                        languageData[LanguageTranslationsResponse.SCHOOL_MED].toString()
                                    },
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = GrayLight01,
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = if (profileData != null) {
                                        if (profileData!!.mediumName != null) {
                                            profileData!!.mediumName
                                        } else {
                                            ""
                                        }
//                                        profileData!!.mediumName
                                    } else "",
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp, bottom = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Black,
                                    textAlign = TextAlign.Start
                                )

                            }
                        }
                        Column(
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = GrayLight02,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (languageData[LanguageTranslationsResponse.KEY_PROFILER].toString() == "") {
                                        stringResource(id = R.string.txt_profiler)
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_PROFILER].toString()
                                    },
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = Black,
                                    textAlign = TextAlign.Start
                                )
                                Log.d("profilerData:", "" + profilerData.size)
                                if (profilerData.isNotEmpty()) {
                                    profilerData.forEach { profiler ->
                                        ProfilerData(
                                            profiler, viewModel
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun isEncrypted(email: String): Boolean {
    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    return !(emailPattern.matches(email))
}

fun isEncryptedPhone(data: String): Boolean {
    val phonePattern = Regex("^\\+?[0-9]{10,15}$")
    return !(phonePattern.matches(data))
}

@Composable
private fun ProfilerData(
    profiler: StudentProfileResponseModel.ProfileData.ProfilerData, viewModel: LoginViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize()

    ) {
        Text(
            text = profiler.question,
            modifier = Modifier
                .wrapContentSize()
                .padding(start = 10.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = GrayLight01,
            textAlign = TextAlign.Start
        )
        Text(
            text = profiler.answer,
            modifier = Modifier
                .wrapContentSize()
                .padding(start = 10.dp, bottom = 10.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = Black,
            textAlign = TextAlign.Start
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheetMenu(
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    isDialogVisible: Boolean,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var logOutSheet by remember { mutableStateOf(false) }
    // show logout sheet while click on logout
    if (logOutSheet) {
        LogoutBottomSheetMenu(viewModel, languageData) {
            logOutSheet = false
        }
    }
    var confirmDeleteState by remember { mutableStateOf(false) }
    // show logout sheet while click on logout
    if (confirmDeleteState) {
        ConfirmationDeleteAccountBottomSheet(viewModel, languageData, isDialogVisible) {
            confirmDeleteState = false
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
                .padding(start = 15.dp, end = 15.dp, bottom = 20.dp)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(start = 5.dp),
                verticalAlignment = Alignment.CenterVertically // Vertically center items in the Row
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_delete), // Replace with your drawable resource
                    contentDescription = "delete", // Provide a description for accessibility purposes
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Unspecified),
                )

                TextButton(onClick = {
//                    onDismiss()
                    confirmDeleteState = true
                }) {
                    Text(
//                        stringResource(id = R.string.txt_delete_account)
                        text = languageData[LanguageTranslationsResponse.REMOVE_ACC].toString(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = LightRed01,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start
                        )
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(
                    start = 8.dp, end = 8.dp, bottom = 10.dp
                )// Vertically center items in the Row
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logout), // Replace with your drawable resource
                    contentDescription = "icon", // Provide a description for accessibility purposes
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Unspecified),
                    tint = Unspecified
                )
                TextButton(
                    onClick = {
//                        onDismiss()
                        logOutSheet = true

                    },
                ) {
                    Text(
//                        stringResource(id = R.string.txt_logout),
                        text = languageData[LanguageTranslationsResponse.LOGOUT].toString(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoutBottomSheetMenu(
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    onDismiss: () -> Unit
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
                            viewModel.saveScreenName(isLogout)
                            viewModel.clearPreferenceData(context)
                            onDismiss()
                            context.startActivity(Intent(context, LoginMainActivity::class.java))
                                .also {
                                    if (context is Activity) {
                                        context.finish()   // This will close the current Activity
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDeleteAccountBottomSheet(
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    isDialogVisible: Boolean,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var verifyPinSheet by remember { mutableStateOf(false) }

    // show verify pin sheet while click on logout
    if (verifyPinSheet) {
//        onDismiss()
        VerifyPinBottomSheet(viewModel, languageData, isDialogVisible) {
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
                tint = Unspecified,
                contentDescription = null,
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Unspecified),
            )

            Text(
//                stringResource(id = R.string.txt_sure_delete_acc),
                text = languageData[LanguageTranslationsResponse.DELETE_ACCOUNT_CONFIRMATION].toString(),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            Text(
//                stringResource(id = R.string.txt_sure_delete_desc),
                text = languageData[LanguageTranslationsResponse.DELETE_REQUIREMENTS].toString() + "\n" +
                        languageData[LanguageTranslationsResponse.DELETE_DATA_WARNING].toString(),
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
                            text = languageData[LanguageTranslationsResponse.KEY_DELETE].toString(),
                            modifier = Modifier
                                .background(
                                    color = LightRed01, shape = RoundedCornerShape(12.dp)
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
fun VerifyPasswordBottomSheet(viewModel: LoginViewModel, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val password = remember { mutableStateOf("") }
    val showPassword = remember { mutableStateOf(false) }
    val enterYourPassword = stringResource(id = R.string.enter_your_password)

    var verifyPinSheet by remember { mutableStateOf(false) }

    // show verify pin sheet while click on logout

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        /*if (verifyPinSheet) {
            VerifyPinBottomSheet(viewModel, isDialogVisible) {
                verifyPinSheet = false
            }
        }*/
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
                modifier = Modifier.wrapContentSize(),
            )

            Text(
                stringResource(id = R.string.txt_verify_password),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            PasswordTextField(
                password = password, showPassword = showPassword, hint = enterYourPassword
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
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_cancel),
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
//                            onDismiss()
                            verifyPinSheet = true
                        },

                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_confirm),
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
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    isDialogVisible: Boolean,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var isPinFilled by remember { mutableStateOf(false) }
    var inValidPin by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var errorMsg by remember { mutableStateOf("") }
    val phoneNo = viewModel.getUserPhoneNo()
    val token = viewModel.getToken()
//    var textPhoneNo by remember { mutableStateOf(phoneNo) }
    var pinValue by remember { mutableStateOf("") }
    val context = LocalContext.current

    var verifyOTPSheet by remember { mutableStateOf(false) }
    // if pin verified successfully for delete account
    if (verifyOTPSheet) {
        VerifyOtpBottomSheet(phoneNo, viewModel, languageData) {
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
                        viewModel.saveUserName(it.data.data?.userDetails!!.name)
                        viewModel.saveUserEmail(it.data.data.userDetails.email)
                        viewModel.saveUserImage(it.data.data.userDetails.profilePic)
                        viewModel.setLoginInfo(it.data.data.userDetails)
                        viewModel.sendOtp(phoneNo.encryptAES().toString())

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
                    context.toast(it.message)
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
                    errorMsg,
                    color = LightRed01,
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
                                    "",
                                    viewModel.getUserId().toString(),
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
fun VerifyOtpBottomSheet(
    phoneNo: String,
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    onDismiss: () -> Unit
) {
//    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })

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
        SuccessBottomSheet(successTitle, successMessage, viewModel, languageData) {
            successSheet = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.verifyResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data?.isSuccess == true && it.data.data == "OTP verify successfully") {
                        if (it.data.isSuccess) {
                            viewModel.getUserInactive()

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
//                        successTitle = "Account Successfully Deleted!"
                        successTitle =
                            languageData[LanguageTranslationsResponse.ACCOUNT_DELETED_SUCCESS].toString()
                        successMessage = it.data.data.message
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
                text = languageData[LanguageTranslationsResponse.ENTER_YOUR_OTP_HERE].toString(),
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
                        onClick = { onDismiss() }, modifier = Modifier.weight(1f)
                    ) {
                        Text(
//                            text = stringResource(id = R.string.txt_cancel),
                            text = languageData[LanguageTranslationsResponse.KEY_CANCEL].toString(),
                            modifier = Modifier.background(
                                color = Transparent, shape = RoundedCornerShape(12.dp)
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
fun SuccessBottomSheet(
    title: String,
    description: String,
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    onDismiss: () -> Unit
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
                    viewModel.saveScreenName(isLogout)
                    viewModel.clearPreferenceData(context)
                    context.startActivity(Intent(context, LoginMainActivity::class.java)).also {
                        if (context is Activity) {
                            context.finish()   // This will close the current Activity
                        }
                    }
                },

                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                Text(
//                    text = stringResource(id = R.string.okay_got_it),
                    text = languageData[LanguageTranslationsResponse.OKAY_GOT_IT].toString(),
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

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun RegistatoinStep2() {
    StudentProfileScreen(navHostController = rememberNavController(),
        context = LocalContext.current,
        sendBundle = {

        })
}
package com.auro.application.ui.features.parent.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import com.auro.application.R
import com.auro.application.core.ConstantVariables.isLogout
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.Constants
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.api.aes.AESEncryption.decrypt
import com.auro.application.data.api.aes.AESEncryption.encryptAES
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.OtpInputField
import com.auro.application.ui.common_ui.PasswordTextField
import com.auro.application.ui.common_ui.components.DoubleBackPressHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.LoginMainActivity
import com.auro.application.ui.features.login.models.ChildListResponse
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.screens.otpTextField
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionOptionsResponseModel
import com.auro.application.ui.features.parent.model.ParentProfileData
import com.auro.application.ui.features.parent.viewmodel.ParentViewModel
import com.auro.application.ui.features.student.screens.isEncrypted
import com.auro.application.ui.features.student.screens.isEncryptedPhone
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightRed01
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun ParentProfileScreen(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    val sharedPref: SharedPref? = null
    val viewModels: LoginViewModel = hiltViewModel()
    val viewModel: ParentViewModel = hiltViewModel()   // to get sharedPref data
    var showSheetMenu by remember { mutableStateOf(false) }
    var schoolListOpen by remember { mutableStateOf(false) }
    var isDialogVisible by remember { mutableStateOf(false) }
    var parentProfileData by remember { mutableStateOf<ParentProfileData?>(null) }
    val profilerData =
        remember { mutableStateListOf<GetCompleteProfilerQuestionOptionsResponseModel.Data>() }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModels.getLanguageTranslationData(languageListName)

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    DoubleBackPressHandler {
        navController.popBackStack()
        (context as? Activity)?.finish()

    }

    CustomDialog(
        isVisible = isDialogVisible, onDismiss = { isDialogVisible = false }, message = msgLoader
    )

    if (showSheetMenu) {
        ProfileBottomSheetMenu(viewModels, languageData) {
            showSheetMenu = false
        }
    }

    LaunchedEffect(Unit) {
        sharedPref?.saveLogin(true)
    }

    LaunchedEffect(Unit) {
        viewModel.parentProfileResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        if (it.data.data.gender != null) {
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
                        } else {
                            ""
                        }
                        parentProfileData = it.data.data
                        viewModels.saveParentProfileData(parentProfileData!!)
                        println("Parent profile data details :- $parentProfileData")
                    } else {
                        context.toast(it.data!!.error.toString())
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                }
            }
        }
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

                    // Clear the existing list before adding new questions
                    profilerData.clear()
                    networkStatus.data?.data?.let {
                        profilerData.addAll(it)
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    // Handle the error case, e.g., show a message to the user
                }
            }
        }

        viewModel.getParentProfile()
        viewModel.getCompleteProfilerQuestionAndOption()
    }

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
                Image(painter = painterResource(R.drawable.back_icon),
                    contentDescription = "logo",
                    modifier = Modifier
                        .clickable {
                            navController.popBackStack()
                        }
                        .background(Color.Unspecified),
                    colorFilter = ColorFilter.tint(Black))
                Text(
//                    text = stringResource(id = R.string.txt_profile),
                    text = languageData[LanguageTranslationsResponse.PROFILE].toString(),
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
                Image(painter = painterResource(R.drawable.edit_profile_icon),
                    contentDescription = "logo",
                    modifier = Modifier
                        .clickable {
                            navController.popBackStack()
                            navController.navigate(AppRoute.ParentEditProfile.route)
                        }
                        .background(Color.Unspecified),
                    colorFilter = ColorFilter.tint(Black))
                Image(painter = painterResource(R.drawable.more_icon),
                    contentDescription = "logo",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable {
                            showSheetMenu = true
                        }
                        .background(Color.Unspecified),
                    colorFilter = ColorFilter.tint(Black))
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
                    Box(
                        modifier = Modifier.padding(10.dp) // Add padding to the outer box
                    ) {
                        if (viewModels.getUserName().isNotEmpty()) {
                            val initials = viewModels.getUserName().split(" ").take(2)
                                .joinToString("") { it.firstOrNull()?.toString() ?: "" }.uppercase()
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(100.dp) // set the size of the circle
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
                                    .background(Color.Unspecified)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .weight(1f),
                    ) {
                        Text(
                            text = (if (parentProfileData != null) {
                                if (parentProfileData?.name != null) {
                                    parentProfileData!!.name
                                } else {
                                    ""
                                }
                            } else {
                                ""
                            })!!, style = MaterialTheme.typography.bodyMedium.copy(
                                color = White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(text = (if (parentProfileData != null) {
                            if (parentProfileData!!.email != null) {
                                val isEncrypted =
                                    remember { isEncrypted(parentProfileData!!.email!!) }
                                if (isEncrypted) {
                                    decrypt(parentProfileData!!.email!!)
                                } else {
                                    parentProfileData!!.email
                                }
                            } else {
                                ""
                            }
                            //                                parentProfileData!!.email.toString()
                        } else {
                            ""
                        })!!, style = MaterialTheme.typography.bodyMedium.copy(
                            color = White, fontSize = 12.sp, fontWeight = FontWeight.Normal
                        ))

                        Text(text = (if (parentProfileData != null) {
                            if (parentProfileData!!.phone != null) {
                                val isEncrypted =
                                    remember { isEncryptedPhone(parentProfileData!!.phone!!) }
                                if (isEncrypted) {
                                    decrypt(parentProfileData!!.phone!!)
                                } else {
                                    parentProfileData!!.phone
                                }
                            } else {
                                ""
                            }
                            //                                decrypt(parentProfileData!!.phone)
                        } else {
                            "+91-0000000000"
                        })!!, style = MaterialTheme.typography.bodyMedium.copy(
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
//                                    text = stringResource(id = R.string.txt_personal_details),
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
                                    text = if (parentProfileData != null) {
                                        if (parentProfileData?.gender != null) {
                                            parentProfileData!!.gender.toString()
                                        } else {
                                            ""
                                        }
                                    } else {
                                        ""
                                    },
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
                                    text = if (parentProfileData != null) {
                                        if (parentProfileData!!.stateName != null) {
                                            parentProfileData!!.stateName.toString()
                                        } else {
                                            ""
                                        }
                                    } else {
                                        ""
                                    },
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
                                    text = if (parentProfileData != null) {
                                        if (parentProfileData!!.districtName != null) {
                                            parentProfileData!!.districtName.toString()
                                        } else {
                                            ""
                                        }
                                    } else {
                                        ""
                                    },
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
                                    text = if (parentProfileData != null) {
                                        if (parentProfileData!!.dob != null) {
                                            val isoDateString = parentProfileData!!.dob
                                            val nisoDateString = isoDateString.replace(" " , "T")
                                            val instant =  Instant.parse("2007-02-13T05:30:00Z")   //Instant.parse(nisoDateString)
                                            val formatter =
                                                DateTimeFormatter.ofPattern("yyyy-MM-dd")  //dd/MM/yyyy
                                                    .withZone(ZoneId.systemDefault())
                                            "${formatter.format(instant)} "
                                        } else {
                                            ""
                                        }
//                                        parentProfileData!!.dob.toString()
                                    } else {
                                        "10/06/94"
                                    },
//                                    text = "10/06/94",
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
                                modifier = Modifier.wrapContentSize()
                            ) {
                                Text(
//                                    text = stringResource(id = R.string.txt_professional_details),
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
//                                    text = stringResource(id = R.string.txt_username),
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
                                    text = if (parentProfileData != null) {
                                        if (parentProfileData!!.username != null) {
                                            decrypt(parentProfileData!!.username)
                                        } else {
                                            ""
                                        }
                                    } else {
                                        "test@123"
                                    },
//                                    text = "test@123",
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
//                                    text = stringResource(id = R.string.numberOfChildren),
                                    text = if (languageData[LanguageTranslationsResponse.KEY_NO_CHILDREN].toString() == "") {
                                        stringResource(id = R.string.numberOfChildren)
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_NO_CHILDREN].toString()
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
                                    text = if (parentProfileData != null) {
                                        if (parentProfileData!!.studentCount != null) {
                                            parentProfileData!!.studentCount.toString()
                                        } else {
                                            ""
                                        }
                                    } else {
                                        "0"
                                    },
//                                    text = "4",
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
//                                    text = stringResource(id = R.string.alternateMobileNumber),
                                    text = if (languageData[LanguageTranslationsResponse.KEY_ALTERNATE_NO].toString() == "") {
                                        stringResource(id = R.string.alternateMobileNumber)
                                    } else {
                                        languageData[LanguageTranslationsResponse.KEY_ALTERNATE_NO].toString()
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
                                Text(text = if (parentProfileData != null) {
                                    if (parentProfileData!!.alternativePhone != null) {
                                        val isEncrypted =
                                            remember { isEncryptedPhone(parentProfileData!!.alternativePhone) }
                                        if (isEncrypted) {
                                            decrypt(parentProfileData!!.alternativePhone)
                                        } else {
                                            parentProfileData!!.alternativePhone
                                        }
                                    } else {
                                        ""
                                    }
//                                        parentProfileData!!.alternativePhone.toString()
                                } else {
                                    "+91-9000000000"
                                },
//                                    text = "9898989898",
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp, bottom = 10.dp),
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = Black,
                                    textAlign = TextAlign.Start)
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
//                                    text = stringResource(id = R.string.txt_profiler),
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
                                )/*if (parentProfileData != null) {
                                    val listOf = listOf(parentProfileData?.dob.toString())
                                    listOf.forEach {
//                                        RowItem(it)
                                    }
                                }*/
                                if (profilerData.isNotEmpty()) {
                                    profilerData.forEach { profiler ->
                                        ProfilerData(
                                            profiler
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

@Composable
private fun ProfilerData(
    profiler: GetCompleteProfilerQuestionOptionsResponseModel.Data,
) {
    if (profiler.answers.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize()

        ) {
            Text(
                text = profiler.question.questionText,
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
                text = profiler.answers[0].text,
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
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RowItem(dateOfBirth: String) {
    val isoDateString = dateOfBirth
    val instant = Instant.parse(isoDateString)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault())

    Column(modifier = Modifier) {
        Text(
            text = stringResource(id = R.string.txt_date_of_birth),
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
            text = "${formatter.format(instant)} ",
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
    viewModel: LoginViewModel, languageData: HashMap<String, String>, onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var logOutSheet by remember { mutableStateOf(false) }
    // show logout sheet while click on logout
    if (logOutSheet) {
        LogoutBottomSheetMenu(viewModel, languageData) {
            logOutSheet = false
        }
    }
    var openStudentListDialog by remember { mutableStateOf(false) }

    if (openStudentListDialog) {
        StudentListDialog(viewModel, languageData) {
            openStudentListDialog = false
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
                .padding(start = 15.dp, end = 15.dp, bottom = 25.dp)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(start = 5.dp),
                verticalAlignment = Alignment.CenterVertically // Vertically center items in the Row
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_delete), // Replace with your drawable resource
                    contentDescription = null, // Provide a description for accessibility purposes
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Unspecified),
                )

                TextButton(onClick = {
//                    onDismiss()
                    openStudentListDialog = true
//                    confirmDeleteState = true
                }) {
                    Text(
//                        stringResource(id = R.string.txt_delete_account),
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
fun StudentListDialog(
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var childList by remember { mutableStateOf(mutableListOf<ChildListResponse.Data.Student>()) }
    val context = LocalContext.current

    var confirmDeleteState by remember { mutableStateOf(false) }
    var isParent by remember { mutableStateOf(true) }
//    var isParent by remember { mutableStateOf(false) }
    // show delete confirmation screen while click on logout
    if (confirmDeleteState) {
        ConfirmationDeleteAccountBottomSheet(
            viewModel,
            languageData,
            isParent,
            viewModel.getUserId()
        ) {
            confirmDeleteState = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.childListLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {

                    if (it.data?.isSuccess == true) {
                        viewModel.saveChildCount(it.data.data.student.size)
                        childList = it.data.data.student.toMutableList()
                    }
                }

                is NetworkStatus.Error -> {
//                    context.toast(it.message)
                }
            }
        }
        viewModel.getChildListApi()
    }

    var childSize = childList.size.toString()

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
            Column(modifier = Modifier.fillMaxSize()) {
                Card(shape = RoundedCornerShape(12.dp), modifier = Modifier
                    .clickable {
                        confirmDeleteState = true // delete parent dialog
                    }
                    .padding(10.dp)
                    .fillMaxWidth(), border = BorderStroke(1.dp, GrayLight02)) {
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
                                .background(Color.Unspecified),
                            painter = painterResource(R.drawable.ic_parent),
                            contentDescription = "Logo"
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(10.dp)
                                .weight(0.1f)
                        ) {
                            Text(
//                                text = "Name",
                                text = if (languageData[LanguageTranslationsResponse.KEY_NAME].toString() == "") {
                                    "Name"
                                } else {
                                    languageData[LanguageTranslationsResponse.KEY_NAME].toString()
                                },
                                textAlign = TextAlign.Start,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black
                            )
                            Text(
//                                text = "Parent",
                                text = if (languageData[LanguageTranslationsResponse.KEY_PARENT].toString() == "") {
                                    "Parent"
                                } else {
                                    languageData[LanguageTranslationsResponse.KEY_PARENT].toString()
                                },
                                textAlign = TextAlign.Start,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = GrayLight01
                            )
                        }

                        Image(
                            painter = painterResource(R.drawable.ic_right_side),
                            contentDescription = "sdsds",
                            modifier = Modifier
                                .size(25.dp)
                                .background(Color.Unspecified)
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(childList) { child ->
                        ChildItem(child, viewModel, languageData, context)
                    }
                }
            }
        }
    }
}

@Composable
fun ChildItem(
    child: ChildListResponse.Data.Student,
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    context: Context
) {
    val isParent by remember { mutableStateOf(false) }
    var confirmDeleteState by remember { mutableStateOf(false) }
    var childUserId by remember { mutableStateOf("") }
//    var isParent by remember { mutableStateOf(false) }
    // show delete confirmation screen while click on logout
    if (confirmDeleteState) {
        ConfirmationDeleteAccountBottomSheet(viewModel, languageData, isParent, childUserId) {
            confirmDeleteState = false
        }
    }
    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier
        .clickable {
            confirmDeleteState = true
        }
        .padding(10.dp)
        .fillMaxWidth(), border = BorderStroke(1.dp, GrayLight02), onClick = {
        confirmDeleteState = true
        childUserId = child.userId
        if (!child.isUserPin) {         // onboarding done but Pin not created
//            context.toast("Pin not created")
            context.toast(languageData[LanguageTranslationsResponse.KEY_PIN_NOT_CREATED].toString())

        } else {
            confirmDeleteState = true
        }
    }) {
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
                    .background(Color.Unspecified),
                painter = painterResource(R.drawable.ic_parent),
                contentDescription = "Logo"
            )

            Text(
                text = child.name,
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
    isParent: Boolean,
    userId: String?,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var verifyPinSheet by remember { mutableStateOf(false) }

    // show verify pin sheet while click on logout
    if (verifyPinSheet) {
//        onDismiss()
        if (isParent) {
            VerifyPasswordBottomSheet(viewModel, languageData) {
                verifyPinSheet = false
            }
        } else {
            VerifyPinBottomSheet(viewModel, languageData, userId) {
                verifyPinSheet = false
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
fun VerifyPasswordBottomSheet(
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val password = remember { mutableStateOf("") }
    val showPassword = remember { mutableStateOf(false) }
    var inValidPassword by remember { mutableStateOf(false) }
//    val invalidPassword = stringResource(id = R.string.invalid_password)
    val invalidPassword = languageData[LanguageTranslationsResponse.ENTER_PASS].toString()
    var textInvalidPassword by remember { mutableStateOf(invalidPassword) }
//    val enterYourPassword = stringResource(id = R.string.enter_your_password)
    val enterYourPassword =
        languageData[LanguageTranslationsResponse.KEY_ENTER_YOUR_PASS].toString()
    var showError by remember { mutableStateOf(false) }
    val phoneNo = viewModel.getUserPhoneNo()
    val token = viewModel.getToken().toString()
    val context = LocalContext.current

    var verifyOTPSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
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
                        inValidPassword = true
                        textInvalidPassword = it.data!!.error
                    }
                }

                is NetworkStatus.Error -> {
                    inValidPassword = true
                    textInvalidPassword = it.message

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
        if (verifyOTPSheet) {
            VerifyOtpBottomSheet(phoneNo, viewModel, languageData, token) {
                verifyOTPSheet = false
            }
        }
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
//                stringResource(id = R.string.txt_verify_password),
                text = languageData[LanguageTranslationsResponse.KEY_VERIFY_PASSWORD].toString(),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            PasswordTextField(
                password = password, showPassword = showPassword, hint = enterYourPassword
            )

            if (inValidPassword) {
                Text(
                    textInvalidPassword,
                    color = LightRed01,
                    modifier = Modifier.padding(start = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Light,
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
                            showError = password.value.isEmpty()
                            if (showError || password.value.length < 5) {
                                inValidPassword = true
                            } else {
                                inValidPassword = false
                                phoneNo.toString().encryptAES()?.trim()?.let {
                                    viewModel.loginRequestCall(
                                        phoneNo = it, password = password.value
                                    )
                                }
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
fun VerifyPinBottomSheet(
    viewModel: LoginViewModel,
    languageData: HashMap<String, String>,
    userId: String?,
    onDismiss: () -> Unit
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
        VerifyOtpBottomSheet(phoneNo, viewModel, languageData, token) {
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
                                    userId.toString(),
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
    token: String,
    onDismiss: () -> Unit
) {
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
                            Constants.token = token
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
                    onDismiss()
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

package com.auro.application.ui.features.parent.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.auro.application.R
import com.auro.application.core.ConstantVariables.onboarding1
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.Constants
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.data.utlis.CommonFunction.getGenderIconState
import com.auro.application.data.utlis.CustomProgressBar
import com.auro.application.ui.common_ui.components.DoubleBackPressHandler
import com.auro.application.ui.common_ui.components.WalkThroughModel
import com.auro.application.ui.common_ui.components.WalkthroughDialog
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.LoginMainActivity
import com.auro.application.ui.features.login.componets.CommandAppKey
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.model.GetBannerListResponseModel
import com.auro.application.ui.features.parent.model.GetPerentProgressResponseModel
import com.auro.application.ui.features.parent.model.StudentInformetionResponseModel
import com.auro.application.ui.features.parent.viewmodel.ParentViewModel
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.PrimaryBlueLt
import com.auro.application.ui.theme.White
import java.math.RoundingMode
import java.text.DecimalFormat


@Preview(showSystemUi = true)
@Composable
fun ParentDashboardScreen(
    navController: NavHostController = rememberNavController(),
    sharedPref: SharedPref? = null,
    viewModel: ParentViewModel = hiltViewModel(),
) {

    val loginViewModel: LoginViewModel = hiltViewModel()
    var isDialogVisible by remember { mutableStateOf(false) }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    val studentData =
        remember { mutableStateListOf<StudentInformetionResponseModel.Data.Student?>() }
    val parentBannerListResponseModel =
        remember { mutableStateListOf<GetBannerListResponseModel.Data?>() }
    val parentProfilePercentage =
        remember { mutableStateOf<GetPerentProgressResponseModel.Data?>(null) }

    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(true) }

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    sharedPref?.getToken().toString().let {
        Constants.token = it
        Log.d("Token Key :- ", it)
    }

    CustomDialog(
        isVisible = isDialogVisible, onDismiss = { isDialogVisible = false },
        //message = "Loading your data..."
        message = msgLoader
    )

    DoubleBackPressHandler {
        if (!navController.popBackStack()) {
            // If unable to pop back (i.e., no more screens), finish the activity
            (context as? Activity)?.finish()
        }
    }
    LaunchedEffect(Unit) {
        sharedPref?.saveLogin(true)
    }

    LaunchedEffect(Unit) {
        viewModel.studentInformationResponseModelLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        studentData.clear()
                        Log.e(
                            "TAG",
                            "ParentDashboardScreen: child list is her " + it.data.data.student.size
                        )
                        loginViewModel.saveChildCount(it.data.data.student.size)
                        studentData.addAll(it.data.data.student)
                    }

                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
            }
        }
        viewModel.parentProfilePercentageLiveModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    parentProfilePercentage.value = it.data?.data
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
            }
        }

        viewModel.percentBannerListResponseModelLiveData.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    parentBannerListResponseModel.clear()
                    it.data?.data?.let { it1 -> parentBannerListResponseModel.addAll(it1) }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
            }
        }

        viewModel.parentProfileResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        loginViewModel.saveParentProfileData(it.data.data)
                        if ((it.data.data.name.equals("null") || it.data.data.name!!.isEmpty())
                            || (it.data.data.stateName.equals("null") || it.data.data.stateName!!.isEmpty())
                            || (it.data.data.districtName.equals("null") || it.data.data.districtName!!.isEmpty())
                        ) {
//                            navHostController.navigate(AppRoute.StudentEditProfile.route)
                            navController.navigate(AppRoute.ParentEditProfile.route)
                            Toast
                                .makeText(
                                    context,
                                    "Please Complete your profile!",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }
            }
        }

        viewModel.getParentProfile()
        viewModel.getParentStudentList()
    }

    val isShow = remember { mutableStateOf(sharedPref?.getParentOnboardingWalkthrough() ?: true) }

    val list = listOf<WalkThroughModel>(
        WalkThroughModel(
            languageData[LanguageTranslationsResponse.REGISTER_AND_COMPLETE_KYC].toString(),
            languageData[LanguageTranslationsResponse.KEY_KYC_LEARNING_TEXT].toString(),
            R.drawable.select_your_subject
        ), WalkThroughModel(
            languageData[LanguageTranslationsResponse.TAKE_TAILORED_QUIZZES_AND_PRACTICE].toString(),
            languageData[LanguageTranslationsResponse.KEY_PRACTICE_CONCEPT_TEXT].toString(),
            R.drawable.select_concepts
        ), WalkThroughModel(
            languageData[LanguageTranslationsResponse.TRACK_YOUR_PROGRESS].toString(),
            languageData[LanguageTranslationsResponse.KEY_IMPROVE_SKILLS_TEXT].toString(),
            R.drawable.other_subject
        ), WalkThroughModel(
            languageData[LanguageTranslationsResponse.WIN_SCHOLARSHIPS].toString(),
            languageData[LanguageTranslationsResponse.KEY_HIGHER_MICRO_SCHOLER_TEXT].toString(),
            R.drawable.add_more_concept
        )
    )
    if (isShow.value) {
        WalkthroughDialog(showDialog = true, onDismissRequest = {
            isShow.value = false
            sharedPref?.saveParentOnboardingWalkthrough(isShow.value)
        }, list)
    }

    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        val connection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset, source: NestedScrollSource,
                ): Offset {
                    return super.onPreScroll(available, source)
                }
            }
        }

        Box(
            Modifier
                .nestedScroll(connection)
                .padding(horizontal = 10.dp)
        ) {
            LazyColumn(contentPadding = PaddingValues(0.dp)) {
                // Add first image
                item {
                    /*Image(
                        painter = painterResource(id = R.drawable.parent_banner),
                        contentDescription = null,
                        modifier = Modifier
                            .height(200.dp)
                            .padding(vertical = 10.dp, horizontal = 5.dp)
                            .fillMaxWidth(), contentScale = ContentScale.FillBounds
                    )*/
                    Image(
                        modifier = Modifier
                            .height(200.dp)
                            .padding(vertical = 10.dp, horizontal = 5.dp)
                            .fillMaxWidth()
                            .background(Color.Unspecified),
                        painter = if (parentBannerListResponseModel.isNotEmpty()) {
                            if (parentBannerListResponseModel[0]!!.image != null) {
                                rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(parentBannerListResponseModel[0]!!.image)
                                        .decoderFactory(SvgDecoder.Factory()) // Adds SVG support
                                        .size(Size.ORIGINAL) // Use original or specify size
                                        .placeholder(R.drawable.parent_banner)
                                        .error(R.drawable.parent_banner).build()
                                )
                            } else {
                                painterResource(R.drawable.parent_banner)
                            }
                        } else {
                            painterResource(R.drawable.parent_banner)
                        },
                        contentDescription = "Logo"
                    )
                }
                // Add first image
                item {
                    ProfileProgress(
                        navController,
                        BorderStroke(width = 0.5.dp, color = GrayLight02),
                        if (languageData[LanguageTranslationsResponse.KEY_PROFILE_PROGRESS].toString() == "") {
                            stringResource(id = R.string.text_profileProgress)
                        } else {
                            languageData[LanguageTranslationsResponse.KEY_PROFILE_PROGRESS].toString()
                        },
                        if (languageData[LanguageTranslationsResponse.KEY_COMPLETE_PROFILER].toString() == "") {
                            stringResource(id = R.string.text_complete_your_profile)
                        } else {
                            languageData[LanguageTranslationsResponse.KEY_COMPLETE_PROFILER].toString()
                        },
                        progress = (parentProfilePercentage.value?.totalProgress ?: 50).toFloat(),
                        parentProfilePercentage.value
                    )
                }                // Add first image
                item {
                    val student =/*stringResource(id = R.string.student)*/
                        languageData[LanguageTranslationsResponse.STUDENTS].toString() + " (" + studentData.size.toString() + "/" + 5 + ") "
                    Text(
                        text = student,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_bold, FontWeight.Bold)
                        ),
                        modifier = Modifier.padding(start = 8.dp, top = 15.dp)
                    )
                }

                items(studentData) { item ->
                    if (item != null) {
                        ItemRow(item = item, onClick = {
                                 navController.navigate(AppRoute.QuizPerformance("manju",4,
                                     sharedPref?.getUserId()
                                 ))
                        })
                    }
                }

                // Add second image
                item {
                    if (studentData.size == 5) {
                        isVisible = false
                        viewModel.saveNumberOfStudent(studentData.size)
                        InviteUi(navController, isVisible)
                    } else {
                        InviteUi(navController, isVisible)
                    }
                }
            }
        }
    }
}

@Composable
fun ItemRow(item: StudentInformetionResponseModel.Data.Student, onClick: () -> Unit) {

    val loginViewModel: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
        colors = CardColors(
            containerColor = PrimaryBlue,
            contentColor = PrimaryBlue,
            disabledContentColor = PrimaryBlue,
            disabledContainerColor = PrimaryBlue
        ),
        border = BorderStroke(width = 0.5.dp, GrayLight02),
    ) {
        Column {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardColors(
                    containerColor = White,
                    contentColor = White,
                    disabledContentColor = White,
                    disabledContainerColor = White
                ),
                border = BorderStroke(width = 0.5.dp, GrayLight02),
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        println("Parent Profile Image url :- ${item.imageUrl}")

                        if (item.imageUrl != null && item.gender != null) {
                            CustomCircularImageViewWithBorder(
                                gender = item.gender,
                                imageRes = item.imageUrl,
                                borderColor = GrayLight02,
                                borderWidth = 1f
                            )
                        } else {
                            println("Parent Profile Image url not found")
                        }
                        Column(
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                buildAnnotatedString {
                                    withStyle(style = SpanStyle(color = Color.Black)) {
                                        append(item.name)
                                    }
                                }, fontFamily = FontFamily(
                                    Font(R.font.inter_bold, FontWeight.Bold)
                                )
                            )

                            Text(
                                text = if (languageData[LanguageTranslationsResponse.KEY_CLASS].toString() == "") {
                                    "Class " + item.grade
                                } else {
                                    languageData[LanguageTranslationsResponse.KEY_CLASS].toString() + " " + item.grade
                                },
                                fontFamily = FontFamily(
                                    Font(R.font.inter_medium, FontWeight.Medium)
                                ), fontSize = 12.sp, color = Gray
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            painter = painterResource(R.drawable.ic_right_side),
                            contentDescription = stringResource(id = R.string.icon_description),
                            modifier = Modifier
                                .size(25.dp)
                                .background(Color.Unspecified)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(PrimaryBlueLt)
                    ) {
                        val modifier: Modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                        ScoreTextView(
                            score = item.totalQuizCount,
//                            heading = "Total quizzes \nplayed",
                            heading = languageData[LanguageTranslationsResponse.TOTAL_QUIZZES_PLAYED].toString(),
                            modifier = modifier
                        )
                        ScoreTextView(
                            score = item.winningQuizCount,
//                            heading = "Scholarship \nwon",
                            heading = languageData[LanguageTranslationsResponse.SCHOLARSHIP_WON].toString(),
                            modifier = modifier
                        )
                        ScoreTextView(
                            score = item.pendingQuizCount,
//                            heading = "Scholarship \npending",
                            heading = languageData[LanguageTranslationsResponse.SCHOLARSHIP_PENDING].toString(),
                            modifier = modifier
                        )

                        val num = item.avgScore.toFloat()
                        val df = DecimalFormat("#.##")
                        df.roundingMode = RoundingMode.CEILING
                        val floatValue = df.format(num)
                        println("Number format data :- $floatValue")
                        ScoreTextView(
                            score = floatValue.toString(),
//                            heading = "Average \nscore",
                            heading = languageData[LanguageTranslationsResponse.AVERAGE_SCORE].toString(),
                            modifier = modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreTextView(
    score: String = "544", heading: String = "Total quizzes \nplayed", modifier: Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = score,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontFamily = FontFamily(
                Font(R.font.inter_bold, FontWeight.Bold)
            ),
        )
        Text(
            text = heading,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp
        )
    }
}

/*@Composable
fun CustomCircularImageViewWithBorder(
    imageRes: Int,
    borderColor: Color,
    borderWidth: Float,
    modifier: Modifier = Modifier // Allow passing additional modifiers
) {
    Box(
        modifier = modifier
            .size(60.dp) // Set the desired size for the image view
            .border(
                width = borderWidth.dp,
                color = borderColor,
                shape = CircleShape
            ) // Add circular border
            .clip(CircleShape) // Clip to a circle
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null, // Provide a meaningful description if needed
            modifier = Modifier.fillMaxSize() // Fill the entire box
        )
    }
}*/


@Composable
fun CustomCircularImageViewWithBorder(
    gender: String,
    imageRes: Any, // Can be either a URL or a drawable resource ID
    borderColor: Color,
    borderWidth: Float,
    modifier: Modifier = Modifier, // Allow passing additional modifiers
) {
    Box(
        modifier = modifier
            .size(60.dp) // Set the desired size for the image view
            .border(
                width = borderWidth.dp, color = borderColor, shape = CircleShape
            ) // Add circular border
            .clip(CircleShape) // Clip to a circle
    ) {
        Image(
            painter = if (imageRes != null) {
                rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(imageRes).placeholder(
                        if (gender != null) {
                            getGenderIconState(gender)
                        } else {
                            R.drawable.icon_male_student
                        }
                    ).error(
                        if (gender != null) {
                            getGenderIconState(gender)
                        } else {
                            R.drawable.icon_male_student
                        }
                    ).crossfade(true) // Optional: Add a fade transition
                        .build()
                )
            } else {
                painterResource(
                    if (gender != null) {
                        getGenderIconState(gender)
                    } else {
                        R.drawable.icon_male_student
                    }
                )
            },
            contentDescription = null, // Provide a meaningful description if needed
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Unspecified),
            contentScale = ContentScale.Crop // Crop to fill the circle
        )
    }
}

@Composable
private fun ProfileProgress(
    navController: NavHostController,
    selectedBorder: BorderStroke,
    profileProgress: String,
    completeYourProfile: String,
    progress: Float = 0f,
    parentProgressResponse: GetPerentProgressResponseModel.Data?,
) {

    val context = LocalContext.current
//    val navController = rememberNavController()

    val loginViewModel: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    var totalProgress by remember { mutableIntStateOf(0) }
    var kycProgress by remember { mutableIntStateOf(0) }
    var profilerProgress by remember { mutableIntStateOf(0) }
    var completionFraction by remember { mutableStateOf("") }

    if (parentProgressResponse != null) {
        completionFraction = parentProgressResponse.completionFraction.toString()
        totalProgress = parentProgressResponse.totalProgress
        kycProgress = parentProgressResponse.kycProgress
        profilerProgress = parentProgressResponse.profilerProgress
    } else {
        completionFraction = "0"
        totalProgress = 0
        kycProgress = 0
        profilerProgress = 0
    }


    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)
            .wrapContentHeight(),
        colors = CardColors(
            containerColor = White,
            contentColor = White,
            disabledContentColor = White,
            disabledContainerColor = White
        ),
        border = selectedBorder,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .clickable {
                    try {
                        if (totalProgress < 100) {
                            if (profilerProgress < 70) {
                                navController.navigate(AppRoute.ParentEditProfile.route)
                            } else if (kycProgress < 30) {
                                navController.navigate(AppRoute.ParentAuthentication.route)
                            }
                        } else {
                            Toast
                                .makeText(
                                    context,
                                    "Profile progress already completed successfully",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    } catch (exp: Exception) {
                        exp.message
                        println("Progress parent profile error :- ${exp.message}")
                    }
                }, verticalAlignment = Alignment.CenterVertically
        ) {
            // Update this to reflect your progress
            CustomProgressBar(percentage = progress, modifier = Modifier.size(50.dp))
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Black)) {
                            append(profileProgress)
                        }
                        withStyle(style = SpanStyle(color = PrimaryBlue)) {
                            append(" ($completionFraction)")
                        }
                    }, fontFamily = FontFamily(
                        Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                    ),
                    textAlign = TextAlign.Start
                )

                Text(
                    text = if (completionFraction.equals("0/2")) {
                        completeYourProfile
                    } else if (completionFraction.equals("1/2")) {
                        "Complete your student's authentication process"
                    } else {
                        "Complete Profile for 100% profile Update."
                    },
                    fontFamily = FontFamily(
                        Font(R.font.inter_regular, FontWeight.Normal)
                    ),
                    fontSize = 12.sp,
                    color = Gray
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            if (totalProgress == 100) {
                println("Profile complete already...")
            } else {
                Image(painter = painterResource(R.drawable.ic_right_side),
                    contentDescription = stringResource(id = R.string.icon_description),
                    modifier = Modifier
                        .size(25.dp)
                        .background(Color.Unspecified)
                        .clickable {
                            try {
                                if (totalProgress < 100) {
                                    if (profilerProgress < 70) {
                                        navController.navigate(AppRoute.ParentEditProfile.route)
                                    } else if (kycProgress < 30) {
                                        navController.navigate(AppRoute.ParentAuthentication.route)
                                    }
                                } else {
                                    Toast
                                        .makeText(
                                            context,
                                            "Profile progress already completed successfully",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            } catch (exp: Exception) {
                                exp.message
                                println("Progress parent profile error :- ${exp.message}")
                            }
                        })
            }
        }
    }
}

@Composable
fun InviteUi(navController: NavHostController, isVisible: Boolean) {

    val context: Context = LocalContext.current
    val loginViewModel: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = if (isVisible) {
            Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
        } else {
            Modifier.alpha(0f)
        },
        colors = CardColors(
            containerColor = PrimaryBlue,
            contentColor = PrimaryBlue,
            disabledContentColor = PrimaryBlue,
            disabledContainerColor = PrimaryBlue
        ),
        border = BorderStroke(width = 0.5.dp, GrayLight02),
    ) {
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .weight(1f),
            ) {
                Text(
//                    text = stringResource(id = R.string.addStudent),
                    text = languageData[LanguageTranslationsResponse.ADD_STUDENT].toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start
                    )
                )

                Text(
                    modifier = Modifier.padding(end = 10.dp),
//                    text = stringResource(id = R.string.txt_enroll),
                    text = languageData[LanguageTranslationsResponse.ENROLL_YOUR_CHILD].toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start
                    )
                )

                Button(
                    onClick = {
                        val intent = Intent(context, LoginMainActivity::class.java)
                        intent.putExtra(CommandAppKey.screenName, onboarding1)
                        context.startActivity(intent)
                        loginViewModel.setAddStudentFromParentDashboard("ParentDashboard")
                    }, modifier = Modifier
                        .wrapContentSize()
                        .padding(top = 5.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp
                            )
                        ), colors = ButtonDefaults.buttonColors(
                        containerColor = White, contentColor = PrimaryBlue
                    )
                ) {
                    Text(
//                        text = stringResource(id = R.string.addStudentPlus),
                        text = "+ " + languageData[LanguageTranslationsResponse.ADD_STUDENT].toString(),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
            Image(
                painter = painterResource(id = R.drawable.add_child_girl_icon),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .wrapContentHeight()
                    .background(Color.Unspecified),
                alignment = Alignment.TopEnd
            )
        }
    }
}

package com.auro.application.ui.features.login.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.auro.application.App
import com.auro.application.R
import com.auro.application.core.ConstantVariables.isLogout
import com.auro.application.core.ConstantVariables.isRegistration
import com.auro.application.core.extions.toast
import com.auro.application.data.api.Constants
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.BottomSheetAlert
import com.auro.application.ui.common_ui.BtnNextUi
import com.auro.application.ui.common_ui.RectangleCheckbox
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.components.CreatePinDoubleBackPressHandler
import com.auro.application.ui.features.login.LoginMainActivity
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.features.login.models.GetSubjectPrefrenceSaveRequestModel
import com.auro.application.ui.features.login.models.GetSubjectPrefrenceSaveRequestModel.Subject
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.model.GradeWiseSubjectResponseModel
import com.auro.application.ui.features.parent.viewmodel.ParentViewModel
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue

@Composable
fun SelectSubjectScreen(
    navHostController: NavHostController,
    args: String?,
    viewModel: LoginViewModel,
    sharedPref: SharedPref?
) {
    val parentViewModel: ParentViewModel = hiltViewModel()
    val context = LocalContext.current

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    val subject = remember { mutableStateListOf<GradeWiseSubjectResponseModel.Data>() }
    Constants.token = sharedPref!!.getToken()
    // Clear response when the screen is disposed

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }

    LaunchedEffect(Unit) {
        parentViewModel.gradeWiseSubjectResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    subject.clear()
                    it.data?.data?.let { it1 -> subject.addAll(it1) }
                }

                is NetworkStatus.Error -> {}
            }
        }

        viewModel.getSubjectPrefrenceSaveResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    sharedPref?.saveLogin(true)
                    navHostController.popBackStack()
                    context.startActivity(Intent(
                        context, StudentDashboardActivity::class.java
                    ).apply {
                        putExtra(isRegistration, isRegistration)
                    })
//                    navHostController.navigate(AppRoute.ChildList(viewModel.getUserId(), "1"))
                }

                is NetworkStatus.Error -> {}
            }
        }

        val clazz = args ?: viewModel.getGrade()
        parentViewModel.getGradeWiseSubject(clazz!!.toInt())
//        clazz?.let {
//            viewModel.getSubjectList(it.toInt())
//        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }

    val selectedItems = remember { mutableStateListOf<String>() }

    CreatePinDoubleBackPressHandler(onExit = {
        viewModel.saveScreenName(isLogout)
        viewModel.clearPreferenceData(context)
        context.startActivity(Intent(context, LoginMainActivity::class.java))
            .also {
                if (context is Activity) {
                    context.finish()
                }
            }
    })

    BackHandler {
        viewModel.saveScreenName(isLogout)
        viewModel.clearPreferenceData(context)
        context.startActivity(Intent(context, LoginMainActivity::class.java))
            .also {
                if (context is Activity) {
                    context.finish()
                }
            }
    }

//    isShowBackButton = false, because it is overlapping if switching user of 11 & 12 grade.
    StudentRegisterBackground(isShowBackButton = false, onBackButtonClick = {
//        navHostController.navigateUp()
//        navHostController.popBackStack()
        viewModel.saveScreenName(isLogout)
        viewModel.clearPreferenceData(context)
        context.startActivity(Intent(context, LoginMainActivity::class.java))
            .also {
                if (context is Activity) {
                    context.finish()
                }
            }
    }, content = {

        Text(
//            text = stringResource(id = R.string.select_your_subject),
            text = languageData[LanguageTranslationsResponse.SELECT_YOUR_SUBJECT].toString(),
            modifier = Modifier.padding(top = 10.dp, start = 15.dp, end = 15.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Text(
//            stringResource(id = R.string.select_preferences_for_your_quiz_this_month),
            text = languageData[LanguageTranslationsResponse.SELECT__PREFERENCES].toString(),
            modifier = Modifier.padding(top = 0.dp, start = 15.dp, end = 15.dp, bottom = 10.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Thin,
            fontSize = 14.sp
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // context
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 65.dp)
                ) {
                    SubjectList(list = subject.map { it.subjectName },
                        selectedItems = selectedItems,
                        onItemClick = { item ->
                            if (selectedItems.contains(item)) {
                                selectedItems.remove(item)
                            } else {
                                if (selectedItems.size < 5) {
                                    selectedItems.add(item)
                                } else {
                                    showBottomSheet = !showBottomSheet
                                }
                            }
                        })
                }
            }

            if (showBottomSheet) {
                BottomSheetAlert(
                    showBottomSheet,
                    onHide = {
                        showBottomSheet = false
                    }, /*stringResource(R.string.okay_got_it)*/
                    languageData[LanguageTranslationsResponse.OKAY_GOT_IT].toString()
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
//                            stringResource(R.string.do_not_select_more_than_five_subjects),
                            text = languageData[LanguageTranslationsResponse.KEY_DO_NOT_SELECT_SUBJECT].toString(),
                            color = Black,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
//                            stringResource(R.string.do_not_select_more_than_five_subjects),
                            text = languageData[LanguageTranslationsResponse.KEY_DO_NOT_SELECT_SUBJECT].toString(),
                            modifier = Modifier.padding(10.dp),
                            color = PrimaryBlue,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.BottomCenter)
                    .background(color = Color.White)
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(0.dp)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row {
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
                    ) {
                        Text(
                            selectedItems.size.toString() + "/5",
                            color = PrimaryBlue,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
//                        Text(stringResource(R.string.subject_selected))
                        Text(text = languageData[LanguageTranslationsResponse.KEY_SUBJECT_SELECTED].toString())
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    BtnNextUi(
                        onClick = {
                            Log.d("selectedItems:",""+selectedItems.size)
                            if (selectedItems.size < 5) {
                                context.toast("Please select at least 5 subject.")
                            } else {
                                var tempSubject = mutableStateListOf<Subject>()
                                tempSubject.clear()
                                sharedPref?.getUserId()?.let { userId ->
                                    subject.forEachIndexed { index, data ->
                                        if (selectedItems.contains(data.subjectName)) {
                                            tempSubject.add(Subject(data.subjectId))
                                        }
                                    }
                                    if (tempSubject.isNotEmpty()) {
                                        Log.d("", "" + tempSubject)
                                        viewModel.getSubjectPreferenceSave(
                                            GetSubjectPrefrenceSaveRequestModel(
                                                userId, tempSubject.toList()
                                            )
                                        )
                                    }
                                }
                            }
                        }, /*title = stringResource(R.string.continues)*/
                        title = languageData[LanguageTranslationsResponse.CONT].toString(),
                        enabled = true
                    )
                }
            }
        }
    })
}

@Composable
private fun moveToStudentDashboard(context: Context) {
    val intent = Intent(context, StudentDashboardActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    intent.also {
        it.putExtra("", true)
    }
    context.startActivity(intent)
    (context as ComponentActivity).finishAffinity()
}

@Composable
fun SubjectList(
    list: List<String>, selectedItems: SnapshotStateList<String>, onItemClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(list) { child ->
            SubjectItem(child = child,
                isSelected = selectedItems.contains(child),
                onClick = { onItemClick(child) })
        }
    }
}

@Composable
fun SubjectItem(
    child: String, isSelected: Boolean, onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .clickable {
                onClick.invoke()
            }
            .padding(10.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, GrayLight02),
    ) {
        Row(modifier = Modifier
            .clickable {
                onClick.invoke()
            }
            .fillMaxWidth()
            .background(
                if (isSelected) Color.White else Color.White
            ) // Set background color based on selection
            .padding(4.dp), // Add horizontal padding
            verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier
                    .padding(10.dp)
                    .size(52.dp)
                    .background(Color.Unspecified),
                painter = painterResource(R.drawable.ic_parent),
                contentDescription = "Logo"
            )

            Text(
                text = child, modifier = Modifier.weight(0.1f)
            )

            RectangleCheckbox(selected = isSelected, enabled = true, onChecked = {
                onClick.invoke()
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectSubjectScreenPreview() {
    AuroscholarAppTheme {
        SelectSubjectScreen(
            navHostController = rememberNavController(),
            args = null,
            viewModel = hiltViewModel(),
            sharedPref = null
        )
    }
}
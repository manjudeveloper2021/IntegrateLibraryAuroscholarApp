package com.auro.application.ui.features.student.screens

import android.app.Activity
import android.content.Intent
import android.util.Log
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
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.Constants
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.BottomSheetAlert
import com.auro.application.ui.common_ui.BtnNextUi
import com.auro.application.ui.common_ui.RectangleCheckbox
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.features.login.componets.StudentRegisterBackground
import com.auro.application.ui.features.login.models.GetSubjectListResponseModel
import com.auro.application.ui.features.login.models.GetSubjectPrefrenceSaveRequestModel
import com.auro.application.ui.features.login.models.GetSubjectPrefrenceSaveRequestModel.Subject
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.viewmodel.ParentViewModel
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Bg_Gray1
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White

@Composable
fun StudentSubjectPreferenceScreen(
    navHostController: NavHostController,
    args: String?,
    viewModel: LoginViewModel,
    sharedPref: SharedPref?
) {
    val parentViewModel: ParentViewModel = hiltViewModel()
    val studentViewModel: StudentViewModel = hiltViewModel()
    val context = LocalContext.current
    var isDialogVisible by remember { mutableStateOf(false) }
    var isSubjPref by remember { mutableStateOf(false) }

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

//    val subject = remember { mutableStateListOf<GradeWiseSubjectResponseModel.Data>() }
    val subjectData = remember { mutableStateListOf<GetSubjectListResponseModel.Data>() }
    Constants.token = sharedPref!!.getToken()
    // Clear response when the screen is disposed
//    subjectData.clear()
    CustomDialog(
        isVisible = isDialogVisible, onDismiss = { isDialogVisible = false },
//        message = "Loading your data..."
        message = msgLoader
    )

    LaunchedEffect(Unit) {
        /* parentViewModel.gradeWiseSubjectResponseModel.observeForever {
             when (it) {
                 is NetworkStatus.Idle -> {}
                 is NetworkStatus.Loading -> {}
                 is NetworkStatus.Success -> {
                     subject.clear()
                     it.data?.data?.let { it1 -> subject.addAll(it1) }
                 }

                 is NetworkStatus.Error -> {}
             }
         }*/
        studentViewModel.getSubjectListResponseModel.observeForever {
            subjectData.clear()
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }
                is NetworkStatus.Success -> {
//                    isSubjPref = true
                    if (it.data?.isSuccess == true) {
                        isDialogVisible = false
//                        subjectData.addAll(it.data.data)
                        it.data.data.let { it1 -> subjectData.addAll(it1) }
//                        subjectData = it.data.data.toMutableList()
                        Log.d("subjectData:", "" + subjectData)
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
//                    context.toast(it.message)
                    println("Subject List Data Error :-${it.message}")
                }
            }
        }

        viewModel.getSubjectPrefrenceSaveResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {
                    if (it.data!!.data == "Please select 5 subjects") {
                        context.toast(it.data.data)
                    } else {
                        navHostController.popBackStack()
                        context.startActivity(Intent(
                            context, StudentDashboardActivity::class.java
                        ).also {
                            if (context is Activity) {
                                context.finish()
                            }
                        })
                    }
                }

                is NetworkStatus.Error -> {}
            }
        }

        /* val clazz = args ?: viewModel.getGrade()
         parentViewModel.getGradeWiseSubject(clazz!!.toInt())*/
//        clazz?.let {
//            viewModel.getSubjectList(it.toInt())
//        }
        isDialogVisible = true
        studentViewModel.getSubjectList()
    }

    var showBottomSheet by remember { mutableStateOf(false) }

    val selectedItems = remember { mutableStateListOf<String>() }

    BackHandler {
        navHostController.navigateUp()
//        navHostController.popBackStack()
    }

//    isShowBackButton = false, because it is overlapping if switching user of 11 & 12 grade.
    StudentRegisterBackground(isShowBackButton = false, onBackButtonClick = {
        navHostController.navigateUp()
//        navHostController.popBackStack()

    }, content = {

        Text(
//            text = stringResource(id = R.string.select_your_subject),
            text = if (languageData[LanguageTranslationsResponse.SELECT_YOUR_SUBJECT].toString() == "") {
                stringResource(id = R.string.select_your_subject)
            } else {
                languageData[LanguageTranslationsResponse.SELECT_YOUR_SUBJECT].toString()
            },
            modifier = Modifier.padding(top = 10.dp, start = 15.dp, end = 15.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Text(
//            stringResource(id = R.string.select_preferences_for_your_quiz_this_month),
            text = if (languageData[LanguageTranslationsResponse.SELECT__PREFERENCES].toString() == "") {
                stringResource(id = R.string.select_preferences_for_your_quiz_this_month)
            } else {
                languageData[LanguageTranslationsResponse.SELECT__PREFERENCES].toString()
            },
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
                    Log.d("item.isSelected", "" + selectedItems.size)
                    var isSelectedChanged by remember { mutableStateOf(false) }
                    if (!isSelectedChanged) { // adding already selected item to list
                        if (selectedItems.size == 0) {
                            for (data in subjectData) {
                                if (data.isSelected) selectedItems.add(data.subjectName)
                            }
                        }
                    }
                    SubjectList(list = subjectData.map { it.subjectName },
                        subjectData = subjectData,
                        selectedItems = selectedItems,
                        onItemClick = { item ->
//                            val isSelected = item.isDisabled == 1 || item.isSelected
                            isSelectedChanged = true // checked value change
                            if (selectedItems.contains(item.subjectName)) {
                                if (item.isDisabled == 0) {
                                    item.isSelected = false
                                    selectedItems.remove(item.subjectName)
                                }
                            } else {
                                if (selectedItems.size < 5) {
                                    selectedItems.add(item.subjectName)
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
                            alignment = Alignment.Center
                        )

                        Text(
//                            stringResource(R.string.do_not_select_more_than_five_subjects),
                            text = if (languageData[LanguageTranslationsResponse.KEY_DO_NOT_SELECT_SUBJECT].toString() == "") {
                                stringResource(R.string.do_not_select_more_than_five_subjects)
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_DO_NOT_SELECT_SUBJECT].toString()
                            },
                            color = Black,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
//                            stringResource(R.string.do_not_select_more_than_five_subjects),
                            text = if (languageData[LanguageTranslationsResponse.KEY_DO_NOT_SELECT_SUBJECT].toString() == "") {
                                stringResource(R.string.do_not_select_more_than_five_subjects)
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_DO_NOT_SELECT_SUBJECT].toString()
                            },
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
                        Text(
                            text = if (languageData[LanguageTranslationsResponse.KEY_SUBJECT_SELECTED].toString() == "") {
                                stringResource(R.string.subject_selected)
                            } else {
                                languageData[LanguageTranslationsResponse.KEY_SUBJECT_SELECTED].toString()
                            }
                        )
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
                                    subjectData.forEachIndexed { index, data ->
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
                        },
                        title = /*stringResource(R.string.continues)*/ languageData[LanguageTranslationsResponse.CONT].toString(),
                        enabled = true
                    )
                }
            }
        }
    })
}

@Composable
fun SubjectList(
    list: List<String>,
    subjectData: MutableList<GetSubjectListResponseModel.Data>,
    selectedItems: SnapshotStateList<String>,
    onItemClick: (GetSubjectListResponseModel.Data) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(subjectData) { child ->
            val isSelected = selectedItems.contains(child.subjectName) || child.isSelected
            SubjectItem(child = child,
                subjectData = subjectData,
                isSelected = isSelected,
                onClick = { onItemClick(child) })
        }
    }
}

@Composable
fun SubjectItem(
    child: GetSubjectListResponseModel.Data,
    subjectData: MutableList<GetSubjectListResponseModel.Data>,
    isSelected: Boolean,
    onClick: () -> Unit
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
                if (child.isDisabled == 1) Bg_Gray1 else White
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
                text = child.subjectName, modifier = Modifier.weight(0.1f)
            )

            RectangleCheckbox(selected = isSelected, enabled = true, onChecked = {
                if (child.isDisabled == 0) onClick.invoke()
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectSubjectScreenPreview() {
    AuroscholarAppTheme {
        StudentSubjectPreferenceScreen(
            navHostController = rememberNavController(),
            args = null,
            viewModel = hiltViewModel(),
            sharedPref = null
        )
    }
}
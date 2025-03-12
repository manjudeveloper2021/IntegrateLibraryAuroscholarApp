package com.auro.application.ui.features.student.assessment.screens

import android.content.Context
import android.graphics.Color.alpha
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.enableLiveLiterals
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.fontResource
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.transition.Visibility
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.data.utlis.CommonFunction.currentDateDisplay
import com.auro.application.ui.common_ui.BottomSheetAlert
import com.auro.application.ui.common_ui.MobileTextField
import com.auro.application.ui.common_ui.RectangleBtnUi
import com.auro.application.ui.common_ui.RectangleCheckbox
import com.auro.application.ui.common_ui.RectangleQuizCheckbox
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.components.WalkThroughModel
import com.auro.application.ui.common_ui.components.WalkthroughDialog
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.GetSubjectListResponseModel
import com.auro.application.ui.features.login.models.GetSubjectPrefrenceSaveRequestModel.Subject
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.assessment.model.AssessmentConceptsRequestModel
import com.auro.application.ui.features.student.assessment.model.AssessmentConceptsResponseModel
import com.auro.application.ui.features.student.assessment.model.AssessmentSaveConceptRequestModel
import com.auro.application.ui.features.student.assessment.model.AssessmentSaveConceptRequestModel.AssessmentSaveConcept
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.BackgroundGray
import com.auro.application.ui.theme.Bg_Gray1
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.HashMap

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectAssessmentConceptList(
    navHostController: NavHostController,
    sharedPref: SharedPref
) {
    val context: Context = LocalContext.current
    val viewModel: StudentViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
    var currentMonth = currentDateDisplay(false)
    val selectedItems = remember { mutableStateListOf<String>() }
    val selectedConceptList = remember { mutableStateListOf<AssessmentSaveConcept>() }
    var selectedConceptSize by remember { mutableStateOf(0) }
    var isDialogVisible by remember { mutableStateOf(false) }
    var clickEnabled by remember { mutableStateOf(false) }
    var selectedConceptNo by remember { mutableStateOf(0) }

    var showBottomSheet by remember { mutableStateOf(false) }
    var conceptList by remember { mutableStateOf(mutableListOf<AssessmentConceptsResponseModel.AssessmentConcept>()) }
    /*   viewModel.saveScreenName(addNewChild)
       val userId = viewModel.getUserId().toString()
       val parent = viewModel.getParentInfo()*/
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)
    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()
    val scope = rememberCoroutineScope()
    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = msgLoader
    )

    var isShowWalkthrough by remember { mutableStateOf(false) }
    val list = listOf<WalkThroughModel>(
        WalkThroughModel(
            "Subject Selection Simplified",
            if (languageData[LanguageTranslationsResponse.WELCOME_IN_ELV_TH_AND_TW_TH_GRADE].toString() == "") {
                "Welcome! In 11th and 12th Grade? Pick your quiz subjects. For other grades, subjects are auto-selected."
            } else {
                languageData[LanguageTranslationsResponse.WELCOME_IN_ELV_TH_AND_TW_TH_GRADE].toString()
            },
            R.drawable.select_your_subject
        ),
        WalkThroughModel(
            "Explore Fresh Concepts Monthly",
            if (languageData[LanguageTranslationsResponse.CHOOSE_FOUR_NEW_CONCEPTS_EACH_MONTH].toString() == "") {
                "Choose 4 new concepts each month from 48 options. These concepts cannot be repeated in a year!"
            } else {
                languageData[LanguageTranslationsResponse.CHOOSE_FOUR_NEW_CONCEPTS_EACH_MONTH].toString()
            },
            R.drawable.select_concepts
        ),
        WalkThroughModel(
            "Pick Smart, Win More!",
            if (languageData[LanguageTranslationsResponse.CHANGE_SUBJECTS_AND_CONCEPTS_MONTHLY].toString() == "") {
                "Change subjects and concepts monthly if you haven't played quizzes. For each quiz, you get 2 extra tries and 3 practices."
            } else {
                languageData[LanguageTranslationsResponse.CHANGE_SUBJECTS_AND_CONCEPTS_MONTHLY].toString()
            },
            R.drawable.other_subject
        ),
        WalkThroughModel(
            "Play, Learn, and Win!",
            if (languageData[LanguageTranslationsResponse.READY].toString() == "") {
                "Ready? Choose concepts, start quizzes, and win! Keep playing, and have fun learning!"
            } else {
                languageData[LanguageTranslationsResponse.READY].toString()
            },
            R.drawable.add_more_concept
        )
    )
    if (isShowWalkthrough) {
        WalkthroughDialog(showDialog = true, onDismissRequest = {
            isShowWalkthrough = false
        }, list)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }
    BackHandler {
        navHostController.popBackStack()
        navHostController.navigate(AppRoute.StudentDashboard.route)
    }

    LaunchedEffect(Unit) {
        viewModel.getStudentConceptResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {}
                is NetworkStatus.Success -> {

                    if (it.data?.isSuccess == true) {
                        conceptList = it.data.data.toMutableList()
                    }
                }

                is NetworkStatus.Error -> {
                    context.toast(it.message)
                }
            }
        }

        viewModel.getSaveConceptResponse.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        //                        context.toast(it.data.data)
                        navHostController.popBackStack()
                        navHostController.navigate(AppRoute.StudentQuizList.route)
                    } else {
                        context.toast(it.data!!.data)
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                }
            }
        }

        Log.d("CurrentMonth:",""+currentMonth)
        viewModel.getAssessmentConceptData(
            AssessmentConceptsRequestModel(
                loginViewModel.getStudentSelectedSubjectData().subjectId,
                loginViewModel.getGrade()!!.toInt(),
                currentMonth,
                loginViewModel.getLanguageId().toInt()
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Surface(
                tonalElevation = 10.dp, // Set the elevation here
                color = White,
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.back_icon),
                        contentDescription = "logo",
                        modifier = Modifier
                            .clickable {
                                navHostController.popBackStack()
                                navHostController.navigate(AppRoute.StudentDashboard.route)
                            },

                        colorFilter = ColorFilter.tint(Black)
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = 25.dp, end = 20.dp)
                            .weight(1f)
                            .wrapContentSize(),
                    ) {
                        Text(
                            text = loginViewModel.getStudentSelectedSubjectData().subjectName,
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontFamily = FontFamily(
                                Font(R.font.inter_bold, FontWeight.Bold)
                            ),
                            fontSize = 18.sp,
                            color = Black,
                            textAlign = TextAlign.Start
                        )

                        selectedConceptNo = selectedConceptList.size
                        Text(
                            text = selectedItems.size.toString() + "/4 concepts selected",
                            modifier = Modifier
                                .wrapContentSize(),
                            fontFamily = FontFamily(
                                Font(R.font.inter_medium, FontWeight.Medium)
                            ),
                            fontSize = 12.sp,
                            color = Gray,
                            textAlign = TextAlign.Start
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.dash_help),
                        contentDescription = "logo",
                        modifier = Modifier
                            .clickable {
                                isShowWalkthrough = true
                            },

                        colorFilter = ColorFilter.tint(Black)
                    )
                }
            }

            /* Box(
                 modifier = Modifier
                     .fillMaxWidth()
                     .height(2.dp)
                     .padding(top = 20.dp, bottom = 20.dp)
             ) {
                 Canvas(modifier = Modifier
                     .fillMaxWidth()
                     .height(2.dp)) {
                     drawLine(
                         color = GrayLight02,
                         start = Offset(0f, 0f),
                         end = Offset(size.width, 0f),
                         strokeWidth = 1.5.dp.toPx()
                     )
                 }
             }*/
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .padding(top = 20.dp, bottom = 20.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            ) {
                drawLine(
                    color = GrayLight02,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .weight(1f)
                .fillMaxSize()
                .background(White)
        ) {
            Text(
                text = if (conceptList.isNotEmpty()) {
                    "Play up to 4 Quiz each month. Choose 4 concepts. The selected concepts cant be played for the rest of the academic year."
                } else {
                    "No Concept Found!"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                fontFamily = FontFamily(
                    Font(R.font.inter_regular, FontWeight.Normal)
                ),
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Start
            )

            var isSelectedChanged by remember { mutableStateOf(false) }
            if (!isSelectedChanged) { // adding already selected item to list
                if (selectedItems.size == 0) {
                    for (data in conceptList) {
                        if (data.isSelected == "1" || data.isSelected == "2") {
                            selectedConceptList.add(
                                AssessmentSaveConcept(
                                    data.ID!!, data.Name!!
                                )
                            )
                            selectedItems.add(data.Name.toString())
                        }
                    }
                }
            }
            SubjectList(
                list = conceptList.map { it.Name },
                subjectData = conceptList,
                selectedItems = selectedItems,
                onItemClick = { item ->
                    //                            val isSelected = item.isDisabled == 1 || item.isSelected
                    isSelectedChanged = true // checked value change
                    if (selectedItems.contains(item.Name)) {
                        if (item.isSelected != "2") {
                            item.isSelected = "0"

                            selectedConceptList.remove(
                                AssessmentSaveConcept(
                                    item.ID!!, item.Name!!
                                )
                            )
                            selectedItems.remove(item.Name)
                        }
                    } else {
                        if (selectedItems.size < 4) {
                            selectedConceptList.add(
                                AssessmentSaveConcept(
                                    item.ID!!, item.Name!!
                                )
                            )
                            selectedItems.add(item.Name.toString())
                        } else {
                            showBottomSheet = !showBottomSheet
                        }
                    }
                })
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .padding(top = 5.dp, bottom = 20.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            ) {
                drawLine(
                    color = GrayLight02,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
        if (showBottomSheet) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(top = 20.dp, bottom = 32.dp)
            ) {
                //            if (showBottomSheet) {
                BottomSheetAlert(
                    showBottomSheet,
                    onHide = {
                        showBottomSheet = false
                    },
                    if (languageData[LanguageTranslationsResponse.OKAY_GOT_IT].toString() == "") {
                        stringResource(R.string.okay_got_it)
                    } else {
                        languageData[LanguageTranslationsResponse.OKAY_GOT_IT].toString()
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_alert),
                            contentDescription = "sahsga",
                            alignment = Alignment.Center
                        )
                        Text(
                            stringResource(R.string.do_not_select_more_than_four_subjects),
                            modifier = Modifier.padding(10.dp),
                            fontSize = 16.sp,
                            color = Black,
                            fontFamily = FontFamily(
                                Font(R.font.inter_bold, FontWeight.Bold)
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            stringResource(R.string.select_up_to_4_concept),
                            modifier = Modifier.padding(horizontal = 10.dp),
                            fontSize = 14.sp,
                            color = BackgroundGray,
                            fontFamily = FontFamily(
                                Font(R.font.inter_regular, FontWeight.Normal)
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

        }

        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .weight(1f)
                    .wrapContentSize(),
            ) {
                Text(
                    text = selectedItems.size.toString() + "/4",
                    modifier = Modifier
                        .fillMaxWidth(),
                    fontFamily = FontFamily(
                        Font(R.font.inter_bold, FontWeight.Bold)
                    ),
                    fontSize = 20.sp,
                    color = PrimaryBlue,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = "Concepts selected",
                    modifier = Modifier
                        .wrapContentSize(),
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    ),
                    fontSize = 12.sp,
                    color = Gray,
                    textAlign = TextAlign.Start
                )
            }
            if (!clickEnabled) {
                RectangleBtnUi(
                    onClick = {
                        clickEnabled = true
                        if (selectedConceptList.size == 0) {
                            Toast.makeText(
                                context,
                                "Please select your concept firstly.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Modifier.clickable { false }


                            isDialogVisible = true


                            loginViewModel.setSelectedConcept(selectedItems.size)
                            var tempSubject = mutableStateListOf<Subject>()
                            tempSubject.clear()
                            viewModel.saveAssessmentConceptData(
                                AssessmentSaveConceptRequestModel(
                                    loginViewModel.getStudentSelectedSubjectData().subjectId,
                                    loginViewModel.getGrade()!!.toInt(),
                                    currentMonth,
                                    loginViewModel.getLanguageId().toInt(), selectedConceptList
                                )
                            )


                        }


                    },
                    title = if (languageData[LanguageTranslationsResponse.CONT].toString() == "") {
                        stringResource(R.string.continues)
                    } else {
                        languageData[LanguageTranslationsResponse.CONT].toString()
                    },

                    enabled = (
                            if (selectedConceptList.size == 0) {
                                false
                            } else if (selectedConceptList.size > 0) {

                                true
                            } else if (selectedConceptList.size > 0 && clickEnabled) {

                                Modifier.alpha(0f)
                                false
                            } else {
                                clickEnabled = false
                                true
                            }
                            )

                )
            }
        }
    }
}

@Composable
fun SubjectList(
    list: List<String?>,
    subjectData: MutableList<AssessmentConceptsResponseModel.AssessmentConcept>,
    selectedItems: SnapshotStateList<String>,
    onItemClick: (AssessmentConceptsResponseModel.AssessmentConcept) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(subjectData) { child ->
            val isSelected =
                selectedItems.contains(child.Name) || child.isSelected == "1" || child.isSelected == "2"

            SubjectItem(
                child = child,
                subjectData = subjectData,
                isSelected = isSelected,
                onClick = { onItemClick(child) })
        }
    }
}

@Composable
fun SubjectItem(
    child: AssessmentConceptsResponseModel.AssessmentConcept,
    subjectData: MutableList<AssessmentConceptsResponseModel.AssessmentConcept>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .clickable {
                onClick.invoke()
            }
            .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
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
                if (child.isSelected == "2") Bg_Gray1 else White
            ) // Set background color based on selection
            .padding(4.dp), // Add horizontal padding
            verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier
                    .padding(8.dp)
                    .size(52.dp),
                painter = painterResource(R.drawable.ic_microscholarship),
                contentDescription = "Logo"
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = child.Name!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    fontFamily = FontFamily(
                        Font(R.font.inter_medium, FontWeight.Medium)
                    ),
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Left
                )

                Text(
                    text = "10 Questions",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp),
                    color = Color.Gray.copy(alpha = 0.6f), // Adjusting for GrayLight01
                    fontFamily = FontFamily(
                        Font(R.font.inter_regular, FontWeight.Normal)
                    ),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start
                )
            }

            RectangleCheckbox(selected = isSelected,
                enabled = true,
                onChecked = {
                    if (child.isSelected != "2")
                        onClick.invoke()
                })
        }
    }
}

@Composable
fun SelectConceptList(
    context: Context,
    selectedConceptSize: Int,
    conceptList: MutableList<AssessmentConceptsResponseModel.AssessmentConcept>,
    selectedItems: SnapshotStateList<String>,
    selectedConcept: MutableList<AssessmentSaveConcept>,
    onItemClick: (String) -> Unit
) {
    val scrollState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    selectedConcept.clear()
    LazyColumn(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(conceptList) { items ->
            ConceptItem(
                context,
                selectedConceptSize = selectedConceptSize,
                conceptItem = items,
                isSelected = selectedItems.contains(items.isSelected),
                selectedConceptList = selectedConcept,
                onClick = { onItemClick(items.isSelected!!) })
        }
    }
}

@Composable
fun ConceptItem(
    context: Context,
    selectedConceptSize: Int,
    conceptItem: AssessmentConceptsResponseModel.AssessmentConcept,
    isSelected: Boolean,
    selectedConceptList: MutableList<AssessmentSaveConcept>,
    onClick: () -> Unit// Add a parameter for the click action){}){}){}
) {

    Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
        if (conceptItem.isSelected == "1" || conceptItem.isSelected == "2") {
            selectedConceptList.add(
                AssessmentSaveConcept(
                    conceptItem.ID!!, conceptItem.Name!!
                )
            )
        }
        Column(
            modifier = Modifier
                .background(
                    color = if (conceptItem.isSelected == "2") {
                        Bg_Gray1
                    } else {
                        White
                    }
                )
                .border(
                    width = 1.dp,
                    color = GrayLight02,
                    shape = RoundedCornerShape(15)
                )
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(bottom = 5.dp, start = 2.dp, end = 0.dp)
                    .clickable(onClick = {

                    }) // Make the entire row clickable
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_microscholarship),
                    contentDescription = "leftIcon",
                    modifier = Modifier.size(65.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = conceptItem.Name!!,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Left
                    )

                    Text(
                        text = "10 Questions",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        color = Color.Gray.copy(alpha = 0.6f), // Adjusting for GrayLight01
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                }

                RectangleQuizCheckbox(
                    isDisabled = conceptItem.isSelected == "2", // disabled color
                    selected = conceptItem.isSelected == "1" || conceptItem.isSelected == "2",
                    enabled = true,
                    onChecked = {
                        //   1 == selected, 2 = selected but disabled , 3 = completed
                        if (conceptItem.isSelected == "2") {
                            context.toast("already completed!")
                        } else {
                            if (conceptItem.isSelected == "1") {
                                conceptItem.isSelected = "0"
                                !isSelected
                                selectedConceptList.remove(
                                    AssessmentSaveConcept(
                                        conceptItem.ID!!, conceptItem.Name!!
                                    )
                                )
                            } else if (conceptItem.isSelected == "0") {
                                conceptItem.isSelected = "1"
                                selectedConceptList.add(
                                    AssessmentSaveConcept(
                                        conceptItem.ID!!, conceptItem.Name!!
                                    )
                                )
                                onClick.invoke()
                            }
                        }
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ConceptListPreview() {
    AuroscholarAppTheme {
        var viewModel: StudentViewModel = hiltViewModel()
        lateinit var sharedPref: SharedPref
        SelectAssessmentConceptList(
            navHostController = rememberNavController(), sharedPref
        )
    }
}
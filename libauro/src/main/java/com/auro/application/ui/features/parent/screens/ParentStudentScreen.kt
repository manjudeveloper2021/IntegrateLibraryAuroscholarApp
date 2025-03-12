package com.auro.application.ui.features.parent.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.core.ConstantVariables.onboarding1
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.features.login.LoginMainActivity
import com.auro.application.ui.features.login.componets.CommandAppKey
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.model.StudentInformetionResponseModel
import com.auro.application.ui.features.parent.viewmodel.ParentViewModel
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White


@Preview(showSystemUi = true)
@Composable
fun ParentStudentScreen(
    navController: NavHostController = rememberNavController(),
    sharedPref: SharedPref? = null,
    title: (String) -> Unit = {},
    viewModel: ParentViewModel = hiltViewModel(),
) {
    val context: Context = LocalContext.current
    val loginViewModel: LoginViewModel = hiltViewModel()

    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    val msgLoader = languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

    LaunchedEffect(Unit) {
//        title.invoke("Student")
        title.invoke(languageData[LanguageTranslationsResponse.STUDENTS].toString())
    }
    BackHandler {
        navController.popBackStack()
        (context as? Activity)?.finish()
    }

    var isDialogVisible by remember { mutableStateOf(false) }

    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
//        message = "Loading your data..."
        message = msgLoader
    )

    val studentData =
        remember { mutableStateListOf<StudentInformetionResponseModel.Data.Student?>() }

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

                        loginViewModel.saveChildCount(it.data.data.student.size)
                        studentData.addAll(it.data.data.student)
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                }

                else -> {}
            }
        }
        viewModel.getParentStudentList()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .nestedScroll(rememberNestedScrollInteropConnection())
        ) {
            ItemListList(
                items = studentData
            ) { clickedItem ->
                Log.e(
                    "TAG",
                    "ParentStudentScreen: name ${clickedItem.name} class ${clickedItem.grade} subjectId ${clickedItem.userId}"
                )
//                context.toast("Currently Unavailable")
                /*navController.navigate(
                    AppRoute.QuizPerformance(
                        name = clickedItem.name,
                        grade = clickedItem.grade,
                        userId = clickedItem.userId
                    )
                )*/
            }
        }

        if (studentData.size == 5) {
            println("No more add students")
        } else {
            Button(
                onClick = {
                    val intent = Intent(context, LoginMainActivity::class.java)
                    intent.putExtra(CommandAppKey.screenName, onboarding1)
                    context.startActivity(intent)
                    loginViewModel.setAddStudentFromParentDashboard("ParentDashboard")
                }, modifier = Modifier
                    .align(Alignment.BottomEnd) // Align to the bottom end
                    .padding(16.dp) // Same padding as the FAB
                    .wrapContentSize()
                    .clip(
                        RoundedCornerShape(
                            topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp
                        )
                    ), colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue, contentColor = White
                )
            ) {
                Text(
//                    text = stringResource(id = R.string.addStudentPlus),
                    text = "+" + languageData[LanguageTranslationsResponse.ADD_STUDENT].toString(),
                    fontFamily = FontFamily(
                        Font(R.font.inter_bold, FontWeight.Bold)
                    ),
                    fontSize = 14.sp
                )
            }
        }
    }
}


@Composable
fun ItemListList(
    items: SnapshotStateList<StudentInformetionResponseModel.Data.Student?>,
    onItemClick: (StudentInformetionResponseModel.Data.Student) -> Unit,
) {
    LazyColumn {
        items(items) { item ->
            Item(item = item, onClick = {
                if (item != null) {
                    onItemClick(item)
                }
            })
        }
    }
}

@Composable
fun Item(item: StudentInformetionResponseModel.Data.Student?, onClick: () -> Unit) {

    val loginViewModel: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = loginViewModel.getLanguageTranslationData(languageListName)

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 5.dp)
            .fillMaxWidth(),
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
                if (item!!.gender != null && item.imageUrl != null) {
                    CustomCircularImageViewWithBorder(
                        item!!.gender,
                        imageRes = item.imageUrl.toString(),
                        borderColor = GrayLight02,
                        borderWidth = 4f,
                    )
                } else {
                    println("Gender and image url not found...")
                }
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Gray)) {
                                append(item.name)
                            }
                        },
                        fontFamily = FontFamily(
                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                        ), fontSize = 14.sp
                    )

                    Text(
                        text = if (languageData[LanguageTranslationsResponse.KEY_CLASS].toString() == "") {
                            "Class - " + item?.grade.toString()
                        } else {
                            languageData[LanguageTranslationsResponse.KEY_CLASS].toString() + " - " + item?.grade.toString()
                        },
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
                        ),
                        fontSize = 12.sp,
                        color = GrayLight01
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
        }

    }
}

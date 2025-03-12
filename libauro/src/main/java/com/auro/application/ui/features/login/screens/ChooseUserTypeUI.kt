package com.auro.application.ui.features.login.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.App
import com.auro.application.R
import com.auro.application.core.ConstantVariables.LOGIN
import com.auro.application.core.extions.toast
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.Constants.BASE_URL_LOCAL_TEACHER
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.utlis.CommonFunction
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.CircleCheckbox
import com.auro.application.ui.common_ui.DefaultBackgroundUi
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.LoginMainActivity
import com.auro.application.ui.features.login.models.GetUserTypeListResponseModel
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.teacher.TeacherMainActivity
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightBlue
import com.auro.application.ui.theme.PrimaryBlue

@SuppressLint("MutableCollectionMutableState")
@Preview
@Composable
fun ChooseUserTypeUI(navController: NavHostController = rememberNavController()) {

    var userTypes by remember { mutableStateOf(mutableListOf<GetUserTypeListResponseModel.Data>()) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

    var selectedUserType = remember { mutableStateOf<String?>(null) }
    var isDialogVisible by remember { mutableStateOf(false) }

    val viewModel: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
//    val languageData = viewModel.getLanguageTranslationData()
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)


    CustomDialog(
        isVisible = isDialogVisible,
        onDismiss = { isDialogVisible = false },
        message = "Loading your data..."
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResponse()
        }
    }
    BackHandler {
//        navController.navigateUp()
//        navController.popBackStack()
        navController.navigate(AppRoute.LanguageSelect)

    }

    LaunchedEffect(Unit) {
        viewModel.userTypeListResponseModel.observeForever {
            when (it) {
                is NetworkStatus.Idle -> {}
                is NetworkStatus.Loading -> {
                    isDialogVisible = true
                }

                is NetworkStatus.Success -> {
                    Log.e("TAG", "ChooseUserTypeUI: " + it.data?.data?.size)
                    isDialogVisible = false
                    if (it.data?.isSuccess == true) {
                        userTypes = it.data.data as MutableList<GetUserTypeListResponseModel.Data>
//                        userTypes.clear()
//                        userTypes.addAll(it.data.data)
                    }
                }

                is NetworkStatus.Error -> {
                    isDialogVisible = false
                    context.toast(it.message)
                }
            }
        }
        viewModel.getUserTypeList()
    }

    DefaultBackgroundUi(
        isShowBackButton = true,
        onBackButtonClick = {
//            navController.navigateUp()
//            navController.popBackStack()
            navController.navigate(AppRoute.LanguageSelect)
        },
        content = {

            Box(modifier = Modifier.fillMaxSize()) {
                Column {
                    val selectYourRole =
                        languageData[LanguageTranslationsResponse.USER_TYPE].toString()
                    val roleDescription =
                        languageData[LanguageTranslationsResponse.PERSONALISE_YOU].toString()
                    Text(
                        text = selectYourRole,
                        modifier = Modifier.padding(top = 20.dp, start = 12.dp, end = 12.dp),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Text(
                        roleDescription,
                        modifier = Modifier.padding(top = 0.dp, start = 12.dp, end = 12.dp),
                        color = GrayLight01,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 10.dp, end = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {

                        items(userTypes) { userTypeData ->
                            val index =
                                userTypes.indexOf(userTypeData) // Get the index of the current item

                            RoleItem(
                                userType = userTypeData,
                                isSelected = selectedIndex == index,
                                index = index,
                                selectedType = selectedUserType,
                                onSelect = {
                                    selectedIndex =
                                        if (selectedIndex == index) null else index // Toggle selection
                                    selectedUserType.value = userTypeData.id
                                    viewModel.saveUserType(selectedUserType.value.toString())
                                    println("Selected user type :- ${selectedUserType.value}")
                                }
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter)
                        .background(color = Color.White),
                    contentAlignment = Alignment.CenterEnd
                )
                {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val msg = languageData[LanguageTranslationsResponse.USER_TYPE].toString()
                        val continues =
                            languageData[LanguageTranslationsResponse.CONTINUE].toString()
                        BtnUi(
                            continues,
                            onClick = {
                                if (selectedIndex != null) {
                                    if (CommonFunction.isNetworkAvailable(context)) {
                                        if (selectedUserType.value == "3") {
                                            val strTeacherId = viewModel.getUserType().toString()
                                            val strLanguageId = viewModel.getLanguageId()
                                            val strMainUrl: String =
                                                "$BASE_URL_LOCAL_TEACHER?languageList=$strLanguageId&userType=$strTeacherId"
                                            println("Full url of teacher login :- $strMainUrl")
                                            if (strTeacherId != null) {
                                                println("Selected type :- $strTeacherId")
                                                launchCustomTab(context, strMainUrl)
                                            } else {
                                                println("Full url of teacher login not found")
                                            }

                                            /* viewModel.saveUserType(selectedUserType.value.toString())
                                             context.startActivity(
                                                 Intent(
                                                     context,
                                                     TeacherMainActivity::class.java
                                                 )
                                             )*/
//                                                .apply { (context as Activity).finish() })

//                                        val intent = Intent(context, TeacherMainActivity::class.java)
//                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                        intent.also {
//                                            it.putExtra("", true)
//                                        }
//                                        context.startActivity(intent)
                                        } else {
                                            // for student & parent default id is 1
                                            // will change this id after entering pin
                                            viewModel.saveUserType("1")
//                                        navController.popBackStack()
                                            navController.navigate(AppRoute.Login(LOGIN))
                                        }
                                    } else {
                                        context.toast("No internet connection")
                                    }
                                } else {
                                    context.toast(msg)
                                }
                            }, selectedIndex != null
                        )
                    }
                }
            }
        }
    )
}

//@Composable
//fun OpenWebPageButton(url: String) {
//    val context = LocalContext.current
//
//    launchCustomTab(context, url)
////            Text(text = "Open Web Page")
////        }
//}

fun launchCustomTab(context: Context, url: String) {
    val customTabsIntent = CustomTabsIntent.Builder()
        .setShowTitle(false)
//            .setSession(false)
        .build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
}

@Composable
fun RoleItem(
    userType: GetUserTypeListResponseModel.Data,
    isSelected: Boolean = true,
    onSelect: () -> Unit = {},
    index: Int,
    selectedType: MutableState<String?>,
) {
    val textStudent = stringResource(id = R.string.txt_student)
    val textPArent = stringResource(id = R.string.txt_Parent)
    if (userType.id != "1")
        Card(
            modifier = Modifier
                .clickable {
                    onSelect.invoke()
                }
                .padding(bottom = 20.dp)
                .border(
                    BorderStroke(
                        1.dp,
                        if (isSelected) PrimaryBlue else GrayLight02
                    ), // Border width and color
                    shape = RoundedCornerShape(10.dp) // Same shape as the card
                )
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isSelected) LightBlue else Color.White) // Set background color based on selection
                    .padding(vertical = 10.dp, horizontal = 5.dp), // Add horizontal padding
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Image(
                        modifier = Modifier
                            .padding(5.dp)
                            .background(Color.Unspecified),
                        painter = if (userType.id == "3") {
                            painterResource(R.drawable.icon_teacher)
                        } else {
                            painterResource(R.drawable.icon_student)
                        },
                        contentDescription = "Logo"
                    )

                    Text(
                        text = if (userType.id == "3") {
                            userType.name
                        } else {
                            "$textPArent/$textStudent"
                        },
                        modifier = Modifier
                            .weight(0.1f)
                            .padding(start = 10.dp),
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )

                    CircleCheckbox(
                        selected = isSelected,
                        enabled = true, // or false based on your logic
                        onChecked = {
//                            if (isSelected) onSelect()
                            onSelect()
                            selectedType.value = userType.id
                            BorderStroke(
                                1.dp,
                                if (isSelected) PrimaryBlue else GrayLight02
                            )
                        }
                    )
                }
            }
        }
}

val LocalLoginViewModel =
    compositionLocalOf<LoginViewModel> { error("No LoginViewModel provided") }



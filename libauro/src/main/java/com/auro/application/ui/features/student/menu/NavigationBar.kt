package com.auro.application.ui.features.student.menu

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.data.api.aes.AESEncryption.decrypt
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.screens.isEncrypted
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White

@Composable
fun DrawerHeader(
    viewModel: LoginViewModel,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Open),
    onItemClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()/* .clickable {
                onItemClick.invoke()
            }*/
            .wrapContentHeight()
            .padding(end = 50.dp)
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onItemClick.invoke() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .wrapContentSize()
                            .border(
                                width = 1.dp,
                                color = GrayLight02,
                                shape = CircleShape
                            )
                    ) {
                        if (viewModel.getUserImage().isNotEmpty()) {
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
                                    .size(72.dp) // Add size modifier to make the image visible
                                    .clip(CircleShape) // Add clip modifier to make the image circular
                                    .background(shape = CircleShape,color = White)
                                    .border( // Add border modifier to make image stand out
                                        width = 1.dp, color = GrayLight02, shape = CircleShape
                                    ),
                                contentScale = ContentScale.Crop // Clip the image to a circular shape
                            )
                        } else {
                            if (viewModel.getUserName().isNotEmpty()) {
                                val initials =
                                    viewModel.getUserName().split(" ").take(2)
                                        .joinToString("") { it.firstOrNull()?.toString() ?: "" }
                                        .uppercase()
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(72.dp) // set the size of the circle
                                        .background(
                                            PrimaryBlue, CircleShape
                                        ) // background color with circle shape
                                ) {
                                    Text(
                                        text = initials,
                                        color = White,
                                        fontSize = (72 / 2).sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Image(
                                    painterResource(R.drawable.ic_profile),
                                    contentDescription = "logo",
                                    modifier = Modifier
                                        .size(36.dp) // Set the image size
                                        .clip(CircleShape)
                                        .background(color = White, shape = CircleShape)// Clip the image to a circular shape
                                )
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .weight(1f),
                ) {
                    var textName: String = ""
                    var textEmail: String = ""
                    textName = try {
                        viewModel.getUserName().ifEmpty {
                            ""
                        }
                    } catch (e: Exception) {
                        ""
                    }

                    //  email
                    textEmail = if (viewModel.getUserEmail().isNotEmpty()) {
                        val isEncrypted = remember { isEncrypted(viewModel.getUserEmail()) }
                        if (isEncrypted) {
                            decrypt(viewModel.getUserEmail())
                        } else {
                            viewModel.getUserEmail()
                        }
                    } else {
                        ""
                    }
                    Text(
                        text = textName, style = MaterialTheme.typography.bodyMedium.copy(
                            color = Black, fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = textEmail, style = MaterialTheme.typography.bodyMedium.copy(
                            color = Gray, fontSize = 12.sp, fontWeight = FontWeight.Normal
                        )
                    )
                }
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_right_side),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                    .height(1.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawLine(
                        color = GrayLight02,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 0.8.dp.toPx()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun DrawerBody(
    items: List<MenuItem> = emptyList(),
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (String) -> Unit = {}
) {
    LazyColumn(
        modifier
            .padding(end = 50.dp)
            .zIndex(1f)
            .fillMaxHeight()
            .background(color = Color.White)
    ) {
        items(items) { item ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onItemClick(item.id)
                }
                .padding(start = 15.dp, end = 16.dp, top = 5.dp, bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = "",
                    tint = Color.Unspecified,
                )
                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = item.title,
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .weight(1f),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Gray,
                        fontSize = 16.sp,
                    ),
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_right_side),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
            }
        }
    }
}

//@Preview
@Composable
fun BottomNavigationBar(
    navController: NavHostController = rememberNavController(),
    currentDestination: String? = "",
    kycStatus: String = "",
    languageData: HashMap<String, String>
) {
    val item = listOf(
        BottomNavigationItems(
            appRoute = AppRoute.StudentDashboard.route,
//            title = stringResource(id = R.string.text_dashboard),
            title = languageData[LanguageTranslationsResponse.QUIZZES_AND_SCORES_HERE].toString(),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.dashboard_selected_icon),
            unSelectedIcon = ImageVector.vectorResource(id = R.drawable.dashboard_menu_icon)
        ), BottomNavigationItems(
            appRoute = AppRoute.StudentPassport.route,
            title = stringResource(id = R.string.text_passport),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.passport_menu_icon),
            unSelectedIcon = ImageVector.vectorResource(id = R.drawable.passport_menu_icon)
        ), BottomNavigationItems(
            appRoute = AppRoute.StudentWallet.route,
            title = if (languageData[LanguageTranslationsResponse.WALLET].toString() == "") {
                stringResource(id = R.string.txt_wallet)
            } else {
                languageData[LanguageTranslationsResponse.WALLET].toString()
            },
            selectedIcon = ImageVector.vectorResource(id = R.drawable.wallet_menu_icon),
            unSelectedIcon = ImageVector.vectorResource(id = R.drawable.wallet_menu_icon)
        ), BottomNavigationItems(
            appRoute = AppRoute.StudentPractice.route,
            title = if (languageData[LanguageTranslationsResponse.PRACTICE].toString() == "") {
                stringResource(id = R.string.txt_practice)
            } else {
                languageData[LanguageTranslationsResponse.PRACTICE].toString()
            },
            selectedIcon = ImageVector.vectorResource(id = R.drawable.practice_menu_icon),
            unSelectedIcon = ImageVector.vectorResource(id = R.drawable.practice_menu_icon)
        ), BottomNavigationItems(
            appRoute = AppRoute.StudentPartner.route,
            title = if (languageData[LanguageTranslationsResponse.PARTNER].toString() == "") {
                stringResource(id = R.string.txt_partner)
            } else {
                languageData[LanguageTranslationsResponse.PARTNER].toString()
            },
            selectedIcon = ImageVector.vectorResource(id = R.drawable.partner_menu_icon),
            unSelectedIcon = ImageVector.vectorResource(id = R.drawable.partner_menu_icon)
        )
    )
    val local = LocalContext.current
    val activeColor = PrimaryBlue
    val inactiveColor = GrayLight01
    val context: Context = LocalContext.current
    var isKycDialogVisible by remember { mutableStateOf(false) }

    NavigationBar(
        modifier = Modifier.padding(horizontal = 1.dp),
        containerColor = White,
        tonalElevation = 8.dp
    ) {
        item.forEach { screen ->
            NavigationBarItem(icon = {
                Log.d("SelectedScreen: ", "" + screen.appRoute)
                when (screen.appRoute) {
                    AppRoute.StudentDashboard.route -> Icon(
                        imageVector = screen.selectedIcon,
                        contentDescription = null,
                        tint = if (currentDestination == screen.appRoute) activeColor else inactiveColor,
                        modifier = Modifier.size(30.dp)
                    )

                    AppRoute.StudentPassport.route -> Icon(
                        imageVector = screen.selectedIcon,
                        contentDescription = null,
                        tint = if (currentDestination == screen.appRoute) activeColor else inactiveColor,
                        modifier = Modifier.size(30.dp)
                    )

                    AppRoute.StudentWallet.route -> Icon(
                        imageVector = screen.selectedIcon,
                        contentDescription = null,
                        tint = if (currentDestination == screen.appRoute) activeColor else inactiveColor,
                        modifier = Modifier.size(30.dp)
                    )

                    AppRoute.StudentPractice.route -> Icon(
                        imageVector = screen.selectedIcon,
                        contentDescription = null,
                        tint = if (currentDestination == screen.appRoute) activeColor else inactiveColor,
                        modifier = Modifier.size(30.dp)
                    )

                    AppRoute.StudentPartner.route -> Icon(
                        imageVector = screen.selectedIcon,
                        contentDescription = null,
                        tint = if (currentDestination == screen.appRoute) activeColor else inactiveColor,
                        modifier = Modifier.size(30.dp)
                    )

                    else -> {
                        Icon(
                            imageVector = screen.selectedIcon,
                            contentDescription = null,
                            tint = if (currentDestination == screen.appRoute) activeColor else inactiveColor,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }, selected = currentDestination == screen.appRoute, onClick = {
                if (screen.appRoute == "student_wallet") {
                    navController.navigate(screen.appRoute)
//                    if (kycStatus.contains("Approve")) {
//                        navController.navigate(screen.appRoute)
//                    } else {
//                        /*if (screen.appRoute == "student_wallet_kyc") {
//                            navController.navigate(AppRoute.StudentWalletKyc.route)
//                        } else {
//                            println("KYC is mandatory to withdraw Scholarship")
//                        }*/
//
//                        /* context.toast(
//                             if (languageData[LanguageTranslationsResponse.MANDATE_KYC].toString() == "") {
//                                 "KYC is mandatory to withdraw Scholarship"
//                             } else {
//                                 languageData[LanguageTranslationsResponse.MANDATE_KYC].toString()
//                             }
//                         )*/
//                    }
                } else {
                    navController.navigate(screen.appRoute)
                }
            }, label = {
                Text(
                    text = screen.title,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold,
                    color = if (currentDestination == screen.appRoute) activeColor else inactiveColor,
                    fontSize = 10.sp
                )
            }, colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent // Removes background color when selected
            )/* selectedColor = activeColor,
                 unselectedContentColor = inactiveColor*/
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationPreview() {
    AuroscholarAppTheme {

    }
}
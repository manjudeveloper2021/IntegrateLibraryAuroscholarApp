package com.auro.application.ui.features.student.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.auro.application.R
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightPink02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.Transparent
import com.auro.application.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenDashboardHeader(
    onNavigationIconClick: () -> Unit = {},
    navHostController: NavHostController,
    loginViewModel: LoginViewModel
) {


    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .clipToBounds()
    ) {
        Image(
            painter = painterResource(id = R.drawable.dashboard_header),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        )
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(75.dp)
                            .height(35.dp)
                    )
                    if (loginViewModel.getUserImage().isNotEmpty()) { // if image found
                        Image(
                            painter = if (loginViewModel.getUserImage().isNotEmpty()) {
                                rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current)
                                        .data(loginViewModel.getUserImage())
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
                                .size(36.dp) // Add size modifier to make the image visible
                                .clip(CircleShape) // Add clip modifier to make the image circular
                                .background(shape = CircleShape,color = White)
                                .border( // Add border modifier to make image stand out
                                    width = 1.dp, color = GrayLight02, shape = CircleShape
                                ),
                            contentScale = ContentScale.Crop// Clip the image to a circular shape
                        )
                    } else {  // if image not found put Initials
                        if (loginViewModel.getUserName().isNotEmpty()) {
                            val initials = loginViewModel.getUserName().split(" ").take(2)
                                .joinToString("") { it.firstOrNull()?.toString() ?: "" }.uppercase()
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp) // set the size of the circle
                                    .background(
                                        White, CircleShape
                                    ) // background color with circle shape
                            ) {
                                Text(
                                    text = initials,
                                    color = PrimaryBlue,
                                    fontSize = (36 / 2).sp,
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
                                    .background(color = Color.White, shape = CircleShape)// Clip the image to a circular shape
                            )
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue),
            modifier = Modifier
                .background(Transparent)
                .zIndex(1f),
            navigationIcon = {
                IconButton(onClick = onNavigationIconClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Toggle drawer",
                        tint = White
                    )
                }
            }
        )
    }
}

@Composable
fun AppBar(
    loginViewModel: LoginViewModel,
    onNavigationIconClick: () -> Unit = {},
    scope: CoroutineScope = rememberCoroutineScope({ Dispatchers.IO }),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Open),
    currentDestination: String? = "",
    navHostController: NavHostController
) {
    when (currentDestination) {
        AppRoute.StudentDashboard.route -> {
            OpenDashboardHeader(onNavigationIconClick, navHostController, loginViewModel)
        }

        else -> {
            when (currentDestination) {
                AppRoute.StudentProfile.route -> {

                }

                AppRoute.StudentPassport.route -> {
                    TopBarPassport(scope, drawerState)
                }

                else -> {
                    /* TopBarWallet(scope, drawerState, viewHistoryClickable = {
                         walletViewHistoryClickable.invoke()
                     })*/
                }
            }
        }
    }
}

/*
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun TopBar(navController: NavController, topBarState: MutableState<Boolean>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val title: String = when (navBackStackEntry?.destination?.route ?: "cars") {
        "cars" -> "Cars"
        "bikes" -> "Bikes"
        "settings" -> "Settings"
        "car_details" -> "Cars"
        else -> "Cars"
    }

    AnimatedVisibility(
        visible = topBarState.value,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        content = {
            TopAppBar(
                title = { Text(text = title) },
            )
        }
    )
}*/


@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopBarWallet(openMenu: () -> Unit = {}) {
    TopAppBar(title = {
        Row(
            modifier = Modifier.height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.MyWallet),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.clickable {

                },
                text = stringResource(id = R.string.viewHistory),
                fontSize = 15.sp,
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold
            )
        }
    },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        modifier = Modifier
            .background(Transparent)
            .zIndex(1f),
        navigationIcon = {
            IconButton(onClick = {
//                scope.launch {
//                    drawerState.open()
//                }
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Toggle drawer",
                    tint = Black
                )
            }
        }
    )
}

@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopBarPassport(
    scope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Open)
) {
    TopAppBar(title = {
        Row(
            modifier = Modifier.height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.text_passport),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.ic_passport_help), // Replace with your drawable resource
                contentDescription = null, // Provide a description for accessibility purposes
                modifier = Modifier.wrapContentSize()
            )
        }
    },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        modifier = Modifier
            .background(Transparent)
            .zIndex(1f),
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.open()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Toggle drawer",
                    tint = Black
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    AuroscholarAppTheme {
        TopBarPassport(scope, drawerState)
        /*AppBar(onNavigationIconClick = {
        })*/
    }
}
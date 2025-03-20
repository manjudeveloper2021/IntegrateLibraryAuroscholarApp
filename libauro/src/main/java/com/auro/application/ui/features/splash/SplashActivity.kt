package com.auro.application.ui.features.splash

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.auro.application.App
import com.auro.application.R
import com.auro.application.core.extions.toast
import com.auro.application.data.api.Constants
import com.auro.application.data.api.Constants.isLogin
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.LoginMainActivity
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.parent.ParentDashboardActivity
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowSizeClass
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.PrimaryBlue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    @Inject
    lateinit var sharedPref: SharedPref
    private val appclass: App?=null
    override fun onStart() {
        super.onStart()
        sharedPref.getToken().toString()?.let {
            Constants.token = it
        }
    }

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isLogin = sharedPref.isLogin()
        Log.e("TAG", "onCreate: login status --- > " + isLogin)
        setContent {
            AuroscholarAppTheme {
                val navController = rememberNavController()
                SplashUi("")
                WindowSizeClassSnippet()
                SplashScreen(viewModel, navController)

            }
        }
        appclass?.disableANR()
    }
    @Composable
    fun SplashScreen(viewModel: LoginViewModel, navController: NavHostController) {
        val context = LocalContext.current
        val appPackageName = context.packageName
        val studentViewModel: StudentViewModel = hiltViewModel()
        val appLink =
            "https://play.google.com/store/apps/details?id=$appPackageName"
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionName = packageInfo.versionName
//        val versionName = "129"
        var isForceUpdate by remember { mutableStateOf(false) }
        var isAPICalled by remember { mutableStateOf(false) }

//        val versionCode = BuildConfig.VERSION_CODE

        LaunchedEffect(Unit) {
            var intent = intent.data
            if (intent != null) {
                val userIdReferred = intent!!.getQueryParameter("userId")    // referred by
                val userTypeId = intent.getQueryParameter("userTypeId")  // usertype id
                studentViewModel.setReferredBy(userIdReferred!!.toInt())
                studentViewModel.isReferred(true)
                studentViewModel.setReferredTypeId(userTypeId!!.toInt())
                Log.d("DeepLinking:", " if --> $intent .. $userIdReferred .. $userTypeId")
            } else {
                studentViewModel.setReferredBy(0)
                studentViewModel.isReferred(false)
                studentViewModel.setReferredTypeId(0)
                Log.d("DeepLinking:", " else --> $intent")
            }

            viewModel.forceUpdateResponse.observeForever {
                when (it) {
                    is NetworkStatus.Idle -> {}
                    is NetworkStatus.Loading -> {}
                    is NetworkStatus.Success -> {
                        isAPICalled = true
//                        Log.e("TAG", "ChooseUserTypeUI: " + it.data?.data)
                        if (it.data?.isSuccess == true) {
                            isForceUpdate = false

                        } else {
                            isForceUpdate = true
                            context.toast(it.data!!.error)
                        }
                    }

                    is NetworkStatus.Error -> {
                        isAPICalled = true
                        isForceUpdate = true
                        context.toast(it.message)
                    }
                }
            }
            viewModel.getForceUpdateAPI(versionName.toString())
        }

        if (isForceUpdate) {
            try {
                // Try to open the Play Store app
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Fallback to a browser if the Play Store app isn't installed
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(appLink)
                )
                context.startActivity(intent)
            }
        } else {
            if (isAPICalled) {
                var splashCalled by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(3500L) //
                    splashCalled = true
                }
                if (splashCalled) {
                    navigationSetup(navController)
                }
            }
        }
    }

    @Composable
    private fun navigationSetup(
        navController: NavHostController = rememberNavController()
    ) {
        Log.d("Started:", "" + isLogin + " .. " + sharedPref.getUserTypeId())
        NavHost(
            navController = navController,
            startDestination = if (isLogin && sharedPref.getUserTypeId().equals("1")) {
                AppRoute.ParentDashboard.route
            } else if (isLogin && sharedPref.getUserTypeId().equals("2")) {
                AppRoute.Student_Dashboard
            } else {
                AppRoute.Language_Activity
            },

            builder = {
                composable(AppRoute.Language_Activity) {
                    startActivity(
                        Intent(
                            this@SplashActivity,
                            LoginMainActivity::class.java
                        )
                    ).also { finish() }
                }
                composable(AppRoute.Student_Dashboard) {
                    startActivity(
                        Intent(
                            this@SplashActivity,
                            StudentDashboardActivity::class.java
                        )
                    ).also { finish() }
                }
                composable(AppRoute.ParentDashboard.route) {
                    startActivity(
                        Intent(
                            this@SplashActivity,
                            ParentDashboardActivity::class.java
                        )
                    ).also { finish() }
                }
            })

    }

    @Composable
//    @Preview(showBackground = true, showSystemUi = true)
    fun SplashUi(version: String = "") {

        val context = LocalContext.current
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionName = packageInfo.versionName

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_scrn))

        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

        Box(
            modifier = Modifier
                .fillMaxSize() // Fill the entire screen
                .background(color = PrimaryBlue) // Set the background color
        ) {
            // Full-screen Image
            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier
                    .fillMaxSize()
            )

            // Text at the bottom
            Text(
                text = "Version : $versionName",
                modifier = Modifier
                    .fillMaxWidth() // Take the full width of the screen
                    .align(Alignment.BottomCenter) // Align the text at the bottom center of the screen
                    .padding(top = 20.dp, bottom = 30.dp), // Add some padding
                fontStyle = FontStyle.Normal,
                fontSize = 16.sp,
                color = Color.White,
                fontFamily = FontFamily(
                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                ),
                textAlign = TextAlign.Center, // Center-align the text
            )
        }
    }
    @Composable
    private fun WindowSizeClassSnippet() {
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    }
}
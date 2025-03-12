package com.auro.application.ui.features.teacher

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.auro.application.core.ConstantVariables.LOGIN
import com.auro.application.data.api.Constants.BASE_URL_LOCAL_TEACHER
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.components.BackHandler
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class TeacherMainActivity : ComponentActivity() {

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuroscholarAppTheme {
                val context = LocalContext.current
                val activity = (context as? Activity)
                val navController = rememberNavController()
                val viewModel: LoginViewModel = hiltViewModel()
                val strTeacherId = viewModel.getUserType().toString()
                val strLanguageId = viewModel.getLanguageId()
                var showWebView by remember { mutableStateOf(true) }

                val strMainUrl: String = "$BASE_URL_LOCAL_TEACHER?languageList=$strLanguageId&userType=$strTeacherId"
//                val strMainUrl: String = BASE_URL_LOCAL_TEACHER
                Log.d("webView:",""+strMainUrl)
                if (showWebView) {
                    OpenWebPageButton(strMainUrl)
                /*WebViewScreen(strMainUrl,
                    onBack = {
                        showWebView = false
//                        navController.navigate(AppRoute.SelectRole.route)
                        activity?.finish()
                    } // Navigate back to the previous Compose screen)
//                WebView()
                )*/
                }
                /*else {
                    // Other UI for your app
                    finish()
                    navController.navigateUp()
                    navController.popBackStack()
                }*/
            }
        }
    }

    @Composable
    fun OpenWebPageButton(url: String) {
        val context = LocalContext.current

       launchCustomTab(context, url)
//            Text(text = "Open Web Page")
//        }
    }
    fun launchCustomTab(context: Context, url: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(false)
//            .setSession(false)
            .build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun WebViewScreen(url: String, onBack: () -> Unit) // Called when the back stack should be navigated)
     {

        var webViewState by remember { mutableStateOf(WebViewState.Loading) }
        val webView = remember { WebView(this) }
        var canGoBack by remember { mutableStateOf(false) }

        Box(Modifier.fillMaxSize()) {
            AndroidView(factory = { context ->
                WebView(context).apply {
                    WebView.setWebContentsDebuggingEnabled(true)
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.javaScriptCanOpenWindowsAutomatically = true
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    settings.useWideViewPort = true
                    CookieManager.getInstance().setAcceptCookie(true)
                    CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                    overScrollMode = WebView.OVER_SCROLL_IF_CONTENT_SCROLLS
//                    isVerticalScrollBarEnabled = true // Enable vertical scrollbar
                    isNestedScrollingEnabled = true // Enable nested scrolling for Compose interop
                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            canGoBack = webView.canGoBack()
                            webViewState = WebViewState.Loaded
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onReceivedError(
                            view: WebView?,
                            errorCode: Int,
                            description: String?,
                            failingUrl: String?
                        ) {
                            super.onReceivedError(view, errorCode, description, failingUrl)
                            webViewState = WebViewState.Error
                            println("Web view error information :- $failingUrl")
                        }
                    }
                    loadUrl(url)
                }
            }, update = { webView -> webView.loadUrl(url) }, modifier = Modifier.fillMaxSize()
            )

            if (webViewState == WebViewState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
         BackHandler(enabled = true) {
             if (canGoBack) {
                 webView.goBack()
             } else {
                 onBack()
             }
         }
    }

    enum class WebViewState {
        Loading, Loaded, Error;
    }

@Composable
fun WebView() {
    val navController = rememberNavController()
    val viewModel: LoginViewModel = hiltViewModel()
    val strTeacherId = viewModel.getUserType().toString()
    val strLanguageId = viewModel.getLanguageId()

    val strMainUrl: String = "$BASE_URL_LOCAL_TEACHER?languageList=$strLanguageId&userType=$strTeacherId"

    println("Actual url link :- $strMainUrl")
    var showWebView by remember { mutableStateOf(true) }

    if (showWebView) {
        WebViewWithBackHandler(
            url = strMainUrl,
            onBack = { showWebView = false } // Navigate back to the previous Compose screen
        )
    } else {
        // Other UI for your app
        navController.navigateUp()
        navController.popBackStack()
    }
}

    @Composable
    fun WebViewWithBackHandler(
        url: String,
        onBack: () -> Unit // Called when the back stack should be navigated
    ) {
        val webView = remember { WebView(this) }
        var canGoBack by remember { mutableStateOf(false) }

        // WebViewClient to update `canGoBack`
        DisposableEffect(Unit) {
            val webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    canGoBack = webView.canGoBack()
                }
            }
            webView.webViewClient = webViewClient
            onDispose { webView.destroy() }
        }

        BackHandler(enabled = true) {
            if (canGoBack) {
                webView.goBack()
            } else {
                onBack()
            }
        }

        AndroidView(
            factory = { webView },
            modifier = Modifier.fillMaxSize(),
            update = {
                it.loadUrl(url)
            }
        )
    }

}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun TeacherPreviewPanel() {
    TeacherMainActivity()
}
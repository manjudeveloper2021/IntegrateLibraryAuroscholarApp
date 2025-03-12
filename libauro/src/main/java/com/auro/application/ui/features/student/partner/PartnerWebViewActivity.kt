package com.auro.application.ui.features.student.partner

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.auro.application.R
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.White
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class PartnerWebViewActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuroscholarAppTheme {

                val partnerViewModel: PartnerViewModel = hiltViewModel()
                var strCompanyName by remember { mutableStateOf("") }
                var strCompanyUrl by remember { mutableStateOf("") }

                if (partnerViewModel.getPartnerCompanyInfo() != null) {
                    strCompanyName = partnerViewModel.getPartnerCompanyInfo().name.toString()
                    strCompanyUrl = partnerViewModel.getPartnerCompanyInfo().companyUrl.toString()

                    println("Company name and website :- $strCompanyName, $strCompanyUrl")
                } else {
                    println("Company name and website :- N/A, N/A")
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 6.dp, bottom = 16.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Surface(
                            tonalElevation = 10.dp, // Set the elevation here
                            color = White,
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.back_icon),
                                    contentDescription = "logo",
                                    modifier = Modifier.clickable {
                                        onBackPressed()
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
                                        text = strCompanyName.ifEmpty {
                                            "N/A"
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        fontStyle = FontStyle.Normal,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Black,
                                        textAlign = TextAlign.Start
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(top = 20.dp, bottom = 20.dp)
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

                    WebViewScreen(strCompanyName, strCompanyUrl)
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun WebViewScreen(companyName: String, url: String) {

        var webViewState by remember { mutableStateOf(WebViewState.Loading) }

        Box(Modifier.fillMaxSize()) {
            AndroidView(factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = false // Allow autoplay videos
                    settings.allowFileAccess = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()  // Enables video playback in WebView
                    loadUrl(url)

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
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
                            Toast.makeText(
                                context, "$companyName is not working.", Toast.LENGTH_SHORT
                            ).show()
                            onBackPressed()
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onShowCustomView(
                            view: View, callback: CustomViewCallback
                        ) {
                            // Handle full-screen video here
                            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                        }

                        override fun onHideCustomView() {
                            // Exit full-screen video here
                        }
                    }
                }
            }, update = { webView -> webView.loadUrl(url) }, modifier = Modifier.fillMaxSize()
            )

            if (webViewState == WebViewState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    enum class WebViewState {
        Loading, Loaded, Error;
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PartnerPreviewPanel() {
    PartnerWebViewActivity()
}
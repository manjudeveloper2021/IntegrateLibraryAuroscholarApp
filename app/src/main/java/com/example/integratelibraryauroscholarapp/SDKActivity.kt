package com.example.integratelibraryauroscholarapp

import android.app.Activity
import android.icu.util.ULocale.getLanguage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import com.example.integratelibraryauroscholarapp.ui.theme.IntegrateLibraryAuroscholarAppTheme



class SDKActivity  : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IntegrateLibraryAuroscholarAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                    val inputModel = AuroScholarInputModel()
                    inputModel.setMobileNumber("9289180019")
                    inputModel.setStudentClass(java.lang.String.valueOf("8"))
                    inputModel.setPartner_unique_id("jfhejhfengdkghd")
                    inputModel.setPartnerSource("Auroscholar")
                    inputModel.setPartner_api_key("gjnbgjnffngknrfn")
                    inputModel.setActivity(this@SDKActivity as Activity)
                   startAuroSDK(inputModel)
                }
            }
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
fun startAuroSDK(inputModel: AuroScholarInputModel): Fragment? {
    var auroScholarDataModel = AuroScholarDataModel()
    auroScholarDataModel = AuroScholarDataModel()
    auroScholarDataModel.setMobileNumber(inputModel.getMobileNumber())
    auroScholarDataModel.setStudentClass(inputModel.getStudentClass())
    auroScholarDataModel.setActivity(inputModel.getActivity())
    auroScholarDataModel.setUserPartnerid(inputModel.getPartner_unique_id())
    auroScholarDataModel.setPartnerSource(inputModel.getPartnerSource())
    auroScholarDataModel.setApikey(inputModel.getPartner_api_key())
    return null
}
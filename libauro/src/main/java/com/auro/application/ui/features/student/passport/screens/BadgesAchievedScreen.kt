package com.auro.application.ui.features.student.passport.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.auro.application.R
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.BtnUi
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.White
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BadgesAchievedScreen : ComponentActivity() {

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuroscholarAppTheme {
                BadgesAchievedData()
            }
        }
    }
}

@Composable
fun BadgesAchievedData() {

    val navController = rememberNavController()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    ImageVector.vectorResource(id = R.drawable.icon_party_popper),
                    tint = Color.Unspecified,
                    modifier = Modifier.padding(top = 30.dp).background(Color.Unspecified),
                    contentDescription = "Expand/Collapse Button"
                )
                Icon(
                    ImageVector.vectorResource(id = R.drawable.icon_popup_beginner),
                    tint = Color.Unspecified,
                    contentDescription = "Expand/Collapse Button",
                    modifier = Modifier.background(Color.Unspecified)
                )
            }
            Text(
                "New Achievement",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 50.dp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Black,
                textAlign = TextAlign.Center
            )
            Text(
                "Congratulations! You've unlocked the Beginner badge! Keep up the great start!\n",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Black,
                textAlign = TextAlign.Center
            )
        }
        BtnUi(stringResource(id = R.string.txt_collect), onClick = {
            navController.navigate(AppRoute.ChildList("1","1"))
        }, true)
    }
}

@Preview(showBackground = true)
@Composable
fun StudentBadgesPreview() {
    AuroscholarAppTheme {
        BadgesAchievedData()
    }
}
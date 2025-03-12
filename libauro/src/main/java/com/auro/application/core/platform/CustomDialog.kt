package com.auro.application.core.platform

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.PrimaryBlueLt

@Preview
@Composable
fun CustomDialog(
    isVisible: Boolean = true,
    onDismiss: () -> Unit = {},
    message: String = "Loading..."
) {
    BackHandler(isVisible) {
        onDismiss()
    }
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding().background(color = Color.Transparent)
                        .fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding()
                            .background(color = Color.White, shape = RoundedCornerShape(10.dp))) {
                        CircularProgressIndicator(modifier = Modifier.size(150.dp).padding(40.dp), trackColor = PrimaryBlueLt, color = PrimaryBlue)
                    }
                }
            },
            confirmButton = {

            },
            dismissButton = {
            },
            modifier = Modifier.fillMaxWidth(0.9f), containerColor = Color.Transparent
        )
    }
}
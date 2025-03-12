package com.auro.application.data.utlis

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.PrimaryBlueLt
import com.auro.application.ui.theme.White

/*@Composable
fun CustomProgressBar(
    percentage: Float = 0.5f,
    modifier: Modifier = Modifier,
    color: Color = PrimaryBlue,
    secondaryColor: Color = PrimaryBlueLt,
    strokeWidth: Float = 12f
) {
    Box(
        contentAlignment = androidx.compose.ui.Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = modifier) {
            val diameter = size.minDimension
            val radius = diameter / 2
            val center = size.center

            // Progress Bar background color
            drawArc(
                color = secondaryColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            // Progress color
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * (percentage/100),
                useCenter = false,
                topLeft = center - androidx.compose.ui.geometry.Offset(radius, radius),
                size = androidx.compose.ui.geometry.Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${(percentage).toInt()}%",
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = color,
        )
    }
}*/

/*
@Composable
fun CustomProgressBar(
    percentage: Float = 10f,
    modifier: Modifier = Modifier,
    color: Color = PrimaryBlue,
    secondaryColor: Color = PrimaryBlueLt,
    strokeWidth: Float = 12f
) {
    // Ensure percentage is clamped between 0 and 100
    val clampedPercentage = percentage.coerceIn(0f, 100f)

    // Animate the progress for a smooth transition
    val animatedPercentage by animateFloatAsState(targetValue = clampedPercentage)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = modifier) {
            val diameter = size.minDimension
            val radius = diameter / 2
            val center = size.center

            // Draw progress bar background
            drawArc(
                color = secondaryColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            // Draw progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * (animatedPercentage / 100),
                useCenter = false,
                topLeft = center - Offset(radius, radius),
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${animatedPercentage.toInt()}%",
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = color,
        )
    }
}*/

@Composable
fun CustomProgressBar(
    percentage: Float = 0.7f,
    modifier: Modifier = Modifier,
    color: Color = PrimaryBlue,
    secondaryColor: Color = PrimaryBlueLt,
    strokeWidth: Float = 12f
) {
    // Ensure percentage is clamped between 0 and 100
    val clampedPercentage = percentage.coerceIn(0f, 100f)

    // Animate the progress for a smooth transition
    val animatedPercentage by animateFloatAsState(
        targetValue = clampedPercentage,
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = modifier) {
            val diameter = size.minDimension
            val radius = diameter / 2
            val center = size.center

            // Draw progress bar background
            drawArc(
                color = secondaryColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            // Draw progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * (animatedPercentage / 100),
                useCenter = false,
                topLeft = center - Offset(radius, radius),
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${animatedPercentage.toInt()}%",
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = color,
        )
    }
}

@Preview
@Composable
fun CustomerCircularProgressBarPreview() {
    CustomProgressBar(
        percentage = 0.75f,
        modifier = Modifier
            .fillMaxSize(0.5f)
    )

}
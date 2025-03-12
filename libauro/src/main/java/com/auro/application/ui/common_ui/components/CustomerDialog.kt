package com.auro.application.ui.common_ui.components

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.auro.application.R
import com.auro.application.ui.features.student.StudentDashboardActivity
import com.auro.application.ui.features.student.authentication.screens.Button
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.White
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class WalkThroughModel(var title: String, var description: String, var imgUrl: Int)

val list = listOf<WalkThroughModel>(
    WalkThroughModel(
        "Register and Complete KYC",
        "Begin your learning journey by registering and completing your KYC on the Auroscholar App.",
        R.drawable.select_your_subject
    ),
    WalkThroughModel(
        "Take tailored Quizzes and Practice",
        "Attempt concept based quizzes for subjects of your grade and practice to improve your skills.",
        R.drawable.select_concepts
    ),
    WalkThroughModel(
        "Track Your Progress",
        "Attempt concept based quizzes for subjects of your grade and practice to improve your skills.",
        R.drawable.other_subject
    ),
    WalkThroughModel(
        "Win Scholarships",
        "Score 8 or higher marks in quizzes to qualify for Micro-scholarships. Keep striving for excellence!",
        R.drawable.add_more_concept
    )
)

enum class WalkthroughDialogStatus {
    UNVIEW,
    VIEWED,
    DEFAULT
}

class WalkthroughDialog {
    // Mutable property to hold the current status
    var currentStatus: WalkthroughDialogStatus? = null
        private set // Make the setter private to control access

    // Method to update the status
    fun updateStatus(newStatus: WalkthroughDialogStatus) {
        currentStatus = newStatus
        println("Status updated to: $currentStatus")
    }
}

@Composable
fun WalkthroughDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    list: List<WalkThroughModel> = emptyList()
) {

    if (showDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            ),
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    Walkthrough(
                        list = list,
                        position = { pos ->
                        }, onDismissRequest = {
                            onDismissRequest.invoke()
                        }
                    )
                }
            }
        )
    }

}


@Composable
fun Walkthrough(
    list: List<WalkThroughModel> = emptyList(),
    position: (Int) -> Unit = {},
    onDismissRequest: () -> Unit
) {
    val state = rememberPagerState()
    val scope = rememberCoroutineScope()
    HorizontalPager(
        count = list.size,
        state = state,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                }
            },
        verticalAlignment = Alignment.Top
    ) { page ->
        position.invoke(page)
        WalkthroughPage(
            walkthroughItem = list[page], nextSlide = {
                scope.launch {
                    state.scrollToPage(state.currentPage + 1)
                }
            }, onHide = {
                onDismissRequest.invoke()
            }, page
        )
    }
}

@Composable
fun WalkthroughPage(
    walkthroughItem: WalkThroughModel? = null,
    nextSlide: () -> Unit,
    onHide: () -> Unit,
    page: Int
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.walkthrough_bg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Image(
                painter = painterResource(id = walkthroughItem?.imgUrl ?: -1),
                contentDescription = "Poster Image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .padding(top = 0.dp, start = 40.dp, end = 40.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .align(Alignment.TopCenter)
            )

            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Image(
                    painter = painterResource(id = R.drawable.close_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(25.dp)
                        .clickable {
                            onHide.invoke()
                            /* context.startActivity(
                                 Intent(
                                     context, StudentDashboardActivity::class.java
                                 ).apply { (context as Activity).finish() })*/
                        },
                )
            }

            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                Card(
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .background(color = Color.White)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = walkthroughItem?.title ?: "",
                            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontFamily = FontFamily(
                                Font(R.font.inter_bold, FontWeight.Bold)
                            )
                        )

                        Text(
                            text = walkthroughItem?.description ?: "",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp),
                            fontFamily = FontFamily(
                                Font(R.font.inter_medium, FontWeight.Medium)
                            )
                        )

                        PageIndicatorSample(
                            selectedPage = page,
                            numberOfPages = list.size,
                            modifier = Modifier
                                .padding()
                                .padding(top = 20.dp)
                        ) // Assuming this is a Composable for page indicators

                        Button(
                            enabled = true,
                            onClick = {
                                if (page == list.lastIndex) {
                                    onHide.invoke()
                                } else {
                                    nextSlide.invoke()
                                }
                            },
                            title = if (page == list.lastIndex) "Continue" else "Next",
                            modifier = Modifier
                                .padding()
                                .padding(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PageIndicatorSample(
    selectedPage: Int = 0,
    numberOfPages: Int = 3, modifier: Modifier
) {
    // Use remember to hold the mutable state of the selected page
    val (currentPage, setCurrentPage) = remember { mutableStateOf(selectedPage) }

    // This effect will change the selected page every 3 seconds
    LaunchedEffect(currentPage) {
        delay(3000)
        setCurrentPage((currentPage + 1) % numberOfPages)
    }

    PageIndicator(
        modifier = modifier,
        numberOfPages = numberOfPages,
        selectedPage = selectedPage,
        defaultRadius = 15.dp,
        selectedLength = 30.dp,
        space = 7.dp,
        animationDurationInMillis = 1000,
    )
}

@Composable
fun PageIndicator(
    numberOfPages: Int,
    modifier: Modifier = Modifier,
    selectedPage: Int = 0,
    selectedColor: Color = PrimaryBlue,
    defaultColor: Color = Color.LightGray,
    defaultRadius: Dp = 50.dp,
    selectedLength: Dp = 50.dp,
    space: Dp = 10.dp,
    animationDurationInMillis: Int = 300,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space),
        modifier = modifier,
    ) {
        for (i in 0 until numberOfPages) {
            val isSelected = i == selectedPage
            PageIndicatorView(
                isSelected = isSelected,
                selectedColor = selectedColor,
                defaultColor = defaultColor,
                defaultRadius = defaultRadius,
                selectedLength = selectedLength,
                animationDurationInMillis = animationDurationInMillis,
            )
        }
    }
}

@Composable
fun PageIndicatorView(
    isSelected: Boolean,
    selectedColor: Color,
    defaultColor: Color,
    defaultRadius: Dp,
    selectedLength: Dp,
    animationDurationInMillis: Int,
    modifier: Modifier = Modifier,
) {

    val color: Color by animateColorAsState(
        targetValue = if (isSelected) {
            selectedColor
        } else {
            defaultColor
        },
        animationSpec = tween(
            durationMillis = animationDurationInMillis,
        )
    )
    val width: Dp by animateDpAsState(
        targetValue = if (isSelected) {
            selectedLength
        } else {
            defaultRadius
        },
        animationSpec = tween(
            durationMillis = animationDurationInMillis,
        )
    )

    Canvas(
        modifier = modifier
            .size(
                width = width,
                height = defaultRadius,
            ),
    ) {
        drawRoundRect(
            color = color,
            topLeft = Offset.Zero,
            size = Size(
                width = width.toPx(),
                height = defaultRadius.toPx(),
            ),
            cornerRadius = CornerRadius(
                x = defaultRadius.toPx(),
                y = defaultRadius.toPx(),
            ),
        )
    }
}
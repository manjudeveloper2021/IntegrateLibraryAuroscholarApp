package com.auro.application.ui.common_ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.auro.application.R
import com.auro.application.ui.common_ui.Btn12PXUi
import com.auro.application.ui.common_ui.BtnTextUi
import com.auro.application.ui.features.student.passport.models.ReferalCodeResponse
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.LightPink02
import com.auro.application.ui.theme.PrimaryBlue
import com.auro.application.ui.theme.Transparent
import com.auro.application.ui.theme.White
import com.auro.application.ui.theme.Yellow
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

@Composable
fun CheckedLanguageCardView(
    selected: Boolean, onChecked: () -> Unit, content: @Composable ColumnScope.() -> Unit
) {
    if (selected) {
        Card(
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .padding(7.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp),
            border = BorderStroke(width = 1.dp, PrimaryBlue),
        ) {}
    } else {
        Card(
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .padding(7.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp),
            border = BorderStroke(width = 1.dp, PrimaryBlue),
        ) {

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferBottomSheet(
    referalCode: ReferalCodeResponse.Data?, onDismiss: () -> Unit/*  show: Boolean,
      onHide: () -> Unit,
      buttonText: String,
      content: @Composable ColumnScope.() -> Unit*/
) {
    val sheetState = rememberModalBottomSheetState()
    /* val sheetState = rememberModalBottomSheetState(
         confirmValueChange = { false } // Prevent state change via dragging
     )*/
    val scope = rememberCoroutineScope()
    val context: Context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.txt_refer_friend),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start
                )
            )
            Text(
                text = stringResource(id = R.string.txt_refer_copy_code),
                color = Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start
            )
            Image(
                painter = painterResource(R.drawable.circlular_refer_friend),
                contentDescription = "logo",
                modifier = Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(100.dp))
                    .background(color = Transparent)// Add clip modifier to make the image circular
                    .border( // Add border modifier to make image stand out
                        width = 1.dp, color = LightPink02, shape = CircleShape
                    )
            )
            Text(
                text = stringResource(id = R.string.txt_code_to_invite),
                color = Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                ) {
                    val width = size.width
                    val height = size.height

                    // Grey part (80%)
                    drawRect(
                        color = GrayLight02,
                        size = androidx.compose.ui.geometry.Size(width * 0.8f, height)
                    )

                    // Blue part (20%)
                    drawRect(
                        color = PrimaryBlue,
                        topLeft = androidx.compose.ui.geometry.Offset(width * 0.8f, 0f),
                        size = androidx.compose.ui.geometry.Size(width * 0.2f, height)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp, horizontal = 10.dp)
                ) {

                    Text(
                        text = if (referalCode != null) {
                            referalCode.referralCode.toString()
                        } else {
                            ""
                        }, modifier = Modifier
                            .padding(10.dp)
                            .weight(1f), style = TextStyle(
                            textAlign = TextAlign.Start, // Center align the text
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = Black,
                        )
                    )
                    Text(
                        text = "Copy", modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                val strCode: String = referalCode?.referralCode ?: ""
                                clipboardManager.setText(AnnotatedString(strCode))
                                Toast
                                    .makeText(context, "Code Copied!!", Toast.LENGTH_SHORT)
                                    .show()
                            }, style = TextStyle(
                            textAlign = TextAlign.End, // End align the text
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = White,
                        )
                    )
                }
            }
            Text(
                text = "OR", fontWeight = FontWeight.SemiBold, fontSize = 14.sp
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp, start = 10.dp, end = 10.dp),
            ) {
                Button(
                    onClick = {
                        try {
                            val strCode: String = referalCode?.referralCode ?: ""

                            val appPackageName = context.packageName
                            val appLink =
                                "https://play.google.com/store/apps/details?id=$appPackageName"
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Hey, check out this awesome app:- $appLink \n\n" + "Your referal code:- $strCode"
                                )
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share via")
                            context.startActivity(shareIntent)
                        } catch (exp: Exception) {
                            exp.message
                            println("Refer now find error is :- ${exp.message}")
                        }
                    }, modifier = Modifier
                        .wrapContentSize()
                        .padding(top = 5.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp
                            )
                        ), colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue, contentColor = White
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.txt_or_share_via),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

}

@Composable
fun TextWithIconOnRight(
    text: String,
    icon: ImageVector,
    textColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text, style = MaterialTheme.typography.bodyMedium.copy(
                color = textColor,
                fontSize = 12.sp,
                fontFamily = FontFamily(
                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                ),
                textAlign = TextAlign.Start
            )
        )
        Spacer(modifier = Modifier.width(4.dp)) // Add space between text and icon
        Icon(
            imageVector = icon, contentDescription = null, // Decorative element
            tint = iconColor
        )
    }
}

@Composable
fun TextWithIconOnLeft(
    text: String,
    icon: ImageVector,
    textColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon, contentDescription = null, // Decorative element
            tint = iconColor
        )
        Spacer(modifier = Modifier.width(4.dp)) // Add space between text and icon
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 5.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start
            )
        )

    }
}

@Composable
fun CustomHorizontalProgressBar(progressBar: Float) {

    val progress by remember { mutableStateOf(progressBar) } // Example progress value
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .height(4.dp)
                .fillMaxWidth(fraction = 1f)
                .clip(RoundedCornerShape(50))
                .background(White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = progress)
                    .background(Yellow)
            )
        }
    }
}

/*fun filterByQuizAttemptMonthSchollership(
    filterQuizAttemptValue: String, onDismiss: () -> Unit,
    filterMonthValue: String, onDismiss: () -> Unit,
    filterSchollershipValue: String, onDismiss: () -> Unit
){

}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun quizAttemptsFilterBottomSheet(
    filterValue: String, /*win: String,*/ onDismiss: () -> Unit
): String {
    val filterOptions = listOf(
        "Core", "Try again 1", "Try again 2", "Practice 1", "Practice 2", "Practice 3"
    )
    val filterMicroscholar = listOf("Yes", "No")
    val sheetState = rememberModalBottomSheetState()
    var selectedValue by remember { mutableStateOf(filterValue) }
//    var selectedWinValue by remember { mutableStateOf(win) }
    var selectedOption by remember { mutableStateOf(filterOptions[0]) }
    var selectedMicroOption by remember { mutableStateOf(filterMicroscholar[0]) }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, start = 15.dp, end = 15.dp, bottom = 20.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.txt_filter),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(top = 10.dp, bottom = 20.dp)
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

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.txt_quiz_attempts),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = GrayLight01,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start
                    )
                )

                Column(
                    modifier = Modifier.padding(
                        top = 10.dp, bottom = 10.dp, start = 16.dp, end = 16.dp
                    ), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterOptions.forEach { text ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (text == selectedOption), onClick = {
                                        selectedOption = text
                                        selectedValue = text
//                                        onDismiss()
                                    }, role = Role.RadioButton
                                ), verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null, // null recommended for accessibility with screenreaders
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = PrimaryBlue, // Color when selected
                                    unselectedColor = GrayLight02 // Color when not selected
                                )
                            )
                            Text(
                                text = text,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 10.dp),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Gray,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Start
                                )
                            )
                        }
                    }
                }
                Text(
                    modifier = Modifier.padding(top = 15.dp, start = 16.dp, end = 16.dp),
                    text = stringResource(id = R.string.txt_micro_scholarship),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = GrayLight01,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start
                    )
                )

                Column(
                    modifier = Modifier.padding(
                        top = 10.dp, bottom = 15.dp, start = 16.dp, end = 16.dp
                    ), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterMicroscholar.forEach { text ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (text == selectedMicroOption), onClick = {
                                        selectedMicroOption = text
                                        selectedValue = text
//                                        onDismiss()
                                    }, role = Role.RadioButton
                                ), verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedMicroOption),
                                onClick = null, // null recommended for accessibility with screenreaders
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = PrimaryBlue, // Color when selected
                                    unselectedColor = GrayLight02 // Color when not selected
                                )
                            )
                            Text(
                                text = text,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 10.dp),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Gray,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Start
                                )
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(top = 10.dp)
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    BtnTextUi(stringResource(id = R.string.txt_cancel),
                        color = PrimaryBlue,
                        onClick = {
//                        onDecline.invoke()
                            onDismiss()
                        })
                    Btn12PXUi(title = stringResource(R.string.txt_apply_result), onClick = {
                        onDismiss()
                    }, true)
                }
            }
        }
    }
    return selectedValue
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun monthYearFilterBottomSheet(
    filterValue: String, onDismiss: () -> Unit/*  show: Boolean,
      onHide: () -> Unit,
      buttonText: String,
      content: @Composable ColumnScope.() -> Unit*/
): String {
    var selectedYear by remember { mutableStateOf(Year.now().value) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now().monthValue) }

    val sheetState = rememberModalBottomSheetState()
    var selectedValue by remember { mutableStateOf(filterValue) }


    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, start = 15.dp, end = 15.dp, bottom = 20.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.txt_select_month_year),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(top = 10.dp, bottom = 20.dp)
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

                // calender
                YearMonthGridPicker(
                    selectedYear = selectedYear, selectedMonth = selectedMonth
                ) { year, month ->
                    selectedYear = year
                    selectedMonth = month
                    if (selectedMonth < 10) {
                        println("Selected Year: $selectedYear, Month: 0$selectedMonth")
                        selectedValue = "$selectedYear" + "0$selectedYear"
                    } else {
                        println("Selected Year: $selectedYear, Month: $selectedMonth")
                    }

                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(top = 10.dp)
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    BtnTextUi(stringResource(id = R.string.txt_cancel),
                        color = PrimaryBlue,
                        onClick = {
                            onDismiss()
                        })

                    Btn12PXUi(title = stringResource(R.string.txt_apply_result), onClick = {
                        onDismiss()
                        println("Selected months :- $selectedValue")
                    }, false)
                }
            }
        }
    }
    return selectedValue
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun quizVerificationStatusBottomSheet(
    filterValue: String, onDismiss: () -> Unit/*  show: Boolean,
      onHide: () -> Unit,
      buttonText: String,
      content: @Composable ColumnScope.() -> Unit*/
): String {
    val filterOptions = listOf(
        "Approved", "Disapproved", "In Process"
    )

    val sheetState = rememberModalBottomSheetState()
    var selectedValue by remember { mutableStateOf(filterValue) }
    var selectedOption by remember { mutableStateOf(filterOptions[0]) }


    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, start = 15.dp, end = 15.dp, bottom = 20.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.txt_filter),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(top = 10.dp, bottom = 20.dp)
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

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.txt_quiz_attempts),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = GrayLight01,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start
                    )
                )

                Column(
                    modifier = Modifier.padding(
                        top = 10.dp, bottom = 10.dp, start = 16.dp, end = 16.dp
                    ), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterOptions.forEach { text ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (text == selectedOption), onClick = {
                                        selectedOption = text
                                        selectedValue = text
//                                        onDismiss()
                                    }, role = Role.RadioButton
                                ), verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null, // null recommended for accessibility with screenreaders
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = PrimaryBlue, // Color when selected
                                    unselectedColor = GrayLight02 // Color when not selected
                                )
                            )
                            Text(
                                text = text,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 10.dp),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Gray,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Start
                                )
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(top = 10.dp)
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    BtnTextUi(stringResource(id = R.string.txt_cancel),
                        color = PrimaryBlue,
                        onClick = {
//                        onDecline.invoke()
                            onDismiss()
                        })
                    Btn12PXUi(title = stringResource(R.string.txt_apply_result), onClick = {
                        onDismiss()

                    }, true)
                }
            }
        }
    }
    return selectedValue
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun monthlyFilterBottomSheet(
    filterValue: String,
    onValueSelected: (String) -> Unit,  //  callback
    onDismiss: () -> Unit,
    /*  show: Boolean,
      onHide: () -> Unit,
      buttonText: String,
      content: @Composable ColumnScope.() -> Unit*/
): String {
    val filterOptions = listOf("Monthly", "Yearly", "Lifetime")
    val sheetState = rememberModalBottomSheetState()
    var selectedValue by remember { mutableStateOf(filterValue) }
    var selectedOption by remember { mutableStateOf(filterOptions[0]) }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, start = 15.dp, end = 15.dp, bottom = 20.dp),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(id = R.string.txt_time_period),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
            )

            Column(
                modifier = Modifier.padding(vertical = 15.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { text ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (text == selectedOption), onClick = {
                                    selectedOption = text
                                    selectedValue = text
                                    onValueSelected(selectedValue)
                                    onDismiss()
                                    Log.d("selectedOption:", "$selectedValue .. $selectedOption")
                                }, role = Role.RadioButton
                            ), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = text,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Start
                            )
                        )
                        RadioButton(
                            selected = (text == selectedOption), onClick = {
                                selectedOption = text
                                selectedValue = text
                                onValueSelected(selectedValue)
                                onDismiss()
                            }, // null recommended for accessibility with screenreaders
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PrimaryBlue, // Color when selected
                                unselectedColor = GrayLight02 // Color when not selected
                            )
                        )
                    }
                }
            }
        }
    }
    Log.d("selectedOption:Radio", "$selectedValue .. $selectedOption")
    return selectedValue
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun YearMonthGridPicker(
    selectedYear: Int, selectedMonth: Int, onYearMonthSelected: (Int, Int) -> Unit
) {
    var currentYear by remember { mutableIntStateOf(selectedYear) }
    var currentSelectedYear by remember { mutableIntStateOf(selectedYear) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()
    ) {

        YearSelector(currentYear = currentYear,
            currentSelectedYear = currentSelectedYear,
            onPreviousYear = { currentYear -= 1 },
            onNextYear = { currentYear += 1 })

        Spacer(modifier = Modifier.height(16.dp))

        onYearMonthSelected(currentYear, selectedMonth)

        MonthGridPicker(selectedMonth = selectedMonth, onMonthSelected = { month ->
            onYearMonthSelected(currentYear, month)
        })
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun YearMonthWalletGridPicker(
    selectedYear: Int, selectedMonth: Int, onYearMonthSelected: (Int, Int) -> Unit
) {
    var currentYear by remember { mutableIntStateOf(selectedYear) }
    var currentSelectedYear by remember { mutableIntStateOf(selectedYear) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()
    ) {
        YearSelector(currentYear = currentYear,
            currentSelectedYear = currentSelectedYear,
            onPreviousYear = { currentYear -= 1 },
            onNextYear = { currentYear += 1 })

        Spacer(modifier = Modifier.height(16.dp))

        onYearMonthSelected(currentYear, selectedMonth)

        MonthWalletGridPicker(selectedMonth = selectedMonth, onMonthSelected = { month ->
            onYearMonthSelected(currentYear, month)
        })
    }
}

@Composable
fun YearSelector(
    currentYear: Int,
    currentSelectedYear: Int,
    onPreviousYear: () -> Unit, onNextYear: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        IconButton(onClick = onPreviousYear) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_back_icon),
                contentDescription = "Previous Year"
            )
        }

        Text(
            text = currentYear.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold
        )

        IconButton(onClick = {
//            Log.d("CurrentYear:", "$currentYear .. $currentSelectedYear")
            if (currentYear < currentSelectedYear) {
                onNextYear()
            } else {
                onNextYear()
            }

        }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.right_arrow_icon),
                contentDescription = "Next Year"
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthGridPicker(
    selectedMonth: Int, onMonthSelected: (Int) -> Unit
) {
    val months = (1..12).toList()
    val columns = 3 // Display months in a 3-column grid

    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in months.chunked(columns)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (month in row) {
                    MonthItem(month, isSelected = month == selectedMonth) {
                        onMonthSelected(month)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthWalletGridPicker(
    selectedMonth: Int, onMonthSelected: (Int) -> Unit
) {
    val months = (1..12).toList()
    val columns = 4 // Display months in a 3-column grid

    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in months.chunked(columns)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (month in row) {
                    MonthItem(month, isSelected = month == selectedMonth) {
                        onMonthSelected(month)
                    }
                }
            }
        }
    }
}

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthItem(
    month: Int, isSelected: Boolean, onClick: () -> Unit
) {
    val monthName =
        YearMonth.of(0, month).month.name.lowercase().replaceFirstChar { it.uppercase() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(4.dp)
            .size(50.dp)
            .clip(CircleShape)
            .clickable(onClick = {
                onClick()
            })
            .then(
                if (isSelected) Modifier.background(PrimaryBlue) else Modifier.background(Color.White)
            )
    ) {
        Text(
            text = monthName.take(3), // Show first three letters of month name
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) White else Gray
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun ShowBottomSheetUI() {
    AuroscholarAppTheme {
//        var showFilterSheet by remember { mutableStateOf(false) }
//        var filter = monthYearFilterBottomSheet("filterValue") {
//            showFilterSheet = false
//        }
//        StudentHomePage(navController = rememberNavController(), hiltViewModel())
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun YearMonthPicker(
    selectedYear: Int, selectedMonth: Int, onYearMonthSelected: (Int, Int) -> Unit
) { val currentDate = LocalDate.now()
    val currentYear = currentDate.year
    val currentMonth = currentDate.monthValue

    var displayedYear by remember { mutableIntStateOf(selectedYear) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()
    ) {

        YearSelector(
            currentYear = displayedYear,
            onPreviousYear = { displayedYear -= 1 },
            onNextYear = { displayedYear += 1 }
        )

        Spacer(modifier = Modifier.height(16.dp))

        MonthGridPicker(
            selectedMonth = selectedMonth,
            displayedYear = displayedYear,
            currentYear = currentYear,
            currentMonth = currentMonth,
            onMonthSelected = { month ->
                onYearMonthSelected(displayedYear, month)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthGridPicker(
    selectedMonth: Int,
    displayedYear: Int,
    currentYear: Int,
    currentMonth: Int,
    onMonthSelected: (Int) -> Unit
) {
    val months = (1..12).toList()
    val columns = 3 // Display months in a 3-column grid

    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in months.chunked(columns)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (month in row) {
                    val isDisabled = (displayedYear > currentYear) ||
                            (displayedYear == currentYear && month >= currentMonth)
                    MonthItem(
                        month = month,
                        isSelected = month == selectedMonth,
                        isDisabled = isDisabled
                    ) {
                        if (!isDisabled) onMonthSelected(month)
                    }
                }
            }
        }
    }
}

@Composable
fun MonthItem(
    month: Int,
    isSelected: Boolean,
    isDisabled: Boolean,
    onClick: () -> Unit
) {
    val monthName =
        YearMonth.of(0, month).month.name.lowercase().replaceFirstChar { it.uppercase() }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(4.dp)
            .size(50.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) PrimaryBlue else if (isDisabled) GrayLight02 else White
            )
            .clickable(enabled = !isDisabled, onClick = onClick)
    ) {
        Text(
            text = monthName.take(3), // Show first three letters of month name,
            color = if (isDisabled || isSelected) White
            else GrayLight01,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun YearSelector(
    currentYear: Int,
    onPreviousYear: () -> Unit,
    onNextYear: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        IconButton(onClick = onPreviousYear) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_back_icon),
                contentDescription = "Previous Year"
            )
        }

        Text(
            text = currentYear.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onNextYear) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.right_arrow_icon),
                contentDescription = "Next Year"
            )
        }
    }
}
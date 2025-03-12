@file:OptIn(ExperimentalMaterial3Api::class)

package com.auro.application.ui.common_ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.datastore.preferences.protobuf.FieldMask
import coil.compose.rememberImagePainter
import com.auro.application.R
import com.auro.application.core.ConstantVariables.isLogout
import com.auro.application.ui.features.login.LoginMainActivity
import com.auro.application.ui.features.login.models.GetSubjectListResponseModel
import com.auro.application.ui.features.login.screens.otpTextField
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.theme.Bg_Gray
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.Gray
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.GrayLight03
import com.auro.application.ui.theme.LightRed01
import com.auro.application.ui.theme.PrimaryBlue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

@Composable
fun BtnUi(
    title: String = "Continue",
    onClick: () -> Unit,
    enabled: Boolean = false,
    cornerRadius: Dp = 10.dp,
    modifier: Modifier
) {
    Button(
        onClick = onClick, modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(
                color = if (enabled) PrimaryBlue else DarkGray,
                shape = RoundedCornerShape(cornerRadius)
            ), colors = ButtonDefaults.buttonColors(
            containerColor = Transparent, contentColor = White
        )
    ) {
        Text(title)
    }
}

@Preview
@Composable
fun BtnUi(
    title: String = "Continue", onClick: () -> Unit = {}, enabled: Boolean = false
) {
    Button(
        onClick = onClick, modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    5.dp
                )
            ),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) PrimaryBlue else GrayLight03,
            contentColor = if (enabled) {
                White
            } else {
                Gray
            }
        )
    ) {
        Text(
            title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = if (enabled) {
                White
            } else {
                Gray
            },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Btn12PXUi(
    title: String = "Continue", onClick: () -> Unit, enabled: Boolean = false
) {
    Button(
        onClick = onClick, modifier = Modifier
            .wrapContentSize()
            .padding(vertical = 10.dp)
            .clip(
                RoundedCornerShape(12.dp)
            ), colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue, contentColor = White
        )
    ) {
        Text(
            title,
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 5.dp, vertical = 10.dp),
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = White,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun show() {
    BtnUi(onClick = {})
    BtnNextUi(onClick = { })

}

@Preview
@Composable
fun BtnNextUi(
    title: String = "Next",
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier.padding() // Add modifier as a parameter
) {
    var clicked by remember { mutableStateOf(false) }

    Button(
        onClick = {
            onClick()
            clicked = !clicked
        }, modifier = modifier // Use the provided modifier
            .wrapContentWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp)
            .clip(RoundedCornerShape(4.dp)), colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) {
                if (clicked) PrimaryBlue else PrimaryBlue
            } else GrayLight02,
            contentColor = if (enabled) {
                if (clicked) White else White
            } else Gray,
        )
    ) {
        Text(
            title, style = TextStyle(
                textAlign = TextAlign.Center, // Center align the text
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
        )
    }
}

@Preview
@Composable
fun RectangleBtnUi(
    title: String = "Next",
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier.padding() // Add modifier as a parameter
) {
    var clicked by remember { mutableStateOf(false) }

    Button(
        onClick = {
            onClick()
            clicked = !clicked
        },
        modifier = modifier // Use the provided modifier
            .wrapContentWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) {
                if (clicked) PrimaryBlue else PrimaryBlue
            } else GrayLight02,
            contentColor = if (enabled) {
                if (clicked) White else White
            } else PrimaryBlue,
        )
    ) {
        Text(
            title, style = TextStyle(
                textAlign = TextAlign.Center, // Center align the text
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
        )
    }
}

@Preview
@Composable
fun RectangleAddBtnUi(
    title: String = "Next",
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier.padding() // Add modifier as a parameter
) {
    var clicked by remember { mutableStateOf(false) }

    Button(
        onClick = {
            onClick()
            clicked = !clicked
        },
        modifier = modifier // Use the provided modifier
            .wrapContentWidth()
            .padding(vertical = 5.dp, horizontal = 20.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) {
                if (clicked) PrimaryBlue else PrimaryBlue
            } else GrayLight02,
            contentColor = if (enabled) {
                if (clicked) White else White
            } else PrimaryBlue,
        )
    ) {
        Text(
            title, style = TextStyle(
                textAlign = TextAlign.Center, // Center align the text
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
        )
    }
}

@Preview
@Composable
fun RectangleBtnUi1(
    title: String = "Next",
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier.padding() // Add modifier as a parameter
) {
    var clicked by remember { mutableStateOf(false) }

    Button(
        onClick = {
            onClick()
            clicked = !clicked
        },
        modifier = modifier // Use the provided modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 10.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) {
                if (clicked) PrimaryBlue else PrimaryBlue
            } else GrayLight02,
            contentColor = if (enabled) {
                if (clicked) White else White
            } else PrimaryBlue,
        )
    ) {
        Text(
            title, style = TextStyle(
                textAlign = TextAlign.Center, // Center align the text
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
        )
    }
}

@Preview
@Composable
fun RectangleBtnPrev(
    title: String = "Previous",
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier.padding() // Add modifier as a parameter
) {
    var clicked by remember { mutableStateOf(false) }

    Button(
        onClick = {
            onClick()
            clicked = !clicked
        },
        modifier = modifier // Use the provided modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 10.dp, start = 20.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Bg_Gray,
            contentColor = if (enabled) {
                PrimaryBlue
            } else GrayLight01,
        )
    ) {
        Text(
            title, style = TextStyle(
                textAlign = TextAlign.Center, // Center align the text
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
        )
    }
}

@Composable
fun BtnTextUi(
    title: String = "", color: Color = Color.Black, onClick: () -> Unit, enabled: Boolean = true
) {
    var clicked by remember { mutableStateOf(false) }

    TextButton(
        onClick = {
            onClick()
            clicked = !clicked
        },
        modifier = Modifier
            .wrapContentSize()
            .padding(vertical = 10.dp, horizontal = 20.dp)
            .clip(
                RoundedCornerShape(4.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) {
                if (clicked) Transparent else Color.Transparent // Set background to transparent
            } else Color.Transparent,
            contentColor = if (enabled) {
                if (clicked) White else Gray
            } else PrimaryBlue,
        )
    ) {
        if (!title.isNullOrEmpty()) {
            Text(
                title, color = color, style = TextStyle(
                    textAlign = TextAlign.Center, // Center align the text
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
            )
        }

    }
}

@Composable
@Preview(showBackground = true)
fun Show() {
    DefaultBackgroundUi(modifier = Modifier) {}
}

@Preview
@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    password: MutableState<String> = remember { mutableStateOf("") },
    showPassword: MutableState<Boolean> = remember { mutableStateOf(false) },
    hint: String = remember { mutableStateOf("") }.toString()
) {
    TextField(
        value = password.value,
        onValueChange = { password.value = it },
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .wrapContentHeight()
            .border(
                width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(8.dp)
            ),
        textStyle = TextStyle(
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Black
        ),
        placeholder = { Text(hint, color = GrayLight01, fontSize = 13.sp) },
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = com.auro.application.ui.theme.White,
            focusedTextColor = Black,
            unfocusedTextColor = White,
            focusedIndicatorColor = White,
            unfocusedContainerColor = White,
            unfocusedIndicatorColor = White
        ),
        trailingIcon = {
            IconButton(onClick = { showPassword.value = !showPassword.value }) {
                Icon(
                    imageVector = if (showPassword.value) ImageVector.vectorResource(id = R.drawable.eye_open) else ImageVector.vectorResource(
                        id = R.drawable.eye_close
                    ), contentDescription = "Show/Hide Password"
                )
            }
        },
        visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation('*')
    )
}

@Composable
fun PinInputUi(
    modifier: Modifier = Modifier,
    password: MutableState<String> = remember { mutableStateOf("") },
    showPassword: MutableState<Boolean> = remember { mutableStateOf(false) },
    isError: MutableState<Boolean> = remember { mutableStateOf(false) },
    maxInputLength: Int = 4,
    enterPIN: String
) {
    TextField(value = password.value,
        onValueChange = {
            if (it.length <= maxInputLength) {
                password.value = it
            }
        },
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .border(
                width = 1.dp,
                color = if (isError.value) Color.Red else GrayLight01,
                shape = RoundedCornerShape(8.dp)
            ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = com.auro.application.ui.theme.White,
            focusedTextColor = com.auro.application.ui.theme.Black,
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = White
        ),
        trailingIcon = {
            IconButton(onClick = { showPassword.value = !showPassword.value }) {
                Icon(
                    imageVector = if (showPassword.value) ImageVector.vectorResource(id = R.drawable.eye_open) else ImageVector.vectorResource(
                        id = R.drawable.eye_close
                    ), contentDescription = "Show/Hide Password"
                )
            }
        },
        visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation('*'),
        placeholder = {
            Text(
                text = enterPIN, color = Color.Gray
            )
        })
}

@Composable
fun MobileTextField(
    trueFalse: Boolean,
    modifier: Modifier = Modifier,
    number: MutableState<String> = remember { mutableStateOf("") },
    hint: String = remember { mutableStateOf("") }.toString()
) {
    TextField(
        value = number.value,
        onValueChange = {
            if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                number.value = it
            }
        },
        enabled = trueFalse,
        placeholder = { Text(hint, color = GrayLight01, fontSize = 13.sp) },
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        visualTransformation = VisualTransformation.None,

        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(8.dp)
            ),
        textStyle = TextStyle(
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Black
        ),
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = White,
            focusedTextColor = Gray,
            unfocusedTextColor = Black,
            focusedIndicatorColor = Transparent,
            unfocusedContainerColor = White,
            unfocusedIndicatorColor = Transparent,
            disabledContainerColor = GrayLight03
        )
    )
}

@Composable
fun OtpInputField(

    modifier: Modifier = Modifier,
    otpText: String,
    otpLength: Int = 6,
    shouldShowCursor: Boolean = false,
    shouldCursorBlink: Boolean = false,
    onOtpModified: (String, Boolean) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val textState = remember { mutableStateOf(TextFieldValue(otpText)) }
    val focusRequester = remember { FocusRequester() }
//    val maskedOtp = otpText.map { '*' }.joinToString("")

    // Request focus when the Composable enters the composition
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Update text state when otpText changes
    LaunchedEffect(textState) {
        if (otpText.length <= otpLength) {
            textState.value = TextFieldValue(otpText, selection = TextRange(otpText.length))

        }
    }

    BasicTextField(value = textState.value,
        onValueChange = {
            if (it.text.length <= otpLength) {
                textState.value = it
                onOtpModified(it.text, it.text.length == otpLength)
                if (it.text.length == otpLength) {
                    keyboardController?.hide()
                }
            }
        },
        visualTransformation = if (otpText.length > 0) VisualTransformation.None else PasswordVisualTransformation('*'),

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .focusRequester(focusRequester),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()
            ) {
                repeat(otpLength) { index ->    // repeating box 6 times for otp & 4 times for PIN
                    CharacterContainer(
                        index = index,
                        text = textState.value.text,
                        shouldShowCursor = shouldShowCursor,
                        shouldCursorBlink = shouldCursorBlink,
                    )
//                    Spacer(modifier = Modifier.width(5.dp))
                }
            }
        })
}

@Composable
internal fun CharacterContainer(
    index: Int,
    text: String,
    shouldShowCursor: Boolean,
    shouldCursorBlink: Boolean,
) {
    val isFocused = text.length == index
    val character = when {
        index < text.length -> text[index].toString()
        else -> ""
    }

    // Cursor visibility state
    val cursorVisible = remember { mutableStateOf(false) }

    // Blinking effect for the cursor
    LaunchedEffect(key1 = isFocused) {
        if (isFocused && shouldShowCursor && shouldCursorBlink) {
            cursorVisible.value = true
            while (true) {
                delay(800) // Adjust the blinking speed here
                cursorVisible.value = !cursorVisible.value
            }
        } else {
            cursorVisible.value = false
        }
    }

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(45.dp) // Ensure this is wide enough
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) PrimaryBlue else GrayLight02,
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(2.dp)
        ) {
            Text(
                text = character,
                style = MaterialTheme.typography.headlineMedium,
                color = if (isFocused) PrimaryBlue else Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center) // Center the text within the Box
            )
        }


        // Display cursor when focused
        AnimatedVisibility(visible = isFocused && cursorVisible.value) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(2.dp)
                    .height(24.dp) // Adjust height according to your design
                    .background(GrayLight02)
            )
        }
    }
}


@Composable
fun DefaultBackgroundUi(
    modifier: Modifier = Modifier,
    isShowBackButton: Boolean = true,
    onBackButtonClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(), color = White
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = PrimaryBlue),
            verticalArrangement = Arrangement.Top
        ) {
            // Top 20% for background image
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Image(
                    modifier = modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .alpha(0.8F),
                    painter = painterResource(id = R.drawable.background),
                    contentDescription = "Background image",
                    contentScale = ContentScale.FillWidth
                )
                Image(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(100.dp), // adjust the size as needed
                    painter = painterResource(id = R.drawable.logo), contentDescription = "Logo"
                )
                if (isShowBackButton) {
                    Image(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(y = 45.dp, x = 10.dp)
                            .size(30.dp) // adjust the size as needed
                            .clickable(onClick = onBackButtonClick),
                        painter = painterResource(id = R.drawable.back_icon),
                        contentDescription = "back_button"
                    )
                }
            }

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                        White, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
            ) {
                Column(
                    modifier = modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun ChildListBackgroundUi(
    modifier: Modifier = Modifier,
    isShowBackButton: Boolean = true,
    onBackButtonClick: () -> Unit = {},
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(), color = White
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = PrimaryBlue),
            verticalArrangement = Arrangement.Top
        ) {
            // Top 20% for background image
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Image(
                    modifier = modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .alpha(0.8F),
                    painter = painterResource(id = R.drawable.background),
                    contentDescription = "Background image",
                    contentScale = ContentScale.FillWidth
                )
                Image(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(100.dp), // adjust the size as needed
                    painter = painterResource(id = R.drawable.logo), contentDescription = "Logo"
                )
                if (isShowBackButton) {
                    Image(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(y = 45.dp, x = 10.dp)
                            .size(30.dp) // adjust the size as needed
                            .clickable(onClick = onBackButtonClick),
                        painter = painterResource(id = R.drawable.back_icon),
                        contentDescription = "back_button"
                    )
                }

                Column(
                    modifier = modifier
                        .wrapContentSize()
                        .align(Alignment.TopEnd)
                        .clickable {
                            onClick()
                        },
                    horizontalAlignment = Alignment.End
                ) {
                    Image(
                        modifier = Modifier
                            .padding(top = 45.dp, start = 15.dp, end = 25.dp)
                            .size(20.dp), // adjust the size as needed
                        painter = painterResource(id = R.drawable.ic_logout),
                        colorFilter = ColorFilter.tint(White),
                        contentDescription = "Logout"
                    )

                    Text(
                        text = stringResource(id = R.string.txt_logout),
                        modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        fontSize = 14.sp
                    )
                }
            }

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                        White, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
            ) {
                Column(
                    modifier = modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    content()
                }
            }
        }
    }
}

/*@Preview
@Composable
fun CircleCheckbox(selected: Boolean = true, enabled: Boolean = true, onChecked: () -> Unit = {}) {
    IconButton(
        onClick = { onChecked() },
        modifier = Modifier
            .offset(x = 4.dp, y = 4.dp)
            .border(width = 1.dp, color = GrayLight01, shape = CircleShape)
            .padding(end = 5.dp),
        enabled = enabled,
    ) {
        Image(
            modifier = Modifier
                .size(30.dp) // Use size to set both width and height
                .padding(start = 8.dp, end = 5.dp)
                .clip(CircleShape), // Clip to a circle
            painter = if (selected) painterResource(R.drawable.check_box) else painterResource(R.drawable.unchecked),
            contentDescription = "Logo",
            contentScale = ContentScale.FillBounds // Use Crop to fill the circle
        )
    }

}*//*@Preview
@Composable
fun CircleCheckbox(selected: Boolean = false, enabled: Boolean = true, onChecked: () -> Unit = {}) {
    IconButton(
        onClick = { onChecked() },
        modifier = Modifier
            .offset(x = 4.dp, y = 4.dp)
            .border(width = 1.dp, color = GrayLight01, shape = CircleShape)
            .padding(end = 5.dp),
        enabled = enabled,
    ) {
        Box(
            modifier = Modifier
                .size(50.dp) // Set both width and height to 30.dp
                .clip(CircleShape), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = if (selected) painterResource(R.drawable.check_box) else painterResource(R.drawable.unchecked),
                contentDescription = "Logo",
                contentScale = ContentScale.Crop, // Stretch the image to fill the bounds
                modifier = Modifier.fillMaxSize().align(Alignment.Center),
            )
        }
    }
}*/
@Preview
@Composable
fun CircleCheckbox(
    selected: Boolean = true,
    enabled: Boolean = true,
    onChecked: () -> Unit = {},
    modifier: Modifier = Modifier.padding(),
    activeIcon: Painter = painterResource(R.drawable.check_box),
    inActiveIcon: Painter = painterResource(R.drawable.unchecked)
) {
    IconButton(
        onClick = { onChecked() },
        modifier = modifier
            .background(Color.Unspecified)
            .size(20.dp) // Set a fixed size for the IconButton to ensure centering
            .border(
                width = 1.dp,
                color = if (selected) PrimaryBlue else GrayLight01,
                shape = CircleShape
            )
            .padding(0.dp), // Remove padding to keep the button centered
        enabled = enabled,
    ) {
        Box(
            modifier = modifier
                .fillMaxSize() // Fill the IconButton size
                .clip(CircleShape),
            contentAlignment = Alignment.Center // Center the content within the Box
        ) {
            Image(
                painter = if (selected) activeIcon else inActiveIcon,
                contentDescription = "Checkbox",
                contentScale = ContentScale.Crop, // Stretch the image to fill the bounds
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Unspecified), // Ensure the image fills the Box
            )
        }
    }
}

@Composable
fun RectangleCheckbox(
    selected: Boolean,
    enabled: Boolean = true,
    onChecked: () -> Unit,
) {
    val image = painterResource(R.drawable.ic_ractanglebox_active)

    Box(
        modifier = Modifier
            .clickable(enabled = enabled, onClick = { onChecked() })
            .padding(8.dp)
            .size(width = 30.dp, height = 30.dp)
            .background(
                color = if (selected) PrimaryBlue else Color.White,
                shape = RoundedCornerShape(4.dp),
            )
            .border(
                width = 1.dp,
                color = if (selected) PrimaryBlue else GrayLight02,
                shape = RoundedCornerShape(4.dp)
            ), contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (selected) {
                Image(
                    painter = image, contentDescription = "Check", Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun RectangleQuizCheckbox(
    isDisabled: Boolean,
    selected: Boolean,
    enabled: Boolean = true,
    onChecked: () -> Unit,
) {
    val image = painterResource(R.drawable.ic_ractanglebox_active)
    val inactiveImage = painterResource(R.drawable.ic_ractanglebox_inactive)

    Box(
        modifier = Modifier
            .clickable(enabled = enabled, onClick = { onChecked() })
            .padding(8.dp)
            .size(width = 30.dp, height = 30.dp)
            .background(
                color = if (selected) PrimaryBlue else Color.White,
                shape = RoundedCornerShape(4.dp),
            )
            .border(
                width = 1.dp,
                color = if (selected) PrimaryBlue else GrayLight02,
                shape = RoundedCornerShape(4.dp)
            ), contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (selected) {
                Image(
                    painter = if (isDisabled) {
                        inactiveImage
                    } else {
                        image
                    },
                    contentDescription = "Check", Modifier.fillMaxSize(),
                )
            }
        }
    }
}


@Composable
fun GenderSelection(
    modifier: Modifier = Modifier,
    items: List<String>,
    selectedItem: MutableState<String> = remember { mutableStateOf("") },
    onItemSelected: (String) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), modifier = modifier.height(100.dp) // Set a bounded height
    ) {
        items(items = items) { item ->
            CircleCheckboxItem(item = item, isSelected = item == selectedItem.value, onClick = {
                selectedItem.value = item
                onItemSelected(item)
            })
        }
    }
}

@Composable
fun GenderEditSelection(
    modifier: Modifier = Modifier,
    items: List<String>,
    selectedItem: MutableState<String> = remember { mutableStateOf("") },
    disableEnable: Boolean,
    onItemSelected: (String) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), modifier = modifier.height(100.dp) // Set a bounded height
    ) {
        items(items = items) { item ->
            CircleCheckboxItem(item = item, isSelected = item == selectedItem.value, onClick = {
                if (disableEnable) {
                    selectedItem.value = item
                    onItemSelected(item)
                }
            })
        }
    }
}

@Composable
fun CircleCheckboxItem(
    item: String, isSelected: Boolean, onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {

        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color.White, shape = CircleShape)
                .border(
                    width = 2.dp,
                    color = if (isSelected) PrimaryBlue else Color.Gray,
                    shape = CircleShape
                )
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Checked",
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.Center),
                    tint = PrimaryBlue
                )
            }
        }

        Text(
            text = item,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f), // Make the text take up the remaining space,
            color = Black,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )

    }
}

@Composable
fun DropdownMenuUi(
    options: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select",
    icon: Painter = painterResource(R.drawable.ic_down),
    onClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier
            .border(
                width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .clickable {
                if (options.isNotEmpty()) menuExpanded = true
                onClick()
            }
            .padding(vertical = 15.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = selectedOption ?: placeholder,
                modifier = Modifier.weight(1f),
                color = Black,
                fontSize = 14.sp,
                fontStyle = FontStyle.Normal,
                fontFamily = FontFamily(
                    Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 7.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = GrayLight01)
                .background(Color.White)
        ) {
            options.forEach { option ->
                androidx.compose.material.DropdownMenuItem(onClick = {
                    onItemSelected(option)
                    selectedOption = option
                    menuExpanded = false
                }) {
                    Text(
                        text = option,
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold
                    )
                }/* DropdownMenuItem(
                     onClick = {
                         onItemSelected(option)
                         selectedOption = option
                         menuExpanded = false
                     }
                 ) {
                     Text(
                         text = option,
                         color = Color.Gray,
                         fontSize = 16.sp,
                         fontStyle = FontStyle.Normal,
                         fontWeight = FontWeight.SemiBold
                     )
                 }*/
            }
        }
    }
    // Update the selectedOption when options list is cleared
    LaunchedEffect(options) {
        if (options.isEmpty()) {
            selectedOption = null
        }
    }
}

@Composable
fun DropdownSchoolMenuUi(
    options: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select",
    icon: Painter = painterResource(R.drawable.ic_down),
    onClick: () -> Unit,
    disableEnable: Boolean
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier
            .border(
                width = 1.dp, color = if (disableEnable) {
                    GrayLight02
                } else {
                    GrayLight03
                }, shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .clickable {
                if (disableEnable) {
                    if (options.isNotEmpty()) menuExpanded = true
                    onClick()
                }
//                if (options.isNotEmpty()) menuExpanded = true
//                onClick()
            }
            .padding(vertical = 15.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = selectedOption ?: placeholder,
                modifier = Modifier.weight(1f),
                color = Color.Gray,
                fontSize = 16.sp,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 7.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = GrayLight01)
                .background(Color.White)
        ) {
            options.forEach { option ->
                androidx.compose.material.DropdownMenuItem(onClick = {
                    onItemSelected(option)
                    selectedOption = option
                    menuExpanded = false
                }) {
                    Text(
                        text = option,
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold
                    )
                }/* DropdownMenuItem(
                     onClick = {
                         onItemSelected(option)
                         selectedOption = option
                         menuExpanded = false
                     }
                 ) {
                     Text(
                         text = option,
                         color = Color.Gray,
                         fontSize = 16.sp,
                         fontStyle = FontStyle.Normal,
                         fontWeight = FontWeight.SemiBold
                     )
                 }*/
            }
        }
    }
    // Update the selectedOption when options list is cleared
    LaunchedEffect(options) {
        if (options.isEmpty()) {
            selectedOption = null
        }
    }
}

@Composable
fun DropdownStateMenuUi(
    options: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select",
    icon: Painter = painterResource(R.drawable.ic_down),
    onClick: () -> Unit,
    disableEnable: Boolean
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier
            .border(
                width = 1.dp, color = if (disableEnable) {
                    GrayLight02
                } else {
                    GrayLight03
                }, shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .clickable {
                if (disableEnable) {
                    if (options.isNotEmpty()) menuExpanded = true
                    onClick()
                }

//                if (options.isNotEmpty()) menuExpanded = true
//                onClick()
            }
            .padding(vertical = 15.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = selectedOption ?: placeholder,
                modifier = Modifier.weight(1f),
                color = Color.Gray,
                fontSize = 16.sp,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 7.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = GrayLight01)
                .background(Color.White)
        ) {
            options.forEach { option ->
                androidx.compose.material.DropdownMenuItem(onClick = {
                    onItemSelected(option)
                    selectedOption = option
                    menuExpanded = false
                }) {
                    Text(
                        text = option,
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold
                    )
                }/* DropdownMenuItem(
                     onClick = {
                         onItemSelected(option)
                         selectedOption = option
                         menuExpanded = false
                     }
                 ) {
                     Text(
                         text = option,
                         color = Color.Gray,
                         fontSize = 16.sp,
                         fontStyle = FontStyle.Normal,
                         fontWeight = FontWeight.SemiBold
                     )
                 }*/
            }
        }
    }
    // Update the selectedOption when options list is cleared
    LaunchedEffect(options) {
        if (options.isEmpty()) {
            selectedOption = null
        }
    }
}

@Composable
fun DropdownStudentGradeUi(
    options: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select",
    icon: Painter = painterResource(R.drawable.ic_down),
    onClick: () -> Unit,
    disableEnable: Boolean
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier
            .border(
                width = 1.dp, color = if (disableEnable) {
                    GrayLight02
                } else {
                    GrayLight03
                }, shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .clickable {
                if (disableEnable) {
                    if (options.isNotEmpty()) menuExpanded = true
                    onClick()
                }
            }
            .padding(vertical = 15.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = selectedOption ?: placeholder,
                modifier = Modifier.weight(1f),
                color = Color.Gray,
                fontSize = 16.sp,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 7.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = GrayLight01)
                .background(Color.White)
        ) {
            options.forEach { option ->
                androidx.compose.material.DropdownMenuItem(onClick = {
                    onItemSelected(option)
                    selectedOption = option
                    menuExpanded = false
                }) {
                    Text(
                        text = option,
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
    // Update the selectedOption when options list is cleared
    LaunchedEffect(options) {
        if (options.isEmpty()) {
            selectedOption = null
        }
    }
}

@Composable
fun DropdownStudentSubjectUi(
    options: List<GetSubjectListResponseModel.Data?>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select",
    icon: Painter = painterResource(R.drawable.ic_down),
    onClick: () -> Unit,
    disableEnable: Boolean
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier
            .border(
                width = 1.dp, color = if (disableEnable) {
                    GrayLight02
                } else {
                    GrayLight03
                }, shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(vertical = 15.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = selectedOption ?: placeholder,
                modifier = Modifier.weight(1f),
                color = Color.Gray,
                fontSize = 16.sp,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 7.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
    // Update the selectedOption when options list is cleared
    LaunchedEffect(options) {
        if (options.isEmpty()) {
            selectedOption = null
        }
    }
}

@Composable
fun InputTextField(
    modifier: Modifier = Modifier,
    value: MutableState<String> = remember { mutableStateOf("") },
    placeholder: String = "Enter Here",
    editable: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text, // Add a parameter for the keyboard type,
    maxLength: Int = Int.MAX_VALUE // Add a parameter for the maximum length
) {
    TextField(
        value = value.value,
        onValueChange = when (placeholder) {
            "Enter Beneficiary Name" -> {
                { newValue ->
                    if (newValue.length <= 35 && newValue.all { char -> !char.isDigit() }) {
                        value.value = newValue
                    }
                }
            }

            "Enter Account Number" -> {
                { newValue ->
                    if (newValue.length <= 18 && newValue.all { char -> char.isDigit() }) {
                        value.value = newValue
                    }
                }
            }

            "Enter Confirm Account Number" -> {
                { newValue ->
                    if (newValue.length <= 18 && newValue.all { char -> char.isDigit() }) {
                        value.value = newValue
                    }
                }
            }

            "Enter IFSC Code" -> {
                { newValue ->
                    if (newValue.length <= 11 && newValue.all { char -> char.isLetterOrDigit() }) {
                        value.value = newValue
                    }
                }
            }

            else -> {
                { newValue ->
                    if (newValue.length <= maxLength) { // Check if the new value is within the limit
                        value.value = newValue
                    }
                }
            }
        }, // Update the value state here
        shape = RoundedCornerShape(8.dp),
        enabled = editable,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType), // Use the keyboardType parameter
        visualTransformation = VisualTransformation.None,
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray,
                fontSize = 14.sp,
                fontFamily = FontFamily(
                    Font(R.font.inter_regular, FontWeight.Normal)
                )
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(8.dp)
            ),
        textStyle = TextStyle(
            color = Black,
            fontSize = 14.sp,
            fontStyle = FontStyle.Normal,
            fontFamily = FontFamily(
                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
            )
        ),
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = White,
            focusedTextColor = Gray,
            unfocusedTextColor = Black,
            focusedIndicatorColor = Transparent,
            unfocusedContainerColor = White,
            unfocusedIndicatorColor = Transparent,
            disabledContainerColor = GrayLight03
        )
    )
}

@Composable
fun DisabledInputTextField(
    isEnable: Boolean,
    modifier: Modifier = Modifier,
    value: MutableState<String> = remember { mutableStateOf("") },
    placeholder: String = "Enter Here",
    keyboardType: KeyboardType = KeyboardType.Text, // Add a parameter for the keyboard type,
    maxLength: Int = Int.MAX_VALUE
) {
    TextField(
        value = value.value,
        onValueChange = { newValue ->
            if (newValue.length <= maxLength) { // Check if the new value is within the limit
                value.value = newValue
            }
        }, // Update the value state here
        shape = RoundedCornerShape(8.dp),
        enabled = isEnable,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType), // Use the keyboardType parameter
        visualTransformation = VisualTransformation.None,
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray,
                fontSize = 12.sp,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Normal
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(8.dp)
            ),
        textStyle = TextStyle(
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Gray
        ),
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = Color.White,
            focusedTextColor = Gray,
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}


@Composable
fun InputTextFieldWithError(
    modifier: Modifier = Modifier,
    value: MutableState<String> = remember { mutableStateOf("") },
    placeholder: String = "Enter Here",
    editable: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxChars: Int = Int.MAX_VALUE, // Parameter for max character limit
    errorMessage: String = "Maximum character limit exceeded!"
) {
    // State to track if the input exceeds max characters
    val isError = remember { mutableStateOf(false) }

    // Update the value state and check for max character limit
    val onValueChange: (String) -> Unit = { newValue ->
        if (newValue.length <= maxChars) {
            value.value = newValue
            isError.value = false // Reset error state if within limit
        } else {
            isError.value = true // Set error state if exceeded
        }
    }

    Column(modifier = modifier) {
        TextField(
            value = value.value,
            onValueChange = onValueChange,
            shape = RoundedCornerShape(8.dp),
            enabled = editable,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
            visualTransformation = VisualTransformation.None,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(
                    width = 1.dp,
//                    color = if (isError.value) Color.Red else GrayLight02, // Change border color based on error state
                    color = if (isError.value) GrayLight02 else GrayLight02, // Change border color based on error state
                    shape = RoundedCornerShape(8.dp)
                ),
            textStyle = TextStyle(
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Gray
            ),
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = Color.White,
                focusedTextColor = Gray,
                unfocusedTextColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        // Error message display
//        if (isError.value) {
//            Text(
//                text = errorMessage,
//                color = Color.Red,
//                fontSize = 12.sp,
//                modifier = Modifier.padding(top = 4.dp)
//            )
//        }
    }
}

@Preview
@Composable
fun ProgressBarCompose(progress: Int = 5) {
    // Ensure progress is within the valid range of 0 to 10
    val normalizedProgress = remember { mutableStateOf(progress.coerceIn(0, 10) / 10f) }

    // Update the normalized progress based on the input progress value
    LaunchedEffect(progress) {
        normalizedProgress.value = progress.coerceIn(0, 10) / 10f
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // First Circle
        Text(
            text = "1",
            modifier = Modifier
                .background(shape = CircleShape, color = PrimaryBlue)
                .padding(5.dp)
                .size(25.dp)
                .wrapContentSize(Alignment.Center),
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Progress Bar
        LinearProgressIndicator(
            progress = normalizedProgress.value,
            modifier = Modifier
                .padding(5.dp)
                .width(150.dp),
            color = PrimaryBlue,
        )

        // Second Circle
        Text(
            text = "2",
            modifier = Modifier
                .background(
                    shape = CircleShape, color = if (normalizedProgress.value < 0.7f) {
                        GrayLight01
                    } else {
                        PrimaryBlue
                    }
                )
                .padding(5.dp)
                .size(25.dp)
                .wrapContentSize(Alignment.Center),
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun TextFieldWithIcon(
    modifier: Modifier = Modifier,
    value: MutableState<String> = remember { mutableStateOf("") },
    placeholder: String = "Enter Here"
) {
    Row(
        modifier = modifier
            .border(
                width = 1.dp, color = GrayLight02, shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically

    ) {
        Text(
            text = placeholder, modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(10.dp),
            color = Black,
            fontSize = 14.sp,
            fontStyle = FontStyle.Normal,
            fontFamily = FontFamily(
                Font(R.font.inter_semi_bold, FontWeight.SemiBold)
            )

        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = painterResource(R.drawable.ic_calender),
            contentDescription = "data is here",
            modifier = Modifier
                .size(24.dp)
                .padding(end = 7.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

fun showDatePickerDialog(
    context: Context, onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit
) {
    val calendar = Calendar.getInstance()
    val fiveYearsAgoCalendar = Calendar.getInstance().apply {
        add(Calendar.YEAR, -4)
    }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) // This is 0-based
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    // Set the maximum date to today
//    val maxDate = calendar.timeInMillis
    val maxDate = fiveYearsAgoCalendar.timeInMillis

    val datePickerDialog = DatePickerDialog(
        context, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            // Adjust the month by adding 1 to convert from 0-based to 1-based
            val mon = selectedMonth
            onDateSelected(selectedYear, mon + 1, selectedDayOfMonth)
        }, year, month, dayOfMonth
        // Set the background color of the date picker dialog
    ).apply {
        datePicker.maxDate = maxDate
//        datePicker.minDate = calendar.timeInMillis
    }
    // Set the maximum date to prevent future date selection
//    datePickerDialog.datePicker.maxDate = maxDate
    datePickerDialog.show()
}

fun showParentDatePickerDialog(
    context: Context, onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit,
) {
    val calendar = Calendar.getInstance()
    val fiveYearsAgoCalendar = Calendar.getInstance().apply {
        add(Calendar.YEAR, -18)
    }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) // This is 0-based
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    // Set the maximum date to today
//    val maxDate = calendar.timeInMillis
    val maxDate = fiveYearsAgoCalendar.timeInMillis

    val datePickerDialog = DatePickerDialog(
        context, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            // Adjust the month by adding 1 to convert from 0-based to 1-based
            val mon = selectedMonth
            onDateSelected(selectedYear, mon + 1, selectedDayOfMonth)
        }, year, month, dayOfMonth
        // Set the background color of the date picker dialog
    ).apply {
        datePicker.maxDate = maxDate
//        datePicker.minDate = calendar.timeInMillis
    }
    // Set the maximum date to prevent future date selection
//    datePickerDialog.datePicker.maxDate = maxDate
    datePickerDialog.show()
}
fun showWalletDatePickerDialog(
    context: Context, onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit
) {
    val calendar = Calendar.getInstance()
    val fiveYearsAgoCalendar = Calendar.getInstance().apply {
        add(Calendar.YEAR, -0)
    }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) // This is 0-based
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    // Set the maximum date to today
//    val maxDate = calendar.timeInMillis
    val maxDate = fiveYearsAgoCalendar.timeInMillis

    val datePickerDialog = DatePickerDialog(
        context, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            // Adjust the month by adding 1 to convert from 0-based to 1-based
            val mon = selectedMonth
            onDateSelected(selectedYear, mon + 1, selectedDayOfMonth)
        }, year, month, dayOfMonth
        // Set the background color of the date picker dialog
    ).apply {
        datePicker.maxDate = maxDate
//        datePicker.minDate = calendar.timeInMillis
    }
    // Set the maximum date to prevent future date selection
//    datePickerDialog.datePicker.maxDate = maxDate
    datePickerDialog.show()
}

@Composable
fun TextWithIconExample(
    textQuiz: String, icon: Painter, color: Color, size: TextUnit, onClick: () -> Unit
) {

//    val icon = painterResource(id = R.drawable.dash_help)
    val inlineContentId = "inlineIcon"
    val text = buildAnnotatedString {
        append(textQuiz)
        appendInlineContent(inlineContentId, "[icon]")
    }

    val inlineContent = mapOf(inlineContentId to InlineTextContent(
        placeholder = Placeholder(
            width = 20.sp,
            height = 20.sp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
        ),
    ) {
        Image(
            painter = icon,
            colorFilter = ColorFilter.tint(color),
            contentDescription = null,
            modifier = Modifier
                .size(25.dp)
                .padding(start = 5.dp)
        )
    })
    Text(
        modifier = Modifier
            .padding(start = 5.dp, top = 15.dp)
            .clickable {
                onClick.invoke()
            },
        text = text,
        inlineContent = inlineContent,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = color,
            fontSize = size,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start
        )
    )
}

@Composable
fun TextWithIconQuizExample(
    textQuiz: String, icon: Painter,
    color: Color, size: TextUnit,
    onClick: () -> Unit,
) {

    val inlineContentId = "inlineIcon"
    val text = buildAnnotatedString {
        append(textQuiz)
        appendInlineContent(inlineContentId, "[icon]")
    }

    val inlineContent = mapOf(inlineContentId to InlineTextContent(
        placeholder = Placeholder(
            width = 20.sp,
            height = 20.sp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
        ),
    ) {
        Image(
            painter = icon,
            colorFilter = ColorFilter.tint(color),
            contentDescription = null,
            modifier = Modifier
                .size(25.dp)
                .padding(start = 5.dp)
                .clickable { onClick() }
        )
    })
    Text(
        modifier = Modifier.padding(start = 5.dp, top = 15.dp),
        text = text,
        inlineContent = inlineContent,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = color,
            fontSize = size,
            fontFamily = FontFamily(
                Font(R.font.inter_bold, FontWeight.Bold)
            ),
            textAlign = TextAlign.Start
        )
    )
}


/**
// how to use this
var showBottomSheet by remember { mutableStateOf(false) }

if (showBottomSheet) {
BottomSheetContent(showBottomSheet, onHide = {},"ok Close it") { }
}

 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetAlert(
    show: Boolean,
    onHide: () -> Unit,
    buttonText: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        contentColor = com.auro.application.ui.theme.White, onDismissRequest = {
            onHide()
        }, sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, start = 15.dp, end = 15.dp)
        ) {
            content()
            if (!buttonText.isNullOrEmpty()) {
                BtnUi(buttonText, onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onHide()
                        }
                    }
                }, true)
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContentNew(
    sheetState: SheetState = rememberModalBottomSheetState(),
    show: Boolean,
    onHide: () -> Unit,
    buttonText: String,
    content: @Composable ColumnScope.(handleShowHideProgrammatically: String) -> Unit,
    onSheetHidden: () -> Unit,
    coroutineScope: CoroutineScope
) {
//    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = {
            onHide()
        }, sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            content(content.toString())
            if (buttonText.isNotEmpty()) {
                BtnUi(buttonText, onClick = {
                    coroutineScope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onHide()
                            onSheetHidden()
                        }
                    }
                }, true)
            }
        }
    }
}

//delete
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheetMenu(viewModel: LoginViewModel, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var logOutSheet by remember { mutableStateOf(false) }
    // show logout sheet while click on logout
    if (logOutSheet) {
        LogoutBottomSheetMenu(viewModel) {
            logOutSheet = false
        }
    }
    var confirmDeleteState by remember { mutableStateOf(false) }
    // show logout sheet while click on logout
    if (confirmDeleteState) {
        ConfirmationDeleteAccountBottomSheet() {
            confirmDeleteState = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 20.dp)
                .fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically // Vertically center items in the Row
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_delete), // Replace with your drawable resource
                    contentDescription = "delete", // Provide a description for accessibility purposes
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp),
                )

                TextButton(onClick = {
//                    onDismiss()
                    confirmDeleteState = true
                }) {
                    Text(
                        stringResource(id = R.string.txt_delete_account),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = LightRed01,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start
                        )
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically // Vertically center items in the Row
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logout), // Replace with your drawable resource
                    contentDescription = null, // Provide a description for accessibility purposes
                    modifier = Modifier
                        .size(24.dp)
                        .padding(8.dp),
                )
                TextButton(
                    onClick = {
//                        onDismiss()
                        logOutSheet = true
                    },
                ) {
                    Text(
                        stringResource(id = R.string.txt_logout),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoutBottomSheetMenu(viewModel: LoginViewModel, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 25.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items in the Column
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile_bg), // Replace with your drawable resource
                contentDescription = null, // Provide a description for accessibility purposes
                modifier = Modifier.wrapContentSize(),
            )
            TextButton(onClick = {

            }) {
                Text(
                    stringResource(id = R.string.txt_sure_logout),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                    )
                )
            }
            Divider(
                color = GrayLight02,
                thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TextButton(
                        onClick = {
                            onDismiss()
                        },
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 5.dp)
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            stringResource(id = R.string.txt_cancel),
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 5.dp)
                                .wrapContentWidth(),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = PrimaryBlue,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.SemiBold,
                            )
                        )
                    }
                    TextButton(
                        onClick = {
                            viewModel.saveScreenName(isLogout)
                            viewModel.clearPreferenceData(context)
                            onDismiss()
                            context.startActivity(Intent(context, LoginMainActivity::class.java))
                                .also {
                                    if (context is Activity) {
                                        context.finish()   // This will close the current Activity
                                    }
                                }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = White, containerColor = LightRed01
                        ),
                        modifier = Modifier
                            .padding(vertical = 5.dp, horizontal = 5.dp)
                            .fillMaxWidth()
                            .weight(1f),

                        ) {
                        Text(
                            stringResource(id = R.string.txt_logout),
                            modifier = Modifier.wrapContentWidth(),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = White,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.SemiBold,
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDeleteAccountBottomSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var verifyPasswordSheet by remember { mutableStateOf(false) }
    // show verify password sheet while click on logout
    if (verifyPasswordSheet) {
        VerifyPasswordBottomSheet() {
            verifyPasswordSheet = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items in the Column
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile_bg), // Replace with your drawable resource
                tint = Color.Unspecified,
                contentDescription = null,
                modifier = Modifier.wrapContentSize(),
            )

            Text(
                stringResource(id = R.string.txt_sure_delete_acc),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            Text(
                stringResource(id = R.string.txt_sure_delete_desc),
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Gray, fontSize = 14.sp, fontWeight = FontWeight.Normal
                ),
                textAlign = TextAlign.Center
            )

            Divider(
                color = GrayLight02,
                thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Box(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_cancel),
                            modifier = Modifier
                                .background(
                                    color = White, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = PrimaryBlue,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    TextButton(
                        onClick = {
                            onDismiss()
                            verifyPasswordSheet = true
                        }, modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_delete),
                            modifier = Modifier
                                .background(
                                    color = LightRed01, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyPasswordBottomSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val password = remember { mutableStateOf("") }
    val showPassword = remember { mutableStateOf(false) }
    val enterYourPassword = stringResource(id = R.string.enter_your_password)

    var verifyPinSheet by remember { mutableStateOf(false) }

    // show verify pin sheet while click on logout
    if (verifyPinSheet) {
        VerifyPinBottomSheet() {
            verifyPinSheet = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items in the Column
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_lock_bg), // Replace with your drawable resource
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.wrapContentSize(),
            )

            Text(
                stringResource(id = R.string.txt_verify_password),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            PasswordTextField(
                password = password, showPassword = showPassword, hint = enterYourPassword
            )

            Divider(
                color = GrayLight02,
                thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_cancel),
                            modifier = Modifier
                                .background(
                                    color = White, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = PrimaryBlue,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    TextButton(
                        onClick = {
//                            onDismiss()
                            verifyPinSheet = true
                        },

                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_confirm),
                            modifier = Modifier
                                .background(
                                    color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyPinBottomSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var isPinFilled by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var verifyOTPSheet by remember { mutableStateOf(false) }

    // show verify pin sheet while click on logout
    if (verifyOTPSheet) {
        VerifyOtpBottomSheet() {
            verifyOTPSheet = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items in the Column
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_lock_bg), // Replace with your drawable resource
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.wrapContentSize(),
            )

            Text(
                stringResource(id = R.string.txt_verify_pin),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            OtpInputField(otpLength = 4,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                otpText = "",
                shouldCursorBlink = false,
                onOtpModified = { value, otpFilled ->
                    isPinFilled = otpFilled
                    if (otpFilled) {
                        keyboardController?.hide()
                    }
                })

            Divider(
                color = GrayLight02,
                thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onDismiss() }, modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_cancel),
                            modifier = Modifier
                                .background(
                                    color = White, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = PrimaryBlue,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    TextButton(
                        onClick = {
//                        onDismiss()
                            verifyOTPSheet = true
                        },

                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_confirm),
                            modifier = Modifier
                                .background(
                                    color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyOtpBottomSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val invalidOtpText = stringResource(id = R.string.invalid_OTP)

    var otpValue by remember { mutableStateOf("") }
    var inValidOTP by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    var successSheet by remember { mutableStateOf(false) }

    // show Success sheet while click on logout
    if (successSheet) {
        SuccessBottomSheet() {
            successSheet = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items in the Column
        ) {
            /* Icon(
                 painter = painterResource(id = R.drawable.ic_lock_bg), // Replace with your drawable resource
                 contentDescription = null, // Provide a description for accessibility purposes
                 modifier = Modifier.wrapContentSize(),
             )*/

            Text(
                stringResource(id = R.string.text_enter_otp),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                stringResource(id = R.string.txt_otp_has_been_sent),
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Gray,
                    fontSize = 14.sp,
                ),
                textAlign = TextAlign.Center
            )
            Text(
                stringResource(id = R.string.demo_no),
                modifier = Modifier.padding(bottom = 10.dp),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Black,
                    fontSize = 14.sp,
                ),
                textAlign = TextAlign.Center
            )

            otpValue = otpTextField(/*loginViewModel*/)  // inside VerifyOTScreen
            if (inValidOTP) {
                Text(
                    invalidOtpText,
                    color = LightRed01,
                    modifier = Modifier.padding(start = 10.dp),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp
                )
            }

            Divider(
                color = GrayLight02,
                thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onDismiss() }, modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.txt_cancel),
                            modifier = Modifier.background(
                                color = Transparent, shape = RoundedCornerShape(12.dp)
                            ),
                            color = PrimaryBlue,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    TextButton(
                        onClick = {
                            showError = otpValue.isEmpty()
                            if (showError || otpValue.length < 6) {
                                inValidOTP = true
                            } else {
//                            onDismiss
                                inValidOTP = false
                                successSheet = true
                            }
                        },

                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.text_verify_otp),
                            modifier = Modifier
                                .background(
                                    color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            color = White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessBottomSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally // Horizontally center items in the Column
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_success), // Replace with your drawable resource
                contentDescription = null, // Provide a description for accessibility purposes
                tint = Color.Unspecified,
                modifier = Modifier.wrapContentSize(),
            )

            Text(
                "Account Successfully Deleted!",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                "You can stop the deletion process by logging back in before April 30, 2024",
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Gray,
                    fontSize = 14.sp,
                ),
                textAlign = TextAlign.Center
            )
            TextButton(
                onClick = {
                    onDismiss()
                },

                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.okay_got_it),
                    modifier = Modifier
                        .background(
                            color = PrimaryBlue, shape = RoundedCornerShape(12.dp)
                        )
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    color = White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerScreen(
    onImagePicked: (Uri) -> Unit, onDismiss: () -> Unit
) {

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    // State to hold the URI of the selected image
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        // Launchers for Camera and Gallery
        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { success ->
                if (success && imageUri != null) {
                    onImagePicked(imageUri!!)
                } else {
                    // Handle failure case
                }
            })

        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                onDismiss()
                imageUri = uri // Handle the selected image
                onImagePicked(imageUri!!)
            })

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Button(onClick = {
                val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val imageFile = File.createTempFile(
                    "JPEG_${System.currentTimeMillis()}_", /* prefix */
                    ".jpg", /* suffix */
                    storageDir /* directory */
                )
                val photoUri = FileProvider.getUriForFile(
                    context, context.packageName + ".provider", imageFile
                )
                cameraLauncher.launch(photoUri)
                imageUri = photoUri
            }) {
                Text("Open Camera")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = {
                galleryLauncher.launch("image/*")
            }) {
                Text("Open Gallery")
            }
            Spacer(modifier = Modifier.height(16.dp))

            imageUri?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = rememberImagePainter(it),
                    contentDescription = "Selected Image",
                    modifier = Modifier.size(200.dp)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun BottomSheetPreview() {
    var showSheet by remember { mutableStateOf(true) }
    showSheet = true/*ImagePickerScreen(true){

    }*/
}
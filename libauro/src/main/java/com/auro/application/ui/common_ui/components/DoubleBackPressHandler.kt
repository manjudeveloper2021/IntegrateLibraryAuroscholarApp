package com.auro.application.ui.common_ui.components

import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DoubleBackPressHandler(enabled: Boolean = true, onExit: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isBackPressed = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

    // Handle the back press event
    BackHandler(enabled) {
        if (showDialog.value) {
            // If dialog is already shown, ignore further back presses
            return@BackHandler
        }

        if (isBackPressed.value) {
            // Show the dialog if back was pressed twice
            showDialog.value = true
        } else {
            // First back press
            isBackPressed.value = true
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
            scope.launch {
                delay(2000L)
                isBackPressed.value = false // Reset after 2 seconds
            }
        }
    }

    // Show the confirmation dialog
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Exit Confirmation") },
            text = { Text("Are you sure you want to exit?") },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    onExit() // Call the exit function
                }) {
                    Text("Exit")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun CreatePinDoubleBackPressHandler(enabled: Boolean = true, onExit: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isBackPressed = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

    // Handle the back press event
    BackHandler(enabled) {
        if (showDialog.value) {
            // If dialog is already shown, ignore further back presses
            return@BackHandler
        }

        if (isBackPressed.value) {
            // Show the dialog if back was pressed twice
            showDialog.value = true
        } else {
            // First back press
            isBackPressed.value = true
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
            scope.launch {
                delay(2000L)
                isBackPressed.value = false // Reset after 2 seconds
            }
        }
    }

    // Show the confirmation dialog
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Exit Confirmation") },
            text = { Text("Are you sure you want to exit?") },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    onExit() // Call the exit function
                }) {
                    Text("Exit")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
public fun BackHandler(enabled: Boolean = true, onBack: () -> Unit) {
    // Safely update the current `onBack` lambda when a new one is provided
    val currentOnBack by rememberUpdatedState(onBack)
    // Remember in Composition a back callback that calls the `onBack` lambda
    val backCallback = remember {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                currentOnBack()
            }
        }
    }
    // On every successful composition, update the callback with the `enabled` value
    SideEffect {
        backCallback.isEnabled = enabled
    }
    val backDispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current) {
        "No OnBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }.onBackPressedDispatcher
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, backDispatcher) {
        // Add callback to the backDispatcher
        backDispatcher.addCallback(lifecycleOwner, backCallback)
        // When the effect leaves the Composition, remove the callback
        onDispose {
            backCallback.remove()
        }
    }
}


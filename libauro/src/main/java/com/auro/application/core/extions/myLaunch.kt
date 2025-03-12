package com.auro.application.core.extions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun CoroutineScope.launchWithSuspend(block: suspend CoroutineScope.() -> Unit) {
    launch {
        withContext(Dispatchers.IO) {
            block()
        }
    }
}
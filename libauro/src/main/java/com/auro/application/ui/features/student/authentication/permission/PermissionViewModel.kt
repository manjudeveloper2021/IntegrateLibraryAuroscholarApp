package com.auro.application.ui.features.student.authentication.permission

import androidx.compose.runtime.mutableStateListOf

class PermissionViewModel {
    val permissionDialog = mutableStateListOf<NeededPermission>()

    fun dismissDialog() {
        permissionDialog.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean,
    ){
        if (!isGranted) {
            permissionDialog.add(getNeededPermission(permission))
        }
    }
}
package com.auro.application.ui.features.student.authentication.permission


enum class NeededPermission(
    val permission: String,
    val title: String,
    val description: String,
    val permanentlyDeniedDescription: String,
) {
    COARSE_LOCATION(
        permission = android.Manifest.permission.ACCESS_COARSE_LOCATION,
        title = "Approximate Location Permission",
        description = "This permission is needed to get your approximate location. Please grant the permission.",
        permanentlyDeniedDescription = "This permission is needed to get your approximate location. Please grant the permission in app settings.",
    ),

    CAMERA_PERMISSION(
        permission = android.Manifest.permission.CAMERA,
        title = "Camera permission",
        description = "This permission is needed to access your camera. Please grant the permission.",
        permanentlyDeniedDescription = "This permission is needed to access your microphone. Please grant the permission in app settings.",
    );

    fun permissionTextProvider(isPermanentDenied: Boolean): String {
        return if (isPermanentDenied) this.permanentlyDeniedDescription else this.description
    }
}

fun getNeededPermission(permission: String): NeededPermission {
    return NeededPermission.values().find { it.permission == permission } ?: throw IllegalArgumentException("Permission $permission is not supported")
}
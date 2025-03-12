package com.auro.application.ui.features.student.menu

import androidx.compose.ui.graphics.vector.ImageVector
import com.auro.application.ui.common_ui.utils.AppRoute

// Navigation Drawer
data class MenuItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val icon: ImageVector
)

// Bottom Menu
data class BottomNavigationItems(
    val appRoute: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
)
data class TabItem(
    var title : String
)


package com.flowlauncher.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val icon: Drawable?,
    val isSystemApp: Boolean = false
) {
    val isLauncherApp: Boolean
        get() = activityName.isNotEmpty()
}

package com.flowlauncher.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PreferencesManager {
    private const val PREFS_NAME = "flow_launcher_prefs"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Theme
    var themeIndex: Int
        get() = prefs.getInt("theme_index", 0)
        set(value) = prefs.edit { putInt("theme_index", value) }

    var wallpaperPath: String?
        get() = prefs.getString("wallpaper_path", null)
        set(value) = prefs.edit { putString("wallpaper_path", value) }

    var textColor: Int
        get() = prefs.getInt("text_color", 0xFFFFFFFF.toInt())
        set(value) = prefs.edit { putInt("text_color", value) }

    var backgroundColor: Int
        get() = prefs.getInt("bg_color", 0xFF1A1A2E.toInt())
        set(value) = prefs.edit { putInt("bg_color", value) }

    var backgroundOpacity: Float
        get() = prefs.getFloat("bg_opacity", 0.7f)
        set(value) = prefs.edit { putFloat("bg_opacity", value) }

    var fontName: String
        get() = prefs.getString("font_name", "system") ?: "system"
        set(value) = prefs.edit { putString("font_name", value) }

    var iconPackPackage: String
        get() = prefs.getString("icon_pack", "") ?: ""
        set(value) = prefs.edit { putString("icon_pack", value) }

    var searchEnabled: Boolean
        get() = prefs.getBoolean("search_enabled", true)
        set(value) = prefs.edit { putBoolean("search_enabled", value) }

    var searchSuggestions: Boolean
        get() = prefs.getBoolean("search_suggestions", true)
        set(value) = prefs.edit { putBoolean("search_suggestions", value) }

    var notificationDots: Boolean
        get() = prefs.getBoolean("notification_dots", true)
        set(value) = prefs.edit { putBoolean("notification_dots", value) }

    var autoHideStatusBar: Boolean
        get() = prefs.getBoolean("auto_hide_status_bar", false)
        set(value) = prefs.edit { putBoolean("auto_hide_status_bar", value) }

    var doubleTapAction: String
        get() = prefs.getString("double_tap_action", "lock_screen") ?: "lock_screen"
        set(value) = prefs.edit { putString("double_tap_action", value) }

    var swipeUpAction: String
        get() = prefs.getString("swipe_up_action", "open_notifications") ?: "open_notifications"
        set(value) = prefs.edit { putString("swipe_up_action", value) }

    var swipeDownAction: String
        get() = prefs.getString("swipe_down_action", "open_notifications") ?: "open_notifications"
        set(value) = prefs.edit { putString("swipe_down_action", value) }

    var twoFingerAction: String
        get() = prefs.getString("two_finger_action", "none") ?: "none"
        set(value) = prefs.edit { putString("two_finger_action", value) }

    var alphabetScrollEnabled: Boolean
        get() = prefs.getBoolean("alphabet_scroll", true)
        set(value) = prefs.edit { putBoolean("alphabet_scroll", value) }

    var showAtAGlance: Boolean
        get() = prefs.getBoolean("show_at_a_glance", true)
        set(value) = prefs.edit { putBoolean("show_at_a_glance", value) }

    var atAGlanceStyle: Int
        get() = prefs.getInt("at_a_glance_style", 0)
        set(value) = prefs.edit { putInt("at_a_glance_style", value) }

    var iconSize: Float
        get() = prefs.getFloat("icon_size", 1.0f)
        set(value) = prefs.edit { putFloat("icon_size", value) }

    var textSize: Float
        get() = prefs.getFloat("text_size", 1.0f)
        set(value) = prefs.edit { putFloat("text_size", value) }

    var itemSpacing: Float
        get() = prefs.getFloat("item_spacing", 1.0f)
        set(value) = prefs.edit { putFloat("item_spacing", value) }

    fun resetToDefaults() {
        prefs.edit { clear() }
    }
}

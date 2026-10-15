package com.flowlauncher.settings

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.flowlauncher.R
import com.flowlauncher.data.PreferencesManager
import com.flowlauncher.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupAppearanceSettings()
        setupBehaviorSettings()
        setupGestureSettings()
        setupAboutSection()
    }

    private fun setupToolbar() {
        binding.toolbar.title = getString(R.string.settings)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupAppearanceSettings() {
        // Theme
        binding.settingTheme.setOnClickListener {
            startActivity(Intent(this, ThemePickerActivity::class.java))
        }

        // Wallpaper
        binding.settingWallpaper.setOnClickListener {
            val intent = Intent(android.intent.ACTION_SET_WALLPAPER)
            startActivity(Intent.createChooser(intent, getString(R.string.wallpaper)))
        }

        // Text Size
        binding.textSizeSlider.value = PreferencesManager.textSize
        binding.textSizeSlider.addOnChangeListener { _, value, _ ->
            PreferencesManager.textSize = value
        }

        // Icon Size
        binding.iconSizeSlider.value = PreferencesManager.iconSize
        binding.iconSizeSlider.addOnChangeListener { _, value, _ ->
            PreferencesManager.iconSize = value
        }

        // Item Spacing
        binding.spacingSlider.value = PreferencesManager.itemSpacing
        binding.spacingSlider.addOnChangeListener { _, value, _ ->
            PreferencesManager.itemSpacing = value
        }

        // Background Opacity
        binding.opacitySlider.value = PreferencesManager.backgroundOpacity
        binding.opacitySlider.addOnChangeListener { _, value, _ ->
            PreferencesManager.backgroundOpacity = value
        }

        // Show At A Glance
        binding.switchAtAGlance.isChecked = PreferencesManager.showAtAGlance
        binding.switchAtAGlance.setOnCheckedChangeListener { _, checked ->
            PreferencesManager.showAtAGlance = checked
        }

        // Notification Dots
        binding.switchNotificationDots.isChecked = PreferencesManager.notificationDots
        binding.switchNotificationDots.setOnCheckedChangeListener { _, checked ->
            PreferencesManager.notificationDots = checked
        }

        // Auto Hide Status Bar
        binding.switchHideStatusBar.isChecked = PreferencesManager.autoHideStatusBar
        binding.switchHideStatusBar.setOnCheckedChangeListener { _, checked ->
            PreferencesManager.autoHideStatusBar = checked
        }

        // Alphabet Scroll
        binding.switchAlphabetScroll.isChecked = PreferencesManager.alphabetScrollEnabled
        binding.switchAlphabetScroll.setOnCheckedChangeListener { _, checked ->
            PreferencesManager.alphabetScrollEnabled = checked
        }
    }

    private fun setupBehaviorSettings() {
        binding.settingNotifications.setOnClickListener {
            val intent = android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
            startActivity(intent)
        }

        binding.settingAccessibility.setOnClickListener {
            val intent = android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS
            startActivity(intent)
        }

        binding.settingDefaultLauncher.setOnClickListener {
            val intent = android.provider.Settings.ACTION_HOME_SETTINGS
            startActivity(intent)
        }
    }

    private fun setupGestureSettings() {
        binding.settingDoubleTap.setOnClickListener {
            showGestureDialog(
                getString(R.string.gesture_double_tap),
                PreferencesManager.doubleTapAction
            ) { action ->
                PreferencesManager.doubleTapAction = action
            }
        }

        binding.settingSwipeUp.setOnClickListener {
            showGestureDialog(
                getString(R.string.gesture_swipe_up),
                PreferencesManager.swipeUpAction
            ) { action ->
                PreferencesManager.swipeUpAction = action
            }
        }

        binding.settingSwipeDown.setOnClickListener {
            showGestureDialog(
                getString(R.string.gesture_swipe_down),
                PreferencesManager.swipeDownAction
            ) { action ->
                PreferencesManager.swipeDownAction = action
            }
        }
    }

    private fun setupAboutSection() {
        binding.settingVersion.setOnClickListener {
            Toast.makeText(this, "Flow Launcher v1.0.0", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showGestureDialog(title: String, currentAction: String, onSelect: (String) -> Unit) {
        val actions = arrayOf(
            getString(R.string.action_none),
            getString(R.string.action_lock_screen),
            getString(R.string.action_open_notifications),
            getString(R.string.action_open_settings),
            getString(R.string.action_search)
        )
        val actionKeys = arrayOf("none", "lock_screen", "open_notifications", "open_settings", "search")

        val currentIndex = actionKeys.indexOf(currentAction).coerceAtLeast(0)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setSingleChoiceItems(actions, currentIndex) { dialog, which ->
                onSelect(actionKeys[which])
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}

package com.flowlauncher.ui

import android.animation.ValueAnimator
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.LauncherApps
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flowlauncher.R
import com.flowlauncher.data.AppRepository
import com.flowlauncher.data.PreferencesManager
import com.flowlauncher.databinding.ActivityHomeBinding
import com.flowlauncher.model.AppInfo
import com.flowlauncher.notification.NotificationService
import com.flowlauncher.service.LockScreenService
import com.flowlauncher.settings.SettingsActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: AppListAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var allApps: List<AppInfo> = emptyList()
    private var currentTimeRunnable: Runnable? = null

    private val appUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED,
                Intent.ACTION_PACKAGE_REMOVED,
                Intent.ACTION_PACKAGE_CHANGED -> {
                    AppRepository.clearCache()
                    loadApps()
                }
            }
        }
    }

    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make fullscreen and transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
        setupAtAGlance()
        setupGestures()
        setupSystemBars()
        registerReceivers()
        loadApps()
        startClockUpdates()
    }

    private fun setupRecyclerView() {
        adapter = AppListAdapter(
            onAppClick = { app -> launchApp(app) },
            onAppLongClick = { app, view -> showAppContextMenu(app, view) }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = this@HomeActivity.adapter
            setHasFixedSize(true)
            itemAnimator?.changeDuration = 0
        }
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterApps(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                hideSearch()
                true
            } else false
        }

        binding.searchIcon.setOnClickListener {
            binding.searchEditText.requestFocus()
            binding.searchEditText.showKeyboard()
        }
    }

    private fun setupAtAGlance() {
        updateAtAGlanceStyle()
    }

    private fun setupGestures() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                handleDoubleTap()
                return true
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false
                val deltaY = e2.y - e1.y
                val deltaX = e2.x - e1.x
                if (kotlin.math.abs(deltaY) > kotlin.math.abs(deltaX) && kotlin.math.abs(deltaY) > 200) {
                    if (velocityY > 0) {
                        handleSwipeDown()
                    } else {
                        handleSwipeUp()
                    }
                    return true
                }
                return false
            }

            override fun onLongPress(e: MotionEvent) {
                if (allApps.isNotEmpty()) {
                    showHomeMenu()
                }
            }
        })

        binding.homeContainer.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
    }

    private fun setupSystemBars() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        if (PreferencesManager.autoHideStatusBar) {
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            controller.show(WindowInsetsCompat.Type.statusBars())
        }
    }

    private fun registerReceivers() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        registerReceiver(appUpdateReceiver, filter)
    }

    private fun loadApps() {
        lifecycleScope.launch {
            allApps = AppRepository.loadInstalledApps(this@HomeActivity)
            adapter.submitList(allApps)
        }
    }

    private fun filterApps(query: String) {
        val filtered = if (query.isEmpty()) allApps
        else allApps.filter {
            it.label.lowercase().contains(query.lowercase()) ||
            it.packageName.lowercase().contains(query.lowercase())
        }
        adapter.submitList(filtered)
    }

    private fun launchApp(app: AppInfo) {
        AppRepository.launchApp(this, app)
    }

    private fun showAppContextMenu(app: AppInfo, anchor: View) {
        // Show popup menu with app info, uninstall, etc.
        val popup = android.widget.PopupMenu(this, anchor)
        popup.menu.add(0, 1, 0, getString(R.string.app_info))
        if (!app.isSystemApp) {
            popup.menu.add(0, 2, 1, getString(R.string.uninstall))
        }
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> { showAppInfo(app); true }
                2 -> { uninstallApp(app); true }
                else -> false
            }
        }
        popup.show()
    }

    private fun showAppInfo(app: AppInfo) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.parse("package:${app.packageName}")
            }
            startActivity(intent)
        } catch (_: Exception) {}
    }

    private fun uninstallApp(app: AppInfo) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = android.net.Uri.parse("package:${app.packageName}")
            }
            startActivity(intent)
        } catch (_: Exception) {}
    }

    private fun handleDoubleTap() {
        when (PreferencesManager.doubleTapAction) {
            "lock_screen" -> lockScreen()
            "open_notifications" -> openNotifications()
            "toggle_flashlight" -> toggleFlashlight()
            "open_settings" -> openSettings()
        }
    }

    private fun handleSwipeUp() {
        when (PreferencesManager.swipeUpAction) {
            "open_notifications" -> openNotifications()
            "open_settings" -> openSettings()
            "search" -> showSearch()
            "recent_apps" -> openRecentApps()
        }
    }

    private fun handleSwipeDown() {
        when (PreferencesManager.swipeDownAction) {
            "open_notifications" -> openNotifications()
            "open_settings" -> openSettings()
        }
    }

    private fun showHomeMenu() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun showSearch() {
        binding.searchContainer.visibility = View.VISIBLE
        binding.searchEditText.requestFocus()
        binding.searchEditText.showKeyboard()
    }

    private fun hideSearch() {
        binding.searchEditText.text?.clear()
        binding.searchContainer.visibility = View.GONE
        binding.searchEditText.hideKeyboard()
    }

    private fun lockScreen() {
        try {
            val service = Intent(this, LockScreenService::class.java)
            startService(service)
            val lockIntent = Intent("com.flowlauncher.ACTION_LOCK_SCREEN")
            sendBroadcast(lockIntent)
        } catch (_: Exception) {}
    }

    private fun openNotifications() {
        try {
            val sbservice = getSystemService("statusbar")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val expand = statusBarManager.getMethod("expandNotificationsPanel")
            expand.invoke(sbservice)
        } catch (_: Exception) {}
    }

    private fun toggleFlashlight() {
        // Would need CameraManager implementation for actual flashlight
    }

    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun openRecentApps() {
        try {
            val service = getSystemService("statusbar")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val expand = statusBarManager.getMethod("toggleRecentApps")
            expand.invoke(sbservice)
        } catch (_: Exception) {}
    }

    private fun startClockUpdates() {
        currentTimeRunnable = object : Runnable {
            override fun run() {
                updateClock()
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(currentTimeRunnable!!)
    }

    private fun updateClock() {
        if (!PreferencesManager.showAtAGlance) {
            binding.atAGlanceContainer.visibility = View.GONE
            return
        }
        binding.atAGlanceContainer.visibility = View.VISIBLE

        val timeFormat = SimpleDateFormat("h:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())

        binding.glanceTime.text = timeFormat.format(Date())
        binding.glanceDate.text = dateFormat.format(Date())
    }

    private fun updateAtAGlanceStyle() {
        if (!PreferencesManager.showAtAGlance) {
            binding.atAGlanceContainer.visibility = View.GONE
            return
        }
        // Apply selected style variant
        when (PreferencesManager.atAGlanceStyle) {
            0 -> { // Basic
                binding.glanceTime.textSize = 48f
                binding.glanceDate.textSize = 14f
            }
            1 -> { // Large
                binding.glanceTime.textSize = 64f
                binding.glanceDate.textSize = 16f
            }
            2 -> { // Compact
                binding.glanceTime.textSize = 36f
                binding.glanceDate.textSize = 12f
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateAtAGlanceStyle()
        updateClock()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        unregisterReceiver(appUpdateReceiver)
    }

    override fun onBackPressed() {
        if (binding.searchContainer.visibility == View.VISIBLE) {
            hideSearch()
        } else {
            // Consume back press - we're the home screen
        }
    }

    private fun EditText.showKeyboard() {
        post {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun EditText.hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}

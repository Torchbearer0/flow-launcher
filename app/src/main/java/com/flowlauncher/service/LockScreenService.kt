package com.flowlauncher.service

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent

class LockScreenService : AccessibilityService() {

    private val lockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "com.flowlauncher.ACTION_LOCK_SCREEN") {
                performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val filter = IntentFilter("com.flowlauncher.ACTION_LOCK_SCREEN")
        registerReceiver(lockReceiver, filter)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used for lock screen
    }

    override fun onInterrupt() {
        // Required override
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(lockReceiver)
    }
}

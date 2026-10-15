package com.flowlauncher.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // Track notification posting for badge dots
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Track notification removal
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
    }
}

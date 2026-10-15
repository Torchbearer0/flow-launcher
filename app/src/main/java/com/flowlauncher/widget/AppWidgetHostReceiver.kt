package com.flowlauncher.widget

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.FrameLayout

class AppWidgetHostReceiver : BroadcastReceiver() {

    private var appWidgetHost: AppWidgetHost? = null

    override fun onReceive(context: Context, intent: Intent) {
        val appWidgetManager = AppWidgetManager.getInstance(context)

        when (intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                if (appWidgetIds != null) {
                    for (appWidgetId in appWidgetIds) {
                        updateAppWidget(context, appWidgetManager, appWidgetId)
                    }
                }
            }
            AppWidgetManager.ACTION_APPWIDGET_ENABLED -> {
                // Widget enabled
            }
            AppWidgetManager.ACTION_APPWIDGET_DISABLED -> {
                // Widget disabled
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val providerInfo = appWidgetManager.getAppWidgetInfo(appWidgetId) ?: return
        val hostView = AppWidgetHostView(context)
        hostView.setAppWidget(appWidgetId, providerInfo)
    }
}

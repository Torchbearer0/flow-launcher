package com.flowlauncher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.flowlauncher.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AppRepository {

    private var cachedApps: List<AppInfo>? = null

    suspend fun loadInstalledApps(context: Context, forceRefresh: Boolean = false): List<AppInfo> {
        if (!forceRefresh && cachedApps != null) {
            return cachedApps!!
        }
        return withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)
            val apps = resolveInfos.map { resolveInfo ->
                val activityInfo = resolveInfo.activityInfo
                AppInfo(
                    packageName = activityInfo.packageName,
                    activityName = activityInfo.name,
                    label = resolveInfo.loadLabel(pm).toString(),
                    icon = try { resolveInfo.loadIcon(pm) } catch (e: Exception) { null },
                    isSystemApp = (activityInfo.applicationInfo.flags and
                            android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                )
            }.sortedBy { it.label.lowercase() }
            cachedApps = apps
            apps
        }
    }

    fun clearCache() {
        cachedApps = null
    }

    fun searchApps(context: Context, query: String): List<AppInfo> {
        val apps = cachedApps ?: return emptyList()
        val q = query.lowercase().trim()
        if (q.isEmpty()) return apps
        return apps.filter { app ->
            app.label.lowercase().contains(q) ||
            app.packageName.lowercase().contains(q)
        }
    }

    fun launchApp(context: Context, appInfo: AppInfo): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(appInfo.packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}

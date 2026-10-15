package com.flowlauncher

import android.app.Application
import com.flowlauncher.data.PreferencesManager

class FlowLauncherApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        PreferencesManager.init(this)
    }

    companion object {
        lateinit var instance: FlowLauncherApp
            private set
    }
}

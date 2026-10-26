package com.build.gcunlock

import android.app.Application
import com.build.gcunlock.core.ConfigBridge

class ModuleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ConfigBridge.init(this)
    }
}

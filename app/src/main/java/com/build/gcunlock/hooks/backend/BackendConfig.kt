package com.build.gcunlock.hooks.backend

import android.util.Log
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.BackendConfig"

object BackendConfig {
    private var revision = 0
    private var snapshot = ConfigSnapshot()

    fun init(config: ConfigSnapshot) { snapshot = config; revision = 0; Log.d(TAG, "init rev=$revision") }
    fun update(config: ConfigSnapshot) { snapshot = config; revision++; Log.d(TAG, "update rev=$revision") }
    fun current(): ConfigSnapshot = snapshot
    fun revision(): Int = revision
}

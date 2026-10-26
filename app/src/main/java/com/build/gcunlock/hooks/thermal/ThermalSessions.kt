package com.build.gcunlock.hooks.thermal

import android.util.Log
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.ThermalSessions"

object ThermalSessions {

    data class ThermalState(val pkg: String, val bypassed: Boolean = false, val at: Long = System.currentTimeMillis())

    private val active = mutableMapOf<String, ThermalState>()

    fun init(config: ConfigSnapshot) { Log.d(TAG, "init bypass=${config.thermalBypass}") }
    fun register(pkg: String, bypassed: Boolean) { active[pkg] = ThermalState(pkg, bypassed); Log.d(TAG, "register pkg=$pkg") }
    fun clear(pkg: String) { active.remove(pkg) }
    fun isBypassed(pkg: String) = active[pkg]?.bypassed ?: false
}

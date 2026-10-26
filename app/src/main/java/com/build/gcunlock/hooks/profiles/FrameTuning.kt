package com.build.gcunlock.hooks.profiles

import android.util.Log
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.FrameTuning"

object FrameTuning {

    data class Ticket(val pkg: String, val gameFps: Int, val depressFps: Int, val interFps: Int, val at: Long = System.currentTimeMillis())

    private val tickets = mutableMapOf<String, Ticket>()

    fun init(config: ConfigSnapshot) { Log.d(TAG, "init fps=${config.fpsOverride}") }
    fun issue(pkg: String, gameFps: Int, depressFps: Int, interFps: Int) {
        tickets[pkg] = Ticket(pkg, gameFps, depressFps, interFps)
        Log.d(TAG, "ticket issued pkg=$pkg gameFps=$gameFps")
    }
    fun get(pkg: String): Ticket? = tickets[pkg]
    fun revoke(pkg: String) { tickets.remove(pkg); Log.d(TAG, "revoked pkg=$pkg") }
}

package com.build.gcunlock.core

import android.os.Build
import android.util.Log

private const val TAG = "GCUnlock.DevicePolicy"

object DevicePolicy {
    fun isSupported(): Boolean = try {
        val model = getProp("ro.vivo.product.model")
        val ver   = getProp("persist.vivo.displayic.ver")
        (model.isNotEmpty() || ver.isNotEmpty()).also { Log.d(TAG, "isSupported=$it model=$model") }
    } catch (e: Exception) { false }

    fun isOs5(): Boolean = Build.VERSION.SDK_INT >= 35

    private fun getProp(key: String): String = try {
        val c = Class.forName("android.os.SystemProperties")
        (c.getMethod("get", String::class.java).invoke(null, key) as? String) ?: ""
    } catch (e: Exception) { "" }
}

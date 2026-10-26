package com.build.gcunlock.hooks.backend

import android.util.Log
import io.github.libxposed.api.XposedInterface
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.ThermalGraphics"

object ThermalGraphicsHooks {
    fun bind(xposed: XposedInterface, cl: ClassLoader, config: ConfigSnapshot) {
        if (!config.thermalBypass) return
        listOf(
            "com.vivo.gameassistant.superresolution.a",
            "com.vivo.gameassistant.superresolution.b",
            "com.vivo.gameassistant.superresolution.d",
            "com.vivo.gameassistant.superresolution.i",
            "com.vivo.gameassistant.superresolution.o",
            "com.vivo.common.utils.h0",
        ).forEach { cls ->
            try {
                cl.loadClass(cls).declaredMethods
                    .filter { m -> m.name.lowercase().let { n -> n.contains("thermal") || n.contains("reduce") || n.contains("disable") || n.contains("memc") } }
                    .forEach { m ->
                        xposed.hook(m).intercept { chain -> Unit }
                    }
            } catch (e: Exception) { Log.w(TAG, "ThermalGraphics $cls: $e") }
        }
    }
}

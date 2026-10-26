package com.build.gcunlock.hooks.backend

import android.util.Log
import io.github.libxposed.api.XposedInterface
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.ThermalFps"

object ThermalFpsHooks {
    fun bind(xposed: XposedInterface, cl: ClassLoader, config: ConfigSnapshot) {
        if (!config.thermalBypass) return
        listOf(
            "com.vivo.gameassistant.frameinterpolation.FrameInterpolationState",
            "com.vivo.gameassistant.frameinterpolation.ChangeFrameReason",
            "com.vivo.gameassistant.frameinterpolation.f",
            "com.vivo.gameassistant.frameinterpolation.h",
        ).forEach { cls ->
            try {
                cl.loadClass(cls).declaredMethods
                    .filter { m -> m.name.lowercase().let { n -> n.contains("thermal") || n.contains("reduce") || n.contains("mapfps") } }
                    .forEach { m ->
                        xposed.hook(m).intercept { chain -> Unit }
                    }
            } catch (e: Exception) { Log.w(TAG, "ThermalFps $cls: $e") }
        }
        try {
            cl.loadClass("com.vivo.common.utils.TemperatureHelper").declaredMethods
                .filter { m -> m.name.lowercase().let { n -> n.contains("temp") || n.contains("thermal") } &&
                    (m.returnType == Float::class.java || m.returnType == Int::class.java) }
                .forEach { m ->
                    xposed.hook(m).intercept { chain ->
                        if (m.returnType == Float::class.java) 35.0f else 35
                    }
                }
        } catch (e: Exception) { Log.w(TAG, "TemperatureHelper: $e") }
    }
}

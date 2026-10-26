package com.build.gcunlock.hooks.backend

import android.util.Log
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.BeforeHookCallback
import io.github.libxposed.api.XposedInterface.AfterHookCallback
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
                    .filter { m -> m.name.lowercase().let { n ->
                        n.contains("thermal") || n.contains("reduce") || n.contains("mapfps") } }
                    .forEach { m ->
                        xposed.hook(m, object : XposedInterface.Hooker {
                            override fun beforeHookedMethod(callback: BeforeHookCallback) {
                                Log.d(TAG, "thermal block: $cls.${m.name}")
                                callback.returnAndSkip(Unit)
                            }
                        })
                    }
            } catch (e: Exception) { Log.w(TAG, "ThermalFps $cls: $e") }
        }
        // TemperatureHelper — override para temperatura segura
        try {
            cl.loadClass("com.vivo.common.utils.TemperatureHelper").declaredMethods
                .filter { m -> m.name.lowercase().let { n -> n.contains("temp") || n.contains("thermal") } &&
                    (m.returnType == Float::class.java || m.returnType == Int::class.java) }
                .forEach { m ->
                    xposed.hook(m, object : XposedInterface.Hooker {
                        override fun afterHookedMethod(callback: AfterHookCallback) {
                            callback.result = if (m.returnType == Float::class.java) 35.0f else 35
                            Log.d(TAG, "TemperatureHelper.${m.name} -> safe")
                        }
                    })
                }
        } catch (e: Exception) { Log.w(TAG, "TemperatureHelper: $e") }
        Log.d(TAG, "bound")
    }
}

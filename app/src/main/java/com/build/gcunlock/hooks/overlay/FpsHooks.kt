package com.build.gcunlock.hooks.overlay

import android.util.Log
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.AfterHookCallback
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.FpsHooks"

object FpsHooks {

    fun bind(xposed: XposedInterface, cl: ClassLoader, config: ConfigSnapshot) {
        // framedisplay overlay
        try {
            cl.loadClass("com.vivo.gameassistant.framedisplay.e").declaredMethods
                .filter { m -> m.name.lowercase().let { n -> n.contains("fps") || n.contains("display") } }
                .forEach { m ->
                    xposed.hook(m, object : XposedInterface.Hooker {
                        override fun afterHookedMethod(callback: AfterHookCallback) {
                            Log.d(TAG, "framedisplay.e.${m.name} intercepted")
                        }
                    })
                }
        } catch (e: Exception) { Log.w(TAG, "FpsDisplay: $e") }
        // lifecycle
        listOf("com.vivo.gameassistant.utils.l", "com.vivo.gameassistant.utils.o", "com.vivo.gameassistant.utils.p")
            .forEach { cls ->
                try {
                    cl.loadClass(cls).declaredMethods.forEach { m ->
                        xposed.hook(m, object : XposedInterface.Hooker {
                            override fun afterHookedMethod(callback: AfterHookCallback) {
                                Log.d(TAG, "fps_lifecycle $cls.${m.name}")
                            }
                        })
                    }
                } catch (e: Exception) { /* silent */ }
            }
        Log.d(TAG, "bound")
    }
}

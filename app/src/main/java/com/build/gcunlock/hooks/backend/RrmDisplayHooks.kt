package com.build.gcunlock.hooks.backend

import android.util.Log
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.AfterHookCallback
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.RrmDisplay"

object RrmDisplayHooks {

    fun bind(xposed: XposedInterface, cl: ClassLoader, config: ConfigSnapshot) {
        if (!config.hold144HzIdle) return
        listOf("com.vivo.services.rrm.RMWms", "com.vivo.services.rms.RMWms",
               "com.vivo.services.rms.display.DisplayRateManager").forEach { cls ->
            try {
                cl.loadClass(cls).declaredMethods
                    .filter { m -> m.returnType == Int::class.java &&
                        m.name.lowercase().let { n -> n.contains("fps") || n.contains("rate") || n.contains("refresh") } }
                    .forEach { m ->
                        xposed.hook(m, object : XposedInterface.Hooker {
                            override fun afterHookedMethod(callback: AfterHookCallback) {
                                val v = callback.result as? Int ?: return
                                if (v < 144) { callback.result = 144; Log.d(TAG, "${m.name} $v->144") }
                            }
                        })
                    }
            } catch (e: Exception) { Log.w(TAG, "RrmDisplay $cls: $e") }
        }
        Log.d(TAG, "bound")
    }
}

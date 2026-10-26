package com.build.gcunlock.hooks.backend

import android.util.Log
import io.github.libxposed.api.XposedInterface
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
                        xposed.hook(m).intercept { chain ->
                            val result = chain.proceed() // Chama só UMA vez
                            val v = result as? Int ?: return@intercept result
                            if (v < 144) { 
                                Log.d(TAG, "${m.name} $v->144")
                                144 
                            } else {
                                v
                            }
                        }
                    }
            } catch (e: Exception) { Log.w(TAG, "RrmDisplay $cls: $e") }
        }
    }
}

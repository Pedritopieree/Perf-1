package com.build.gcunlock.hooks.thermal

import android.util.Log
import io.github.libxposed.api.XposedInterface
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.CubeThermal"

object CubeThermalHooks {
    fun bind(xposed: XposedInterface, cl: ClassLoader, config: ConfigSnapshot) {
        if (!config.thermalBypass) return
        listOf(
            "com.vivo.framework.vperf.VivoPerfManager",
            "com.vivo.services.game.GameManagerService",
        ).forEach { cls ->
            try {
                cl.loadClass(cls).declaredMethods
                    .filter { m -> m.name.lowercase().let { n -> n.contains("thermal") || n.contains("boost") || n.contains("perf") } }
                    .forEach { m ->
                        xposed.hook(m).intercept { chain ->
                            Log.d(TAG, "$cls.${m.name} blocked")
                            if (m.name.lowercase().contains("thermal")) {
                                return@intercept Unit
                            }
                            return@intercept chain.proceed()
                        }
                    }
            } catch (e: Exception) { Log.w(TAG, "CubeThermal $cls: $e") }
        }
    }
}

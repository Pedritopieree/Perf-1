package com.build.gcunlock.hooks.backend

import android.util.Log
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.BeforeHookCallback
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.RrmScene"

object RrmSceneHooks {

    fun bind(xposed: XposedInterface, cl: ClassLoader, config: ConfigSnapshot) {
        if (!config.hold144HzIdle && !config.thermalBypass) return
        listOf(
            "com.vivo.services.rrm.RMWms" to "onSceneChanged",
            "com.vivo.services.rrm.RMWms" to "notifySceneChanged",
            "com.vivo.services.rms.RmsInjectorImpl" to "onSceneChanged",
        ).forEach { (cls, mtd) ->
            try {
                cl.loadClass(cls).declaredMethods.filter { it.name == mtd }.forEach { m ->
                    xposed.hook(m, object : XposedInterface.Hooker {
                        override fun beforeHookedMethod(callback: BeforeHookCallback) {
                            Log.d(TAG, "$mtd args=${callback.args?.toList()}")
                        }
                    })
                }
            } catch (e: Exception) { Log.w(TAG, "RrmScene $cls.$mtd: $e") }
        }
        Log.d(TAG, "bound")
    }
}

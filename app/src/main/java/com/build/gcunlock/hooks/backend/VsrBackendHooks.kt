package com.build.gcunlock.hooks.backend

import android.util.Log
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.BeforeHookCallback
import io.github.libxposed.api.XposedInterface.AfterHookCallback
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.VsrBackend"

object VsrBackendHooks {

    fun bind(xposed: XposedInterface, cl: ClassLoader, config: ConfigSnapshot) {
        if (!config.srEnabled && !config.thermalBypass) return
        listOf(
            "com.vivo.services.game.sr.VSRController" to "setVSR",
            "com.vivo.services.game.sr.VSRController" to "requestVSR",
            "vivo.common.AbsVivoDisplayEnhanceManager" to "setVSR",
        ).forEach { (cls, mtd) ->
            tryHookAll(xposed, cl, cls, mtd) { callback ->
                if (config.srEnabled && config.srAlgorithm > 0) {
                    callback.args?.let { args ->
                        if (args.size >= 3) {
                            args[0] = config.srAlgorithm
                            if (config.srTargetShortSide > 0) args[1] = config.srTargetShortSide
                            if (config.srSourceSize > 0)      args[2] = config.srSourceSize
                            Log.d(TAG, "$mtd overridden alg=${config.srAlgorithm}")
                        }
                    }
                }
            }
        }
        Log.d(TAG, "bound sr=${config.srEnabled}")
    }

    fun hookSystemServer(xposed: XposedInterface, cl: ClassLoader) {
        tryHookAll(xposed, cl, "com.vivo.services.game.sr.VSRController\$H", "handleMessage") {
            Log.d(TAG, "VSRController\$H.handleMessage intercepted")
        }
    }

    private fun tryHookAll(xposed: XposedInterface, cl: ClassLoader, cls: String, mtd: String, before: (BeforeHookCallback) -> Unit) {
        try {
            cl.loadClass(cls).declaredMethods.filter { it.name == mtd }.forEach { m ->
                xposed.hook(m, object : XposedInterface.Hooker {
                    override fun beforeHookedMethod(callback: BeforeHookCallback) = before(callback)
                })
            }
        } catch (e: Exception) { Log.w(TAG, "hook $cls.$mtd: $e") }
    }
}

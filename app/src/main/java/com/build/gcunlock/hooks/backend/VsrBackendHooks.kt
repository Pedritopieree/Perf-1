package com.build.gcunlock.hooks.backend

import android.util.Log
import io.github.libxposed.api.XposedInterface
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
            tryHookAll(xposed, cl, cls, mtd) { chain ->
                if (config.srEnabled && config.srAlgorithm > 0) {
                    val args = chain.args
                    if (args != null && args.size >= 3) {
                        args[0] = config.srAlgorithm
                        if (config.srTargetShortSide > 0) args[1] = config.srTargetShortSide
                        if (config.srSourceSize > 0)      args[2] = config.srSourceSize
                        Log.d(TAG, "$mtd overridden alg=${config.srAlgorithm}")
                        return@tryHookAll chain.proceed(args)
                    }
                }
                chain.proceed()
            }
        }
    }

    fun hookSystemServer(xposed: XposedInterface, cl: ClassLoader) {
        tryHookAll(xposed, cl, "com.vivo.services.game.sr.VSRController\$H", "handleMessage") { chain ->
            Log.d(TAG, "VSRController\$H.handleMessage intercepted")
            chain.proceed()
        }
    }

    private fun tryHookAll(xposed: XposedInterface, cl: ClassLoader, cls: String, mtd: String, intercept: (XposedInterface.Chain) -> Any?) {
        try {
            cl.loadClass(cls).declaredMethods.filter { it.name == mtd }.forEach { m ->
                xposed.hook(m).intercept(intercept)
            }
        } catch (e: Exception) { Log.w(TAG, "hook $cls.$mtd: $e") }
    }
}

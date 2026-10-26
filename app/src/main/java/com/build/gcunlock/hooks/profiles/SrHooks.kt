package com.build.gcunlock.hooks.profiles

import android.util.Log
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.AfterHookCallback
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.SrHooks"

object SrHooks {

    fun bind(xposed: XposedInterface, cl: ClassLoader, config: ConfigSnapshot, isOs5: Boolean) {
        if (!config.srEnabled) return
        val classes = if (isOs5)
            listOf("com.vivo.gameassistant.superresolution.i", "com.vivo.gameassistant.superresolution.o")
        else
            listOf("com.vivo.gameassistant.superresolution.a", "com.vivo.gameassistant.superresolution.b", "com.vivo.gameassistant.superresolution.d")
        classes.forEach { cls ->
            try {
                val clazz = cl.loadClass(cls)
                // force enable
                clazz.declaredMethods.filter { m -> m.returnType == Boolean::class.java &&
                    m.name.lowercase().let { n -> n.contains("enable") || n.contains("support") || n.contains("available") } }
                    .forEach { m -> xposed.hook(m, object : XposedInterface.Hooker {
                        override fun afterHookedMethod(callback: AfterHookCallback) { callback.result = true }
                    }) }
                // override algorithm
                clazz.declaredMethods.filter { m -> m.returnType == Int::class.java && m.name.lowercase().contains("algorithm") }
                    .forEach { m -> xposed.hook(m, object : XposedInterface.Hooker {
                        override fun afterHookedMethod(callback: AfterHookCallback) {
                            if (config.srAlgorithm > 0) callback.result = config.srAlgorithm
                        }
                    }) }
            } catch (e: Exception) { Log.w(TAG, "SrHooks $cls: $e") }
        }
        // gamecube_super_resolution feature flag
        try {
            cl.loadClass("com.vivo.gameassistant.utils.e0").declaredMethods
                .filter { m -> m.returnType == Boolean::class.java }
                .forEach { m -> xposed.hook(m, object : XposedInterface.Hooker {
                    override fun afterHookedMethod(callback: AfterHookCallback) { callback.result = true }
                }) }
        } catch (e: Exception) { Log.w(TAG, "SrEnable: $e") }
        Log.d(TAG, "bound isOs5=$isOs5 alg=${config.srAlgorithm}")
    }
}

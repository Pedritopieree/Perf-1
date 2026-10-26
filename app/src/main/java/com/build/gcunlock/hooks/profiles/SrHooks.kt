package com.build.gcunlock.hooks.profiles

import android.util.Log
import io.github.libxposed.api.XposedInterface
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
                clazz.declaredMethods.filter { m -> m.returnType == Boolean::class.java &&
                    m.name.lowercase().let { n -> n.contains("enable") || n.contains("support") || n.contains("available") } }
                    .forEach { m -> 
                        xposed.hook(m).intercept { chain -> true } 
                    }
                clazz.declaredMethods.filter { m -> m.returnType == Int::class.java && m.name.lowercase().contains("algorithm") }
                    .forEach { m -> 
                        xposed.hook(m).intercept { chain ->
                            if (config.srAlgorithm > 0) config.srAlgorithm else chain.proceed()
                        } 
                    }
            } catch (e: Exception) { Log.w(TAG, "SrHooks $cls: $e") }
        }
        try {
            cl.loadClass("com.vivo.gameassistant.utils.e0").declaredMethods
                .filter { m -> m.returnType == Boolean::class.java }
                .forEach { m -> 
                    xposed.hook(m).intercept { chain -> true } 
                }
        } catch (e: Exception) { Log.w(TAG, "SrEnable: $e") }
    }
}

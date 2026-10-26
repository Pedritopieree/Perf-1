package com.build.gcunlock.hooks.overlay

import android.util.Log
import io.github.libxposed.api.XposedInterface
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.FpsHooks"

object FpsHooks {
    fun bind(xposed: XposedInterface, cl: ClassLoader, config: ConfigSnapshot) {
        try {
            cl.loadClass("com.vivo.gameassistant.framedisplay.e").declaredMethods
                .filter { m -> m.name.lowercase().let { n -> n.contains("fps") || n.contains("display") } }
                .forEach { m ->
                    xposed.hook(m).intercept { chain ->
                        Log.d(TAG, "framedisplay.e.${m.name} intercepted")
                        chain.proceed()
                    }
                }
        } catch (e: Exception) { Log.w(TAG, "FpsDisplay: $e") }
        
        listOf("com.vivo.gameassistant.utils.l", "com.vivo.gameassistant.utils.o", "com.vivo.gameassistant.utils.p")
            .forEach { cls ->
                try {
                    cl.loadClass(cls).declaredMethods.forEach { m ->
                        xposed.hook(m).intercept { chain ->
                            Log.d(TAG, "fps_lifecycle $cls.${m.name}")
                            chain.proceed()
                        }
                    }
                } catch (e: Exception) { /* silent */ }
            }
    }
}

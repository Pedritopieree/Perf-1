package com.build.gcunlock.hooks.profiles

import android.util.Log
import io.github.libxposed.api.XposedInterface
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.FrameHooks"

object FrameHooks {
    fun bind(xposed: XposedInterface, cl: ClassLoader, config: ConfigSnapshot, isOs5: Boolean) {
        val targets = if (isOs5) listOf("com.vivo.gameassistant.framedisplay.e")
                      else listOf("com.vivo.gameassistant.framedisplay.a", "com.vivo.gameassistant.framedisplay.d")
        targets.forEach { cls ->
            try {
                cl.loadClass(cls).declaredMethods
                    .filter { m -> m.returnType == Int::class.java &&
                        m.name.lowercase().let { n -> n.contains("fps") || n.contains("frame") } }
                    .forEach { m ->
                        xposed.hook(m).intercept { chain ->
                            val result = chain.proceed() // Chama só UMA vez
                            val orig = result as? Int ?: return@intercept result
                            val name = m.name.lowercase()
                            val override = when {
                                name.contains("depress") && config.depressFps > 0 -> config.depressFps
                                name.contains("inter")   && config.interFps > 0   -> config.interFps
                                name.contains("game")    && config.fpsOverride > 0 -> config.fpsOverride
                                else -> orig
                            }
                            if (override != orig) {
                                Log.d(TAG, "$cls.${m.name} $orig->$override")
                                override
                            } else {
                                orig
                            }
                        }
                    }
            } catch (e: Exception) { Log.w(TAG, "FrameHooks $cls: $e") }
        }
    }
}

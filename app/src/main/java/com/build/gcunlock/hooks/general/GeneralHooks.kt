package com.build.gcunlock.hooks.general

import android.util.Log
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.AfterHookCallback
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.GeneralHooks"

object GeneralHooks {

    fun bind(xposed: XposedInterface, cl: ClassLoader, config: ConfigSnapshot) {
        if (config.audioUnlock) hookAudio(xposed, cl)
        hookGyroAntiShake(xposed, cl)
        hookSyncSwitch(xposed, cl)
        Log.d(TAG, "bound audio=${config.audioUnlock}")
    }

    private fun hookAudio(xposed: XposedInterface, cl: ClassLoader) {
        listOf(
            "com.vivo.gameassistant.gamecustomsound.GameSoundSupportGame",
            "com.vivo.gameassistant.gamecustomsound.c",
            "com.vivo.gameassistant.gamecustomsound.d",
        ).forEach { cls ->
            try {
                cl.loadClass(cls).declaredMethods
                    .filter { m -> m.returnType == Boolean::class.java }
                    .forEach { m ->
                        xposed.hook(m, object : XposedInterface.Hooker {
                            override fun afterHookedMethod(callback: AfterHookCallback) {
                                callback.result = true
                                Log.d(TAG, "audio $cls.${m.name}->true")
                            }
                        })
                    }
            } catch (e: Exception) { Log.w(TAG, "audio $cls: $e") }
        }
    }

    private fun hookGyroAntiShake(xposed: XposedInterface, cl: ClassLoader) {
        try {
            cl.loadClass("com.vivo.gameassistant.utils.z0").declaredMethods
                .filter { m -> m.name.lowercase().contains("gyro") }
                .forEach { m ->
                    xposed.hook(m, object : XposedInterface.Hooker {
                        override fun afterHookedMethod(callback: AfterHookCallback) {
                            Log.d(TAG, "gyro anti-shake ${m.name}")
                        }
                    })
                }
        } catch (e: Exception) { Log.w(TAG, "gyro: $e") }
    }

    private fun hookSyncSwitch(xposed: XposedInterface, cl: ClassLoader) {
        try {
            cl.loadClass("com.vivo.gameassistant.o").declaredMethods
                .filter { m -> m.name.lowercase().let { n -> n.contains("sync") || n.contains("switch") } }
                .forEach { m ->
                    xposed.hook(m, object : XposedInterface.Hooker {
                        override fun afterHookedMethod(callback: AfterHookCallback) {
                            Log.d(TAG, "syncSwitch ${m.name}")
                        }
                    })
                }
        } catch (e: Exception) { Log.w(TAG, "syncSwitch: $e") }
    }
}

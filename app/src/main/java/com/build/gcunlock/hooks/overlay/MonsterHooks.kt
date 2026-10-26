package com.build.gcunlock.hooks.overlay

import android.util.Log
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.AfterHookCallback
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.Monster"

object MonsterHooks {

    fun bind(xposed: XposedInterface, cl: ClassLoader, config: ConfigSnapshot) {
        if (!config.monsterPlusEnabled) return
        listOf(
            "com.vivo.gameassistant.gamemanipulation.e",
            "com.vivo.common.gamemanipulation.a",
            "com.vivo.common.gamemanipulation.b",
        ).forEach { cls ->
            try {
                cl.loadClass(cls).declaredMethods
                    .filter { m -> m.returnType == Boolean::class.java }
                    .forEach { m ->
                        xposed.hook(m, object : XposedInterface.Hooker {
                            override fun afterHookedMethod(callback: AfterHookCallback) {
                                callback.result = true
                                Log.d(TAG, "MonsterAccess $cls.${m.name} -> true")
                            }
                        })
                    }
            } catch (e: Exception) { Log.w(TAG, "MonsterAccess $cls: $e") }
        }
        try {
            cl.loadClass("com.vivo.gameassistant.homegui.sideslide.panels.performance.modeshift.ModeShiftBar")
                .declaredMethods
                .filter { m -> m.name.lowercase().let { n -> n.contains("mode") || n.contains("monster") } }
                .forEach { m ->
                    xposed.hook(m, object : XposedInterface.Hooker {
                        override fun afterHookedMethod(callback: AfterHookCallback) {
                            Log.d(TAG, "ModeShiftBar.${m.name} intercepted")
                        }
                    })
                }
        } catch (e: Exception) { Log.w(TAG, "ModeShiftBar: $e") }
        Log.d(TAG, "bound")
    }
}

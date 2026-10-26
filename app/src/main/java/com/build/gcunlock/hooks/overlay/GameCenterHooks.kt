package com.build.gcunlock.hooks.overlay

import android.util.Log
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.BeforeHookCallback
import io.github.libxposed.api.XposedInterface.AfterHookCallback
import com.build.gcunlock.core.ConfigSnapshot

private const val TAG = "GCUnlock.GameCenter"

object GameCenterHooks {

    fun bind(xposed: XposedInterface, cl: ClassLoader, config: ConfigSnapshot, isOs5: Boolean) {
        hookAssistantService(xposed, cl)
        hookEdgeDock(xposed, cl, config)
        if (isOs5) hookOs5Panel(xposed, cl)
        Log.d(TAG, "bound isOs5=$isOs5")
    }

    private fun hookAssistantService(xposed: XposedInterface, cl: ClassLoader) {
        try {
            cl.loadClass("com.vivo.gameassistant.AssistantUIService").declaredMethods
                .filter { m -> m.name.lowercase().let { n -> n.contains("show") || n.contains("panel") || n.contains("monster") } }
                .forEach { m ->
                    xposed.hook(m, object : XposedInterface.Hooker {
                        override fun beforeHookedMethod(callback: BeforeHookCallback) {
                            Log.d(TAG, "AssistantUIService.${m.name}")
                        }
                    })
                }
        } catch (e: Exception) { Log.w(TAG, "AssistantUIService: $e") }
    }

    private fun hookEdgeDock(xposed: XposedInterface, cl: ClassLoader, config: ConfigSnapshot) {
        try {
            cl.loadClass("com.vivo.gameassistant.homegui.sideslide.dock.view.EdgeDockView").declaredMethods
                .filter { m -> m.returnType == Boolean::class.java && m.name.lowercase().contains("enable") }
                .forEach { m ->
                    xposed.hook(m, object : XposedInterface.Hooker {
                        override fun afterHookedMethod(callback: AfterHookCallback) {
                            if (config.monsterPlusEnabled) { callback.result = true; Log.d(TAG, "EdgeDock.${m.name}->true") }
                        }
                    })
                }
        } catch (e: Exception) { Log.w(TAG, "EdgeDock: $e") }
    }

    private fun hookOs5Panel(xposed: XposedInterface, cl: ClassLoader) {
        try {
            cl.loadClass("com.vivo.gameassistant.homegui.sideslide.panels.performance.common.panel.PerformancePanelPanelView")
                .declaredMethods
                .filter { m -> m.name.lowercase().let { n -> n.contains("monster") || n.contains("show") } }
                .forEach { m ->
                    xposed.hook(m, object : XposedInterface.Hooker {
                        override fun afterHookedMethod(callback: AfterHookCallback) {
                            Log.d(TAG, "PerformancePanelPanelView.${m.name}")
                        }
                    })
                }
        } catch (e: Exception) { Log.w(TAG, "Os5Panel: $e") }
    }
}

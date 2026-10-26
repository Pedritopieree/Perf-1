package com.build.gcunlock

import android.util.Log
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam
import io.github.libxposed.api.XposedModuleInterface.SystemServerStartingParam
import com.build.gcunlock.core.ConfigBridge
import com.build.gcunlock.core.DevicePolicy
import com.build.gcunlock.hooks.HookRuntime
import com.build.gcunlock.hooks.backend.*
import com.build.gcunlock.hooks.overlay.*
import com.build.gcunlock.hooks.profiles.*
import com.build.gcunlock.hooks.thermal.*
import com.build.gcunlock.hooks.general.*

private const val TAG = "GCUnlock"

// O CONSTRUTOR PRECISA DO HOST E PARAM
class ModuleEntry(host: XposedInterface, param: ModuleLoadedParam) : XposedModule(host, param) {

    private lateinit var runtime: HookRuntime

    override fun onPackageReady(param: PackageReadyParam) {
        val pkg = param.packageName
        val cl  = param.classLoader
        Log.d(TAG, "onPackageReady pkg=$pkg")

        runtime = HookRuntime(this, cl)
        val config = ConfigBridge.snapshot()

        when (pkg) {
            "com.vivo.gamecube" -> {
                if (!DevicePolicy.isSupported()) return
                bindHooks(cl, config, isOs5 = DevicePolicy.isOs5())
            }
            "com.vivo.gamewatch" -> {
                bindGameWatchHooks(cl, config)
            }
        }
    }

    override fun onSystemServerStarting(param: SystemServerStartingParam) {
        VsrBackendHooks.hookSystemServer(this, param.classLoader)
    }

    private fun bindHooks(cl: ClassLoader, config: com.build.gcunlock.core.ConfigSnapshot, isOs5: Boolean) {
        BackendConfig.init(config)
        VsrBackendHooks.bind(this, cl, config)
        RrmDisplayHooks.bind(this, cl, config)
        RrmSceneHooks.bind(this, cl, config)
        ThermalFpsHooks.bind(this, cl, config)
        ThermalGraphicsHooks.bind(this, cl, config)
        CubeThermalHooks.bind(this, cl, config)
        ThermalSessions.init(config)
        FrameHooks.bind(this, cl, config, isOs5)
        SrHooks.bind(this, cl, config, isOs5)
        FrameTuning.init(config)
        FpsHooks.bind(this, cl, config)
        MonsterHooks.bind(this, cl, config)
        GameCenterHooks.bind(this, cl, config, isOs5)
        GeneralHooks.bind(this, cl, config)
    }

    private fun bindGameWatchHooks(cl: ClassLoader, config: com.build.gcunlock.core.ConfigSnapshot) {
        ThermalFpsHooks.bind(this, cl, config)
        FpsHooks.bind(this, cl, config)
    }
}

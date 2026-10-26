package com.build.gcunlock.hooks

import android.util.Log
import io.github.libxposed.api.XposedInterface

private const val TAG = "GCUnlock.HookRuntime"

class HookRuntime(private val xposed: XposedInterface, val classLoader: ClassLoader) {

    fun findClass(name: String): Class<*>? = try {
        classLoader.loadClass(name)
    } catch (e: ClassNotFoundException) { 
        Log.w(TAG, "not found: $name")
        null 
    }

    // O LSPosed 102 usa o padrao chain.intercept
    fun hookAllMethods(className: String, methodName: String, hooker: (XposedInterface.Chain) -> Any?) {
        val clazz = findClass(className) ?: return
        clazz.declaredMethods.filter { it.name == methodName }.forEach { m ->
            try { 
                xposed.hook(m).intercept { chain -> hooker(chain) }
                Log.d(TAG, "hooked $className.$methodName") 
            } catch (e: Exception) { 
                Log.w(TAG, "hookAll failed: $e") 
            }
        }
    }
}

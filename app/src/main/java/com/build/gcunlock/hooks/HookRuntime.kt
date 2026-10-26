package com.build.gcunlock.hooks

import android.util.Log
import io.github.libxposed.api.XposedInterface

private const val TAG = "GCUnlock.HookRuntime"

class HookRuntime(private val xposed: XposedInterface, val classLoader: ClassLoader) {

    fun findClass(name: String): Class<*>? = try {
        classLoader.loadClass(name)
    } catch (e: ClassNotFoundException) { Log.w(TAG, "not found: $name"); null }

    fun hookMethod(className: String, methodName: String, vararg paramTypes: Class<*>, hooker: XposedInterface.Hooker) {
        val clazz = findClass(className) ?: return
        try {
            xposed.hook(clazz.getDeclaredMethod(methodName, *paramTypes), hooker)
            Log.d(TAG, "hooked $className.$methodName")
        } catch (e: Exception) { Log.w(TAG, "hook failed $className.$methodName: $e") }
    }

    fun hookAllMethods(className: String, methodName: String, hooker: XposedInterface.Hooker) {
        val clazz = findClass(className) ?: return
        clazz.declaredMethods.filter { it.name == methodName }.forEach { m ->
            try { xposed.hook(m, hooker); Log.d(TAG, "hooked $className.$methodName") }
            catch (e: Exception) { Log.w(TAG, "hookAll failed: $e") }
        }
    }
}

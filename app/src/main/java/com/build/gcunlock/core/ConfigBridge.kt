package com.build.gcunlock.core

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject

private const val TAG = "GCUnlock.ConfigBridge"
private const val PREFS_NAME = "gcunlock_config"

object ConfigBridge {

    private lateinit var prefs: SharedPreferences
    private val _state = MutableStateFlow(ConfigSnapshot())
    val state: StateFlow<ConfigSnapshot> = _state

    fun init(ctx: Context) {
        prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _state.value = load()
        Log.d(TAG, "init snapshot=${_state.value}")
    }

    fun snapshot(): ConfigSnapshot = _state.value

    fun save(snap: ConfigSnapshot) {
        prefs.edit().apply {
            putBoolean("thermalBypass",    snap.thermalBypass)
            putBoolean("monsterPlus",      snap.monsterPlusEnabled)
            putInt("fpsOverride",          snap.fpsOverride)
            putInt("depressFps",           snap.depressFps)
            putInt("interFps",             snap.interFps)
            putBoolean("srEnabled",        snap.srEnabled)
            putInt("srAlgorithm",          snap.srAlgorithm)
            putInt("srTargetShortSide",    snap.srTargetShortSide)
            putInt("srSourceSize",         snap.srSourceSize)
            putFloat("srSharpness",        snap.srSharpness)
            putBoolean("memcEnabled",      snap.memcEnabled)
            putBoolean("hold144Hz",        snap.hold144HzIdle)
            putBoolean("audioUnlock",      snap.audioUnlock)
            putString("perAppRules",       serializeRules(snap.perAppRules))
            apply()
        }
        _state.value = snap
        Log.d(TAG, "saved")
    }

    private fun load(): ConfigSnapshot = ConfigSnapshot(
        thermalBypass      = prefs.getBoolean("thermalBypass", false),
        monsterPlusEnabled = prefs.getBoolean("monsterPlus", false),
        fpsOverride        = prefs.getInt("fpsOverride", 0),
        depressFps         = prefs.getInt("depressFps", 60),
        interFps           = prefs.getInt("interFps", 120),
        srEnabled          = prefs.getBoolean("srEnabled", false),
        srAlgorithm        = prefs.getInt("srAlgorithm", 0),
        srTargetShortSide  = prefs.getInt("srTargetShortSide", 0),
        srSourceSize       = prefs.getInt("srSourceSize", 0),
        srSharpness        = prefs.getFloat("srSharpness", 0f),
        memcEnabled        = prefs.getBoolean("memcEnabled", false),
        hold144HzIdle      = prefs.getBoolean("hold144Hz", false),
        audioUnlock        = prefs.getBoolean("audioUnlock", false),
        perAppRules        = deserializeRules(prefs.getString("perAppRules", null)),
    )

    private fun serializeRules(rules: Map<String, AppRule>): String {
        val obj = JSONObject()
        rules.forEach { (pkg, rule) ->
            obj.put(pkg, JSONObject().apply {
                put("gameFps",         rule.gameFps)
                put("depressFps",      rule.depressFps)
                put("interFps",        rule.interFps)
                put("monsterPlus",     rule.monsterPlus)
                put("srCustom",        rule.srCustom)
                put("algorithm",       rule.algorithm)
                put("targetShortSide", rule.targetShortSide)
                put("sourceSize",      rule.sourceSize)
                put("sharpness",       rule.sharpness)
                put("darkLimit",       rule.darkLimit)
                put("whiteLimit",      rule.whiteLimit)
                put("thermalBypass",   rule.thermalBypass)
                put("channel",         rule.channel)
                put("boostFps",        rule.boostFps)
                put("powerFps",        rule.powerFps)
            })
        }
        return obj.toString()
    }

    private fun deserializeRules(json: String?): Map<String, AppRule> {
        json ?: return emptyMap()
        return try {
            val obj = JSONObject(json)
            buildMap {
                obj.keys().forEach { pkg ->
                    val r = obj.getJSONObject(pkg)
                    put(pkg, AppRule(
                        packageName    = pkg,
                        gameFps        = r.optInt("gameFps", 0),
                        depressFps     = r.optInt("depressFps", 60),
                        interFps       = r.optInt("interFps", 120),
                        monsterPlus    = r.optInt("monsterPlus", 0),
                        srCustom       = r.optBoolean("srCustom", false),
                        algorithm      = r.optInt("algorithm", 0),
                        targetShortSide= r.optInt("targetShortSide", 0),
                        sourceSize     = r.optInt("sourceSize", 0),
                        sharpness      = r.optDouble("sharpness", 0.0).toFloat(),
                        darkLimit      = r.optDouble("darkLimit", 0.0).toFloat(),
                        whiteLimit     = r.optDouble("whiteLimit", 1.0).toFloat(),
                        thermalBypass  = r.optBoolean("thermalBypass", false),
                        channel        = r.optString("channel", "vivo"),
                        boostFps       = r.optInt("boostFps", 0),
                        powerFps       = r.optInt("powerFps", 0),
                    ))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "deserialize failed: $e")
            emptyMap()
        }
    }
}

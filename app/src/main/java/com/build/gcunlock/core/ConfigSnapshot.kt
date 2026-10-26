package com.build.gcunlock.core

data class ConfigSnapshot(
    val thermalBypass: Boolean = false,
    val monsterPlusEnabled: Boolean = false,
    val fpsOverride: Int = 0,
    val depressFps: Int = 60,
    val interFps: Int = 120,
    val srEnabled: Boolean = false,
    val srAlgorithm: Int = 0,
    val srTargetShortSide: Int = 0,
    val srSourceSize: Int = 0,
    val srSharpness: Float = 0f,
    val memcEnabled: Boolean = false,
    val hold144HzIdle: Boolean = false,
    val audioUnlock: Boolean = false,
    val perAppRules: Map<String, AppRule> = emptyMap(),
)

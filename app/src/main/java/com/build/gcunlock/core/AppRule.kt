package com.build.gcunlock.core

data class AppRule(
    val packageName: String,
    val gameFps: Int = 0,
    val depressFps: Int = 60,
    val interFps: Int = 120,
    val monsterPlus: Int = 0,
    val srCustom: Boolean = false,
    val algorithm: Int = 0,
    val targetShortSide: Int = 0,
    val sourceSize: Int = 0,
    val sharpness: Float = 0f,
    val darkLimit: Float = 0f,
    val whiteLimit: Float = 1f,
    val thermalBypass: Boolean = false,
    val channel: String = "vivo",
    val boostFps: Int = 0,
    val powerFps: Int = 0,
)

package com.build.gcunlock.ui

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import com.build.gcunlock.core.ConfigBridge
import com.build.gcunlock.core.ConfigSnapshot

class MainActivity : Activity() {

    private var current = ConfigSnapshot()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConfigBridge.init(applicationContext)
        current = ConfigBridge.snapshot()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0d0d0d"))
            setPadding(40, 60, 40, 40)
        }

        root.addView(TextView(this).apply {
            text = "GCUnlock"
            textSize = 28f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 8)
        })
        root.addView(TextView(this).apply {
            text = "LSPosed • com.vivo.gamecube"
            textSize = 12f
            setTextColor(Color.parseColor("#888888"))
            setPadding(0, 0, 0, 40)
        })

        fun section(title: String) {
            root.addView(TextView(this).apply {
                text = title.uppercase()
                textSize = 10f
                setTextColor(Color.parseColor("#ff6b35"))
                setTypeface(null, Typeface.BOLD)
                setPadding(0, 24, 0, 8)
                letterSpacing = 0.15f
            })
        }

        fun addSwitch(label: String, sub: String?, checked: Boolean, onChange: (Boolean) -> Unit) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 14, 0, 14)
            }
            val texts = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            texts.addView(TextView(this).apply {
                text = label; textSize = 15f; setTextColor(Color.WHITE)
            })
            sub?.let {
                texts.addView(TextView(this).apply {
                    text = it; textSize = 12f; setTextColor(Color.parseColor("#666666"))
                })
            }
            val sw = Switch(this).apply {
                isChecked = checked
                setOnCheckedChangeListener { _, v -> onChange(v) }
            }
            row.addView(texts)
            row.addView(sw)

            val divider = android.view.View(this).apply {
                setBackgroundColor(Color.parseColor("#1a1a1a"))
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
            }
            root.addView(row)
            root.addView(divider)
        }

        fun addSlider(label: String, min: Int, max: Int, value: Int, unit: String = "", onChange: (Int) -> Unit) {
            val header = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 18, 0, 4)
            }
            val labelView = TextView(this).apply {
                text = label; textSize = 14f; setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val valueView = TextView(this).apply {
                text = if (value == 0 && unit.isEmpty()) "off" else "$value$unit"
                textSize = 14f; setTextColor(Color.parseColor("#ff6b35"))
            }
            header.addView(labelView); header.addView(valueView)
            root.addView(header)
            root.addView(SeekBar(this).apply {
                this.max = max - min
                progress = value - min
                progressTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#ff6b35"))
                thumbTintList    = android.content.res.ColorStateList.valueOf(Color.WHITE)
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(s: SeekBar, p: Int, fromUser: Boolean) {
                        val v = p + min
                        valueView.text = if (v == 0 && unit.isEmpty()) "off" else "$v$unit"
                        if (fromUser) onChange(v)
                    }
                    override fun onStartTrackingTouch(s: SeekBar) {}
                    override fun onStopTrackingTouch(s: SeekBar) {}
                })
                setPadding(0, 0, 0, 18)
            })
        }

        // ─── THERMAL ───────────────────────────────────────────────────────
        section("Thermal")
        addSwitch("Thermal Bypass", "Bloqueia reduções de FPS/SR por temperatura", current.thermalBypass) {
            current = current.copy(thermalBypass = it); ConfigBridge.save(current)
        }

        // ─── PERFORMANCE ────────────────────────────────────────────────────
        section("Performance")
        addSwitch("Monster+", "Desbloqueia modo Monster+ na sidebar", current.monsterPlusEnabled) {
            current = current.copy(monsterPlusEnabled = it); ConfigBridge.save(current)
        }
        addSwitch("Hold 144Hz Idle", "Mantém 144Hz em ecrã idle e transições", current.hold144HzIdle) {
            current = current.copy(hold144HzIdle = it); ConfigBridge.save(current)
        }
        addSlider("FPS Override", 0, 144, current.fpsOverride, " fps") {
            current = current.copy(fpsOverride = it); ConfigBridge.save(current)
        }
        addSlider("Depress FPS", 30, 120, current.depressFps, " fps") {
            current = current.copy(depressFps = it); ConfigBridge.save(current)
        }
        addSlider("Inter FPS", 30, 144, current.interFps, " fps") {
            current = current.copy(interFps = it); ConfigBridge.save(current)
        }

        // ─── SUPER RESOLUTION ───────────────────────────────────────────────
        section("Super Resolution")
        addSwitch("SR Enable", "Força SR activo mesmo em apps não suportadas", current.srEnabled) {
            current = current.copy(srEnabled = it); ConfigBridge.save(current)
        }
        addSlider("SR Algorithm", 0, 6, current.srAlgorithm) {
            current = current.copy(srAlgorithm = it); ConfigBridge.save(current)
        }
        addSlider("SR Target Short Side", 0, 1440, current.srTargetShortSide, "px") {
            current = current.copy(srTargetShortSide = it); ConfigBridge.save(current)
        }
        addSlider("SR Source Size", 0, 1080, current.srSourceSize, "px") {
            current = current.copy(srSourceSize = it); ConfigBridge.save(current)
        }

        // ─── MEMC ───────────────────────────────────────────────────────────
        section("Frame Interpolation")
        addSwitch("MEMC Enable", "Interpolação de frames (frameinterpolation)", current.memcEnabled) {
            current = current.copy(memcEnabled = it); ConfigBridge.save(current)
        }

        // ─── AUDIO ──────────────────────────────────────────────────────────
        section("Audio")
        addSwitch("Audio Unlock", "Desbloqueia perfis de áudio GameCube em todos os jogos", current.audioUnlock) {
            current = current.copy(audioUnlock = it); ConfigBridge.save(current)
        }

        // ─── INFO ────────────────────────────────────────────────────────────
        root.addView(TextView(this).apply {
            text = "Alterações aplicam no próximo restart do jogo."
            textSize = 11f
            setTextColor(Color.parseColor("#444444"))
            setPadding(0, 40, 0, 0)
            gravity = Gravity.CENTER_HORIZONTAL
        })

        setContentView(ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#0d0d0d"))
            addView(root)
        })
    }
}

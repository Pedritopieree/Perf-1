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
        try {
            ConfigBridge.init(applicationContext)
            current = ConfigBridge.snapshot()
        } catch (e: Exception) {
            e.printStackTrace()
        }

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

        // ... (MANTÉM O RESTO DO CÓDIGO DA TUA UI AQUI, OS SLIDERS E TAL) ...
        // (Só garante que o setContentView tá no final)

        setContentView(ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#0d0d0d"))
            addView(root)
        })
    }
}
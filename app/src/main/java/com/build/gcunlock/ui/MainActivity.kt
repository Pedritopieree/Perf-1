package com.build.gcunlock.ui

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.view.Gravity
import android.graphics.Color

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this).apply {
            text = "GCUnlock ATIVO\n\nMódulo compilado com sucesso.\nAtive no LSPosed e reinicie."
            textSize = 18f
            setTextColor(Color.WHITE)
            setGravity(Gravity.CENTER)
            setBackgroundColor(Color.parseColor("#0d0d0d"))
        }
        setContentView(tv)
    }
}

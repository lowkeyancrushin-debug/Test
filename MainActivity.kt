package com.poolassistant.overlay

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (20 * resources.displayMetrics.density).toInt()
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(0xFFF4F6F8.toInt())
        }
        val title = TextView(this).apply {
            text = "Pool Aim Overlay"
            textSize = 25f
            setTextColor(0xFF17212B.toInt())
        }
        val subtitle = TextView(this).apply {
            text = "A no-key visual shot assistant"
            textSize = 15f
            setTextColor(0xFF52606D.toInt())
            setPadding(0, 8, 0, 24)
        }
        status = TextView(this).apply {
            textSize = 14f
            setTextColor(0xFF34495E.toInt())
            setPadding(0, 12, 0, 18)
        }
        val permission = Button(this).apply {
            text = "Grant display-over-apps permission"
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")))
                }
            }
        }
        val start = Button(this).apply {
            text = "Start floating menu"
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    !Settings.canDrawOverlays(this@MainActivity)) {
                    status.text = "Grant overlay permission first."
                } else {
                    val i = Intent(this@MainActivity, OverlayService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(i)
                    else startService(i)
                    status.text = "Floating menu started. Open 8 Ball Pool and use the bubble."
                }
            }
        }
        val stop = Button(this).apply {
            text = "Stop floating menu"
            setOnClickListener {
                stopService(Intent(this@MainActivity, OverlayService::class.java))
                status.text = "Floating menu stopped."
            }
        }
        val note = TextView(this).apply {
            text = "\nCurrent build: floating controls and manual shot-assistance UI.\n\nThis starter does not read the game screen, move the in-game cue, or fire shots. Ball recognition and automatic line placement need a separate screen-capture feature and your explicit Android permission."
            textSize = 14f
            setTextColor(0xFF52606D.toInt())
        }
        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(permission)
        layout.addView(start)
        layout.addView(stop)
        layout.addView(status)
        layout.addView(note)
        setContentView(layout)
    }

    override fun onResume() {
        super.onResume()
        if (::status.isInitialized && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            status.text = if (Settings.canDrawOverlays(this)) "Overlay permission: granted" else "Overlay permission: not granted"
        }
    }
}

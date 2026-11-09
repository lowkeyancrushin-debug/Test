package com.poolassistant.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private var root: View? = null
    private var expanded = false
    private val enabled = mutableMapOf(
        "Ghost Ball" to true,
        "Extended Lines" to true,
        "Bank Shot" to true,
        "Pocket Prediction" to true,
        "Auto Aim" to false,
        "Hard Hit" to false
    )

    override fun onCreate() {
        super.onCreate()
        createNotification()
        startForeground(41, notification())
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        showBubble()
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("pool_overlay", "Pool Aim Overlay",
                NotificationManager.IMPORTANCE_LOW)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun notification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            Notification.Builder(this, "pool_overlay") else Notification.Builder(this)
        return builder.setContentTitle("Pool Aim Overlay")
            .setContentText("Floating assistant is running")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()
    }

    @Suppress("DEPRECATION")
    private fun overlayType(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE

    private fun params(): WindowManager.LayoutParams {
        val type = overlayType()
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START; x = 24; y = 180 }
    }

    private fun showBubble() {
        val bubble = Button(this).apply {
            text = "8B"
            textSize = 15f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(25, 110, 75))
            setOnClickListener { if (expanded) showBubble() else showPanel() }
            setOnTouchListener(dragListener())
        }
        replaceView(bubble)
        expanded = false
    }

    private fun showPanel() {
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 14, 18, 14)
            setBackgroundColor(0xEE17212B.toInt())
        }
        val header = TextView(this).apply {
            text = "POOL AIM  •  NO KEY"
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(0, 0, 0, 10)
            setOnTouchListener(dragListener())
        }
        panel.addView(header)
        enabled.keys.forEach { name ->
            val b = Button(this).apply {
                text = label(name)
                textSize = 12f
                setOnClickListener {
                    enabled[name] = !(enabled[name] ?: false)
                    text = label(name)
                }
            }
            panel.addView(b)
        }
        val close = Button(this).apply {
            text = "Minimize"
            setOnClickListener { showBubble() }
        }
        panel.addView(close)
        val info = TextView(this).apply {
            text = "Manual visual tools only. Screen analysis is not active in this starter."
            textSize = 11f
            setTextColor(0xFFD5DEE7.toInt())
            setPadding(2, 8, 2, 0)
        }
        panel.addView(info)
        replaceView(panel)
        expanded = true
    }

    private fun label(name: String): String =
        (if (enabled[name] == true) "ON  " else "OFF ") + name

    private fun replaceView(view: View) {
        root?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        root = view
        try { wm.addView(view, params()) } catch (_: Exception) { stopSelf() }
    }

    private fun dragListener(): View.OnTouchListener {
        var sx = 0
        var sy = 0
        var tx = 0f
        var ty = 0f
        return View.OnTouchListener { v, event ->
            val target = root ?: return@OnTouchListener false
            val lp = target.layoutParams as? WindowManager.LayoutParams ?: return@OnTouchListener false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> { sx = lp.x; sy = lp.y; tx = event.rawX; ty = event.rawY; true }
                MotionEvent.ACTION_MOVE -> {
                    lp.x = sx + (event.rawX - tx).toInt()
                    lp.y = sy + (event.rawY - ty).toInt()
                    try { wm.updateViewLayout(target, lp) } catch (_: Exception) {}
                    true
                }
                else -> false
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onDestroy() {
        root?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        root = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

package com.af.pb.ads

import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import com.af.pb.utils.FirebaseConfigManager
import com.af.pb.utils.gone
import com.af.pb.utils.visible

class NativeFullAdCloseHelper(
    private val btnClose: View,
    private val tvCountDown: TextView? = null,
) {
    private var showCloseTimer: CountDownTimer? = null
    private var autoCloseTimer: CountDownTimer? = null

    fun show(shouldShow: () -> Boolean = { true }) {
        cancel()
        val timeToShowCloseButton = FirebaseConfigManager.instance().adConfig.timeShowCloseObFull
        if (timeToShowCloseButton == -1) return

        if (timeToShowCloseButton == 0) {
            if (!shouldShow()) return
            btnClose.visible()
            startAutoCloseTimer()
            return
        }

        tvCountDown?.visible()
        showCloseTimer = object : CountDownTimer(timeToShowCloseButton * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished + 1000) / 1000
                tvCountDown?.text = "$secondsLeft"
            }

            override fun onFinish() {
                if (!shouldShow()) return
                btnClose.visible()
                tvCountDown?.gone()
                startAutoCloseTimer()
            }
        }
        showCloseTimer?.start()
    }

    private fun startAutoCloseTimer() {
        val timeAutoCloseNativeFull = FirebaseConfigManager.instance().adConfig.timeAutoCloseNativeFull
        if (timeAutoCloseNativeFull <= 0) return
        autoCloseTimer = object : CountDownTimer(timeAutoCloseNativeFull * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                btnClose.performClick()
            }
        }
        autoCloseTimer?.start()
    }

    fun cancelAutoClose() {
        autoCloseTimer?.cancel()
    }

    fun cancel() {
        showCloseTimer?.cancel()
        autoCloseTimer?.cancel()
    }
}

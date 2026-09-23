package com.af.pb.component.flashalert.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.activity.viewModels
import com.af.pb.base.activity.BaseActivity
import com.af.pb.component.flashalert.viewmodel.TestCallViewModel
import com.af.pb.databinding.ActivityTestCallBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestCallActivity : BaseActivity<ActivityTestCallBinding>() {

    private val viewModel: TestCallViewModel by viewModels()

    private var speedOnMs: Long = 500L
    private var speedOffMs: Long = 500L

    override fun provideViewBinding(): ActivityTestCallBinding {
        return ActivityTestCallBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        speedOnMs = intent.getLongExtra(EXTRA_SPEED_ON, 500L)
        speedOffMs = intent.getLongExtra(EXTRA_SPEED_OFF, 500L)
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initViews() = with(viewBinding) {
        super.initViews()

        btnBack.setOnClickListener {
            answerAndExit()
        }

        btnAnswerCall.setOnClickListener {
            answerAndExit()
        }

        val swipeAnimation = TranslateAnimation(0f, 0f, 0f, -24f).apply {
            duration = 800
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
        }
        imgSwipeUp.startAnimation(swipeAnimation)

        var startY = 0f
        btnAnswerCall.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaY = startY - event.rawY
                    if (deltaY > 100) {
                        answerAndExit()
                        true
                    } else false
                }
                MotionEvent.ACTION_UP -> {
                    if (startY - event.rawY <= 100) {
                        answerAndExit()
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startCallTest(this, speedOnMs, speedOffMs)
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopCallTest(this)
    }

    private fun answerAndExit() {
        viewModel.stopCallTest(this)
        finish()
    }

    companion object {
        const val EXTRA_SPEED_ON = "extra_speed_on"
        const val EXTRA_SPEED_OFF = "extra_speed_off"

        fun start(context: Context, speedOnMs: Long, speedOffMs: Long) {
            Intent(context, TestCallActivity::class.java).apply {
                putExtra(EXTRA_SPEED_ON, speedOnMs)
                putExtra(EXTRA_SPEED_OFF, speedOffMs)
            }.also {
                context.startActivity(it)
            }
        }
    }
}

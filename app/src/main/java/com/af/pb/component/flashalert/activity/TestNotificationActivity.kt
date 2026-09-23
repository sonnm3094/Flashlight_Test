package com.af.pb.component.flashalert.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.af.pb.base.activity.BaseActivity
import com.af.pb.component.flashalert.viewmodel.TestNotificationViewModel
import com.af.pb.databinding.ActivityTestNotificationBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TestNotificationActivity : BaseActivity<ActivityTestNotificationBinding>() {

    private val viewModel: TestNotificationViewModel by viewModels()

    private var speedOnMs: Long = 500L
    private var speedOffMs: Long = 500L

    override fun provideViewBinding(): ActivityTestNotificationBinding {
        return ActivityTestNotificationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        speedOnMs = intent.getLongExtra(EXTRA_SPEED_ON, 500L)
        speedOffMs = intent.getLongExtra(EXTRA_SPEED_OFF, 500L)
        super.onCreate(savedInstanceState)
    }

    override fun initViews() {
        super.initViews()

        viewBinding.btnBack.setOnClickListener {
            viewModel.stopNotificationTest(this)
            finish()
        }

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        viewBinding.tvClock.text = currentTime
    }

    override fun onResume() {
        super.onResume()
        viewModel.startNotificationTest(this, speedOnMs, speedOffMs)
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopNotificationTest(this)
    }

    companion object {
        const val EXTRA_SPEED_ON = "extra_speed_on"
        const val EXTRA_SPEED_OFF = "extra_speed_off"

        fun start(context: Context, speedOnMs: Long, speedOffMs: Long) {
            Intent(context, TestNotificationActivity::class.java).apply {
                putExtra(EXTRA_SPEED_ON, speedOnMs)
                putExtra(EXTRA_SPEED_OFF, speedOffMs)
            }.also {
                context.startActivity(it)
            }
        }
    }
}

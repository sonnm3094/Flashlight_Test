package com.af.pb.component.flashalert.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.af.pb.base.activity.BaseActivity
import com.af.pb.component.flashalert.viewmodel.TestSmsViewModel
import com.af.pb.databinding.ActivityTestSmsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestSmsActivity : BaseActivity<ActivityTestSmsBinding>() {

    private val viewModel: TestSmsViewModel by viewModels()

    private var speedOnMs: Long = 500L
    private var speedOffMs: Long = 500L

    override fun provideViewBinding(): ActivityTestSmsBinding {
        return ActivityTestSmsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        speedOnMs = intent.getLongExtra(EXTRA_SPEED_ON, 500L)
        speedOffMs = intent.getLongExtra(EXTRA_SPEED_OFF, 500L)
        super.onCreate(savedInstanceState)
    }

    override fun initViews() {
        super.initViews()

        viewBinding.btnBack.setOnClickListener {
            viewModel.stopSmsTest(this@TestSmsActivity)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startSmsTest(this, speedOnMs, speedOffMs)
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopSmsTest(this)
    }

    companion object {
        const val EXTRA_SPEED_ON = "extra_speed_on"
        const val EXTRA_SPEED_OFF = "extra_speed_off"

        fun start(context: Context, speedOnMs: Long, speedOffMs: Long) {
            Intent(context, TestSmsActivity::class.java).apply {
                putExtra(EXTRA_SPEED_ON, speedOnMs)
                putExtra(EXTRA_SPEED_OFF, speedOffMs)
            }.also {
                context.startActivity(it)
            }
        }
    }
}

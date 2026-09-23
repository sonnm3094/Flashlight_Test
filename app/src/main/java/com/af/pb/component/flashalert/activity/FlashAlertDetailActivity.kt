package com.af.pb.component.flashalert.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.af.pb.R
import com.af.pb.base.activity.BaseActivity
import com.af.pb.databinding.ActivityFlashAlertDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class FlashAlertDetailActivity : BaseActivity<ActivityFlashAlertDetailBinding>() {

    private var alertType: Int = TYPE_CALL

    override fun provideViewBinding(): ActivityFlashAlertDetailBinding {
        return ActivityFlashAlertDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        alertType = intent.getIntExtra(EXTRA_ALERT_TYPE, TYPE_CALL)
        super.onCreate(savedInstanceState)
    }

    override fun initViews() {
        super.initViews()
        val binding = viewBinding

        binding.btnBack.setOnClickListener { finish() }

        when (alertType) {
            TYPE_CALL -> {
                binding.tvTitle.text = getString(R.string.incoming_calls)
                binding.imgHeaderIcon.setImageResource(R.drawable.ic_header_incoming_calls)
                binding.btnSelectApp.visibility = View.GONE
            }
            TYPE_SMS -> {
                binding.tvTitle.text = getString(R.string.sms_text)
                binding.imgHeaderIcon.setImageResource(R.drawable.ic_header_sms)
                binding.btnSelectApp.visibility = View.GONE
            }
            TYPE_NOTIFICATION -> {
                binding.tvTitle.text = getString(R.string.notification_text)
                binding.imgHeaderIcon.setImageResource(R.drawable.ic_header_notification)
                binding.btnSelectApp.visibility = View.VISIBLE
            }
        }

        // Separate Switch Card
        val switchCardBinding = binding.layoutSwitchCard
        switchCardBinding.tvStatus.text = "Status: Off"
        switchCardBinding.swStatus.setOnCheckedChangeListener { _, isChecked ->
            switchCardBinding.tvStatus.text = if (isChecked) "Status: On" else "Status: Off"
        }

        // Flashing speed sliders
        val speedFormatter = { value: Int -> String.format(Locale.US, "%.1fs", value / 10f) }

        val speedCardBinding = binding.layoutFlashingSpeedCard
        speedCardBinding.cardFlashingOn.setRange(1, 50)
        speedCardBinding.cardFlashingOn.setValueFormatter(speedFormatter)
        speedCardBinding.cardFlashingOn.setValue(5) // 0.5s

        speedCardBinding.cardFlashingOff.setRange(1, 50)
        speedCardBinding.cardFlashingOff.setValueFormatter(speedFormatter)
        speedCardBinding.cardFlashingOff.setValue(5) // 0.5s

        binding.btnSelectApp.setOnClickListener {
            // Select App
        }

        binding.btnTest.setOnClickListener {
            // Test Flash Alert
        }
    }

    companion object {
        const val EXTRA_ALERT_TYPE = "extra_alert_type"
        const val TYPE_CALL = 1
        const val TYPE_SMS = 2
        const val TYPE_NOTIFICATION = 3

        fun start(context: Context, type: Int) {
            Intent(context, FlashAlertDetailActivity::class.java).apply {
                putExtra(EXTRA_ALERT_TYPE, type)
            }.also {
                context.startActivity(it)
            }
        }
    }
}

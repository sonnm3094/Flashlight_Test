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

    override fun initViews() = with(viewBinding) {
        super.initViews()

        btnBack.setOnClickListener { finish() }

        when (alertType) {
            TYPE_CALL -> {
                tvTitle.text = getString(R.string.incoming_calls)
                imgHeaderIcon.setImageResource(R.drawable.ic_header_incoming_calls)
                btnSelectApp.visibility = View.GONE
            }
            TYPE_SMS -> {
                tvTitle.text = getString(R.string.sms_text)
                imgHeaderIcon.setImageResource(R.drawable.ic_header_sms)
                btnSelectApp.visibility = View.GONE
            }
            TYPE_NOTIFICATION -> {
                tvTitle.text = getString(R.string.notification_text)
                imgHeaderIcon.setImageResource(R.drawable.ic_header_notification)
                btnSelectApp.visibility = View.VISIBLE
            }
        }

        layoutSwitchCard.tvStatus.text = "Status: Off"
        layoutSwitchCard.swStatus.setOnCheckedChangeListener { _, isChecked ->
            layoutSwitchCard.tvStatus.text = if (isChecked) "Status: On" else "Status: Off"
        }

        val speedFormatter = { value: Int -> String.format(Locale.US, "%.1fs", value / 10f) }

        layoutFlashingSpeedCard.cardFlashingOn.apply {
            setRange(1, 50)
            setValueFormatter(speedFormatter)
            setValue(5) // 0.5s
        }

        layoutFlashingSpeedCard.cardFlashingOff.apply {
            setRange(1, 50)
            setValueFormatter(speedFormatter)
            setValue(5) // 0.5s
        }

        btnSelectApp.setOnClickListener {

        }

        btnTest.setOnClickListener {

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

package com.af.pb.component.flashalert.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.af.pb.R
import com.af.pb.base.fragment.BaseFragment
import com.af.pb.component.flashalert.activity.FlashAlertDetailActivity
import com.af.pb.databinding.FragmentFlashAlertBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FlashAlertFragment : BaseFragment<FragmentFlashAlertBinding>() {

    override fun provideViewBinding(container: ViewGroup?): FragmentFlashAlertBinding {
        return FragmentFlashAlertBinding.inflate(LayoutInflater.from(context), container, false)
    }

    override fun initViews() {
        super.initViews()

        viewBinding.btnIncomingCalls.apply {
            imgIcon.setImageResource(R.drawable.ic_phone)
            tvTitle.setText(R.string.incoming_calls)
            root.setOnClickListener {
                FlashAlertDetailActivity.start(requireContext(), FlashAlertDetailActivity.TYPE_CALL)
            }
        }

        viewBinding.btnSms.apply {
            imgIcon.setImageResource(R.drawable.ic_mail)
            tvTitle.setText(R.string.sms_text)
            root.setOnClickListener {
                FlashAlertDetailActivity.start(requireContext(), FlashAlertDetailActivity.TYPE_SMS)
            }
        }

        viewBinding.btnNotification.apply {
            imgIcon.setImageResource(R.drawable.ic_notify)
            tvTitle.setText(R.string.notification_text)
            root.setOnClickListener {
                FlashAlertDetailActivity.start(requireContext(), FlashAlertDetailActivity.TYPE_NOTIFICATION)
            }
        }
    }

    companion object {
        fun newInstance() = FlashAlertFragment()
    }
}

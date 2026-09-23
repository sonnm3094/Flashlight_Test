package com.af.pb.dialog

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.af.pb.base.dialog.BaseDialog
import com.af.pb.databinding.DialogNoInternetBinding

class NoInternetDialog(private val context: Context) :
    BaseDialog<DialogNoInternetBinding>(context) {

    var onRetry: () -> Unit = {}
    var onCancel: () -> Unit = {}

    override fun provideViewBinding(): DialogNoInternetBinding {
        return DialogNoInternetBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setCancelable(false)

        btnRetry.setOnClickListener {
            dismiss()
            try {
                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            onRetry.invoke()
        }

        btnCancel.setOnClickListener {
            dismiss()
            onCancel.invoke()
        }
    }

}

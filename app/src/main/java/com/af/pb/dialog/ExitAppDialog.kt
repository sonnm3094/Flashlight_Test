package com.af.pb.dialog

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.af.pb.ads.NativeAdsUtils
import com.af.pb.base.dialog.BaseDialog
import com.af.pb.databinding.DialogExitAppBinding

class ExitAppDialog(private val context: Context) :
    BaseDialog<DialogExitAppBinding>(context) {

    var onExit: () -> Unit = {}

    override fun provideViewBinding(): DialogExitAppBinding {
        return DialogExitAppBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setCancelable(false)

        btnKeepUsingApp.setOnClickListener {
            dismiss()
        }

        btnExitApp.setOnClickListener {
            dismiss()
            onExit.invoke()
        }

        initAds()
    }

    private fun initAds() {
        val activity = context as? FragmentActivity ?: return
        NativeAdsUtils.loadAndShowNativeExitApp(
            activity,
            activity,
            viewBinding.frAdsNative,
            viewBinding.shimmerContainerNative.shimmerContainerNative
        )
    }

}

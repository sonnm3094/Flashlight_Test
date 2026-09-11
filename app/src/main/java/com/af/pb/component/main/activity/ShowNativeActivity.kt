package com.af.pb.component.main.activity

import android.app.Activity
import android.content.Intent
import com.af.pb.ads.NativeAdsUtils
import com.af.pb.base.activity.BaseActivity
import com.af.pb.databinding.ActivityShowNativeBinding

class ShowNativeActivity : BaseActivity<ActivityShowNativeBinding>() {
    override fun provideViewBinding(): ActivityShowNativeBinding {
        return ActivityShowNativeBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        super.initViews()
        initNativeType1()
        initNativeType2()

        viewBinding.btnClose.setOnClickListener {
            onBack()
        }
    }

    private fun initNativeType1() = with(viewBinding) {
        NativeAdsUtils.loadAndShowNativeSetting(
            this@ShowNativeActivity,
            this@ShowNativeActivity,
            frAdsNativeType1,
            shimmerContainerNative1.shimmerContainerNative
        )
    }

    private fun initNativeType2() = with(viewBinding) {
        NativeAdsUtils.loadAndShowNativeExitApp(
            this@ShowNativeActivity,
            this@ShowNativeActivity,
            frAdsNativeType2,
            shimmerContainerNative2.shimmerContainerNative
        )
    }

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, ShowNativeActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
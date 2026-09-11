package com.af.pb.component.main.activity

import android.app.Activity
import android.content.Intent
import com.af.pb.ads.BannerAdsUtils
import com.af.pb.base.activity.BaseActivity
import com.af.pb.databinding.ActivityShowBannerBinding

class ShowBannerActivity : BaseActivity<ActivityShowBannerBinding>() {
    override fun provideViewBinding(): ActivityShowBannerBinding {
        return ActivityShowBannerBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        BannerAdsUtils.initBannerAll(this@ShowBannerActivity, this@ShowBannerActivity, frAdsBanner)
        BannerAdsUtils.initBannerCollapse(this@ShowBannerActivity, this@ShowBannerActivity, frAdsBannerCollapse)

        viewBinding.btnClose.setOnClickListener {
            onBack()
        }
    }


    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, ShowBannerActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
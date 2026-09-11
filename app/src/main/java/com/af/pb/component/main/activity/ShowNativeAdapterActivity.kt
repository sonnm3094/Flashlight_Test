package com.af.pb.component.main.activity

import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ads.admob.helper.adnative.AdmobNativeAdAdapter
import com.ads.admob.helper.adnative.NativeAdapterConfig
import com.af.pb.BuildConfig
import com.af.pb.R
import com.af.pb.base.activity.BaseActivity
import com.af.pb.component.main.adapter.TestNativeAdapter
import com.af.pb.databinding.ActivityShowNativeAdapterBinding
import com.af.pb.utils.FirebaseConfigManager

class ShowNativeAdapterActivity : BaseActivity<ActivityShowNativeAdapterBinding>() {
    private val adapter by lazy { TestNativeAdapter() }

    override fun provideViewBinding(): ActivityShowNativeAdapterBinding {
        return ActivityShowNativeAdapterBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        super.initViews()
        viewBinding.toolBar.btnBack.setOnClickListener { onBack() }

        val layoutManager = GridLayoutManager(this, 2)
        val nativeConfig = NativeAdapterConfig.Builder(
            nativeAdId = BuildConfig.native_language,
            adapter = adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>,
            itemNativeAd = R.layout.item_native_ad,
            nativeContentView = com.ads.admob.R.layout.layout_native_ad_view_square,
            firstPositionNativeApp = 2,
            gridLayoutManager = layoutManager,
            adItemInterval = 2,
            isRepeat = true,
            adPlacement = "native_language"
        ).build()

        val adapterWithAd = AdmobNativeAdAdapter(nativeConfig)
        if (FirebaseConfigManager.instance().adConfig.enableNativeLanguage) {
            viewBinding.rcvTest.adapter = adapterWithAd
        } else {
            viewBinding.rcvTest.adapter = adapter
        }
    }

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, ShowNativeAdapterActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
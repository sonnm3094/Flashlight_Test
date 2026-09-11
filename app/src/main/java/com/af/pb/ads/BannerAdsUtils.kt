package com.af.pb.ads

import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.ads.admob.BannerCollapsibleGravity
import com.ads.admob.helper.banner.BannerAdConfig
import com.ads.admob.helper.banner.BannerAdHelper
import com.ads.admob.helper.banner.params.BannerAdParam
import com.af.pb.BuildConfig
import com.af.pb.utils.FirebaseConfigManager

object BannerAdsUtils {

    var bannerAdMain: BannerAdHelper? = null
    var bannerAdCollapse: BannerAdHelper? = null

    private fun setBanner(activity: AppCompatActivity, lifecycleOwner: LifecycleOwner, bannerContentView: FrameLayout, config: BannerAdConfig) {
        val bannerAdHelper = BannerAdHelper(activity = activity, lifecycleOwner = lifecycleOwner, config = config)
        bannerAdHelper.setBannerContentView(bannerContentView)
        bannerAdHelper.requestAds(BannerAdParam.Request)
    }

    fun initBannerAll(activity: AppCompatActivity, lifecycleOwner: LifecycleOwner, bannerContentView: FrameLayout) {
        val config = BannerAdConfig(
            idAds = BuildConfig.banner_all,
            canShowAds = FirebaseConfigManager.instance().adConfig.enableBannerAll,
            canReloadAds = false,
            adPlacement = "banner_all",
            reloadIfFirstFail = true
        )
        bannerAdMain = BannerAdHelper(activity = activity, lifecycleOwner = lifecycleOwner, config = config)
        bannerAdMain?.setBannerContentView(bannerContentView)
        bannerAdMain?.requestAds(BannerAdParam.Request)
    }

    fun initBannerCollapse(activity: AppCompatActivity, lifecycleOwner: LifecycleOwner, bannerContentView: FrameLayout) {
        val config = BannerAdConfig(
            idAds = BuildConfig.banner_collap,
            canShowAds = FirebaseConfigManager.instance().adConfig.enableBannerCollapse,
            canReloadAds = false,
            adPlacement = "banner_collap",
            reloadIfFirstFail = true,
            refreshIntervalSeconds = 50
        ).apply { collapsibleGravity = BannerCollapsibleGravity.BOTTOM }
        bannerAdCollapse = BannerAdHelper(activity = activity, lifecycleOwner = lifecycleOwner, config = config)
        bannerAdCollapse?.setBannerContentView(bannerContentView)
        bannerAdCollapse?.requestAds(BannerAdParam.Request)
    }

    fun cancelAllAds(isPurchased: Boolean) {
        bannerAdMain?.cancelRequestAndShowAllAds(isPurchased)
        bannerAdMain?.cancel()
        bannerAdCollapse?.cancelRequestAndShowAllAds(isPurchased)
        bannerAdCollapse?.cancel()

    }
}
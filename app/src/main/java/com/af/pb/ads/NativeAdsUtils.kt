package com.af.pb.ads

import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.ads.admob.R
import com.ads.admob.helper.AdOptionVisibility
import com.ads.admob.helper.adnative.NativeAdConfig
import com.ads.admob.helper.adnative.NativeAdHelper
import com.ads.admob.helper.adnative.params.AdNativeState
import com.ads.admob.helper.adnative.params.NativeAdParam
import com.facebook.shimmer.ShimmerFrameLayout
import com.af.pb.BuildConfig
import com.af.pb.utils.FirebaseConfigManager
import com.af.pb.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object NativeAdsUtils {

    private val lifecycleOwner = ProcessLifecycleOwner.get()

    var nativeFullSplash: NativeAdHelper? = null
        private set
    var nativeLanguage: NativeAdHelper? = null
        private set
    var nativeLanguageSelect: NativeAdHelper? = null
        private set

    var nativeObd1: NativeAdHelper? = null
        private set
    var nativeObd2: NativeAdHelper? = null
        private set
    var nativeObd3: NativeAdHelper? = null
        private set
    var nativeObd4: NativeAdHelper? = null
        private set
    var nativeOb12Full: NativeAdHelper? = null
        private set
    var nativeOb23Full: NativeAdHelper? = null
        private set
    var nativeOb34Full: NativeAdHelper? = null
        private set
    var nativeFullPlayFilm: NativeAdHelper? = null
        private set


    private fun destroyAds() {
        nativeFullSplash = null
        nativeLanguage = null
        nativeLanguageSelect = null
        nativeObd1 = null
        nativeObd2 = null
        nativeObd3 = null
        nativeObd4 = null
        nativeOb12Full = null
        nativeOb23Full = null
        nativeOb34Full = null
        nativeFullPlayFilm = null
    }

    fun cancelAllAds(isPurchased: Boolean) {
        nativeLanguage?.cancelRequestAndShowAllAds(isPurchased)
        nativeLanguage?.cancel()
        nativeFullSplash?.cancelRequestAndShowAllAds(isPurchased)
        nativeFullSplash?.cancel()
        nativeLanguageSelect?.cancelRequestAndShowAllAds(isPurchased)
        nativeLanguageSelect?.cancel()
        nativeObd1?.cancelRequestAndShowAllAds(isPurchased)
        nativeObd1?.cancel()
        nativeObd2?.cancelRequestAndShowAllAds(isPurchased)
        nativeObd2?.cancel()
        nativeObd3?.cancelRequestAndShowAllAds(isPurchased)
        nativeObd3?.cancel()
        nativeObd4?.cancelRequestAndShowAllAds(isPurchased)
        nativeObd4?.cancel()
        nativeOb12Full?.cancelRequestAndShowAllAds(isPurchased)
        nativeOb12Full?.cancel()
        nativeOb23Full?.cancelRequestAndShowAllAds(isPurchased)
        nativeOb23Full?.cancel()
        nativeOb34Full?.cancelRequestAndShowAllAds(isPurchased)
        nativeOb34Full?.cancel()
        nativeFullPlayFilm?.cancelRequestAndShowAllAds(isPurchased)
        nativeFullPlayFilm?.cancel()
    }

    fun preLoadNativeFullSplash(activity: AppCompatActivity) {
        val isEnable = FirebaseConfigManager.instance().adConfig.enableNativeFullSplash
        val idAdsPriority = if (FirebaseConfigManager.instance().adConfig.enableNativeFullSplash2f) BuildConfig.native_full_splash_2f else null

        if (!isEnable) return
        if (nativeFullSplash != null) return

        val nativeAdConfig = NativeAdConfig(
            idAds = BuildConfig.native_full_splash,
            idAdsPriority = idAdsPriority,
            canShowAds = true,
            reloadIfFirstFail = true,
            canReloadAds = false,
            layoutId = R.layout.layout_native_fullscreen_view,
            adPlacement = "native_full_splash"
        )
        nativeFullSplash = NativeAdHelper(activity, lifecycleOwner, nativeAdConfig).apply {
            adVisibility = AdOptionVisibility.GONE
        }
        nativeFullSplash?.requestAds(NativeAdParam.Request)
        Logger.e("NativeAdsUtils preLoadNativeFullSplash")
    }

    fun preLoadNativeLanguage(activity: AppCompatActivity) {
        val isEnable = FirebaseConfigManager.instance().adConfig.enableNativeLanguage
        val idAdsPriority = if (FirebaseConfigManager.instance().adConfig.enableNativeLanguage2f) BuildConfig.native_language_2f else null
        val layoutId =
            if (FirebaseConfigManager.instance().adConfig.ctaLanguageTop) R.layout.layout_native_ad_view_type1_cta_top else R.layout.layout_native_ad_view_type1

        if (!isEnable) return
        if (nativeLanguage != null) return

        val nativeAdConfig = NativeAdConfig(
            idAds = BuildConfig.native_language,
            idAdsPriority = idAdsPriority,
            canShowAds = true,
            reloadIfFirstFail = true,
            canReloadAds = false,
            layoutId = layoutId,
            adPlacement = "native_language"
        )
        nativeLanguage = NativeAdHelper(activity, lifecycleOwner, nativeAdConfig).apply {
            adVisibility = AdOptionVisibility.GONE
        }
        nativeLanguage?.requestAds(NativeAdParam.Request)
        Logger.e("NativeAdsUtils preLoadNativeLanguage")
    }

    fun preLoadNativeLanguageSelect(activity: AppCompatActivity) {
        val isEnable = FirebaseConfigManager.instance().adConfig.enableNativeLanguageSelect
        val idAdsPriority =
            if (FirebaseConfigManager.instance().adConfig.enableNativeLanguageSelect2f) BuildConfig.native_language_select_2f else null
        val layoutId =
            if (FirebaseConfigManager.instance().adConfig.ctaLanguageTop) R.layout.layout_native_ad_view_type1_cta_top else R.layout.layout_native_ad_view_type1

        if (!isEnable) return
        if (nativeLanguageSelect != null) return

        val nativeAdConfig = NativeAdConfig(
            idAds = BuildConfig.native_language_select,
            idAdsPriority = idAdsPriority,
            canShowAds = true,
            reloadIfFirstFail = true,
            canReloadAds = false,
            layoutId = layoutId,
            adPlacement = "native_language_select"
        )
        nativeLanguageSelect = NativeAdHelper(activity, lifecycleOwner, nativeAdConfig).apply {
            adVisibility = AdOptionVisibility.GONE
        }
        nativeLanguageSelect?.requestAds(NativeAdParam.Request)
        Logger.e("NativeAdsUtils preLoadNativeLanguageSelect")
    }

    fun preLoadNativeObd1(activity: AppCompatActivity) {
        val isEnable = FirebaseConfigManager.instance().adConfig.enableNativeObd1
        val idAdsPriority = if (FirebaseConfigManager.instance().adConfig.enableNativeObd12f) BuildConfig.native_obd1_2f else null
        val layoutId =
            if (FirebaseConfigManager.instance().adConfig.ctaOnBoardingTop) R.layout.layout_native_ad_view_type1_cta_top else R.layout.layout_native_ad_view_type1

        if (!isEnable) return
        if (nativeObd1 != null) return

        val nativeAdConfig = NativeAdConfig(
            idAds = BuildConfig.native_obd1,
            idAdsPriority = idAdsPriority,
            canShowAds = true,
            reloadIfFirstFail = true,
            canReloadAds = false,
            layoutId = layoutId,
            adPlacement = "native_obd1"
        )
        nativeObd1 = NativeAdHelper(activity, lifecycleOwner, nativeAdConfig).apply {
            adVisibility = AdOptionVisibility.GONE
        }
        nativeObd1?.requestAds(NativeAdParam.Request)
        Logger.e("NativeAdsUtils preLoadNativeObd1")
    }

    fun preLoadNativeObd2(activity: AppCompatActivity) {
        val isEnable = FirebaseConfigManager.instance().adConfig.enableNativeObd2
        val idAdsPriority = if (FirebaseConfigManager.instance().adConfig.enableNativeObd22f) BuildConfig.native_obd2_2f else null
        val layoutId =
            if (FirebaseConfigManager.instance().adConfig.ctaOnBoardingTop) R.layout.layout_native_ad_view_type1_cta_top else R.layout.layout_native_ad_view_type1

        if (!isEnable) return
        if (nativeObd2 != null) return

        val nativeAdConfig = NativeAdConfig(
            idAds = BuildConfig.native_obd2,
            idAdsPriority = idAdsPriority,
            canShowAds = true,
            reloadIfFirstFail = true,
            canReloadAds = false,
            layoutId = layoutId,
            adPlacement = "native_obd2"
        )
        nativeObd2 = NativeAdHelper(activity, lifecycleOwner, nativeAdConfig).apply {
            adVisibility = AdOptionVisibility.GONE
        }
        nativeObd2?.requestAds(NativeAdParam.Request)
        Logger.e("NativeAdsUtils preLoadNativeOb2")
    }

    fun preLoadNativeObd3(activity: AppCompatActivity) {
        val isEnable = FirebaseConfigManager.instance().adConfig.enableNativeObd3
        val idAdsPriority = if (FirebaseConfigManager.instance().adConfig.enableNativeObd32f) BuildConfig.native_obd3_2f else null
        val layoutId =
            if (FirebaseConfigManager.instance().adConfig.ctaOnBoardingTop) R.layout.layout_native_ad_view_type1_cta_top else R.layout.layout_native_ad_view_type1

        if (!isEnable) return
        if (nativeObd3 != null) return

        val nativeAdConfig = NativeAdConfig(
            idAds = BuildConfig.native_obd3,
            idAdsPriority = idAdsPriority,
            canShowAds = true,
            reloadIfFirstFail = true,
            canReloadAds = false,
            layoutId = layoutId,
            adPlacement = "native_obd3"
        )
        nativeObd3 = NativeAdHelper(activity, lifecycleOwner, nativeAdConfig).apply {
            adVisibility = AdOptionVisibility.GONE
        }
        nativeObd3?.requestAds(NativeAdParam.Request)
        Logger.e("NativeAdsUtils preLoadNativeObd3")
    }

    fun preLoadNativeObd4(activity: AppCompatActivity) {
        val isEnable = FirebaseConfigManager.instance().adConfig.enableNativeObd4
        val layoutId =
            if (FirebaseConfigManager.instance().adConfig.ctaOnBoardingTop) R.layout.layout_native_ad_view_type1_cta_top else R.layout.layout_native_ad_view_type1

        if (!isEnable) return
        if (nativeObd4 != null) return

        val nativeAdConfig = NativeAdConfig(
            idAds = BuildConfig.native_obd4,
            canShowAds = true,
            reloadIfFirstFail = true,
            canReloadAds = false,
            layoutId = layoutId,
            adPlacement = "native_obd4"
        )
        nativeObd4 = NativeAdHelper(activity, lifecycleOwner, nativeAdConfig).apply {
            adVisibility = AdOptionVisibility.GONE
        }
        nativeObd4?.requestAds(NativeAdParam.Request)
        Logger.e("NativeAdsUtils preLoadNativeObd4")
    }

    fun preLoadNativeOb12Full(activity: AppCompatActivity) {
        val isEnable = FirebaseConfigManager.instance().adConfig.enableNativeOb12Full

        if (!isEnable) return
        if (nativeOb12Full != null) return

        val nativeAdConfig = NativeAdConfig(
            idAds = BuildConfig.native_ob12_full,
            canShowAds = true,
            reloadIfFirstFail = true,
            canReloadAds = false,
            layoutId = R.layout.layout_native_fullscreen_view,
            adPlacement = "native_ob12_full"
        )
        nativeOb12Full = NativeAdHelper(activity, lifecycleOwner, nativeAdConfig).apply {
            adVisibility = AdOptionVisibility.GONE
        }
        nativeOb12Full?.requestAds(NativeAdParam.Request)
        Logger.e("NativeAdsUtils preLoadNativeOb12Full")
    }

    fun preLoadNativeOb23Full(activity: AppCompatActivity) {
        val isEnable = FirebaseConfigManager.instance().adConfig.enableNativeOb23Full
        val idAdsPriority = if (FirebaseConfigManager.instance().adConfig.enableNativeOb23Full2f) BuildConfig.native_ob23_full_2f else null

        if (!isEnable) return
        if (nativeOb23Full != null) return

        val nativeAdConfig = NativeAdConfig(
            idAds = BuildConfig.native_ob23_full,
            idAdsPriority = idAdsPriority,
            canShowAds = true,
            reloadIfFirstFail = true,
            canReloadAds = false,
            layoutId = R.layout.layout_native_fullscreen_view,
            adPlacement = "native_ob23_full"
        )
        nativeOb23Full = NativeAdHelper(activity, lifecycleOwner, nativeAdConfig).apply {
            adVisibility = AdOptionVisibility.GONE
        }
        nativeOb23Full?.requestAds(NativeAdParam.Request)
        Logger.e("NativeAdsUtils preLoadNativeOb23Full")
    }

    fun preLoadNativeOb34Full(activity: AppCompatActivity) {
        val isEnable = FirebaseConfigManager.instance().adConfig.enableNativeOb34Full

        if (!isEnable) return
        if (nativeOb34Full != null) return

        val nativeAdConfig = NativeAdConfig(
            idAds = BuildConfig.native_ob34_full,
            canShowAds = true,
            reloadIfFirstFail = true,
            canReloadAds = false,
            layoutId = R.layout.layout_native_fullscreen_view,
            adPlacement = "native_ob34_full"
        )
        nativeOb34Full = NativeAdHelper(activity, lifecycleOwner, nativeAdConfig).apply {
            adVisibility = AdOptionVisibility.GONE
        }
        nativeOb34Full?.requestAds(NativeAdParam.Request)
        Logger.e("NativeAdsUtils preLoadNativeOb34Full")
    }

    fun loadAndShowNativeUninstall(
        activity: FragmentActivity, lifecycleOwner: LifecycleOwner, nativeContentView: FrameLayout, shimmerContainerNative: ShimmerFrameLayout
    ) {
        val config = NativeAdConfig(
            idAds = BuildConfig.native_uninstall,
            canShowAds = FirebaseConfigManager.instance().adConfig.enableNativeUninstall,
            canReloadAds = false,
            layoutId = R.layout.layout_native_ad_view_type2,
            adPlacement = "native_uninstall"
        )
        setNative(activity, config, lifecycleOwner, nativeContentView, shimmerContainerNative)
    }

    fun loadAndShowNativeExitApp(
        activity: FragmentActivity, lifecycleOwner: LifecycleOwner, nativeContentView: FrameLayout, shimmerContainerNative: ShimmerFrameLayout
    ) {
        val config = NativeAdConfig(
            idAds = BuildConfig.native_exit,
            canShowAds = FirebaseConfigManager.instance().adConfig.enableNativeExit,
            canReloadAds = false,
            layoutId = R.layout.layout_native_ad_view_type2,
            adPlacement = "native_exit"
        )
        setNative(activity, config, lifecycleOwner, nativeContentView, shimmerContainerNative)
    }

    fun loadAndShowNativeSetting(
        activity: FragmentActivity, lifecycleOwner: LifecycleOwner, nativeContentView: FrameLayout, shimmerContainerNative: ShimmerFrameLayout
    ) {
        val config = NativeAdConfig(
            idAds = BuildConfig.native_setting,
            canShowAds = FirebaseConfigManager.instance().adConfig.enableNativeSetting,
            canReloadAds = false,
            layoutId = R.layout.layout_native_ad_view_type1,
            adPlacement = "native_setting"
        )
        setNative(activity, config, lifecycleOwner, nativeContentView, shimmerContainerNative)
    }

    fun loadAndShowNativeRemove(
        activity: FragmentActivity,
        lifecycleOwner: LifecycleOwner,
        nativeContentView: FrameLayout,
        shimmerContainerNative: ShimmerFrameLayout,
        onAdLoaded: (() -> Unit)? = null,
        onAdFailedToLoad: (() -> Unit)? = null
    ) {
        val config = NativeAdConfig(
            idAds = BuildConfig.native_full_remove,
            canShowAds = FirebaseConfigManager.instance().adConfig.enableNativeFullRemove,
            canReloadAds = false,
            layoutId = R.layout.layout_native_fullscreen_view,
            adPlacement = "native_full_remove"
        )
        setNative(activity, config, lifecycleOwner, nativeContentView, shimmerContainerNative, onAdLoaded, onAdFailedToLoad)
    }

    private fun setNative(
        activity: FragmentActivity,
        config: NativeAdConfig,
        lifecycleOwner: LifecycleOwner,
        nativeContentView: FrameLayout,
        shimmerContainerNative: ShimmerFrameLayout,
        onAdLoaded: (() -> Unit)? = null,
        onAdFailedToLoad: (() -> Unit)? = null
    ) {
        val nativeAdHelper = NativeAdHelper(
            activity, lifecycleOwner, config
        ).apply {
            adVisibility = AdOptionVisibility.GONE
        }

        activity.lifecycleScope.launch(Dispatchers.Main) {
            nativeAdHelper.setNativeContentView(nativeContentView)
            nativeAdHelper.setShimmerLayoutView(shimmerContainerNative)
            activity.lifecycleScope.launch(Dispatchers.IO) {
                nativeAdHelper.requestAds(NativeAdParam.Request)
            }
            if (onAdLoaded != null || onAdFailedToLoad != null) {
                val state = nativeAdHelper.getAdNativeState().first { it is AdNativeState.Loaded || it is AdNativeState.Fail }
                if (state is AdNativeState.Loaded) onAdLoaded?.invoke() else onAdFailedToLoad?.invoke()
            }
        }
    }

}
package com.af.pb.ads

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import com.ads.admob.data.ContentAd
import com.ads.admob.helper.interstitial.InterstitialAdConfig
import com.ads.admob.helper.interstitial.InterstitialAdsHelper
import com.ads.admob.listener.InterstitialAdRequestCallBack
import com.ads.admob.listener.InterstitialAdShowCallBack
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.af.pb.App
import com.af.pb.BuildConfig
import com.af.pb.utils.FirebaseConfigManager
import com.af.pb.utils.Logger

object InterAdsUtils {

    fun loadInterAds(
        activity: Activity, idAdsPriority: String? = null, idAds: String, adPlacement: String, isEnable: Boolean, isLoadAndShow: Boolean = false,
        onAdLoaded: (() -> Unit)? = null, onAdLoadFail: (() -> Unit)? = null
    ) {
        InterstitialAdsHelper.getInstance(adPlacement).setInterstitialAdConfig(
            InterstitialAdConfig(
                idAdsPriority = idAdsPriority,
                idAds = idAds,
                canShowAds = isEnable,
                canReloadAds = false,
                adPlacement = adPlacement,
                reloadIfFirstFail = true,
            )
        )
        loadInter(activity, adPlacement, isLoadAndShow, onAdLoaded, onAdLoadFail)
    }


    fun showInterAds(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        adPlacement: String,
        isReloadWhenClose: Boolean = false,
        action: () -> Unit
    ) {
        InterstitialAdsHelper.getInstance(adPlacement)
            .forceShowInterstitial(activity, lifecycleOwner, object : InterstitialAdShowCallBack {
                override fun onNextAction() {
                    action.invoke()
                }

                override fun onAdClose() {
                    checkReloadInter(activity, adPlacement, isReloadWhenClose)
                    action.invoke()
                }

                override fun onInterstitialShow() {
                    Logger.e("InterAds: onInterstitialShow $adPlacement")
                }

                override fun onAdClicked() {
                }

                override fun onAdImpression() {
                }

                override fun onAdFailedToShow(adError: AdError) {
                    checkReloadInter(activity, adPlacement, isReloadWhenClose)
                    action.invoke()
                }

            })
    }

    fun loadAndShowInterAds(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        idAds: String,
        adPlacement: String,
        isEnable: Boolean,
        action: () -> Unit
    ) {
        loadInterAds(
            activity,
            idAds = idAds,
            adPlacement = adPlacement,
            isEnable = isEnable,
            isLoadAndShow = true,
            onAdLoaded = {
                showInterAds(activity, lifecycleOwner, adPlacement, action = action)
            },
            onAdLoadFail = { action.invoke() })
    }

    private fun loadInter(
        activity: Activity,
        adPlacement: String,
        isLoadAndShow: Boolean = false,
        onAdLoaded: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        InterstitialAdsHelper.getInstance(adPlacement).requestInterAds(activity, isLoadAndShow, object : InterstitialAdRequestCallBack {
            override fun onAdLoaded(data: ContentAd) {
                Logger.e("$adPlacement : onAdLoaded")
                onAdLoaded?.invoke()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Logger.e("$adPlacement : onAdFailedToLoad: ${loadAdError.message}")
                onAdLoadFail?.invoke()
            }

        })
    }

    private fun checkReloadInter(context: Activity, adPlacement: String, isReloadWhenClose: Boolean) {
        if (isReloadWhenClose) {
            loadInter(context, adPlacement)
        }
    }

    fun showInterFunction(activity: Activity, lifecycleOwner: LifecycleOwner, action: () -> Unit) {
        App.currentInterShowFrequency++
        if (App.currentInterShowFrequency % (FirebaseConfigManager.instance().adConfig.interShowFrequency) == 0) {
            loadAndShowInterAds(
                activity = activity,
                lifecycleOwner = lifecycleOwner,
                idAds = BuildConfig.inter_function,
                adPlacement = AdPlacement.INTER_FUNCTION,
                isEnable = FirebaseConfigManager.instance().adConfig.enableInterFunction,
                action = action
            )
        } else {
            action.invoke()
        }
    }

    fun showInterHome(activity: Activity, lifecycleOwner: LifecycleOwner, action: () -> Unit) {
        if (System.currentTimeMillis() - App.currentTime >= (FirebaseConfigManager.instance().adConfig.interval * 1000)) {
            loadAndShowInterAds(
                activity = activity,
                lifecycleOwner = lifecycleOwner,
                idAds = BuildConfig.inter_home,
                adPlacement = AdPlacement.INTER_HOME,
                isEnable = FirebaseConfigManager.instance().adConfig.enableInterHome,
                action = {
                    App.currentTime = System.currentTimeMillis()
                    action.invoke()
                }
            )
        } else {
            action.invoke()
        }
    }

    fun showInterExit(activity: Activity, lifecycleOwner: LifecycleOwner, action: () -> Unit) {
        App.currentInterShowFrequency++
        if (App.currentInterShowFrequency % (FirebaseConfigManager.instance().adConfig.interShowFrequency) == 0) {
            loadAndShowInterAds(
                activity = activity,
                lifecycleOwner = lifecycleOwner,
                idAds = BuildConfig.inter_exit,
                adPlacement = AdPlacement.INTER_EXIT,
                isEnable = FirebaseConfigManager.instance().adConfig.enableInterExit,
                action = action
            )
        } else {
            action.invoke()
        }
    }

    fun showInterBack(activity: Activity, lifecycleOwner: LifecycleOwner, action: () -> Unit) {
        if (System.currentTimeMillis() - App.currentTime >= (FirebaseConfigManager.instance().adConfig.interval * 1000)) {
            loadAndShowInterAds(
                activity = activity,
                lifecycleOwner = lifecycleOwner,
                idAds = BuildConfig.inter_back,
                adPlacement = AdPlacement.INTER_BACK,
                isEnable = FirebaseConfigManager.instance().adConfig.enableInterBack,
                action = {
                    App.currentTime = System.currentTimeMillis()
                    action.invoke()
                }
            )
        } else {
            action.invoke()
        }
    }

    fun showInterCancelPaywall(activity: Activity, lifecycleOwner: LifecycleOwner, action: () -> Unit) {
        loadAndShowInterAds(
            activity = activity,
            lifecycleOwner = lifecycleOwner,
            idAds = BuildConfig.inter_cancel_paywall,
            adPlacement = AdPlacement.INTER_CANCEL_PAYWALL,
            isEnable = FirebaseConfigManager.instance().adConfig.enableInterCancelPaywall,
            action = {
                action.invoke()
            }
        )
    }

    fun cancelAllAds(isPurchased: Boolean) {
        InterstitialAdsHelper.getInstance(AdPlacement.INTER_FUNCTION).cancelRequestAndShowAllAds(isPurchased)
        InterstitialAdsHelper.getInstance(AdPlacement.INTER_SPLASH).cancelRequestAndShowAllAds(isPurchased)
        InterstitialAdsHelper.getInstance(AdPlacement.INTER_BACK).cancelRequestAndShowAllAds(isPurchased)
        InterstitialAdsHelper.getInstance(AdPlacement.INTER_CANCEL_PAYWALL).cancelRequestAndShowAllAds(isPurchased)
    }
}
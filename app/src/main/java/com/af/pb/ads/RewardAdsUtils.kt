package com.af.pb.ads

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import com.ads.admob.cmp.ConsentManager
import com.ads.admob.data.ContentAd
import com.ads.admob.helper.reward.RewardAdConfig
import com.ads.admob.helper.reward.RewardAdHelper
import com.ads.admob.listener.RewardAdRequestCallBack
import com.ads.admob.listener.RewardAdShowCallBack
import com.af.pb.BuildConfig
import com.af.pb.utils.FirebaseConfigManager
import com.af.pb.utils.Logger
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem

object RewardAdsUtils {

    fun loadRewardAds(
        activity: Activity,
        idAds: String,
        adPlacement: String,
        isEnable: Boolean,
        isLoadAndShow: Boolean = false,
        onAdLoaded: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        if (ConsentManager.getInstance(activity).getConsentResult(activity)) {
            RewardAdHelper.getInstance(adPlacement).setRewardAdConfig(
                RewardAdConfig(
                    idAds = idAds,
                    canShowAds = isEnable,
                    canReloadAds = false,
                    adPlacement = adPlacement,
                    reloadIfFirstFail = true
                )
            )
            loadAds(activity, adPlacement, isLoadAndShow, onAdLoaded, onAdLoadFail)
        }
    }


    fun showRewardAds(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        adPlacement: String,
        isReloadWhenClose: Boolean = false,
        onUserEarnedReward: (() -> Unit)? = null,
        adClose: (() -> Unit)? = null,
        adFail: (() -> Unit)? = null,
    ) {
        RewardAdHelper.getInstance(adPlacement).forceShowRewardAd(activity, lifecycleOwner, object : RewardAdShowCallBack {

            override fun onAdClose() {
                checkReload(activity, adPlacement, isReloadWhenClose)
                adClose?.invoke()
            }

            override fun onAdFailedToShow(adError: AdError) {
                checkReload(activity, adPlacement, isReloadWhenClose)
                adFail?.invoke()
            }

            override fun onUserEarnedReward(rewardItem: RewardItem?) {
                onUserEarnedReward?.invoke()
            }

        })
    }

    fun loadAndShowRewardAds(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        idAds: String,
        adPlacement: String,
        isEnable: Boolean,
        onUserEarnedReward: (() -> Unit)? = null,
        adClose: (() -> Unit)? = null,
        adFail: (() -> Unit)? = null,
    ) {
        loadRewardAds(activity, idAds, adPlacement, isEnable, true, onAdLoaded = {
            showRewardAds(activity, lifecycleOwner, adPlacement, adClose = adClose, adFail = adFail, onUserEarnedReward = onUserEarnedReward)
        }, onAdLoadFail = {
            adFail?.invoke()
        })
    }

    private fun loadAds(
        activity: Activity,
        adPlacement: String,
        isLoadAndShow: Boolean = false,
        onAdLoaded: (() -> Unit)? = null,
        onAdLoadFail: (() -> Unit)? = null
    ) {
        RewardAdHelper.getInstance(adPlacement).requestRewardAds(activity, isLoadAndShow, object : RewardAdRequestCallBack {
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

    private fun checkReload(activity: Activity, adPlacement: String, isReloadWhenClose: Boolean) {
        if (isReloadWhenClose) {
            loadAds(activity, adPlacement)
        }
    }

    fun showRewardFilm(
        activity: Activity, lifecycleOwner: LifecycleOwner, onUserEarnedReward: (() -> Unit)? = null,
        adClose: (() -> Unit)? = null,
        adFail: (() -> Unit)? = null
    ) {
        loadAndShowRewardAds(
            activity = activity,
            lifecycleOwner = lifecycleOwner,
            idAds = BuildConfig.reward_film,
            adPlacement = AdPlacement.REWARD_FILM,
            isEnable = FirebaseConfigManager.instance().adConfig.enableRewardFilm,
            onUserEarnedReward = { onUserEarnedReward?.invoke() },
            adClose = { adClose?.invoke() },
            adFail = { adFail?.invoke() })
    }

    fun showRewardTopFilm(
        activity: Activity, lifecycleOwner: LifecycleOwner, onUserEarnedReward: (() -> Unit)? = null,
        adClose: (() -> Unit)? = null,
        adFail: (() -> Unit)? = null
    ) {
        loadAndShowRewardAds(
            activity = activity,
            lifecycleOwner = lifecycleOwner,
            idAds = BuildConfig.reward_top_film,
            adPlacement = AdPlacement.REWARD_TOP_FILM,
            isEnable = FirebaseConfigManager.instance().adConfig.enableRewardTopFilm,
            onUserEarnedReward = { onUserEarnedReward?.invoke() },
            adClose = { adClose?.invoke() },
            adFail = { adFail?.invoke() })
    }

    fun showRewardQuality(
        activity: Activity, lifecycleOwner: LifecycleOwner, onUserEarnedReward: (() -> Unit)? = null,
        adClose: (() -> Unit)? = null,
        adFail: (() -> Unit)? = null
    ) {
        loadAndShowRewardAds(
            activity = activity,
            lifecycleOwner = lifecycleOwner,
            idAds = BuildConfig.reward_quality,
            adPlacement = AdPlacement.REWARD_QUALITY,
            isEnable = FirebaseConfigManager.instance().adConfig.enableRewardQuality,
            onUserEarnedReward = { onUserEarnedReward?.invoke() },
            adClose = { adClose?.invoke() },
            adFail = { adFail?.invoke() })
    }

    fun cancelAllAds(isPurchased: Boolean) {
        RewardAdHelper.getInstance(AdPlacement.REWARD_FILM).cancelRequestAndShowAllAds(isPurchased)
    }
}
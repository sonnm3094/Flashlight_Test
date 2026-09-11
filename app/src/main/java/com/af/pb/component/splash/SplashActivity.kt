package com.af.pb.component.splash

import android.content.Intent
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.ads.admob.data.ContentAd
import com.ads.admob.event.FirebaseTrackingManager
import com.ads.admob.helper.adnative.NativeAdHelper.Companion.bindViews
import com.ads.admob.helper.appoppen.AppOpenAdConfig
import com.ads.admob.helper.appoppen.AppOpenAdsHelper
import com.ads.admob.helper.banner.BannerAdConfig
import com.ads.admob.helper.banner.BannerAdHelper
import com.ads.admob.helper.banner.params.BannerAdParam
import com.ads.admob.helper.interstitial.InterstitialAdSplashConfig
import com.ads.admob.helper.interstitial.InterstitialAdSplashHelper
import com.ads.admob.helper.interstitial.params.InterstitialAdParam
import com.ads.admob.listener.AppOpenAdRequestCallBack
import com.ads.admob.listener.AppOpenAdShowCallBack
import com.ads.admob.listener.BannerAdCallBack
import com.ads.admob.listener.InterstitialAdCallback
import com.af.pb.App
import com.af.pb.BuildConfig
import com.af.pb.ads.AdPlacement
import com.af.pb.ads.NativeAdsUtils
import com.af.pb.ads.NativeFullAdCloseHelper
import com.af.pb.base.activity.BaseActivity
import com.af.pb.component.language.activity.LanguageActivity
import com.af.pb.component.main.activity.MainActivity
import com.af.pb.databinding.ActivitySplashBinding
import com.af.pb.dialog.ForceUpdateDialog
import com.af.pb.dialog.NoInternetDialog
import com.af.pb.utils.FirebaseConfigManager
import com.af.pb.utils.SpManager
import com.af.pb.utils.Utils
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    @Inject
    lateinit var spManager: SpManager

    override val shouldShowNoInternetDialog: Boolean = false
    private var bannerTimeoutJob: Job? = null
    private var isLoginDone = true
    private var isBannerConditionMet = false

    private var forceUpdateDialog: ForceUpdateDialog? = null
    private var typeFrom: String = ""
    private var isSplashAdHandled = false

    private val closeButtonHelper by lazy { NativeFullAdCloseHelper(viewBinding.btnClose, viewBinding.tvCountDown) }

    companion object {
        private const val REQUEST_CODE_UPDATE = 1001
        private const val FROM_FOR_YOU = "for_you"
        private const val FROM_YOUR_LIST = "your_list"
        private const val FROM_UNINSTALL = "uninstall"
    }

    override fun provideViewBinding(): ActivitySplashBinding =
        ActivitySplashBinding.inflate(layoutInflater)


    override fun initViews() {
        setFullscreen()


        checkConnection()
        FirebaseTrackingManager.getInstance().logEvent("splash_open")
        viewBinding.tvAds.isVisible = !spManager.isPurchased()
        viewBinding.frAdsBanner.isVisible = !spManager.isPurchased()
        viewBinding.btnClose.setOnClickListener {
            closeButtonHelper.cancelAutoClose()
            goToMainScreen()
        }
    }

    private fun checkConnection() {
        if (Utils.isConnected(this)) {
            initAds()
        } else {
            NoInternetDialog(this).apply {
                show()
                onRetry = {
                    checkConnection()
                }
                onCancel = {
                    finish()
                }
            }
        }
    }


    private fun tryReleaseInterAdHold() {
        isBannerConditionMet = true
        if (isLoginDone) {
            releaseSplashAdHold()
        }
    }

    private fun releaseSplashAdHold() {
        if (FirebaseConfigManager.instance().adConfig.useInterSplash) {
            interAdSplashHelper.releaseHoldShow()
        } else {
            showAppOpenSplash()
        }
    }

    private fun initAds() {
        App.instance?.initConsentManager(this) {
            runOnUiThread {
                if (spManager.isPurchased()) {
                    lifecycleScope.launch {
                        delay(2000.milliseconds)
                        goToMainScreen()
                    }
                } else {
                    FirebaseConfigManager.instance().doAfterFetch {
                        if (shouldForceUpdate()) {
                            startForceUpdate()
                        } else {
                            loadSplashInter()
                            loadLanguageNativeAd()
                            initBanner()
                        }
                    }
                }
            }
        }
    }

    private fun shouldForceUpdate(): Boolean {
        val config = FirebaseConfigManager.instance()
        return config.isForceUpdate && config.versionForce != BuildConfig.VERSION_NAME
    }

    private fun startForceUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                val canUpdate = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                if (canUpdate) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        this,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                        REQUEST_CODE_UPDATE
                    )
                } else {
                    showForceUpdateFallback()
                }
            }
            .addOnFailureListener {
                showForceUpdateFallback()
            }
    }

    private fun showForceUpdateFallback() {
        if (isFinishing || isDestroyed) return
        forceUpdateDialog = ForceUpdateDialog(this).apply {
            onExit = { finish() }
            show()
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE && resultCode != RESULT_OK) {
            showForceUpdateFallback()
        }
    }

    override fun onBack() {
        if (forceUpdateDialog?.isShowing == true) return
        super.onBack()
    }

    private fun loadSplashInter() {
        if (FirebaseConfigManager.instance().adConfig.useInterSplash) {
            interAdSplashHelper.holdShow = true
            interAdSplashHelper.requestAds(InterstitialAdParam.Request)
        } else {
            aoaSplashHelper.requestAppOpenAds(this, isLoadAndShow = false, aoaSplashRequestCallback)
        }
    }

    private val aoaSplashHelper by lazy { initAoaSplash() }
    private fun initAoaSplash(): AppOpenAdsHelper {
        val helper = AppOpenAdsHelper.getInstance(AdPlacement.AOA_SPLASH)
        helper.setAppOpenAdConfig(
            AppOpenAdConfig(
                idAds = BuildConfig.aoa_splash,
                canShowAds = FirebaseConfigManager.instance().adConfig.enableAppOpenSplash,
                canReloadAds = false,
                adPlacement = AdPlacement.AOA_SPLASH,
                reloadIfFirstFail = true
            )
        )
        return helper
    }

    private val aoaSplashRequestCallback = object : AppOpenAdRequestCallBack {
        override fun onAdLoaded(data: ContentAd) {}
        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            goToMainScreen()
        }
    }

    private fun showAppOpenSplash() {
        if (isSplashAdHandled) return
        isSplashAdHandled = true
        aoaSplashHelper.forceShowAppOpen(this, this, aoaSplashShowCallback)
    }

    private val aoaSplashShowCallback = object : AppOpenAdShowCallBack {
        override fun onNextAction() {
            goToMainScreen()
        }

        override fun onAdClose() {
            goToMainScreen()
        }

        override fun onAppOpenShow() {}
        override fun onAdClicked() {}
        override fun onAdImpression() {}
        override fun onAdFailedToShow(adError: AdError) {}
    }

    private val interAdSplashHelper by lazy { initInterAdSplash() }
    private fun initInterAdSplash(): InterstitialAdSplashHelper {
        val idAdsPriority = if (FirebaseConfigManager.instance().adConfig.enableInterSplash2f) BuildConfig.inter_splash_2f else null
        val config = InterstitialAdSplashConfig(
            idAds = BuildConfig.inter_splash,
            idAdsPriority = idAdsPriority,
            canShowAds = FirebaseConfigManager.instance().adConfig.enableInterSplash,
            canReloadAds = false,
            reloadIfFirstFail = true,
            timeDelay = 2000L,
            timeOut = 25000L,
            showReady = true,
            adPlacement = AdPlacement.INTER_SPLASH
        )
        return InterstitialAdSplashHelper(
            activity = this,
            lifecycleOwner = this,
            config = config
        ).apply {
            registerAdListener(interAdCallBack)
        }
    }

    private val interAdCallBack = object : InterstitialAdCallback {
        override fun onNextAction() {
            initNativeFullSplash()
        }

        override fun onAdClose() {
            initNativeFullSplash()
        }

        override fun onInterstitialShow() {
            NativeAdsUtils.preLoadNativeFullSplash(this@SplashActivity)
        }

        override fun onAdLoaded(data: ContentAd) {
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {}
        override fun onAdClicked() {}
        override fun onAdImpression() {}
        override fun onAdFailedToShow(adError: AdError) {}
    }

    private fun initBanner() {
        val isEnable = FirebaseConfigManager.instance().adConfig.enableBannerSplash
        if (!isEnable) {
            tryReleaseInterAdHold()
            return
        }

        val config = BannerAdConfig(
            idAds = BuildConfig.banner_splash,
            idAdsPriority = BuildConfig.banner_splash_2f,
            canShowAds = true,
            canReloadAds = false,
            adPlacement = "banner_splash",
            reloadIfFirstFail = true
        )
        val bannerAdSplash = BannerAdHelper(activity = this, lifecycleOwner = this, config = config).apply {
            setBannerContentView(viewBinding.frAdsBanner)

            registerAdListener(object : BannerAdCallBack {
                override fun onAdLoaded(data: ContentAd) {
                    bannerTimeoutJob?.cancel()
                    bannerTimeoutJob = lifecycleScope.launch {
                        delay(2000.milliseconds)
                        tryReleaseInterAdHold()
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    bannerTimeoutJob?.cancel()
                    tryReleaseInterAdHold()
                }

                override fun onAdClicked() {}
                override fun onAdImpression() {}
                override fun onAdFailedToShow(adError: AdError) {}
            })
        }
        bannerAdSplash.requestAds(BannerAdParam.Request)
        bannerTimeoutJob = lifecycleScope.launch {
            delay(8000.milliseconds)
            tryReleaseInterAdHold()
        }
    }

    private fun initNativeFullSplash() {
        if (NativeAdsUtils.nativeFullSplash != null) {
            showCloseButton()
            lifecycleScope.launch(Dispatchers.Main) {
                NativeAdsUtils.nativeFullSplash.bindViews(
                    this@SplashActivity,
                    this@SplashActivity,
                    viewBinding.frAdsNative,
                    viewBinding.shimmerContainerNative.shimmerContainerNative
                )
            }
        } else {
            goToMainScreen()
        }
    }

    private fun showCloseButton() {
        closeButtonHelper.show()
    }

    private fun loadLanguageNativeAd() {
        if (!spManager.isLanguageChosen()) {
            NativeAdsUtils.preLoadNativeLanguage(this)
            NativeAdsUtils.preLoadNativeLanguageSelect(this)
        }
    }

    private fun goToMainScreen() {
        if (spManager.isLanguageChosen()) {
            MainActivity.start(this)
        } else {
            LanguageActivity.start(this, true)
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        closeButtonHelper.cancel()
    }

}
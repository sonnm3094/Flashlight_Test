package com.af.pb

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.lifecycle.ProcessLifecycleOwner
import com.ads.admob.admob.AdmobFactory
import com.ads.admob.billing.factory.IapFactory
import com.ads.admob.billing.factory.IapType
import com.ads.admob.cmp.ConsentManager
import com.ads.admob.cmp.interfaces.OnConsentResponse
import com.ads.admob.config.AfAdConfig
import com.ads.admob.config.AfAdjustConfig
import com.ads.admob.config.NetworkProvider
import com.ads.admob.data.ContentAd
import com.ads.admob.data.IapItem
import com.ads.admob.helper.appoppen.AppResumeAdConfig
import com.ads.admob.helper.appoppen.AppResumeAdHelper
import com.ads.admob.listener.AdmobCallBack
import com.ads.admob.listener.AppOpenAdCallBack
import com.ads.admob.listener.BillingClientConnectionListener
import com.ads.admob.listener.PurchaseServiceListener
import com.ads.admob.widget.DataWrappers
import com.af.pb.ads.AdPlacement
import com.af.pb.ads.BannerAdsUtils
import com.af.pb.ads.InterAdsUtils
import com.af.pb.ads.NativeAdsUtils
import com.af.pb.ads.RewardAdsUtils
import com.af.pb.component.language.activity.LanguageActivity
import com.af.pb.component.main.activity.MainActivity
import com.af.pb.component.onboarding.activity.OnBoardingActivity
import com.af.pb.component.splash.SplashActivity
import com.af.pb.utils.Constant
import com.af.pb.utils.FirebaseConfigManager
import com.af.pb.utils.LocaleHelper
import com.af.pb.utils.Logger
import com.af.pb.utils.SpManager
import com.af.pb.utils.getDeviceLanguage
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidApp
@Singleton
class App : Application(), Application.ActivityLifecycleCallbacks {
    @Inject
    lateinit var spManager: SpManager

    private var isColdStart = true
    private var firstActivityCreated = false

    companion object {
        var context: Context? = null
        private var mInstance: App? = null
        val instance get() = mInstance

        var appResumeAdHelper: AppResumeAdHelper? = null
            private set

        var currentTime = 0L
        var currentInterShowFrequency = 0
    }

    private fun initAppOpenAd(): AppResumeAdHelper {
        val listClassInValid = mutableListOf<Class<*>>()
        listClassInValid.add(AdActivity::class.java)
        listClassInValid.add(SplashActivity::class.java)
        listClassInValid.add(LanguageActivity::class.java)
        listClassInValid.add(OnBoardingActivity::class.java)
        val config = AppResumeAdConfig(
            idAds = BuildConfig.app_open_resume,
            listClassInValid = listClassInValid,
            canShowAds = FirebaseConfigManager.instance().adConfig.enableAppOpenResume,
            reloadIfFirstFail = true,
            networkProvider = NetworkProvider.ADMOB,
            adPlacement = AdPlacement.OPEN_RESUME,
            loadOnResume = true
        )
        return AppResumeAdHelper(
            application = this,
            lifecycleOwner = ProcessLifecycleOwner.get(),
            config = config
        )
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        context = applicationContext

        FirebaseApp.initializeApp(this)
        FirebaseConfigManager.instance().fetch()

        initAdmob()
        initBilling()

        registerActivityLifecycleCallbacks(this)
    }

    private fun handleColdStart(activity: Activity) {
        if (firstActivityCreated) return
        firstActivityCreated = true

        if (activity !is SplashActivity) {
            initAdmob()
        }
    }

    fun initConsentManager(activity: Activity, action: () -> Unit) {
        val consentManager = ConsentManager.getInstance(activity)
        consentManager.initReleaseConsent(onConsentResponse = object : OnConsentResponse {
            override fun onResponse(errorMessage: String?) {
                Logger.e("onResponse: $errorMessage -- ${consentManager.canRequestAds} ")
                initOpenResume()
                action.invoke()
            }

            override fun onPolicyRequired(isRequired: Boolean) {
                Logger.e("onPolicyRequired: $isRequired")
                spManager.putBoolean(Constant.KEY_SP_IS_SHOW_UMP_SETTING, isRequired)
            }
        })
    }

    private fun initAdmob() {
        Logger.e("initAdmob")
        val afAdjustConfig = AfAdjustConfig.Build(
            adjustToken = "xzrz6j945uyo",
            adRevenueKey = "jm9cwh",
            environmentProduct = true
        ).build()
        val afAdConfig = AfAdConfig.Builder(afAdjustConfig = afAdjustConfig)
            .intervalBetweenInterstitial(1000)
            .buildVariantProduce(false)
            .mediationProvider(NetworkProvider.ADMOB)
//            .eventConfig(EventConfig(25_900, "VND")) // config exchangeRate  ?current = 1 usd
            .listTestDevices(arrayListOf("E779E105A3CF0D57287E842172791525"))
            .build()
        AdmobFactory.INSTANCE.initAdmob(this, afAdConfig, adCallback = object : AdmobCallBack {
            override fun initialized() {
                Logger.e("Admob initialized")
            }
        })
    }

    private fun initBilling() {
        val listPurchaseItem: MutableList<IapItem> = ArrayList()
        listPurchaseItem.add(IapItem(Constant.WEEKLY_IAP, IapType.SUBSCRIPTION))
        listPurchaseItem.add(IapItem(Constant.MONTHLY_IAP, IapType.SUBSCRIPTION))
        listPurchaseItem.add(IapItem(Constant.YEARLY_IAP, IapType.SUBSCRIPTION))
        IapFactory.initialize(this, listPurchaseItem, false)
        IapFactory.getInstance()
            .registerBillingClientConnectionListener(object : BillingClientConnectionListener {
                override fun onConnected(status: Boolean, billingResponseCode: Int) {
                    Logger.e("onConnected: $status $billingResponseCode")
                }
            })

        IapFactory.getInstance().registerPurchaseServiceListener(object : PurchaseServiceListener {
            override fun onPricesUpdated(iapKeyPrices: Map<String, List<DataWrappers.ProductDetails>>) {
                Logger.e("onPricesUpdated: ${IapFactory.getInstance().isProductPurchased()}")
            }

            override fun onProductPurchased(purchaseInfo: DataWrappers.PurchaseInfo?) {
                Logger.e("onProductPurchased: ${IapFactory.getInstance().isProductPurchased()}")
                initPurchased(IapFactory.getInstance().isProductPurchased())
            }

            override fun onProductRestored(purchaseInfo: DataWrappers.PurchaseInfo?) {
                Logger.e("onProductRestored: ")
                initPurchased(IapFactory.getInstance().isProductPurchased())
            }

            override fun onPurchaseFailed(
                purchaseInfo: DataWrappers.PurchaseInfo?,
                billingResponseCode: Int?
            ) {
                Logger.e("onPurchaseFailed: $billingResponseCode")
            }
        })
    }

    fun initPurchased(isPurchased: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            if (isPurchased) {
                AdmobFactory.INSTANCE.cancelRequestAndShowAllAds()
            }
            NativeAdsUtils.cancelAllAds(isPurchased)
            InterAdsUtils.cancelAllAds(isPurchased)
            RewardAdsUtils.cancelAllAds(isPurchased)
            BannerAdsUtils.cancelAllAds(isPurchased)
            appResumeAdHelper?.cancelRequestAndShowAllAds(isPurchased)
            spManager.setPurchased(isPurchased)
            Logger.e("initPurchased: $isPurchased")
        }
    }

    private fun initOpenResume() {
        appResumeAdHelper = initAppOpenAd()
        if (spManager.isPurchased()) {
            appResumeAdHelper?.cancelRequestAndShowAllAds(true)
        }
        appResumeAdHelper?.registerAdListener(object : AppOpenAdCallBack {
            override fun onAppOpenAdShow() {
                Logger.e("onAppOpenAdShow: ")
            }

            override fun onAppOpenAdClose() {
                Logger.e("onAppOpenAdClose: ")
            }

            override fun onAdLoaded(data: ContentAd) {
                Logger.e("onAdLoaded: ")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Logger.e("onAdFailedToLoad: ")
            }

            override fun onAdClicked() {
                Logger.e("onAdClicked: ")
            }

            override fun onAdImpression() {
                Logger.e("onAdImpression: ")
            }

            override fun onAdFailedToShow(adError: AdError) {
                Logger.e("onAdFailedToShow: ")
            }
        })
    }

    fun destroyAppOpenAd() {
        appResumeAdHelper?.unregisterAllAdListener()
        appResumeAdHelper?.destroyAds()
        appResumeAdHelper = null
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        val locale = getDeviceLanguage()
        val language = spManager.getLanguage()
        LocaleHelper.onAttach(this, language.languageCode)
        super.onConfigurationChanged(newConfig)
    }

    override fun attachBaseContext(base: Context?) {
        val context = LocaleHelper.onAttach(base, Locale.getDefault().language)
        super.attachBaseContext(context)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (isColdStart) {
            isColdStart = false
            handleColdStart(activity)
        }
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (activity is MainActivity) {
            destroyAppOpenAd()
        }
    }
}
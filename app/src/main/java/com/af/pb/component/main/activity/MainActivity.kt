package com.af.pb.component.main.activity

import android.app.Activity
import android.content.Intent
import com.ads.admob.billing.factory.IapFactory
import com.ads.admob.event.FirebaseTrackingManager
import com.af.pb.ads.InterAdsUtils
import com.af.pb.ads.RewardAdsUtils
import com.af.pb.base.activity.BaseActivity
import com.af.pb.component.main.setting.SettingActivity
import com.af.pb.component.purchase.activity.Purchase1Activity
import com.af.pb.databinding.ActivityMainBinding
import com.af.pb.dialog.ExitAppDialog
import com.af.pb.utils.SpManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var spManager: SpManager

    override fun provideViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setFullscreen()
        if (!spManager.isLanguageChosen()) FirebaseTrackingManager.getInstance().logEvent("home_view_first")
        spManager.setLanguageChosen()
        spManager.setPurchased(IapFactory.getInstance().isProductPurchased())
        FirebaseTrackingManager.getInstance().logEvent("home_view")

        btnPremium.setOnClickListener {
            Purchase1Activity.start(this@MainActivity)
        }

        btnShowBanner.setOnClickListener {
            ShowBannerActivity.start(this@MainActivity)
        }

        btnShowNative.setOnClickListener {
            ShowNativeActivity.start(this@MainActivity)
        }

        btnLoadAndShowInter.setOnClickListener {
            InterAdsUtils.showInterFunction(this@MainActivity, this@MainActivity) {
                showToast("Close ads inter")
            }
        }

        btnLoadAndShowReward.setOnClickListener {
            RewardAdsUtils.showRewardFilm(this@MainActivity, this@MainActivity) {
                showToast("Close ads reward")
            }
        }

        btnShowNativeAdapter.setOnClickListener {
            ShowNativeAdapterActivity.start(this@MainActivity)
        }

        btnSetting.setOnClickListener {
            SettingActivity.start(this@MainActivity)
        }

    }


    override fun onBack() {
        ExitAppDialog(this).apply {
            show()
            onExit = {
                InterAdsUtils.showInterExit(this@MainActivity, this@MainActivity) {
                    super.onBack()
                }
            }
        }
    }

    companion object {
        fun startNewTask(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity.startActivity(intent)
        }

        fun start(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
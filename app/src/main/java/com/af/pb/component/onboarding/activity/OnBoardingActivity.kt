package com.af.pb.component.onboarding.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.ads.admob.event.FirebaseTrackingManager
import com.ads.admob.helper.adnative.NativeAdHelper.Companion.bindViews
import com.ads.admob.helper.adnative.params.AdNativeState
import com.af.pb.R
import com.af.pb.ads.NativeAdsUtils
import com.af.pb.ads.NativeFullAdCloseHelper
import com.af.pb.base.activity.BaseActivity
import com.af.pb.component.main.activity.MainActivity
import com.af.pb.component.onboarding.adpater.OnBoardingAdapter
import com.af.pb.component.onboarding.viewmodel.OnBoardingViewModel
import com.af.pb.databinding.ActivityOnBoardingBinding
import com.af.pb.domain.layer.FullscreenAdConfig
import com.af.pb.utils.Logger
import com.af.pb.utils.SpManager
import com.af.pb.utils.gone
import com.af.pb.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding>() {

    @Inject
    lateinit var spManager: SpManager

    private val onBoardingAdapter = OnBoardingAdapter()
    private val viewModels: OnBoardingViewModel by viewModels()
    private val closeButtonHelper by lazy { NativeFullAdCloseHelper(viewBinding.btnClose, viewBinding.tvCountDown) }

    override fun provideViewBinding(): ActivityOnBoardingBinding = ActivityOnBoardingBinding.inflate(layoutInflater)

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setFullscreen()
        preloadNativeObd3()

        vpOnBoarding.adapter = onBoardingAdapter
        dotsIndicator.attachTo(vpOnBoarding)

        vpOnBoarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                initScreenView(position)
                initMultiNativeAd(position)
                viewModels.currentPosition = position
                if (position != onBoardingAdapter.dataSet.size - 1) {
                    btnNext.text = getString(R.string.next)
                } else {
                    btnNext.text = getString(R.string.get_started)
                }
            }
        })

        btnNext.setOnClickListener {
            initEvent(viewModels.currentPosition)
            nextAction()
        }

        btnClose.setOnClickListener {
            closeButtonHelper.cancelAutoClose()
            nextAction()
        }
    }

    private fun nextAction() {
        if (viewModels.currentPosition < onBoardingAdapter.dataSet.size - 1) {
            viewBinding.vpOnBoarding.setCurrentItem(viewModels.currentPosition + 1, true)
        } else {
            MainActivity.start(this@OnBoardingActivity)
            finish()
        }
    }

    private fun initEvent(position: Int) {
        when (onBoardingAdapter.dataSet.getOrNull(position)?.imageId) {
            R.mipmap.bg_onboarding_1 -> FirebaseTrackingManager.getInstance().logEvent("obd1_next_click")
            R.mipmap.bg_onboarding_2 -> FirebaseTrackingManager.getInstance().logEvent("obd2_next_click")
            R.mipmap.bg_onboarding_3 -> FirebaseTrackingManager.getInstance().logEvent("obd3_next_click")
            R.mipmap.bg_onboarding_4 -> FirebaseTrackingManager.getInstance().logEvent("obd4_get_started_click")
        }
    }

    private fun initScreenView(position: Int) {
        when (onBoardingAdapter.dataSet.getOrNull(position)?.imageId) {
            R.mipmap.bg_onboarding_1 -> FirebaseTrackingManager.getInstance().logEvent("obd1_view")
            R.mipmap.bg_onboarding_2 -> FirebaseTrackingManager.getInstance().logEvent("obd2_view")
            R.mipmap.bg_onboarding_3 -> FirebaseTrackingManager.getInstance().logEvent("obd3_view")
            R.mipmap.bg_onboarding_4 -> FirebaseTrackingManager.getInstance().logEvent("obd4_view")
        }
    }

    private fun initNativeOb1() {
        lifecycleScope.launch(Dispatchers.Main) {
            NativeAdsUtils.nativeObd1.bindViews(
                this@OnBoardingActivity, this@OnBoardingActivity,
                viewBinding.frAdsNative,
                viewBinding.shimmerContainerNative.shimmerContainerNative
            )
        }
    }

    private fun initNativeOb2() {
        lifecycleScope.launch(Dispatchers.Main) {
            NativeAdsUtils.nativeObd2?.bindViews(
                this@OnBoardingActivity, this@OnBoardingActivity,
                viewBinding.frAdsNative,
                viewBinding.shimmerContainerNative.shimmerContainerNative
            )
        }
    }

    private fun initNativeOb3() {
        lifecycleScope.launch(Dispatchers.Main) {
            NativeAdsUtils.nativeObd3?.bindViews(
                this@OnBoardingActivity, this@OnBoardingActivity,
                viewBinding.frAdsNative,
                viewBinding.shimmerContainerNative.shimmerContainerNative
            )
        }
    }

    private fun initNativeOb4() {
        lifecycleScope.launch(Dispatchers.Main) {
            NativeAdsUtils.nativeObd4?.bindViews(
                this@OnBoardingActivity, this@OnBoardingActivity,
                viewBinding.frAdsNative,
                viewBinding.shimmerContainerNative.shimmerContainerNative
            )
        }
    }

    private fun preloadNativeObd3() {
        NativeAdsUtils.preLoadNativeOb23Full(this)
        NativeAdsUtils.preLoadNativeObd3(this)
        NativeAdsUtils.preLoadNativeOb34Full(this)
        NativeAdsUtils.preLoadNativeObd4(this)
    }

    private fun initNativeObFull() {
        if (!spManager.isPurchased()) {
            val configs = listOf(
                FullscreenAdConfig(
                    tag = "fs1",
                    afterNonFullscreenCount = 1,
                    adHelper = NativeAdsUtils.nativeOb12Full
                ),
                FullscreenAdConfig(
                    tag = "fs2",
                    afterNonFullscreenCount = 2,
                    adHelper = NativeAdsUtils.nativeOb23Full
                ),
                FullscreenAdConfig(
                    tag = "fs3",
                    afterNonFullscreenCount = 3,
                    adHelper = NativeAdsUtils.nativeOb34Full
                )
            )

            configs.forEach { config ->
                config.adHelper?.let { adHelper ->
                    adHelper.getAdNativeState()
                        .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                        .onEach { state ->
                            when {
                                state == AdNativeState.Fail -> {
                                    Logger.e("AdNativeState Fail")
                                }

                                state == adHelper.nativeAd?.let { AdNativeState.Loaded(it) } -> {
                                    adHelper.updateActivity(this@OnBoardingActivity, this@OnBoardingActivity)
                                    onBoardingAdapter.setNativeAdHelper(config.tag, adHelper)
                                    val insertPos = onBoardingAdapter.findInsertPosition(config.afterNonFullscreenCount)
                                    if (viewModels.currentPosition < insertPos && !onBoardingAdapter.hasFullscreenItem(config.tag)) {
                                        onBoardingAdapter.insertFullscreenAt(insertPos, config.tag)
                                    }
                                }
                            }
                        }
                        .launchIn(lifecycleScope)
                }
            }
        }
    }

    private fun initMultiNativeAd(position: Int) {
        if (!spManager.isPurchased()) {
            closeButtonHelper.cancel()
            viewBinding.apply {
                btnNext.visible()
                dotsIndicator.visible()
                btnClose.gone()
                tvCountDown.gone()
                frAdsNative.visible()
                when (onBoardingAdapter.dataSet.getOrNull(position)?.imageId) {
                    0 -> {
                        btnNext.gone()
                        dotsIndicator.gone()
                        frAdsNative.gone()
                        showCloseButton()
                    }

                    R.mipmap.bg_onboarding_1 -> {
                        initNativeOb1()
                    }

                    R.mipmap.bg_onboarding_2 -> {
                        initNativeOb2()
                    }

                    R.mipmap.bg_onboarding_3 -> {
                        initNativeOb3()
                    }

                    R.mipmap.bg_onboarding_4 -> {
                        initNativeOb4()
                    }
                }
            }
        }
    }

    private fun showCloseButton() {
        closeButtonHelper.show { onBoardingAdapter.dataSet[viewModels.currentPosition].imageId == 0 }
    }


    override fun initData() {
        super.initData()
        viewModels.getListOnBoarding()
    }

    override fun initObserver() {
        super.initObserver()
        viewModels.listOnBoarding.onEach {
            onBoardingAdapter.setData(ArrayList(it))
            initNativeObFull()
            initMultiNativeAd(viewModels.currentPosition)
        }.flowWithLifecycle(lifecycle, Lifecycle.State.CREATED).launchIn(lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        closeButtonHelper.cancel()
    }

    companion object {
        fun start(context: Context) {
            Intent(context, OnBoardingActivity::class.java).also {
                context.startActivity(it)
            }
        }
    }
}
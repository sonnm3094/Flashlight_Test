package com.af.pb.component.language.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ads.admob.event.FirebaseTrackingManager
import com.ads.admob.helper.adnative.NativeAdHelper.Companion.bindViews
import com.af.pb.R
import com.af.pb.ads.NativeAdsUtils
import com.af.pb.base.activity.BaseActivity
import com.af.pb.component.language.adapter.LanguageAdapter
import com.af.pb.component.language.viewmodel.LanguageViewModel
import com.af.pb.component.main.activity.MainActivity
import com.af.pb.component.onboarding.activity.OnBoardingActivity
import com.af.pb.data.model.Language
import com.af.pb.databinding.ActivityLanguageBinding
import com.af.pb.utils.Constant
import com.af.pb.utils.SpManager
import com.af.pb.utils.gone
import com.af.pb.utils.setAppLanguage
import com.af.pb.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {
    private var selectLanguageModel: Language? = null
    private val viewModel: LanguageViewModel by viewModels()
    private val languageAdapter = LanguageAdapter()

    @Inject
    lateinit var spManager: SpManager

    private var isLoadingShowed = false
    private var isFromSplash = false

    override fun provideViewBinding(): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        isFromSplash = intent.getBooleanExtra(Constant.KEY_INTENT_FROM_SPLASH, false)
        if (isFromSplash) {
            FirebaseTrackingManager.getInstance().logEvent("language_view")
        }
        toolBar.btnBack.isVisible = !isFromSplash
        toolBar.tvTitle.text = resources.getString(R.string.language)

        toolBar.btnAction.visible()
        toolBar.btnAction.isEnabled = false

        if (!spManager.isPurchased()) {
            if (isFromSplash) {
                initNativeLanguage()
                preloadNativeOb()
            } else {
                initNativeSetting()
            }
        }

        rcvLanguage.adapter = languageAdapter
        languageAdapter.onClick = {
            if (isFromSplash) {
                FirebaseTrackingManager.getInstance().logEvent("language_select_click")
            }
            toolBar.btnAction.gone()
            var timeDelay = 0L
            if (isFromSplash && !isLoadingShowed) {
                progressBar.visible()
                timeDelay = 2000
                isLoadingShowed = true
            } else {
                progressBar.gone()
            }
            lifecycleScope.launch {
                delay(timeDelay.milliseconds)
                progressBar.gone()
                if (isFromSplash) lottieView.visible()
                toolBar.btnAction.isEnabled = true
                toolBar.btnAction.visible()
                initNativeLanguageSelect()
            }
            languageAdapter.selectLanguage(it.languageCode)
            languageAdapter.selectedLanguage()?.let { languageModel ->
                selectLanguageModel = languageModel
            }
        }

        toolBar.btnBack.setOnClickListener {
            finish()
        }

        toolBar.btnAction.setOnClickListener {
            if (isFromSplash) {
                FirebaseTrackingManager.getInstance().logEvent("language_fo_save_click")
            }
            progressBar.visible()
            lottieView.gone()
            toolBar.btnAction.gone()
            lifecycleScope.launch {
                delay(1000.milliseconds)
                progressBar.gone()
                selectLanguageModel?.let {
                    spManager.saveLanguage(it)
                    setAppLanguage(it.languageCode)
                    toolBar.btnAction.isEnabled = false
                    if (isFromSplash) {
                        OnBoardingActivity.start(this@LanguageActivity)
                    } else {
                        MainActivity.startNewTask(this@LanguageActivity)
                    }
                }
            }
        }
    }

    private fun initNativeLanguage() {
        lifecycleScope.launch(Dispatchers.Main) {
            NativeAdsUtils.nativeLanguage.bindViews(
                this@LanguageActivity,
                this@LanguageActivity,
                viewBinding.frAdsNative,
                viewBinding.shimmerContainerNative.shimmerContainerNative
            )
        }
    }

    private fun initNativeLanguageSelect() {
        if (isFromSplash && !spManager.isPurchased()) {
            lifecycleScope.launch(Dispatchers.Main) {
                NativeAdsUtils.nativeLanguageSelect.bindViews(
                    this@LanguageActivity,
                    this@LanguageActivity,
                    viewBinding.frAdsNative,
                    viewBinding.shimmerContainerNative.shimmerContainerNative
                )
            }
        }
    }

    private fun initNativeSetting() {
        NativeAdsUtils.loadAndShowNativeSetting(
            this,
            this,
            viewBinding.frAdsNative,
            viewBinding.shimmerContainerNative.shimmerContainerNative
        )
    }

    private fun preloadNativeOb() {
        NativeAdsUtils.preLoadNativeObd1(this)
        NativeAdsUtils.preLoadNativeOb12Full(this)
        NativeAdsUtils.preLoadNativeObd2(this)
    }

    override fun initData() {
        viewModel.loadListLanguage()
    }

    override fun initObserver() {
        viewModel.listLanguage.onEach {
            languageAdapter.setData(ArrayList(it), isFromSplash)
            if (!isFromSplash) {
                languageAdapter.selectLanguage(spManager.getLanguage().languageCode)
            }
        }.flowWithLifecycle(lifecycle, Lifecycle.State.CREATED).launchIn(lifecycleScope)

    }

    companion object {
        fun start(context: Context, isFromSplash: Boolean) {
            Intent(context, LanguageActivity::class.java).putExtra(Constant.KEY_INTENT_FROM_SPLASH, isFromSplash).also {
                context.startActivity(it)
            }
        }
    }
}
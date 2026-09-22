package com.af.pb.component.language.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.af.pb.R
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {
    private var selectLanguageModel: Language? = null
    private val viewModel: LanguageViewModel by viewModels()
    private val languageAdapter = LanguageAdapter()

    @Inject
    lateinit var spManager: SpManager

    override val shouldShowNoInternetDialog: Boolean = false

    private var isLoadingShowed = false
    private var isFromSplash = false

    override fun provideViewBinding(): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        isFromSplash = intent.getBooleanExtra(Constant.KEY_INTENT_FROM_SPLASH, false)
        toolBar.btnBack.isVisible = !isFromSplash
        toolBar.tvTitle.text = resources.getString(R.string.language)

        toolBar.btnAction.visible()
        toolBar.btnAction.isEnabled = false

        rcvLanguage.adapter = languageAdapter
        languageAdapter.onClick = {
            var timeDelay = 0L
            if (isFromSplash && !isLoadingShowed) {
                progressBar.visible()
                timeDelay = 500L
                isLoadingShowed = true
            } else {
                progressBar.gone()
            }
            lifecycleScope.launch {
                delay(timeDelay.milliseconds)
                progressBar.gone()
                toolBar.btnAction.isEnabled = true
                toolBar.btnAction.visible()
            }
            languageAdapter.selectLanguage(it.languageCode)
            languageAdapter.selectedLanguage()?.let { languageModel ->
                selectLanguageModel = languageModel
                toolBar.btnAction.text = getLocalizedSavedString(languageModel.languageCode)
            }
        }

        toolBar.btnBack.setOnClickListener {
            finish()
        }

        toolBar.btnAction.setOnClickListener {
            progressBar.visible()
            toolBar.btnAction.gone()
            lifecycleScope.launch {
                delay(500.milliseconds)
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
                    finish()
                }
            }
        }
    }

    private fun getLocalizedSavedString(languageCode: String): String {
        return try {
            val locale = when {
                languageCode.startsWith("b+") -> Locale.forLanguageTag(languageCode.removePrefix("b+"))
                languageCode.contains("-r") -> {
                    val parts = languageCode.split("-r")
                    Locale(parts[0], parts[1])
                }
                languageCode.contains("-") -> {
                    val parts = languageCode.split("-")
                    Locale(parts[0], parts[1])
                }
                else -> Locale(languageCode)
            }
            val config = Configuration(resources.configuration)
            config.setLocale(locale)
            val localizedContext = createConfigurationContext(config)
            localizedContext.getString(R.string.done)
        } catch (_: Exception) {
            getString(R.string.done)
        }
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
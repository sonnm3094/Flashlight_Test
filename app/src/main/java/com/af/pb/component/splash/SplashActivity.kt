package com.af.pb.component.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.af.pb.BuildConfig
import com.af.pb.base.activity.BaseActivity
import com.af.pb.component.language.activity.LanguageActivity
import com.af.pb.databinding.ActivitySplashBinding
import com.af.pb.dialog.ForceUpdateDialog
import com.af.pb.utils.FirebaseConfigManager
import com.af.pb.utils.SpManager
import com.af.pb.utils.Utils
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    @Inject
    lateinit var spManager: SpManager

    override val shouldShowNoInternetDialog: Boolean = false

    private var forceUpdateDialog: ForceUpdateDialog? = null
    private var progressAnimator: ValueAnimator? = null

    companion object {
        private const val REQUEST_CODE_UPDATE = 1001
        private const val SPLASH_DURATION_MS = 2000L
    }

    override fun provideViewBinding(): ActivitySplashBinding =
        ActivitySplashBinding.inflate(layoutInflater)

    override fun initViews() {
        viewBinding.tvAds.isVisible = !spManager.isPurchased()
        startProgressAnimation()
    }

    private fun startProgressAnimation() {
        progressAnimator?.cancel()
        progressAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = SPLASH_DURATION_MS
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                if (isFinishing || isDestroyed) return@addUpdateListener
                val progress = animation.animatedValue as Int
                viewBinding.progressBar.progress = progress
                viewBinding.tvProgress.text = "$progress%"
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (isFinishing || isDestroyed) return
                    checkConnectionAndProceed()
                }
            })
            start()
        }
    }

    private fun checkConnectionAndProceed() {
        lifecycleScope.launch {
            if (Utils.isConnected(this@SplashActivity) && shouldForceUpdate()) {
                startForceUpdate()
            } else {
                goToMainScreen()
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

    override fun onDestroy() {
        progressAnimator?.cancel()
        progressAnimator = null
        super.onDestroy()
    }

    private fun goToMainScreen() {
        LanguageActivity.start(this, true)
        finish()
    }
}
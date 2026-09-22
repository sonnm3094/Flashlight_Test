package com.af.pb.component.splash

import android.content.Intent
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    @Inject
    lateinit var spManager: SpManager

    override val shouldShowNoInternetDialog: Boolean = false

    private var forceUpdateDialog: ForceUpdateDialog? = null


    companion object {
        private const val REQUEST_CODE_UPDATE = 1001
    }

    override fun provideViewBinding(): ActivitySplashBinding =
        ActivitySplashBinding.inflate(layoutInflater)


    override fun initViews() {
        checkConnection()
        viewBinding.tvAds.isVisible = !spManager.isPurchased()
    }

    private fun checkConnection() {
        lifecycleScope.launch {
            delay(2000.milliseconds)
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

    private fun goToMainScreen() {
        LanguageActivity.start(this, true)
        finish()
    }

}
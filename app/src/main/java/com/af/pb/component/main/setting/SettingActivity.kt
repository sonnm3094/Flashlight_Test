package com.af.pb.component.main.setting

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.view.isVisible
import com.af.pb.BuildConfig
import com.af.pb.R
import com.af.pb.base.activity.BaseActivity
import com.af.pb.component.language.activity.LanguageActivity
import com.af.pb.databinding.ActivitySettingBinding
import com.af.pb.dialog.RateDialog
import com.af.pb.utils.Constant
import com.af.pb.utils.SpManager
import com.af.pb.utils.openBrowser
import com.af.pb.utils.share
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : BaseActivity<ActivitySettingBinding>(), View.OnClickListener {
    @Inject
    lateinit var spManager: SpManager

    override fun provideViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        tvTitle.text = getString(R.string.settings)
        btnBack.setOnClickListener { onBack() }

        btnLanguage.apply {
            imgIcon.setImageResource(R.drawable.ic_language)
            tvTitle.setText(R.string.language)
            tvValue.visibility = View.VISIBLE
            root.setOnClickListener(this@SettingActivity)
        }

        btnShareApp.apply {
            imgIcon.setImageResource(R.drawable.ic_share)
            tvTitle.setText(R.string.share_app)
            root.setOnClickListener(this@SettingActivity)
        }

        btnRateUs.apply {
            imgIcon.setImageResource(R.drawable.ic_rate_app)
            tvTitle.setText(R.string.rate_us)
            root.setOnClickListener(this@SettingActivity)
        }

        btnPrivacyPolicy.apply {
            imgIcon.setImageResource(R.drawable.ic_privacy_policy)
            tvTitle.setText(R.string.privacy_policy)
            root.setOnClickListener(this@SettingActivity)
        }

        btnPolicySetting.apply {
            imgIcon.setImageResource(R.drawable.ic_policy_setting)
            tvTitle.setText(R.string.policy_setting)
            root.setOnClickListener(this@SettingActivity)
        }

        val isShowPolicySettings = spManager.getBoolean(
            Constant.KEY_SP_IS_SHOW_UMP_SETTING, false
        )
        btnPolicySetting.root.isVisible = isShowPolicySettings

        updateLanguageDisplay()
    }

    override fun onResume() {
        super.onResume()
        updateLanguageDisplay()
    }

    private fun updateLanguageDisplay() {
        try {
            val lang = spManager.getLanguage()
            viewBinding.btnLanguage.tvValue.text = getString(lang.nameRes)
        } catch (_: Exception) {}
    }

    override fun onClick(v: View?) {
        when (v) {
            viewBinding.btnLanguage.root -> {
                LanguageActivity.start(this, false)
            }

            viewBinding.btnShareApp.root -> {
                share("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
            }

            viewBinding.btnRateUs.root -> {
                RateDialog(this).show()
            }

            viewBinding.btnPrivacyPolicy.root -> {
                openBrowser(Constant.LINK_POLICY)
            }

            viewBinding.btnPolicySetting.root -> {
//                showPolicySetting()
            }
        }
    }

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, SettingActivity::class.java)
            activity.startActivity(intent)
        }
    }
}

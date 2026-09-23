package com.af.pb.component.flashalert.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.af.pb.R
import com.af.pb.base.activity.BaseActivity
import com.af.pb.databinding.ActivityFlashAlertDetailBinding
import com.af.pb.domain.usecase.GetSelectedAppPackagesUseCase
import com.af.pb.utils.Permission
import com.af.pb.utils.SpManager
import com.af.pb.utils.isPermissionGranted
import com.makeramen.roundedimageview.RoundedImageView
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class FlashAlertDetailActivity : BaseActivity<ActivityFlashAlertDetailBinding>() {

    @Inject
    lateinit var getSelectedAppPackagesUseCase: GetSelectedAppPackagesUseCase

    private var alertType: Int = TYPE_CALL
    private var isProgrammaticChange = false

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val isGranted = result.resultCode == RESULT_OK || areAllPermissionsGranted()
            if (isGranted) {
                SpManager.getInstance(this).setFlashAlertEnabled(alertType, true)
                updateStatusUi(true)
            } else {
                isProgrammaticChange = true
                viewBinding.layoutSwitchCard.swStatus.isChecked = false
                isProgrammaticChange = false
                SpManager.getInstance(this).setFlashAlertEnabled(alertType, false)
                updateStatusUi(false)
            }
        }

    override fun provideViewBinding(): ActivityFlashAlertDetailBinding {
        return ActivityFlashAlertDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        alertType = intent.getIntExtra(EXTRA_ALERT_TYPE, TYPE_CALL)
        super.onCreate(savedInstanceState)
    }

    override fun initViews() {
        super.initViews()
        val binding = viewBinding

        binding.btnBack.setOnClickListener { finish() }

        when (alertType) {
            TYPE_CALL -> {
                binding.tvTitle.text = getString(R.string.incoming_calls)
                binding.imgHeaderIcon.setImageResource(R.drawable.ic_header_incoming_calls)
                binding.btnSelectApp.visibility = View.GONE
            }
            TYPE_SMS -> {
                binding.tvTitle.text = getString(R.string.sms_text)
                binding.imgHeaderIcon.setImageResource(R.drawable.ic_header_sms)
                binding.btnSelectApp.visibility = View.GONE
            }
            TYPE_NOTIFICATION -> {
                binding.tvTitle.text = getString(R.string.notification_text)
                binding.imgHeaderIcon.setImageResource(R.drawable.ic_header_notification)
                binding.btnSelectApp.visibility = View.VISIBLE
            }
        }

        val speedFormatter = { value: Int -> String.format(Locale.US, "%.1fs", value / 10f) }

        val speedCardBinding = binding.layoutFlashingSpeedCard
        speedCardBinding.cardFlashingOn.setRange(1, 50)
        speedCardBinding.cardFlashingOn.setValueFormatter(speedFormatter)
        speedCardBinding.cardFlashingOn.setValue(5) // 0.5s

        speedCardBinding.cardFlashingOff.setRange(1, 50)
        speedCardBinding.cardFlashingOff.setValueFormatter(speedFormatter)
        speedCardBinding.cardFlashingOff.setValue(5) // 0.5s

        fun updateToolsState(isEnabled: Boolean) {
            binding.btnSelectApp.isEnabled = isEnabled
            binding.btnSelectApp.alpha = if (isEnabled) 1.0f else 0.5f

            speedCardBinding.cardFlashingOn.isEnabled = isEnabled
            speedCardBinding.cardFlashingOff.isEnabled = isEnabled
            speedCardBinding.root.alpha = if (isEnabled) 1.0f else 0.5f

            binding.btnTest.isEnabled = isEnabled
            binding.btnTest.alpha = if (isEnabled) 1.0f else 0.5f
        }

        val switchCardBinding = binding.layoutSwitchCard

        fun updateStatusUi(isEnabled: Boolean) {
            switchCardBinding.tvStatus.text = if (isEnabled) getString(R.string.status_on) else getString(R.string.status_off)
            updateToolsState(isEnabled)
        }

        switchCardBinding.swStatus.setOnCheckedChangeListener { _, isChecked ->
            if (isProgrammaticChange) return@setOnCheckedChangeListener
            if (isChecked) {
                if (!areAllPermissionsGranted()) {
                    permissionLauncher.launch(Intent(this, PermissionActivity::class.java))
                } else {
                    SpManager.getInstance(this).setFlashAlertEnabled(alertType, true)
                    updateStatusUi(true)
                }
            } else {
                SpManager.getInstance(this).setFlashAlertEnabled(alertType, false)
                updateStatusUi(false)
            }
        }

        val savedStatus = SpManager.getInstance(this).isFlashAlertEnabled(alertType)
        isProgrammaticChange = true
        switchCardBinding.swStatus.isChecked = savedStatus
        isProgrammaticChange = false
        updateStatusUi(savedStatus)

        binding.btnSelectApp.setOnClickListener {
            SelectAppActivity.start(this)
        }

        binding.btnTest.setOnClickListener {
            val speedOnMs = speedCardBinding.cardFlashingOn.getValue() * 100L
            val speedOffMs = speedCardBinding.cardFlashingOff.getValue() * 100L

            when (alertType) {
                TYPE_CALL -> {
                    TestCallActivity.start(this, speedOnMs, speedOffMs)
                }
                TYPE_SMS -> {
                    TestSmsActivity.start(this, speedOnMs, speedOffMs)
                }
                TYPE_NOTIFICATION -> {
                    TestNotificationActivity.start(this, speedOnMs, speedOffMs)
                }
            }
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return isPermissionGranted(Permission.CAMERA)
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isPermissionGranted(Permission.POST_NOTIFICATIONS)
        } else {
            NotificationManagerCompat.from(this).areNotificationsEnabled()
        }
    }

    private fun areAllPermissionsGranted(): Boolean {
        return isCameraPermissionGranted() && isNotificationPermissionGranted()
    }

    private fun updateStatusUi(isEnabled: Boolean) {
        val switchCardBinding = viewBinding.layoutSwitchCard
        switchCardBinding.tvStatus.text = if (isEnabled) getString(R.string.status_on) else getString(R.string.status_off)

        val speedCardBinding = viewBinding.layoutFlashingSpeedCard
        viewBinding.btnSelectApp.isEnabled = isEnabled
        viewBinding.btnSelectApp.alpha = if (isEnabled) 1.0f else 0.5f

        speedCardBinding.cardFlashingOn.isEnabled = isEnabled
        speedCardBinding.cardFlashingOff.isEnabled = isEnabled
        speedCardBinding.root.alpha = if (isEnabled) 1.0f else 0.5f

        viewBinding.btnTest.isEnabled = isEnabled
        viewBinding.btnTest.alpha = if (isEnabled) 1.0f else 0.5f
    }

    override fun onResume() {
        super.onResume()
        updateSelectedAppsDisplay()
    }

    private fun updateSelectedAppsDisplay() {
        if (alertType != TYPE_NOTIFICATION) return
        val binding = viewBinding
        val container = binding.llSelectedAppIcons
        container.removeAllViews()

        val selectedPackages = getSelectedAppPackagesUseCase.execute(this).toList()
        if (selectedPackages.isEmpty()) return

        val packageManager = packageManager
        val maxNormalIcons = 2
        val displayPackages = selectedPackages.take(maxNormalIcons)
        val remainingCount = selectedPackages.size - maxNormalIcons

        val sizePx = (28 * resources.displayMetrics.density).toInt()
        val marginPx = (6 * resources.displayMetrics.density).toInt()
        val cornerRadiusPx = 8f * resources.displayMetrics.density

        for (pkg in displayPackages) {
            try {
                val iconDrawable = packageManager.getApplicationIcon(pkg)
                val imageView = RoundedImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(sizePx, sizePx).apply {
                        setMargins(0, 0, marginPx, 0)
                    }
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    setCornerRadius(cornerRadiusPx)
                    setImageDrawable(iconDrawable)
                }
                container.addView(imageView)
            } catch (_: Exception) {}
        }

          if (remainingCount > 0) {
            val thirdPackage = selectedPackages.getOrNull(2)
            val badgeContainer = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(sizePx, sizePx).apply {
                    setMargins(0, 0, marginPx, 0)
                }
            }

            if (thirdPackage != null) {
                try {
                    val iconDrawable = packageManager.getApplicationIcon(thirdPackage)
                    val bgImageView = RoundedImageView(this).apply {
                        layoutParams = FrameLayout.LayoutParams(sizePx, sizePx)
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        setCornerRadius(cornerRadiusPx)
                        setImageDrawable(iconDrawable)
                    }
                    badgeContainer.addView(bgImageView)
                } catch (_: Exception) {}
            }

            val darkOverlay = View(this).apply {
                layoutParams = FrameLayout.LayoutParams(sizePx, sizePx)
                background = ContextCompat.getDrawable(this@FlashAlertDetailActivity, R.drawable.bg_more_apps_badge)
            }
            badgeContainer.addView(darkOverlay)

            val countTextView = TextView(this).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                gravity = Gravity.CENTER
                text = "+$remainingCount"
                setTextColor(ContextCompat.getColor(this@FlashAlertDetailActivity, R.color.white))
                textSize = 13f
                typeface = ResourcesCompat.getFont(this@FlashAlertDetailActivity, R.font.plus_jakarta_sans_bold)
            }
            badgeContainer.addView(countTextView)

            container.addView(badgeContainer)
        }
    }

    companion object {
        const val EXTRA_ALERT_TYPE = "extra_alert_type"
        const val TYPE_CALL = 1
        const val TYPE_SMS = 2
        const val TYPE_NOTIFICATION = 3

        fun start(context: Context, type: Int) {
            Intent(context, FlashAlertDetailActivity::class.java).apply {
                putExtra(EXTRA_ALERT_TYPE, type)
            }.also {
                context.startActivity(it)
            }
        }
    }
}

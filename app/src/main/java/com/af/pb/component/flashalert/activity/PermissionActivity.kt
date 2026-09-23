package com.af.pb.component.flashalert.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import com.af.pb.R
import com.af.pb.base.activity.BaseActivity
import com.af.pb.databinding.ActivityPermissionBinding
import com.af.pb.utils.Permission
import com.af.pb.utils.isPermissionGranted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionActivity : BaseActivity<ActivityPermissionBinding>() {

    private var isProgrammaticChange = false

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
            if (!areAllPermissionsGranted()) {
                openNotificationSettings()
            } else {
                updatePermissionUi(true)
            }
        }

    private val settingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            updatePermissionUi(areAllPermissionsGranted())
        }

    override fun provideViewBinding(): ActivityPermissionBinding {
        return ActivityPermissionBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        super.initViews()
        val binding = viewBinding

        binding.btnBack.setOnClickListener {
            finish()
        }

        val appName = getString(R.string.app_name)
        binding.tvDescription.text = getString(R.string.permission_description, appName)

        val isGranted = areAllPermissionsGranted()
        updatePermissionUi(isGranted)

        binding.swPermission.setOnCheckedChangeListener { _, isChecked ->
            if (isProgrammaticChange) return@setOnCheckedChangeListener
            if (isChecked) {
                if (!areAllPermissionsGranted()) {
                    requestRequiredPermissions()
                } else {
                    updatePermissionUi(true)
                }
            } else {
                updatePermissionUi(false)
            }
        }

        binding.btnContinue.setOnClickListener {
            if (binding.swPermission.isChecked && areAllPermissionsGranted()) {
                setResult(RESULT_OK)
            } else {
                setResult(RESULT_CANCELED)
            }
            finish()
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

    private fun requestRequiredPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isNotificationPermissionGranted()) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (!isCameraPermissionGranted()) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else if (!isNotificationPermissionGranted()) {
            openNotificationSettings()
        } else {
            updatePermissionUi(areAllPermissionsGranted())
        }
    }

    private fun openNotificationSettings() {
        try {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
            } else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
            }
            settingsLauncher.launch(intent)
        } catch (_: Exception) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            settingsLauncher.launch(intent)
        }
    }

    private fun updatePermissionUi(isGranted: Boolean) {
        val binding = viewBinding
        isProgrammaticChange = true
        binding.swPermission.isChecked = isGranted
        isProgrammaticChange = false
        binding.btnContinue.isEnabled = isGranted
        binding.btnContinue.alpha = if (isGranted) 1.0f else 0.5f
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, PermissionActivity::class.java)
            context.startActivity(intent)
        }
    }
}

package com.af.pb.component.flashalert.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.af.pb.R
import com.af.pb.base.activity.BaseActivity
import com.af.pb.databinding.ActivityPermissionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionActivity : BaseActivity<ActivityPermissionBinding>() {

    private val requestCameraLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            updatePermissionUi(isGranted)
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

        val isGranted = isCameraPermissionGranted()
        updatePermissionUi(isGranted)

        binding.swPermission.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!isCameraPermissionGranted()) {
                    requestCameraLauncher.launch(Manifest.permission.CAMERA)
                } else {
                    updatePermissionUi(true)
                }
            } else {
                updatePermissionUi(false)
            }
        }

        binding.btnContinue.setOnClickListener {
            if (binding.swPermission.isChecked) {
                setResult(RESULT_OK)
            }
            finish()
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun updatePermissionUi(isGranted: Boolean) {
        val binding = viewBinding
        binding.swPermission.isChecked = isGranted
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

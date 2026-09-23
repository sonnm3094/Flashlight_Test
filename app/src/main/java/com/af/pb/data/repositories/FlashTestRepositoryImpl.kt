package com.af.pb.data.repositories

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import com.af.pb.domain.repository.FlashTestRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashTestRepositoryImpl @Inject constructor() : FlashTestRepository {

    private var flashJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun startTest(context: Context, speedOnMs: Long, speedOffMs: Long) {
        stopTest(context)
        val onInterval = if (speedOnMs <= 0) 500L else speedOnMs
        val offInterval = if (speedOffMs <= 0) 500L else speedOffMs

        flashJob = scope.launch {
            while (isActive) {
                applyTorch(context, true)
                delay(onInterval)
                applyTorch(context, false)
                delay(offInterval)
            }
        }
    }

    override fun stopTest(context: Context) {
        flashJob?.cancel()
        flashJob = null
        applyTorch(context, false)
    }

    private fun applyTorch(context: Context, enable: Boolean) {
        try {
            if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                return
            }
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            val cameraId = cameraManager?.cameraIdList?.firstOrNull() ?: return
            cameraManager.setTorchMode(cameraId, enable)
        } catch (_: Exception) {
            // Safe handling if camera/flash is unavailable or in use
        }
    }
}

package com.af.pb.data.repositories

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import com.af.pb.domain.model.FlashlightMode
import com.af.pb.domain.model.FlashlightState
import com.af.pb.domain.repository.FlashlightRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashlightRepositoryImpl @Inject constructor() : FlashlightRepository {

    private val _state = MutableStateFlow(FlashlightState())
    override val state: StateFlow<FlashlightState> = _state.asStateFlow()

    private var flashJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun toggleFlashlight(context: Context): Boolean {
        val newState = !_state.value.isOn
        return setFlashlightState(context, newState)
    }

    override fun setFlashlightState(context: Context, isOn: Boolean): Boolean {
        _state.value = _state.value.copy(isOn = isOn)
        updateTorch(context)
        return isOn
    }

    override fun setMode(context: Context, mode: FlashlightMode) {
        _state.value = _state.value.copy(mode = mode)
        if (_state.value.isOn) {
            updateTorch(context)
        }
    }

    override fun turnOff(context: Context) {
        setFlashlightState(context, false)
    }

    private fun updateTorch(context: Context) {
        flashJob?.cancel()
        val currentState = _state.value

        if (!currentState.isOn) {
            applyTorch(context, false)
            return
        }

        when (currentState.mode) {
            FlashlightMode.FLASH_LIGHT -> {
                applyTorch(context, true)
            }
            FlashlightMode.SOS -> {
                flashJob = scope.launch {
                    val pattern = listOf(
                        100L, 100L, 100L, 100L, 100L, 200L,
                        300L, 100L, 300L, 100L, 300L, 200L,
                        100L, 100L, 100L, 100L, 100L, 600L
                    )
                    while (isActive && _state.value.isOn && _state.value.mode == FlashlightMode.SOS) {
                        for (i in pattern.indices step 2) {
                            if (!isActive) break
                            applyTorch(context, true)
                            delay(pattern[i])
                            applyTorch(context, false)
                            if (i + 1 < pattern.size) delay(pattern[i + 1])
                        }
                    }
                }
            }
            FlashlightMode.DJ_MODE -> {
                flashJob = scope.launch {
                    while (isActive && _state.value.isOn && _state.value.mode == FlashlightMode.DJ_MODE) {
                        applyTorch(context, true)
                        delay(80L)
                        applyTorch(context, false)
                        delay(80L)
                    }
                }
            }
        }
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

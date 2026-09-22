package com.af.pb.domain.repository

import android.content.Context
import com.af.pb.domain.model.FlashlightMode
import com.af.pb.domain.model.FlashlightState
import kotlinx.coroutines.flow.StateFlow

interface FlashlightRepository {
    val state: StateFlow<FlashlightState>
    fun toggleFlashlight(context: Context): Boolean
    fun setFlashlightState(context: Context, isOn: Boolean): Boolean
    fun setMode(context: Context, mode: FlashlightMode)
    fun turnOff(context: Context)
}

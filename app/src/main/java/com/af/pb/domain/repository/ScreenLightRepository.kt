package com.af.pb.domain.repository

import com.af.pb.domain.model.ScreenLightState
import kotlinx.coroutines.flow.StateFlow

interface ScreenLightRepository {
    val state: StateFlow<ScreenLightState>
    fun setColor(color: Int)
    fun setBrightness(brightness: Int)
}

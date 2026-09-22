package com.af.pb.data.repositories

import com.af.pb.domain.model.ScreenLightState
import com.af.pb.domain.repository.ScreenLightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenLightRepositoryImpl @Inject constructor() : ScreenLightRepository {

    private val _state = MutableStateFlow(ScreenLightState())
    override val state: StateFlow<ScreenLightState> = _state.asStateFlow()

    override fun setColor(color: Int) {
        _state.update { it.copy(selectedColor = color) }
    }

    override fun setBrightness(brightness: Int) {
        _state.update { it.copy(brightness = brightness.coerceIn(0, 100)) }
    }
}

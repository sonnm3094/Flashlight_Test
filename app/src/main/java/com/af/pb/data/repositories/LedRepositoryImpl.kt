package com.af.pb.data.repositories

import com.af.pb.domain.model.LedDirection
import com.af.pb.domain.model.LedState
import com.af.pb.domain.model.LedVisualEffect
import com.af.pb.domain.repository.LedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LedRepositoryImpl @Inject constructor() : LedRepository {

    private val _state = MutableStateFlow(LedState())
    override val state: StateFlow<LedState> = _state.asStateFlow()

    override fun setText(text: String) {
        _state.update { it.copy(text = text) }
    }

    override fun setFontSize(fontSize: Int) {
        _state.update { it.copy(fontSize = fontSize.coerceIn(16, 120)) }
    }

    override fun setScrollSpeed(speed: Int) {
        _state.update { it.copy(scrollSpeed = speed.coerceIn(1, 10)) }
    }

    override fun setTextColor(color: Int) {
        _state.update { it.copy(textColor = color) }
    }

    override fun setDirection(direction: LedDirection) {
        _state.update { it.copy(direction = direction) }
    }

    override fun setVisualEffect(effect: LedVisualEffect) {
        _state.update { it.copy(visualEffect = effect) }
    }

    override fun setBackground(bgId: String, customUri: String?) {
        _state.update { it.copy(selectedBackgroundId = bgId, customBackgroundUri = customUri) }
    }
}

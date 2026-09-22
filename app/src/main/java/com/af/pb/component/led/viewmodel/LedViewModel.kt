package com.af.pb.component.led.viewmodel

import com.af.pb.base.viewmodel.BaseViewModel
import com.af.pb.domain.model.LedDirection
import com.af.pb.domain.model.LedState
import com.af.pb.domain.model.LedVisualEffect
import com.af.pb.domain.usecase.GetLedStateUseCase
import com.af.pb.domain.usecase.UpdateLedConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LedViewModel @Inject constructor(
    getLedStateUseCase: GetLedStateUseCase,
    private val updateLedConfigUseCase: UpdateLedConfigUseCase
) : BaseViewModel() {

    val state: StateFlow<LedState> = getLedStateUseCase.execute()

    fun setText(text: String) {
        updateLedConfigUseCase.setText(text)
    }

    fun setFontSize(fontSize: Int) {
        updateLedConfigUseCase.setFontSize(fontSize)
    }

    fun setScrollSpeed(speed: Int) {
        updateLedConfigUseCase.setScrollSpeed(speed)
    }

    fun setTextColor(color: Int) {
        updateLedConfigUseCase.setTextColor(color)
    }

    fun setDirection(direction: LedDirection) {
        updateLedConfigUseCase.setDirection(direction)
    }

    fun setVisualEffect(effect: LedVisualEffect) {
        updateLedConfigUseCase.setVisualEffect(effect)
    }

    fun setBackground(bgId: String, customUri: String? = null) {
        updateLedConfigUseCase.setBackground(bgId, customUri)
    }
}

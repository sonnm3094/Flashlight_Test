package com.af.pb.domain.usecase

import com.af.pb.domain.model.LedDirection
import com.af.pb.domain.model.LedVisualEffect
import com.af.pb.domain.repository.LedRepository
import javax.inject.Inject

class UpdateLedConfigUseCase @Inject constructor(
    private val repository: LedRepository
) {
    fun setText(text: String) = repository.setText(text)
    fun setFontSize(fontSize: Int) = repository.setFontSize(fontSize)
    fun setScrollSpeed(speed: Int) = repository.setScrollSpeed(speed)
    fun setTextColor(color: Int) = repository.setTextColor(color)
    fun setDirection(direction: LedDirection) = repository.setDirection(direction)
    fun setVisualEffect(effect: LedVisualEffect) = repository.setVisualEffect(effect)
    fun setBackground(bgId: String, customUri: String? = null) = repository.setBackground(bgId, customUri)
}

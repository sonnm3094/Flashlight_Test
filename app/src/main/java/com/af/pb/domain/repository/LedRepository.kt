package com.af.pb.domain.repository

import com.af.pb.domain.model.LedDirection
import com.af.pb.domain.model.LedState
import com.af.pb.domain.model.LedVisualEffect
import kotlinx.coroutines.flow.StateFlow

interface LedRepository {
    val state: StateFlow<LedState>
    fun setText(text: String)
    fun setFontSize(fontSize: Int)
    fun setScrollSpeed(speed: Int)
    fun setTextColor(color: Int)
    fun setDirection(direction: LedDirection)
    fun setVisualEffect(effect: LedVisualEffect)
    fun setBackground(bgId: String, customUri: String? = null)
}

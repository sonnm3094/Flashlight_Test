package com.af.pb.domain.usecase

import com.af.pb.domain.repository.ScreenLightRepository
import javax.inject.Inject

class SetScreenLightBrightnessUseCase @Inject constructor(
    private val repository: ScreenLightRepository
) {
    fun execute(brightness: Int) {
        repository.setBrightness(brightness)
    }
}

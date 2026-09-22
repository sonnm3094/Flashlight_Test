package com.af.pb.domain.usecase

import com.af.pb.domain.repository.ScreenLightRepository
import javax.inject.Inject

class SetScreenLightColorUseCase @Inject constructor(
    private val repository: ScreenLightRepository
) {
    fun execute(color: Int) {
        repository.setColor(color)
    }
}

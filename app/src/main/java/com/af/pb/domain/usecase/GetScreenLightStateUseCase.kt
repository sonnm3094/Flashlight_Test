package com.af.pb.domain.usecase

import com.af.pb.domain.model.ScreenLightState
import com.af.pb.domain.repository.ScreenLightRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetScreenLightStateUseCase @Inject constructor(
    private val repository: ScreenLightRepository
) {
    fun execute(): StateFlow<ScreenLightState> = repository.state
}

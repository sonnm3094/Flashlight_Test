package com.af.pb.domain.usecase

import com.af.pb.domain.model.FlashlightState
import com.af.pb.domain.repository.FlashlightRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetFlashlightStateUseCase @Inject constructor(
    private val repository: FlashlightRepository
) {
    fun execute(): StateFlow<FlashlightState> = repository.state
}

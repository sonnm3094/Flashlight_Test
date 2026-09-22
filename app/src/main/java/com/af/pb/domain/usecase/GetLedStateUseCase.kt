package com.af.pb.domain.usecase

import com.af.pb.domain.model.LedState
import com.af.pb.domain.repository.LedRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetLedStateUseCase @Inject constructor(
    private val repository: LedRepository
) {
    fun execute(): StateFlow<LedState> = repository.state
}

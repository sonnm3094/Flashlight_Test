package com.af.pb.domain.usecase

import android.content.Context
import com.af.pb.domain.repository.FlashlightRepository
import javax.inject.Inject

class ToggleFlashlightUseCase @Inject constructor(
    private val repository: FlashlightRepository
) : UseCase<ToggleFlashlightUseCase.Param, Boolean>() {

    data class Param(val context: Context) : UseCase.Param()

    override suspend fun execute(param: Param): Boolean {
        return repository.toggleFlashlight(param.context)
    }
}

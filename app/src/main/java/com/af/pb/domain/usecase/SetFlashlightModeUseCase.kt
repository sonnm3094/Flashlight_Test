package com.af.pb.domain.usecase

import android.content.Context
import com.af.pb.domain.model.FlashlightMode
import com.af.pb.domain.repository.FlashlightRepository
import javax.inject.Inject

class SetFlashlightModeUseCase @Inject constructor(
    private val repository: FlashlightRepository
) : UseCase<SetFlashlightModeUseCase.Param, Unit>() {

    data class Param(val context: Context, val mode: FlashlightMode) : UseCase.Param()

    override suspend fun execute(param: Param) {
        repository.setMode(param.context, param.mode)
    }
}

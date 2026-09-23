package com.af.pb.domain.usecase

import android.content.Context
import com.af.pb.domain.repository.FlashTestRepository
import javax.inject.Inject

class StartFlashTestUseCase @Inject constructor(
    private val repository: FlashTestRepository
) : UseCase<StartFlashTestUseCase.Param, Unit>() {

    data class Param(
        val context: Context,
        val speedOnMs: Long,
        val speedOffMs: Long
    ) : UseCase.Param()

    override suspend fun execute(param: Param) {
        repository.startTest(param.context, param.speedOnMs, param.speedOffMs)
    }
}

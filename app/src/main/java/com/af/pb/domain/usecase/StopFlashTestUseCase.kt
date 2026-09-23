package com.af.pb.domain.usecase

import android.content.Context
import com.af.pb.domain.repository.FlashTestRepository
import javax.inject.Inject

class StopFlashTestUseCase @Inject constructor(
    private val repository: FlashTestRepository
) : UseCase<StopFlashTestUseCase.Param, Unit>() {

    data class Param(val context: Context) : UseCase.Param()

    override suspend fun execute(param: Param) {
        repository.stopTest(param.context)
    }
}

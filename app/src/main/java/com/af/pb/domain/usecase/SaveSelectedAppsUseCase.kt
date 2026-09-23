package com.af.pb.domain.usecase

import android.content.Context
import com.af.pb.domain.repository.AppInfoRepository
import javax.inject.Inject

class SaveSelectedAppsUseCase @Inject constructor(
    private val repository: AppInfoRepository
) : UseCase<SaveSelectedAppsUseCase.Param, Unit>() {

    data class Param(val context: Context, val selectedPackages: Set<String>) : UseCase.Param()

    override suspend fun execute(param: Param) {
        repository.saveSelectedAppPackages(param.context, param.selectedPackages)
    }
}

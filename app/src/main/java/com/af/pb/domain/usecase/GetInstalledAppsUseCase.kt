package com.af.pb.domain.usecase

import android.content.Context
import com.af.pb.domain.model.AppInfo
import com.af.pb.domain.repository.AppInfoRepository
import javax.inject.Inject

class GetInstalledAppsUseCase @Inject constructor(
    private val repository: AppInfoRepository
) : UseCase<GetInstalledAppsUseCase.Param, List<AppInfo>>() {

    data class Param(val context: Context) : UseCase.Param()

    override suspend fun execute(param: Param): List<AppInfo> {
        return repository.getInstalledApps(param.context)
    }
}

package com.af.pb.domain.usecase

import android.content.Context
import com.af.pb.domain.repository.AppInfoRepository
import javax.inject.Inject

class GetSelectedAppPackagesUseCase @Inject constructor(
    private val repository: AppInfoRepository
) {
    fun execute(context: Context): Set<String> {
        return repository.getSelectedAppPackages(context)
    }
}

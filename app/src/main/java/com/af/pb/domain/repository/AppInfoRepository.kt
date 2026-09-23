package com.af.pb.domain.repository

import android.content.Context
import com.af.pb.domain.model.AppInfo

interface AppInfoRepository {
    suspend fun getInstalledApps(context: Context): List<AppInfo>
    fun getSelectedAppPackages(context: Context): Set<String>
    fun saveSelectedAppPackages(context: Context, packages: Set<String>)
}

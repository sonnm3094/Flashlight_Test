package com.af.pb.data.repositories

import android.content.Context
import android.content.Intent
import com.af.pb.domain.model.AppInfo
import com.af.pb.domain.repository.AppInfoRepository
import com.af.pb.utils.SpManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInfoRepositoryImpl @Inject constructor(
    private val spManager: SpManager
) : AppInfoRepository {

    override suspend fun getInstalledApps(context: Context): List<AppInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = packageManager.queryIntentActivities(mainIntent, 0)
        val selectedPackages = getSelectedAppPackages(context)

        val appList = mutableListOf<AppInfo>()
        val seenPackages = mutableSetOf<String>()

        for (resolveInfo in resolveInfos) {
            val pkgName = resolveInfo.activityInfo.packageName
            if (pkgName == context.packageName || seenPackages.contains(pkgName)) {
                continue
            }
            seenPackages.add(pkgName)

            val appName = resolveInfo.loadLabel(packageManager).toString()
            val isSelected = selectedPackages.contains(pkgName)
            appList.add(AppInfo(packageName = pkgName, appName = appName, isSelected = isSelected))
        }

        appList.sortBy { it.appName.lowercase() }
        appList
    }

    override fun getSelectedAppPackages(context: Context): Set<String> {
        return spManager.getSelectedApps()
    }

    override fun saveSelectedAppPackages(context: Context, packages: Set<String>) {
        spManager.saveSelectedApps(packages)
    }
}

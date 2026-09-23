package com.af.pb.component.flashalert.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.af.pb.domain.model.AppInfo
import com.af.pb.domain.usecase.GetInstalledAppsUseCase
import com.af.pb.domain.usecase.SaveSelectedAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectAppViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val saveSelectedAppsUseCase: SaveSelectedAppsUseCase
) : ViewModel() {

    private val _appList = MutableStateFlow<List<AppInfo>>(emptyList())
    val appList: StateFlow<List<AppInfo>> = _appList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isAllSelected = MutableStateFlow(false)
    val isAllSelected: StateFlow<Boolean> = _isAllSelected.asStateFlow()

    fun loadInstalledApps(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            val apps = getInstalledAppsUseCase.execute(GetInstalledAppsUseCase.Param(context))
            _appList.value = apps
            _isAllSelected.value = apps.isNotEmpty() && apps.all { it.isSelected }
            _isLoading.value = false
        }
    }

    fun toggleAppSelection(appInfo: AppInfo) {
        val currentList = _appList.value.toMutableList()
        val index = currentList.indexOfFirst { it.packageName == appInfo.packageName }
        if (index != -1) {
            val updatedApp = currentList[index].copy(isSelected = !currentList[index].isSelected)
            currentList[index] = updatedApp
            _appList.value = currentList
            _isAllSelected.value = currentList.isNotEmpty() && currentList.all { it.isSelected }
        }
    }

    fun toggleSelectAll(context: Context) {
        val currentList = _appList.value
        val newSelectState = !_isAllSelected.value
        val updatedList = currentList.map { it.copy(isSelected = newSelectState) }
        _appList.value = updatedList
        _isAllSelected.value = newSelectState
        saveSelection(context)
    }

    fun saveSelection(context: Context) {
        viewModelScope.launch {
            val selectedPackages = _appList.value.filter { it.isSelected }.map { it.packageName }.toSet()
            saveSelectedAppsUseCase.execute(SaveSelectedAppsUseCase.Param(context, selectedPackages))
        }
    }
}

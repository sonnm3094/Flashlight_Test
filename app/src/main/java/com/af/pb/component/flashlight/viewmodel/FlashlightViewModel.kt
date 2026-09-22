package com.af.pb.component.flashlight.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.af.pb.base.viewmodel.BaseViewModel
import com.af.pb.domain.model.BottomTab
import com.af.pb.domain.model.FlashlightMode
import com.af.pb.domain.model.FlashlightState
import com.af.pb.domain.usecase.GetFlashlightStateUseCase
import com.af.pb.domain.usecase.SetFlashlightModeUseCase
import com.af.pb.domain.usecase.ToggleFlashlightUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashlightViewModel @Inject constructor(
    private val toggleFlashlightUseCase: ToggleFlashlightUseCase,
    private val setFlashlightModeUseCase: SetFlashlightModeUseCase,
    getFlashlightStateUseCase: GetFlashlightStateUseCase
) : BaseViewModel() {

    val flashlightState: StateFlow<FlashlightState> = getFlashlightStateUseCase.execute()

    private val _selectedTab = MutableStateFlow(BottomTab.FLASHLIGHT)
    val selectedTab: StateFlow<BottomTab> = _selectedTab.asStateFlow()

    fun toggleFlashlight(context: Context) {
        viewModelScope.launch {
            toggleFlashlightUseCase.execute(ToggleFlashlightUseCase.Param(context))
        }
    }

    fun selectMode(context: Context, mode: FlashlightMode) {
        viewModelScope.launch {
            setFlashlightModeUseCase.execute(SetFlashlightModeUseCase.Param(context, mode))
        }
    }

    fun selectTab(tab: BottomTab) {
        _selectedTab.value = tab
    }
}

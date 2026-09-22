package com.af.pb.component.screenlight.viewmodel

import com.af.pb.base.viewmodel.BaseViewModel
import com.af.pb.domain.model.ScreenLightState
import com.af.pb.domain.usecase.GetScreenLightStateUseCase
import com.af.pb.domain.usecase.SetScreenLightBrightnessUseCase
import com.af.pb.domain.usecase.SetScreenLightColorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ScreenLightViewModel @Inject constructor(
    getScreenLightStateUseCase: GetScreenLightStateUseCase,
    private val setScreenLightColorUseCase: SetScreenLightColorUseCase,
    private val setScreenLightBrightnessUseCase: SetScreenLightBrightnessUseCase
) : BaseViewModel() {

    val state: StateFlow<ScreenLightState> = getScreenLightStateUseCase.execute()

    fun setColor(color: Int) {
        setScreenLightColorUseCase.execute(color)
    }

    fun setBrightness(brightness: Int) {
        setScreenLightBrightnessUseCase.execute(brightness)
    }
}

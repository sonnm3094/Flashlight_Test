package com.af.pb.component.onboarding.viewmodel

import androidx.lifecycle.viewModelScope
import com.af.pb.base.viewmodel.BaseViewModel
import com.af.pb.data.model.OnBoarding
import com.af.pb.domain.usecase.GetListOnBoardingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(private val getListOnBoardingUseCase: GetListOnBoardingUseCase) : BaseViewModel() {

    private val listOnBoardingFlow = MutableStateFlow<List<OnBoarding>>(emptyList())
    var listOnBoarding = listOnBoardingFlow.asStateFlow()
    var currentPosition = 0

    fun getListOnBoarding() {
        viewModelScope.launch {
            listOnBoardingFlow.value = getListOnBoardingUseCase.execute(GetListOnBoardingUseCase.Param())
        }
    }
}
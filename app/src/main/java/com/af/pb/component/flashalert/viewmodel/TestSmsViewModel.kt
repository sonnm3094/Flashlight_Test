package com.af.pb.component.flashalert.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.af.pb.domain.usecase.StartFlashTestUseCase
import com.af.pb.domain.usecase.StopFlashTestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestSmsViewModel @Inject constructor(
    private val startFlashTestUseCase: StartFlashTestUseCase,
    private val stopFlashTestUseCase: StopFlashTestUseCase
) : ViewModel() {

    fun startSmsTest(context: Context, speedOnMs: Long, speedOffMs: Long) {
        viewModelScope.launch {
            startFlashTestUseCase.execute(StartFlashTestUseCase.Param(context, speedOnMs, speedOffMs))
        }
    }

    fun stopSmsTest(context: Context) {
        viewModelScope.launch {
            stopFlashTestUseCase.execute(StopFlashTestUseCase.Param(context))
        }
    }
}

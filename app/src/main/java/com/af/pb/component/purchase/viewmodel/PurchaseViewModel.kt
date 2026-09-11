package com.af.pb.component.purchase.viewmodel

import androidx.lifecycle.viewModelScope
import com.af.pb.base.viewmodel.BaseViewModel
import com.af.pb.data.model.BenefitModel
import com.af.pb.data.model.PurchaseModel
import com.af.pb.domain.usecase.GetListBenefitUseCase
import com.af.pb.domain.usecase.GetListPurchaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    private val getListPurchaseUseCase: GetListPurchaseUseCase,
    private val getListBenefitUseCase: GetListBenefitUseCase
) : BaseViewModel() {

    private val _purchases = MutableStateFlow<List<PurchaseModel>>(emptyList())
    val purchases: StateFlow<List<PurchaseModel>> = _purchases.asStateFlow()

    private val _benefits = MutableStateFlow<List<BenefitModel>>(emptyList())
    val benefits: StateFlow<List<BenefitModel>> = _benefits.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _purchases.value = getListPurchaseUseCase.execute(GetListPurchaseUseCase.Param())
            _benefits.value = getListBenefitUseCase.execute(GetListBenefitUseCase.Param())
        }
    }
}
package com.af.pb.data.repositories

import com.af.pb.data.model.BenefitModel
import com.af.pb.data.model.Language
import com.af.pb.data.model.OnBoarding
import com.af.pb.data.model.PurchaseModel
import com.af.pb.data.sources.LocalDataSource
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) {

    fun getListLanguage(): List<Language> = localDataSource.getListLanguage()

    fun getListOnBoarding(): List<OnBoarding> = localDataSource.getListOnBoarding()

    fun getListBenefit(): List<BenefitModel> = localDataSource.getListBenefit()
    fun getListPurchase(): List<PurchaseModel> = localDataSource.getListPurchase()

}
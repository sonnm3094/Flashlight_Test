package com.af.pb.data.sources

import com.af.pb.R
import com.af.pb.data.model.BenefitModel
import com.af.pb.data.model.Language
import com.af.pb.data.model.OnBoarding
import com.af.pb.data.model.PurchaseModel
import com.af.pb.utils.Constant
import javax.inject.Inject

class LocalDataSource @Inject constructor() {
    fun getListLanguage(): List<Language> {
        return listOf(
            Language("en", R.string.english, "eng.png"),
            Language("es", R.string.spanish, "sp.png"),
            Language("hi", R.string.hindi, "hindi.png"),
            Language("ko", R.string.korean, "kr.png"),
            Language("ja", R.string.japanese, "jp.png"),
            Language("de", R.string.german, "gm.png"),
            Language("pt", R.string.portuguese, "ptg.png"),
            Language("fr", R.string.french, "fr.png"),
            Language("it", R.string.italian, "italy.png"),
            Language("in", R.string.indonesian, "indo.png"),
            Language("ru", R.string.russian, "rus.png"),
            Language("tr", R.string.turkish, "tk.png"),
            Language("zh-TW", R.string.chinese_traditional, "cn.png"),
            Language("vi", R.string.vietnamese, "vn.png")
        )
    }

    fun getListOnBoarding(): List<OnBoarding> {
        return listOf(
            OnBoarding(R.mipmap.bg_onboarding_1, R.string.title_onboarding_1, R.string.des_onboarding_1, type = OnBoarding.TYPE_1),
            OnBoarding(R.mipmap.bg_onboarding_2, R.string.title_onboarding_2, R.string.des_onboarding_2, type = OnBoarding.TYPE_2),
            OnBoarding(R.mipmap.bg_onboarding_3, R.string.title_onboarding_3, R.string.des_onboarding_3, type = OnBoarding.TYPE_1),
            OnBoarding(R.mipmap.bg_onboarding_4, R.string.title_onboarding_4, R.string.des_onboarding_4, type = OnBoarding.TYPE_2)
        )
    }

    fun getListBenefit(): List<BenefitModel> {
        return listOf(
            BenefitModel(R.mipmap.ic_bnf_1, R.mipmap.ic_bnf_1_large, R.string.unlimited_viewing),
            BenefitModel(R.mipmap.ic_bnf_4, R.mipmap.ic_bnf_4_large, R.string.vip_dramas),
            BenefitModel(R.mipmap.ic_bnf_2, R.mipmap.ic_bnf_2_large, R.string.hd_1080),
            BenefitModel(R.mipmap.ic_bnf_3, R.mipmap.ic_bnf_3_large, R.string.ad_free)
        )
    }

    fun getListPurchase(): List<PurchaseModel> {
        return listOf(
            PurchaseModel(
                Constant.MONTHLY_IAP,
                R.string.monthly,
                R.string.monthly_des,
                R.string.most_popular,
                isMostPopular = true,
                isBestValue = false,
                price = "$7.99",
                R.string.month,
                isSelected = true
            ),
            PurchaseModel(
                Constant.WEEKLY_IAP,
                R.string.weekly,
                R.string.weekly_des,
                R.string.weekly,
                isMostPopular = false,
                isBestValue = false,
                price = "$2.99",
                R.string.week,
                isSelected = false
            ),
            PurchaseModel(
                Constant.YEARLY_IAP,
                R.string.yearly,
                R.string.yearly_des,
                R.string.best_value,
                isMostPopular = false,
                isBestValue = true,
                price = "$39.99",
                R.string.year,
                isSelected = false
            )
        )
    }

}
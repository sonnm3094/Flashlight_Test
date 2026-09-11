package com.ads.admob.helper.appoppen

import com.ads.admob.config.NetworkProvider
import com.ads.admob.helper.IAdsConfig


class AppResumeAdConfig(
    override val idAds: String,
    val networkProvider: Int = NetworkProvider.ADMOB,
    val listClassInValid: MutableList<Class<*>> = arrayListOf(),
    override val canShowAds: Boolean = false,
    override val canReloadAds: Boolean = false,
    override val adPlacement: String,
    override val reloadIfFirstFail: Boolean = false,
    val loadOnResume: Boolean = false,
) : IAdsConfig
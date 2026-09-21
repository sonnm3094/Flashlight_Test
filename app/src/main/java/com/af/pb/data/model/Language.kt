package com.af.pb.data.model

data class Language (
    val languageCode: String,
    val nameRes: Int,
    val flagName: String = "",
    var selected: Boolean = false
)
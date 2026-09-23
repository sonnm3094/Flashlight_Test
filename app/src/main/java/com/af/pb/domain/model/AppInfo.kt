package com.af.pb.domain.model

data class AppInfo(
    val packageName: String,
    val appName: String,
    var isSelected: Boolean = false
)

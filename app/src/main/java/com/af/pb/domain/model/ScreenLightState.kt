package com.af.pb.domain.model

data class ScreenLightState(
    val selectedColor: Int = 0xFF38A8DF.toInt(),
    val brightness: Int = 80,
    val presetColors: List<Int> = listOf(
        0xFF38A8DF.toInt(),
        0xFFFFFFFF.toInt(),
        0xFFFFD54F.toInt(),
        0xFF81C784.toInt(),
        0xFFBA68C8.toInt(),
        0xFFE57373.toInt()
    )
)

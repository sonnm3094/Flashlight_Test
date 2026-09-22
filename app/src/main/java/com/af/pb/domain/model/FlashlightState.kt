package com.af.pb.domain.model

data class FlashlightState(
    val isOn: Boolean = false,
    val mode: FlashlightMode = FlashlightMode.FLASH_LIGHT
)

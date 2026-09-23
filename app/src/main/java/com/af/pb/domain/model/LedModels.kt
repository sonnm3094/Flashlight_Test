package com.af.pb.domain.model

enum class LedDirection {
    RIGHT,
    LEFT,
    DOWN,
    UP
}

enum class LedVisualEffect {
    GLOW,
    BLINK,
    NEON,
    FADE
}

data class LedBackgroundItem(
    val id: String,
    val resId: Int? = null,
    val uriString: String? = null,
    val assetPath: String? = null,
    val isAddButton: Boolean = false
)

data class LedState(
    val text: String = "HELLO WORLD",
    val fontSize: Int = 64, // px
    val scrollSpeed: Int = 5, // seconds for full traverse
    val textColor: Int = 0xFFFFD54F.toInt(),
    val direction: LedDirection = LedDirection.LEFT,
    val visualEffect: LedVisualEffect = LedVisualEffect.GLOW,
    val selectedBackgroundId: String = "background/bg_1.jpg",
    val customBackgroundUri: String? = null
)

package com.af.pb.data.model

data class OnBoarding(
    val imageId: Int,
    val title: Int,
    val description: Int,
    val tag: String = "",
    val type: Int = TYPE_1
) {
    companion object {
        const val TYPE_1 = 1
        const val TYPE_2 = 2
    }
}

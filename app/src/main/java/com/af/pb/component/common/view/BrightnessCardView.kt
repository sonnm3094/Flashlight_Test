package com.af.pb.component.common.view

import android.content.Context
import android.util.AttributeSet
import com.af.pb.R

class BrightnessCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SliderCardView(context, attrs, defStyleAttr) {

    init {
        setBackgroundResource(R.drawable.bg_card_container)
        val p = (16 * resources.displayMetrics.density).toInt()
        setPadding(p, p, p, p)
        setTitleRes(R.string.brightness)
        setUnit("%")
        setRange(0, 100)
    }

    fun setOnBrightnessChangeListener(listener: (progress: Int, fromUser: Boolean) -> Unit) {
        setOnValueChangeListener { value, fromUser ->
            listener.invoke(value, fromUser)
        }
    }

    fun setProgress(progress: Int) {
        setValue(progress)
    }

    fun getProgress(): Int = getValue()
}
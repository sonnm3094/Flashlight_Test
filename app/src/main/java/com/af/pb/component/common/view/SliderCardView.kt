package com.af.pb.component.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.SeekBar
import com.af.pb.R
import com.af.pb.databinding.ViewSliderCardBinding

open class SliderCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val binding: ViewSliderCardBinding =
        ViewSliderCardBinding.inflate(LayoutInflater.from(context), this, true)

    private var minValue: Int = 0
    private var maxValue: Int = 100
    private var unit: String = "%"
    private var onValueChangeListener: ((value: Int, fromUser: Boolean) -> Unit)? = null
    private var valueFormatter: ((value: Int) -> String)? = null

    init {
        attrs?.let {
            val typedArray = context.theme.obtainStyledAttributes(
                it,
                R.styleable.SliderCardView,
                defStyleAttr,
                0
            )
            try {
                val title = typedArray.getString(R.styleable.SliderCardView_sliderTitle) ?: ""
                unit = typedArray.getString(R.styleable.SliderCardView_sliderUnit) ?: "%"
                minValue = typedArray.getInt(R.styleable.SliderCardView_sliderMin, 0)
                maxValue = typedArray.getInt(R.styleable.SliderCardView_sliderMax, 100)
                val initialVal = typedArray.getInt(R.styleable.SliderCardView_sliderValue, minValue)

                if (title.isNotEmpty()) setTitle(title)
                setRange(minValue, maxValue)
                setValue(initialVal)
            } finally {
                typedArray.recycle()
            }
        }

        binding.sbSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val actualValue = (progress + minValue).coerceIn(minValue, maxValue)
                updateValueText(actualValue)
                onValueChangeListener?.invoke(actualValue, fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        binding.sbSlider.isEnabled = enabled
        binding.tvTitle.isEnabled = enabled
        binding.tvValue.isEnabled = enabled
        alpha = if (enabled) 1.0f else 0.5f
    }

    fun setRange(min: Int, max: Int) {
        this.minValue = min
        this.maxValue = max
        val range = (maxValue - minValue).coerceAtLeast(1)
        binding.sbSlider.max = range
        updateValueText(getValue())
    }

    fun setUnit(unit: String) {
        this.unit = unit
        updateValueText(getValue())
    }

    fun setOnValueChangeListener(listener: (value: Int, fromUser: Boolean) -> Unit) {
        this.onValueChangeListener = listener
    }

    fun setValueFormatter(formatter: (value: Int) -> String) {
        this.valueFormatter = formatter
        updateValueText(getValue())
    }

    fun setValue(value: Int) {
        val clamped = value.coerceIn(minValue, maxValue)
        updateValueText(clamped)
        val progress = clamped - minValue
        if (binding.sbSlider.progress != progress) {
            binding.sbSlider.progress = progress
        }
    }

    fun getValue(): Int {
        return (binding.sbSlider.progress + minValue).coerceIn(minValue, maxValue)
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun setTitleRes(titleRes: Int) {
        binding.tvTitle.setText(titleRes)
    }

    fun setTitleColor(color: Int) {
        binding.tvTitle.setTextColor(color)
    }

    private fun updateValueText(value: Int) {
        val customText = valueFormatter?.invoke(value)
        if (customText != null) {
            binding.tvValue.text = customText
        } else {
            binding.tvValue.text = "$value$unit"
        }
    }
}
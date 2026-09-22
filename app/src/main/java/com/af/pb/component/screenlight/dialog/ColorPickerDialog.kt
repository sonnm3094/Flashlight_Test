package com.af.pb.component.screenlight.dialog

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import com.af.pb.base.dialog.BaseDialog
import com.af.pb.databinding.DialogColorPickerBinding

class ColorPickerDialog(
    context: Context,
    private val initialColor: Int,
    private val onColorSelected: (Int) -> Unit
) : BaseDialog<DialogColorPickerBinding>(context) {

    private var currentHue: Float = 0f
    private var currentSat: Float = 1f
    private var currentVal: Float = 1f
    private var currentAlpha: Int = 255

    private var isUpdatingFromCode = false

    override fun provideViewBinding(): DialogColorPickerBinding {
        return DialogColorPickerBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setCancelable(true)
        setCanceledOnTouchOutside(true)

        val hsv = FloatArray(3)
        Color.colorToHSV(initialColor, hsv)
        currentHue = hsv[0]
        currentSat = hsv[1]
        currentVal = hsv[2]
        currentAlpha = Color.alpha(initialColor)

        vSatValSpectrum.setHue(currentHue)
        vSatValSpectrum.setSatVal(currentSat, currentVal)
        vHueSlider.setHue(currentHue)
        val opaqueColor = Color.HSVToColor(floatArrayOf(currentHue, currentSat, currentVal))
        vAlphaSlider.setColor(opaqueColor)
        vAlphaSlider.setAlphaValue(currentAlpha)

        updateTextInputs(getCurrentColor())

        vSatValSpectrum.setOnColorChangedListener { sat, value ->
            currentSat = sat
            currentVal = value
            val baseColor = Color.HSVToColor(floatArrayOf(currentHue, currentSat, currentVal))
            vAlphaSlider.setColor(baseColor)
            updateTextInputs(getCurrentColor())
        }

        vHueSlider.setOnHueChangedListener { hue ->
            currentHue = hue
            vSatValSpectrum.setHue(currentHue)
            val baseColor = Color.HSVToColor(floatArrayOf(currentHue, currentSat, currentVal))
            vAlphaSlider.setColor(baseColor)
            updateTextInputs(getCurrentColor())
        }

        vAlphaSlider.setOnAlphaChangedListener { alpha ->
            currentAlpha = alpha
            updateTextInputs(getCurrentColor())
        }

        setupTextWatchers()

        btnChoose.setOnClickListener {
            onColorSelected(getCurrentColor())
            dismiss()
        }
    }

    private fun getCurrentColor(): Int {
        return Color.HSVToColor(currentAlpha, floatArrayOf(currentHue, currentSat, currentVal))
    }

    private fun updateTextInputs(color: Int) {
        with(viewBinding) {
            if (isUpdatingFromCode) return
            isUpdatingFromCode = true

            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)

            val hex = String.format("#%02X%02X%02X", r, g, b)
            etHex.setText(hex)
            etR.setText(r.toString())
            etG.setText(g.toString())
            etB.setText(b.toString())

            isUpdatingFromCode = false
        }
    }

    private fun setupTextWatchers() {
        with(viewBinding) {
            etHex.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (isUpdatingFromCode) return
                    val hexStr = s?.toString()?.trim() ?: return
                    if (hexStr.length == 7 && hexStr.startsWith("#")) {
                        try {
                            val parsed = Color.parseColor(hexStr)
                            applyColorFromInputs(parsed, updateHex = false)
                        } catch (_: Exception) {}
                    } else if (hexStr.length == 6 && !hexStr.startsWith("#")) {
                        try {
                            val parsed = Color.parseColor("#$hexStr")
                            applyColorFromInputs(parsed, updateHex = false)
                        } catch (_: Exception) {}
                    }
                }
            })

            val rgbWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (isUpdatingFromCode) return
                    val r = etR.text.toString().toIntOrNull() ?: return
                    val g = etG.text.toString().toIntOrNull() ?: return
                    val b = etB.text.toString().toIntOrNull() ?: return
                    if (r in 0..255 && g in 0..255 && b in 0..255) {
                        val parsed = Color.argb(currentAlpha, r, g, b)
                        applyColorFromInputs(parsed, updateRgb = false)
                    }
                }
            }

            etR.addTextChangedListener(rgbWatcher)
            etG.addTextChangedListener(rgbWatcher)
            etB.addTextChangedListener(rgbWatcher)
        }
    }

    private fun applyColorFromInputs(color: Int, updateHex: Boolean = true, updateRgb: Boolean = true) {
        with(viewBinding) {
            isUpdatingFromCode = true
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            currentHue = hsv[0]
            currentSat = hsv[1]
            currentVal = hsv[2]

            vSatValSpectrum.setHue(currentHue)
            vSatValSpectrum.setSatVal(currentSat, currentVal)
            vHueSlider.setHue(currentHue)
            val baseColor = Color.HSVToColor(floatArrayOf(currentHue, currentSat, currentVal))
            vAlphaSlider.setColor(baseColor)

            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)

            if (updateHex) {
                etHex.setText(String.format("#%02X%02X%02X", r, g, b))
            }
            if (updateRgb) {
                etR.setText(r.toString())
                etG.setText(g.toString())
                etB.setText(b.toString())
            }

            isUpdatingFromCode = false
        }
    }
}

package com.af.pb.component.led.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import com.af.pb.base.dialog.BaseFullScreenDialogFragment
import com.af.pb.databinding.DialogFullscreenLedBinding
import com.af.pb.domain.model.LedDirection
import com.af.pb.domain.model.LedState
import com.af.pb.domain.model.LedVisualEffect

class LedFullScreenDialog : BaseFullScreenDialogFragment<DialogFullscreenLedBinding>(0) {

    private var text: String = "HELLO WORLD"
    private var fontSize: Int = 64
    private var scrollSpeed: Int = 5
    private var textColor: Int = 0xFFFFD54F.toInt()
    private var direction: LedDirection = LedDirection.LEFT
    private var visualEffect: LedVisualEffect = LedVisualEffect.GLOW
    private var bgResId: Int? = null
    private var bgUriString: String? = null

    override var allowBackToCancel: Boolean = true

    override fun inflateDialogBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogFullscreenLedBinding {
        return DialogFullscreenLedBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            text = it.getString(ARG_TEXT, "HELLO WORLD")
            fontSize = it.getInt(ARG_FONT_SIZE, 64)
            scrollSpeed = it.getInt(ARG_SPEED, 5)
            textColor = it.getInt(ARG_COLOR, 0xFFFFD54F.toInt())
            direction = LedDirection.values()[it.getInt(ARG_DIRECTION, 1)]
            visualEffect = LedVisualEffect.values()[it.getInt(ARG_EFFECT, 0)]
            val res = it.getInt(ARG_BG_RES, 0)
            if (res != 0) bgResId = res
            bgUriString = it.getString(ARG_BG_URI)
        }
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        vFullScreenLed.cornerRadius = 0f
        vFullScreenLed.setLedText(text)
        vFullScreenLed.setLedFontSize(fontSize)
        vFullScreenLed.setLedScrollSpeed(scrollSpeed)
        vFullScreenLed.setLedTextColor(textColor)
        vFullScreenLed.setLedDirection(direction)
        vFullScreenLed.setLedVisualEffect(visualEffect)

        if (!bgUriString.isNullOrEmpty()) {
            vFullScreenLed.setLedBackgroundUri(bgUriString)
        } else {
            vFullScreenLed.setLedBackgroundRes(bgResId)
        }

        flFullScreenContainer.setOnClickListener {
            dismiss()
        }

        btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    companion object {
        private const val ARG_TEXT = "arg_text"
        private const val ARG_FONT_SIZE = "arg_font_size"
        private const val ARG_SPEED = "arg_speed"
        private const val ARG_COLOR = "arg_color"
        private const val ARG_DIRECTION = "arg_direction"
        private const val ARG_EFFECT = "arg_effect"
        private const val ARG_BG_RES = "arg_bg_res"
        private const val ARG_BG_URI = "arg_bg_uri"

        fun newInstance(state: LedState, bgResId: Int?): LedFullScreenDialog {
            return LedFullScreenDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_TEXT, state.text)
                    putInt(ARG_FONT_SIZE, state.fontSize)
                    putInt(ARG_SPEED, state.scrollSpeed)
                    putInt(ARG_COLOR, state.textColor)
                    putInt(ARG_DIRECTION, state.direction.ordinal)
                    putInt(ARG_EFFECT, state.visualEffect.ordinal)
                    putInt(ARG_BG_RES, bgResId ?: 0)
                    putString(ARG_BG_URI, state.customBackgroundUri)
                }
            }
        }
    }
}

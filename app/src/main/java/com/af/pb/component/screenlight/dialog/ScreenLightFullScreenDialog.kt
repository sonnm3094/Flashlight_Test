package com.af.pb.component.screenlight.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.af.pb.base.dialog.BaseFullScreenDialogFragment
import com.af.pb.databinding.DialogFullscreenLightBinding

class ScreenLightFullScreenDialog : BaseFullScreenDialogFragment<DialogFullscreenLightBinding>(0) {

    private var color: Int = 0xFFFFFFFF.toInt()
    private var brightness: Int = 100

    override var allowBackToCancel: Boolean = true
    override val forceLandscape: Boolean = true

    override fun inflateDialogBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogFullscreenLightBinding {
        return DialogFullscreenLightBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            color = it.getInt(ARG_COLOR, 0xFFFFFFFF.toInt())
            brightness = it.getInt(ARG_BRIGHTNESS, 100)
        }
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        vColorView.setBackgroundColor(color)

        ViewCompat.setOnApplyWindowInsetsListener(btnClose) { v, insets ->
            val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.systemBars())
            val params = v.layoutParams as? ViewGroup.MarginLayoutParams
            params?.topMargin = (28 * v.resources.displayMetrics.density).toInt() + cutout.top
            params?.rightMargin = (40 * v.resources.displayMetrics.density).toInt() + cutout.right
            v.layoutParams = params
            insets
        }

        flFullScreenContainer.setOnClickListener {
            dismiss()
        }

        btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        applyBrightness()
    }

    private fun applyBrightness() {
        val targetBrightness = (brightness / 100f).coerceIn(0.01f, 1.0f)

        dialog?.window?.let { window ->
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            val lp = window.attributes
            lp.screenBrightness = targetBrightness
            window.attributes = lp
        }

        activity?.window?.let { window ->
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            val lp = window.attributes
            lp.screenBrightness = targetBrightness
            window.attributes = lp
        }
    }

    companion object {
        private const val ARG_COLOR = "arg_color"
        private const val ARG_BRIGHTNESS = "arg_brightness"

        fun newInstance(color: Int, brightness: Int): ScreenLightFullScreenDialog {
            return ScreenLightFullScreenDialog().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLOR, color)
                    putInt(ARG_BRIGHTNESS, brightness)
                }
            }
        }
    }
}

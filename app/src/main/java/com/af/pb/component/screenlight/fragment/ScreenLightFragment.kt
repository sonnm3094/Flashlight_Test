package com.af.pb.component.screenlight.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.af.pb.base.fragment.BaseFragment
import com.af.pb.component.screenlight.dialog.ColorPickerDialog
import com.af.pb.component.screenlight.dialog.ScreenLightFullScreenDialog
import com.af.pb.component.screenlight.viewmodel.ScreenLightViewModel
import com.af.pb.databinding.FragmentScreenLightBinding
import com.af.pb.domain.model.ScreenLightState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ScreenLightFragment : BaseFragment<FragmentScreenLightBinding>() {

    private val viewModel: ScreenLightViewModel by activityViewModels()

    override fun provideViewBinding(container: ViewGroup?): FragmentScreenLightBinding {
        return FragmentScreenLightBinding.inflate(LayoutInflater.from(context), container, false)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()

        cardColor.setPresetColors(viewModel.state.value.presetColors)

        cardColor.setOnColorSelectedListener { color ->
            viewModel.setColor(color)
        }

        cardColor.setOnPaletteClickListener {
            val currentColor = viewModel.state.value.selectedColor
            ColorPickerDialog(requireContext(), currentColor) { selectedColor ->
                viewModel.setColor(selectedColor)
            }.show()
        }

        cardBrightness.setOnBrightnessChangeListener { progress, fromUser ->
            if (fromUser) {
                layoutScreenLightPreview.vColorPreview.alpha = (progress / 100f).coerceIn(0.15f, 1.0f)
                applyBrightness(progress)
                viewModel.setBrightness(progress)
            }
        }

        layoutScreenLightPreview.btnFullscreen.setOnClickListener {
            val currentState = viewModel.state.value
            ScreenLightFullScreenDialog.newInstance(
                currentState.selectedColor,
                currentState.brightness
            ).show(childFragmentManager, "ScreenLightFullScreenDialog")
        }
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.state.onEach { state ->
            updateUi(state)
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun updateUi(state: ScreenLightState) = with(viewBinding) {
        layoutScreenLightPreview.vColorPreview.setImageDrawable(ColorDrawable(state.selectedColor))
        layoutScreenLightPreview.vColorPreview.alpha = (state.brightness / 100f).coerceIn(0.15f, 1.0f)

        cardBrightness.setProgress(state.brightness)
        cardColor.setPresetColors(state.presetColors)
        cardColor.setSelectedColor(state.selectedColor)

        applyBrightness(state.brightness)
    }

    private fun applyBrightness(brightness: Int) {
        val window = activity?.window ?: return
        val lp = window.attributes
        lp.screenBrightness = (brightness / 100f).coerceIn(0.01f, 1.0f)
        window.attributes = lp
    }

    override fun onResume() {
        super.onResume()
        applyBrightness(viewModel.state.value.brightness)
    }

    override fun onPause() {
        super.onPause()
        resetBrightness()
    }

    private fun resetBrightness() {
        val window = activity?.window ?: return
        val lp = window.attributes
        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        window.attributes = lp
    }

    companion object {
        fun newInstance() = ScreenLightFragment()
    }
}
package com.af.pb.component.flashlight.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.af.pb.R
import com.af.pb.base.fragment.BaseFragment
import com.af.pb.component.flashlight.viewmodel.FlashlightViewModel
import com.af.pb.component.main.setting.SettingActivity
import com.af.pb.databinding.FragmentFlashlightBinding
import com.af.pb.domain.model.FlashlightMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class FlashlightFragment : BaseFragment<FragmentFlashlightBinding>() {

    private val viewModel: FlashlightViewModel by activityViewModels()

    override fun provideViewBinding(container: ViewGroup?): FragmentFlashlightBinding {
        return FragmentFlashlightBinding.inflate(LayoutInflater.from(context), container, false)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()

        btnSetting.setOnClickListener {
            SettingActivity.start(requireActivity())
        }

        clPowerButton.setOnClickListener {
            viewModel.toggleFlashlight(requireContext())
        }

        btnModeFlash.setOnClickListener {
            viewModel.selectMode(requireContext(), FlashlightMode.FLASH_LIGHT)
        }

        btnModeSos.setOnClickListener {
            viewModel.selectMode(requireContext(), FlashlightMode.SOS)
        }

        btnModeDj.setOnClickListener {
            viewModel.selectMode(requireContext(), FlashlightMode.DJ_MODE)
        }
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.flashlightState.onEach { state ->
            updateFlashlightUi(state.isOn, state.mode)
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun updateFlashlightUi(isOn: Boolean, mode: FlashlightMode) = with(viewBinding) {
        val colorMain = ContextCompat.getColor(requireContext(), R.color.color_main)
        val colorUnselected = Color.parseColor("#C1C7D0")
        val iconRes = when (mode) {
            FlashlightMode.FLASH_LIGHT -> R.drawable.ic_flash_light
            FlashlightMode.SOS -> R.drawable.ic_sos
            FlashlightMode.DJ_MODE -> R.drawable.ic_dj_mode
        }
        imgPowerIcon.setImageResource(iconRes)

        if (isOn) {
            vPowerGlow.visibility = View.VISIBLE

            val glowRes = when (mode) {
                FlashlightMode.FLASH_LIGHT -> R.drawable.bg_power_glow_flashlight
                FlashlightMode.SOS -> R.drawable.bg_power_glow_sos
                FlashlightMode.DJ_MODE -> R.drawable.bg_power_glow_dj
            }
            vPowerGlow.setBackgroundResource(glowRes)

            val bgOnRes = when (mode) {
                FlashlightMode.FLASH_LIGHT -> R.drawable.bg_flashlight_button_on
                FlashlightMode.SOS -> R.drawable.bg_sos_button_on
                FlashlightMode.DJ_MODE -> R.drawable.bg_dj_mode_button_on
            }
            clPowerButton.setBackgroundResource(bgOnRes)
            ImageViewCompat.setImageTintList(imgPowerIcon, ColorStateList.valueOf(Color.WHITE))
            tvPowerState.text = getString(R.string.on_text)
            tvPowerState.setTextColor(Color.WHITE)
        } else {
            vPowerGlow.visibility = View.GONE
            clPowerButton.setBackgroundResource(R.drawable.bg_power_button_off)
            ImageViewCompat.setImageTintList(imgPowerIcon, ColorStateList.valueOf(colorUnselected))
            tvPowerState.text = getString(R.string.off_text)
            tvPowerState.setTextColor(Color.parseColor("#A0A0A0"))
        }

        val isFlash = mode == FlashlightMode.FLASH_LIGHT
        btnModeFlash.setBackgroundResource(if (isFlash) R.drawable.bg_mode_card_selected else R.drawable.bg_mode_card_unselected)
        ImageViewCompat.setImageTintList(imgModeFlash, ColorStateList.valueOf(if (isFlash) colorMain else colorUnselected))
        tvModeFlash.setTextColor(if (isFlash) colorMain else colorUnselected)

        val isSos = mode == FlashlightMode.SOS
        btnModeSos.setBackgroundResource(if (isSos) R.drawable.bg_mode_card_selected else R.drawable.bg_mode_card_unselected)
        ImageViewCompat.setImageTintList(imgModeSos, ColorStateList.valueOf(if (isSos) colorMain else colorUnselected))
        tvModeSos.setTextColor(if (isSos) colorMain else colorUnselected)

        val isDj = mode == FlashlightMode.DJ_MODE
        btnModeDj.setBackgroundResource(if (isDj) R.drawable.bg_mode_card_selected else R.drawable.bg_mode_card_unselected)
        ImageViewCompat.setImageTintList(imgModeDj, ColorStateList.valueOf(if (isDj) colorMain else colorUnselected))
        tvModeDj.setTextColor(if (isDj) colorMain else colorUnselected)
    }

    companion object {
        fun newInstance() = FlashlightFragment()
    }
}

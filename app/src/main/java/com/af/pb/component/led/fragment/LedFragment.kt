package com.af.pb.component.led.fragment

import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.af.pb.R
import com.af.pb.base.fragment.BaseFragment
import com.af.pb.component.led.dialog.LedFullScreenDialog
import com.af.pb.component.led.viewmodel.LedViewModel
import com.af.pb.component.screenlight.dialog.ColorPickerDialog
import com.af.pb.databinding.FragmentLedBinding
import com.af.pb.databinding.ItemLedBackgroundBinding
import com.af.pb.domain.model.LedBackgroundItem
import com.af.pb.domain.model.LedDirection
import com.af.pb.domain.model.LedState
import com.af.pb.domain.model.LedVisualEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LedFragment : BaseFragment<FragmentLedBinding>() {

    private val viewModel: LedViewModel by activityViewModels()
    private val backgroundAdapter = BackgroundAdapter()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.setBackground("custom", it.toString())
            }
        }

    private var isUpdatingFromCode = false

    override fun provideViewBinding(container: ViewGroup?): FragmentLedBinding {
        return FragmentLedBinding.inflate(LayoutInflater.from(context), container, false)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()

        // Text input watcher
        layoutLedText.etLedText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdatingFromCode) return
                viewModel.setText(s?.toString() ?: "")
            }
        })

        cardFontSize.setOnValueChangeListener { value, fromUser ->
            if (fromUser) {
                layoutLedPreview.vLedPreview.setLedFontSize(value)
                viewModel.setFontSize(value)
            }
        }

        cardScrollSpeed.setOnValueChangeListener { value, fromUser ->
            if (fromUser) {
                layoutLedPreview.vLedPreview.setLedScrollSpeed(value)
                viewModel.setScrollSpeed(value)
            }
        }

        // Reusable Color Picker Card
        val presetColors = listOf(
            0xFF38A8DF.toInt(), // Cyan
            0xFFFFFFFF.toInt(), // White
            0xFFFFD54F.toInt(), // Yellow
            0xFF81C784.toInt(), // Green
            0xFFBA68C8.toInt(), // Purple
            0xFFE57373.toInt()  // Coral / Red
        )
        cardColor.setPresetColors(presetColors)
        cardColor.setOnColorSelectedListener { color ->
            viewModel.setTextColor(color)
        }
        cardColor.setOnPaletteClickListener {
            val currentColor = viewModel.state.value.textColor
            ColorPickerDialog(requireContext(), currentColor) { selectedColor ->
                viewModel.setTextColor(selectedColor)
            }.show()
        }

        layoutLedDirection.btnDirectionRight.setOnClickListener { viewModel.setDirection(LedDirection.RIGHT) }
        layoutLedDirection.btnDirectionLeft.setOnClickListener { viewModel.setDirection(LedDirection.LEFT) }
        layoutLedDirection.btnDirectionDown.setOnClickListener { viewModel.setDirection(LedDirection.DOWN) }
        layoutLedDirection.btnDirectionUp.setOnClickListener { viewModel.setDirection(LedDirection.UP) }

        layoutLedVisualEffects.btnEffectGlow.setOnClickListener { viewModel.setVisualEffect(LedVisualEffect.GLOW) }
        layoutLedVisualEffects.btnEffectBlink.setOnClickListener { viewModel.setVisualEffect(LedVisualEffect.BLINK) }
        layoutLedVisualEffects.btnEffectNeon.setOnClickListener { viewModel.setVisualEffect(LedVisualEffect.NEON) }
        layoutLedVisualEffects.btnEffectFade.setOnClickListener { viewModel.setVisualEffect(LedVisualEffect.FADE) }

        layoutLedBackground.rvBackgrounds.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layoutLedBackground.rvBackgrounds.adapter = backgroundAdapter

        val openLedFullScreen = {
            val state = viewModel.state.value
            val resId = getPresetResId(state.selectedBackgroundId)
            LedFullScreenDialog.newInstance(state, resId)
                .show(parentFragmentManager, "LedFullScreenDialog")
        }

        layoutLedPreview.btnFullscreen.setOnClickListener {
            openLedFullScreen()
        }

        layoutLedPreview.vLedPreview.setOnClickListener {
            openLedFullScreen()
        }
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.state.onEach { state ->
            updateUi(state)
        }.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun updateUi(state: LedState) = with(viewBinding) {
        // Preview View update
        layoutLedPreview.vLedPreview.setLedText(state.text)
        layoutLedPreview.vLedPreview.setLedFontSize(state.fontSize)
        layoutLedPreview.vLedPreview.setLedScrollSpeed(state.scrollSpeed)
        layoutLedPreview.vLedPreview.setLedTextColor(state.textColor)
        layoutLedPreview.vLedPreview.setLedDirection(state.direction)
        layoutLedPreview.vLedPreview.setLedVisualEffect(state.visualEffect)

        if (!state.customBackgroundUri.isNullOrEmpty() && state.selectedBackgroundId == "custom") {
            layoutLedPreview.vLedPreview.setLedBackgroundUri(state.customBackgroundUri)
        } else if (state.selectedBackgroundId.startsWith("background/")) {
            layoutLedPreview.vLedPreview.setLedBackgroundAsset(state.selectedBackgroundId)
        } else {
            val resId = getPresetResId(state.selectedBackgroundId)
            layoutLedPreview.vLedPreview.setLedBackgroundRes(resId)
        }

        if (layoutLedText.etLedText.text.toString() != state.text && !layoutLedText.etLedText.isFocused) {
            isUpdatingFromCode = true
            layoutLedText.etLedText.setText(state.text)
            isUpdatingFromCode = false
        }

        cardFontSize.setValue(state.fontSize)
        cardScrollSpeed.setValue(state.scrollSpeed)

         cardColor.setSelectedColor(state.textColor)

             updateDirectionButtons(state.direction)

            updateVisualEffectsButtons(state.visualEffect)

          updateBackgroundList(state)
    }

    private fun updateDirectionButtons(direction: LedDirection) = with(viewBinding.layoutLedDirection) {
        val colorMain = ContextCompat.getColor(requireContext(), R.color.color_main)
        val colorUnselected = ContextCompat.getColor(requireContext(), R.color.gray)

        val isRight = direction == LedDirection.RIGHT
        btnDirectionRight.setBackgroundResource(if (isRight) R.drawable.bg_direction_selected else R.drawable.bg_direction_unselected)
        ImageViewCompat.setImageTintList(btnDirectionRight, ColorStateList.valueOf(if (isRight) colorMain else colorUnselected))

        val isLeft = direction == LedDirection.LEFT
        btnDirectionLeft.setBackgroundResource(if (isLeft) R.drawable.bg_direction_selected else R.drawable.bg_direction_unselected)
        ImageViewCompat.setImageTintList(btnDirectionLeft, ColorStateList.valueOf(if (isLeft) colorMain else colorUnselected))

        val isDown = direction == LedDirection.DOWN
        btnDirectionDown.setBackgroundResource(if (isDown) R.drawable.bg_direction_selected else R.drawable.bg_direction_unselected)
        ImageViewCompat.setImageTintList(btnDirectionDown, ColorStateList.valueOf(if (isDown) colorMain else colorUnselected))

        val isUp = direction == LedDirection.UP
        btnDirectionUp.setBackgroundResource(if (isUp) R.drawable.bg_direction_selected else R.drawable.bg_direction_unselected)
        ImageViewCompat.setImageTintList(btnDirectionUp, ColorStateList.valueOf(if (isUp) colorMain else colorUnselected))
    }

    private fun updateVisualEffectsButtons(effect: LedVisualEffect) = with(viewBinding.layoutLedVisualEffects) {
        val colorMain = ContextCompat.getColor(requireContext(), R.color.color_main)
        val colorUnselected = ContextCompat.getColor(requireContext(), R.color.white)

        val isGlow = effect == LedVisualEffect.GLOW
        btnEffectGlow.isSelected = isGlow
        btnEffectGlow.setTextColor(if (isGlow) colorMain else colorUnselected)

        val isBlink = effect == LedVisualEffect.BLINK
        btnEffectBlink.isSelected = isBlink
        btnEffectBlink.setTextColor(if (isBlink) colorMain else colorUnselected)

        val isNeon = effect == LedVisualEffect.NEON
        btnEffectNeon.isSelected = isNeon
        btnEffectNeon.setTextColor(if (isNeon) colorMain else colorUnselected)

        val isFade = effect == LedVisualEffect.FADE
        btnEffectFade.isSelected = isFade
        btnEffectFade.setTextColor(if (isFade) colorMain else colorUnselected)
    }

    private fun updateBackgroundList(state: LedState) {
        val items = mutableListOf<LedBackgroundItem>()
        items.add(LedBackgroundItem("add", isAddButton = true))

        try {
            val assetFiles = requireContext().assets.list("background")?.sortedWith { a, b ->
                val numA = a.substringAfter("_").substringBefore(".").toIntOrNull() ?: Int.MAX_VALUE
                val numB = b.substringAfter("_").substringBefore(".").toIntOrNull() ?: Int.MAX_VALUE
                numA.compareTo(numB)
            } ?: emptyList()

            assetFiles.forEach { fileName ->
                val assetPath = "background/$fileName"
                items.add(LedBackgroundItem(id = assetPath, assetPath = assetPath))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (!state.customBackgroundUri.isNullOrEmpty()) {
            items.add(LedBackgroundItem("custom", uriString = state.customBackgroundUri))
        }

        backgroundAdapter.submitList(items, state.selectedBackgroundId)

        if (!state.customBackgroundUri.isNullOrEmpty() && state.selectedBackgroundId == "custom") {
            try {
                val uri = Uri.parse(state.customBackgroundUri)
                val input = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(input)
                input?.close()
            } catch (_: Exception) {
            }
        } else if (state.selectedBackgroundId.startsWith("background/")) {
            try {
                val input = requireContext().assets.open(state.selectedBackgroundId)
                val bitmap = BitmapFactory.decodeStream(input)
                input.close()

            } catch (_: Exception) {

            }
        } else {
            val resId = getPresetResId(state.selectedBackgroundId)
            if (resId != null) {

            } else {

            }
        }
    }

    private fun getPresetResId(bgId: String): Int? {
        return when (bgId) {
            "preset_1" -> R.drawable.bg_led_preset_1
            "preset_2" -> R.drawable.bg_led_preset_2
            "preset_3" -> R.drawable.bg_led_preset_3
            else -> null
        }
    }

    private inner class BackgroundAdapter :
        RecyclerView.Adapter<BackgroundAdapter.BackgroundViewHolder>() {

        private var items = listOf<LedBackgroundItem>()
        private var selectedId: String = "background/bg_1.jpg"

        fun submitList(newItems: List<LedBackgroundItem>, selectedId: String) {
            this.items = newItems
            this.selectedId = selectedId
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackgroundViewHolder {
            val binding =
                ItemLedBackgroundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return BackgroundViewHolder(binding)
        }

        override fun onBindViewHolder(holder: BackgroundViewHolder, position: Int) {
            holder.bind(items[position], selectedId)
        }

        override fun getItemCount(): Int = items.size

        private inner class BackgroundViewHolder(
            private val itemBinding: ItemLedBackgroundBinding
        ) : RecyclerView.ViewHolder(itemBinding.root) {

            fun bind(item: LedBackgroundItem, selectedId: String) {
                val isSelected = item.id == selectedId

                if (item.isAddButton) {
                    itemBinding.root.setBackgroundResource(R.drawable.bg_add_background_button)
                    itemBinding.imgBackground.setImageDrawable(null)
                    itemBinding.imgBackground.background = null
                    itemBinding.imgAddIcon.visibility = View.VISIBLE
                    itemBinding.vSelectionBorder.visibility = View.GONE
                    itemBinding.root.setOnClickListener {
                        pickImageLauncher.launch("image/*")
                    }
                } else {
                    itemBinding.root.background = null
                    itemBinding.imgBackground.background = null
                    itemBinding.imgAddIcon.visibility = View.GONE
                    itemBinding.vSelectionBorder.visibility =
                        if (isSelected) View.VISIBLE else View.GONE

                    if (item.assetPath != null) {
                        try {
                            val input = itemBinding.root.context.assets.open(item.assetPath)
                            val bitmap = BitmapFactory.decodeStream(input)
                            input.close()
                            itemBinding.imgBackground.setImageBitmap(bitmap)
                        } catch (_: Exception) {
                            itemBinding.imgBackground.setImageDrawable(null)
                        }
                    } else if (item.resId != null) {
                        itemBinding.imgBackground.setImageResource(item.resId)
                    } else if (!item.uriString.isNullOrEmpty()) {
                        try {
                            val uri = Uri.parse(item.uriString)
                            val input = itemBinding.root.context.contentResolver.openInputStream(uri)
                            val bitmap = BitmapFactory.decodeStream(input)
                            input?.close()
                            itemBinding.imgBackground.setImageBitmap(bitmap)
                        } catch (_: Exception) {
                            itemBinding.imgBackground.setImageDrawable(null)
                        }
                    }

                    itemBinding.root.setOnClickListener {
                        viewModel.setBackground(item.id, item.uriString)
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance() = LedFragment()
    }
}

package com.af.pb.component.common.view

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.af.pb.databinding.ItemColorPaletteBinding
import com.af.pb.databinding.ItemColorPresetBinding
import com.af.pb.databinding.ViewColorPickerCardBinding

class ColorPickerCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val binding: ViewColorPickerCardBinding =
        ViewColorPickerCardBinding.inflate(LayoutInflater.from(context), this, true)

    private var presetColors: List<Int> = emptyList()
    private var selectedColor: Int = 0
    private var onColorSelectedListener: ((Int) -> Unit)? = null
    private var onPaletteClickListener: (() -> Unit)? = null

    private val adapter = ColorPresetAdapter()

    init {
        val spanCount = 5
        val gridLayoutManager = GridLayoutManager(context, spanCount)
        binding.rvColors.layoutManager = gridLayoutManager
        binding.rvColors.adapter = adapter
        binding.rvColors.addItemDecoration(GridSpacingItemDecoration(spanCount, 14))
    }

    fun setPresetColors(colors: List<Int>) {
        this.presetColors = colors
        adapter.notifyDataSetChanged()
    }

    fun setSelectedColor(color: Int) {
        this.selectedColor = color
        adapter.notifyDataSetChanged()
    }

    fun setOnColorSelectedListener(listener: (Int) -> Unit) {
        this.onColorSelectedListener = listener
    }

    fun setOnPaletteClickListener(listener: () -> Unit) {
        this.onPaletteClickListener = listener
    }

    fun setTitle(title: String) {
        binding.tvColorLabel.text = title
    }

    fun setTitleRes(titleRes: Int) {
        binding.tvColorLabel.setText(titleRes)
    }

    private inner class ColorPresetAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val TYPE_PALETTE = 0
        private val TYPE_PRESET = 1

        override fun getItemCount(): Int = 1 + presetColors.size

        override fun getItemViewType(position: Int): Int {
            return if (position == 0) TYPE_PALETTE else TYPE_PRESET
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == TYPE_PALETTE) {
                val itemBinding = ItemColorPaletteBinding.inflate(inflater, parent, false)
                PaletteViewHolder(itemBinding)
            } else {
                val itemBinding = ItemColorPresetBinding.inflate(inflater, parent, false)
                PresetViewHolder(itemBinding)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is PaletteViewHolder) {
                holder.bind()
            } else if (holder is PresetViewHolder) {
                val color = presetColors[position - 1]
                val isSelected = isColorEqual(color, selectedColor)
                holder.bind(color, isSelected)
            }
        }
    }

    private inner class PaletteViewHolder(
        private val itemBinding: ItemColorPaletteBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind() {
            val listener = OnClickListener {
                onPaletteClickListener?.invoke()
            }
            itemBinding.root.setOnClickListener(listener)
            itemBinding.btnPalette.setOnClickListener(listener)
        }
    }

    private inner class PresetViewHolder(
        private val itemBinding: ItemColorPresetBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(color: Int, isSelected: Boolean) {
            itemBinding.imgColor.setImageDrawable(ColorDrawable(color))
            itemBinding.vSelectionRing.visibility = if (isSelected) View.VISIBLE else View.GONE
            val listener = OnClickListener {
                onColorSelectedListener?.invoke(color)
            }
            itemBinding.root.setOnClickListener(listener)
            itemBinding.imgColor.setOnClickListener(listener)
        }
    }

    private fun isColorEqual(color1: Int, color2: Int): Boolean {
        return (color1 and 0x00FFFFFF) == (color2 and 0x00FFFFFF)
    }

    private class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val verticalSpacingDp: Int
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position >= spanCount) {
                val density = parent.context.resources.displayMetrics.density
                outRect.top = (verticalSpacingDp * density).toInt()
            }
        }
    }
}

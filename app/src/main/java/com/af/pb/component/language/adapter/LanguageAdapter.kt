package com.af.pb.component.language.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.af.pb.R
import com.af.pb.base.adapter.BaseAdapter
import com.af.pb.data.model.Language
import com.af.pb.databinding.ItemLanguageBinding
import com.bumptech.glide.Glide

class LanguageAdapter : BaseAdapter<Language, ItemLanguageBinding>() {
    private var isFromSplash = false

    fun setData(listItem: ArrayList<Language>, isFromSplash: Boolean) {
        this.isFromSplash = isFromSplash
        super.setData(listItem)
    }

    override fun provideViewBinding(parent: ViewGroup): ItemLanguageBinding {
        return ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun binData(viewBinding: ItemLanguageBinding, item: Language, position: Int) {
        viewBinding.apply {
            tvTitle.setText(item.nameRes)

            val flagFile = if (item.flagName.isNotEmpty()) item.flagName else "${item.languageCode}.png"
            Glide.with(root.context)
                .load("file:///android_asset/$flagFile")
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imgFlag)

            if (item.selected) {
                tvTitle.setTextColor(ContextCompat.getColor(root.context, R.color.yellow))
                imgCheckbox.setImageResource(R.drawable.ic_checkbox_checked)
                linearLayout5.isSelected = true
            } else {
                tvTitle.setTextColor(ContextCompat.getColor(root.context, R.color.white))
                imgCheckbox.setImageResource(R.drawable.ic_checkbox_normal)
                linearLayout5.isSelected = false
            }
            root.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }

    fun selectLanguage(languageCode: String) {
        var index = dataSet.indexOfFirst { it.selected }
        if (index > -1) {
            dataSet[index].selected = false
            notifyItemChanged(index)
        }
        index = dataSet.indexOfFirst { it.languageCode == languageCode }
        if (index > -1) {
            dataSet[index].selected = true
            notifyItemChanged(index)
        }
    }

    fun selectedLanguage(): Language? = dataSet.firstOrNull { it.selected }
}

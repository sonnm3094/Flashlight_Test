package com.af.pb.component.flashalert.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.af.pb.R
import com.af.pb.base.adapter.BaseAdapter
import com.af.pb.databinding.ItemSelectAppBinding
import com.af.pb.domain.model.AppInfo

class SelectAppAdapter : BaseAdapter<AppInfo, ItemSelectAppBinding>() {

    override fun provideViewBinding(parent: ViewGroup): ItemSelectAppBinding {
        return ItemSelectAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun binData(viewBinding: ItemSelectAppBinding, item: AppInfo, position: Int) {
        viewBinding.apply {
            tvAppName.text = item.appName

            try {
                val icon = root.context.packageManager.getApplicationIcon(item.packageName)
                imgAppIcon.setImageDrawable(icon)
            } catch (_: Exception) {
                imgAppIcon.setImageResource(R.mipmap.ic_launcher)
            }

            if (item.isSelected) {
                imgCheckbox.setImageResource(R.drawable.ic_checkbox_checked)
            } else {
                imgCheckbox.setImageResource(R.drawable.ic_checkbox_normal)
            }

            root.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }
}

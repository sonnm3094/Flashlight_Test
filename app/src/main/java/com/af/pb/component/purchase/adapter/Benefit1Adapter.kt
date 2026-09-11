package com.af.pb.component.purchase.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.af.pb.base.adapter.BaseAdapter
import com.af.pb.data.model.BenefitModel
import com.af.pb.databinding.ItemBenefit1Binding

class Benefit1Adapter : BaseAdapter<BenefitModel, ItemBenefit1Binding>() {

    override fun binData(viewBinding: ItemBenefit1Binding, item: BenefitModel, position: Int) {
        viewBinding.apply {
            tvTitle.text = root.context.getString(item.title)
            imgIcon.setImageResource(item.icon)

        }
    }

    override fun provideViewBinding(parent: ViewGroup): ItemBenefit1Binding = ItemBenefit1Binding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
}

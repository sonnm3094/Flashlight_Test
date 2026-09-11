package com.af.pb.component.purchase.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.af.pb.R
import com.af.pb.base.adapter.BaseAdapter
import com.af.pb.data.model.PurchaseModel
import com.af.pb.databinding.ItemPurchase1Binding

class Purchase1Adapter : BaseAdapter<PurchaseModel, ItemPurchase1Binding>() {

    override fun binData(viewBinding: ItemPurchase1Binding, item: PurchaseModel, position: Int) {
        viewBinding.apply {
            tvPack.text = root.context.getString(item.pack)
            tvPrice.text = "${item.price}\n/${root.context.getString(item.period)}"
            tvStatus.text = root.context.getString(item.textStatus)
            if (item.isSelected) {
                mcvItem.setCardBackgroundColor("#D9CEFF".toColorInt())
                mcvItem.strokeColor = "#D9CEFF".toColorInt()
                tvPack.setTextColor(ContextCompat.getColor(root.context, R.color.black))
                tvPrice.setTextColor(ContextCompat.getColor(root.context, R.color.black))
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.white))
                tvStatus.setBackgroundColor(ContextCompat.getColor(root.context, R.color.purple2))
            } else {
                mcvItem.setCardBackgroundColor("#121212".toColorInt())
                mcvItem.strokeColor = "#00000000".toColorInt()
                tvPack.setTextColor(ContextCompat.getColor(root.context, R.color.white))
                tvPrice.setTextColor(ContextCompat.getColor(root.context, R.color.gray4))
                tvStatus.setBackgroundColor(ContextCompat.getColor(root.context, R.color.transparent))
                if (item.isMostPopular) {
                    tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.purple))
                } else if (item.isBestValue) {
                    tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.yellow))
                } else {
                    tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.white))
                }
            }

            tvPack.isSelected = true
            tvStatus.isSelected = true

            root.setOnClickListener {
                dataSet.forEach { it.isSelected = false }
                item.isSelected = true
                tvPack.isSelected = true
                notifyDataSetChanged()
            }
        }
    }

    override fun provideViewBinding(parent: ViewGroup): ItemPurchase1Binding = ItemPurchase1Binding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
}

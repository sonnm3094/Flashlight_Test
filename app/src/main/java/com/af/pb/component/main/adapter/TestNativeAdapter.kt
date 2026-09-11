package com.af.pb.component.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.af.pb.base.adapter.BaseAdapter
import com.af.pb.databinding.ItemTestNativeAdapterBinding

class TestNativeAdapter : BaseAdapter<Int, ItemTestNativeAdapterBinding>() {

    override fun initData(): ArrayList<Int> {
        return ArrayList((1..30).toList())
    }

    override fun provideViewBinding(parent: ViewGroup): ItemTestNativeAdapterBinding {
        return ItemTestNativeAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun binData(viewBinding: ItemTestNativeAdapterBinding, item: Int, position: Int) {
        viewBinding.tvTest.text = item.toString()
    }
}

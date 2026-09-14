package com.af.pb.component.onboarding.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import com.af.pb.base.adapter.BaseAdapter
import com.af.pb.data.model.OnBoarding
import com.af.pb.databinding.ItemOnBoardingBinding

class OnBoardingAdapter : BaseAdapter<OnBoarding, ItemOnBoardingBinding>() {

    override fun binData(viewBinding: ItemOnBoardingBinding, item: OnBoarding, position: Int) {
        viewBinding.apply {
            tvTitle.text = root.context.resources.getString(item.title)
            tvDescription.text = root.context.resources.getString(item.description)
            imgBoarding.setImageResource(item.imageId)
        }
    }

    override fun provideViewBinding(parent: ViewGroup): ItemOnBoardingBinding = ItemOnBoardingBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
}
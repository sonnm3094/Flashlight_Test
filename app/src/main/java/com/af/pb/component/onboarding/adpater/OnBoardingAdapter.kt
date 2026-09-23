package com.af.pb.component.onboarding.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.af.pb.base.adapter.BaseAdapter
import com.af.pb.data.model.OnBoarding
import com.af.pb.databinding.ItemOnBoarding1Binding
import com.af.pb.databinding.ItemOnBoarding2Binding

class OnBoardingAdapter : BaseAdapter<OnBoarding, ViewBinding>() {

    override fun getItemViewType(position: Int): Int {
        return dataSet[position].type
    }

    override fun provideViewBinding(parent: ViewGroup): ViewBinding {
        return ItemOnBoarding1Binding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewBinding = if (viewType == OnBoarding.TYPE_2) {
            ItemOnBoarding2Binding.inflate(inflater, parent, false)
        } else {
            ItemOnBoarding1Binding.inflate(inflater, parent, false)
        }
        return BaseViewHolder(binding)
    }

    override fun binData(viewBinding: ViewBinding, item: OnBoarding, position: Int) {
        val context = viewBinding.root.context
        when (viewBinding) {
            is ItemOnBoarding1Binding -> {
                viewBinding.imgBoarding.setImageResource(item.imageId)
                viewBinding.tvTitle.text = context.getString(item.title)
                viewBinding.tvDescription.text = context.getString(item.description)
            }
            is ItemOnBoarding2Binding -> {
                viewBinding.imgBoarding.setImageResource(item.imageId)
                viewBinding.tvTitle.text = context.getString(item.title)
                viewBinding.tvDescription.text = context.getString(item.description)
            }
        }
    }
}
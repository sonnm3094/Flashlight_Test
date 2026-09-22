package com.af.pb.component.onboarding.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.af.pb.R
import com.af.pb.base.adapter.BaseAdapter
import com.af.pb.data.model.OnBoarding
import com.af.pb.databinding.ItemOnBoarding1Binding
import com.af.pb.databinding.ItemOnBoarding2Binding

class OnBoardingAdapter(
    var onNextClick: (() -> Unit)? = null
) : BaseAdapter<OnBoarding, ViewBinding>() {

    var viewPager: ViewPager2? = null

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
        val isLast = position == dataSet.size - 1
        val btnTextRes = if (isLast) R.string.get_started else R.string.next

        when (viewBinding) {
            is ItemOnBoarding1Binding -> {
                viewBinding.tvTitle.text = viewBinding.root.context.resources.getString(item.title)
                viewBinding.tvDescription.text = viewBinding.root.context.resources.getString(item.description)
                viewBinding.imgBoarding.setImageResource(item.imageId)
                viewBinding.btnNext.setText(btnTextRes)
                viewBinding.btnNext.setOnClickListener { onNextClick?.invoke() }
                viewPager?.let { viewBinding.dotsIndicator.attachTo(it) }
            }
            is ItemOnBoarding2Binding -> {
                viewBinding.tvTitle.text = viewBinding.root.context.resources.getString(item.title)
                viewBinding.tvDescription.text = viewBinding.root.context.resources.getString(item.description)
                viewBinding.imgBoarding.setImageResource(item.imageId)
                viewBinding.btnNext.setText(btnTextRes)
                viewBinding.btnNext.setOnClickListener { onNextClick?.invoke() }
                viewPager?.let { viewBinding.dotsIndicator.attachTo(it) }
            }
        }
    }
}

package com.af.pb.component.onboarding.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.af.pb.R
import com.af.pb.base.activity.BaseActivity
import com.af.pb.component.main.activity.MainActivity
import com.af.pb.component.onboarding.adpater.OnBoardingAdapter
import com.af.pb.component.onboarding.viewmodel.OnBoardingViewModel
import com.af.pb.databinding.ActivityOnBoardingBinding
import com.af.pb.utils.SpManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding>() {

    @Inject
    lateinit var spManager: SpManager

    private val onBoardingAdapter = OnBoardingAdapter()
    private val viewModels: OnBoardingViewModel by viewModels()

    override fun provideViewBinding(): ActivityOnBoardingBinding = ActivityOnBoardingBinding.inflate(layoutInflater)

    override fun initViews() = with(viewBinding) {
        super.initViews()

        vpOnBoarding.adapter = onBoardingAdapter
        dotsIndicator.attachTo(vpOnBoarding)

        btnNext.setOnClickListener {
            nextAction()
        }

        vpOnBoarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModels.currentPosition = position
                updateNextButtonText(position)
            }
        })
    }

    private fun updateNextButtonText(position: Int) {
        val isLast = position == onBoardingAdapter.dataSet.size - 1
        val btnTextRes = if (isLast) R.string.get_started else R.string.next
        viewBinding.btnNext.setText(btnTextRes)
    }

    private fun nextAction() {
        if (viewModels.currentPosition < onBoardingAdapter.dataSet.size - 1) {
            viewBinding.vpOnBoarding.setCurrentItem(viewModels.currentPosition + 1, true)
        } else {
            MainActivity.start(this@OnBoardingActivity)
            finish()
        }
    }

    override fun initData() {
        super.initData()
        viewModels.getListOnBoarding()
    }

    override fun initObserver() {
        super.initObserver()
        viewModels.listOnBoarding.onEach {
            onBoardingAdapter.setData(ArrayList(it))
            updateNextButtonText(viewModels.currentPosition)
        }.flowWithLifecycle(lifecycle, Lifecycle.State.CREATED).launchIn(lifecycleScope)
    }

    companion object {
        fun start(context: Context) {
            Intent(context, OnBoardingActivity::class.java).also {
                context.startActivity(it)
            }
        }
    }
}
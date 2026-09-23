package com.af.pb.component.main.activity

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.af.pb.R
import com.af.pb.base.activity.BaseActivity
import com.af.pb.component.flashalert.fragment.FlashAlertFragment
import com.af.pb.component.flashlight.fragment.FlashlightFragment
import com.af.pb.component.flashlight.viewmodel.FlashlightViewModel
import com.af.pb.component.led.fragment.LedFragment
import com.af.pb.component.screenlight.fragment.ScreenLightFragment
import com.af.pb.databinding.ActivityMainBinding
import com.af.pb.databinding.LayoutBottomNavigationBinding
import com.af.pb.domain.model.BottomTab
import com.af.pb.utils.SpManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var spManager: SpManager

    private val viewModel: FlashlightViewModel by viewModels()

    override fun provideViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initViews() {
        super.initViews()
        spManager.setLanguageChosen()

        viewBinding.layoutBottomNav.btnTabFlashlight.setOnClickListener {
            viewModel.selectTab(BottomTab.FLASHLIGHT)
        }

        viewBinding.layoutBottomNav.btnTabScreenlight.setOnClickListener {
            viewModel.selectTab(BottomTab.SCREENLIGHT)
        }

        viewBinding.layoutBottomNav.btnTabLed.setOnClickListener {
            viewModel.selectTab(BottomTab.LED)
        }

        viewBinding.layoutBottomNav.btnTabFlashAlert.setOnClickListener {
            viewModel.selectTab(BottomTab.FLASH_ALERT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            displayTabFragment(FlashlightFragment.newInstance())
        }
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.selectedTab.onEach { tab ->
            updateBottomNavUi(tab)
        }.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .launchIn(lifecycleScope)
    }

    private fun updateBottomNavUi(selectedTab: BottomTab) {
        val bottomNavBinding: LayoutBottomNavigationBinding = viewBinding.layoutBottomNav
        val colorMain = ContextCompat.getColor(this, R.color.color_main)
        val colorUnselected = Color.parseColor("#80FFFFFF")
        val transparent = ContextCompat.getColor(this, R.color.transparent)

        with(bottomNavBinding) {
            btnTabFlashlight.setBackgroundColor(transparent)
            btnTabScreenlight.setBackgroundColor(transparent)
            btnTabLed.setBackgroundColor(transparent)
            btnTabFlashAlert.setBackgroundColor(transparent)

            vGlowFlashlight.visibility = View.GONE
            vGlowScreenlight.visibility = View.GONE
            vGlowLed.visibility = View.GONE
            vGlowFlashAlert.visibility = View.GONE

            ImageViewCompat.setImageTintList(imgTabFlashlight, ColorStateList.valueOf(colorUnselected))
            ImageViewCompat.setImageTintList(imgTabScreenlight, ColorStateList.valueOf(colorUnselected))
            ImageViewCompat.setImageTintList(imgTabLed, ColorStateList.valueOf(colorUnselected))
            ImageViewCompat.setImageTintList(imgTabFlashAlert, ColorStateList.valueOf(colorUnselected))

            tvTabFlashlight.setTextColor(colorUnselected)
            tvTabScreenlight.setTextColor(colorUnselected)
            tvTabLed.setTextColor(colorUnselected)
            tvTabFlashAlert.setTextColor(colorUnselected)

            when (selectedTab) {
                BottomTab.FLASHLIGHT -> {
                    vGlowFlashlight.visibility = View.VISIBLE
                    btnTabFlashlight.setBackgroundResource(R.drawable.bg_bottom_nav_selected)
                    ImageViewCompat.setImageTintList(imgTabFlashlight, ColorStateList.valueOf(colorMain))
                    tvTabFlashlight.setTextColor(colorMain)
                    displayTabFragment(FlashlightFragment.newInstance())
                }
                BottomTab.SCREENLIGHT -> {
                    vGlowScreenlight.visibility = View.VISIBLE
                    btnTabScreenlight.setBackgroundResource(R.drawable.bg_bottom_nav_selected)
                    ImageViewCompat.setImageTintList(imgTabScreenlight, ColorStateList.valueOf(colorMain))
                    tvTabScreenlight.setTextColor(colorMain)
                    displayTabFragment(ScreenLightFragment.newInstance())
                }
                BottomTab.LED -> {
                    vGlowLed.visibility = View.VISIBLE
                    btnTabLed.setBackgroundResource(R.drawable.bg_bottom_nav_selected)
                    ImageViewCompat.setImageTintList(imgTabLed, ColorStateList.valueOf(colorMain))
                    tvTabLed.setTextColor(colorMain)
                    displayTabFragment(LedFragment.newInstance())
                }
                BottomTab.FLASH_ALERT -> {
                    vGlowFlashAlert.visibility = View.VISIBLE
                    btnTabFlashAlert.setBackgroundResource(R.drawable.bg_bottom_nav_selected)
                    ImageViewCompat.setImageTintList(imgTabFlashAlert, ColorStateList.valueOf(colorMain))
                    tvTabFlashAlert.setTextColor(colorMain)
                    displayTabFragment(FlashAlertFragment.newInstance())
                }
            }
        }
    }

    private fun displayTabFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    companion object {
        fun startNewTask(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity.startActivity(intent)
        }

        fun start(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
    }
}

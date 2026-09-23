package com.af.pb.component.flashalert.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.af.pb.R
import com.af.pb.base.activity.BaseActivity
import com.af.pb.component.flashalert.adapter.SelectAppAdapter
import com.af.pb.component.flashalert.viewmodel.SelectAppViewModel
import com.af.pb.databinding.ActivitySelectAppBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SelectAppActivity : BaseActivity<ActivitySelectAppBinding>() {

    private val viewModel: SelectAppViewModel by viewModels()
    private val adapter = SelectAppAdapter()

    override fun provideViewBinding(): ActivitySelectAppBinding {
        return ActivitySelectAppBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        super.initViews()

        viewBinding.btnBack.setOnClickListener {
            viewModel.saveSelection(this@SelectAppActivity)
            onBack()
        }

        viewBinding.tvSelectAll.setOnClickListener {
            viewModel.toggleSelectAll(this@SelectAppActivity)
        }

        viewBinding.rcvApps.adapter = adapter
        adapter.onClick = { appInfo ->
            viewModel.toggleAppSelection(appInfo)
            viewModel.saveSelection(this@SelectAppActivity)
        }
    }

    override fun initData() {
        viewModel.loadInstalledApps(this)
    }

    override fun initObserver() {
        super.initObserver()

        viewModel.appList.onEach { list ->
            adapter.setData(ArrayList(list))
        }.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).launchIn(lifecycleScope)

        viewModel.isLoading.onEach { isLoading ->
            viewBinding.progressBar.isVisible = isLoading
        }.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).launchIn(lifecycleScope)

        viewModel.isAllSelected.onEach { isAllSelected ->
            viewBinding.tvSelectAll.text = if (isAllSelected) getString(R.string.deselect_all) else getString(R.string.select_all)
        }.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).launchIn(lifecycleScope)
    }

    override fun onBack() {
        viewModel.saveSelection(this)
        super.onBack()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SelectAppActivity::class.java)
            context.startActivity(intent)
        }
    }
}

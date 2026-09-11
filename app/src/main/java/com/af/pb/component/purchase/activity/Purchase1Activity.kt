package com.af.pb.component.purchase.activity

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.ads.admob.billing.factory.IapFactory
import com.ads.admob.billing.factory.IapType
import com.ads.admob.listener.PurchaseServiceListener
import com.ads.admob.widget.DataWrappers
import com.af.pb.ads.InterAdsUtils.showInterCancelPaywall
import com.af.pb.base.activity.BaseActivity
import com.af.pb.component.main.activity.MainActivity
import com.af.pb.component.purchase.adapter.Benefit1Adapter
import com.af.pb.component.purchase.adapter.Purchase1Adapter
import com.af.pb.component.purchase.viewmodel.PurchaseViewModel
import com.af.pb.data.model.PurchaseModel
import com.af.pb.databinding.ActivityPurchase1Binding
import com.af.pb.utils.Constant
import com.af.pb.utils.Logger
import com.af.pb.utils.openBrowser
import com.af.pb.utils.setGradientText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Purchase1Activity : BaseActivity<ActivityPurchase1Binding>() {

    private val viewModel: PurchaseViewModel by viewModels()
    private val purchaseAdapter = Purchase1Adapter()
    private val benefitAdapter = Benefit1Adapter()

    private val purchaseServiceListener = object : PurchaseServiceListener {
        override fun onPricesUpdated(iapKeyPrices: Map<String, List<DataWrappers.ProductDetails>>) {
            Logger.e("onPricesUpdated: ${IapFactory.getInstance().isProductPurchased()}")
        }

        override fun onProductPurchased(purchaseInfo: DataWrappers.PurchaseInfo?) {
            Logger.e("onProductPurchased : ${IapFactory.getInstance().isProductPurchased()}")
            MainActivity.startNewTask(this@Purchase1Activity)
            finish()
        }

        override fun onProductRestored(purchaseInfo: DataWrappers.PurchaseInfo?) {
            Logger.e("onProductRestored: ")
            finish()
        }

        override fun onPurchaseFailed(
            purchaseInfo: DataWrappers.PurchaseInfo?,
            billingResponseCode: Int?
        ) {
            Logger.e("onPurchaseFailed: $billingResponseCode")
        }
    }

    override fun provideViewBinding(): ActivityPurchase1Binding = ActivityPurchase1Binding.inflate(layoutInflater)

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setupBlurView()

        rcvPurchase.adapter = purchaseAdapter
        rcvVipBenefits.adapter = benefitAdapter

        tvTitle.setGradientText(
            "#FFBDE8".toColorInt(),
            "#FFFFFF".toColorInt()
        )

        listenerBilling()

        tvTermOfUse.setOnClickListener {
            openBrowser(Constant.LINK_TERM)
        }
        tvPrivacyPolicy.setOnClickListener {
            openBrowser(Constant.LINK_POLICY)
        }

        btnClose.setOnClickListener {
            showInterCancelPaywall(this@Purchase1Activity, this@Purchase1Activity) {
                actionClose()
            }
        }

        btnVipAccess.setOnClickListener {
            val currentPurchase = purchaseAdapter.getListData().find { it.isSelected }
            currentPurchase?.let { purchase -> actionPurchase(purchase) }
        }
    }

    private fun setupBlurView() {
        val decorView = window.decorView
        val rootView = decorView.findViewById<View>(android.R.id.content)
        viewBinding.blurView.setupWith(rootView as ViewGroup)
            .setFrameClearDrawable(window.decorView.background)
            .setBlurRadius(2f)
            .setBlurAutoUpdate(true)
    }

    private fun initPrice() {
        val weeklyPrice =
            IapFactory.getInstance().getPriceById(Constant.WEEKLY_IAP, IapType.SUBSCRIPTION)
        val monthlyPrice =
            IapFactory.getInstance().getPriceById(Constant.MONTHLY_IAP, IapType.SUBSCRIPTION)
        val yearlyPrice =
            IapFactory.getInstance().getPriceById(Constant.YEARLY_IAP, IapType.SUBSCRIPTION)
        val items = purchaseAdapter.getListData()
        if (items.size >= 3) {
            monthlyPrice?.let { items[0].price = it }
            weeklyPrice?.let { items[1].price = it }
            yearlyPrice?.let { items[2].price = it }
            purchaseAdapter.notifyDataSetChanged()
        }
    }

    private fun actionClose() {
        finish()
    }

    override fun onBack() {}

    private fun actionPurchase(purchase: PurchaseModel) {
        IapFactory.getInstance().buySubscription(this, purchase.id)
    }

    private fun listenerBilling() {
        IapFactory.getInstance().registerPurchaseServiceListener(purchaseServiceListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        IapFactory.getInstance().unregisterPurchaseServiceListener(purchaseServiceListener)
    }

    override fun initObserver() {
        lifecycleScope.launch {
            viewModel.purchases.collect { list ->
                purchaseAdapter.setData(ArrayList(list))
                initPrice()
            }
        }
        lifecycleScope.launch {
            viewModel.benefits.collect { list ->
                benefitAdapter.setData(ArrayList(list))
            }
        }
    }

    companion object {
        fun start(context: Context) {
            Intent(context, Purchase1Activity::class.java).also {
                context.startActivity(it)
            }
        }
    }
}
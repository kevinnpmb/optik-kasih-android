package com.skripsi.optik_kasih.ui.history

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.skripsi.optik_kasih.OptikKasihApp
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.adapter.ItemAdapter
import com.skripsi.optik_kasih.databinding.ActivityHistoryDetailBinding
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.ui.main.MainActivity
import com.skripsi.optik_kasih.ui.payment.PaymentActivity
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.utils.Utilities.toUser
import com.skripsi.optik_kasih.vo.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HistoryDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityHistoryDetailBinding
    private val viewModel: HistoryDetailViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utilities.initToolbar(this, binding.toolbar.toolbar, "", hideBack = false, hideCart = true)
        initObserver()
        initListener()
        binding.srlHistoryDetail.setColorSchemeColors(
            ContextCompat.getColor(
                this@HistoryDetailActivity,
                R.color.primaryColor
            )
        )
        binding.orderedItems.adapter = ItemAdapter()
        viewModel.orderIdMutableLiveData.postValue(intent.extras?.getString(HISTORY_DETAIL_ID))
    }

    private fun initListener() {
        binding.apply {
            srlHistoryDetail.setOnRefreshListener {
                viewModel.getOrder()
            }

            btnPay.setOnClickListener {
                startActivity(Intent(this@HistoryDetailActivity, PaymentActivity::class.java).apply {
                    putExtra(PaymentActivity.ORDER_ID, viewModel.orderId)
                })
            }

            btnDone.setOnClickListener {
                viewModel.finishOrder()
            }
        }
    }

    private fun initObserver() {
        viewModel.apply {
            orderIdMutableLiveData.observe(this@HistoryDetailActivity) {
                viewModel.getOrder()
            }

            orderMutableLiveData.observe(this@HistoryDetailActivity) {
                binding.apply {
                    loading.root.isVisible = it.status == Status.LOADING
                    error.root.isVisible = it.status == Status.ERROR
                    background.isVisible = loading.root.isVisible || error.root.isVisible
                    when (it.status) {
                        Status.SUCCESS -> {
                            srlHistoryDetail.isRefreshing = false
                            it.data?.order?.order?.order?.let { order ->
                                val snapshot = Gson().fromJson(order.snapshot, OrderSnapshot::class.java)
                                val orderStatus = OrderStatus.fromValue(snapshot.orderStatus)
                                toolbar.toolbar.title = getString(R.string.order_id_template, Utilities.zeroPadding(snapshot.id))
                                paidContainer.isVisible = PaymentStatus.fromValue(snapshot.orderPayment?.status) == PaymentStatus.Paid
                                btnPay.isVisible = orderStatus == OrderStatus.WaitingForPayment
                                btnDone.isVisible = !btnPay.isVisible
                                btnDone.isEnabled = orderStatus == OrderStatus.Sent
                                orderDate.text =
                                    Utilities.formatISO8601ToDateString(snapshot.orderDate.toString(), Utilities.DateType.VIEW)
                                doneDate.text =
                                    Utilities.formatISO8601ToDateString(snapshot.orderReceived.toString(), Utilities.DateType.VIEW)
                                        ?: "-"
                                paymentStatus.text = PaymentStatus.fromValue(snapshot.orderPayment?.status)?.label?.let {
                                    getString(it)
                                } ?: "-"
                                transactionStatus.text =
                                    orderStatus?.label?.let {
                                        getString(it)
                                    } ?: "-"
                                (orderedItems.adapter as ItemAdapter).submitList(snapshot.orderProducts.map {
                                    it.productSnapshot?.toCart(it.qty ?: 0)
                                })
                                subtotal.text = Utilities.convertPrice(
                                    (orderedItems.adapter as ItemAdapter).currentList.countSubtotal()
                                        .toString()
                                )
                                discount.text = Utilities.convertPrice(
                                    (orderedItems.adapter as ItemAdapter).currentList.countDiscount()
                                        .toString()
                                )
                                total.text = Utilities.convertPrice(
                                    (orderedItems.adapter as ItemAdapter).currentList.countTotal()
                                        .toString()
                                )
                                if (paidContainer.isVisible) {
                                    paid.text = Utilities.convertPrice(snapshot.orderPayment?.paid.toString())
                                }
                            }
                        }
                        Status.ERROR -> {
                            srlHistoryDetail.isRefreshing = false
                            Utilities.showToast(
                                this@HistoryDetailActivity,
                                binding.root,
                                it.message,
                                Utilities.ToastType.ERROR
                            )
                        }
                        Status.LOADING -> {}
                        Status.UNAUTHORIZED -> {
                            Utilities.showInvalidApiKeyAlert(this@HistoryDetailActivity)
                        }
                    }
                }
            }

            finishOrderMutableLiveData.observe(this@HistoryDetailActivity) {
                binding.apply {
                    binding.btnDone.isEnabled = it.status != Status.LOADING
                    when (it.status) {
                        Status.SUCCESS -> {
                            loadingDialog.dismiss()
                            getOrder()
                        }
                        Status.ERROR -> {
                            loadingDialog.dismiss()
                            Utilities.showToast(
                                this@HistoryDetailActivity,
                                binding.root,
                                it.message,
                                Utilities.ToastType.ERROR
                            )
                        }
                        Status.LOADING -> {
                            loadingDialog.show()
                        }
                        Status.UNAUTHORIZED -> {
                            Utilities.showInvalidApiKeyAlert(this@HistoryDetailActivity)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val HISTORY_DETAIL_ID = "HISTORY_DETAIL_ID"
    }
}
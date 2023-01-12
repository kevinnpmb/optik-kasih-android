package com.skripsi.optik_kasih.ui.payment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.databinding.ActivityPaymentBinding
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.ui.history.HistoryDetailActivity
import com.skripsi.optik_kasih.ui.main.MainActivity
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.vo.OrderStatus
import com.skripsi.optik_kasih.vo.Status
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class PaymentActivity : BaseActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private val viewModel: PaymentViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObserver()
        initListener()
        Utilities.initToolbar(
            this,
            binding.toolbar.toolbar,
            getString(R.string.payment),
            hideBack = false,
            hideCart = true
        )
        viewModel.orderIdMutableLiveData.postValue(intent.extras?.getString(ORDER_ID))
        onBackPressedDispatcher.addCallback(this) {
            if (isTaskRoot) {
                startActivity(Intent(this@PaymentActivity, MainActivity::class.java))
            }
            finish()
        }
        binding.countDownTimer.apply {
            setOnCountdownEndListener {
                setView(OrderStatus.Canceled)
            }
        }
    }

    private fun initListener() {
        binding.apply {
            btnContinueShopping.setOnClickListener {
                startActivity(Intent(this@PaymentActivity, MainActivity::class.java))
                finish()
            }

            trackTransaction.setOnClickListener {
                startActivity(
                    Intent(
                        this@PaymentActivity,
                        HistoryDetailActivity::class.java
                    ).apply {
                        putExtra(HistoryDetailActivity.HISTORY_DETAIL_ID, viewModel.orderId)
                    })
            }

            checkPaymentStatus.setOnClickListener {
                viewModel.getOrder()
            }

            btnCopy.setOnClickListener {
                val clipboard: ClipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Virtual Account", virtualAccount.text)
                clipboard.setPrimaryClip(clip)
                Utilities.showToast(
                    this@PaymentActivity,
                    binding.root,
                    getString(R.string.va_copied),
                    Utilities.ToastType.OTHER,
                    R.drawable.ic_success,
                    R.color.grey
                )
            }
        }
    }

    private fun initObserver() {
        viewModel.apply {
            orderIdMutableLiveData.observe(this@PaymentActivity) {
                viewModel.getOrder()
            }

            orderMutableLiveData.observe(this@PaymentActivity) {
                binding.apply {
                    loading.root.isVisible = it.status == Status.LOADING
                    error.root.isVisible = it.status == Status.ERROR
                    background.isVisible = loading.root.isVisible || error.root.isVisible
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.data?.order?.order?.order?.let { order ->
                                val orderStatus = OrderStatus.fromValue(order.order_status)
                                setView(orderStatus)
                                if (order.order_payment?.orderPayment?.payment_bank?.id == "1") {
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        viewModel.makePayment()
                                    }, 5000)
                                }
                                virtualAccount.text = order.order_payment?.orderPayment?.payment_bank?.bank_number ?: "-"
                                Utilities.loadImageUrl(order.order_payment?.orderPayment?.payment_bank?.bank_logo, bankImage)
                                orderId.text = getString(
                                    R.string.hashtag,
                                    Utilities.zeroPadding(order.id.toInt())
                                )
                                price.text = Utilities.convertPrice(order.grand_total.toString())
                                if (paymentInfo.isVisible) {
                                    Utilities.formatToDateISO8601(order.order_payment?.orderPayment?.expired_date.toString())
                                        ?.let { expiredDate ->
                                            val timeDiff: Long =
                                                Calendar.getInstance().apply {
                                                    time = expiredDate
                                                }.timeInMillis - Calendar.getInstance().timeInMillis
                                            countDownTimer.start(timeDiff)
                                        }
                                }
                            }
                        }
                        Status.ERROR -> {
                            Utilities.showToast(
                                this@PaymentActivity,
                                binding.root,
                                it.message,
                                Utilities.ToastType.ERROR
                            )
                        }
                        Status.LOADING -> {}
                        Status.UNAUTHORIZED -> {
                            Utilities.showInvalidApiKeyAlert(this@PaymentActivity)
                        }
                    }
                }
            }
        }
    }

    private fun setView(orderStatus: OrderStatus?) {
        binding.apply {
            orderIcon.setAnimation(
                when (orderStatus) {
                    OrderStatus.Canceled, null -> "payment_failed.json"
                    else -> "payment_success.json"
                }
            )
            orderIcon.playAnimation()
            orderMessage.text = getString(
                when (orderStatus) {
                    OrderStatus.WaitingForPayment -> R.string.order_success
                    OrderStatus.Canceled, null -> R.string.payment_failed
                    else -> R.string.payment_success
                }
            )
            orderMessage.setTextColor(
                ContextCompat.getColor(
                    this@PaymentActivity, when (orderMessage.text) {
                        getString(R.string.order_success), getString(R.string.payment_success) -> R.color.success
                        getString(R.string.payment_failed) -> R.color.danger
                        else -> R.color.primaryColor
                    }
                )
            )
            paymentInfo.isVisible = orderStatus == OrderStatus.WaitingForPayment
            checkPaymentStatus.isVisible =
                orderStatus == OrderStatus.WaitingForPayment
            trackTransaction.isVisible =
                orderStatus != OrderStatus.WaitingForPayment && orderStatus != OrderStatus.Canceled
            orderDetail.isVisible = orderStatus != OrderStatus.Canceled
        }
    }

    companion object {
        const val ORDER_ID = "ORDER_ID"
    }
}
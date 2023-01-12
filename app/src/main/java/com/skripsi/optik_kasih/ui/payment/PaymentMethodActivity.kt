package com.skripsi.optik_kasih.ui.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.adapter.HistoryAdapter
import com.skripsi.optik_kasih.adapter.PaymentMethodAdapter
import com.skripsi.optik_kasih.adapter.PaymentMethodRow
import com.skripsi.optik_kasih.databinding.ActivityPaymentMethodBinding
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.utils.Utilities.parcelable
import com.skripsi.optik_kasih.vo.PaymentMethod
import com.skripsi.optik_kasih.vo.Status
import com.skripsi.optik_kasih.vo.countTotal
import com.skripsi.optik_kasih.vo.countTotalQty
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodActivity : BaseActivity() {
    private lateinit var binding: ActivityPaymentMethodBinding
    private val viewModel: PaymentMethodViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.address = intent.parcelable(ADDRESS_FOR_ORDER)
        Utilities.initToolbar(
            this,
            binding.toolbar.toolbar,
            getString(R.string.payment_method),
            hideBack = false,
            hideCart = true
        )
        binding.srlPaymentMethod.setColorSchemeColors(
            ContextCompat.getColor(
                this,
                R.color.primaryColor
            )
        )
        initObserver()
        initListener()
        binding.rvPaymentMethod.apply {
            adapter = PaymentMethodAdapter {
                (adapter as PaymentMethodAdapter).selectedPaymentMethodSetter(it)
                binding.btnCheckout.isEnabled =
                    (adapter as PaymentMethodAdapter).selectedPaymentMethod != null
            }
        }
        viewModel.getCartList()
        viewModel.getPaymentMethod()
    }

    private fun initListener() {
        binding.apply {
            btnCheckout.setOnClickListener {
                (binding.rvPaymentMethod.adapter as PaymentMethodAdapter).selectedPaymentMethod?.let {
                    viewModel.createOrder(it.id.toInt())
                }
            }

            srlPaymentMethod.setOnRefreshListener {
                viewModel.getCartList()
                viewModel.getPaymentMethod()
            }
        }
    }

    private fun initObserver() {
        viewModel.apply {
            createOrderMutableLiveData.observe(this@PaymentMethodActivity) {
                binding.btnCheckout.isEnabled = it.status != Status.LOADING
                when (it.status) {
                    Status.SUCCESS -> {
                        loadingDialog.dismiss()
                        Toast.makeText(
                            this@PaymentMethodActivity,
                            getString(R.string.order_success_msg),
                            Toast.LENGTH_SHORT
                        ).show()
                        deleteCart()
                        startActivity(
                            Intent(
                                this@PaymentMethodActivity,
                                PaymentActivity::class.java
                            ).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                putExtra(
                                    PaymentActivity.ORDER_ID,
                                    it.data?.order?.order_create?.order?.id
                                )
                            }
                        )
                        finish()
                    }
                    Status.ERROR -> {
                        loadingDialog.dismiss()
                        Utilities.showToast(
                            this@PaymentMethodActivity,
                            binding.root,
                            it.message,
                            Utilities.ToastType.ERROR
                        )
                    }
                    Status.LOADING -> {
                        loadingDialog.show()
                    }
                    Status.UNAUTHORIZED -> {
                        Utilities.showInvalidApiKeyAlert(this@PaymentMethodActivity)
                    }
                }
            }

            cartListMutableLiveData.observe(this@PaymentMethodActivity) {
                binding.apply {
                    quantity.text =
                        getString(
                            R.string.total_item,
                            getString(R.string.item_quantity, it.countTotalQty())
                        )
                    price.text = Utilities.convertPrice(it.countTotal().toString())
                }
            }

            paymentMethodMutableLiveData.observe(this@PaymentMethodActivity) {
                binding.apply {
                    loading.root.isVisible = it.status == Status.LOADING
                    error.root.isVisible = it.status == Status.ERROR
                    when (it.status) {
                        Status.SUCCESS -> {
                            rvPaymentMethod.isVisible = true
                            srlPaymentMethod.isRefreshing = false
                            val paymentMethods =
                                it.data?.payment_bank?.payment_banks?.mapNotNull { bankData ->
                                    bankData?.let {
                                        PaymentMethod(
                                            bankData.id,
                                            bankData.bank_logo,
                                            "Virtual Account",
                                            bankData.bank_name,
                                            bankData.bank_number
                                        )
                                    }
                                }
                            (rvPaymentMethod.adapter as PaymentMethodAdapter).submitList(
                                paymentMethods?.groupBy { paymentMethod -> paymentMethod.group }
                                    ?.flatMap { paymentMethodMap ->
                                        listOf(
                                            PaymentMethodRow.Header(paymentMethodMap.key),
                                            *(paymentMethodMap.value.map { item ->
                                                (PaymentMethodRow.List(
                                                    item
                                                ))
                                            }).toTypedArray()
                                        )
                                    })
                        }
                        Status.ERROR -> {
                            srlPaymentMethod.isRefreshing = false
                            Utilities.showToast(
                                this@PaymentMethodActivity,
                                binding.root,
                                it.message,
                                Utilities.ToastType.ERROR
                            )
                        }
                        Status.LOADING -> {
                            rvPaymentMethod.isVisible = false
                        }
                        Status.UNAUTHORIZED -> {
                            Utilities.showInvalidApiKeyAlert(this@PaymentMethodActivity)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val ADDRESS_FOR_ORDER = "ADDRESS_FOR_ORDER"
    }
}
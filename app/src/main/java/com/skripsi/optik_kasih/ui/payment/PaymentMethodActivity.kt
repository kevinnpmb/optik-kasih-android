package com.skripsi.optik_kasih.ui.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.skripsi.optik_kasih.OptikKasihApp
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.adapter.HomeRow
import com.skripsi.optik_kasih.adapter.PaymentMethodAdapter
import com.skripsi.optik_kasih.adapter.PaymentMethodRow
import com.skripsi.optik_kasih.databinding.ActivityPaymentMethodBinding
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.ui.main.MainActivity
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.utils.Utilities.toUser
import com.skripsi.optik_kasih.vo.PaymentMethod
import com.skripsi.optik_kasih.vo.Status
import com.skripsi.optik_kasih.vo.countTotal
import com.skripsi.optik_kasih.vo.countTotalQty
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class PaymentMethodActivity : BaseActivity() {
    private lateinit var binding: ActivityPaymentMethodBinding
    private val viewModel: PaymentMethodViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utilities.initToolbar(
            this,
            binding.toolbar.toolbar,
            getString(R.string.payment_method),
            hideBack = false,
            hideCart = true
        )
        initObserver()
        initListener()
        val paymentMethods = listOf(
            PaymentMethod(
                0, R.drawable.logo_bca, "Virtual Account", "BANK BCA"
            ),
            PaymentMethod(
                1, R.drawable.logo_bni, "Virtual Account", "BANK BNI"
            ),
        )
        binding.rvPaymentMethod.apply {
            adapter = PaymentMethodAdapter {
                (adapter as PaymentMethodAdapter).selectedPaymentMethodSetter(it)
                binding.btnCheckout.isEnabled = (adapter as PaymentMethodAdapter).selectedPaymentMethod != null
            }
            (adapter as PaymentMethodAdapter).submitList(paymentMethods.groupBy { it.group }
                .flatMap {
                    listOf(PaymentMethodRow.Header(it.key), *(it.value.map { item ->
                        (PaymentMethodRow.List(
                            item
                        ))
                    }).toTypedArray())
                })
        }
        viewModel.getCartList()
    }

    private fun initListener() {
        binding.apply {
            btnCheckout.setOnClickListener {
                viewModel.createOrder()
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
                        startActivity(
                            Intent(
                                this@PaymentMethodActivity,
                                PaymentActivity::class.java
                            )
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
                        "Total (${getString(R.string.item_quantity, it.countTotalQty())})"
                    price.text = Utilities.convertPrice(it.countTotal().toString())
                }
            }
        }
    }
}
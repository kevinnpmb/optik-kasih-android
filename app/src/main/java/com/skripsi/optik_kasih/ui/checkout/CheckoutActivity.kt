package com.skripsi.optik_kasih.ui.checkout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.adapter.CartAdapter
import com.skripsi.optik_kasih.adapter.ItemAdapter
import com.skripsi.optik_kasih.databinding.ActivityCartBinding
import com.skripsi.optik_kasih.databinding.ActivityCheckoutBinding
import com.skripsi.optik_kasih.ui.cart.CartViewModel
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.ui.detail.DetailActivity
import com.skripsi.optik_kasih.utils.Utilities
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutActivity : BaseActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    val viewModel: CheckoutViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utilities.initToolbar(
            this,
            binding.toolbar.toolbar,
            getString(R.string.checkout),
            hideBack = false,
            hideCart = true
        )
        binding.orderedItems.apply {
            adapter = ItemAdapter {
                startActivity(Intent(this@CheckoutActivity, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.PRODUCT_ID, it.id)
                })
            }
            isNestedScrollingEnabled = false
        }
        initObserver()
        initListener()
        viewModel.getCartList()
    }

    private fun initListener() {
        binding.apply {

        }
    }

    private fun initObserver() {
        viewModel.apply {
            cartListMutableLiveData.observe(this@CheckoutActivity) {
                binding.apply {
                    if (it.isEmpty()) {
                        finish()
                        return@observe
                    }
                    (orderedItems.adapter as ItemAdapter).submitList(it)
                    val discountAmount = it.sumOf { cart -> (cart.discount ?: 0.0) * cart.quantity }
                    val subTotalAmount = it.sumOf { cart -> cart.basePrice * cart.quantity }
                    discount.text = Utilities.convertPrice(discountAmount.toString())
                    subtotal.text = Utilities.convertPrice(subTotalAmount.toString())
                    total.text =
                        Utilities.convertPrice((subTotalAmount - discountAmount).toString())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCartList()
    }
}
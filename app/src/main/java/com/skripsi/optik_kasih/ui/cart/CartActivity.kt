package com.skripsi.optik_kasih.ui.cart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.adapter.CartAdapter
import com.skripsi.optik_kasih.adapter.HomeAdapter
import com.skripsi.optik_kasih.adapter.HomeRow
import com.skripsi.optik_kasih.databinding.ActivityCartBinding
import com.skripsi.optik_kasih.databinding.ActivityDetailBinding
import com.skripsi.optik_kasih.ui.checkout.CheckoutActivity
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.ui.detail.DetailViewModel
import com.skripsi.optik_kasih.utils.Utilities
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartActivity : BaseActivity() {
    private lateinit var binding: ActivityCartBinding
    val viewModel: CartViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utilities.initToolbar(this, binding.toolbar.toolbar, getString(R.string.cart), hideBack = false, hideCart = true)
        binding.srlCart.setColorSchemeColors(
            ContextCompat.getColor(
                this@CartActivity,
                R.color.primaryColor
            )
        )
        binding.rvCart.apply {
            itemAnimator = null
            adapter = CartAdapter { type, cart, qty ->
                when (type) {
                    CartAdapter.Type.INCREMENT -> {
                        viewModel.updateCart(cart.copy(quantity = qty))
                    }
                    CartAdapter.Type.DECREMENT -> {
                        if (qty > 0) {
                            viewModel.updateCart(cart.copy(quantity = qty))
                        } else {
                            viewModel.deleteCart(cart.id)
                        }
                    }
                }
            }
        }
        initObserver()
        initListener()
        viewModel.getCartList()
    }

    private fun initListener() {
        binding.apply {
            btnToCheckout.setOnClickListener {
                startActivity(Intent(this@CartActivity, CheckoutActivity::class.java))
            }

            srlCart.setOnRefreshListener {
                viewModel.getCartList()
            }
        }
    }

    private fun initObserver() {
        viewModel.apply {
            cartListMutableLiveData.observe(this@CartActivity) {
                binding.apply {
                    srlCart.isRefreshing = false
                    rvCart.isVisible = it.isNotEmpty()
                    empty.root.isVisible = !rvCart.isVisible
                    (rvCart.adapter as CartAdapter).submitList(it)
                    btnToCheckout.isVisible = it.isNotEmpty()
                    tvCartItemCount.text =
                        getString(R.string.item_quantity, it.sumOf { cart -> cart.quantity })
                    tvCartPrice.text = Utilities.convertPrice(it.sumOf { cart ->
                        ((cart.basePrice - (cart.discount ?: 0.0)) * cart.quantity)
                    }.toString())
                }
            }
        }
    }
}
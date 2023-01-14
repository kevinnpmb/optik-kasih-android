package com.skripsi.optik_kasih.ui.detail

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.adapter.HomeAdapter
import com.skripsi.optik_kasih.adapter.HomeRow
import com.skripsi.optik_kasih.databinding.ActivityDetailBinding
import com.skripsi.optik_kasih.fragment.Product
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.ui.home.HomeViewModel
import com.skripsi.optik_kasih.ui.login.LoginActivity
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.utils.Utilities.toCart
import com.skripsi.optik_kasih.vo.Cart
import com.skripsi.optik_kasih.vo.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DetailActivity : BaseActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.srlDetail.setColorSchemeColors(
            ContextCompat.getColor(
                this,
                R.color.primaryColor
            )
        )
        Utilities.initToolbar(this, binding.toolbar.toolbar, "", hideBack = false, hideCart = true)
        initObserver()
        initListener()
        viewModel.productIdMutableLiveData.value = intent.extras?.getString(PRODUCT_ID)
    }


    private fun initListener() {
        binding.apply {
            srlDetail.setOnRefreshListener {
                viewModel.getProduct()
            }

            qtyChanger.btnAdd.setOnClickListener {
                viewModel.quantity.value = viewModel.quantity.value?.plus(1)
            }

            qtyChanger.btnRemove.setOnClickListener {
                viewModel.quantity.value = viewModel.quantity.value?.minus(1)
            }

            btnAddToCart.setOnClickListener {
                if (preferencesHelper.isLogin) {
                    viewModel.product?.toCart(viewModel.quantity.value ?: 0)?.let { cart ->
                        viewModel.insertToCart(cart)
                        Toast.makeText(this@DetailActivity, getString(R.string.add_item_to_cart_success), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    startActivity(Intent(this@DetailActivity, LoginActivity::class.java))
                }
            }

            btnDeleteCart.setOnClickListener {
                if (preferencesHelper.isLogin) {
                    viewModel.product?.toCart(viewModel.quantity.value ?: 0)?.let { cart ->
                        viewModel.deleteCart(cart.id)
                        Toast.makeText(this@DetailActivity, getString(R.string.delete_item_cart_success), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    startActivity(Intent(this@DetailActivity, LoginActivity::class.java))
                }
            }
        }
    }

    private fun initObserver() {
        viewModel.apply {
            productIdMutableLiveData.observe(this@DetailActivity) {
                viewModel.getProduct()
            }

            productMutableLiveData.observe(
                this@DetailActivity
            ) {
                binding.apply {
                    loading.root.isVisible = it.status == Status.LOADING
                    error.root.isVisible = it.status == Status.ERROR
                    background.isVisible = loading.root.isVisible || error.root.isVisible
                    when (it.status) {
                        Status.SUCCESS -> {
                            srlDetail.isRefreshing = false
                            viewModel.viewModelScope.launch {
                                viewModel.availableCart = withContext(Dispatchers.IO) {
                                    viewModel.getCart(productId ?: "-1")
                                }
                                quantity.value = viewModel.availableCart?.quantity ?: 0
                            }
                            setView(it.data?.product?.product?.product)
                        }
                        Status.ERROR -> {
                            srlDetail.isRefreshing = false
                            Utilities.showToast(this@DetailActivity, binding.root, it.message, Utilities.ToastType.ERROR)
                        }
                        Status.LOADING -> {}
                        Status.UNAUTHORIZED -> {
                            Utilities.showInvalidApiKeyAlert(this@DetailActivity)
                        }
                    }
                }
            }

            quantity.observe(this@DetailActivity) {
                binding.apply {
                    val price = ((product?.product_price ?: 0.0) - (product?.discount ?: 0.0)) * it
                    qtyChanger.quantity.text = it.toString()
                    qtyChanger.btnAdd.isEnabled = it < (product?.product_stock ?: 0)
                    qtyChanger.btnRemove.isEnabled = it > 0
                    subtotal.text = Utilities.convertPrice(
                        price.toString()
                    )
                    btnDeleteCart.isVisible = it <= 0 && viewModel.isCartDataAvailable
                    btnAddToCart.isVisible = it >= 0 && !btnDeleteCart.isVisible
                    btnAddToCart.isEnabled = it > 0
                }
            }
        }
    }

    private fun setView(product: Product?) {
        binding.apply {
            val isStockEmpty = (product?.product_stock ?: 0) <= 0
            Utilities.loadImageUrl(product?.product_image, itemImg)
            brand.text = product?.product_brand ?: "-"
            description.text = product?.product_description ?: "-"
            priceBfrDiscount.isVisible = product?.discount != null && product.discount > 0
            productStock.text = getString(R.string.product_stock, product?.product_stock ?: 0)
            if (priceBfrDiscount.isVisible) {
                priceBfrDiscount.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    text = Utilities.convertPrice((product?.product_price ?: 0.0).toString())
                }
            }
            pricePerItem.text = Utilities.convertPrice(((product?.product_price ?: 0.0) - (product?.discount ?: 0.0)).toString())
            subtotal.text = Utilities.convertPrice(
                ((product?.product_price ?: 0.0) * (viewModel.quantity.value ?: 0)).toString()
            )
            toolbar.toolbar.title = product?.product_name ?: "-"
            btnAddToCart.isEnabled = isStockEmpty
            btnAddToCart.text = getString(if (isStockEmpty) R.string.stock_empty else R.string.add)
        }
    }

    companion object {
        const val PRODUCT_ID = "PRODUCT_ID"
    }
}
package com.skripsi.optik_kasih.ui.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.adapter.HomeAdapter
import com.skripsi.optik_kasih.adapter.HomeRow
import com.skripsi.optik_kasih.databinding.ActivityDetailBinding
import com.skripsi.optik_kasih.fragment.Product
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.ui.home.HomeViewModel
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.vo.Status
import dagger.hilt.android.AndroidEntryPoint

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
        viewModel.productId = intent.extras?.getString(PRODUCT_ID)
        viewModel.getProduct()
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
        }
    }

    private fun initObserver() {
        viewModel.apply {
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
                    val price = (product?.product_price ?: 0.0) * it
                    qtyChanger.quantity.text = it.toString()
                    qtyChanger.btnAdd.isEnabled = it < 100
                    qtyChanger.btnRemove.isEnabled = it > 0
                    subtotal.text = Utilities.convertPrice(
                        price.toString()
                    )
                    btnAddToCart.isEnabled = it != 0
                }
            }
        }
    }

    private fun setView(product: Product?) {
        binding.apply {
            brand.text = product?.product_brand ?: "-"
            description.text = product?.product_description ?: "-"
            subtotal.text = Utilities.convertPrice(
                ((product?.product_price ?: 0.0) * (viewModel.quantity.value ?: 0)).toString()
            )
            toolbar.toolbar.title = product?.product_name ?: "-"
        }
    }

    companion object {
        const val PRODUCT_ID = "PRODUCT_ID"
    }
}
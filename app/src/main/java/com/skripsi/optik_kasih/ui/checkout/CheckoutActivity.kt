package com.skripsi.optik_kasih.ui.checkout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.adapter.CartAdapter
import com.skripsi.optik_kasih.adapter.ItemAdapter
import com.skripsi.optik_kasih.databinding.ActivityCartBinding
import com.skripsi.optik_kasih.databinding.ActivityCheckoutBinding
import com.skripsi.optik_kasih.ui.address.MyAddressActivity
import com.skripsi.optik_kasih.ui.address.MyAddressActivity.Companion.IS_FOR_SELECT_ADDRESS
import com.skripsi.optik_kasih.ui.address.MyAddressActivity.Companion.SELECTED_ADDRESS
import com.skripsi.optik_kasih.ui.cart.CartViewModel
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.ui.detail.DetailActivity
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.utils.Utilities.addressDetail
import com.skripsi.optik_kasih.utils.Utilities.parcelable
import com.skripsi.optik_kasih.vo.AddressPref
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutActivity : BaseActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    val viewModel: CheckoutViewModel by viewModels()
    private val resultLauncherChooseAddress = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.extras?.parcelable<AddressPref>(SELECTED_ADDRESS)?.let {
                viewModel.selectedAddressMutableLiveData.postValue(it)
            }
        }
    }
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
            adapter = ItemAdapter()
            isNestedScrollingEnabled = false
        }
        initObserver()
        initListener()
        viewModel.getCartList()
    }

    private fun initListener() {
        binding.apply {
            btnChooseAddress.setOnClickListener {
                resultLauncherChooseAddress.launch(Intent(this@CheckoutActivity, MyAddressActivity::class.java).apply {
                    putExtra(IS_FOR_SELECT_ADDRESS, true)
                })
            }
        }
    }

    private fun initObserver() {
        viewModel.apply {
            selectedAddressMutableLiveData.observe(this@CheckoutActivity) {
                binding.apply {
                    selectedAddress.root.isVisible = it != null
                    noAddressContainer.isVisible = it == null
                    it?.let {
                        selectedAddress.apply {
                            val user = preferencesHelper.user
                            btnChangeAddress.isVisible = true
                            btnOption.isVisible = false
                            addressName.text = it.label
                            address.text = it.addressDetail
                            addressUserName.text = user?.name ?: "-"
                            addressPhoneNumber.text = user?.phone_number ?: "-"
                        }
                    }
                }
            }

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
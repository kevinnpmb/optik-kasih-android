package com.skripsi.optik_kasih.ui.address

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.adapter.AddressAdapter
import com.skripsi.optik_kasih.databinding.ActivityMyAddressBinding
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.utils.Utilities.parcelable
import com.skripsi.optik_kasih.utils.Utilities.toAddress
import com.skripsi.optik_kasih.utils.Utilities.toAddressPref
import com.skripsi.optik_kasih.vo.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyAddressActivity : BaseActivity() {
    private lateinit var binding: ActivityMyAddressBinding
    private val viewModel: MyAddressViewModel by viewModels()
    private var isForSelectAddress: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isForSelectAddress = intent.getBooleanExtra(IS_FOR_SELECT_ADDRESS, false)
        setView()
        initObserver()
        initListener()
        viewModel.primaryAddressMutableLiveData.value =
            preferencesHelper.primaryAddress?.toAddress(preferencesHelper)
        viewModel.getSavedAddress()
    }

    private fun setView() {
        binding.apply {
            Utilities.initToolbar(
                this@MyAddressActivity, toolbar.toolbar, getString(R.string.my_address),
                hideBack = false,
                hideCart = true
            )
            srlMyAddress.setColorSchemeColors(
                ContextCompat.getColor(
                    this@MyAddressActivity,
                    R.color.primaryColor
                )
            )
            rvMyAddress.adapter = AddressAdapter {
                if (isForSelectAddress) {
                    (rvMyAddress.adapter as AddressAdapter).selectedAddressSetter(it)
                } else {
                    AddressOptionDialog.newInstance(it.toAddressPref())
                        .show(supportFragmentManager, ADDRESS_DETAIL_TAG)
                }
            }
            fabAddAddress.isVisible = !isForSelectAddress
            btnSelectAddress.isVisible = isForSelectAddress
        }
    }

    private fun initObserver() {
        viewModel.apply {
            addressMutableLiveData.observe(this@MyAddressActivity) {
                binding.apply {
                    loading.root.isVisible = it.status == Status.LOADING
                    error.root.isVisible = it.status == Status.ERROR
                    when (it.status) {
                        Status.SUCCESS -> {
                            srlMyAddress.isRefreshing = false
                            val savedAddresses =
                                it.data?.address?.addressList?.map { it.address }.orEmpty()
                            rvMyAddress.isVisible = savedAddresses.isNotEmpty()
                            empty.root.isVisible = !rvMyAddress.isVisible
                            (rvMyAddress.adapter as AddressAdapter).submitList(savedAddresses)
                        }
                        Status.ERROR -> {
                            srlMyAddress.isRefreshing = false
                            Utilities.showToast(
                                this@MyAddressActivity,
                                binding.root,
                                it.message,
                                Utilities.ToastType.ERROR
                            )
                        }
                        Status.LOADING -> {
                            rvMyAddress.isVisible = false
                        }
                        Status.UNAUTHORIZED -> {
                            Utilities.showInvalidApiKeyAlert(this@MyAddressActivity)
                        }
                    }
                }
            }

            deleteAddressMutableLiveData.observe(this@MyAddressActivity) {
                when (it.status) {
                    Status.SUCCESS -> {
                        Utilities.showToast(
                            this@MyAddressActivity,
                            binding.root,
                            getString(R.string.delete_address_success),
                            Utilities.ToastType.SUCCESS
                        )
                        getSavedAddress()
                        (supportFragmentManager.findFragmentByTag(ADDRESS_DETAIL_TAG) as AddressOptionDialog).dismiss()
                    }
                    Status.ERROR -> {
                        Utilities.showToast(
                            this@MyAddressActivity,
                            binding.root,
                            it.message,
                            Utilities.ToastType.ERROR
                        )
                    }
                    Status.LOADING -> {}
                    Status.UNAUTHORIZED -> {
                        Utilities.showInvalidApiKeyAlert(this@MyAddressActivity)
                    }
                }
            }

            primaryAddressMutableLiveData.observe(this@MyAddressActivity) {
                (binding.rvMyAddress.adapter as AddressAdapter).setPrimaryAddress(it)
            }
        }
    }

    private fun initListener() {
        binding.apply {
            fabAddAddress.setOnClickListener {
                startActivity(Intent(this@MyAddressActivity, AddEditAddressActivity::class.java))
            }

            srlMyAddress.setOnRefreshListener {
                viewModel.getSavedAddress()
            }

            btnSelectAddress.setOnClickListener {
                setResult(RESULT_OK, Intent().apply {
                    putExtra(SELECTED_ADDRESS, (binding.rvMyAddress.adapter as AddressAdapter).selectedAddress?.toAddressPref())
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSavedAddress()
    }

    companion object {
        const val ADDRESS_DETAIL_TAG = "address-option-dialog"
        const val IS_FOR_SELECT_ADDRESS = "is-for-select-address"
        const val SELECTED_ADDRESS = "selected_address"
    }
}
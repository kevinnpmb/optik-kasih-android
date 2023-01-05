package com.skripsi.optik_kasih.ui.address

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.viewModels
import com.apollographql.apollo3.api.Fragment
import com.skripsi.optik_kasih.BuildConfig
import com.skripsi.optik_kasih.OptikKasihApp
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.databinding.ActivityAddEditAddressBinding
import com.skripsi.optik_kasih.fragment.Address
import com.skripsi.optik_kasih.ui.common.BaseActivity
import com.skripsi.optik_kasih.ui.main.MainActivity
import com.skripsi.optik_kasih.ui.register.RegisterActivity
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.utils.Utilities.addressDetail
import com.skripsi.optik_kasih.utils.Utilities.parcelable
import com.skripsi.optik_kasih.utils.Utilities.registerClearText
import com.skripsi.optik_kasih.utils.Utilities.toUser
import com.skripsi.optik_kasih.utils.Utilities.validate
import com.skripsi.optik_kasih.utils.Utilities.validateAll
import com.skripsi.optik_kasih.vo.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class AddEditAddressActivity : BaseActivity() {
    private lateinit var binding: ActivityAddEditAddressBinding
    private val viewModel: AddressViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utilities.initToolbar(
            this, binding.toolbar.toolbar, "",
            hideBack = false,
            hideCart = true
        )
        intent.parcelable<EditAddress>(EDIT_ADDRESS_DATA)?.let { address ->
            binding.toolbar.toolbar.title = getString(R.string.address_detail)
            viewModel.addressDataForEdit.postValue(address)
        } ?: run {
            binding.toolbar.toolbar.title = getString(R.string.add_address)
        }
        registerClearText()
        initObserver()
        initListener()
    }

    private fun initListener() {
        binding.apply {
            btnSave.setOnClickListener {
                val validationList: MutableList<Boolean> = arrayListOf()
                validationList.add(
                    tilAddressLabel.validate(
                        Utilities.TextFieldType.OTHER,
                        getString(R.string.invalid_address_label)
                    )
                )
                validationList.add(
                    tilAddress.validate(
                        Utilities.TextFieldType.OTHER,
                        getString(R.string.invalid_address)
                    )
                )
                validationList.add(
                    tilNeighbourhood.validate(
                        Utilities.TextFieldType.OTHER,
                        getString(R.string.invalid_neighbourhood)
                    )
                )
                validationList.add(
                    tilSubDistrict.validate(
                        Utilities.TextFieldType.OTHER,
                        getString(R.string.invalid_sub_district)
                    )
                )
                validationList.add(
                    tilPostCode.validate(
                        Utilities.TextFieldType.OTHER,
                        getString(R.string.invalid_post_code)
                    )
                )
                validationList.validateAll {
                    //post api
                    btnSave.isEnabled = false
                    viewModel.addressEdit?.let { address ->
                        viewModel.editAddress(
                            address.id,
                            tilAddressLabel.editText?.text.toString(),
                            tilAddress.editText?.text.toString(),
                            tilNeighbourhood.editText?.text.toString(),
                            tilSubDistrict.editText?.text.toString(),
                            tilPostCode.editText?.text.toString(),
                        )
                    } ?: run {
                        viewModel.createAddress(
                            tilAddressLabel.editText?.text.toString(),
                            tilAddress.editText?.text.toString(),
                            tilNeighbourhood.editText?.text.toString(),
                            tilSubDistrict.editText?.text.toString(),
                            tilPostCode.editText?.text.toString(),
                        )
                    }
                }
            }
        }
    }

    private fun registerClearText() {
        binding.apply {
            tilAddressLabel.registerClearText()
            tilAddress.registerClearText()
            tilNeighbourhood.registerClearText()
            tilSubDistrict.registerClearText()
            tilPostCode.registerClearText()
        }
    }

    private fun initObserver() {
        viewModel.apply {
            addressDataForEdit.observe(this@AddEditAddressActivity) { address ->
                binding.apply {
                    tilAddressDetail.editText?.setText(address.addressDetail)
                    tilAddressLabel.editText?.setText(address.label)
                    tilAddress.editText?.setText(address.address)
                    tilNeighbourhood.editText?.setText(address.kelurahan)
                    tilSubDistrict.editText?.setText(address.kecamatan)
                    tilPostCode.editText?.setText(address.postal)
                }
            }

            addAddressMutableLiveData.observe(this@AddEditAddressActivity) {
                binding.btnSave.isEnabled = it.status != Status.LOADING
                when (it.status) {
                    Status.SUCCESS -> {
                        loadingDialog.dismiss()
                        Toast.makeText(
                            this@AddEditAddressActivity,
                            getString(R.string.add_address_success),
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                    Status.ERROR -> {
                        loadingDialog.dismiss()
                        Utilities.showToast(
                            this@AddEditAddressActivity,
                            binding.root,
                            it.message,
                            Utilities.ToastType.ERROR
                        )
                    }
                    Status.LOADING -> {
                        loadingDialog.show()
                    }
                    Status.UNAUTHORIZED -> {
                        Utilities.showInvalidApiKeyAlert(this@AddEditAddressActivity)
                    }
                }
            }

            editAddressMutableLiveData.observe(this@AddEditAddressActivity) {
                binding.btnSave.isEnabled = it.status != Status.LOADING
                when (it.status) {
                    Status.SUCCESS -> {
                        loadingDialog.dismiss()
                        Utilities.showToast(
                            this@AddEditAddressActivity,
                            binding.root,
                            getString(R.string.edit_address_success),
                            Utilities.ToastType.SUCCESS
                        )
                    }
                    Status.ERROR -> {
                        loadingDialog.dismiss()
                        Utilities.showToast(
                            this@AddEditAddressActivity,
                            binding.root,
                            it.message,
                            Utilities.ToastType.ERROR
                        )
                    }
                    Status.LOADING -> {
                        loadingDialog.show()
                    }
                    Status.UNAUTHORIZED -> {
                        Utilities.showInvalidApiKeyAlert(this@AddEditAddressActivity)
                    }
                }
            }
        }
    }

    companion object {
        const val EDIT_ADDRESS_DATA = "EDIT_ADDRESS_DATA"
    }

    @Parcelize
    data class EditAddress(
        val id: String,
        val label: String,
        val address: String,
        val kecamatan: String,
        val kelurahan: String,
        val postal: String,
    ) : Parcelable
}
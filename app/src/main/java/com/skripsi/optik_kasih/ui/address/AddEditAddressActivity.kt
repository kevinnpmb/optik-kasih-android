package com.skripsi.optik_kasih.ui.address

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import com.apollographql.apollo3.api.Fragment
import com.google.android.material.textfield.TextInputLayout
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
import com.skripsi.optik_kasih.vo.AddressPref
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
        intent.parcelable<AddressPref>(EDIT_ADDRESS_DATA)?.let { address ->
            binding.toolbar.toolbar.title = getString(R.string.address_detail)
            viewModel.addressDataForEdit.postValue(address)
        } ?: run {
            binding.toolbar.toolbar.title = getString(R.string.add_address)
        }
        registerTextChangeListener()
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

    private fun registerTextChangeListener() {
        binding.apply {
            tilAddressLabel.registerClearText()
            tilAddress.changeAddressDetailListener {
                setAddressDetail(it, tilNeighbourhood.editText?.text.toString(), tilSubDistrict.editText?.text.toString(), tilPostCode.editText?.text.toString())
            }
            tilNeighbourhood.changeAddressDetailListener {
                setAddressDetail(tilAddress.editText?.text.toString(), it, tilSubDistrict.editText?.text.toString(), tilPostCode.editText?.text.toString())
            }
            tilSubDistrict.changeAddressDetailListener {
                setAddressDetail(tilAddress.editText?.text.toString(), tilNeighbourhood.editText?.text.toString(), it, tilPostCode.editText?.text.toString())
            }
            tilPostCode.changeAddressDetailListener {
                setAddressDetail(tilAddress.editText?.text.toString(), tilNeighbourhood.editText?.text.toString(), tilSubDistrict.editText?.text.toString(), it)
            }
        }
    }

    private fun setAddressDetail(address: String?, kelurahan: String?, kecamatan: String?, postal: String?) {
        viewModel.addressDetailMutableLiveData.postValue((if (!address.isNullOrBlank()) "$address," else "") +
                (if (!kelurahan.isNullOrBlank()) " $kelurahan," else "") +
                (if (!kecamatan.isNullOrBlank()) " $kecamatan," else "") +
                if (!postal.isNullOrBlank()) " $postal" else "")
    }

    private fun TextInputLayout.changeAddressDetailListener(callback: (String) -> Unit) {
        val clearTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                callback.invoke(p0.toString())
                error = null
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        editText?.addTextChangedListener(clearTextChangedListener)
    }

    private fun initObserver() {
        viewModel.apply {
            addressDataForEdit.observe(this@AddEditAddressActivity) { address ->
                binding.apply {
                    viewModel.addressDetailMutableLiveData.postValue(address.addressDetail)
                    tilAddressLabel.editText?.setText(address.label)
                    tilAddress.editText?.setText(address.address)
                    tilNeighbourhood.editText?.setText(address.kelurahan)
                    tilSubDistrict.editText?.setText(address.kecamatan)
                    tilPostCode.editText?.setText(address.postal)
                }
            }

            viewModel.addressDetailMutableLiveData.observe(this@AddEditAddressActivity) {
                binding.tilAddressDetail.editText?.setText(it)
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
}
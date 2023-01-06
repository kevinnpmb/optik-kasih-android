package com.skripsi.optik_kasih.ui.address

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.adapter.AddressAdapter
import com.skripsi.optik_kasih.databinding.FragmentAddressOptionDialogBinding
import com.skripsi.optik_kasih.ui.common.BaseBottomSheetDialogFragment
import com.skripsi.optik_kasih.ui.common.BaseFragment
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.utils.Utilities.parcelable
import com.skripsi.optik_kasih.utils.Utilities.toAddress
import com.skripsi.optik_kasih.vo.AddressPref
import com.skripsi.optik_kasih.vo.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressOptionDialog : BaseBottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddressOptionDialogBinding
    val viewModel: MyAddressViewModel by activityViewModels()
    private lateinit var address: AddressPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            address = it.parcelable(ADDRESS_DATA)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressOptionDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.makePrimaryAddress.text = getString(
            if (address == baseActivity.preferencesHelper.primaryAddress)
                R.string.delete_primary_address else R.string.to_primary
        )
        binding.makePrimaryAddress.icon = ContextCompat.getDrawable(requireContext(), if (address == baseActivity.preferencesHelper.primaryAddress)
            R.drawable.ic_star else R.drawable.star)
        initListener()
    }

    private fun initListener() {
        binding.apply {
            makePrimaryAddress.setOnClickListener {
                if ((address == baseActivity.preferencesHelper.primaryAddress)) {
                    baseActivity.preferencesHelper.primaryAddress = null
                    viewModel.primaryAddressMutableLiveData.postValue(null)
                } else {
                    baseActivity.preferencesHelper.primaryAddress = address
                    viewModel.primaryAddressMutableLiveData.postValue(address.toAddress(baseActivity.preferencesHelper))
                }
                dismiss()
            }

            editAddress.setOnClickListener {
                startActivity(Intent(requireContext(), AddEditAddressActivity::class.java).apply {
                    putExtra(AddEditAddressActivity.EDIT_ADDRESS_DATA, address)
                })
            }

            deleteAddress.setOnClickListener {
                Utilities.showSimpleAlertDialog(
                    requireActivity(),
                    getString(R.string.delete_address),
                    getString(R.string.delete_address_msg),
                    positiveListener = { dialog, _ ->
                        viewModel.deleteAddress(address.id)
                        if (address == baseActivity.preferencesHelper.primaryAddress) {
                            baseActivity.preferencesHelper.primaryAddress = null
                        }
                        dialog.dismiss()
                    }
                )
            }
        }
    }

    companion object {
        private const val ADDRESS_DATA = "ADDRESS_DATA"

        @JvmStatic
        fun newInstance(address: AddressPref) =
            AddressOptionDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(ADDRESS_DATA, address)
                }
            }
    }
}
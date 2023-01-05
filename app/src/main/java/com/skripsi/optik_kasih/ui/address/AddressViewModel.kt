package com.skripsi.optik_kasih.ui.address

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.optik_kasih.CreateAddressMutation
import com.skripsi.optik_kasih.GetProductQuery
import com.skripsi.optik_kasih.UpdateAddressMutation
import com.skripsi.optik_kasih.fragment.Address
import com.skripsi.optik_kasih.repository.AddressRepository
import com.skripsi.optik_kasih.repository.ProductsRepository
import com.skripsi.optik_kasih.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.IDN
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    var addressRepository: AddressRepository
) : ViewModel() {
    val addAddressMutableLiveData = MutableLiveData<Resource<CreateAddressMutation.Data>>()
    val addressDataForEdit = MutableLiveData<AddEditAddressActivity.EditAddress>()
    val addressEdit: AddEditAddressActivity.EditAddress? get() = addressDataForEdit.value
    fun createAddress(
        label: String,
        address: String,
        kecamatan: String,
        kelurahan: String,
        postal: String,
    ) {
        viewModelScope.launch {
            addressRepository.createAddress(
                label,
                address,
                kecamatan,
                kelurahan,
                postal,
                addAddressMutableLiveData
            )
        }
    }

    val editAddressMutableLiveData = MutableLiveData<Resource<UpdateAddressMutation.Data>>()
    fun editAddress(
        id: String,
        label: String,
        address: String,
        kecamatan: String,
        kelurahan: String,
        postal: String,
    ) {
        viewModelScope.launch {
            addressRepository.updateAddress(
                id,
                label,
                address,
                kecamatan,
                kelurahan,
                postal,
                editAddressMutableLiveData
            )
        }
    }
}
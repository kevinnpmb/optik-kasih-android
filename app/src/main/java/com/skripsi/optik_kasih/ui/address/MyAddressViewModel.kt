package com.skripsi.optik_kasih.ui.address

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.optik_kasih.*
import com.skripsi.optik_kasih.fragment.Address
import com.skripsi.optik_kasih.repository.AddressRepository
import com.skripsi.optik_kasih.repository.ProductsRepository
import com.skripsi.optik_kasih.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.IDN
import javax.inject.Inject

@HiltViewModel
class MyAddressViewModel @Inject constructor(
    var addressRepository: AddressRepository
) : ViewModel() {
    val addressMutableLiveData = MutableLiveData<Resource<GetSavedAddressQuery.Data>>()
    fun getSavedAddress() {
        viewModelScope.launch {
            addressRepository.getSavedAddress(addressMutableLiveData)
        }
    }

    val deleteAddressMutableLiveData = MutableLiveData<Resource<DeleteAddressMutation.Data>>()
    fun deleteAddress(id: String) {
        viewModelScope.launch {
            addressRepository.deleteAddress(id, deleteAddressMutableLiveData)
        }
    }
}
package com.skripsi.optik_kasih.ui.checkout

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.skripsi.optik_kasih.GetProductQuery
import com.skripsi.optik_kasih.GetProductsQuery
import com.skripsi.optik_kasih.LoginMutation
import com.skripsi.optik_kasih.fragment.Product
import com.skripsi.optik_kasih.repository.CartRepository
import com.skripsi.optik_kasih.repository.ProductsRepository
import com.skripsi.optik_kasih.repository.UserRepository
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.vo.AddressPref
import com.skripsi.optik_kasih.vo.Cart
import com.skripsi.optik_kasih.vo.CityInfo
import com.skripsi.optik_kasih.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    var cartRepository: CartRepository
) : ViewModel() {
    var allowedPostalCode: List<String>? = null
    fun setJsonToListCityInfo(context: Context) {
        viewModelScope.launch {
            allowedPostalCode = Gson().fromJson<List<CityInfo>>(
                Utilities.getJsonDataFromAsset(
                    context,
                    "raw/pekanbaru.json"
                ), object : TypeToken<List<CityInfo>>() {}.type
            ).map {
                it.postcode
            }
        }
    }

    val selectedAddressMutableLiveData = MutableLiveData<AddressPref>()
    val selectedAddress: AddressPref? get() = selectedAddressMutableLiveData.value

    val cartListMutableLiveData = MutableLiveData<List<Cart>>()
    fun getCartList() {
        viewModelScope.launch {
            cartListMutableLiveData.postValue(cartRepository.getCart())
        }
    }

    fun checkSelectedAddressPostalCodeAvailable(callback: (Boolean) -> Unit) {
        callback.invoke(allowedPostalCode?.contains(selectedAddress?.postal) == true)
    }
}
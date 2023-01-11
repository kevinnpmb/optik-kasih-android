package com.skripsi.optik_kasih.ui.payment

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import com.skripsi.optik_kasih.CreateOrderMutation
import com.skripsi.optik_kasih.GetProductsQuery
import com.skripsi.optik_kasih.LoginMutation
import com.skripsi.optik_kasih.repository.CartRepository
import com.skripsi.optik_kasih.repository.OrderRepository
import com.skripsi.optik_kasih.repository.ProductsRepository
import com.skripsi.optik_kasih.repository.UserRepository
import com.skripsi.optik_kasih.vo.Cart
import com.skripsi.optik_kasih.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PaymentMethodViewModel @Inject constructor(
    var orderRepository: OrderRepository,
    var cartRepository: CartRepository,
): ViewModel() {
    val createOrderMutableLiveData = MutableLiveData<Resource<CreateOrderMutation.Data>>()
    fun createOrder() {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                cartRepository.getCart()
            }
            orderRepository.createOrder(list, createOrderMutableLiveData)
        }
    }

    val cartListMutableLiveData = MutableLiveData<List<Cart>>()
    fun getCartList() {
        viewModelScope.launch {
            cartListMutableLiveData.postValue(cartRepository.getCart())
        }
    }
}
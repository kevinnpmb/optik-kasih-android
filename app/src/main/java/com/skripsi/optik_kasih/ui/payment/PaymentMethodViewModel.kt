package com.skripsi.optik_kasih.ui.payment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.optik_kasih.CreateOrderMutation
import com.skripsi.optik_kasih.GetMethodPaymentQuery
import com.skripsi.optik_kasih.repository.CartRepository
import com.skripsi.optik_kasih.repository.OrderRepository
import com.skripsi.optik_kasih.vo.AddressPref
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
) : ViewModel() {
    var address: AddressPref? = null
    val createOrderMutableLiveData = MutableLiveData<Resource<CreateOrderMutation.Data>>()
    fun createOrder(methodId: Int) {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                cartRepository.getCart()
            }
            orderRepository.createOrder(methodId, list, address, createOrderMutableLiveData)
        }
    }

    val cartListMutableLiveData = MutableLiveData<List<Cart>>()
    fun getCartList() {
        viewModelScope.launch {
            cartListMutableLiveData.postValue(cartRepository.getCart())
        }
    }

    fun deleteCart() {
        viewModelScope.launch {
            cartRepository.deleteAll()
        }
    }

    val paymentMethodMutableLiveData = MutableLiveData<Resource<GetMethodPaymentQuery.Data>>()
    fun getPaymentMethod() {
        viewModelScope.launch {
            orderRepository.getMethodPayment(paymentMethodMutableLiveData)
        }
    }
}
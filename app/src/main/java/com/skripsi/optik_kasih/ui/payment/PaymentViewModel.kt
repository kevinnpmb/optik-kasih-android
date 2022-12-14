package com.skripsi.optik_kasih.ui.payment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.optik_kasih.GetOrderQuery
import com.skripsi.optik_kasih.UpdatePaymentStatusMutation
import com.skripsi.optik_kasih.repository.OrderRepository
import com.skripsi.optik_kasih.vo.PaymentStatus
import com.skripsi.optik_kasih.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    var orderRepository: OrderRepository
): ViewModel() {
    val orderIdMutableLiveData = MutableLiveData<String>()
    val orderId: String? get() = orderIdMutableLiveData.value
    val orderMutableLiveData = MutableLiveData<Resource<GetOrderQuery.Data>>()
    fun getOrder() {
        viewModelScope.launch {
            orderId?.let {
                orderRepository.getOrder(it, orderMutableLiveData)
            }
        }
    }


    val makePaymentMutableLiveData = MutableLiveData<Resource<UpdatePaymentStatusMutation.Data>>()
    fun makePayment() {
        viewModelScope.launch {
            orderId?.let {
                orderRepository.updatePaymentStatus(it, PaymentStatus.Paid, makePaymentMutableLiveData)
            }
        }
    }
}
package com.skripsi.optik_kasih.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.optik_kasih.GetOrderQuery
import com.skripsi.optik_kasih.UpdateOrderStatusMutation
import com.skripsi.optik_kasih.UpdatePaymentStatusMutation
import com.skripsi.optik_kasih.repository.OrderRepository
import com.skripsi.optik_kasih.vo.OrderStatus
import com.skripsi.optik_kasih.vo.PaymentStatus
import com.skripsi.optik_kasih.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
    var orderRepository: OrderRepository,
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

    val finishOrderMutableLiveData = MutableLiveData<Resource<UpdateOrderStatusMutation.Data>>()
    fun finishOrder() {
        viewModelScope.launch {
            orderId?.let {
                orderRepository.updateOrderStatus(it, OrderStatus.Received, finishOrderMutableLiveData)
            }
        }
    }
}
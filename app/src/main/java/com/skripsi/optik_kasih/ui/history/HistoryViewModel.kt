package com.skripsi.optik_kasih.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skripsi.optik_kasih.GetMyOrdersQuery
import com.skripsi.optik_kasih.GetProductsQuery
import com.skripsi.optik_kasih.repository.OrderRepository
import com.skripsi.optik_kasih.repository.ProductsRepository
import com.skripsi.optik_kasih.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    var orderRepository: OrderRepository,
): ViewModel() {
    val ordersMutableLiveData = MutableLiveData<Resource<GetMyOrdersQuery.Data>>()
    fun getMyOrders() {
        viewModelScope.launch {
            orderRepository.getMyOrders(ordersMutableLiveData)
        }
    }
}
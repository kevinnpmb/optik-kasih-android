package com.skripsi.optik_kasih.ui.home

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import com.skripsi.optik_kasih.GetProductsQuery
import com.skripsi.optik_kasih.LoginMutation
import com.skripsi.optik_kasih.repository.ProductsRepository
import com.skripsi.optik_kasih.repository.UserRepository
import com.skripsi.optik_kasih.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    var productsRepository: ProductsRepository,
): ViewModel() {
    val productsMutableLiveData = MutableLiveData<Resource<GetProductsQuery.Data>>()
    fun getProducts() {
        viewModelScope.launch {
            productsRepository.getListProducts(productsMutableLiveData)
        }
    }
}
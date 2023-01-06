package com.skripsi.optik_kasih.ui.detail

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import com.skripsi.optik_kasih.GetProductQuery
import com.skripsi.optik_kasih.GetProductsQuery
import com.skripsi.optik_kasih.LoginMutation
import com.skripsi.optik_kasih.fragment.Product
import com.skripsi.optik_kasih.repository.CartRepository
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
class DetailViewModel @Inject constructor(
    var productsRepository: ProductsRepository,
    var cartRepository: CartRepository
) : ViewModel() {
    val productIdMutableLiveData = MutableLiveData<String>()
    val productId: String? get() = productIdMutableLiveData.value
    val quantity = MutableLiveData(0)
    val productMutableLiveData = MutableLiveData<Resource<GetProductQuery.Data>>()
    val product: Product? get() = productMutableLiveData.value?.data?.product?.product?.product
    fun getProduct() {
        viewModelScope.launch {
            productId?.let { productId ->
                productsRepository.getProduct(productId, productMutableLiveData)
            }
        }
    }

    fun insertToCart(cart: Cart) {
        viewModelScope.launch {
            val oldCart = withContext(Dispatchers.IO) {
                cartRepository.getCart(cart.id)
            }
            if (oldCart != null) {
                cartRepository.update(oldCart.copy(quantity = cart.quantity))
            } else {
                cartRepository.insert(cart)
            }
        }
    }

    suspend fun getCart(id: String) =
        cartRepository.getCart(id)
}
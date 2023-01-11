package com.skripsi.optik_kasih.repository

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo3.ApolloClient
import com.skripsi.optik_kasih.CreateOrderMutation
import com.skripsi.optik_kasih.GetProductQuery
import com.skripsi.optik_kasih.GetProductsQuery
import com.skripsi.optik_kasih.LoginMutation
//import com.skripsi.optik_kasih.LoginQuery
import com.skripsi.optik_kasih.api.NetworkResource
import com.skripsi.optik_kasih.type.LoginParam
import com.skripsi.optik_kasih.type.NewOrder
import com.skripsi.optik_kasih.utils.PreferencesHelper
import com.skripsi.optik_kasih.vo.Cart
import com.skripsi.optik_kasih.vo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    var preferencesHelper: PreferencesHelper,
    var networkResource: NetworkResource,
    var apolloClient: ApolloClient,
) {
    suspend fun createOrder(
        carts: List<Cart>,
        liveData: MutableLiveData<Resource<CreateOrderMutation.Data>>
    ) {
        withContext(Dispatchers.IO) {
//            networkResource.processMutationResponse(
//                apolloClient,
//                CreateOrderMutation(
//                    NewOrder()
//                ),
//                liveData
//            )
        }
    }
}
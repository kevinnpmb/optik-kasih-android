package com.skripsi.optik_kasih.repository

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo3.ApolloClient
import com.skripsi.optik_kasih.GetProductQuery
import com.skripsi.optik_kasih.GetProductsQuery
import com.skripsi.optik_kasih.LoginMutation
//import com.skripsi.optik_kasih.LoginQuery
import com.skripsi.optik_kasih.api.NetworkResource
import com.skripsi.optik_kasih.type.LoginParam
import com.skripsi.optik_kasih.utils.PreferencesHelper
import com.skripsi.optik_kasih.vo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsRepository @Inject constructor(
    var preferencesHelper: PreferencesHelper,
    var networkResource: NetworkResource,
    var apolloClient: ApolloClient,
) {
    suspend fun getListProducts(
        liveData: MutableLiveData<Resource<GetProductsQuery.Data>>
    ) {
        withContext(Dispatchers.IO) {
            networkResource.processQueryResponse(
                apolloClient,
                GetProductsQuery(),
                liveData
            )
        }
    }

    suspend fun getProduct(
        productId: String,
        liveData: MutableLiveData<Resource<GetProductQuery.Data>>
    ) {
        withContext(Dispatchers.IO) {
            networkResource.processQueryResponse(
                apolloClient,
                GetProductQuery(productId),
                liveData
            )
        }
    }
}
package com.skripsi.optik_kasih.repository

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo3.ApolloClient
import com.skripsi.optik_kasih.*
//import com.skripsi.optik_kasih.LoginQuery
import com.skripsi.optik_kasih.api.NetworkResource
import com.skripsi.optik_kasih.type.EditAddress
import com.skripsi.optik_kasih.type.LoginParam
import com.skripsi.optik_kasih.type.NewAddress
import com.skripsi.optik_kasih.utils.PreferencesHelper
import com.skripsi.optik_kasih.vo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepository @Inject constructor(
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

    suspend fun createAddress(
        label: String,
        address: String,
        kecamatan: String,
        kelurahan: String,
        postal: String,
        liveData: MutableLiveData<Resource<CreateAddressMutation.Data>>
    ) {
        withContext(Dispatchers.IO) {
            preferencesHelper.user?.id?.let {
                networkResource.processMutationResponse(
                    apolloClient,
                    CreateAddressMutation(
                        NewAddress(
                            label,
                            address,
                            kecamatan,
                            kelurahan,
                            postal,
                            it
                        )
                    ),
                    liveData
                )
            }
        }
    }

    suspend fun updateAddress(
        id: String,
        label: String,
        address: String,
        kecamatan: String,
        kelurahan: String,
        postal: String,
        liveData: MutableLiveData<Resource<UpdateAddressMutation.Data>>
    ) {
        withContext(Dispatchers.IO) {
            preferencesHelper.user?.id?.let {
                networkResource.processMutationResponse(
                    apolloClient,
                    UpdateAddressMutation(
                        EditAddress(
                            id,
                            label,
                            address,
                            kecamatan,
                            kelurahan,
                            postal,
                            it
                        )
                    ),
                    liveData
                )
            }
        }
    }

    suspend fun deleteAddress(
        id: String,
        liveData: MutableLiveData<Resource<DeleteAddressMutation.Data>>
    ) {
        withContext(Dispatchers.IO) {
            networkResource.processMutationResponse(
                apolloClient,
                DeleteAddressMutation(
                    id
                ),
                liveData
            )

        }
    }
}
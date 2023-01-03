package com.skripsi.optik_kasih.repository

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo3.ApolloClient
import com.skripsi.optik_kasih.LoginMutation
//import com.skripsi.optik_kasih.LoginQuery
import com.skripsi.optik_kasih.api.NetworkResource
import com.skripsi.optik_kasih.type.LoginParam
import com.skripsi.optik_kasih.vo.RequestHeaders
import com.skripsi.optik_kasih.utils.PreferencesHelper
import com.skripsi.optik_kasih.vo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    var preferencesHelper: PreferencesHelper,
    var requestHeaders: RequestHeaders,
    var networkResource: NetworkResource,
    var apolloClient: ApolloClient,
) {
    suspend fun loginUser(
        email: String,
        password: String,
        loginUserLiveData: MutableLiveData<Resource<LoginMutation.Data>>
    ) {
        withContext(Dispatchers.IO) {
            networkResource.processMutationResponse(
                apolloClient,
                LoginMutation(LoginParam(email, password)),
//                LoginQuery(),
                loginUserLiveData
            )
        }
    }
}
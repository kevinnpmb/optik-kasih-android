package com.skripsi.optik_kasih.repository

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.skripsi.optik_kasih.LoginMutation
import com.skripsi.optik_kasih.RegisterMutation
//import com.skripsi.optik_kasih.LoginQuery
import com.skripsi.optik_kasih.api.NetworkResource
import com.skripsi.optik_kasih.type.LoginParam
import com.skripsi.optik_kasih.type.NewCustomer
import com.skripsi.optik_kasih.utils.PreferencesHelper
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.vo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    var preferencesHelper: PreferencesHelper,
    var networkResource: NetworkResource,
    var apolloClient: ApolloClient,
) {
    suspend fun loginUser(
        email: String,
        password: String,
        liveData: MutableLiveData<Resource<LoginMutation.Data>>
    ) {
        withContext(Dispatchers.IO) {
            networkResource.processMutationResponse(
                apolloClient,
                LoginMutation(LoginParam(email, password)),
                liveData
            )
        }
    }

    suspend fun registerUser(
        name: String,
        birthDate: Date,
        gender: Int,
        phoneNumber: String,
        email: String,
        password: String,
        liveData: MutableLiveData<Resource<RegisterMutation.Data>>
    ) {
        withContext(Dispatchers.IO) {
            networkResource.processMutationResponse(
                apolloClient,
                RegisterMutation(NewCustomer(
                    name,
                    gender,
                    Utilities.formatToDateString(birthDate) ?: "",
                    phoneNumber,
                    Optional.present(email),
                    Optional.present(password),
                )),
                liveData
            )
        }
    }
}
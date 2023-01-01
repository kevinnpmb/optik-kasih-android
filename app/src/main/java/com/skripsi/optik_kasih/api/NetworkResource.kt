package com.skripsi.optik_kasih.api

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Mutation
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.exception.ApolloHttpException
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.vo.Resource
import com.skripsi.optik_kasih.vo.Status
import timber.log.Timber
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

private const val eTAG = "NetworkException"

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
class NetworkResource(private val context: Context) {
    suspend fun <D : Query.Data> processQueryResponse(
        apolloClient: ApolloClient,
        query: Query<D>,
        liveData: MutableLiveData<Resource<D>>,
    ) {
        try {
            liveData.postValue(
                Resource(
                    Status.LOADING,
                    null,
                    null
                )
            )
            val response = apolloClient.query(query).execute()
            if (response.data != null) {
                liveData.postValue(
                    Resource(
                        Status.SUCCESS,
                        response.data,
                        null
                    )
                )
            } else {
                liveData.postValue(
                    Resource(
                        Status.ERROR,
                        null,
                        response.errors?.get(0)?.message
                    )
                )
            }
        } catch (e: ApolloHttpException) {
            when (e.statusCode) {
                401 -> {
                    liveData.postValue(
                        Resource(
                            Status.UNAUTHORIZED,
                            null,
                            null
                        )
                    )
                }
                else -> {
                    //internal server error
                    liveData.postValue(
                        Resource(
                            Status.ERROR,
                            null,
                            context.getString(R.string.try_again)
                        )
                    )
                }
            }
            Timber.tag(eTAG).e("processQueryResponseApolloHttp: ${e.message}")
        } catch (e: UnknownHostException) {
            liveData.postValue(
                Resource(
                    Status.ERROR,
                    null,
                    context.getString(R.string.connection_unstable)
                )
            )
            Timber.tag(eTAG).e("processQueryResponseUnknownHost: ${e.message}")
        } catch (e: TimeoutException) {
            liveData.postValue(
                Resource(
                    Status.ERROR,
                    null,
                    context.getString(R.string.connection_unstable)
                )
            )
            Timber.tag(eTAG).e("processQueryResponseTimeout: ${e.message}")
        } catch (e: ApolloException) {
            liveData.postValue(
                Resource(
                    Status.ERROR,
                    null,
                    context.getString(R.string.try_again)
                )
            )
            Timber.tag(eTAG).e("processQueryResponseApollo: ${e.message}")
        } catch (e: Exception) {
            liveData.postValue(
                Resource(
                    Status.ERROR,
                    null,
                    context.getString(R.string.try_again)
                )
            )
            Timber.tag(eTAG).e("processQueryResponse: ${e.message}")
        }
    }

    suspend fun <D : Mutation.Data> processMutationResponse(
        apolloClient: ApolloClient,
        query: Mutation<D>,
        liveData: MutableLiveData<Resource<D>>,
    ) {
        try {
            liveData.postValue(
                Resource(
                    Status.LOADING,
                    null,
                    null
                )
            )
            val response = apolloClient.mutation(query).execute()
            if (response.data != null) {
                liveData.postValue(
                    Resource(
                        Status.SUCCESS,
                        response.data,
                        null
                    )
                )
            } else {
                liveData.postValue(
                    Resource(
                        Status.ERROR,
                        null,
                        response.errors?.get(0)?.message
                    )
                )
            }
        } catch (e: ApolloHttpException) {
            when (e.statusCode) {
                401 -> {
                    liveData.postValue(
                        Resource(
                            Status.UNAUTHORIZED,
                            null,
                            null
                        )
                    )
                }
                else -> {
                    //internal server error
                    liveData.postValue(
                        Resource(
                            Status.ERROR,
                            null,
                            context.getString(R.string.try_again)
                        )
                    )
                }
            }
            Timber.tag(eTAG).e("processQueryResponseApolloHttp: ${e.message}")
        } catch (e: UnknownHostException) {
            liveData.postValue(
                Resource(
                    Status.ERROR,
                    null,
                    context.getString(R.string.connection_unstable)
                )
            )
            Timber.tag(eTAG).e("processQueryResponseUnknownHost: ${e.message}")
        } catch (e: TimeoutException) {
            liveData.postValue(
                Resource(
                    Status.ERROR,
                    null,
                    context.getString(R.string.connection_unstable)
                )
            )
            Timber.tag(eTAG).e("processQueryResponseTimeout: ${e.message}")
        } catch (e: ApolloException) {
            liveData.postValue(
                Resource(
                    Status.ERROR,
                    null,
                    context.getString(R.string.try_again)
                )
            )
            Timber.tag(eTAG).e("processQueryResponseApollo: ${e.message}")
        } catch (e: Exception) {
            liveData.postValue(
                Resource(
                    Status.ERROR,
                    null,
                    context.getString(R.string.try_again)
                )
            )
            Timber.tag(eTAG).e("processQueryResponse: ${e.message}")
        }
    }
}

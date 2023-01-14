package com.skripsi.optik_kasih.repository

//import com.skripsi.optik_kasih.LoginQuery
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.skripsi.optik_kasih.*
import com.skripsi.optik_kasih.api.NetworkResource
import com.skripsi.optik_kasih.type.NewOrder
import com.skripsi.optik_kasih.type.NewOrderProductWithoutOrderID
import com.skripsi.optik_kasih.utils.PreferencesHelper
import com.skripsi.optik_kasih.utils.Utilities
import com.skripsi.optik_kasih.utils.Utilities.addressDetail
import com.skripsi.optik_kasih.vo.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    var preferencesHelper: PreferencesHelper,
    var networkResource: NetworkResource,
    var apolloClient: ApolloClient,
) {
    suspend fun getMyOrders(
        liveData: MutableLiveData<Resource<GetMyOrdersQuery.Data>>
    ) {
        withContext(Dispatchers.IO) {
            networkResource.processQueryResponse(
                apolloClient,
                GetMyOrdersQuery(),
                liveData
            )
        }
    }

    suspend fun getMethodPayment(
        liveData: MutableLiveData<Resource<GetMethodPaymentQuery.Data>>
    ) {
        withContext(Dispatchers.IO) {
            networkResource.processQueryResponse(
                apolloClient,
                GetMethodPaymentQuery(),
                liveData
            )
        }
    }

    suspend fun getOrder(
        id: String,
        liveData: MutableLiveData<Resource<GetOrderQuery.Data>>
    ) {
        withContext(Dispatchers.IO) {
            networkResource.processQueryResponse(
                apolloClient,
                GetOrderQuery(id),
                liveData
            )
        }
    }

    suspend fun createOrder(
        methodId: Int,
        carts: List<Cart>,
        address: AddressPref?,
        liveData: MutableLiveData<Resource<CreateOrderMutation.Data>>
    ) {
        withContext(Dispatchers.IO) {
            val now = Calendar.getInstance()
            networkResource.processMutationResponse(
                apolloClient,
                CreateOrderMutation(
                    NewOrder(
                        Utilities.formatToDateString(now.time)!!,
                        Optional.present(0),
                        Optional.present(0),
                        Optional.absent(),
                        Optional.absent(),
                        carts.countSubtotal(),
                        carts.countDiscount(),
                        carts.countTotal(),
                        Optional.absent(),
                        Optional.absent(),
                        preferencesHelper.user?.id?.toInt()!!,
                        Optional.present(carts.map {
                            NewOrderProductWithoutOrderID(
                                it.quantity,
                                it.basePrice,
                                it.id,
                                Optional.presentIfNotNull(it.discount)
                            )
                        }),
                        Optional.absent(),
                        Optional.presentIfNotNull(address?.addressDetail),
                        true,
                        Optional.present(methodId),
                        Optional.absent()
                    )
                ),
                liveData
            )
        }
    }

    suspend fun updateOrderStatus(
        orderId: String,
        orderStatus: OrderStatus,
        liveData: MutableLiveData<Resource<UpdateOrderStatusMutation.Data>>
    ) {
        withContext(Dispatchers.IO) {
            val now = Calendar.getInstance()
            networkResource.processMutationResponse(
                apolloClient,
                UpdateOrderStatusMutation(
                    orderId,
                    orderStatus.value
                ),
                liveData
            )
        }
    }

    suspend fun updatePaymentStatus(
        orderId: String,
        paymentStatus: PaymentStatus,
        liveData: MutableLiveData<Resource<UpdatePaymentStatusMutation.Data>>
    ) {
        withContext(Dispatchers.IO) {
            val now = Calendar.getInstance()
            networkResource.processMutationResponse(
                apolloClient,
                UpdatePaymentStatusMutation(
                    orderId,
                    paymentStatus.value
                ),
                liveData
            )
        }
    }
}
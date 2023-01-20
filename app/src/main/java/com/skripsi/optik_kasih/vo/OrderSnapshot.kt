package com.skripsi.optik_kasih.vo

import com.google.gson.annotations.SerializedName

data class OrderSnapshot(
    @SerializedName("id") var id: Int,
    @SerializedName("order_date") var orderDate: String?,
    @SerializedName("po") var po: Int?,
    @SerializedName("facet") var facet: Int?,
    @SerializedName("finished_facet") var finishedFacet: String?,
    @SerializedName("order_received") var orderReceived: String?,
    @SerializedName("subtotal") var subtotal: Double?,
    @SerializedName("discount") var discount: Double?,
    @SerializedName("grand_total") var grandTotal: Double?,
    @SerializedName("est_finish_date") var estFinishDate: String?,
    @SerializedName("notes") var notes: String?,
    @SerializedName("address") var address: String?,
    @SerializedName("order_status") var orderStatus: Int?,
    @SerializedName("customer_id") var customerId: Int?,
    @SerializedName("order_products") var orderProducts: ArrayList<OrderProductSnapshot>,
    @SerializedName("order_lenses") var orderLenses: String?,
    @SerializedName("order_payment") var orderPayment: OrderPaymentSnapshot?

)

data class OrderPaymentSnapshot(
    @SerializedName("id") var id: Int,
    @SerializedName("paid") var paid: Double?,
    @SerializedName("remain") var remain: Double?,
    @SerializedName("status") var status: Int?,
    @SerializedName("date") var date: String?,
    @SerializedName("expired_date") var expiredDate: String?,
    @SerializedName("order_id") var orderId: Int?,
    @SerializedName("payment_method") var paymentMethod: Int?,
    @SerializedName("payment_bank_id") var paymentBankId: Int?
)

data class OrderProductSnapshot(
    @SerializedName("id") var id: Int,
    @SerializedName("qty") var qty: Int?,
    @SerializedName("total") var total: Double?,
    @SerializedName("product_id") var productId: Int?,
    @SerializedName("discount") var discount: Int?,
    @SerializedName("product_snapshot") var productSnapshot: ProductSnapshot?
)

data class ProductSnapshot(
    @SerializedName("id") var id: Int,
    @SerializedName("product_name") var productName: String?,
    @SerializedName("product_type") var productType: String?,
    @SerializedName("product_brand") var productBrand: String?,
    @SerializedName("product_image") var productImage: String?,
    @SerializedName("product_description") var productDescription: String?,
    @SerializedName("product_price") var productPrice: Double?,
    @SerializedName("discount") var discount: Double?
)
package com.humbjorch.restaurantapp.data.model

import android.os.Parcelable
import com.humbjorch.restaurantapp.core.utils.OrderStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderModel(
    var id: String = "",
    var table: String = "",
    var productList: List<ProductsOrderModel> = emptyList(),
    var time: String = "",
    var status: String = OrderStatus.WITHOUT_PAYING.value,
    var total: String = "",
    var address: String = "",
    var orderNumber: Int = 1
): Parcelable

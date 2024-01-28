package com.humbjorch.restaurantapp.data.model

import android.os.Parcelable
import com.humbjorch.restaurantapp.core.utils.OrderStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderModel(
    val id: String = "",
    var table: String = "",
    var productList: List<ProductsOrderModel> = emptyList(),
    var time: String = "",
    var status: String = OrderStatus.WITHOUT_PAYING.value
): Parcelable

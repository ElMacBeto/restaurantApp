package com.humbjorch.restaurantapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderListModel(
    var orders: List<OrderModel> = emptyList()
)
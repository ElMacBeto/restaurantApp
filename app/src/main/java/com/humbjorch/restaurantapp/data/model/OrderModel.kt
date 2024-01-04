package com.humbjorch.restaurantapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderModel(
    val id: String,
    var table: String,
    var productList: List<ProductsOrderModel> = emptyList(),
    var time: String
)

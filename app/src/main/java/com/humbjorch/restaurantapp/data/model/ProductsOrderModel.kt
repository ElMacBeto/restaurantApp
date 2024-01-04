package com.humbjorch.restaurantapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductsOrderModel(
    var product:String = "",
    var amount: String = "1",
    var ingredients: List<String> = emptyList(),
    var price: String = "0"
)

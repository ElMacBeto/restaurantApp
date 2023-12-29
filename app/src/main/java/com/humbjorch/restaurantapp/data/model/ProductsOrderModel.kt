package com.humbjorch.restaurantapp.data.model

data class ProductsOrderModel(
    val product:String = "",
    val amount: Int = 1,
    val ingredient:String = "",
    val price: Int = 0
)

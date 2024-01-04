package com.humbjorch.restaurantapp.data.model



data class ProductsModel(
    val name: String = "",
    val description: String? = "",
    val price: String = "0",
    val ingredients: List<String>? = emptyList(),
    var isClicked: Boolean = false
)
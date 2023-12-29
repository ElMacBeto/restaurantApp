package com.humbjorch.restaurantapp.data.datasource.remote.response

import com.humbjorch.restaurantapp.data.model.ProductsModel

data class ProductResponse(
    val name: String = "",
    val list: List<ProductsModel>,
    val imageUrl: String = ""
)

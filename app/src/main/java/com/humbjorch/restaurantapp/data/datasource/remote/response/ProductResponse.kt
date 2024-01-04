package com.humbjorch.restaurantapp.data.datasource.remote.response

import com.humbjorch.restaurantapp.data.model.ProductsModel

data class ProductResponse(
    @field:JvmField
    val imageUrl: String = "",
    @field:JvmField
    val list: ArrayList<ProductsModel> = arrayListOf(),
    @field:JvmField
    val name: String = ""
)

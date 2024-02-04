package com.humbjorch.restaurantapp.data.model

data class ProductListModel(
    @field:JvmField
    val imageUrl: String = "",
    @field:JvmField
    val list: ArrayList<ProductsModel> = arrayListOf(),
    @field:JvmField
    val name: String = "",
    @field:JvmField
    val extras: ArrayList<ExtraModel> = arrayListOf()
)

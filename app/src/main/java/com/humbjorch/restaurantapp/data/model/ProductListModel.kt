package com.humbjorch.restaurantapp.data.model

import com.humbjorch.restaurantapp.core.utils.ProductType

data class ProductListModel(
    @field:JvmField
    val imageUrl: String = "",
    @field:JvmField
    val list: ArrayList<ProductsModel> = arrayListOf(),
    @field:JvmField
    val name: String = "",
    @field:JvmField
    val extras: ArrayList<ExtraModel> = arrayListOf(),
    @field:JvmField
    val orderNum: Int = 0,
    @field:JvmField
    val other: String = "",
    @field:JvmField
    val otherList: ArrayList<String> = arrayListOf(),
    @field:JvmField
    val productType: String = ProductType.FOOD.value//nuevo
)

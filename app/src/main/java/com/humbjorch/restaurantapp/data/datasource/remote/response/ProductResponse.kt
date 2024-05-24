package com.humbjorch.restaurantapp.data.datasource.remote.response

import com.humbjorch.restaurantapp.core.utils.ProductType
import com.humbjorch.restaurantapp.data.model.ExtraModel
import com.humbjorch.restaurantapp.data.model.OthersModel
import com.humbjorch.restaurantapp.data.model.ProductsModel

data class ProductResponse(
    var id: String = "",
    @field:JvmField
    val imageUrl: String = "",
    @field:JvmField
    val list: ArrayList<ProductsModel> = arrayListOf(),
    @field:JvmField
    val name: String = "",
    @field:JvmField
    val extras: ArrayList<ExtraModel> = arrayListOf(),
    @field:JvmField
    val orderNum: String = "0",
    @field:JvmField
    val other: String = "",
    @field:JvmField
    val otherList: ArrayList<String> = arrayListOf(),
    @field:JvmField
    val productType: String = ProductType.FOOD.value,
    @field:JvmField
    val others: List<OthersModel> = emptyList()
)

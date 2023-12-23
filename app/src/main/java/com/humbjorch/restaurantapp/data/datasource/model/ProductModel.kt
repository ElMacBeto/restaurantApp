package com.humbjorch.restaurantapp.data.datasource.model

data class ProductModel(
    val name: String,
    val types: List<ProductTypeModel>
)

data class ProductTypeModel(
    val type: String,
    val description: String,
    val price: Int,
    val image: String,
    val ingredients: List<String>
)


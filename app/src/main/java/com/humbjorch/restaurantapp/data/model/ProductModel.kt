package com.humbjorch.restaurantapp.data.model

import com.humbjorch.restaurantapp.data.datasource.remote.response.TablesAvailableResponse

data class ProductModel(
    val beverages: List<BeverageModel>,
    val hamburgers: List<HamburgerModel>,
    val tablesAvailable: List<TableAvailableModel>
)



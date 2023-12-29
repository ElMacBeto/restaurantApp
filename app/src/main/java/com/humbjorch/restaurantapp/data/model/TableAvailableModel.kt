package com.humbjorch.restaurantapp.data.model

data class TableAvailableModel(
    val available: Boolean = true,
    val position: String = "0",
    var isClicked: Boolean = false
)
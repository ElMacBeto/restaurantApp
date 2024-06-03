package com.humbjorch.restaurantapp.data.model

data class CashRegisterModel(
    var name: String,
    var value: Int,
    var isEditable: Boolean = true
)
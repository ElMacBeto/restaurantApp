package com.humbjorch.restaurantapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductsOrderModel(
    var product:String = "",
    var amount: String = "1",
    var ingredients: List<String> = emptyList(),
    var price: String = "0",
    var extras: List<ExtraModel> = emptyList()
): Parcelable

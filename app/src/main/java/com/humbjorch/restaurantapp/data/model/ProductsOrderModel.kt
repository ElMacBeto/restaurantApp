package com.humbjorch.restaurantapp.data.model

import android.os.Parcelable
import com.humbjorch.restaurantapp.core.utils.ProductType
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductsOrderModel(
    var product: String = "",
    var amount: String = "1",
    var ingredients: List<String> = emptyList(),
    var price: String = "0",
    var extras: List<ExtraModel> = emptyList(),
    var otherName: String = "",
    var other: String = "",
    var productType: String = ProductType.FOOD.value,
    var others: List<OtherModel> = emptyList()
) : Parcelable
